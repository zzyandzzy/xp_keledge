package xyz.zzyitj.keledge.downloader.net;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import xyz.zzyitj.keledge.downloader.util.ConstUtils;

/**
 * @author intent
 * @version 1.0
 * @date 2020/2/18 2:20 下午
 * @email zzy.main@gmail.com
 */
public class DownloadUtils {
    public static Observable<ResponseBody> download(String url){
        DownloadService request = RetrofitHttpUtils.createRetrofit(ConstUtils.BASE_URL).create(DownloadService.class);
        return request.download(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
