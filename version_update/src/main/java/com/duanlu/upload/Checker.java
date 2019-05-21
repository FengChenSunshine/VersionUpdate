package com.duanlu.upload;

import android.content.Context;

/********************************
 * @name Checker
 * @author 段露
 * @createDate 2019/05/20 13:57
 * @updateDate 2019/05/20 13:57
 * @version V1.0.0
 * @describe 版本更新检查者.
 ********************************/
public interface Checker {

    void check(Context context, CheckCallback callback);

}
