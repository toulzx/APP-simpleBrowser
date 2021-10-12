package cn.njupt.assignment.tou.utils;

import android.graphics.Bitmap;
import android.webkit.WebView;

/**
 * @author: sherman
 * @date: 2021/10/11
 * @description:
 */
public class WebViewFragment {
    private WebView webView;
    private Bitmap wBitmap;


    public WebViewFragment(WebView webView, Bitmap wBitmap) {
        this.webView = webView;
        this.wBitmap = wBitmap;
    }



    public WebView getWebView() {
        return webView;
    }

    public void setWebView(WebView webView) {
        this.webView = webView;
    }

    public Bitmap getwBitmap() {
        return wBitmap;
    }

    public void setwBitmap(Bitmap wBitmap) {
        this.wBitmap = wBitmap;
    }

}
