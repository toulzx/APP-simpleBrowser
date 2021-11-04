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

    //根据用户输入获取历史记录
    public LiveData<List<HistoryRecord>> getHistoryRecordByInput(String input){
        return historyRecordRepository.getHistoryByInput(input);
    }

    // 根据日期获取历史记录
    public LiveData<List<HistoryRecord>> getHistoryRecordByInput(Date hdate){
        return historyRecordRepository.getHistoryByTime(hdate);
    }

    //插入历史记录
    public void insertHistoryRecord(String hname, String hurl, String hicon, Date hdate){
        historyRecordRepository.insertHistoryRecord(hname,hurl,hicon,hdate);
    }

    //删除多条历史记录
    public void deleteHistoryRecord(HistoryRecord... historyRecordBeans){
        historyRecordRepository.deleteHistoryRecord(historyRecordBeans);
    }

    //删除一条历史记录
    public void deleteOneHistoryRecord(int id){
        historyRecordRepository.deleteHistoryRecordById(id);
    }

    //删除所有历史记录
    public void deleteAll(){
        historyRecordRepository.deleteAll();
    }

    //根据日期删除历史记录
    public void deleteTodayHistory(String date){
        historyRecordRepository.deleteTodayHistory(date);
    }

    //获取所有历史记录的id
    public List<Integer> getAllIdOfHistory(){
        return historyRecordRepository.getAllId();
    }

    //根据输入内容对历史记录进行模糊搜索
    public LiveData<List<HistoryRecord>> getFuzzySearchInfo(String content){
        return historyRecordRepository.getFuzzySearch(content);
    }

    //根据输入内容对历史记录进行模糊搜索
    public List<HistoryRecord> getFuzzySearchInfoToList(String content){
        return historyRecordRepository.getFuzzySearchToList(content);
    }

    //获取所有历史记录
    public List<HistoryRecord> getAll(){
        return historyRecordRepository.getAll();
    }
}
