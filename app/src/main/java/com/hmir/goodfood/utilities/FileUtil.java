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
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            return android.util.Base64.encodeToString(data, android.util.Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
