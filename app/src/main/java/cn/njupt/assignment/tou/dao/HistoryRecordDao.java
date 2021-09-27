package cn.njupt.assignment.tou.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import cn.njupt.assignment.tou.entity.HistoryRecord;

/**
 * @author: sherman
 * @date: 2021/9/21
 * @description: 操作历史记录数据表的dao层
 */
@Dao
public interface HistoryRecordDao {
    /**
     * @description 查询所有历史记录
     * @param
     * @return
     * @author sherman
     * @time 2021/9/19 21:14
     */
    @Query("SELECT * FROM historyrecord")
    LiveData<List<HistoryRecord>> getHistoryRecordAll();

    /**
     * 根据指定字段模糊查询，根据输入的搜索内容搜索网址的名称name和网址url
     *
     * @return
     */
    @Query("SELECT * FROM historyrecord WHERE hname = :input or hurl = :input")
    LiveData<List<HistoryRecord>> loadHistoryRecordByinput(String input);

    /**
     * 根据时间进行查询，查找出同一天的所有历史记录
     *
     * @return
     */
    @Query("SELECT * FROM historyrecord WHERE hdate = :dateString")
    LiveData<List<HistoryRecord>> loadHistoryRecordByDate(String dateString);

    /**
     * 模糊搜素
     * @param content 输入内容
     * @return 符合的历史记录
     */
    @Query("select * from historyrecord where hname like '%'||:content||'%' or hurl like '%'||:content||'%'")
    LiveData<List<HistoryRecord>> fuzzySearch(String content);

    /**
     * 模糊搜素
     * @param content 输入内容
     * @return 符合的历史记录
     */
    @Query("select * from historyrecord where hname like '%'||:content||'%' or hurl like '%'||:content||'%'")
    List<HistoryRecord> fuzzySearchToList(String content);

    @Query("select * from historyrecord")
    List<HistoryRecord> getAll();

    /**
     * 项数据库添加数据
     *
     * @param historyRecordList
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<HistoryRecord> historyRecordList);

    /**
     * 删除数据
     *
     * @param historyRecordBean
     */
    @Delete()
    void delete(HistoryRecord historyRecordBean);

    /**
     * 根据id删除指定记录
     * @param id
     */
    @Query("delete from historyrecord where id = :id")
    void deleteOne(int id);

    @Query("delete from historyrecord")
    void deleteAll();

    @Query("delete from historyrecord where hdate = :date")
    void deleteTodayHistory(String date);

    @Query("select id from historyrecord")
    List<Integer> getAllId();

}
