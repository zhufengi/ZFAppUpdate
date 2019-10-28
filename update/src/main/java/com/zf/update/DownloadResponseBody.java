package com.zf.update;

import android.util.Log;

import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * @author: zhufeng
 * @github: https://github.com/zhufengi
 * @time: 2019/10/25
 * @description: 重写ResponseBody实现下载进度条
 */
public class DownloadResponseBody extends ResponseBody {

    private ResponseBody mResponseBody;
    private BufferedSource mBufferedSource;
    private DownloadListener mDownloadListener;

    public DownloadResponseBody(ResponseBody responseBody,DownloadListener mDownloadListener){
        this.mResponseBody = responseBody;
        this.mDownloadListener = mDownloadListener;
    }

    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    @Override
    public long contentLength() {
        return mResponseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (mBufferedSource == null) {
            mBufferedSource = Okio.buffer(source(mResponseBody.source()));
        }
        return mBufferedSource;
    }

    /**
     * 实现下载进度
     * @param source
     * @return
     */
    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;
            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                Log.e("download", "read: "+ (int) (totalBytesRead * 100 / contentLength()));
                if (mDownloadListener != null) {
                    if (bytesRead != -1) {
                        mDownloadListener.onDownloadProgress((int) (totalBytesRead * 100 / contentLength()));
                    }
                }
                return bytesRead;
            }
        };

    }
}