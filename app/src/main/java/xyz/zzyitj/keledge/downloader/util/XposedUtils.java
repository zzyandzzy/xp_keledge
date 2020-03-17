package xyz.zzyitj.keledge.downloader.util;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

/**
 * @author intent
 * @version 1.0
 * @date 2020/2/17 11:12 上午
 * @email zzy.main@gmail.com
 */
public class XposedUtils {
    public static boolean isExpModuleActive(Context context) {
        boolean isExp = false;
        if (context == null) {
            throw new IllegalArgumentException("context must not be null!!");
        }
        try {
            ContentResolver contentResolver = context.getContentResolver();
            Uri uri = Uri.parse("content://me.weishu.exposed.CP/");
            Bundle result = null;
            try {
                result = contentResolver.call(uri, "active", null, null);
            } catch (RuntimeException e) {
                // TaiChi is killed, try invoke
                try {
                    Intent intent = new Intent("me.weishu.exp.ACTION_ACTIVE");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (Throwable e1) {
                    return false;
                }
            }
            if (result == null) {
                result = contentResolver.call(uri, "active", null, null);
            }
            if (result == null) {
                return false;
            }
            isExp = result.getBoolean("active", false);
        } catch (Throwable ignored) {
        }
        return isExp;
    }

    public static void gotoActiveModule(Context context) {
        Intent t = new Intent("me.weishu.exp.ACTION_MODULE_MANAGE");
        t.setData(Uri.parse("package:" + "Your package name"));
        t.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(t);
        } catch (ActivityNotFoundException e) {
            // TaiChi not installed.
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
