package com.m3u8project;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.csx.m3u8lib.M3U8LiveManger;
import com.csx.m3u8lib.bean.OnDownloadListener;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @BindView(R.id.btn_start_download) Button mBtnStartDownload;
    @BindView(R.id.tv_hint_info) TextView mTvHintInfo;

    private String m3u8Path=
            "http://118.123.20.133:8080/attachment/video/20191211135029936/20191211135029936.m3u8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({ R.id.btn_start_download, R.id.tv_hint_info })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start_download:
                RxPermissionUtils.requestPermission(this, new RxPermissionUtils.OnRxPermissionCallBack() {
                    @Override
                    public void onGrant() {
                        downLoadM3U8();
                    }

                    @Override
                    public void onRefuse(boolean isNeverAskAgain) {
                        showToast("需要使用该权限，请开启！");
                    }
                }, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE);
                break;
            case R.id.tv_hint_info:
                break;
            default:
                break;
        }
    }

    private void downLoadM3U8() {

        M3U8LiveManger.getInstance(this).caching(m3u8Path, new OnDownloadListener() {
            @Override
            public void onDownloading(long itemFileSize, int totalTs, int curTs) {
                Log.i(TAG, "onDownloading: itemFileSize = "+itemFileSize+"  totalTs = "+totalTs+"  curTs = "+curTs);
            }

            @Override
            public void onSuccess() {
                Log.i(TAG, "onSuccess: ");
            }

            @Override
            public void onProgress(long curLength) {
                Log.i(TAG, "onProgress: curLength"+curLength);
            }

            @Override
            public void onStart() {
                Log.i(TAG, "onStart: ");
            }

            @Override
            public void onError(Throwable errorMsg) {
                Log.i(TAG, "onError: errorMsg.getMessage() = "+errorMsg.getMessage());
            }
        });


    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
