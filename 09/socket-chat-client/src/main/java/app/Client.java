package app;

import com.beust.jcommander.JCommander;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private static BufferedReader reader;
    private static PrintWriter writer;

    private static final BufferedReader scn = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] argv) throws IOException {
        Args args = new Args();

        JCommander.newBuilder()
                .addObject(args)
                .build()
                .parse(argv);

        try {
            Socket socket = new Socket(args.ip, args.port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }

        Thread messageReader = new Thread(() -> {
            while (true) {
                try {
                    System.out.println(reader.readLine());
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            }
        });
        messageReader.setDaemon(true);
        messageReader.start();

        Thread messageWriter = new Thread(() -> {
            while (true) {
                try {
                    writer.println(scn.readLine());
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            }
        });
        messageWriter.start();
    }
}
