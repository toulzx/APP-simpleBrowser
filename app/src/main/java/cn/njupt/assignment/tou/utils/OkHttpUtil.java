package cn.njupt.assignment.tou.utils;

import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 *
 * @author: sherman
 * @date: 2021/10/13
 * @description: okhttp工具类
 */
public class OkHttpUtil {
    final static String TAG = "OkHttpUtils";

    public static String OkGetArt(String url) {
        String html = null;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try  {
            Response response = client.newCall(request).execute();
            //return
            html = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "OkGetArt: html "+html);
        return html;
    }
}
