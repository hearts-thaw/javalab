package ru.itis.words.app;

import com.beust.jcommander.JCommander;
import ru.itis.pool.ThreadPool;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Program {
    public static void main(String[] argv) throws InterruptedException {
        Args args = new Args();

        JCommander.newBuilder()
                .addObject(args)
                .build()
                .parse(argv);

        List<URLConnection> files = Arrays.stream(args.files.split(";")).map(url -> {
            try {
                return new URL(url).openConnection();
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }).collect(Collectors.toList());

        int count = 1;
        if ("multi-thread".equals(args.mode)) {
            count = args.count;
        }
        if (count > 20) {
            throw new IllegalArgumentException("Too much threads to be evaluated");
        }
        ThreadPool executorService = ThreadPool.newPool(count);

        files.forEach(connection -> executorService.submit(GetRunner.create(connection, args.folder)));

        Thread.sleep(files.size() * 2500);
        executorService.stopAll();
    }
}
