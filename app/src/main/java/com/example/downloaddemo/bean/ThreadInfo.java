package com.example.downloaddemo.bean;

/**
 * @Author haoping
 * @Date 2016-10-13
 * @Des TODO
 */
public class ThreadInfo {
    private int id;//线程ID
    private String url;//线程所下载的文件URL
    private int start;//线程开始下载的位置
    private int end;//线程结束下载的位置
    private int finished;//线程已下载的大小

    public ThreadInfo() {
    }

    public ThreadInfo(int end, int finished, int id, int start, String url) {
        this.end = end;
        this.finished = finished;
        this.id = id;
        this.start = start;
        this.url = url;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getEnd() {
        return end;
    }

    public int getFinished() {
        return finished;
    }

    public int getId() {
        return id;
    }

    public int getStart() {
        return start;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "ThreadInfo{" +
                "end=" + end +
                ", id=" + id +
                ", url='" + url + '\'' +
                ", start=" + start +
                ", finished=" + finished +
                '}';
    }
}
