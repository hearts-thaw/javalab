package app;

import com.beust.jcommander.JCommander;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Runner {
    public static void main(String[] argv) {
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
        ExecutorService es = Executors.newFixedThreadPool(20);

        if (args.names == null) {
            files.forEach(urlConnection -> es.submit(Downloader.download(urlConnection, args.folder)));
        } else {
            String[] names = args.names.split(";");
            AtomicInteger k = new AtomicInteger();
            files.forEach(urlConnection ->
                    es.submit(Downloader.download(urlConnection, args.folder, names[k.getAndIncrement()]))
            );
        }

        es.shutdown();
        try {
            if (!es.awaitTermination(3, TimeUnit.SECONDS)) {
                es.shutdownNow();
            }
        } catch (InterruptedException e) {
            es.shutdownNow();
        }
    }
}
