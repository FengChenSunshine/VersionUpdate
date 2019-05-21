package com.duanlu.upload;

import android.content.Intent;

/********************************
 * @name OnDownloadStatusListener
 * @author 段露
 * @createDate 2019/05/20 10:10
 * @updateDate 2019/05/20 10:10
 * @version V1.0.0
 * @describe 下载状态监听器.
 ********************************/
public interface OnDownloadStatusListener {

    void onStart(Postcard postcard);

    void onProgressChanged(long current, long total);

    /**
     * 下载完成.
     *
     * @param intent 安装intent.
     * @return 返回true代表已处理, false未处理交由后续程序处理.
     */
    boolean onComplete(Intent intent);

    /**
     * 下载取消.
     *
     * @return 返回true表示自己已处理后续程序不再处理;返回false时，如果是强制更新时默认推出应用.
     */
    boolean onCancel();

    /**
     * 下载失败.
     *
     * @return 返回true表示自己已处理后续程序不再处理;返回false时，如果是强制更新时默认推出应用.
     */
    boolean onError(Throwable throwable);
}
