package cn.njupt.assignment.tou.fragment;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import cn.njupt.assignment.tou.R;
import cn.njupt.assignment.tou.adapter.PageAdapter;
import cn.njupt.assignment.tou.base.WebViewPage;
import cn.njupt.assignment.tou.helper.WebViewPageHelper;

public class PagesInDialogFragment extends BottomSheetDialogFragment {
    private static final String TAG = PagesInDialogFragment.class.getSimpleName();

    private View mView;

    private BottomSheetDialog mBottomSheetDialog;
    private BottomSheetBehavior<View> mBottomSheetBehavior;

    private RecyclerView mRecyclerView;

    private List<WebViewPage> mWebViewPageList;
    private ArrayList<Bitmap> mBitmapList = new ArrayList<>();

    // test
    private void generateData() {
        mWebViewPageList = WebViewPageHelper.getWebViewPageList();
        for (WebViewPage webViewPage:mWebViewPageList) {
            mBitmapList.add(webViewPage.getBitmap());
            Log.i(TAG, "[abc]generateData: webViewPage.getBitmap() = " + webViewPage.getBitmap());
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // bind  view
        if (mView == null) {
            mView = View.inflate(getContext(), R.layout.fragment_dialog_pages, null);
        }


        // bind bottomSheetDialog
        mBottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        mBottomSheetDialog.setContentView(mView);

        // get and set behavior
        mBottomSheetBehavior = BottomSheetBehavior.from((View) mView.getParent());
        mBottomSheetBehavior.setHideable(true);
        mBottomSheetBehavior.setSkipCollapsed(true);


        // init recyclerview And set LayoutManager horizontally And set Adapter
        mRecyclerView = mView.findViewById(R.id.recycler_view_pages);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        mRecyclerView.setAdapter(new PageAdapter(mView.getContext(), mBitmapList));

        return mBottomSheetDialog;

    }


    @Override
    public void onResume() {
        Log.i(TAG, "[abc]onResume: dialog");
        // test data init
        generateData();
        mRecyclerView.setAdapter(new PageAdapter(mView.getContext(), mBitmapList));
        super.onResume();
    }
}
