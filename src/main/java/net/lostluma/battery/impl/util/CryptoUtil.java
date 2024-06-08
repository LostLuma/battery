package net.lostluma.battery.impl.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CryptoUtil {
    public static String sha512(Path path) throws IOException {
        MessageDigest digest;
        byte[] data = Files.readAllBytes(path);

        try {
            digest = MessageDigest.getInstance("SHA512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No SHA512 algorithm found.", e);
        }

        digest.update(data);
        return toHex(digest.digest());
    }

    public static String toHex(byte[] data) {
        StringBuilder builder = new StringBuilder(data.length * 2);

        for (byte value : data) {
            int number = Byte.toUnsignedInt(value);

            builder.append(Integer.toHexString(number >> 0b100)); // Former four bits
            builder.append(Integer.toHexString(number & 0b1111)); // Latter four bits
        }

        return builder.toString();
    }
}
