package com.csx.m3u8lib;

import android.os.Handler;
import android.os.Message;

import android.support.annotation.NonNull;
import com.csx.m3u8lib.bean.M3U8;
import com.csx.m3u8lib.bean.OnM3U8InfoListener;
import com.csx.m3u8lib.utils.MUtils;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 获取M3U8信息的管理器
 * Created by HDL on 2017/8/10.
 */

public class M3U8InfoManger {
    private static M3U8InfoManger mM3U8InfoManger;
    private OnM3U8InfoListener onM3U8InfoListener;
    private static final int WHAT_ON_ERROR = 1101;
    private static final int WHAT_ON_SUCCESS = 1102;

    private Executor mCachePool;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_ON_ERROR:
                    onM3U8InfoListener.onError((Throwable) msg.obj);
                    break;
                case WHAT_ON_SUCCESS:
                    onM3U8InfoListener.onSuccess((M3U8) msg.obj);
                    break;
            }
        }
    };

    private M3U8InfoManger() {
        mCachePool = Executors.newCachedThreadPool(new ThreadFactory() {
            @Override
            public Thread newThread(@NonNull Runnable r) {
                return new Thread(r,"m3u8 Threads ");
            }
        });
    }

    public static M3U8InfoManger getInstance() {
        synchronized (M3U8InfoManger.class) {
            if (mM3U8InfoManger == null) {
                mM3U8InfoManger = new M3U8InfoManger();
            }
        }
        return mM3U8InfoManger;
    }

    /**
     * 获取m3u8信息
     *
     * @param url
     * @param onM3U8InfoListener
     */
    public synchronized void getM3U8Info(final String url, OnM3U8InfoListener onM3U8InfoListener) {
        this.onM3U8InfoListener = onM3U8InfoListener;
        onM3U8InfoListener.onStart();

        mCachePool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //Log.e("hdltag", "run(M3U8InfoManger.java:62):" + url);
                    M3U8 m3u8 = MUtils.parseIndex(url);
                    handlerSuccess(m3u8);
                } catch (IOException e) {
                    //                    e.printStackTrace();
                    handlerError(e);
                }
            }
        });
    }

    /**
     * 通知异常
     *
     * @param e
     */
    private void handlerError(Throwable e) {
        Message msg = mHandler.obtainMessage();
        msg.obj = e;
        msg.what = WHAT_ON_ERROR;
        mHandler.sendMessage(msg);
    }

    /**
     * 通知成功
     *
     * @param m3u8
     */
    private void handlerSuccess(M3U8 m3u8) {
        Message msg = mHandler.obtainMessage();
        msg.obj = m3u8;
        msg.what = WHAT_ON_SUCCESS;
        mHandler.sendMessage(msg);
    }
}
