package net.lostluma.battery.impl.util;

import net.lostluma.battery.impl.Constants;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@ApiStatus.Internal
public class HttpUtil {
    private static final Duration TIMEOUT = Duration.of(2, ChronoUnit.MINUTES);

    public static void download(URL url, Path into) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout((int) TIMEOUT.toMillis());
        connection.setRequestProperty("User-Agent", "Battery/" + Constants.VERSION);

        connection.connect();
        int status = connection.getResponseCode();

        if (status != 200) {
            throw new IOException("Library download error: " + status);
        }

        int size;
        String length = connection.getHeaderField("Content-Length");

        try {
            size = Integer.parseInt(length);
        } catch (NumberFormatException e) {
            throw new IOException("Received invalid Content-Length header!");
        }

        try (InputStream stream = connection.getInputStream()) {
            int input;
            int index = 0;

            byte[] data = new byte[size];

            while ((input = stream.read()) != -1) {
                data[index] = (byte) input;
                index ++;
            }

            Files.write(into, data);
        }
    }
}
