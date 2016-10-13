package com.example.downloaddemo.manager;

import android.content.Context;
import android.content.Intent;

import com.example.downloaddemo.bean.FileInfo;
import com.example.downloaddemo.bean.ThreadInfo;
import com.example.downloaddemo.db.ThreadDaoImpl;
import com.example.downloaddemo.service.DownloadService;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * @Author haoping
 * @Date 2016-10-13
 * @Des TODO
 */
public class DownloadManager {

    private final ThreadDaoImpl mThreadDao;
    private final Context context;
    private FileInfo fileInfo;
    public boolean isPause;

    public DownloadManager(Context context) {
        this.context = context;
        mThreadDao = new ThreadDaoImpl(context);
    }

    /**
     * 下载
     *
     * @param fileInfo
     */
    public void download(FileInfo fileInfo) {
        this.fileInfo = fileInfo;
        // 查询数据库是否存在线程信息
        List<ThreadInfo> threadInfos = mThreadDao.getThreads(fileInfo.getUrl());
        ThreadInfo threadInfo;
        if (threadInfos.size() == 0) {//如果没有线程
            threadInfo = new ThreadInfo(fileInfo.getLength(), fileInfo.getFinished(), 0, 0, fileInfo.getUrl());
        } else {
            threadInfo = threadInfos.get(0);//这里是单线程下载
            System.out.println("threadInfo: " + threadInfo.toString());
        }
        // 创建子线程今夕下载
        new DownloadThread(threadInfo).start();
    }

    /**
     * 线程下载
     */
    class DownloadThread extends Thread {

        private final ThreadInfo threadInfo;

        public DownloadThread(ThreadInfo threadInfo) {
            this.threadInfo = threadInfo;
        }

        @Override
        public void run() {
            // 如果保存该线程的信息在数据库不存在
            if (!mThreadDao.isExist(threadInfo.getUrl(), threadInfo.getId())) {
                //把线程信息保存到数据库
                mThreadDao.insert(threadInfo);
            }
            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            InputStream is = null;
            try {
                // 获取网络资源
                URL url = new URL(threadInfo.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                int start = threadInfo.getStart() + threadInfo.getFinished();
                // 设置下载范围bytes=
                conn.setRequestProperty("Range", "bytes=" + start + "-" + threadInfo.getEnd());
                if (conn.getResponseCode() == 206) {
                    // 创建本地文件
                    File file = new File(DownloadService.getDiskCacheDir(context, "download"), fileInfo.getFileName());
                    raf = new RandomAccessFile(file, "rwd");
                    raf.seek(start);
                    int finished = threadInfo.getFinished();
                    // 创建广播意图
                    Intent intent = new Intent(DownloadService.ACTION_UPDATE);
                    // 写入本地文件
                    is = conn.getInputStream();
                    int length = -1;
                    byte[] buffer = new byte[1024 * 4];
                    while (-1 != (length = is.read(buffer))) {
                        raf.write(buffer, 0, length);
                        finished += length;
                        // 使用广播发送进度到Activity
                        intent.putExtra("finished", finished * 100 / fileInfo.getLength());
                        context.sendBroadcast(intent);
                        if (isPause) {
                            // 更新进度到数据库
                            mThreadDao.update(threadInfo.getUrl(), threadInfo.getId(), finished);
                            return;
                        }
                    }
                    // 下载完成后删除线程
                    mThreadDao.delete(threadInfo.getUrl(), threadInfo.getId());

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
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
}
