package cn.njupt.assignment.tou.callback;

import android.view.View;

public interface ToHistoryCallbackListener {
    // 当 RecordsInDialogFragment 中点击 Button 时，触发清除历史记录操作，在 RecordsHistoryFragment 实例化
    void onHistoryButtonClick(View view);
}
