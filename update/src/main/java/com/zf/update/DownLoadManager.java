package com.zf.update;

import android.content.Context;
import androidx.annotation.NonNull;

import com.zf.update.net.HttpUtils;
import com.zf.update.utils.Utils;

import org.reactivestreams.Subscriber;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author: zhufeng
 * @github: https://github.com/zhufengi
 * @time: 2019/10/28
 * @description: DownLoadManager
 */
public class DownLoadManager {

    private static DownLoadManager mDownLoadManager = null;
    private static Context mContext = null;
    private static DownloadListener mDownloadListener = null;
    private DownLoadManager(){
    }

    public static DownLoadManager getInstance() {
        if (mDownLoadManager == null){
            synchronized (DownLoadManager.class){
                if (mDownLoadManager == null){
                    return new DownLoadManager();
                }
            }
        }
        return mDownLoadManager;
    }

    /**
     * 初始化入口
     * @param context
     */
    public static void init(Context context){
        mContext = context.getApplicationContext();
    }

    /**
     * 初始化证书
     * @param certName
     */
    public  void initCert(String certName){
        if (mContext != null) {
            HttpUtils.initCert(mContext, certName);
        }else {
            throw new RuntimeException("u must init context");
        }
    }

    public  void onDownloadListener(DownloadListener downloadListener){
        mDownloadListener = downloadListener;

    }



    public void download(@NonNull String url, final String downloadApkPath, final DownloadListener downloadListener){
        HttpUtils.getApi().downloadFile(url).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    writeResponseToDisk(downloadApkPath,response,downloadListener);

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                downloadListener.onDownloadFail(t.toString());
            }
        });
    }
    private  void writeResponseToDisk(String path, Response<ResponseBody> response, DownloadListener downloadListener) {
        //从response获取输入流以及总大小
        writeFileFromIS(new File(path), response.body().byteStream(), response.body().contentLength(), downloadListener);
    }

    private  int sBufferSize = 8192;

    //将输入流写入文件
    private  void writeFileFromIS(File file, InputStream is, long totalLength, DownloadListener downloadListener) {
        //开始下载
        downloadListener.onDownloadStart();

        //创建文件
        if (!file.exists()) {
            if (!file.getParentFile().exists())
                file.getParentFile().mkdir();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                downloadListener.onDownloadFail("createNewFile IOException");
            }
        }

        OutputStream os = null;
        long currentLength = 0;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            byte data[] = new byte[sBufferSize];
            int len;
            while ((len = is.read(data, 0, sBufferSize)) != -1) {
                os.write(data, 0, len);
                currentLength += len;
                //计算当前下载进度
                downloadListener.onDownloadProgress((int) (100 * currentLength / totalLength));
            }
            //下载完成，并返回保存的文件路径
            downloadListener.onDownloadFinish(file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            downloadListener.onDownloadFail("IOException");
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
