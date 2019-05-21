package com.duanlu.example;

import android.support.annotation.NonNull;

import com.duanlu.upload.DownloadStrategy;
import com.duanlu.upload.Postcard;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import java.io.File;

/********************************
 * @name OkGoDownloadStrategy
 * @author 段露
 * @createDate 2019/05/21 15:33
 * @updateDate 2019/05/21 15:33
 * @version V1.0.0
 * @describe OkGo下载策略.
 ********************************/
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
