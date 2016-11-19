package com.example.downloaddemo.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.downloaddemo.manager.DownloadManager;

/**
 * @Author haoping
 * @Date 2016-10-13
 * @Des TODO
 */
public class FileInfo implements Parcelable {
    private int id;//文件ID
    private String url;//文件URL
    private String fileName;//文件名
    private int length;//文件大小
    private int finished;//文件已下载的大小
    private int state = DownloadManager.STATE_UNDOWNLOAD;//文件下载状态
    private int threadCount = 3;//文件下载线程个数，默认为3个线程
    private int itemPosition;//文件在RecyclerView中的位置

    public FileInfo() {

    }

    public FileInfo(int id, String fileName, String url) {
        this.id = id;
        this.fileName = fileName;
        this.url = url;
    }


    public int getItemPosition() {
        return id;
    }

    public void setItemPosition(int itemPosition) {
        this.itemPosition = itemPosition;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "fileName='" + fileName + '\'' +
                ", id=" + id +
                ", url='" + url + '\'' +
                ", length=" + length +
                ", finished=" + finished +
                ", state=" + state +
                ", threadCount=" + threadCount +
                ", itemPosition=" + itemPosition +
                '}';
    }

    protected FileInfo(Parcel in) {
        id = in.readInt();
        url = in.readString();
        fileName = in.readString();
        length = in.readInt();
        finished = in.readInt();
        state = in.readInt();
        threadCount = in.readInt();
        itemPosition = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(url);
        dest.writeString(fileName);
        dest.writeInt(length);
        dest.writeInt(finished);
        dest.writeInt(state);
        dest.writeInt(threadCount);
        dest.writeInt(itemPosition);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FileInfo> CREATOR = new Creator<FileInfo>() {
        @Override
        public FileInfo createFromParcel(Parcel in) {
            return new FileInfo(in);
        }

        @Override
        public FileInfo[] newArray(int size) {
            return new FileInfo[size];
        }
    };
}
