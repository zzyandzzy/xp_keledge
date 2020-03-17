package xyz.zzyitj.keledge.downloader;

import android.annotation.SuppressLint;
import android.app.Application;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author intent
 * @version 1.0
 * @date 2020/2/18 6:08 下午
 * @email zzy.main@gmail.com
 */
public class MyApplication extends Application {
    private static MyApplication myApplication;

    private Map<Long, Integer> downloadProgressMap = new ConcurrentHashMap<>();

    @SuppressLint("UseSparseArrays")
    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static MyApplication getInstance() {
        if (myApplication == null) {
            myApplication = new MyApplication();
        }
        return myApplication;
    }

    public void setDownloadProgress(long id, int p) {
        this.downloadProgressMap.put(id, p);
    }

    public int getDownloadProgress(long id) {
        return this.downloadProgressMap.get(id);
    }

    public void addDownloadProgress(long id, int a) {
        this.downloadProgressMap.put(id, getDownloadProgress(id) + a);
    }
}
