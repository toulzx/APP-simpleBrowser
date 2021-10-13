package cn.njupt.assignment.tou.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import cn.njupt.assignment.tou.R;
import cn.njupt.assignment.tou.utils.ToastUtil;

public class RecordsBookmarkFragment extends Fragment {

    private static final String TAG = RecordsBookmarkFragment.class.getSimpleName();

    private View mView;

    private AppCompatButton button;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_dialog_records_bookmark, container, false);
        }

        // 回调函数，当点击 ParentFragment 中的 Button 时，触发清除历史记录操作
        RecordsInDialogFragment.setBookmarkCallBackListener(new BookmarkCallbackListener() {
            @Override
            public void onBookmarkButtonClick(View view) {
                Log.i(TAG, "onButtonClick: 你好你好你好");
                ToastUtil.shortToast(getContext(), "别点，我怕痒~");
            }
        });

        return mView;
    }
}
