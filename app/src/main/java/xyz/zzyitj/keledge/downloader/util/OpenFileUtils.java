package xyz.zzyitj.keledge.downloader.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.core.content.FileProvider;

import java.io.File;

/**
 * @author intent
 * @version 1.0
 * @date 2020/2/18 3:43 下午
 * @email zzy.main@gmail.com
 */

public class OpenFileUtils {

    // Android获取一个用于打开APK文件的intent
    public static Intent getAllIntent(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String authority = context.getPackageName() + ".fileProvider";
        Uri uri = FileProvider.getUriForFile(context, authority, file);
        intent.setDataAndType(uri, "*/*");
        return intent;
    }

    // Android获取一个用于打开PDF文件的intent
    public static Intent getPdfFileIntent(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory("android.intent.category.DEFAULT");
//    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String authority = context.getPackageName() + ".fileProvider";
        Uri uri = FileProvider.getUriForFile(context, authority, file);
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }
}
