package com.zf.update.utils;

import android.util.Log;
import com.zf.update.DownloadListener;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author: zhufeng
 * @github: https://github.com/zhufengi
 * @time: 2019/10/29
 * @description:
 */
public class IOUtils {

    private static String TAG = "IOUtils";
    /**
     * 文件最终完成路径
     */
    public static String getFilePath = "";


    /**
     * 根据输入流，保存文件
     *
     * @param file
     * @param inputStream
     * @return
     */
    public static boolean writeFile(File file, InputStream inputStream) {
        OutputStream outputStream = null;
        try {
            byte[] fileReader = new byte[4096];
            long fileSizeDownloaded = 0;
            outputStream = new FileOutputStream(file);
            while (true) {
                int read = inputStream.read(fileReader);
                if (read == -1) {
                    break;
                }
                outputStream.write(fileReader, 0, read);
                fileSizeDownloaded += read;
                //计算当前下载百分比，并经由回调传出
                Log.d(TAG, "writeFile file : " + fileSizeDownloaded);
            }
            Log.d("writeFile", "onDownloadFinish file=" + file.getPath());
            outputStream.flush();
            getFilePath = file.getPath();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            if (file != null && file.exists()) {
                file.deleteOnExit();
            }
            return false;
        } finally {
            closeStream(inputStream);
            closeStream(outputStream);
        }
    }

    /**
     * 根据输入流，保存文件
     *
     * @param file
     * @param inputStream
     * @param fileSize
     * @param downloadListener
     * @return
     */
    public static boolean writeFile(File file, InputStream inputStream, long fileSize, DownloadListener downloadListener) {
        OutputStream outputStream = null;
        try {
            byte[] fileReader = new byte[4096];
            long fileSizeDownloaded = 0;
            outputStream = new FileOutputStream(file);
            while (true) {
                int read = inputStream.read(fileReader);
                if (read == -1) {
                    break;
                }
                outputStream.write(fileReader, 0, read);
                fileSizeDownloaded += read;
                //计算当前下载百分比，并经由回调传出
                if (downloadListener != null) {
                    downloadListener.onDownloadProgress((int) (100 * fileSizeDownloaded / fileSize));
                }
                Log.d(TAG, "writeFile file download: " + fileSizeDownloaded + " of " + fileSize);
            }
            if (downloadListener != null) {
                downloadListener.onDownloadFinish(file.getPath());
            }
            Log.e("writeFile", "onDownloadFinish file :" + file.getPath());
            outputStream.flush();
            getFilePath = file.getPath();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            if (file != null && file.exists()) {
                file.deleteOnExit();
            }
            if (downloadListener != null) {
                downloadListener.onDownloadFail("" + e.getMessage());
            }
            return false;
        } finally {
            closeStream(inputStream);
            closeStream(outputStream);
        }
    }



    /**
     * 关闭流
     *
     * @param closeable
     */
    public static void closeStream(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
