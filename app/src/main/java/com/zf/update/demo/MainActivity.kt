package com.zf.update.demo

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.BaseAdapter
import com.zf.update.*
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions



class MainActivity : AppCompatActivity(){

    val TAG = "MainActivity"
    val apkUrl ="https://test-1251233192.coscd.myqcloud.com/1_1.apk"

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
        if (EasyPermissions.hasPermissions(this,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_LOGS)) {
            builder()
        }else{
            EasyPermissions.requestPermissions(this, "需要授权读写外部存储权限!", 1, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_LOGS);
        }
    }

    /**
     * 普通模式
     */
    fun normal(){
        DownLoadManager.getInstance().download(apkUrl,"","",object : DownloadListener{
            override fun onDownloadStart() {
                Log.i(TAG,"onDownloadStart ...")
            }

            override fun onDownloadProgress(progress: Int) {
                Log.i(TAG,"onDownloadProgress ...progress:"+progress)
            }

            override fun onDownloadFinish(path:String?) {
                Log.i(TAG,"onDownloadFinish ...path:"+String)
            }

            override fun onDownloadFail(error: String?) {
                Log.i(TAG,"onDownloadFail ...error:"+error)
            }

        })
    }

    /**
     * build模式
     */
    fun builder(){

       DownloadBuilder.Builder()
           .with(this)
           .setUrl(apkUrl)
           .isShowNotification(true)
           .onDownloadListener(object : DownloadListener{
               override fun onDownloadStart() {
                   Log.i(TAG,"builder onDownloadStart ...")
               }

               override fun onDownloadProgress(progress: Int) {
                   Log.i(TAG,"builder onDownloadProgress ...progress:"+progress)
               }

               override fun onDownloadFinish(path:String?) {
                   Log.i(TAG,"builder onDownloadFinish ...path:"+String)
               }

               override fun onDownloadFail(error: String?) {
                   Log.i(TAG,"builder onDownloadFail ...error:"+error)
               }

           }).build()
    }
}
