package net.lostluma.battery.impl;

import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;
import java.nio.file.Paths;

@ApiStatus.Internal
public final class Constants {
    public static final String VERSION = "1.1.0";
    public static final String NATIVES_VERSION = "1.0.0";

    public static final Path DEFAULT_CACHE_DIR = getDefaultCacheDir();

    private static String getUserHome() {
        return System.getProperty("user.home");
    }

    private static Path getDefaultCacheDir() {
        Path path;
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            path = pathFromEnv("LOCALAPPDATA", null);
        } else if (os.contains("mac")) {
            String home = getUserHome();
            path = Paths.get(home, "Library", "Caches");
        } else {
            path = pathFromEnv("XDG_CACHE_HOME", ".cache");
        }

        return path.resolve("net.lostluma.battery");
    }

    private static Path pathFromEnv(String name, String fallback) {
        String value = System.getenv(name);

        if (value != null && !value.isEmpty()) {
            return Paths.get(value);
        }

        if (fallback != null) {
            return Paths.get(getUserHome(), fallback);
        } else {
            String os = System.getProperty("os.name");
            throw new RuntimeException("Missing expected env '" + name + "' for '" + os + "'");
        }
    }
}
