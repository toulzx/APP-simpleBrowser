package cn.njupt.assignment.tou.base;

import android.graphics.Bitmap;
import android.webkit.WebView;

public class WebViewPage {

    private WebView mWebView;
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