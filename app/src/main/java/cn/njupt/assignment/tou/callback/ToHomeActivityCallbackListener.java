package cn.njupt.assignment.tou.callback;

public interface ToHomeActivityCallbackListener {
    // 当 RecordsHistoryFragment 中点击单条历史记录时，触发跳转页面操作，在 HomeActivity 实例化
    // 当 bookmarkFragment 中点击单条历史记录时，触发跳转页面操作，在 HomeActivity 实例化
    void loadUrl(String url);
    // From OptionsDialog to HomeActivity
    void setGraphlessMode(int flag) ;
    // From OptionsDialog to HomeActivity
    void addBookmark();
    // save webPage
    void saveWebPage();
}


