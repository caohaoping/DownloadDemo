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
public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.MyHolder> {

    private Context context;
    private List<FileInfo> fileInfos;

    public MyListAdapter(Context context, List<FileInfo> fileInfos){
        this.context = context;
        this.fileInfos = fileInfos;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(View.inflate(context, R.layout.list_item, null));
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.setDataAndRefreshUI(position);
    }

    @Override
    public int getItemCount() {
        if(fileInfos != null){
            return fileInfos.size();
        }
        return 0;
    }

    class MyHolder extends RecyclerView.ViewHolder{

        private TextView mName;
        private ProgressBar mProgressBar;
        private TextView mProgressTv;
        private Button mStart;
        private Button mStop;

        public MyHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.name);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.progressbar);
            mProgressTv = (TextView) itemView.findViewById(R.id.progress);
            mStart = (Button) itemView.findViewById(R.id.start);
            mStop = (Button) itemView.findViewById(R.id.stop);
        }

        public void setDataAndRefreshUI(int position){
            final FileInfo fileInfo = fileInfos.get(position);
            mName.setText(fileInfo.getFileName());
            mProgressBar.setMax(100);
            mProgressBar.setProgress(fileInfo.getFinished());
            mProgressTv.setText(String.valueOf(fileInfo.getFinished()));
            mStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //开始下载，发送mFileInfo到服务
                    Intent intent = new Intent(context, DownloadService.class);
                    intent.setAction(DownloadService.ACTION_START);
                    intent.putExtra("mFileInfo", fileInfo);
                    context.startService(intent);
                }
            });

            mStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //暂停下载
                    Intent intent = new Intent(context, DownloadService.class);
                    intent.setAction(DownloadService.ACTION_STOP);
                    intent.putExtra("mFileInfo", fileInfo);
                    context.startService(intent);
                }
            });
        }
    }
}
