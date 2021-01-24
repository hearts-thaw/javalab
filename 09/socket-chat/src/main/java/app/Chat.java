package app;

import com.beust.jcommander.JCommander;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Chat {
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    private static final ExecutorService es = Executors.newFixedThreadPool(20);

    private static final List<String> codes = Codes.getServerCodes();

    private static final Map<String, ClientHandler> users = new ConcurrentHashMap<>();

    private final static Sender sender = (name, message, condition) ->
            (Runnable) () -> users.forEach((__, client) -> {
                if (condition.test(client)) {
                    client.getWriter().println(name + ": " + message);
                }
            });

    public static void main(String[] argv) {
        Args args = new Args();
        JCommander.newBuilder()
                .addObject(args)
                .build().parse(argv);
        new Chat().start(args.port);
    }

    private void start(int port) {
        try (ServerSocket socket = new ServerSocket(port)) {
            while (true)
                new ClientHandler(socket.accept()).start();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    static class ClientHandler extends Thread {

        String name = randomName(6);

        private final Socket socket;
        private BufferedReader reader;
        private PrintWriter writer;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);

                users.put(name, this);

                String parsedCode, message, codeContent;
                while ((message = reader.readLine()) != null) {
                    if ((parsedCode = codes.stream().filter(message::startsWith).findAny().orElse(null)) != null) {

                        codeContent = message.substring(parsedCode.length());

                        Codes currentCode;
                        try {
                            currentCode = Codes.parseServerCode(parsedCode);
                        } catch (IllegalArgumentException e) {
                            System.out.printf("%s is not supported code!%n", parsedCode);
                            continue;
                        }

                        switch (currentCode) {
                            case CHANGE_NAME:
                                es.submit(sender.send(name, String.format(" changed name to %s ", codeContent), this::equals));
                                this.name = codeContent;
                        }
                    } else {
                        System.out.println(message);
                        es.submit(sender.send(name, message, __ -> true));
                    }
                }

            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            } finally {
                writer.close();
                try {
                    reader.close();
                    socket.close();
                } catch (IOException ignored) {
                }
            }
        }

        public PrintWriter getWriter() {
            return writer;
        }
    }

    private static String randomName(int length) {
        StringBuilder sb = new StringBuilder(length);
        for(int i = 0; i < length; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }
}
