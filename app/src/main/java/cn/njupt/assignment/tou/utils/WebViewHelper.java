package cn.njupt.assignment.tou.utils;

import android.os.Bundle;
import android.webkit.WebView;


import java.util.ArrayList;
import java.util.List;

/**
 * @author: sherman
 * @date: 2021/10/11
 * @description:
 */
public class WebViewHelper {
    public static Bundle currentBundle = null;
    public static WebView headWebView = null;
    public static WebView currentWebView= null;
    public static boolean isExist = false;
    public static List<WebViewFragment> webList = new ArrayList<>();
}
