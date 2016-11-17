package com.playfire.livetv.utils;

import com.playfire.livetv.MyApplication;

/**
 * Created on 2016-11-17.
 *
 * @author LiuHuiJie
 */
public class ScreenUtils {


    /**
     * sp转px。
     */
    public static int sp2px(float spValue) {
        final float fontScale = MyApplication.getApplication().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

}
