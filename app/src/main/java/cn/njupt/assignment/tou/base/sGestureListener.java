package cn.njupt.assignment.tou.base;

import android.view.GestureDetector;
import android.view.MotionEvent;

public abstract class sGestureListener extends GestureDetector.SimpleOnGestureListener {

    @Override
    public abstract boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY);


}
