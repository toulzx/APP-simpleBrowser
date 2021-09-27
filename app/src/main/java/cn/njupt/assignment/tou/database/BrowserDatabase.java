package cn.njupt.assignment.tou.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import cn.njupt.assignment.tou.dao.HistoryRecordDao;
import cn.njupt.assignment.tou.entity.HistoryRecord;

/**
 * @author: sherman
 * @date: 2021/9/21
 * @description: 数据库类
 */
@Database(entities = {HistoryRecord.class},version = 1,exportSchema = false)
public abstract class BrowserDatabase extends RoomDatabase {
    private static BrowserDatabase INSTANCE;

    public static synchronized BrowserDatabase getDatabase(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),BrowserDatabase.class,"browser.db")
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }

    public abstract HistoryRecordDao getHistoryRecordDao();
}
