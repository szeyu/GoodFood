package com.hmir.goodfood.utilities;

import android.content.Context;
import android.util.Base64;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Utility class for handling file operations, specifically encoding and decoding
 * files to and from Base64 format.
 *
 * This class provides static methods to:
 * - Save a Base64-encoded string to a file in the device's cache directory.
 * - Read a file and return its contents as a Base64-encoded string.
 *
 * Note: The methods in this class are not thread-safe and should be used accordingly.
 */
public class FileUtil {

    /**
     * Saves a Base64-encoded string as a file in the application's cache directory.
     *
     * @param context  The application context used to access the cache directory.
     * @param base64Data The Base64-encoded string to decode and save.
     * @param fileName The name of the file to create in the cache directory.
     * @return The saved file, or {@code null} if an error occurred.
     * @throws IllegalArgumentException If the input Base64 data is null or invalid.
     */
    public static File saveBase64ToFile(Context context, String base64Data, String fileName) {
        if (context == null || base64Data == null || fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("Invalid input parameters for saving Base64 data.");
        }
        File file = new File(context.getCacheDir(), fileName); // Save in cache directory
        try (FileOutputStream fos = new FileOutputStream(file)) {
            byte[] decodedData = Base64.decode(base64Data, Base64.DEFAULT);
            fos.write(decodedData);
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Reads the contents of a file and encodes it as a Base64 string.
     *
     * @param file The file to read. Must be non-null and exist on the filesystem.
     * @return A Base64-encoded string representing the file's contents, or {@code null}
     *         if an error occurred during reading.
     * @throws IllegalArgumentException If the input file is null or does not exist.
     * @throws IOException If an error occurs during file reading.
     */
    public static String readBase64FromFile(File file) {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("Invalid file");
        }
        byte[] data = new byte[(int) file.length()]; // Allocate buffer based on file size
        try (FileInputStream fis = new FileInputStream(file)) {
            int bytesRead = 0;
            int offset = 0;
            int remaining = data.length;

            // Ensure all bytes are read
            while (remaining > 0) {
                bytesRead = fis.read(data, offset, remaining);
                if (bytesRead == -1) {
                    throw new IOException("Unexpected end of file while reading: " + file.getName());
                }
                offset += bytesRead;
                remaining -= bytesRead;
            }

            // Encode data to Base64 and return
            return android.util.Base64.encodeToString(data, android.util.Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
