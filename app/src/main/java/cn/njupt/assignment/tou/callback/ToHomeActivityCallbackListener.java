package cn.njupt.assignment.tou.callback;

public interface ToHomeActivityCallbackListener {
    // 当 RecordsHistoryFragment 中点击单条历史记录时，触发跳转页面操作，在 HomeActivity 实例化
    // 当 bookmarkFragment 中点击单条历史记录时，触发跳转页面操作，在 HomeActivity 实例化
    void loadUrl(String url);
    // From BarFooterFragment to HomeActivity
    // 注意，这里不是强制全屏，其它需要隐藏 bar 的调用通过这个接口
    void fullScreenWhenInput(boolean isToHide);

    // From OptionsDialog to HomeActivity
    void setGraphlessMode(int flag) ;
    // From OptionsDialog to HomeActivity
    void addBookmark();
}


