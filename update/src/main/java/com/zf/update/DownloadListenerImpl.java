package com.zf.update;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.zf.update.utils.InstallUtils;

import java.io.File;

/**
 * @author: zhufeng
 * @github: https://github.com/zhufengi
 * @time: 2019/10/30
 * @description: DownloadListenerImpl
 */
public class DownloadListenerImpl implements DownloadListener {

    private static final String TAG = "DownloadListenerImpl";
    private Context mContext;
    private DownloadBuilder mDownloadBuilder;

    public DownloadListenerImpl(Context context,DownloadBuilder downloadBuilder,DownloadListenerImpl mDownloadListenerImpl){
        this.mContext = context;
        this.mDownloadBuilder = downloadBuilder;
    }

    @Override
    public void onDownloadStart() {
        Log.d(TAG, "onDownloadStart ");
    }

    @SuppressLint("NewApi")
    @Override
    public void onDownloadProgress(int progress) {
        Log.d(TAG, "onDownloadProgress: "+progress);
        NotificationUtils.showNotification(mContext,"",String.format("下载进度:%d%%/100%%", progress),NotificationUtils.DEFAULT_ID,"update",progress,100);
    }

    @SuppressLint("NewApi")
    @Override
    public void onDownloadFinish(String path) {
        NotificationUtils.cancleNotification(mContext,NotificationUtils.DEFAULT_ID);
        if (mDownloadBuilder.isSilentDownload()){
            //静默下载，不立即安装。下次点击下载时再安装
            Config.isSilentDownload = true;
        }else {
            File file = new File(path);
            if (file.exists()) {
                InstallUtils.installApk(mContext, new File(path));
            }else {
                Log.e(TAG,"onDownloadFinish installApk path is error");
            }
        }
    }

    @Override
    public void onDownloadFail(String error) {

    }
}
