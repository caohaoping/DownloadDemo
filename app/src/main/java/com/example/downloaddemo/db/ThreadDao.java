package com.example.downloaddemo.db;

import com.example.downloaddemo.bean.ThreadInfo;

import java.util.List;

/**
 * @Author haoping
 * @Date 2016-10-13
 * @Des TODO
 */
public interface ThreadDao {
    /**
     * 插入线程信息
     * @param threadInfo
     */
    void insert(ThreadInfo threadInfo);

    /**
     * 删除线程
     * @param url
     * @param thread_id
     */
    void delete(String url, int thread_id);

    /**
     * 更新线程完成进度
     * @param url
     * @param thread_id
     * @param finished
     */
    void update(String url, int thread_id, int finished);

    /**
     * 查询下载的线程
     * @param url
     */
    List<ThreadInfo> getThreads(String url);

    /**
     * 查询线程是否存在
     * @param url
     * @param thread_id
     * @return
     */
    boolean isExist(String url, int thread_id);
}
