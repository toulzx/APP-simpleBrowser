package cn.njupt.assignment.tou.base;

import android.graphics.Bitmap;
import android.webkit.WebView;

// 网页窗口实体类
public class WebViewPage {

    // webview窗口
    private WebView mWebView;
    // bitmap截图
    private Bitmap mBitmap;

    public WebViewPage(WebView webView, Bitmap bitmap) {
        this.mWebView = webView;
        this.mBitmap = bitmap;
    }

    public WebView getWebView() {
        return mWebView;
    }

    public void setWebView(WebView mWebView) {
        this.mWebView = mWebView;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

}