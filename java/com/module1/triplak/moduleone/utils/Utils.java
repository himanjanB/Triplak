package com.module1.triplak.moduleone.utils;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Himanjan on 4/24/2016.
 */
public class Utils {

    public static void copyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;

        try {
            byte[] bytes = new byte[buffer_size];
            while(true) {
                int count = is.read(bytes, 0, buffer_size);
                if(count == -1) {
                    break;
                }
                os.write(bytes, 0, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

