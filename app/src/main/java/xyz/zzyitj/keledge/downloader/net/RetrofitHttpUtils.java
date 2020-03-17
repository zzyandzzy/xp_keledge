package xyz.zzyitj.keledge.downloader.net;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author intent
 * @version 1.0
 * @date 2020/2/4 6:46 下午
 * @email zzy.main@gmail.com
 */
public class RetrofitHttpUtils {
    private static final long TIMEOUT = 10;

    private static OkHttpClient client = new OkHttpClient().newBuilder()
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .build();

    private static Retrofit retrofit = null;

    public static Retrofit createRetrofit(String baseUrl) {
        if (retrofit == null) {
            synchronized (Retrofit.class) {
                if (retrofit == null) {
//                    client.dispatcher().setMaxRequests(128);
//                    client.dispatcher().setMaxRequestsPerHost(1);
                    retrofit = new Retrofit.Builder()
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .baseUrl(baseUrl)
                            .client(client)
                            .build();
                }
            }
        }
        return retrofit;
    }
}
