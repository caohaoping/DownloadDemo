package com.example.downloaddemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.downloaddemo.bean.FileInfo;
import com.example.downloaddemo.service.DownloadService;

/**
 * @Author haoping
 * @Date 2016-10-13
 * @Des TODO
 */
public class MainActivity extends AppCompatActivity {

    private TextView mName;
    private ProgressBar mProgressBar;
    private Button  mStart;
    private Button mStop;
    private FileInfo mFileInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
        initEvent();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        mName = (TextView) findViewById(R.id.name);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mStart = (Button) findViewById(R.id.start);
        mStop = (Button) findViewById(R.id.stop);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mFileInfo = new FileInfo("1.mp4", 0, 0, 0, "http://192.168.31.92:8080/1.mp4");
        mName.setText(mFileInfo.getFileName());
        mProgressBar.setMax(100);

        // 注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadService.ACTION_UPDATE);
        registerReceiver(mReceiver, filter);
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //开始下载，发送mFileInfo到服务
                Intent intent = new Intent(MainActivity.this, DownloadService.class);
                intent.setAction(DownloadService.ACTION_START);
                intent.putExtra("mFileInfo", mFileInfo);
                startService(intent);
            }
        });

        mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //暂停下载
                Intent intent = new Intent(MainActivity.this, DownloadService.class);
                intent.setAction(DownloadService.ACTION_STOP);
                intent.putExtra("mFileInfo", mFileInfo);
                startService(intent);
            }
        });
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(DownloadService.ACTION_UPDATE.equals(intent.getAction())){
                // 更新进度条
                int finished = intent.getIntExtra("finished", 0);
                System.out.println("finished：" + finished);
                mProgressBar.setProgress(finished);
            }
        }
    };

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}
