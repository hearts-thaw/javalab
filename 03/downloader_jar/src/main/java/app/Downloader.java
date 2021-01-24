package app;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;

public class Downloader {
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    public static Runnable download(URLConnection urlConnection, String folder) {
        return download(urlConnection, folder, getNameFromUrlConnection(urlConnection));
    }

    public static Runnable download(URLConnection urlConnection, String folder, String name) {
        return () -> {
            try {
                InputStream response = urlConnection.getInputStream();

                Files.copy(response, Paths.get(folder, name));
            } catch (IOException e) {
                throw new IllegalArgumentException("Error has occurred while downloading file");
            }
        };
    }

    private static String getNameFromUrlConnection(URLConnection connection) {
        String raw = connection.getHeaderField("Content-Disposition"), res;
        if (raw != null && raw.contains("=")) {
            res = raw.split("=")[1];
        } else {
            res = randomName(6);
        }
        return res;
    }

    private static String randomName(int length) {
        StringBuilder sb = new StringBuilder(length);
        for(int i = 0; i < length; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }
}
