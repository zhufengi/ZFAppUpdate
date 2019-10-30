package com.zf.update.net;


import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * @author: zhufeng
 * @github: https://github.com/zhufengi
 * @time: 2019/10/25
 * @description: address api
 */
public interface DownloadService {

    @Streaming
    @GET
    @Headers("Content-Type: application/json;charset=UTF-8")
    Observable<ResponseBody> download(@Url String url);


    @Streaming
    @POST
    @Headers("Content-Type: application/json;charset=UTF-8")
    Observable<ResponseBody> downloadPost(@Url String url);

    @Streaming
    @GET
    @Headers("Content-Type: application/json;charset=UTF-8")
    Call<ResponseBody> downloadFile(@Url String url);

    @Streaming
    @POST
    @Headers("Content-Type: application/json;charset=UTF-8")
    Call<ResponseBody> downloadFilePost(@Url String url);

}
