package com.duanlu.example;

import android.content.Context;
import android.os.Environment;

import com.duanlu.upload.CheckCallback;
import com.duanlu.upload.Checker;
import com.duanlu.upload.Postcard;

/********************************
 * @name VersionChecker
 * @author 段露
 * @createDate 2019/05/21 15:32
 * @updateDate 2019/05/21 15:32
 * @version V1.0.0
 * @describe 版本更新检查.
 ********************************/
public class VersionChecker implements Checker {
    @Override
    public void check(Context context, CheckCallback callback) {
        String downloadUrl = "http://gdown.baidu.com/data/wisegame/f529780563bd7983/yingyongbao_7362130.apk";
        Postcard postcard = new Postcard.Builder(downloadUrl)
                .setForce(true)
                .setVersionDesc("有新版本啦~，是否更新?")
                .setVersionCode("1")
                .setVersionName("v1.0.3")
                .setSaveConfig(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AAA/", "test.apk")
                .build();
        callback.callback(postcard);
    }
}
