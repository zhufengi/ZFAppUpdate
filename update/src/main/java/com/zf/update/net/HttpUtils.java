package com.zf.update.net;


import android.content.Context;

import com.zf.update.DownloadListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/**
 * @author: zhufeng
 * @github: https://github.com/zhufengi
 * @time: 2019/10/28
 * @description:
 */
public class HttpUtils {

    private static HttpSSLUtils.SSLParams mSSLParams = null;
    private static OkHttpClient client = null;
    private static DownloadListener mDownloadListener;
    private static final String BASEURL = "https://www.baidu.com";

    public HttpUtils(DownloadListener downloadListener){
        mDownloadListener = downloadListener;
    }

    /**
     * 初始化证书
     * @param context
     * @param certName "uploadlogs.cer"
     */
    public static void initCert(Context context, String certName){
        try {
            mSSLParams = HttpSSLUtils.getSslSocketFactory(new InputStream[]{context.getAssets().open(certName)}, null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DownloadService getApi(){
        //创建Retrofit的实例，把Gson转换器设置下
        Retrofit retrofit = new Retrofit
                .Builder()
                 //设置API的基础地址
                .baseUrl(BASEURL)
                 //设置后才才支持json字符串转化为Bean
                .addConverterFactory(GsonConverterFactory.create())
                .client(client())
                .build();
        return retrofit.create(DownloadService.class);
    }

    private static OkHttpClient client(){
            client = new OkHttpClient.Builder()
                    .addInterceptor(new DownloadInterceptor(mDownloadListener))
                    .connectTimeout(30*1000L, TimeUnit.SECONDS)
                    .readTimeout(30*1000L, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
//                    .sslSocketFactory(mSSLParams.sSLSocketFactory, mSSLParams.trustManager)
                    .build();
            return client;
    }


}
