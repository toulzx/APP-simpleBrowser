package cn.njupt.assignment.tou.fragment;

import android.app.Dialog;
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
import cn.njupt.assignment.tou.utils.OptionSPHelper;

public class OptionsInDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener, SwitchCompat.OnCheckedChangeListener {

    private static final String TAG = OptionsInDialogFragment.class.getSimpleName();

    private View mView;

    private BottomSheetDialog mBottomSheetDialog;
    private BottomSheetBehavior<View> mBottomSheetBehavior;

    private ImageButton mBtnCancel;
    private ConstraintLayout mCtOrientation;
    private Switch mStOrientation;

    public static OptionsImageBlockCallbackListener mImageBlockCallbackListener;

    public static void SetImageBlockCallbackListener(OptionsImageBlockCallbackListener listener) {
        mImageBlockCallbackListener = listener;
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
        mCtOrientation = mView.findViewById(R.id.option_graphless_mode);
        mStOrientation = mView.findViewById(R.id.switch_graphless_mode);

        if (Objects.equals(OptionSPHelper.getGraphlessModeValue(), String.valueOf(true))) {
            mStOrientation.setChecked(true);
        }

        mBtnCancel.setOnClickListener(this);
        mCtOrientation.setOnClickListener(this);
        mStOrientation.setOnCheckedChangeListener(this);

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
    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.options_button_cancel) {

            //设置合起状态
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        } else if (view.getId() == R.id.option_graphless_mode) {

            if (mStOrientation.isChecked()) {
                mImageBlockCallbackListener.setImageBlock(HomeActivity.PROHIBIT_IMAGE_LOADED);
            } else {
                mImageBlockCallbackListener.setImageBlock(HomeActivity.ALLOW_IMAGE_LOADED);
            }

            mStOrientation.setChecked(!mStOrientation.isChecked());
            OptionSPHelper.setValue(null, null, String.valueOf(mStOrientation.isChecked()));

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
        if (buttonView.getId() == R.id.switch_graphless_mode) {

            if (mImageBlockCallbackListener != null) {

                if (isChecked) {
                    mImageBlockCallbackListener.setImageBlock(HomeActivity.PROHIBIT_IMAGE_LOADED);
                } else {
                    mImageBlockCallbackListener.setImageBlock(HomeActivity.ALLOW_IMAGE_LOADED);
                }

                OptionSPHelper.setValue(null, null, String.valueOf(isChecked));

            } else {

                Log.e(TAG, "onClick: mImageBlockCallbackListener != null");

            }

        }
    }
}
