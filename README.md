# ZFAppUpdate
android app update of All

#Builder
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
