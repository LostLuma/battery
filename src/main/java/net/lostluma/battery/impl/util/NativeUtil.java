package net.lostluma.battery.impl.util;

import net.lostluma.battery.api.exception.LibraryLoadError;
import net.lostluma.battery.impl.Constants;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

@ApiStatus.Internal
public class NativeUtil {
    private static Path cacheDir = null;
    private static boolean download = true;

    private static boolean isLoaded = false;
    private static final String BASE_URL = "https://files.lostluma.net/battery-jni/" + Constants.NATIVES + "/";

    public static void load() throws LibraryLoadError {
        if (isLoaded) {
            return;
        }

        try {
            load0();
            isLoaded = true;
        } catch (IOException e) {
            throw new LibraryLoadError(e);
        }
    }

    public static void setCacheDir(Path path) {
        cacheDir = path;
    }

    public static void setAllowDownloads(boolean value) {
        download = value;
    }

    private static void load0() throws IOException, LibraryLoadError{
        Properties properties = new Properties();

        try (InputStream stream = NativeUtil.class.getResourceAsStream("/natives.properties")) {
            if (stream == null) {
                throw new LibraryLoadError("Failed to read native library info.");
            }

            properties.load(stream);
        }

        String base = getArch() + "." + getName();

        if (properties.getProperty(base + ".name") == null) {
            throw new LibraryLoadError("No library found for " + getArch() + " " + getName());
        }

        String name = properties.getProperty(base + ".name");
        String hash = properties.getProperty(base + ".hash");

        Path path;

        if (cacheDir != null) {
            path = cacheDir.resolve(name);
        } else {
            path = Constants.CACHE_DIR.resolve(name);
        }

        if (download && !isLibraryValid(path, hash)) {
            Files.createDirectories(Constants.CACHE_DIR);
            HttpUtil.download(new URL(BASE_URL + name), path);
        }

        if (!isLibraryValid(path, hash)) {
            throw new LibraryLoadError("Native library could not be validated.");
        }

        try {
            System.load(path.toAbsolutePath().toString());
        } catch (UnsatisfiedLinkError e) {
            throw new LibraryLoadError(e);
        }
    }

    private static String getArch() {
        String arch = System.getProperty("os.arch");

        // MacOS
        if (arch.equals("x86_64")) {
            arch = "amd64";
        }

        return arch;
    }

    private static String getName() {
        String name = System.getProperty("os.name").toLowerCase();

        if (name.contains("win")) {
            return "windows";
        } else if (name.contains("mac")) {
            return "macos";
        } else {
            return "linux";
        }
    }

    private static boolean isLibraryValid(Path path, String hash) throws IOException {
        return Files.exists(path) && hash.equals(CryptoUtil.sha512(path));
    }
}
