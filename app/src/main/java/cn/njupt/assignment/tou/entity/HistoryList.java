package cn.njupt.assignment.tou.entity;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * @author: sherman
 * @date: 2021/10/8
 * @description: 可排序的历史记录类，按日期存放
 */
public class HistoryList implements Serializable,Comparable<HistoryList> {

    private int sort; // 将历史记录时间转化为可排序的整数
    private String time;// 历史记录时间
    private List<HistoryRecord> listOfDay = new LinkedList<>(); // 按天存放历史记录的列表
    public void setTimeAndSort(String time){
        this.time = time;
        //在定义时间时就定义了sort属性
        this.sort = Integer.parseInt(time.replace("-",""));
    }

    public void setTimeToday(String time){
        this.time = "今天 - "+time;
        this.sort = Integer.parseInt(time.replace("-",""));
    }

    public void setListOfDay(List<HistoryRecord> listOfDay){
        this.listOfDay = listOfDay;
    }

    public int getSort(){
        return this.sort;
    }

    public String getTime(){
        return this.time;
    }

    public List<HistoryRecord> getListOfDay(){
        return this.listOfDay;
    }

    @Override
    public int compareTo(HistoryList h) {
        return h.getSort() - this.sort;
    }

    @Override
    public String toString() {
        return "HistoryListBean{" +
                "sort=" + sort +
                ", time='" + time + '\'' +
                ", listOfDay=" + listOfDay +
                '}';
    }
}
