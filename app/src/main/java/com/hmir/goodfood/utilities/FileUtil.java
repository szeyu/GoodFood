package com.hmir.goodfood.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {
    public static File saveBase64ToFile(Context context, String base64Data, String fileName) {
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
