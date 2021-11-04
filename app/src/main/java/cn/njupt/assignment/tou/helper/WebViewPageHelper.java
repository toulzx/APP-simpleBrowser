package cn.njupt.assignment.tou.helper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.List;

import cn.njupt.assignment.tou.activity.HomeActivity;
import cn.njupt.assignment.tou.base.WebViewPage;
import cn.njupt.assignment.tou.utils.BitmapUtil;

/**
 *
 * 存放 WebView 和对应的 Bitmap
 * @date 2021/10/30 10:20
 * @author tou
 *
 */
public class WebViewPageHelper {

    private static final String TAG = WebViewPageHelper.class.getSimpleName();

    private static WebView currentWebView = null;
    private static int currentWebViewIndex = -1;
    public static Bundle currentBundle = null;
    private static List<WebViewPage> webViewPageList = new ArrayList<>();

    /**
     * 创建或更新 WebViewPage
     * @param webView:
     * @param bitmap:
     * @return void
     * @date 2021/10/31 14:18
     * @author tou
     */
    public static void updateCurrentPage(WebView webView, Bitmap bitmap, boolean isNewCreated) {
        if (WebViewPageHelper.currentWebViewIndex == -1  || isNewCreated) {
            WebViewPageHelper.setCurrentBundle(null);
            Log.i(TAG, "[abc]updateCurrentPage: create current webviewpage");
            WebViewPageHelper.currentWebView = webView;
            WebViewPageHelper.currentWebViewIndex ++;
            WebViewPageHelper.webViewPageList.add(new WebViewPage(WebViewPageHelper.currentWebView, bitmap));
        } else {
            WebViewPage wnew = new WebViewPage(webView, bitmap);
            WebViewPage wold = WebViewPageHelper.webViewPageList.set(WebViewPageHelper.currentWebViewIndex,
                    wnew);
            Log.i(TAG, "[abc]updateCurrentPage: set WebViewPage  old => "+ wold + " new => "+wnew);
            WebViewPageHelper.currentWebView = webView;
            WebViewPageHelper.currentWebView.saveState(WebViewPageHelper.currentBundle);
        }
    }

    public static void deletePage(int index) {

        WebViewPageHelper.webViewPageList.remove(index);
        if (index < WebViewPageHelper.currentWebViewIndex) {
            WebViewPageHelper.currentWebViewIndex--;
        }

    }




    /* Getter */
    public static List<WebViewPage> getWebViewPageList() {
        return webViewPageList;
    }

    public static int getCurrentWebViewIndex() {
        return currentWebViewIndex;
    }

    public static WebView getCurrentWebView() {
        return currentWebView;
    }

    public static void setCurrentWebView(WebView currentWebView, int index) {
        WebViewPageHelper.currentWebView = currentWebView;
        WebViewPageHelper.currentWebView.saveState(WebViewPageHelper.currentBundle);
        WebViewPageHelper.currentWebViewIndex = index;

    }

    public static void setCurrentBundle(Bundle currentBundle) {
        WebViewPageHelper.currentBundle = currentBundle;
    }

}
