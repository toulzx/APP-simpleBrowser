package cn.njupt.assignment.tou.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import cn.njupt.assignment.tou.R;
import cn.njupt.assignment.tou.callback.InputStatusCallbackListener;

public class BarFooterFragment extends Fragment {

    private View mView;

    public static InputStatusCallbackListener mInputStatusCallbackListener;

    public static void SetInputStatusCallbackListener(InputStatusCallbackListener listener) {
        mInputStatusCallbackListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_bar_footer, container, false);
        }

        ConstraintLayout constraintLayout = mView.findViewById(R.id.bar_footer_container);
        constraintLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom - oldBottom < 0) {
                    mInputStatusCallbackListener.fullScreen(true);
                }
            }
        });

        return mView;
    }
}
