package cn.njupt.assignment.tou.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author: sherman
 * @date: 2021/10/13
 * @description: 操作文件的工具类
 */
public class FileUtil {

    public static void saveFile(String filePath,String data){
        File file = new File(filePath);
        Log.i("fafa", "saveFile: "+filePath);
        try {
            if (!file.exists()){
                file.createNewFile();
            }
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(data.getBytes(StandardCharsets.UTF_8));
            outputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static File getFileFromUrl(String url){
        return new File("");
    }
}
