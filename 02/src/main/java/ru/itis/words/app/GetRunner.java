package ru.itis.words.app;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GetRunner implements Runnable {

    private final static String RESOURCES_PATH = "/home/northern_avrora/kphu/practice/java_lab_2020_2/02. JARS/target/images/";

    private final URLConnection connection;

    private final String pathToDir;

    private final String path;

    private GetRunner(final URLConnection connection, String dirname) {
        this.connection = connection;
        this.pathToDir = dirname;
        this.path = dirname + "/" + extractFilenameFromURL(connection.getURL());
    }

    private GetRunner(final URLConnection connection) {
        this(connection, RESOURCES_PATH + extractFilenameFromURL(connection.getURL()));
    }

    private static String extractFilenameFromURL(URL url) {
        String[] urlSplit = url.getPath().split("/");

        return urlSplit[urlSplit.length - 1];
    }

    public static GetRunner create(URLConnection connection) {
        return new GetRunner(connection);
    }

    public static GetRunner create(URLConnection connection, String filename) {
        return new GetRunner(connection, filename);
    }

    @Override
    public synchronized void run() {
        try {
            InputStream response = connection.getInputStream();

            File file = new File(path);
            System.out.println(file.getAbsoluteFile());

            System.out.println("DIRECTORY: " + Path.of(RESOURCES_PATH).toFile().getAbsolutePath());

            if (Path.of(RESOURCES_PATH).toFile().mkdir()) {
                System.out.println("Directiory " + RESOURCES_PATH + " was created.");
            }

            if (file.delete()) {
                System.out.printf("File %s deleted, creating new...\n", file.getName());
            } else {
                System.out.printf("Creating new file %s...\n", file.getName());
            }
            Files.copy(response, Paths.get(path));
        } catch (IOException e) {
            throw new IllegalArgumentException("Error has occurred while downloading file");
        }
    }
}
