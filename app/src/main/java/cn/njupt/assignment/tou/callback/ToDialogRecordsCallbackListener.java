package cn.njupt.assignment.tou.callback;


import android.widget.TextView;

public interface ToDialogRecordsCallbackListener {
    // 当 bookmarkFragment 进入子文件夹时，通知 RecordsInDialogFragment 刷新 viewPager2 的 fragment 元素（bookmarkFragment）以及修改相应按钮文字标签
    void refreshFragment(int currentFolderIndex, String upperFolderName);
    // 当 bookmarkFragment 切换编辑模式时，通知 RecordsInDialogFragment 修改按钮文字标签
    void resetBtnsText(boolean isEditMode);
    // 当 bookmarkFragment 切换编辑模式时，通知 RecordsInDialogFragment, 获取 checked_num 控件给 adapter
    TextView getCheckedNumTv();
    // from HomeActivity to RecordsDialog
    void hideDialog();
}
