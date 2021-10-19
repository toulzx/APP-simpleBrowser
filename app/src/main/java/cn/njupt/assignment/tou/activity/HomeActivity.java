package cn.njupt.assignment.tou.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
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

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Objects;

import cn.njupt.assignment.tou.R;
import cn.njupt.assignment.tou.base.sWebView;
import cn.njupt.assignment.tou.callback.BookmarkAddCallbackListener;
import cn.njupt.assignment.tou.callback.InputStatusCallbackListener;
import cn.njupt.assignment.tou.dao.BookmarkDao;
import cn.njupt.assignment.tou.entity.Bookmark;
import cn.njupt.assignment.tou.fragment.BarFooterFragment;
import cn.njupt.assignment.tou.fragment.BarHeaderFragment;
import cn.njupt.assignment.tou.callback.OptionsGraphlessModeCallbackListener;
import cn.njupt.assignment.tou.fragment.OptionsInDialogFragment;
import cn.njupt.assignment.tou.fragment.RecordsInDialogFragment;
import cn.njupt.assignment.tou.repository.BookmarkRepository;
import cn.njupt.assignment.tou.utils.OptionSPHelper;
import cn.njupt.assignment.tou.utils.ToastUtil;
import cn.njupt.assignment.tou.utils.UrlUtil;
import cn.njupt.assignment.tou.utils.WebViewFragment;
import cn.njupt.assignment.tou.utils.WebViewHelper;
import cn.njupt.assignment.tou.viewmodel.BookmarkViewModel;
import cn.njupt.assignment.tou.viewmodel.HistoryRecordViewModel;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = HomeActivity.class.getSimpleName();

    private sWebView mWebView;
    private EditText mSearch;
    private ProgressBar mProgressBar;
    private ImageView mResize, mReload, mBack, mForward, mOptions, mRecords, mPages;

    WebSettings mWebSettings;

    private BarFooterFragment mFooter;
    private BarHeaderFragment mHeader;

    private Intent intentOfHistory;
    private HistoryRecordViewModel historyRecordViewModel;
    private BookmarkRepository bookmarkRepository;

    private BookmarkViewModel mBookmarkViewModel;

    private Context mContext;
    private FragmentManager mFragmentManager;
    private InputMethodManager inputMethodManager;

    private long exitTime = 0;
    private static final int PRESS_BACK_EXIT_GAP = 2000;

    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";

    private static final int SCREEN_ORIENTATION_LANDSCAPE = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    private static final int SCREEN_ORIENTATION_PORTRAIT = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    private static final int SCREEN_ORIENTATION_UNSPECIFIED = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;

    public static final int ALLOW_IMAGE_LOADED = 1;
    public static final int PROHIBIT_IMAGE_LOADED = 0;

    private BottomSheetDialog bottomSheetDialog;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        historyRecordViewModel = new ViewModelProvider(this).get(HistoryRecordViewModel.class);
        mBookmarkViewModel = new ViewModelProvider(this).get(BookmarkViewModel.class);
        setContentView(R.layout.activity_home);

        mContext = this;
        mFragmentManager = getSupportFragmentManager();
        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        initUI();

        initWebView();

        initData();

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mWebView != null) {
            mWebView.saveState(outState);
        }
        WebViewHelper.currentBundle = outState;
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
     * 系统返回键 绑定 webView 后退操作
     * @return void
     * @date 2021/9/14 17:27
     * @author tou
     */
    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed: pressed");
        if (Objects.equals(OptionSPHelper.getForceFullScreenValue(), String.valueOf(true))) {
            // 关闭强制全屏选项
            OptionSPHelper.setValue(String.valueOf(false), null, null, null);
            // 重现 bar 栏
            setBarStatus(false);
            Log.i(TAG, "onBackPressed: 全屏");
        } else {
            Log.i(TAG, "onBackPressed: else");
            if (mWebView.canGoBack()) {
                Log.i(TAG, "onBackPressed: canGoBack");
                mWebView.goBack();
            } else {
                Log.i(TAG, "onBackPressed: cannotGoBack");
                if ((System.currentTimeMillis() - exitTime) > PRESS_BACK_EXIT_GAP) {
                    // 连点两次退出程序
                    ToastUtil.shortToast(mContext, "再按一次退出程序");
                    exitTime = System.currentTimeMillis();
                } else {
                    super.onBackPressed();
                }
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

    @SuppressLint("ClickableViewAccessibility")
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

        mFooter =  (BarFooterFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_footer);
        mHeader =  (BarHeaderFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_header);

        mResize.setOnClickListener(this);
        mReload.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mForward.setOnClickListener(this);
        mOptions.setOnClickListener(this);
        mRecords.setOnClickListener(this);
        mPages.setOnClickListener(this);

        setListener();

    }

    /**
     * 初始化 webView 参数
     * @return void
     * @date 2021/9/14 17:38
     * @author tou
     */
    @SuppressLint({"SetJavaScriptEnabled"})
    private void initWebView() {


        /* 使用重写的 WebViewClient 和 WebChromeClient */

        mWebView.setWebViewClient(new sWebViewClient(){});
        mWebView.setWebChromeClient(new sWebChromeClient(){});

        /* 重写 WebView 的 onScroll 实现自适应全屏模式 */
        mWebView.setOnScrollChangedCallback(new sWebView.OnScrollChangedCallback(){
            public void onScroll(int dx, int dy){
                // 判断是否启动了强制全屏模式
                if (Objects.equals(OptionSPHelper.getForceFullScreenValue(), String.valueOf(true))) return;
                // 自适应全屏
                if (dy > 25) {
                    setBarStatus(true);
                } else if ( dy < -25 ) {
                    setBarStatus(false);
                }
            }
        });


        /* 设置 */

        mWebSettings = mWebView.getSettings();

        // 设置默认编码格式
        mWebSettings.setDefaultTextEncodingName("utf-8");
        // 设置浏览器 UserAgent
        mWebSettings.setUserAgentString(mWebSettings.getUserAgentString() + "; " + getResources().getString(R.string.user_agent_detail));
//        settings.setUserAgentString("Android");
        // 设置浏览器 UserAgent
        mWebSettings.setUserAgentString(mWebSettings.getUserAgentString() + " MyBrowser/" + getVerName(mContext));

        // 启用 js 功能
        mWebSettings.setJavaScriptEnabled(true);
        // 支持通过JS打开新窗口
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        // 使用网页元标记中定义的属性加载 WebView
        mWebSettings.setUseWideViewPort(true);

        // 支持缩放，默认为true。是下面那个的前提。
        mWebSettings.setSupportZoom(true);
        // 设置内置的缩放控件。
        mWebSettings.setBuiltInZoomControls(true);
        // 隐藏原生的缩放控件
        mWebSettings.setDisplayZoomControls(false);
        // 缩放至屏幕的大小
        mWebSettings.setLoadWithOverviewMode(true);

        // 设置可以访问文件
        mWebSettings.setAllowFileAccess(true);
        // 启用本地缓存
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setAppCacheEnabled(true);      // 不推荐使用？弃用？
        // 设置缓存文件路径
        String appCacheDir = this.getBaseContext().getCacheDir().getAbsolutePath(); // /data/user/0/cn.njupt.assignment.tou/cache
//        String appCacheDir = this.getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();    // /data/user/0/cn.njupt.assignment.tou/app_cache
        mWebSettings.setAppCachePath(appCacheDir);
        // 设置缓存模式
        mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        // 启用自动加载图片
        mWebSettings.setLoadsImagesAutomatically(true);
        mWebSettings.setBlockNetworkImage(false);

        // HTTP与HTTPS混合加载模式（注：application 中需启用 android:usesCleartextTraffic="true"）
        // 资源混合模式
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        }
        mWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        intentOfHistory =getIntent();
        String historyUrl = intentOfHistory.getStringExtra("history_url");
        if (WebViewHelper.currentWebView!=null){
            if (WebViewHelper.currentBundle!=null){
                mWebView.restoreState(WebViewHelper.currentBundle);
                mSearch.setText(WebViewHelper.currentWebView.getUrl());
            }else{
                mWebView.loadUrl(WebViewHelper.currentWebView.getUrl());
            }
        }else{
            if (historyUrl!=null){
                mWebView.loadUrl(historyUrl);
            }else{
                // 加载首页
                mWebView.loadUrl(getResources().getString(R.string.home_url));
//                mWebView.loadUrl("file://///storage/emulated/0/Download/test.mht");
            }
        }
        mWebView.loadUrl(getResources().getString(R.string.home_url));

        WebViewHelper.headWebView = WebViewHelper.currentWebView;

    }



    /**
     *初始化 SharedPreferences 和控件
     * @return void
     * @date 2021/10/14 17:55
     * @author tou
     */
    private void initData() {

        OptionSPHelper.init(getApplication());

        // set Orientation Mode
        if (Objects.equals(OptionSPHelper.getLockOrientationValue(), "horizontal")) {
            setScreenOrientation(SCREEN_ORIENTATION_LANDSCAPE);
        } else if (Objects.equals(OptionSPHelper.getLockOrientationValue(), "vertical")) {
            setScreenOrientation(SCREEN_ORIENTATION_PORTRAIT);
        } else if (Objects.equals(OptionSPHelper.getLockOrientationValue(), "auto")) {
            setScreenOrientation(SCREEN_ORIENTATION_UNSPECIFIED);
        }

        // set Graphless Mode
        if (Objects.equals(OptionSPHelper.getGraphlessModeValue(), String.valueOf(true))) {
            setImageBlock(PROHIBIT_IMAGE_LOADED);
        }

        // no need to set PrivateMode here :-)
        // no need to set FullScreenForce here :-)

    }


    /*---------------------------------------------------2-----------------------------------------------------------*/

    /**
     * View.OnClick 事件监听
     * Called when a view has been clicked.
     * @param view The view that was clicked.
     * @date 2021/9/13
     * @author tou
     */
    @Override
    public void onClick(View view) {

        int id = view.getId();

        /* 判断点击事件的触发者，选择对应触发内容 */

        if (id == R.id.img_view_resize) {

            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

            if (mFooter.isHidden() || mHeader.isHidden()) {
                setBarStatus(false);
            } else {
                setBarStatus(true);
            }

            // 设置强制全屏
            OptionSPHelper.setValue(String.valueOf(true), null, null, null);

            // 回调 HomeActivity 使 Bar 栏消失
            setBarStatus(true);

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
            if (mWebView.canGoBack()) {
                Log.i(TAG, "onClick: canGoBack");
                if (mWebView.copyBackForwardList()!=null&&mWebView.copyBackForwardList().getSize()>0) {
                    Log.i(TAG, "onClick: okokokok");
                    if (mWebView.copyBackForwardList().getSize() >= 3) {
                        Log.i(TAG, "onClick: "+mWebView.copyBackForwardList().getCurrentItem().getTitle());
                        Log.i(TAG, "onClick: " + mWebView.copyBackForwardList().getItemAtIndex(0).getTitle());
                        Log.i(TAG, "onClick: " + mWebView.copyBackForwardList().getItemAtIndex(1).getTitle());
                        Log.i(TAG, "onClick: " + mWebView.copyBackForwardList().getItemAtIndex(2).getTitle());
                    }
                }
                else
                    Log.i(TAG, "onClick: nonononono");
                mWebView.goBack();
            }else
                Log.i(TAG, "onClick: cannotGoBack");

        } else if (id == R.id.img_view_forward) {

            mWebView.goForward();

        } else if (id == R.id.img_view_options) {

            new OptionsInDialogFragment().show(getSupportFragmentManager(), OptionsInDialogFragment.class.getSimpleName());

        } else if (id == R.id.img_view_records) {

            new RecordsInDialogFragment().show(getSupportFragmentManager(), RecordsInDialogFragment.class.getSimpleName());


        } else if (id == R.id.img_view_pages) {
            // TODO
            // Toast.makeText(mContext, "pages 功能开发中", Toast.LENGTH_SHORT).show();
            WebViewHelper.headWebView = mWebView;
            if (!WebViewHelper.isExist) {
                if (mWebView == null) {
                    Log.e("houxl", "生成空窗口");
                    WebViewHelper.webList.add(new WebViewFragment(null, myShot(HomeActivity.this)));
                    WebViewHelper.currentWebView = null;
                } else {
                    Log.e("houxl", "生成实时窗口");
                    WebViewHelper.currentWebView = mWebView;
                    mWebView.setDrawingCacheEnabled(true);
                    WebViewHelper.webList.add(new WebViewFragment(mWebView, myShot(HomeActivity.this)));
                }
                WebViewHelper.isExist = true;
            }
            Intent intent=new Intent(HomeActivity.this, PagerActivity.class);
            startActivity(intent);
        }

    }

    /**
     * 其它监听器的设置
     * - 地址输入栏失去焦点监听
     * - 监听键盘回车搜索
     * - 回调函数
     * @return void
     * @date 2021/10/11 9:43
     * @author tou
     */
    private void setListener() {

        // 地址输入栏获取与失去焦点处理
        mSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    // 显示当前网址链接
                    mSearch.setTextColor(ContextCompat.getColor(mContext, R.color.edit_text_color));
                    mSearch.setText(mWebView.getUrl());
                    // 光标置于末尾
                    mSearch.setSelection(0, mSearch.getText().length());
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

        /* 回调函数 */

        // 无图模式
        OptionsInDialogFragment.SetGraphlessModeCallbackListener(new OptionsGraphlessModeCallbackListener() {
            @Override
            public void setGraphlessMode(int flag) {
                setImageBlock(flag);
            }
        });

         // 网页内调用软键盘时
        BarFooterFragment.SetInputStatusCallbackListener(new InputStatusCallbackListener() {
            @Override
            public void fullScreen(boolean isToHide) {
                if (!mSearch.hasFocus()) {
                    setBarStatus(isToHide);
                }
            }
        });

        OptionsInDialogFragment.SetBookmarkAddCallbackListener(new BookmarkAddCallbackListener() {
            @Override
            public void addBookmark() {
                mBookmarkViewModel.insertBookmark(new Bookmark(mWebView.getTitle(), mWebView.getUrl(), UrlUtil.getIconUrl(mWebView.getUrl()), 0, 0, -1, mBookmarkViewModel.getMaxSort(-1) + 1));
            }
        });

    }

    /**
     * 重写 WebViewClient
     * @date 2021/9/14
     * @author tou
     */
    private class sWebViewClient extends WebViewClient {

        private String startUrl;

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

            if (startUrl!=null&&startUrl.equals(request.getUrl().toString())){
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
                    return false;
                }
            }else{
                return super.shouldOverrideUrlLoading(view,request);
            }
        }

        /**
         * 当页面开始加载时
         * 显示加载进度
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
            startUrl = url;

        }

        /**
         * 当页面加载完毕
         * 修改页面标题
         * @param webView The WebView that is initiating the callback.
         * @param url  The url of the page.
         * @return void
         * @date 2021/9/14 20:07
         * @author tou
         */
        @Override
        public void onPageFinished(WebView webView, String url) {

            super.onPageFinished(webView, url);

            mProgressBar.setVisibility(View.INVISIBLE);
            setTitle(mWebView.getTitle());
            mSearch.setText(mWebView.getTitle());


            /* 调用js代码 */
//            String code = "";
//            mWebView.post(new Runnable() {
//                @Override
//                public void run() {
//                    mWebView.loadUrl(code);
//                    Log.i(TAG, "run: start download");
//                }
//            });

            if (!url.endsWith(".mht")) {
                /* 保存网页功能 */
//                String cacheFileName = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DOWNLOADS
//                        + File.separator + System.currentTimeMillis() + ".xml";
//                mWebView.saveWebArchive(cacheFileName);
//                Log.i(TAG, "onPageFinished: "+cacheFileName);
//                String cacheFileDir = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DOWNLOADS;
//                File file = new File(cacheFileDir,"test.mht");
//                try {
//                    if (!file.exists()){
//                        file.createNewFile();
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//                mWebView.saveWebArchive(file.getAbsolutePath());
//                Log.i(TAG, "onPageFinished: "+file.getAbsolutePath());
//                mWebView.saveWebArchive(cacheFileDir, true, new ValueCallback<String>() {
//                    @Override
//                    public void onReceiveValue(String value) {
//                        if (value == null) {
//                            Log.i(TAG, "onReceiveValue: saveWebArchive -> " + value);
//                        } else {
//                            Log.i(TAG, "onReceiveValue: saveWebArchive -> successfully in " + value);
//                        }
//                    }
//                });
                //记录浏览的历史记录
                if (Objects.equals(OptionSPHelper.getPrivateModeValue(), String.valueOf(false))) {
                    historyRecordViewModel.insertHistoryRecord(webView.getTitle(), webView.getUrl(), UrlUtil.getIconUrl(webView.getUrl()), new Date());
                }
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            mWebView.getClass().getMethod("onPause").invoke(mWebView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mWebView.getClass().getMethod("onResume").invoke(mWebView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重写 WebChromeClient 类
     * @date 2021/9/14
     * @author tou
     */
    private class sWebChromeClient extends WebChromeClient {

        private int lastProgress = 0;
        private final static int WEB_PROGRESS_MAX = 100;

        /**
         * 当加载进度改变时：
         * 刷新时更新进度条
         * @param webView:
         * @param newProgress:
         * @return void
         * @date 2021/9/14 20:11
         * @author tou
         */
        @Override
        public void onProgressChanged(WebView webView, int newProgress) {

            super.onProgressChanged(webView, newProgress);

            mProgressBar.setProgress(newProgress);
            if (newProgress > 0) {
                if (newProgress == WEB_PROGRESS_MAX) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }

            if ( lastProgress != WEB_PROGRESS_MAX && newProgress == WEB_PROGRESS_MAX ) {
                lastProgress = newProgress;
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
         * @param view:
         * @param title:
         * @return void
         * @date 2021/9/14 20:16
         * @author tou
         */
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            mSearch.setText(title);
        }


        /**
         * @description 修改js弹出框样式
         * @param
         * @return
         * @author sherman
         * @time 2021/10/13 21:02
         */
//        @Override
//        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
//            AlertDialog.Builder b = new AlertDialog.Builder(HomeActivity.this);
//            b.setTitle("Alert");
//            b.setMessage(message);
//            b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    result.confirm();
//                }
//            });
//            b.setCancelable(false);
//            b.create().show();
//            return true;
//        }
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

        return UrlUtil.isTopURL(strInput);

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
     * 设置 bar 栏的显示状态
     * @param isToHide:  true:设置隐藏，false:设置显示
     * @return void
     * @date 2021/10/15 14:52
     * @author tou
     */
    private void setBarStatus(boolean isToHide) {

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        if (isToHide) {
            fragmentTransaction.hide(mFooter);
            fragmentTransaction.hide(mHeader);
        } else {
            fragmentTransaction.show(mFooter);
            fragmentTransaction.show(mHeader);
        }

        fragmentTransaction.commit();

    }

    /**
     * 设置屏幕方向
     * @return void
     * @date 2021/9/22 11:07
     * @author tou
     */
    @SuppressLint("SourceLockedOrientationActivity")
    public void setScreenOrientation(int flag) {

        switch (flag) {
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

    /**
     * 设置是否自动加载图片
     * 注意 Options 中的设置通过回调函数实现
     * 注意 setLoadsImagesAutomatically 必须始终允许，它关联的不仅是网页上的图片。
//     * @param flag:
     * @return void
     * @date 2021/9/22 15:01
     * @author tou
     */
    public void setImageBlock(int flag) {

        switch (flag) {
            case ALLOW_IMAGE_LOADED:
                mWebSettings.setLoadsImagesAutomatically(true);
                mWebSettings.setBlockNetworkImage(false);
                break;
            case PROHIBIT_IMAGE_LOADED:
                mWebSettings.setLoadsImagesAutomatically(true);
                mWebSettings.setBlockNetworkImage(true);
                break;
        }

    }

    public Bitmap myShot(Activity activity) {
        // 获取windows中最顶层的view
        View view = activity.getWindow().getDecorView();
        view.buildDrawingCache();

        // 获取状态栏高度
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBarHeights = rect.top;
        Display display = activity.getWindowManager().getDefaultDisplay();

        // 获取屏幕宽和高
        int widths = display.getWidth();
        int heights = display.getHeight();

        // 允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);

        // 去掉状态栏
        Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache(), 0,
                statusBarHeights, widths, heights - statusBarHeights);

        // 销毁缓存信息
        view.destroyDrawingCache();

        return bmp;
    }

    /**
     * 获取版本号名称
     *
     * @param context 上下文
     * @return 当前版本名称
     */
    private static String getVerName(Context context) {
        String verName = "unKnown";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

}

