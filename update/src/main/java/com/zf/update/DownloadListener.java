package com.zf.update;
/**
 * @author: zhufeng
 * @github: https://github.com/zhufengi
 * @time: 2019/10/25
 * @description: file download callback
 */
public interface DownloadListener {

    void onDownloadStart();
    void onDownloadProgress(int progress);
    void onDownloadFinish(String path);
    void onDownloadFail(String error);
}
