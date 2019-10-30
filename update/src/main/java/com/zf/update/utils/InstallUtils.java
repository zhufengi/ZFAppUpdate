package com.zf.update.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import java.io.File;
/**
 * @author: zhufeng
 * @github: https://github.com/zhufengi
 * @time: 2019/10/30
 * @description: apk安装工具类
 */
public class InstallUtils {

    private static final String TAG = "InstallUtils";
    private static final String MIME_TYPE_APK = "application/vnd.android.package-archive";

    /**
     * 安装 apk 文件
     *
     * @param apkFile
     */
    public static void installApk(Context context, File apkFile) {
        Intent installApkIntent = new Intent();
        installApkIntent.setAction(Intent.ACTION_VIEW);
        installApkIntent.addCategory(Intent.CATEGORY_DEFAULT);
        installApkIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            installApkIntent.setDataAndType(FileProvider.getUriForFile(context, getFileProviderAuthority(context), apkFile), MIME_TYPE_APK);
            installApkIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            installApkIntent.setDataAndType(Uri.fromFile(apkFile), MIME_TYPE_APK);
        }
        if (context.getPackageManager().queryIntentActivities(installApkIntent, 0).size() > 0) {
            context.startActivity(installApkIntent);
        }
    }

    /**
     * 获取FileProvider的auth
     *
     * @return
     */
    private static String getFileProviderAuthority(Context context) {
        try {
            for (ProviderInfo provider : context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PROVIDERS).providers) {
                if (FileProvider.class.getName().equals(provider.name) && provider.authority.endsWith(".file_provider")) {
                    return provider.authority;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
