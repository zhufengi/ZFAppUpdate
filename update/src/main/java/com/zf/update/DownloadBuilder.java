package com.zf.update;

import android.content.Context;

import androidx.annotation.NonNull;

import com.zf.update.net.HttpRequestType;

/**
 * @author: zhufeng
 * @github: https://github.com/zhufengi
 * @time: 2019/10/29
 * @description:
 */
public class DownloadBuilder {

    /*静默下载*/
    private boolean isSilentDownload;
    /*下载后路径*/
    private String downloadFilePath;
    /*是否强制下载*/
    private boolean isForceDownload;
    /*下载url*/
    private String downloadUrl;
    /*是否显示通知栏*/
    private boolean isShowNotification;
    /*文件名字*/
    private String fileName;
    /*请求类型*/
    private HttpRequestType httpRequestType;
    private NotificationBuilder notificationBuilder;
    private DownloadListener mDownloadListener;
    private Context mContext;


    private DownloadBuilder(Builder builder) {
        this.isSilentDownload = builder.isSilentDownload;
        this.downloadFilePath = builder.downloadFilePath;
        this.isForceDownload = builder.isForceDownload;
        this.isShowNotification = builder.isShowNotification;
        this.notificationBuilder = builder.notificationBuilder;
        this.downloadUrl = builder.downloadUrl;
        this.fileName = builder.fileName;
        this.mDownloadListener = builder.mDownloadListener;
        this.httpRequestType = builder.httpRequestType;
        this.mContext = builder.mContext;
        new NotificationUtils(this);
        new DownloadListenerImpl(mContext,this, (DownloadListenerImpl) mDownloadListener);
    }

   public static class Builder {
        /*静默下载*/
        private boolean isSilentDownload = false;
        /*下载后路径*/
        private String downloadFilePath;
        /*是否强制下载*/
        private boolean isForceDownload = false;
        /*下载url*/
        private String downloadUrl;
        /*是否显示通知栏*/
        private boolean isShowNotification = true;
        /*文件名字*/
        private String fileName;
        private HttpRequestType httpRequestType;
        private NotificationBuilder notificationBuilder;
        private DownloadListener mDownloadListener;
       private Context mContext;

        public Builder with(Context context) {
            this.mContext = context.getApplicationContext();
            return this;
        }
        public Builder isSilent(boolean isSilentDownload) {
            this.isSilentDownload = isSilentDownload;
            return this;
        }

        public Builder setFilePath(String downloadFilePath) {
            this.downloadFilePath = downloadFilePath;
            return this;
        }

        public Builder isForce(boolean isForceDownload) {
            this.isForceDownload = isForceDownload;
            return this;
        }

        public Builder setUrl(@NonNull String downloadUrl) {
            this.downloadUrl = downloadUrl;
            return this;
        }

        public Builder isShowNotification(boolean isShowNotification) {
            this.isShowNotification = isShowNotification;
            return this;
        }

        public Builder setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }


        public Builder onDownloadListener(DownloadListener downloadListener) {
            this.mDownloadListener = downloadListener;
            return this;
        }

        public Builder setNotificationBuilder(NotificationBuilder notificationBuilder) {
            this.notificationBuilder = notificationBuilder;
            return this;
        }
        public Builder setRequset(HttpRequestType httpRequestType){
            this.httpRequestType = httpRequestType;
            return this;
        }
        public DownloadBuilder build(){
            if (httpRequestType == null ||httpRequestType == HttpRequestType.GET){
                DownLoadManager.getInstance().download(downloadUrl,downloadFilePath,fileName,mDownloadListener);
            }else {
                DownLoadManager.getInstance().downloadPost(downloadUrl,downloadFilePath,fileName,mDownloadListener);
            }
            return new DownloadBuilder(this);
        }

    }

    public boolean isSilentDownload() {
        return isSilentDownload;
    }

    public String getDownloadFilePath() {
        return downloadFilePath;
    }

    public boolean isForceDownload() {
        return isForceDownload;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public boolean isShowNotification() {
        return isShowNotification;
    }

    public String getFileName() {
        return fileName;
    }

    public HttpRequestType getHttpRequestType() {
        return httpRequestType;
    }

    public NotificationBuilder getNotificationBuilder() {
        return notificationBuilder;
    }
}
