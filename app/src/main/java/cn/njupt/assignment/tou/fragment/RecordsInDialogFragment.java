package cn.njupt.assignment.tou.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import cn.njupt.assignment.tou.R;

public class RecordsInDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private static final String TAG = RecordsInDialogFragment.class.getSimpleName();

    private View mView;

    private BottomSheetDialog mBottomSheetDialog;
    private BottomSheetBehavior<View> mBottomSheetBehavior;

    private TabLayout mTabLayout;
    private ViewPager2 mViewPager2;
    private AppCompatButton mBtnCancel;
    private AppCompatButton mBtnSet;

    private TabLayoutMediator mTabLayoutMediator;

    private static final String[] TAB_NAMES = new String[]{"bookmark", "history"};
    private final List<Fragment> mFragmentList = new ArrayList<>();

    private final ViewPager2.OnPageChangeCallback changeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            // 修改公用按钮
            if (position == 0) {
                mBtnSet.setText("看情况呢");
            } else if (position == 1) {
                mBtnSet.setText("编辑");
            }
        }
    };

    // TODO: 也许可以写到 1 个 Callback 中...
    // 但我遇到了问题，要怎么分辨唤起哪边的 Callback 呢
    public static HistoryCallBackListener mHistoryListener;
    public static BookmarkCallbackListener mBookmarkListener;

    public static void setHistoryCallBackListener(HistoryCallBackListener listener) {
        mHistoryListener = listener;
    }
    public static void setBookmarkCallBackListener(BookmarkCallbackListener listener) {
        mBookmarkListener = listener;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

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

        mBtnCancel.setOnClickListener(this);
        mBtnSet.setOnClickListener(this);

        mFragmentList.add(new RecordsBookmarkFragment());
        mFragmentList.add(new RecordsHistoryFragment());

        // TODO: 最后要禁止使用 viewPager2 滚动，以防与手势冲突
        mViewPager2.setUserInputEnabled(true);
        mViewPager2.setNestedScrollingEnabled(false);

        // bind Adapter
        mViewPager2.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public int getItemCount() {
                return mFragmentList.size();
            }
        });

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
        mViewPager2.registerOnPageChangeCallback(changeCallback);

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

    @Override
    public void dismiss() {
        super.dismiss();
    }


    /**
     * Called when a view has been clicked.
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_records_cancel) {

            //设置合起状态
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        } else if (view.getId() == R.id.btn_records_ultra_set) {

            if (mViewPager2.getCurrentItem() == 0) {
                if (mBookmarkListener != null) {
                    mBookmarkListener.onBookmarkButtonClick(view);
                } else {
                    Log.e(TAG, "onClick: mBookmarkListener != null");
                }
            } else if (mViewPager2.getCurrentItem() == 1) {
                if (mHistoryListener != null) {
                    mHistoryListener.onHistoryButtonClick(view);
                } else {
                    Log.e(TAG, "onClick: mBookmarkListener != null");
                }
            }

        }
    }
}
