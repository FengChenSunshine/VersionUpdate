package com.duanlu.upload;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.IOException;

/********************************
 * @name VersionUpdateUtils
 * @author 段露
 * @createDate 2019/05/20 10:15
 * @updateDate 2019/05/20 10:15
 * @version V1.0.0
 * @describe 版本更新工具类.
 ********************************/
class VersionUpdateUtils {

    static Intent getInstallAppIntent(@NonNull Context context, @NonNull File file, String authority) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //适配API23 Android6.0以上获取文件Uri.
        Uri contentUri = getUriFromFile(context, file, authority);
        String type;
        //API23 Android6.0以上获取type.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            type = "application/vnd.android.package-archive";
        } else {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(getFileExtension(file));
        }
        //API24 Android7.0以上需要添加权限.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(contentUri, type);
        return intent;
    }

    private static Uri getUriFromFile(Context context, File file, String authority) {
        Uri uri = null;
        if (null != file) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //第二个参数为在Manifest定义的provider的authorities属性
                uri = VersionUpdateFileProvider.getUriForFile(context, authority, file);
            } else {
                uri = Uri.fromFile(file);
            }
        }
        return uri;
    }

    private static String getFileExtension(File file) {
        if (file == null) return null;
        return getFileExtension(file.getPath());
    }

    private static String getFileExtension(String filePath) {
        if (TextUtils.isEmpty(filePath.trim())) return filePath;
        int lastPoi = filePath.lastIndexOf('.');
        int lastSep = filePath.lastIndexOf(File.separator);
        if (lastPoi == -1 || lastSep >= lastPoi) return "";
        return filePath.substring(lastPoi + 1);
    }

    static boolean isFileExists(File file) {
        return file != null && file.exists();
    }

    static boolean createFileByDeleteOldFile(File file) {
        if (file == null) return false;
        // 文件存在并且删除失败返回false
        if (file.exists() && file.isFile() && !file.delete()) return false;
        // 创建目录失败返回false
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean createOrExistsFile(File file) {
        if (file == null) return false;
        // 如果存在，是文件则返回true，是目录则返回false
        if (file.exists()) return file.isFile();
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean createOrExistsDir(File file) {
        // 如果存在，是目录则返回true，是文件则返回false，不存在则返回是否创建成功
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    static void killApplication(int delayMillis) {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                AppExit();
            }
        }, delayMillis);
    }

    private static void AppExit() {
        try {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
