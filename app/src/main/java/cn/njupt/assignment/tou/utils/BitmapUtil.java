package cn.njupt.assignment.tou.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Environment;
import android.os.FileUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtil {

    public static Bitmap currentActivityShot(Activity activity) {
        // 获取windows中最顶层的view
        View view = activity.getWindow().getDecorView();
        view.buildDrawingCache();

        // 获取状态栏高度
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBarHeights = rect.top;

        // 获取屏幕宽和高
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int widths = size.x;
        int heights = size.y;

        // 允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);

        // 去掉状态栏
        Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache(), 0,
                statusBarHeights, widths, heights - statusBarHeights);

        // 销毁缓存信息
        view.destroyDrawingCache();

        // test 保存照片
//        saveBitmap(String.valueOf(System.currentTimeMillis()),bmp,activity );

        return bmp;
    }




    /**
     * Save Bitmap
     *
     * @param name file name
     * @param bm  picture to save
     */
    static void saveBitmap(String name, Bitmap bm, Context mContext) {
        //指定我们想要存储文件的地址
        String TargetPath = mContext.getExternalFilesDir(Environment.DIRECTORY_DCIM) + File.separator;
        Log.i("Save Bitmap", "[abc]Save Path=" + TargetPath);
        //判断指定文件夹的路径是否存在
        if (!fileIsExist(TargetPath)) {
            Log.i("Save Bitmap", "[abc]TargetPath isn't exist");
        } else {
            //如果指定文件夹创建成功，那么我们则需要进行图片存储操作
            File saveFile = new File(TargetPath,   name + ".jpeg");

            try {
                FileOutputStream saveImgOut = new FileOutputStream(saveFile);
                // compress - 压缩的意思
                bm.compress(Bitmap.CompressFormat.JPEG, 80, saveImgOut);
                //存储完成后需要清除相关的进程
                saveImgOut.flush();
                saveImgOut.close();
                Log.i("Save Bitmap", "[abc]The picture is save to your phone!");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    /**
     * 判断指定目录的文件夹是否存在，如果不存在则需要创建新的文件夹
     * @param fileName 指定目录
     * @return 返回创建结果 TRUE or FALSE
     */
    static boolean fileIsExist(String fileName)
    {
        //传入指定的路径，然后判断路径是否存在
        File file=new File(fileName);
        if (file.exists())
            return true;
        else{
            //file.mkdirs() 创建文件夹的意思
            return file.mkdirs();
        }
    }

}
