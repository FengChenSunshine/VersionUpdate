package com.duanlu.upload;

/********************************
 * @name NewVersionActionCallback
 * @author 段露
 * @createDate 2019/05/20 14:58
 * @updateDate 2019/05/20 14:58
 * @version V1.0.0
 * @describe 新版本弹出操作回调.
 ********************************/
public interface NewVersionActionCallback {

    void update();

    void ignore();

    void late();

    void cancel();
}
