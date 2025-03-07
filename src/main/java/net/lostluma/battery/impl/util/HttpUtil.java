package net.lostluma.battery.impl.util;

import net.lostluma.battery.impl.Constants;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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

        try (InputStream stream = connection.getInputStream()) {
            Files.copy(stream, into, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
