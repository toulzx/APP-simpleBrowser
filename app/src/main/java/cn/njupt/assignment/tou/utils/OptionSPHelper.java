package cn.njupt.assignment.tou.utils;

import static android.content.Context.MODE_PRIVATE;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一管理存储 options 的 SharedPreferences
 * @date 2021/10/14 17:49
 * @author tou
 */
public class OptionSPHelper {

    private static final String TAG = OptionSPHelper.class.getSimpleName();

    private static Application mApp;
    private static SharedPreferences mSharedPreferences;

    public static final String SP_NAME = "userSetting";

    private static final String KEY_FIRST_TIME_USED = "firstTimeUsed";
    public static final String KEY_LOCK_ORIENTATION = "lockOrientation";
    public static final String KEY_PRIVATE_MODE = "privateMode";
    public static final String KEY_GRAPHLESS_MODE = "graphlessMode";

    private static final String VALUE_FIRST_TIME_USED = "true";
    private static final String VALUE_NOT_FIRST_TIME_USED = "false";
    public static final String VALUE_DEFAULT_LOCK_ORIENTATION = "vertical";     // vertical horizontal auto
    public static final String VALUE_DEFAULT_PRIVATE_MODE = "false";
    public static final String VALUE_DEFAULT_GRAPHLESS_MODE = "false";

    public static final String VALUE_WRONG = "WRONG";


    /**
     * 应用启动后对设置参数的初始化
     * @param app:  拥有此活动的应用程序
     * @return void
     * @date 2021/10/14 17:05
     * @author tou
     */
    public static void init(Application app) {

        mApp = app;

        mSharedPreferences = mApp.getSharedPreferences(SP_NAME, MODE_PRIVATE);

        // 判断是否首次使用 App

        String firstTimeUsed = mSharedPreferences.getString(KEY_FIRST_TIME_USED, VALUE_FIRST_TIME_USED);

        if (firstTimeUsed.equals(VALUE_FIRST_TIME_USED)) {

            SharedPreferences.Editor editor = mSharedPreferences.edit();

            editor.putString(KEY_FIRST_TIME_USED, VALUE_NOT_FIRST_TIME_USED);

            editor.putString(KEY_LOCK_ORIENTATION, VALUE_DEFAULT_LOCK_ORIENTATION);
            editor.putString(KEY_PRIVATE_MODE, VALUE_DEFAULT_PRIVATE_MODE);
            editor.putString(KEY_GRAPHLESS_MODE, VALUE_DEFAULT_GRAPHLESS_MODE);

            editor.apply();

        }

    }


    /**
     * 修改 SharedPReferences 中所有的数据，不需要修改的填写 null
     * @param orientation: 屏幕方向的设定值：vertical,horizontal,auto
     * @param privateMode: 无痕模式的设定值：true,false
     * @param graphlessMode:  无图模式的设定值：true,false
     * @return void
     * @date 2021/10/14 17:15
     * @author tou
     */
    public static void setValue(String orientation, String privateMode, String graphlessMode) {

        if (mApp == null) {
            Log.e(TAG, "在使用本类方法之前，请先调用初始化函数 init() ！！！");
            return;
        }

        SharedPreferences.Editor editor = mSharedPreferences.edit();

        if (orientation != null) { editor.putString(KEY_LOCK_ORIENTATION, orientation); }
        if (privateMode != null) { editor.putString(KEY_PRIVATE_MODE, privateMode); }
        if (graphlessMode != null) { editor.putString(KEY_GRAPHLESS_MODE, graphlessMode); }

        editor.apply();

    }


    /**
     * 获取 SharedPreferences 中所有数据的值，返回 Map
     * @return java.util.Map<java.lang.String,java.lang.String>
     * @date 2021/10/14 17:31
     * @author tou
     */
    public static Map<String,String> getAllValue() {

        if (mApp == null) {
            Log.e(TAG, "getAllValue(): 在使用本类方法之前，请先调用初始化函数 init() ！！！");
            return null;
        }

        Map <String,String> map = new HashMap<>();

        map.put(KEY_LOCK_ORIENTATION, getLockOrientationValue());
        map.put(KEY_PRIVATE_MODE, getPrivateModeValue());
        map.put(KEY_GRAPHLESS_MODE, getGraphlessModeValue());

        return map;

    }

    /**
     * 返回屏幕方向的设定值：vertical,horizontal,auto
     * @return java.lang.String
     * @author tou
     */
    public static String getLockOrientationValue() {

        if (mApp == null) {
            Log.e(TAG, "getLockOrientationValue(): 在使用本类方法之前，请先调用初始化函数 init() ！！！");
            return null;
        }

        return mSharedPreferences.getString(KEY_LOCK_ORIENTATION, VALUE_WRONG);

    }

    /**
     * 返回无痕模式的设定值：true,false
     * @return java.lang.String
     * @author tou
     */
    public static String getPrivateModeValue() {

        if (mApp == null) {
            Log.e(TAG, "getPrivateModeValue(): 在使用本类方法之前，请先调用初始化函数 init() ！！！");
            return null;
        }

        return mSharedPreferences.getString(KEY_PRIVATE_MODE, VALUE_WRONG);

    }

    /**
     * 返回无图模式的设定值：true,false
     * @return java.lang.String
     * @author tou
     */
    public static String getGraphlessModeValue() {

        if (mApp == null) {
            Log.e(TAG, "getGraphlessModeValue(): 在使用本类方法之前，请先调用初始化函数 init() ！！！");
            return null;
        }

        return mSharedPreferences.getString(KEY_GRAPHLESS_MODE, VALUE_WRONG);

    }

}
