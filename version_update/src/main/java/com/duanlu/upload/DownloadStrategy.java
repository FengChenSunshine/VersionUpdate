package com.duanlu.upload;

import android.support.annotation.NonNull;

/********************************
 * @name DownloadStrategy
 * @author 段露
 * @createDate 2019/05/20 13:44
 * @updateDate 2019/05/20 13:44
 * @version V1.0.0
 * @describe 下载策略.
 ********************************/
public abstract class DownloadStrategy {

    protected OnDownloadStatusListener mDownloadStatusListener;

    void download(@NonNull Postcard postcard, @NonNull OnDownloadStatusListener listener) {
        this.mDownloadStatusListener = listener;

        executeDownload(postcard);
    }

    protected abstract void executeDownload(@NonNull Postcard postcard);

}