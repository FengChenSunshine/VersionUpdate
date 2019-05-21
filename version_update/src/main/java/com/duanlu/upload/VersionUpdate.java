package com.duanlu.upload;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;

/********************************
 * @name VersionUpdate
 * @author 段露
 * @createDate 2019/05/20 13:51
 * @updateDate 2019/05/20 13:51
 * @version V1.0.0
 * @describe 版本更新管理者.
 ********************************/
public class VersionUpdate implements CheckCallback, NewVersionActionCallback, OnDownloadStatusListener {

    private static final String TAG = "VersionUpdate";
    private static final String AUTHORITY = "version_update";
    private boolean isDebug;
    private Context mContext;
    private Checker mChecker;

    private DialogProvider mDialogProvider;
    private Dialog mNewVersionDialog;
    private Dialog mDownloadDialog;

    private DownloadStrategy mDownloadStrategy;

    private Postcard mPostcard;

    private OnDownloadStatusListener mDownloadStatusListener;

    private Handler mHandler;

    private VersionUpdate(Context context) {
        this.mContext = context;
    }

    public void check() {
        if (null == mChecker) {
            throw new RuntimeException("Please set Checker!!!");
        }
        mChecker.check(mContext, this);
    }

    @Override
    public void callback(Postcard postcard) {
        if (null != postcard && !TextUtils.isEmpty(postcard.downloadUrl)) {

            this.mPostcard = postcard;

            File file = new File(mPostcard.saveDir, mPostcard.saveName);
            if (!VersionUpdateUtils.createFileByDeleteOldFile(file)) {
                if (isDebug) {
                    Log.i(TAG, "版本更新——创建文件失败");
                }
                onError(new FileNotFoundException("创建文件失败"));
            }

            if (isDebug) {
                Log.i(TAG, String.format("版本更新——提醒更新[%s]", mPostcard.toString()));
            }

            ensureNewVersionDialog();

            showDialog(mNewVersionDialog);

        } else {
            if (isDebug) {
                Log.i(TAG, "版本更新——已经是最新版本了~");
            }
            //已经是最新版本了.
        }
    }

    @Override
    public void update() {
        if (isDebug) {
            Log.i(TAG, "版本更新——准备下载APK文件");
        }

        dismissDialog(mNewVersionDialog);

        ensureDownloadDialog();

        if (null == mHandler) {
            mHandler = new DownloadHandler(this);
        } else {
            onCancel();
        }

        mDownloadStrategy.download(mPostcard, this);
    }

    @Override
    public void ignore() {
        dismissDialog(mDownloadDialog);
    }

    @Override
    public void late() {
        dismissDialog(mDownloadDialog);
    }

    @Override
    public void cancel() {
        dismissDialog(mDownloadDialog);
    }

    private void ensureNewVersionDialog() {
        if (null == mNewVersionDialog && null != mDialogProvider) {
            mNewVersionDialog = mDialogProvider.createNewVersionDialog(mContext, this);
        }
        if (null == mNewVersionDialog) {
            throw new RuntimeException("NewVersionDialog is Null!");
        }
    }

    private void ensureDownloadDialog() {
        if (null == mDownloadDialog && null != mDialogProvider) {
            mDownloadDialog = mDialogProvider.createDownloadDialog(mContext);
        }

        if (!(mDownloadDialog instanceof OnDownloadStatusListener)) {
            throw new RuntimeException("DownloadDialog must implements OnDownloadStatusListener!");
        }
        mDownloadStatusListener = (OnDownloadStatusListener) mDownloadDialog;
    }

    @Override
    public void onStart(Postcard postcard) {
        if (isDebug) {
            Log.i(TAG, "开始下载APK文件");
        }
        mHandler.sendEmptyMessage(WHAT_START);
    }

    @Override
    public void onProgressChanged(long current, long total) {
        if (isDebug) {
            Log.i(TAG, String.format("APK文件下载进度：current=%d,total=%d", current, total));
        }
        Message message = Message.obtain(mHandler, WHAT_PROGRESS_CHANGED);
        Bundle bundle = new Bundle();
        bundle.putLong("current", current);
        bundle.putLong("total", total);
        message.setData(bundle);
        message.sendToTarget();
    }

    @Override
    public boolean onComplete(@NonNull Intent intent) {
        if (isDebug) {
            Log.i(TAG, "APK文件下载完成");
        }

        mHandler.sendEmptyMessage(WHAT_COMPLETE);
        return true;
    }

    @Override
    public boolean onCancel() {
        if (isDebug) {
            Log.i(TAG, "APK文件下载取消");
        }
        mHandler.sendEmptyMessage(WHAT_CANCEL);
        return true;
    }

    @Override
    public boolean onError(Throwable throwable) {
        if (isDebug) {
            Log.i(TAG, String.format("APK文件下载失败：%s", throwable.getMessage()));
        }

        Message message = Message.obtain(mHandler, WHAT_ERROR);
        Bundle bundle = new Bundle();
        bundle.putSerializable("throwable", throwable);
        message.setData(bundle);
        message.sendToTarget();

        return true;
    }

    private static final int WHAT_START = 1;
    private static final int WHAT_PROGRESS_CHANGED = 2;
    private static final int WHAT_COMPLETE = 3;
    private static final int WHAT_CANCEL = 4;
    private static final int WHAT_ERROR = 5;

    private static class DownloadHandler extends Handler {

        private WeakReference<VersionUpdate> mVersionUpdate;

        private DownloadHandler(VersionUpdate versionUpdate) {
            mVersionUpdate = new WeakReference<>(versionUpdate);
        }

        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            VersionUpdate versionUpdate = mVersionUpdate.get();
            boolean result;
            switch (msg.what) {
                case WHAT_START://开始下载.
                    versionUpdate.showDialog(versionUpdate.mDownloadDialog);

                    versionUpdate.mDownloadStatusListener.onStart(versionUpdate.mPostcard);
                    break;
                case WHAT_PROGRESS_CHANGED://下载进度改变.
                    Bundle bundle = msg.getData();
                    versionUpdate.mDownloadStatusListener.onProgressChanged(
                            bundle.getLong("current"),
                            bundle.getLong("total"));
                    break;
                case WHAT_COMPLETE://下载完成.
                    Context context = versionUpdate.mDownloadDialog.getContext();
                    File apkFile = new File(versionUpdate.mPostcard.saveDir, versionUpdate.mPostcard.saveName);
                    if (!VersionUpdateUtils.isFileExists(apkFile)) {
                        if (versionUpdate.isDebug) {
                            Log.i(TAG, "下载APK文件未找到");
                        }
                        versionUpdate.onError(new FileNotFoundException("下载APK文件未找到"));
                        return;
                    }
                    Intent intent = VersionUpdateUtils.getInstallAppIntent(context, apkFile, AUTHORITY);
                    result = versionUpdate.mDownloadStatusListener.onComplete(intent);
                    if (!result) {
                        if (versionUpdate.isDebug) {
                            Log.i(TAG, "安装APK文件：" + apkFile.getAbsolutePath());
                        }
                        context.startActivity(intent);
                    }
                    versionUpdate.dismissDialog(versionUpdate.mDownloadDialog);
                    break;
                case WHAT_CANCEL://下载取消.
                    if (versionUpdate.isDebug) {
                        Log.i(TAG, "下载取消");
                    }
                    result = versionUpdate.mDownloadStatusListener.onCancel();
                    versionUpdate.dismissDialog(versionUpdate.mDownloadDialog);
                    if (!result && versionUpdate.mPostcard.isForce) {
                        if (versionUpdate.isDebug) {
                            Log.i(TAG, "下载取消,当前为强制更新,2s后将自动退出应用");
                        }
                        VersionUpdateUtils.killApplication(2000);
                    }
                    break;
                case WHAT_ERROR://下载失败.
                    if (versionUpdate.isDebug) {
                        Log.i(TAG, "下载失败");
                    }
                    Bundle throwableBundle = msg.getData();
                    result = versionUpdate.mDownloadStatusListener.onError((Throwable) throwableBundle.getSerializable("throwable"));
                    versionUpdate.dismissDialog(versionUpdate.mDownloadDialog);
                    if (!result && versionUpdate.mPostcard.isForce) {
                        if (versionUpdate.isDebug) {
                            Log.i(TAG, "下载失败,当前为强制更新,2s后将自动退出应用");
                        }
                        VersionUpdateUtils.killApplication(2000);
                    }
                    break;
            }
        }
    }

    public static class Builder {
        private boolean isDebug;
        private Context context;
        private Checker checker;

        private DialogProvider dialogProvider;
        private DownloadStrategy downloadStrategy;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder debug(boolean debug) {
            this.isDebug = debug;
            return this;
        }

        public Builder checker(Checker checker) {
            this.checker = checker;
            return this;
        }

        public Builder dialogProvider(DialogProvider provider) {
            this.dialogProvider = provider;
            return this;
        }

        public Builder downloadStrategy(DownloadStrategy strategy) {
            this.downloadStrategy = strategy;
            return this;
        }

        public VersionUpdate build() {
            VersionUpdate versionUpdate = new VersionUpdate(context);
            versionUpdate.isDebug = isDebug;
            versionUpdate.mChecker = checker;
            versionUpdate.mDialogProvider = dialogProvider;
            versionUpdate.mDownloadStrategy = downloadStrategy;
            return versionUpdate;
        }

        public void check() {
            build().check();
        }
    }

    private void showDialog(Dialog dialog) {
        if (null != dialog && !dialog.isShowing()) {
            dialog.show();
        }
    }

    private void dismissDialog(Dialog dialog) {
        if (null != dialog && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

}
