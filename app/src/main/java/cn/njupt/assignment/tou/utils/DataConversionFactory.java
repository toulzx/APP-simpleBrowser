package cn.njupt.assignment.tou.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: sherman
 * @date: 2021/9/21
 * @description: 日期转换工具类
 */
public class DataConversionFactory {
    public static String fromDateToString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(date);
        return date == null ? null : dateString;
    }
}
