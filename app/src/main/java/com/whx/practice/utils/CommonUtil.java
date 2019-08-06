package com.whx.practice.utils;

import android.widget.Toast;

import com.whx.practice.MyApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by whx on 2017/9/5.
 */

public class CommonUtil {

    public static void toast(String msg) {
        Toast.makeText(MyApplication.getInstance(), msg, Toast.LENGTH_SHORT).show();
    }

    public static boolean isNotEmpty(List list) {
        return list != null && !list.isEmpty();
    }

    public static String readInputStreamToString(InputStream in, String encoding) {

        StringBuilder sb = new StringBuilder(16384);
        BufferedReader r;
        try {
            r = new BufferedReader(new InputStreamReader(in, encoding == null ? "utf-8" : encoding), 16384);

            char[] buffer = new char[16384];
            int len;

            while ((len = r.read(buffer, 0, buffer.length)) >= 0) {
                sb.append(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
//            if (r != null) {
//                r.close();
//            }
        }

        return sb.toString();
    }
}
