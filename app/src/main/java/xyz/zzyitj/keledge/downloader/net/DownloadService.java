package xyz.zzyitj.keledge.downloader.net;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * @author intent
 * @version 1.0
 * @date 2020/2/18 2:16 下午
 * @email zzy.main@gmail.com
 */
public interface DownloadService {
    @GET
    Observable<ResponseBody> download(@Url String url);
}
