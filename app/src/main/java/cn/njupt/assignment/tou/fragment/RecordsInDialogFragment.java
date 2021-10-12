package cn.njupt.assignment.tou.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class RecordsInDialogFragment extends BottomSheetDialogFragment {

    private View mView;

    private BottomSheetDialog mBottomSheetDialog;
    private BottomSheetBehavior<View> mBottomSheetBehavior;

    private TabLayout mTabLayout;
    private ViewPager2 mViewPager2;

    private TabLayoutMediator mTabLayoutMediator;

    private static final String[] TAB_NAMES = new String[]{"bookmark", "history"};
    private final List<Fragment> mFragmentList = new ArrayList<>();

    private final ViewPager2.OnPageChangeCallback changeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            // test: 设置选中时tab的大小
//            for (int i = 0; i < mTabLayout.getTabCount(); i++) {
//                TabLayout.Tab tab = mTabLayout.getTabAt(i);
//                assert tab != null;
//                TextView tabView = (TextView) tab.getCustomView();
//                if (tab.getPosition() == position) {
//                    tabView.setTextSize(20);
//                    tabView.setTypeface(Typeface.DEFAULT_BOLD);
//                } else {
//                    tabView.setTextSize(14);
//                    tabView.setTypeface(Typeface.DEFAULT);
//                }
//            }
        }
    };


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
        mView = View.inflate(getContext(), R.layout.fragment_dialog_records, null);

        // bind bottomSheetDialog
        mBottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        mBottomSheetDialog.setContentView(mView);

        // 获取behavior
        mBottomSheetBehavior = BottomSheetBehavior.from((View) mView.getParent());

        // bind var
        mTabLayout = mView.findViewById(R.id.tab_layout_records);
        mViewPager2 = mView.findViewById(R.id.view_pager_2_records);

        mFragmentList.add(new RecordsBookmarkFragment());
        mFragmentList.add(new RecordsHistoryFragment());

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

}
