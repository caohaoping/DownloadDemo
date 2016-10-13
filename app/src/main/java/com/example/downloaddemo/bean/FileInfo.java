package com.example.downloaddemo.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @Author haoping
 * @Date 2016-10-13
 * @Des TODO
 */
public class FileInfo implements Parcelable{
    private int id;//文件ID
    private String url;//文件URL
    private String fileName;//文件名
    private int length;//文件大小
    private int finished;//文件已下载的大小

    public FileInfo() {

    }

    public FileInfo(String fileName, int finished, int id, int length, String url) {
        this.fileName = fileName;
        this.finished = finished;
        this.id = id;
        this.length = length;
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public int getFinished() {
        return finished;
    }

    public int getId() {
        return id;
    }

    public int getLength() {
        return length;
    }

    public String getUrl() {
        return url;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLength(int length) {
        this.length = length;
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
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.url);
        dest.writeString(this.fileName);
        dest.writeInt(this.length);
        dest.writeInt(this.finished);
    }

    protected FileInfo(Parcel in) {
        this.id = in.readInt();
        this.url = in.readString();
        this.fileName = in.readString();
        this.length = in.readInt();
        this.finished = in.readInt();
    }

    public static final Creator<FileInfo> CREATOR = new Creator<FileInfo>() {
        @Override
        public FileInfo createFromParcel(Parcel source) {
            return new FileInfo(source);
        }

        @Override
        public FileInfo[] newArray(int size) {
            return new FileInfo[size];
        }
    };
}
