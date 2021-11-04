package cn.njupt.assignment.tou.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * https://blog.csdn.net/wei1583812/article/details/68488234?locationNum=8&fps=1
 * @return
 * @date 2021/10/31 10:37
 */

public class ReplaceViewUtil {
    private View mTargetView;
    private View mReplaceView = null;
    private Context mContext;

    private ReplaceViewUtil() {
    }

    public ReplaceViewUtil(Context context) {
        mContext = context;
    }

    /**
     * 用来替换某个View，比如你可以用一个空页面去替换某个View
     *
     * @param targetView       被替换的那个View
     * @param replaceViewResId 要替换进去的布局LayoutId
     * @return
     */
    public ReplaceViewUtil toReplaceView(View targetView, final int replaceViewResId) {
        toReplaceView(targetView, View.inflate(mContext, replaceViewResId, null));
        return this;
    }

    /**
     * 用来替换某个View，比如你可以用一个空页面去替换某个View
     *
     * @param targetView  被替换的那个View
     * @param replaceView 要替换进去的那个View
     * @return
     */
    public ReplaceViewUtil toReplaceView(View targetView, final View replaceView) {
        mTargetView = targetView;
        if (mTargetView == null) {
            return this;
        } else if (!(mTargetView.getParent() instanceof ViewGroup)) {
            return this;
        }

        ViewGroup parentViewGroup = (ViewGroup) mTargetView.getParent();
        int index = parentViewGroup.indexOfChild(mTargetView);
        if (mReplaceView != null) {
            parentViewGroup.removeView(mReplaceView);
        }
        mReplaceView = replaceView;
        mReplaceView.setLayoutParams(mTargetView.getLayoutParams());

        parentViewGroup.addView(mReplaceView, index);

        //RelativeLayout时别的View可能会依赖这个View的位置，所以不能GONE
        if (parentViewGroup instanceof RelativeLayout) {
            mTargetView.setVisibility(View.INVISIBLE);
        } else {
            mTargetView.setVisibility(View.GONE);
        }
        return this;
    }

    /**
     * 移除你替换进来的View
     */
    public final ReplaceViewUtil removeView() {
        if (mReplaceView != null && mTargetView != null) {
            if (mTargetView.getParent() instanceof ViewGroup) {
                ViewGroup parentViewGroup = (ViewGroup) mTargetView.getParent();
                parentViewGroup.removeView(mReplaceView);
                mReplaceView = null;
                mTargetView.setVisibility(View.VISIBLE);
            }
        }
        return this;
    }

    /**
     * @return 返回你替换进来的View
     */
    public final View getView() {
        return mReplaceView;
    }
}