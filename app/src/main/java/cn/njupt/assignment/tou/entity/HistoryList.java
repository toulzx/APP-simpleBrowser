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

    private int sort;
    private String time;
    private List<HistoryRecord> listOfDay = new LinkedList<>();
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
