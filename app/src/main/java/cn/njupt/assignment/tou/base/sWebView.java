package cn.njupt.assignment.tou.base;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class sWebView extends WebView {

    private final static String TAG = sWebView.class.getSimpleName();

    private OnScrollChangedCallback mOnScrollChangedCallback;

    public sWebView(@NonNull Context context) {
        super(context);
    }

    public sWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public sWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public sWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * 监听网页内容滚动
     * 当向下滑动时: t-oldt > 0
     * @param l: 当前水平滚动原点
     * @param t: 当前垂直滚动原点
     * @param oldl: 上一个水平滚动原点
     * @param oldt: 上一个垂直滚动原点
     * @return void
     * @date 2021/9/15 22:05
     * @author tou
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {

        super.onScrollChanged(l, t, oldl, oldt);

//        float webHeight = getContentHeight() * getWebScale();
//
//        float viewHeight = getHeight();
//
//        /*内容滑动百分比*/
////            int percent = (int) (t / (webHeight - viewHeight) * 100);
//
////            Log.i(TAG, "onScrollChanged: t - oldt = " + (t-oldt));
//
//
//        if (t - oldt > 0) {
//
//        }

        if (mOnScrollChangedCallback != null) {
            mOnScrollChangedCallback.onScroll(l - oldl, t - oldt);
        }

    }

    public OnScrollChangedCallback getOnScrollChangedCallback() {
        return mOnScrollChangedCallback;
    }

    public void setOnScrollChangedCallback(
            final OnScrollChangedCallback onScrollChangedCallback) {
        mOnScrollChangedCallback = onScrollChangedCallback;
    }

    /**
     * Impliment in the activity/fragment/view that you want to listen to the webview
     */
    public interface OnScrollChangedCallback {
        public void onScroll(int dx, int dy);
    }

    /**
     * 获取网页缩放比例
     * 目的是重写方法 WebView.getScale()
     * 该方法已被弃用，原因：Web 渲染和 UI 线程之间的竞争条件，此方法容易出现不准确
     * 详见 https://developer.android.com/reference/android/webkit/WebView?hl=en#getScale()
     * @return float
     * @date 2021/9/15 22:09
     * @author tou
     */
    private float getWebScale() {

        Log.i(TAG, "getWebScale: getResources().getDisplayMetrics().density = " + getResources().getDisplayMetrics().density);
        Log.i(TAG, "getWebScale: getScale = " + getScale());
        Log.i(TAG, "getWebScale: getScaleY = " + getScaleY());
        Log.i(TAG, "getWebScale: getScaleX = " + getScaleX());

        return getScale();

    }

}
