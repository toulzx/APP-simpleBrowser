package cn.njupt.assignment.tou.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cn.njupt.assignment.tou.R;
import cn.njupt.assignment.tou.base.sGestureListener;
import cn.njupt.assignment.tou.fragment.footerFragment;
import cn.njupt.assignment.tou.fragment.headerFragment;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = HomeActivity.class.getSimpleName();

    private WebView mWebView;
    private EditText mSearch;
    private ProgressBar mProgressBar;
    private ImageView mResize, mReload, mBack, mForward, mOptions, mRecords, mPages;

    private footerFragment mFooter;
    private headerFragment mHeader;

    FragmentManager mFragmentManager = getSupportFragmentManager();

    private Context mContext;
    private GestureDetector mDetector;
    private InputMethodManager inputMethodManager;

    private long exitTime = 0;
    private static final int PRESS_BACK_EXIT_GAP = 2000;

    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";

    private static final int SCREEN_ORIENTATION_LANDSCAPE = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    private static final int SCREEN_ORIENTATION_PORTRAIT = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    private static final int SCREEN_ORIENTATION_UNSPECIFIED = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mContext = this;
        mDetector = new GestureDetector(this, onWebViewGestureListener);
        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        initUI();

        initWebView();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onNightModeChanged(int mode) {
        super.onNightModeChanged(mode);
    }


    /**
     * 用来监听 WebView 的手势
     * 实例化类
     * @date 2021/9/22 2:06
     * @author tou
     */
    private final sGestureListener onWebViewGestureListener = new sGestureListener() {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float x = e2.getX() - e1.getX();
            float y = e2.getY() - e1.getY();
            judgeSwiperDirection(x, y);
            return true;
        }
    };


    /**
     * 系统返回键 绑定 webView 后退操作
     *
     * @return void
     * @date 2021/9/14 17:27
     * @author tou
     */
    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            if ((System.currentTimeMillis() - exitTime) > PRESS_BACK_EXIT_GAP) {
                // 连点两次退出程序
                Toast.makeText(mContext, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                super.onBackPressed();
            }

        }
    }



    /*---------------------------------------------------1-----------------------------------------------------------*/

    /**
     * 初始化控件：绑定与监听
     * @return void
     * @date 2021/9/15
     * @author tou
     */
    private void initUI() {

        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);        // 状态栏透明

        mWebView = findViewById(R.id.web_view);
        mSearch = findViewById(R.id.edit_text_searchBar);
        mProgressBar = findViewById(R.id.progress_bar);
        mResize = findViewById(R.id.img_view_resize);
        mReload = findViewById(R.id.img_view_reload);
        mBack = findViewById(R.id.img_view_back);
        mForward = findViewById(R.id.img_view_forward);
        mOptions = findViewById(R.id.img_view_options);
        mRecords = findViewById(R.id.img_view_records);
        mPages = findViewById(R.id.img_view_pages);

        mFooter =  (footerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_footer);
        mHeader =  (headerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_header);

        mResize.setOnClickListener(this);
        mReload.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mForward.setOnClickListener(this);
        mOptions.setOnClickListener(this);
        mRecords.setOnClickListener(this);
        mPages.setOnClickListener(this);

        // 地址输入栏获取与失去焦点处理
        mSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    // 显示当前网址链接
                    mSearch.setTextColor(ContextCompat.getColor(mContext, R.color.edit_text_color));
                    mSearch.setText(mWebView.getUrl());
                    // 光标置于末尾
                    mSearch.setSelection(mSearch.getText().length());
                    // 显示因特网图标
//                    webIcon.setImageResource(R.drawable.internet);
                    // 显示跳转按钮
//                    btnStart.setImageResource(R.drawable.go);
                } else {
                    // 显示网站名
                    mSearch.setTextColor(ContextCompat.getColor(mContext, R.color.edit_text_color_hint));
                    mSearch.setText(mWebView.getTitle());
                    // 显示网站图标
//                    webIcon.setImageBitmap(mWebView.getFavicon());
                    // 显示刷新按钮
//                    btnStart.setImageResource(R.drawable.refresh);
                }
            }
        });

        // 监听键盘回车搜索
        mSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    mReload.callOnClick();
                    mSearch.clearFocus();
                }
                return false;
            }
        });


    }

    /**
     * 初始化 webView 参数
     * @return void
     * @date 2021/9/14 17:38
     * @author tou
     */
    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    private void initWebView() {

        /* 使用重写的 WebViewClient 和 WebChromeClient */
        mWebView.setWebViewClient(new sWebViewClient(){});
        mWebView.setWebChromeClient(new sWebChromeClient(){});

        mWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mDetector.onTouchEvent(event);
            }
        });

        /* 设置 */
        WebSettings settings = mWebView.getSettings();

        // 设置默认编码格式
        settings.setDefaultTextEncodingName("utf-8");
        // 设置浏览器 UserAgent
        settings.setUserAgentString(settings.getUserAgentString() + "; " + getResources().getString(R.string.user_agent_detail));

        // 启用 js 功能
        settings.setJavaScriptEnabled(true);
        // 支持通过JS打开新窗口
        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        // 使用网页元标记中定义的属性加载 WebView
        settings.setUseWideViewPort(true);
        // 缩放至屏幕的大小
        settings.setLoadWithOverviewMode(true);

        // 支持缩放，默认为true。是下面那个的前提。
        settings.setSupportZoom(true);
        // 设置内置的缩放控件。
        settings.setBuiltInZoomControls(true);
        // 隐藏原生的缩放控件
        settings.setDisplayZoomControls(false);


        // 设置可以访问文件
        settings.setAllowFileAccess(true);
        // 启用本地缓存
        settings.setDomStorageEnabled(true);
        // 支持自动加载图片 TODO: set it optional
        settings.setLoadsImagesAutomatically(true);
        // HTTP与HTTPS混合加载模式（注：application 中需启用 android:usesCleartextTraffic="true"）
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        // 加载首页
        mWebView.loadUrl(getResources().getString(R.string.home_url));

    }

    /**
     * 判断左滑右滑
     * @param xAxis X轴滑动距离
     * @param yAxis Y轴滑动距离
     * @date 2021/9/22 2:34
     * @author tou
     */
    private void judgeSwiperDirection(float xAxis, float yAxis) {

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        /* 避免和点击事件冲突 */
        if (yAxis > 25) {
//            Log.i(TAG, "judgeSwiperDirection:下滑触发" + " yAxis=" + yAxis);
            fragmentTransaction.show(mFooter);
            fragmentTransaction.show(mHeader);

        } else {
//            Log.i(TAG, "judgeSwiperDirection:上滑触发" + " yAxis=" + yAxis);
            fragmentTransaction.hide(mFooter);
            fragmentTransaction.hide(mHeader);
        }

        fragmentTransaction.commit();      // 提交！

    }


    /*---------------------------------------------------2-----------------------------------------------------------*/

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     *
     * @date 2021/9/13
     * @author tou
     */
    @Override
    public void onClick(View v) {

        int id = v.getId();

        /* 判断点击事件的触发者，选择对应触发内容 */

        if (id == R.id.img_view_resize) {

            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

            if (mFooter.isHidden() || mHeader.isHidden()) {
                fragmentTransaction.show(mFooter);
                fragmentTransaction.show(mHeader);
            } else {
                fragmentTransaction.hide(mFooter);
                fragmentTransaction.hide(mHeader);
            }
            fragmentTransaction.commit();      // 提交！

        } else if (id == R.id.img_view_reload) {

            /* 通过搜索栏是否聚焦判断是搜索还是刷新 */

            if (mSearch.hasFocus()) {

                if (inputMethodManager.isActive()) inputMethodManager.hideSoftInputFromWindow(mSearch.getApplicationWindowToken(), 0);

                String strInput = mSearch.getText().toString();

                if (!isUrl(strInput)) {

                    try { mWebView.loadUrl(getSearchEngineUrl() + URLEncoder.encode(strInput,"utf-8")); }
                    catch (UnsupportedEncodingException e) { e.printStackTrace(); }

                } else {

                    mWebView.loadUrl(strInput);

                }

            } else {

                mWebView.reload();

            }

        } else if (id == R.id.img_view_back) {

            mWebView.goBack();

        } else if (id == R.id.img_view_forward) {

            mWebView.goForward();

        } else if (id == R.id.img_view_options) {
            // TODO
            Toast.makeText(mContext, "options 功能开发中", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.img_view_records) {
            // TODO
            Toast.makeText(mContext, "records 功能开发中", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.img_view_pages) {
            // TODO
            Toast.makeText(mContext, "pages 功能开发中", Toast.LENGTH_SHORT).show();
        }

    }



    /**
     * 重写 WebViewClient
     * @date 2021/9/14
     * @author tou
     */
    private class sWebViewClient extends WebViewClient {

        /**
         * 使用 webView 加载网页，而不调用外部浏览器
         * TODO: 404 处理
         * TODO:调用第三方应用前try-catch可靠性
         * TODO:调用第三方应用前弹窗提示用户，允许后再调用
         * @param view:
         * @param request:
         * @return boolean
         * @date 2021/9/14 20:03
         * @author tou
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

            String url = request.getUrl().toString();

            // 正常的内容，打开
            if (url.startsWith(HTTP) || url.startsWith(HTTPS)) {
                view.loadUrl(url);
                return true;
            }

            // 调用第三方应用时，如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            } catch (Exception e) {
                return true;
            }

        }


        /**
         * 当页面开始加载时，显示加载进度
         * @param view:
         * @param url:
         * @param favicon:
         * @return void
         * @date 2021/9/14 20:06
         * @author tou
         */
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            super.onPageStarted(view, url, favicon);

            mProgressBar.setProgress(0);
            mProgressBar.setVisibility(View.VISIBLE);

            // 更新状态文字
            mSearch.setText(getResources().getText(R.string.loading_hint));

        }




        /**
         * 当页面加载完毕，修改页面标题
         * @param view The WebView that is initiating the callback.
         * @param url  The url of the page.
         * @return void
         * @date 2021/9/14 20:07
         * @author tou
         */
        @Override
        public void onPageFinished(WebView view, String url) {

            super.onPageFinished(view, url);

            mProgressBar.setVisibility(View.INVISIBLE);
            setTitle(mWebView.getTitle());
            mSearch.setText(mWebView.getTitle());

        }


    }



    /**
     * 重写 WebChromeClient 类
     * @date 2021/9/14
     * @author tou
     */
    private class sWebChromeClient extends WebChromeClient {

        private final static int WEB_PROGRESS_MAX = 100;

        /**
         * 刷新时更新进度条
         * @param view:
         * @param newProgress:
         * @return void
         * @date 2021/9/14 20:11
         * @author tou
         */
        @Override
        public void onProgressChanged(WebView view, int newProgress) {

            super.onProgressChanged(view, newProgress);

            mProgressBar.setProgress(newProgress);

            if (newProgress > 0) {
                if (newProgress == WEB_PROGRESS_MAX) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }

        }

        /**
         * 更新当前页面图标
         * TODO: 可以不用
         * @param view:
         * @param icon:
         * @return void
         * @date 2021/9/14 20:15
         * @author tou
         */
        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);
//            webIcon.setImageBitmap(icon);
        }


        /**
         * 更新当前页面标题
         * TODO: 可以不用
         * @param view:
         * @param title:
         * @return void
         * @date 2021/9/14 20:16
         * @author tou
         */
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
//            setTitle(title);
            mSearch.setText(title);
        }



    }


    /*---------------------------------------------------2-----------------------------------------------------------*/

    /**
     * 判断是否是网页链接
     * TODO:已委托大宇 0915
     * TODO:进阶；判断是否是网址简写
     * @param strInput: 用户在搜索栏输入的内容
     * @return boolean
     * @date 2021/9/15 19:17
     * @author tou
     */
    private boolean isUrl(String strInput) {

        return false;

    }

    /**
     * 预留接口：判断用户使用的搜索引擎
     * TODO: 自定义搜索引擎
     * @return java.lang.String
     * @date 2021/9/15 19:16
     * @author tou
     */
    private String getSearchEngineUrl() {

        return getResources().getString(R.string.search_engine_baidu_url);

    }

    /**
     * 预留接口：设置屏幕方向
     * TODO: 设置页
     * @return void
     * @date 2021/9/22 11:07
     * @author tou
     */
    @SuppressLint("SourceLockedOrientationActivity")
    private void setScreenOrientation(int id) {

        switch (id) {
            case SCREEN_ORIENTATION_LANDSCAPE:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//设置屏幕为横屏, 锁定方向
                break;
            case SCREEN_ORIENTATION_PORTRAIT:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//设置屏幕为竖屏, 锁定方向
                break;
            case SCREEN_ORIENTATION_UNSPECIFIED:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);//放弃锁定方向
                break;
        }

    }


}

