package cn.njupt.assignment.tou.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.Date;
import java.util.List;

import cn.njupt.assignment.tou.entity.HistoryRecord;
import cn.njupt.assignment.tou.repository.HistoryRecordRepository;

/**
 * @author: sherman
 * @date: 2021/9/21
 * @description: 历史记录的viewmodel类
 */
public class HistoryRecordViewModel extends AndroidViewModel {
    private final HistoryRecordRepository historyRecordRepository;

    public HistoryRecordViewModel(@NonNull Application application) {
        super(application);
        historyRecordRepository = new HistoryRecordRepository(application);
    }

    //获取所有历史记录
    public LiveData<List<HistoryRecord>> getHistoryRecordAll(){
        return historyRecordRepository.getHistoryRecordAll();
    }

    public LiveData<List<HistoryRecord>> getHistoryRecordByInput(String input){
        return historyRecordRepository.getHistoryByInput(input);
    }

    public LiveData<List<HistoryRecord>> getHistoryRecordByInput(Date hdate){
        return historyRecordRepository.getHistoryByTime(hdate);
    }

    public void insertHistoryRecord(String hname, String hurl, String hicon, Date hdate){
        historyRecordRepository.insertHistoryRecord(hname,hurl,hicon,hdate);
    }

    public void deleteHistoryRecord(HistoryRecord... historyRecordBeans){
        historyRecordRepository.deleteHistoryRecord(historyRecordBeans);
    }

    public void deleteOneHistoryRecord(int id){
        historyRecordRepository.deleteHistoryRecordById(id);
    }

    public void deleteAll(){
        historyRecordRepository.deleteAll();
    }

    public void deleteTodayHistory(String date){
        historyRecordRepository.deleteTodayHistory(date);
    }

    public List<Integer> getAllIdOfHistory(){
        return historyRecordRepository.getAllId();
    }

    public LiveData<List<HistoryRecord>> getFuzzySearchInfo(String content){
        return historyRecordRepository.getFuzzySearch(content);
    }

    public List<HistoryRecord> getFuzzySearchInfoToList(String content){
        return historyRecordRepository.getFuzzySearchToList(content);
    }

    public List<HistoryRecord> getAll(){
        return historyRecordRepository.getAll();
    }
}
