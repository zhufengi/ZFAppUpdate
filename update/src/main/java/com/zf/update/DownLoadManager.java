package com.zf.update;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import com.zf.update.net.HttpUtils;
import com.zf.update.utils.IOUtils;
import com.zf.update.utils.InstallUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    private static final String TAG = "DownLoadManager";
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



    public void download(@NonNull String url, final String filePath, final String fileName, final DownloadListener downloadListener){
//        new HttpUtils(downloadListener);
        HttpUtils.getApi().downloadFile(url).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (response.isSuccessful()) {
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            writeResponseBodyToDisk(response.body(),downloadListener);
//                        }
//                    }).start();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            File file = null;
                            file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)+"/"+ "app.apk");
                            if (file.exists()) {
                                file.delete();
                            }
                            IOUtils.writeFile(file,response.body().byteStream(),response.body().contentLength(),downloadListener);
                        }
                    }).start();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                downloadListener.onDownloadFail(t.toString());
            }
        });
    }

    /**
     * post请求下载文件
     * @param url
     * @param filePath
     * @param fileName
     * @param downloadListener
     */
    public void downloadPost(@NonNull String url, final String filePath, final String fileName, final DownloadListener downloadListener){
        HttpUtils.getApi().downloadFilePost(url).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            File file = null;
                            file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)+"/"+ "app.apk");
                            if (file.exists()) {
                                file.delete();
                            }
                            IOUtils.writeFile(file,response.body().byteStream(),response.body().contentLength(),downloadListener);
                        }
                    }).start();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                downloadListener.onDownloadFail(t.toString());
            }
        });
    }

    /**
     * 文件写到存储位置
     * @param body
     * @param downloadListener
     * @return
     */
    @SuppressLint("NewApi")
    private boolean writeResponseBodyToDisk(ResponseBody body, final DownloadListener downloadListener) {
        if (downloadListener!=null) {
            downloadListener.onDownloadStart();
        }
        try {
            File file = null;
            file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)+"/"+ "app.apk");
            if (file.exists()) {
                file.delete();
            }
            Log.e(TAG,"writeResponseBodyToDisk file ："+file.getPath());
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                //文件总大小
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    //计算当前下载百分比，并经由回调传出
                    if (downloadListener!=null) {
                        downloadListener.onDownloadProgress((int) (100 * fileSizeDownloaded / fileSize));
                        NotificationUtils.showNotification(mContext,"",String.format("下载进度:%d%%/100%%", (int) (100 * fileSizeDownloaded / fileSize)),NotificationUtils.DEFAULT_ID,"update",(int) (100 * fileSizeDownloaded / fileSize),100);
                    }
                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }
                if (downloadListener!=null) {
                    downloadListener.onDownloadFinish(file.getPath());
                    NotificationUtils.cancleNotification(mContext,NotificationUtils.DEFAULT_ID);
                }
                Log.e(TAG,"onDownloadFinish file="+file.getPath());
                outputStream.flush();
                //设置静默下载时，不立即安装
                if (!Config.isSilentDownload){
                    //设置下载成功后就立即安装
                    InstallUtils.installApk(mContext,file);
                }
                return true;
            } catch (IOException e) {
                if (downloadListener!=null) {
                    downloadListener.onDownloadFail("" + e.getMessage());
                }
                e.printStackTrace();
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 文件写到存储位置
     * @param body
     * @param downloadListener
     * @return
     */
    private boolean writeResponseBodyToDisk(ResponseBody body,String filePath,String fileName,final DownloadListener downloadListener) {
        if (downloadListener!=null) {
            downloadListener.onDownloadStart();
        }
        try {
            File file = null;
            file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)+"/"+ "app.apk");
            if (file.exists()) {
                file.delete();
            }
            Log.e(TAG,"writeResponseBodyToDisk file ："+file.getPath());
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                //文件总大小
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    //计算当前下载百分比，并经由回调传出
                    if (downloadListener!=null) {
                        downloadListener.onDownloadProgress((int) (100 * fileSizeDownloaded / fileSize));
                    }
                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }
                if (downloadListener!=null) {
                    downloadListener.onDownloadFinish(file.getPath());
                }
                Log.e(TAG,"onDownloadFinish file="+file.getPath());
                outputStream.flush();
                //设置下载成功后就立即安装
                InstallUtils.installApk(mContext,file);
                return true;
            } catch (IOException e) {
                if (downloadListener!=null) {
                    downloadListener.onDownloadFail("" + e.getMessage());
                }
                e.printStackTrace();
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
