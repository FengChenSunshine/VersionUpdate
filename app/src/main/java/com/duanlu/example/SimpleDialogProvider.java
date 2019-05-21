package com.duanlu.example;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
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
    public Dialog createNewVersionDialog(Context context, final NewVersionActionCallback callback) {
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
        return new AlertDialog.Builder(context)
                .setTitle("版本更新")
                .setMessage("有新版本啦~，是否更新?")
                .setNegativeButton("取消", listener)
                .setNeutralButton("稍后提醒", listener)
                .setPositiveButton("更新", listener)
                .create();
    }

    @Override
    public Dialog createDownloadDialog(Context context) {
        return new DownloadDialog(context);
    }

    private class DownloadDialog extends AlertDialog implements OnDownloadStatusListener {
        ProgressBar progressBar;
        TextView tvPercent;

        protected DownloadDialog(Context context) {
            super(context);


            setTitle("下载进度");

            LinearLayout container = new LinearLayout(context);
            container.setOrientation(LinearLayout.VERTICAL);

            progressBar = new ProgressBar(context);
            progressBar.setProgressDrawable(context.getResources().getDrawable(android.R.drawable.progress_horizontal));
            progressBar.setMinimumHeight(3);
            progressBar.setMax(100);
            container.addView(progressBar);

            tvPercent = new TextView(context);
            tvPercent.setText("0%");
            container.addView(tvPercent);

            setView(container);
        }

        @Override
        public void onStart(Postcard postcard) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                progressBar.setProgress(0, true);
            } else {
                progressBar.setProgress(0);
            }
            tvPercent.setText("0%");
        }

        @Override
        public void onProgressChanged(long current, long total) {
            double percent = ((double) current / (double) total);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                progressBar.setProgress((int) percent, true);
            } else {
                progressBar.setProgress((int) percent);
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
