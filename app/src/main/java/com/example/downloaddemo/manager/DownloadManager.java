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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author haoping
 * @Date 2016-10-13
 * @Des TODO
 */
public class DownloadManager {

    public static final int STATE_UNDOWNLOAD = 0;// 未下载
    public static final int STATE_DOWNLOADING = 1;// 下载中
    public static final int STATE_PAUSEDDOWNLOAD = 2;// 暂停下载
    public static final int STATE_WAITINGDOWNLOAD = 3;// 等待下载
    public static final int STATE_DOWNLOADFAILED = 4;// 下载失败
    public static final int STATE_DOWNLOADED = 5;// 下载完成

    Map<Integer, FileInfo> downloadFileInfos = new LinkedHashMap<>();

    private ThreadDaoImpl mThreadDaoInstance;
    private Context context;
    private static DownloadManager downloadInstance;
    private ExecutorService mExecutor;

    private DownloadManager(Context context) {
        this.context = context;
        mThreadDaoInstance = new ThreadDaoImpl(context);
        mExecutor = Executors.newFixedThreadPool(6);

    }

    public static DownloadManager getInstance(Context context){
        if(downloadInstance==null){
            synchronized (DownloadManager.class){
                if(downloadInstance == null){
                    downloadInstance = new DownloadManager(context);
                }
            }
        }
        return downloadInstance;
    }

    /**
     * 多线程下载
     *
     * @param fileInfo
     */
    public void multiThreadDownload(FileInfo fileInfo, int count) {
        downloadFileInfos.put(fileInfo.getId(), fileInfo);
        fileInfo.setState(STATE_WAITINGDOWNLOAD);
        //判断数据库中线程是否存在
        List<ThreadInfo> threads = mThreadDaoInstance.getThreads(fileInfo.getUrl());
        fileInfo.setThreadCount(count);
        int blockSize = fileInfo.getLength() / count;
        if (threads.size() == 0) {//如果数据库中线程信息不存在
            for (int i = 0; i < count; i++) {
                ThreadInfo threadInfo = new ThreadInfo();
                threadInfo.setId(i);
                threadInfo.setUrl(fileInfo.getUrl());
                int start = blockSize * i;
                int end;
                if (i == count - 1) {
                    end = fileInfo.getLength();
                } else {
                    end = blockSize * (i + 1) - 1;
                }
                threadInfo.setStart(start);
                threadInfo.setEnd(end);
                //将线程信息插入数据库
                mThreadDaoInstance.insert(threadInfo);
                //new DownloadThread(fileInfo, threadInfo).start();
                mExecutor.execute(new DownloadThread(fileInfo, threadInfo));
            }

        } else {//如果数据库中保存了上次的线程信息
            int mFinished = 0;
            for (ThreadInfo threadInfo : threads) {
                //累加文件已下载进度
                mFinished += threadInfo.getFinished();
            }
            fileInfo.setFinished(mFinished);
            for (ThreadInfo threadInfo : threads) {
                //new DownloadThread(fileInfo, threadInfo).start();
                mExecutor.execute(new DownloadThread(fileInfo, threadInfo));
            }
        }
    }

    /**
     * 单线程下载
     *
     * @param fileInfo
     */
    public void download(FileInfo fileInfo) {
        // 查询数据库是否存在线程信息
        List<ThreadInfo> threadInfos = mThreadDaoInstance.getThreads(fileInfo.getUrl());
        ThreadInfo threadInfo;
        if (threadInfos.size() == 0) {//如果没有线程
            threadInfo = new ThreadInfo(fileInfo.getLength(), fileInfo.getFinished(), 0, 0, fileInfo.getUrl());
        } else {
            threadInfo = threadInfos.get(0);//这里是单线程下载
        }
        // 创建子线程今夕下载
        new DownloadThread(fileInfo, threadInfo).start();
    }

    /**
     * 线程下载
     */
    class DownloadThread extends Thread {

        private FileInfo fileInfo;
        private ThreadInfo threadInfo;

        public DownloadThread(FileInfo fileInfo, ThreadInfo threadInfo) {
            this.fileInfo = fileInfo;
            this.threadInfo = threadInfo;
        }

        @Override
        public void run() {
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
                    fileInfo.setState(STATE_DOWNLOADING);
                    // 创建广播意图
                    Intent intent = new Intent(DownloadService.ACTION_UPDATE);
                    // 创建本地文件
                    File file = new File(DownloadService.getDiskCacheDir(context, "download"), fileInfo.getFileName());
                    raf = new RandomAccessFile(file, "rwd");
                    raf.seek(start);
                    // 写入本地文件
                    is = conn.getInputStream();
                    int length;
                    byte[] buffer = new byte[1024 * 4];
                    while (-1 != (length = is.read(buffer))) {
                        raf.write(buffer, 0, length);
                        // 文件累加完成进度
                        fileInfo.setFinished(fileInfo.getFinished() + length);
                        // 该线程下载完成进度
                        threadInfo.setFinished(threadInfo.getFinished() + length);
                        // 使用广播发送进度到Activity
                        intent.putExtra("fileInfo", fileInfo);
                        context.sendBroadcast(intent);
                        if (STATE_PAUSEDDOWNLOAD == fileInfo.getState()) {
                            //保存该线程下载信息
                            mThreadDaoInstance.update(threadInfo.getUrl(), threadInfo.getId(), threadInfo.getFinished());
                            return;
                        }
                    }
                    int threadCount = fileInfo.getThreadCount();
                    fileInfo.setThreadCount(--threadCount);
                }
            } catch (Exception e) {
                fileInfo.setState(STATE_DOWNLOADFAILED);
                //保存该线程下载信息
                mThreadDaoInstance.update(threadInfo.getUrl(), threadInfo.getId(), threadInfo.getFinished());
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
                    if (0 == fileInfo.getThreadCount()) {
                        //所有线程下载完成后再删除数据库线程信息
                        fileInfo.setState(STATE_DOWNLOADED);
                        mThreadDaoInstance.delete(threadInfo.getUrl());
                        downloadFileInfos.remove(fileInfo.getId());
                    }else {
                        System.out.println("下载未完成，删除线程信息失败");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }
    }

    /**
     * 暂停下载
     */
    public void pauseDownload(int id){
        FileInfo fileInfo = downloadFileInfos.get(id);
        if(fileInfo != null){
            fileInfo.setState(STATE_PAUSEDDOWNLOAD);
        }
    }
}
