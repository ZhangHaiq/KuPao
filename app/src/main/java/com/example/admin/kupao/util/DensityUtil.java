package com.example.admin.kupao.util;

import android.content.Context;

/**
 * Created by admin on 2018/2/2.
 */

public class DensityUtil {

    /**
     * 像素密度转为像素
     *
     * @param context
     * @param dp
     * @return
     */
    public static int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
