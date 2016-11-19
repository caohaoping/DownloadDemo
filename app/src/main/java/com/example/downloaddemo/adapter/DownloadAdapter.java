package com.example.downloaddemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.downloaddemo.R;
import com.example.downloaddemo.bean.FileInfo;
import com.example.downloaddemo.service.DownloadService;

import java.util.List;

/**
 * @Author haoping
 * @Date 2016-11-09
 * @Des TODO
 */
public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.MyHolder> {

    private Context mContext;
    public List<FileInfo> mFileInfos;
    public MyHolder myHolder;

    public DownloadAdapter(Context context, List<FileInfo> fileInfos) {
        this.mContext = context;
        this.mFileInfos = fileInfos;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        myHolder = new MyHolder(View.inflate(mContext, R.layout.list_item, null));
        return myHolder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        final FileInfo fileInfo = mFileInfos.get(position);
        fileInfo.setItemPosition(position);
        holder.fileNameTV.setText(fileInfo.getFileName());
        holder.mProgressBar.setMax(100);

        holder.mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //开始下载，发送mFileInfo到服务
                Intent intent = new Intent(mContext, DownloadService.class);
                intent.setAction(DownloadService.ACTION_START);
                intent.putExtra("mFileInfo", fileInfo);
                mContext.startService(intent);
            }
        });

        holder.mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //暂停下载
                Intent intent = new Intent(mContext, DownloadService.class);
                intent.setAction(DownloadService.ACTION_STOP);
                intent.putExtra("fileId", fileInfo.getId());
                mContext.startService(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        if (mFileInfos != null) {
            return mFileInfos.size();
        }
        return 0;
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public TextView progressTV;
        public TextView fileNameTV;
        public ProgressBar mProgressBar;
        public Button mStart;
        public Button mStop;

        public MyHolder(View itemView) {
            super(itemView);
            fileNameTV = (TextView) itemView.findViewById(R.id.name);
            progressTV = (TextView) itemView.findViewById(R.id.progress);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.progressbar);
            mStart = (Button) itemView.findViewById(R.id.start);
            mStop = (Button) itemView.findViewById(R.id.stop);
        }

    }
}