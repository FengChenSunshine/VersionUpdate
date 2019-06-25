# VersionUpdate
### 提供一个应用版本更新功能的库！[![](https://jitpack.io/v/FengChenSunshine/VersionUpdate.svg)](https://jitpack.io/#FengChenSunshine/VersionUpdate)
[![](https://img.shields.io/badge/license-Apache2.0-brightgreen.svg)](https://github.com/FengChenSunshine/LibraryModel/blob/master/LICENSE)

### 优势：
 1.完全解耦：从版本检查到下载安装每一步都完全解耦，通过接口实现可以自由配置；
 
 2.兼容性强：使用该库无需关心各版本差异造成的适配问题，其内部已经兼容了各版本差异；
 
 3.使用简单：链式调用，逻辑清晰，使用简单。

### 使用方法：
	private void checkVersion() {
        new VersionUpdate.Builder(MainActivity.this)
                .debug(true)
                .checker(new VersionChecker())
                .dialogProvider(new SimpleDialogProvider())
                .downloadStrategy(new OkGoDownloadStrategy())
                .build()
                .check();
    }
 1.其中debug()方法传入true时可以调试打印详细日志信息;
 
 2.checker()方法传入一个实现了Checker接口的对象，该方法内一般调取接口实现自己的版本检查逻辑，并构造一个Postcard对象返回；
 
 3.dialogProvider()方法传入一个实现了DialogProvider接口的对象，该对象提供了创建新版本提醒Dialog和下载进度Dialog，其中下载进度Dialog必须实现OnDownloadStatusListener接口；
 
 4.downloadStrategy()方法传入一个继承DownloadStrategy的对象，该对象实现了具体的下载逻辑，并通过其内部mDownloadStatusListener对象回调给VersionUpdate库。使用时可以自行实现自己的下载策略，比如基于OkGo、OkHttp、DownloadManager等；
 
 5.最后通过build()方法构建VersionUpdate对象并调用其check()方法开启检查。
 
 6.注意：由于该库的重点不在于权限申请，所以针对Android6.0以上需要自行判断读写权限是否赋予！！！

## 简单使用举例：
### step 1.
	private void checkVersion() {
        new VersionUpdate.Builder(MainActivity.this)
                .debug(true)
                .checker(new VersionChecker())
                .dialogProvider(new SimpleDialogProvider())
                .downloadStrategy(new OkGoDownloadStrategy())
                .build()
                .check();
    }
### Checker举例.
	public class VersionChecker implements Checker {
    	     @Override
    	     public void check(Context context, CheckCallback callback) {
        	 String downloadUrl = "http://gdown.baidu.com/data/wisegame/f529780563bd7983/yingyongbao_7362130.apk";
        	 Postcard postcard = new Postcard.Builder(downloadUrl)
                	  .setForce(true)
                	  .setSaveConfig(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AAA/", "test.apk")
                	  .build();
        	 callback.callback(postcard);
    }
}

### DialogProvider举例.
	public class SimpleDialogProvider implements DialogProvider {

    @Override
    public Dialog createNewVersionDialog(Context context, final NewVersionActionCallback callback) {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_NEGATIVE:
                        callback.cancel();
                        break;
                    case DialogInterface.BUTTON_NEUTRAL:
                        callback.late();
                        break;
                    case DialogInterface.BUTTON_POSITIVE:
                        callback.update();
                        break;
                }
            }
        };
        return new AlertDialog.Builder(context)
                .setTitle("版本更新")
                .setMessage("有新版本啦~，是否更新?")
                .setNegativeButton("取消", listener)
                .setNeutralButton("稍后提醒", listener)
                .setPositiveButton("更新", listener)
                .create();
    }

    @Override
    public Dialog createDownloadDialog(Context context) {
        return new DownloadDialog(context);
    }

    private class DownloadDialog extends AlertDialog implements OnDownloadStatusListener {
        ProgressBar progressBar;
        TextView tvPercent;

        protected DownloadDialog(Context context) {
            super(context);

            setTitle("下载进度");

            LinearLayout container = new LinearLayout(context);
            container.setOrientation(LinearLayout.VERTICAL);

            progressBar = new ProgressBar(context);
            progressBar.setProgressDrawable(context.getResources().getDrawable(android.R.drawable.progress_horizontal));
            progressBar.setMinimumHeight(3);
            progressBar.setMax(100);
            container.addView(progressBar);

            tvPercent = new TextView(context);
            tvPercent.setText("0%");
            container.addView(tvPercent);

            setView(container);
        }

        @Override
        public void onStart(Postcard postcard) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                progressBar.setProgress(0, true);
            } else {
                progressBar.setProgress(0);
            }
            tvPercent.setText("0%");
        }

        @Override
        public void onProgressChanged(long current, long total) {
            double percent = ((double) current / (double) total);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                progressBar.setProgress((int) percent, true);
            } else {
                progressBar.setProgress((int) percent);
            }
            tvPercent.setText(String.format(Locale.getDefault(), "%.1f", percent * 100) + "%");
        }

        @Override
        public boolean onComplete(Intent intent) {
            return false;
        }

        @Override
        public boolean onCancel() {
            return false;
        }

        @Override
        public boolean onError(Throwable throwable) {
            Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }
    }

}

### DownloadStrategy举例.
	public class OkGoDownloadStrategy extends DownloadStrategy {
    @Override
    protected void executeDownload(final @NonNull Postcard postcard) {
        OkGo.<File>get(postcard.downloadUrl).execute(new FileCallback(postcard.saveDir, postcard.saveName) {
            @Override
            public void onStart(Request<File, ? extends Request> request) {
                super.onStart(request);
                mDownloadStatusListener.onStart(postcard);
            }

            @Override
            public void downloadProgress(Progress progress) {
                super.downloadProgress(progress);
                mDownloadStatusListener.onProgressChanged(progress.currentSize, progress.totalSize);
            }

            @Override
            public void onSuccess(Response<File> response) {
                mDownloadStatusListener.onComplete(null);
            }

            @Override
            public void onError(Response<File> response) {
                super.onError(response);
                mDownloadStatusListener.onError(response.getException());
            }
        });
    }
}


## 怎样使用？
Step 1. Add the JitPack repository to your build file,Add it in your root build.gradle at the end of repositories:

    allprojects {
		    repositories {
			      ...
			      maven { url 'https://jitpack.io' }
		    }
	  }
Step 2. Add the dependency

    dependencies {
	          implementation 'com.github.FengChenSunshine:VersionUpdate:v1.0.2'
	  }
	  
## 8.版本说明
 
### v1.0.2 
   1.创建Dialog接口参数里增加Postcard对象，这样方便在创建Dialog时根据不同条件创建不同内容的Dialog。
   
### v1.0.1 
   1.修改使用该库的2个APP不能同时安装的问题。
