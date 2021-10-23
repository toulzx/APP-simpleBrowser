package cn.njupt.assignment.tou.fragment;

import android.app.Dialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;

import cn.njupt.assignment.tou.R;
import cn.njupt.assignment.tou.activity.HomeActivity;
import cn.njupt.assignment.tou.adapter.RecordsViewPager2Adapter;
import cn.njupt.assignment.tou.callback.ToBookmarkCallbackListener;
import cn.njupt.assignment.tou.callback.ToHistoryCallbackListener;
import cn.njupt.assignment.tou.callback.ToDialogRecordsCallbackListener;
import cn.njupt.assignment.tou.utils.OptionSPHelper;

public class RecordsInDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private static final String TAG = RecordsInDialogFragment.class.getSimpleName();

    private View mView;

    private static boolean mIsBookmarkEditMode = false;
    private static boolean mIsBookmarkInHomePage = true;
    private static String mStrBookmarkUpperFolderName = "";

    private BottomSheetDialog mBottomSheetDialog;
    private BottomSheetBehavior<View> mBottomSheetBehavior;

    private TabLayout mTabLayout;
    private ViewPager2 mViewPager2;
    private ConstraintLayout mClRecords;
    private AppCompatButton mBtnCancel;
    private AppCompatButton mBtnSet;
    private TextView mTvBookmarkUpperFolderName;

    // Bookmark 编辑状态时的顶部工具栏
    private LinearLayout checked_top;
    private TextView checked_all;
    private TextView checked_num;
    private TextView checked_cancel;

    private TabLayoutMediator mTabLayoutMediator;
    private RecordsViewPager2Adapter mViewPager2Adapter;

    private static final String[] TAB_NAMES = new String[]{"history", "bookmark" };

    // callback
    private ViewPager2.OnPageChangeCallback changeCallback;
    public static ToBookmarkCallbackListener mToBookmarkCallbackListener;
    public static ToHistoryCallbackListener mToHistoryCallbackListener ;
    public static void setToBookmarkCallbackListener(ToBookmarkCallbackListener listener) {
        mToBookmarkCallbackListener = listener;
    }
    public static void setToHistoryCallbackListener (ToHistoryCallbackListener listener) {
        mToHistoryCallbackListener = listener;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // bind  view
        if (mView == null) {
            mView = View.inflate(getContext(), R.layout.fragment_dialog_records, null);
        }

        // bind bottomSheetDialog
        mBottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        mBottomSheetDialog.setContentView(mView);
        // get and set behavior
        mBottomSheetBehavior = BottomSheetBehavior.from((View) mView.getParent());
        mBottomSheetBehavior.setHideable(true);
        mBottomSheetBehavior.setSkipCollapsed(true);

        // bind var
        mTabLayout = mView.findViewById(R.id.tab_layout_records);
        mViewPager2 = mView.findViewById(R.id.view_pager_2_records);
        mBtnCancel = mView.findViewById(R.id.btn_records_cancel);
        mBtnSet = mView.findViewById(R.id.btn_records_ultra_set);
        mTvBookmarkUpperFolderName = mView.findViewById(R.id.bookmark_upper_folder_name);
        mClRecords = mView.findViewById(R.id.records_header_container);
        checked_top = mView.findViewById(R.id.checked_top);
        checked_num = mView.findViewById(R.id.checked_num);
        checked_all = mView.findViewById(R.id.checked_all);
        checked_cancel = mView.findViewById(R.id.checked_cancel);


        mBtnCancel.setOnClickListener(this);
        mBtnSet.setOnClickListener(this);
        checked_all.setOnClickListener(this);
        checked_cancel.setOnClickListener(this);

        // initialize layout based on orientation
        Configuration configuration = getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            mClRecords.setVisibility(View.GONE);
        }

        // bind Adapter
        mViewPager2Adapter = new RecordsViewPager2Adapter(this);
        mViewPager2.setAdapter(mViewPager2Adapter);

        // 允许 viewPager2 横向滚动
        mViewPager2.setUserInputEnabled(true);

        mViewPager2.setNestedScrollingEnabled(false);

        if (Objects.equals(OptionSPHelper.getTargetBookmarkPageValue(), "true")) {
            mBtnSet.setText("更多");
            mIsBookmarkEditMode = false;
            mViewPager2.setCurrentItem(RecordsViewPager2Adapter.BOOKMARK_PAGE_INDEX);
            mBtnCancel.setText(mIsBookmarkInHomePage ? "关闭" : "返回");
            mTvBookmarkUpperFolderName.setText(mIsBookmarkInHomePage ? "" : "上一级：" + mStrBookmarkUpperFolderName);
        } else {
            mBtnSet.setText("清除");
            mViewPager2.setCurrentItem(RecordsViewPager2Adapter.HISTORY_PAGE_INDEX);
        }


        //bind TabLayout with ViewPager
        mTabLayoutMediator = new TabLayoutMediator(mTabLayout, mViewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                // set tab's title
                tab.setText(TAB_NAMES[position]);
            }
        });
        mTabLayoutMediator.attach();

        // set ViewPager2 changeCallback
        changeCallback = new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // 记录 viewPager2 最后使用的页面
                if (position == RecordsViewPager2Adapter.BOOKMARK_PAGE_INDEX) {
                    if (mIsBookmarkEditMode) {
                        mBtnSet.setText("删除");
                        mBtnCancel.setText("移动至");
                    } else {
                        mBtnSet.setText("更多");
                        /* TODO: 这里可以体会到 viewPager2 中的销毁机制
                         * 当你以 HistoryFragment 退出时，textView 的内容被销毁，当你以 BookmarkFragment 退出时，textView 内容被保留
                         * 在这里两个过程中，static 的 str 内容始终没有被销毁
                         * 注释下面这句你就能体会到了*/
                        mTvBookmarkUpperFolderName.setText("上一级：" + mStrBookmarkUpperFolderName);
                        if (mIsBookmarkInHomePage) {
                            mBtnCancel.setText("关闭");
                            mTvBookmarkUpperFolderName.setVisibility(View.GONE);
                        } else {
                            mBtnCancel.setText("返回");
                            mTvBookmarkUpperFolderName.setVisibility(View.VISIBLE);
                        }
                    }

                    OptionSPHelper.setValue(null,null,null,null, "true");
                } else if (position == RecordsViewPager2Adapter.HISTORY_PAGE_INDEX) {
                    mBtnSet.setText("清除");
                    mBtnCancel.setText("关闭");
                    mTvBookmarkUpperFolderName.setVisibility(View.GONE);
                    OptionSPHelper.setValue(null,null,null,null, "false");
                }
            }
        };
        mViewPager2.registerOnPageChangeCallback(changeCallback);

        // callback listener
        RecordsBookmarkFragment.setToDialogRecordsCallbackListener(new ToDialogRecordsCallbackListener() {
            @Override
            public void refreshFragment(int currentFolderIndex, String upperFolderName) {
                mViewPager2Adapter.update();
                if (currentFolderIndex > 0) {
                    mIsBookmarkInHomePage = false;
                    mBtnCancel.setText("返回");
                    mStrBookmarkUpperFolderName = upperFolderName;
                    mTvBookmarkUpperFolderName.setText("上一级：" + upperFolderName);
                } else {
                    mIsBookmarkInHomePage = true;
                    mBtnCancel.setText("关闭");
                    mStrBookmarkUpperFolderName = "";
                    mTvBookmarkUpperFolderName.setText("");
                }
            }
            @Override
            public void resetBtnsText(boolean isEditMode) {
                mIsBookmarkEditMode = isEditMode;
                if (isEditMode) {
                    mBtnSet.setText("删除");
                    mBtnCancel.setText("移动至");
                    mViewPager2.setUserInputEnabled(false);
                    mTabLayout.setVisibility(View.GONE);
                    checked_top.setVisibility(View.VISIBLE);
                    checked_all.setText("全选");
                    checked_num.setText("已选择0项");
                } else {
                    mBtnSet.setText("更多");
                    mBtnCancel.setText(mIsBookmarkInHomePage ? "关闭" : "返回");
                    mViewPager2.setUserInputEnabled(true);
                    checked_top.setVisibility(View.GONE);
                    mTabLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public TextView getCheckedNumTv() {
                return checked_num;
            }

            @Override
            public void hideDialog() {}
        });
        HomeActivity.setToDialogRecordsCallbackListener(new ToDialogRecordsCallbackListener() {
            @Override
            public void refreshFragment(int currentFolderIndex, String upperFolderName) {}
            @Override
            public void resetBtnsText(boolean isEditMode) {}
            @Override
            public TextView getCheckedNumTv() {
                return null;
            }

            @Override
            public void hideDialog() {
                //设置合起状态
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        return mBottomSheetDialog;
    }


    @Override
    public void onStart() {

        super.onStart();

        /*
        //拿到系统的 bottom_sheet
        FrameLayout container =
                (FrameLayout) View.inflate(getContext(), R.layout.design_bottom_sheet_dialog, null);
        FrameLayout bottomSheet = (FrameLayout) container.findViewById(R.id.design_bottom_sheet);
        // FrameLayout bottomSheet = Objects.requireNonNull(getDialog()).findViewById(R.id.design_bottom_sheet);
        //设置view高度
        bottomSheet.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
        //设置弹出高度
        BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setPeekHeight(3000);
        */

        //设置展开状态
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // unbind TabLayout with ViewPager
        mTabLayoutMediator.detach();

        // delete ViewPager2 changeCallback
        mViewPager2.unregisterOnPageChangeCallback(changeCallback);

    }

    /**
     * 监听旋转状态的改变
     *
     * 注意：必须在对应 activity 中设置
     * android:configChanges="orientation|screenSize|layoutDirection"
     * 才会生效（缺一不可）
     *
     * @param newConfig:
     * @return void
     * @date 2021/10/15 19:09
     * @author tou
     */
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mClRecords.setVisibility(View.GONE);
        } else {
            mClRecords.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Called when a view has been clicked.
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_records_cancel) {

            if (mIsBookmarkEditMode) {
                mToBookmarkCallbackListener.onMoveButtonClick();
            } else {
                if (mIsBookmarkInHomePage){
                    //设置合起状态
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                } else {
                    mToBookmarkCallbackListener.onBackButtonClick();
                }
            }


        } else if (view.getId() == R.id.btn_records_ultra_set) {

            if (mViewPager2.getCurrentItem() == RecordsViewPager2Adapter.BOOKMARK_PAGE_INDEX) {
                if (mToBookmarkCallbackListener != null) {
                    mToBookmarkCallbackListener.onMenuButtonClick(view);
                }
            } else if (mViewPager2.getCurrentItem() == RecordsViewPager2Adapter.HISTORY_PAGE_INDEX) {
                if (mToHistoryCallbackListener != null) {
                    mToHistoryCallbackListener.onHistoryButtonClick(view);
                }
            }

        } else if (view.getId() == R.id.checked_all) {

            mToBookmarkCallbackListener.onCheckAllButtonClick(checked_all,checked_num);

        } else if (view.getId() == R.id.checked_cancel) {

            mToBookmarkCallbackListener.onCancelEditModeButtonClick();

        }
    }
}
