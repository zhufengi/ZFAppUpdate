package com.zf.update.net;

import com.zf.update.DownloadListener;
import com.zf.update.DownloadResponseBody;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;
/**
 * @author: zhufeng
 * @github: https://github.com/zhufengi
 * @time: 2019/10/28
 * @description: 带进度拦截器
 *
 * 观察，修改以及可能短路的请求输出和响应请求的回来。
 * 通常情况下拦截器用来添加，移除或者转换请求或者回应的头部信息
 */
public class DownloadInterceptor implements Interceptor {

    private DownloadListener mDownloadListener;

    public DownloadInterceptor(DownloadListener downloadListener){
        this.mDownloadListener = downloadListener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        return response.newBuilder().body(
                new DownloadResponseBody(response.body(), mDownloadListener)).build();
    }
}
