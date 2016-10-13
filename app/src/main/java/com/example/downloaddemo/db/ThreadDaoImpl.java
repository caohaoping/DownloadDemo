package com.example.downloaddemo.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.downloaddemo.bean.ThreadInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author haoping
 * @Date 2016-10-13
 * @Des TODO
 */
public class ThreadDaoImpl implements ThreadDao {

    private final DBHelper mDbHelper;

    public ThreadDaoImpl(Context context) {
        mDbHelper = new DBHelper(context);
    }

    @Override
    public void insert(ThreadInfo threadInfo) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("insert into thread_info(thread_id, url, start, end, finished) values(?,?,?,?,?)", new Object[]{threadInfo.getId(), threadInfo.getUrl(), threadInfo.getStart(), threadInfo.getEnd(), threadInfo.getFinished()});

        }
        db.close();
    }

    @Override
    public void delete(String url, int thread_id) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("delete from thread_info where url = ? and thread_id = ?", new Object[]{url, thread_id});
        }
        db.close();
    }

    @Override
    public void update(String url, int thread_id, int finished) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("update thread_info set finished = ? where url = ? and thread_id = ?", new Object[]{finished, url, thread_id});
        }
        db.close();
    }

    @Override
    public List<ThreadInfo> getThreads(String url) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        List<ThreadInfo> threadInfos = null;
        if (db.isOpen()) {
            threadInfos = new ArrayList<>();
            Cursor cursor = db.rawQuery("select * from thread_info where url = ?", new String[]{url});
            while (cursor.moveToNext()) {
                ThreadInfo threadInfo = new ThreadInfo();
                String url2 = cursor.getString(cursor.getColumnIndex("url"));
                int thread_id = cursor.getInt(cursor.getColumnIndex("thread_id"));
                int start = cursor.getInt(cursor.getColumnIndex("start"));
                int end = cursor.getInt(cursor.getColumnIndex("end"));
                int finished = cursor.getInt(cursor.getColumnIndex("finished"));
                threadInfo.setUrl(url2);
                threadInfo.setId(thread_id);
                threadInfo.setStart(start);
                threadInfo.setEnd(end);
                threadInfo.setFinished(finished);
                threadInfos.add(threadInfo);
            }
            cursor.close();
        }
        db.close();
        return threadInfos;
    }

    @Override
    public boolean isExist(String url, int thread_id) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        boolean toNext = false;
        if (db.isOpen()) {
            System.out.println("url: " + url + ", thread_id: " + thread_id);
            Cursor cursor = db.rawQuery("select * from thread_info where url = ? and thread_id = ?", new String[]{url, thread_id + ""});
            toNext = cursor.moveToNext();
            cursor.close();
        }
        return toNext;
    }
}
