package cn.njupt.assignment.tou.repository;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cn.njupt.assignment.tou.dao.HistoryRecordDao;
import cn.njupt.assignment.tou.database.BrowserDatabase;
import cn.njupt.assignment.tou.entity.HistoryRecord;
import cn.njupt.assignment.tou.utils.DataConversionFactory;

/**
 * @author: sherman
 * @date: 2021/9/21
 * @description: 历史记录的repository类
 */

public class HistoryRecordRepository {
    private LiveData<List<HistoryRecord>> historyRecordAll;
    private HistoryRecordDao historyRecordDao;
    private LiveData<List<HistoryRecord>> historyByInput;
    private LiveData<List<HistoryRecord>> historyByTime;

    public HistoryRecordRepository(Context context){
        BrowserDatabase browserDatabase = BrowserDatabase.getDatabase(context.getApplicationContext());
        historyRecordDao = browserDatabase.getHistoryRecordDao();
        //查询所有bookmark
        historyRecordAll = historyRecordDao.getHistoryRecordAll();
    }

    public LiveData<List<HistoryRecord>> getHistoryRecordAll() {
        return historyRecordAll;
    }

    public LiveData<List<HistoryRecord>> getHistoryByInput(String input) {
        historyByInput = historyRecordDao.loadHistoryRecordByinput(input);
        return historyByInput;
    }

    public LiveData<List<HistoryRecord>> getHistoryByTime(Date hdate) {
        String dateString = DataConversionFactory.fromDateToString(hdate);
        historyByTime = historyRecordDao.loadHistoryRecordByDate(dateString);
        return historyByTime;
    }

    public List<HistoryRecord> getAll(){
        return historyRecordDao.getAll();
    }

    //增加history(单条)，输入数据为new HistoryRecord(hname, hurl, hicon, hdate)
    public void insertHistoryRecord(String hname, String hurl, String hicon, Date hdate) {
        String dateString = DataConversionFactory.fromDateToString(hdate);
        new InsertAsyncTask(historyRecordDao).execute(new HistoryRecord(hname, hurl, hicon, dateString));
    }

    //删除history(单条)
    public void deleteHistoryRecord(HistoryRecord... historyRecordBeans){
        new DeleteAsyncTask(historyRecordDao).execute(historyRecordBeans);
    }

    //删除所有的记录
    public void deleteAll(){
        new DeleteAsyncTask(historyRecordDao).deleteAll();
    }

    //删除今天的记录
    public void deleteTodayHistory(String date){
        new DeleteAsyncTask(historyRecordDao).deleteTodayHistory(date);
    }

    public void deleteHistoryRecordById(int id){
        new DeleteAsyncTask(historyRecordDao).deleteOneHistoryRecord(id);
    }

    public List<HistoryRecord> getFuzzySearchToList(String content){
        return historyRecordDao.fuzzySearchToList(content);
    }

    public LiveData<List<HistoryRecord>> getFuzzySearch(String content){
        return new InsertAsyncTask(historyRecordDao).fuzzySearch(content);
    }

    public List<Integer> getAllId(){
        return historyRecordDao.getAllId();
    };

    static class InsertAsyncTask extends AsyncTask<HistoryRecord,Void,Void> {
        private HistoryRecordDao historyRecordDao;

        public InsertAsyncTask(HistoryRecordDao historyRecordDao) {
            this.historyRecordDao = historyRecordDao;
        }
        @Override
        protected Void doInBackground(HistoryRecord... historyRecordBeans) {
            List<HistoryRecord> historyRecordBeans1 = new ArrayList<>(Arrays.asList(historyRecordBeans));

            historyRecordDao.insertAll(historyRecordBeans1);
            return null;
        }

        public LiveData<List<HistoryRecord>> fuzzySearch(String content){
            return historyRecordDao.fuzzySearch(content);
        }
    }

    static class DeleteAsyncTask extends AsyncTask<HistoryRecord,Void,Void> {
        private HistoryRecordDao historyRecordDao;

        public DeleteAsyncTask(HistoryRecordDao historyRecordDao) {
            this.historyRecordDao = historyRecordDao;
        }
        @Override
        protected Void doInBackground(HistoryRecord... historyRecordBeans) {
            HistoryRecord bean = historyRecordBeans[0];
            historyRecordDao.delete(bean);
            return null;
        }

        public void deleteOneHistoryRecord(int id){
            this.historyRecordDao.deleteOne(id);
        }

        public void deleteAll(){
            this.historyRecordDao.deleteAll();
        }

        public void deleteTodayHistory(String date){
            this.historyRecordDao.deleteTodayHistory(date);
        }

    }
}
