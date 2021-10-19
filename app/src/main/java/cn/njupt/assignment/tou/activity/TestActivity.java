package cn.njupt.assignment.tou.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import cn.njupt.assignment.tou.R;
import cn.njupt.assignment.tou.utils.FileUtil;
import cn.njupt.assignment.tou.utils.OkHttpUtil;

public class TestActivity extends AppCompatActivity {

    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        webView = findViewById(R.id.test_webview);
//        webView.loadUrl("http://www.baidu.com");
        webView.loadUrl("file:////storage/emulated/0/Download/-9.mht");
        //系统默认会通过手机浏览器打开网页，为了能够直接通过WebView显示网页，则必须设置
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
    }
}