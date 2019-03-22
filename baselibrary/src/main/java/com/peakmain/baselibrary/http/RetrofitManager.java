package com.peakmain.baselibrary.http;

import com.blankj.utilcode.util.NetworkUtils;
import com.peakmain.baselibrary.BuildConfig;
import com.peakmain.baselibrary.app.BaseApp;
import com.peakmain.baselibrary.base.Constant;
import com.peakmain.baselibrary.http.interceptor.log.RequestInterceptor;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Leehor
 * 版本：
 * 创建日期：2019/3/18
 * 描述：上网类的封装
 */
public class RetrofitManager {
    //连接超时
    private static long CONNECT_TIMEOUT = 60L;
    //阅读超时
    private static long READ_TIMEOUT = 10L;
    //写入超时
    private static long WRITE_TIMEOUT = 10L;
    //设缓存有效期为1天
    private static final long CACHE_STALE_SEC = 60 * 60 * 24 * 1;
    //查询缓存的Cache-Control设置，为only-if-cached时只查询缓存而不会请求服务器，max-stale可以配合设置缓存失效时间
    public static final String CACHE_CONTROL_CACHE = "only-if-cached, max-stale=" + CACHE_STALE_SEC;
    //查询网络的Cache-Control设置
    //(假如请求了服务器并在a时刻返回响应结果，则在max-age规定的秒数内，浏览器将不会发送对应的请求到服务器，数据由缓存直接返回)
    public static final String CACHE_CONTROL_NETWORK = "Cache-Control: public, max-age=10";
    // 避免出现 HTTP 403 Forbidden，参考：http://stackoverflow.com/questions/13670692/403-forbidden-with-java-but-not-web-browser
    private static final String AVOID_HTTP403_FORBIDDEN = "User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";
    private static volatile OkHttpClient mOkHttpClient;
    /**
     * 云端响应头拦截器，用来配置缓存策略
     */
    private static final Interceptor mRewriteCacheControlInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();//获得上一个请求
            if (!NetworkUtils.isConnected()) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
            }
            Response originalResponse = chain.proceed(request);
            if (NetworkUtils.isConnected()) {
                //有网的时候读接口上的@Headers里的配置，可以在这里进行统一的设置
                String cacheControl = request.cacheControl().toString();
                return originalResponse.newBuilder()
                        .header("Cache-Control", cacheControl)
                        .removeHeader("Pragma")
                        .build();
            } else {
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + CACHE_CONTROL_CACHE)
                        .removeHeader("Pragma")
                        .build();
            }
        }
    };
    private static RequestInterceptor requestInterceptor;


//    /**
//     * 日志拦截器
//     */
//    private static final Interceptor mLoggingIntercepter = new Interceptor() {
//        @Override
//        public Response intercept(Chain chain) throws IOException {
//            Request request = chain.request();
//            Response response = chain.proceed(request);
//            String isSuccess = response.isSuccessful() ? "true" : "false";
//           // Logger.w(isSuccess);
//            ResponseBody body = response.body();
//            BufferedSource source = body.source();
//            source.request(Long.MAX_VALUE);
//            Buffer buffer = source.buffer();
//            Charset charset = Charset.defaultCharset();
//            MediaType contentType = body.contentType();
//            if (contentType != null) {
//                charset = contentType.charset();
//            }
//            String bodyString = buffer.clone().readString(charset);
//           // Logger.w(String.format("Received response json string " + bodyString));
//            return response;
//        }
//    };


    /**
     * 获取OkHttpClient实例
     *
     * @return
     */
    private static OkHttpClient getOkHttpClient() {
        if (mOkHttpClient == null) {
            synchronized (RetrofitManager.class) {
                Cache cache = new Cache(new File(BaseApp.getInstance().getCacheDir(), "HttpCache"), 1024 * 1024 * 100);
              /*  HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
                if (BuildConfig.DEBUG){
                    httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                } else {
                    httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
                }*/
                if (BuildConfig.DEBUG){
                    requestInterceptor = RequestInterceptor.getInstance(RequestInterceptor.Level.ALL);
                } else {
                     requestInterceptor = RequestInterceptor.getInstance(RequestInterceptor.Level.NONE);
                }
                if (mOkHttpClient == null) {
                    mOkHttpClient = new OkHttpClient.Builder()
                            .cache(cache)
                            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                            .addInterceptor(mRewriteCacheControlInterceptor)
                            .addInterceptor(requestInterceptor)
//                            .addInterceptor(interceptor)
//                            .cookieJar(new CookiesManager())
//                            .cookieJar(cookieJar)
                            .build();
                }
            }
        }
        return mOkHttpClient;
    }

    /**
     * 可以根据根地址不同设置不同的网络请求实例
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T create_1(Class<T> clazz) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL_1)
                .client(getOkHttpClient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(clazz);
    }

    /**
     * Retrofit 2
     * 可以根据根地址不同设置不同的网络请求实例
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T create_2(Class<T> clazz) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL_2)
                .client(getOkHttpClient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(clazz);
    }
}
