package com.example.downloaddemo.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.downloaddemo.bean.FileInfo;
import com.example.downloaddemo.manager.DownloadManager;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @Author haoping
 * @Date 2016-10-13
 * @Des TODO
 */
public class DownloadService extends Service {

    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_STOP = "ACTION_STOP";
    public static final String ACTION_UPDATE = "ACTION_UPDATE";
    private static final int MSG_INIT = 0;
    private DownloadManager mDownloadManager;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_INIT) {
                //获取数据
                FileInfo fileInfo = (FileInfo) msg.obj;
                //开启线程下载
                mDownloadManager = DownloadManager.getInstance(DownloadService.this);
                mDownloadManager.multiThreadDownload(fileInfo, 3);
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (ACTION_START.equals(intent.getAction())) {
            // 接收Activity传过来的数据
            FileInfo fileInfo = intent.getParcelableExtra("mFileInfo");
            Log.i("test", "start: " + fileInfo.toString());
            // 开始下载，开启线程
            new InitThread(fileInfo).start();

        } else if (ACTION_STOP.equals(intent.getAction())) {
            // 接收Activity传过来的数据
            int id = intent.getIntExtra("fileId", 0);
            // 暂停下载
            mDownloadManager.pauseDownload(id);
            Log.i("test", "stop: " + id);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 初始化子线程
     */
    class InitThread extends Thread {
        private FileInfo mFileInfo = null;

        public InitThread(FileInfo fileInfo) {
            this.mFileInfo = fileInfo;
        }

        @Override
        public void run() {
            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            try {
                // 连接网络文件
                URL url = new URL(mFileInfo.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                int length = -1;
                if (conn.getResponseCode() == 200) {
                    // 获取文件大小
                    length = conn.getContentLength();
                }
                if (length <= 0) {
                    return;
                }
                // 在本地创建文件
                File file = new File(getDiskCacheDir(DownloadService.this, "download"), mFileInfo.getFileName());
                raf = new RandomAccessFile(file, "rwd");
                // 设置文件长度
                raf.setLength(length);
                mFileInfo.setLength(length);
                // 发送给服务
                mHandler.obtainMessage(MSG_INIT, mFileInfo).sendToTarget();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (raf != null) {
                        raf.close();
                    }
                    if (conn != null) {
                        conn.disconnect();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * 获取文件路径
     *
     * @param context
     * @param uniqueName
     * @return
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        File file = new File(cachePath + File.separator + uniqueName);
        if (!file.exists()) {
            file.mkdir();
        }
        return file;
    }
}
