package com.whx.practice.view.leaf;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by whx on 2017/9/30.
 */

public class UiUtils {

    public static int dp2px(Context context, int dp) {
        return (int) (dp * getScreenDensity(context) + 0.5f);
    }

    private static float getScreenDensity(Context context) {
        try {
            DisplayMetrics dm = new DisplayMetrics();
            ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);

            return dm.density;
        } catch (Exception e) {
            return DisplayMetrics.DENSITY_DEFAULT;
        }
    }

    public static int getScreenWidth(Context context) {
        Point point = new Point();
        ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(point);
        return point.x;
    }


}
