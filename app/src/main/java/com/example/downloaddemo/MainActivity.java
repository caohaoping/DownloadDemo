package com.example.downloaddemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.downloaddemo.adapter.DownloadAdapter;
import com.example.downloaddemo.bean.FileInfo;
import com.example.downloaddemo.service.DownloadService;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author haoping
 * @Date 2016-10-13
 * @Des TODO
 */
public class MainActivity extends AppCompatActivity {

    private static final int UPDATE = 1;
    private FileInfo mFileInfo;
    private RecyclerView mRecyclerView;
    private List<FileInfo> mFileInfos = new ArrayList<>();
    private DownloadAdapter mAdapter;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(UPDATE == msg.what){
                FileInfo fileInfo = (FileInfo) msg.obj;
                if(isCurrentListViewItemVisible(fileInfo.getItemPosition())){
                    DownloadAdapter.MyHolder holder = (DownloadAdapter.MyHolder) mRecyclerView.findViewHolderForLayoutPosition(fileInfo.getItemPosition());
                    int finished = (int) ((fileInfo.getFinished() * 100 / fileInfo.getLength() + .5f));
                    holder.progressTV.setText(finished + "%");
                    holder.mProgressBar.setProgress(finished);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        regFilter();

        //String[] urls = new String[]{"http://10.0.2.2:8080/1.mp4", "http://10.0.2.2:8080/2.mp4", "http://10.0.2.2:8080/3.mp4", "http://10.0.2.2:8080/4.mp4", "http://10.0.2.2:8080/5.mp4","http://10.0.2.2:8080/6.mp4", "http://10.0.2.2:8080/7.mp4", "http://10.0.2.2:8080/8.mp4", "http://10.0.2.2:8080/9.mp4", "http://10.0.2.2:8080/10.mp4"};
        String[] urls = {
                "http://s1.music.126.net/download/android/CloudMusic_2.8.1_official_4.apk",
                "http://dl.m.cc.youku.com/android/phone/Youku_Phone_youkuweb.apk",
                "http://dldir1.qq.com/qqmi/TencentVideo_V4.1.0.8897_51.apk",
                "http://wap3.ucweb.com/files/UCBrowser/zh-cn/999/UCBrowser_V10.6.0.620_android_pf145_(Build150721222435).apk",
                "http://msoftdl.360.cn/mobilesafe/shouji360/360safesis/360MobileSafe_6.2.3.1060.apk",
                "http://www.51job.com/client/51job_51JOB_1_AND2.9.3.apk",
                "http://upgrade.m.tv.sohu.com/channels/hdv/5.0.0/SohuTV_5.0.0_47_201506112011.apk",
                "http://dldir1.qq.com/qqcontacts/100001_phonebook_4.0.0_3148.apk",
                "http://download.alicdn.com/wireless/taobao4android/latest/702757.apk",
                "http://apps.wandoujia.com/apps/com.jm.android.jumei/download",
                "http://download.3g.fang.com/soufun_android_30001_7.9.0.apk"
        };

        for (int i = 0; i < urls.length; i++) {
            mFileInfo = new FileInfo(i, getFileName(urls[i]), urls[i]);
            mFileInfos.add(mFileInfo);
        }
        LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAnimation(null);
        mRecyclerView.setItemAnimator(null);
        mAdapter = new DownloadAdapter(MainActivity.this, mFileInfos);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void regFilter() {
        // 注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadService.ACTION_UPDATE);
        registerReceiver(mReceiver, filter);
    }

    /**
     * 更新进度条的广播
     */
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DownloadService.ACTION_UPDATE.equals(intent.getAction())) {
                // 更新进度条
                FileInfo fileInfo = intent.getParcelableExtra("fileInfo");
                mHandler.obtainMessage(UPDATE, fileInfo).sendToTarget();
            }
        }
    };

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private String getFileName(String path) {
        int start = path.lastIndexOf("/") + 1;
        return path.substring(start);
    }

    /**
     * 判断当前item是否可见
     * @param position
     * @return
     */
    private boolean isCurrentListViewItemVisible(int position) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        int first = layoutManager.findFirstVisibleItemPosition();
        int last = layoutManager.findLastVisibleItemPosition();
        return first <= position && position <= last;
    }
}
