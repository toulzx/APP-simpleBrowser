package cn.njupt.assignment.tou.callback;

// 注意，这里不是强制全屏，其它需要隐藏 bar 的调用通过这个接口
public interface InputStatusCallbackListener {
    void fullScreen(boolean isToHide);
}
