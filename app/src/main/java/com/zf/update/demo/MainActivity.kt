package com.zf.update.demo

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import com.zf.update.DownLoadManager
import com.zf.update.DownloadListener
import com.zf.update.R
import kotlinx.android.synthetic.main.activity_main.*
import org.reactivestreams.Subscriber
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions



class MainActivity : AppCompatActivity(){

    val TAG = "MainActivity"
    val apkUrl ="https://test-1251233192.coscd.myqcloud.com/1_1.apk"
    /**
     * 下载文件权限请求码
     */
    private val RC_PERMISSION_DOWNLOAD = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DownLoadManager.init(this.applicationContext)
        btn.setOnClickListener {
          downloadApkFile()
        }

    }

    @AfterPermissionGranted(1)
     fun downloadApkFile() {
        if (EasyPermissions.hasPermissions(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            DownLoadManager.getInstance().download(apkUrl,Environment.getExternalStorageState()+"/down/",object : DownloadListener{
                override fun onDownloadStart() {
                    Log.i(TAG,"onDownloadStart ...")
                }

                override fun onDownloadProgress(progress: Int) {
                    Log.i(TAG,"onDownloadProgress ...progress:"+progress)
                }

                override fun onDownloadFinish(path:String?) {
                    Log.i(TAG,"onDownloadFinish ...")
                }

                override fun onDownloadFail(error: String?) {
                    Log.i(TAG,"onDownloadFail ...error:"+error)
                }

            })

        }else{
            EasyPermissions.requestPermissions(this, "需要授权读写外部存储权限!", 1, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }
}
