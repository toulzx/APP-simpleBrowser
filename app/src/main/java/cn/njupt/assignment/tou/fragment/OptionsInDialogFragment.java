package cn.njupt.assignment.tou.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

import cn.njupt.assignment.tou.R;
import cn.njupt.assignment.tou.activity.HomeActivity;
import cn.njupt.assignment.tou.callback.BookmarkAddCallbackListener;
import cn.njupt.assignment.tou.callback.OptionsGraphlessModeCallbackListener;
import cn.njupt.assignment.tou.entity.Bookmark;
import cn.njupt.assignment.tou.utils.OptionSPHelper;
import cn.njupt.assignment.tou.utils.ToastUtil;
import cn.njupt.assignment.tou.viewmodel.BookmarkViewModel;

public class OptionsInDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener, SwitchCompat.OnCheckedChangeListener {

    private static final String TAG = OptionsInDialogFragment.class.getSimpleName();

    private View mView;

    private BottomSheetDialog mBottomSheetDialog;
    private BottomSheetBehavior<View> mBottomSheetBehavior;

    private ImageButton mBtnCancel;
    private ConstraintLayout mClOptionsBookmarkAdd;
    private Switch mStOrientation, mStGraphless, mStPrivate;
    private ConstraintLayout mClOptions;

    public static OptionsGraphlessModeCallbackListener mGraphlessModeCallbackListener;
    public static BookmarkAddCallbackListener mBookmarkAddCallbackListener;

    public static void SetGraphlessModeCallbackListener(OptionsGraphlessModeCallbackListener listener) {
        mGraphlessModeCallbackListener = listener;
    }
    public static void SetBookmarkAddCallbackListener(BookmarkAddCallbackListener listener) {
        mBookmarkAddCallbackListener = listener;
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
            mView = View.inflate(getContext(), R.layout.fragment_dialog_options, null);
        }

        // bind bottomSheetDialog
        mBottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        mBottomSheetDialog.setContentView(mView);

        // get and set behavior
        mBottomSheetBehavior = BottomSheetBehavior.from((View) mView.getParent());
        mBottomSheetBehavior.setHideable(true);
        mBottomSheetBehavior.setSkipCollapsed(true);

        // bind var
        mBtnCancel = mView.findViewById(R.id.options_button_cancel);
        mStOrientation = mView.findViewById(R.id.switch_orientation_lock);
        mStGraphless = mView.findViewById(R.id.switch_graphless_mode);
        mStPrivate = mView.findViewById(R.id.switch_private_mode);
        mClOptions = mView.findViewById(R.id.options_header_container);
        mClOptionsBookmarkAdd = mView.findViewById(R.id.option_bookmark_add);

        // set status
        if (!Objects.equals(OptionSPHelper.getLockOrientationValue(), "auto")) {
            mStOrientation.setChecked(true);
        }
        if (Objects.equals(OptionSPHelper.getGraphlessModeValue(), String.valueOf(true))) {
            mStGraphless.setChecked(true);
        }
        if (Objects.equals(OptionSPHelper.getPrivateModeValue(), String.valueOf(true))) {
            mStPrivate.setChecked(true);
        }

        // initialize layout based on orientation
        Configuration configuration = getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mClOptions.setVisibility(View.GONE);
        }

        // set listener
        mBtnCancel.setOnClickListener(this);
        mStOrientation.setOnCheckedChangeListener(this);
        mStGraphless.setOnCheckedChangeListener(this);
        mStPrivate.setOnCheckedChangeListener(this);
        mClOptionsBookmarkAdd.setOnClickListener(this);

        return mBottomSheetDialog;
    }

    @Override
    public void onStart() {

        super.onStart();

        //设置展开状态
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Called when a view has been clicked.
     * @param view The view that was clicked.
     */
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.options_button_cancel) {

            // 设置合起状态
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        } else if (view.getId() == R.id.option_bookmark_add) {

            mBookmarkAddCallbackListener.addBookmark();

        }

    }

    /**
     * 监听旋转状态的改变
     * @param newConfig:
     * @return void
     * @date 2021/10/15 19:09
     * @author tou
     */
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mClOptions.setVisibility(View.GONE);
        } else {
            mClOptions.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Called when the checked state of a compound button has changed.
     * @param buttonView The compound button view whose state has changed.
     * @param isChecked  The new checked state of buttonView.
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!buttonView.isPressed()) {
            return;
        }
        if (buttonView.getId() == R.id.switch_orientation_lock) {

            Configuration configuration = getResources().getConfiguration();

            if (isChecked) {

                // 如果当前是横屏，锁定横屏
                if(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    OptionSPHelper.setValue(null,"horizontal", null, null);
                    ToastUtil.shortToast(requireContext(), "锁定横屏啦");
                }
                // 如果当前是竖屏，锁定竖屏
                if(configuration.orientation==Configuration.ORIENTATION_PORTRAIT){
                    requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    OptionSPHelper.setValue(null, "vertical", null, null);
                    ToastUtil.shortToast(requireContext(), "锁定竖屏啦");
                }

            } else {
                // 如果关闭开关，则取消锁定
                requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                OptionSPHelper.setValue(null, "auto", null, null);
            }

        } else if (buttonView.getId() == R.id.switch_graphless_mode) {

            if (mGraphlessModeCallbackListener != null) {

                if (isChecked) {
                    mGraphlessModeCallbackListener.setGraphlessMode(HomeActivity.PROHIBIT_IMAGE_LOADED);
                } else {
                    mGraphlessModeCallbackListener.setGraphlessMode(HomeActivity.ALLOW_IMAGE_LOADED);
                }

                OptionSPHelper.setValue(null, null, null, String.valueOf(isChecked));

            } else {

                Log.e(TAG, "onClick: mImageBlockCallbackListener != null");

            }

        } else if (buttonView.getId() == R.id.switch_private_mode) {

            OptionSPHelper.setValue(null, null, String.valueOf(isChecked), null);

        }
    }
}
