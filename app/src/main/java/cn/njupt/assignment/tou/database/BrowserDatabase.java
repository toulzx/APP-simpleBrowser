package cn.njupt.assignment.tou.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import cn.njupt.assignment.tou.dao.BookmarkDao;
import cn.njupt.assignment.tou.dao.HistoryRecordDao;
import cn.njupt.assignment.tou.entity.HistoryRecord;
import cn.njupt.assignment.tou.entity.Bookmark;

/**
 * @author: sherman
 * @date: 2021/9/21
 * @description: 数据库类
 */
@Database(entities = {HistoryRecord.class,Bookmark.class},version = 2,exportSchema = false)

public abstract class BrowserDatabase extends RoomDatabase {
    private static BrowserDatabase INSTANCE;

    public static synchronized BrowserDatabase getDatabase(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    BrowserDatabase.class,"browser.db")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

    public abstract HistoryRecordDao getHistoryRecordDao();

    public abstract BookmarkDao getBookmarkDao();
}
