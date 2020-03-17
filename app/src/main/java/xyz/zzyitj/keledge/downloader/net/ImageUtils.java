package xyz.zzyitj.keledge.downloader.net;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import xyz.zzyitj.keledge.downloader.util.ConstUtils;

/**
 * @author intent
 * @version 1.0
 * @date 2020/2/18 1:22 下午
 * @email zzy.main@gmail.com
 */
public class ImageUtils {
    public static Observable<ResponseBody> getImg(String url) {
        ImageService request = RetrofitHttpUtils.createRetrofit(ConstUtils.BASE_URL).create(ImageService.class);
        return request.getImg(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
