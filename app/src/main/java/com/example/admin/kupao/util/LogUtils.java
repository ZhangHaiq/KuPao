package com.example.admin.kupao.util;


import android.util.Log;
import android.widget.Toast;

import com.example.admin.kupao.app.MyApplication;

/**
 * Created by admin on 2018/2/2.
 */

public class LogUtils {

    public static final boolean DEBUG = true;
    public static final String TAG = "+++++++++++>>>>>";
    private static Toast toast;
    public static void debug(String msg) {
        if (DEBUG) {
            System.err.println(TAG + msg);
            Log.d(TAG, msg);
        }
    }

    public static void showToast( String content) {
        if (toast == null) {  //判断Toast对象是否为空
            toast = Toast.makeText(MyApplication.getContext(), content, Toast.LENGTH_LONG);
        } else {
            toast.setText(content);
        }
        toast.show();
    }

}
