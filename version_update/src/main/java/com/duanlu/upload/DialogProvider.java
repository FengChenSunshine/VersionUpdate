package com.duanlu.upload;

import android.app.Dialog;
import android.content.Context;

/********************************
 * @name DialogProvider
 * @author 段露
 * @createDate 2019/05/20 14:56
 * @updateDate 2019/05/20 14:56
 * @version V1.0.0
 * @describe Dialog提供者.
 ********************************/
public interface DialogProvider {

    /**
     * 新版本提醒Dialog，必须回调NewVersionActionCallback相关方法.
     */
    Dialog createNewVersionDialog(Context context, NewVersionActionCallback callback);

    /**
     * 下载进度Dialog,必须实现OnDownloadStatusListener方法.
     */
    Dialog createDownloadDialog(Context context);
}
