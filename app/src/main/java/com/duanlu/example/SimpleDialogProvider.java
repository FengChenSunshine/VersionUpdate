package com.duanlu.example;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.duanlu.upload.DialogProvider;
import com.duanlu.upload.NewVersionActionCallback;
import com.duanlu.upload.OnDownloadStatusListener;
import com.duanlu.upload.Postcard;

import java.util.Locale;

/********************************
 * @name SimpleDialogProvider
 * @author 段露
 * @createDate 2019/05/20 17:06
 * @updateDate 2019/05/20 17:06
 * @version V1.0.0
 * @describe 一个简单的DialogProvider.
 ********************************/
public class SimpleDialogProvider implements DialogProvider {

    @Override
    public Dialog createNewVersionDialog(@NonNull Context context, @NonNull Postcard postcard, final @NonNull NewVersionActionCallback callback) {
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

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("版本更新")
                .setMessage(postcard.versionDesc)
                .setPositiveButton("更新", listener)
                .setCancelable(false);
        if (!postcard.isForce) {
            builder.setNegativeButton("取消", listener)
                    .setNeutralButton("稍后提醒", listener);
        }

        return builder.create();
    }

    @Override
    public Dialog createDownloadDialog(@NonNull Context context, @NonNull Postcard postcard) {
        return new DownloadDialog(context);
    }

    private class DownloadDialog extends AlertDialog implements OnDownloadStatusListener {
        ProgressBar progressBar;
        TextView tvPercent;

        protected DownloadDialog(Context context) {
            super(context);

            setCancelable(false);

            setTitle("下载进度");

            View container = LayoutInflater.from(context).inflate(R.layout.dialog_version_update_download, null);
            setView(container);

            progressBar = container.findViewById(R.id.progress_bar);
            tvPercent = container.findViewById(R.id.tv_percent);
        }

        @Override
        public void onStart(Postcard postcard) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                progressBar.setProgress(0, true);
            } else {
                progressBar.setProgress(0);
            }
            tvPercent.setText("0.0%");
        }

        @Override
        public void onProgressChanged(long current, long total) {
            double percent = ((double) current / (double) total);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                progressBar.setProgress((int) (percent * 100), true);
            } else {
                progressBar.setProgress((int) (percent * 100));
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
