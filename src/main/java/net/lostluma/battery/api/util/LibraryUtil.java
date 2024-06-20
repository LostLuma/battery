package net.lostluma.battery.api.util;

import net.lostluma.battery.impl.util.NativeUtil;

import java.nio.file.Path;

/**
 * Optional utilities to customize library installation.
 */
public class LibraryUtil {
    /**
     * Customize the directory for storing the dynamic library.
     * By default, this is a subfolder in the user's cache directory.
     *
     * @param path the new cache directory
     */
    public static void setCacheDir(Path path) {
        NativeUtil.setCacheDir(path);
    }

    /**
     * Customize whether the dynamic library may be automatically downloaded.
     *
     * @param value whether to allow automatic library downloading. Defaults to true.
     */
    public static void setAllowDownloads(boolean value) {
        NativeUtil.setAllowDownloads(value);
    }
}
