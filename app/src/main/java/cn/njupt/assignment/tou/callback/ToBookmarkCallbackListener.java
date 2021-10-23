package cn.njupt.assignment.tou.callback;

import android.view.View;
import android.widget.TextView;

public interface ToBookmarkCallbackListener {

    // 当 RecordsInDialogFragment 中点击左下侧 Ultra Button 时，触发菜单，在 RecordsBookmarkFragment 实例化
    void onMenuButtonClick(View view);
    // 当 RecordsInDialogFragment 中点击右下侧 Cancel Button 时，若不在编辑状态，触发返回，在 RecordsBookmarkFragment 实例化
    void onBackButtonClick();
    // 当 RecordsInDialogFragment 中点击右下侧 Cancel Button 时，若在编辑状态，触发移动，在 RecordsBookmarkFragment 实例化
    void onMoveButtonClick();
    // 编辑状态下，当 RecordsInDialogFragment 中点击左上侧 CheckAll Button 时，触发全选或全不选，在 RecordsBookmarkFragment 实例化
    void onCheckAllButtonClick(TextView checked_all, TextView checked_num);
    // 编辑状态下，当 RecordsInDialogFragment 中点击右上侧 Cancel Button 时，关闭编辑状态，在 RecordsBookmarkFragment 实例化
    void onCancelEditModeButtonClick();

}
