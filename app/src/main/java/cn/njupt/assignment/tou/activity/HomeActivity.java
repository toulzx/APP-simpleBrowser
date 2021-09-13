package cn.njupt.assignment.tou.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import cn.njupt.assignment.tou.R;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private Context mContext = this;

    private WebView mWebView;
    private EditText mSearch;
    private ImageView mResize, mReload, mBack, mForward, mOptions, mRecords, mPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupUI();

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

    /*---------------------------------------------------1-----------------------------------------------------------*/

    private void setupUI() {

        mWebView = findViewById(R.id.web_view);
        mResize = findViewById(R.id.img_view_resize);
        mReload = findViewById(R.id.img_view_reload);
        mBack = findViewById(R.id.img_view_back);
        mForward = findViewById(R.id.img_view_forward);
        mOptions = findViewById(R.id.img_view_options);
        mRecords = findViewById(R.id.img_view_records);
        mPages = findViewById(R.id.img_view_pages);

        mWebView.loadUrl("https://www.bing.com");
        mWebView.setWebViewClient(new WebViewClient(){});

        mResize.setOnClickListener(this);
        mReload.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mForward.setOnClickListener(this);
        mOptions.setOnClickListener(this);
        mRecords.setOnClickListener(this);
        mPages.setOnClickListener(this);



    }

    /*---------------------------------------------------2-----------------------------------------------------------*/

    /**
     * Called when a view has been clicked.
     *
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.img_view_resize:{
                // TODO
                Toast.makeText(mContext, "resize 功能开发中", Toast.LENGTH_SHORT).show();
                break;
            }

            case R.id.img_view_reload:{
                mWebView.reload();
                break;
            }

            case R.id.img_view_back:{
                mWebView.goBack();
                break;
            }

            case R.id.img_view_forward:{
                mWebView.goForward();
                break;
            }

            case R.id.img_view_options:{
                // TODO
                Toast.makeText(mContext, "options 功能开发中", Toast.LENGTH_SHORT).show();
                break;

            }

            case R.id.img_view_records:{
                // TODO
                Toast.makeText(mContext, "records 功能开发中", Toast.LENGTH_SHORT).show();
                break;
            }

            case R.id.img_view_pages:{
                // TODO
                Toast.makeText(mContext, "pages 功能开发中", Toast.LENGTH_SHORT).show();
                break;

            }

        }

    }
}

