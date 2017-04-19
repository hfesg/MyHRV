package com.utils;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by xushuzhan on 2017/3/15.
 */

public class GradientUtil {
    private static final String TAG = "GradientUtil";
    public int i= -1;
    public ArrayList<Integer> Gradient_R = new ArrayList<Integer>();
    public ArrayList<Integer> Gradient_G = new ArrayList<Integer>();
    public ArrayList<Integer> Gradient_B = new ArrayList<Integer>();

    private static class GradientUtilHolder {
        private static final GradientUtil INSTANCE = new GradientUtil();
    }
    private GradientUtil (){}
    public static final GradientUtil getInstance() {
        return GradientUtilHolder.INSTANCE;
    }


    /**
     * 获取当前渐变颜色
     * @param totalDistance 颜色渐变的范围距离，单位m
     * @param startColor 渐变开始的颜色
     * @param endColor 渐变结束的颜色
     * @return
     */
    public int getGradient(int totalDistance, int startColor, int endColor) {
        i++;
        int gradient_R ;
        int gradient_G ;
        int gradient_B ;
        int delta_R = Color.red(startColor) - Color.red(endColor);
        int delta_G = Color.green(startColor) - Color.green(endColor);
        int delta_B = Color.blue(startColor) - Color.blue(endColor);
        if (i <= totalDistance) {
            gradient_R = Color.red(endColor) + delta_R * i / totalDistance;
            gradient_G = Color.green(endColor)  + delta_G * i / totalDistance;
            gradient_B = Color.blue(endColor)  + delta_B * i / totalDistance;
            Gradient_R.add(gradient_R);
            Gradient_G.add(gradient_G);
            Gradient_B.add(gradient_B);
        } else if (i <= totalDistance*2 +1) {
            gradient_R = Gradient_R.get(Gradient_R.size() - i % (totalDistance + 1) -1);
            gradient_G = Gradient_G.get(Gradient_G.size() - i % (totalDistance + 1) -1);
            gradient_B = Gradient_B.get(Gradient_B.size() - i % (totalDistance + 1) -1);
        } else {
            gradient_R = Gradient_R.get(0);
            gradient_G = Gradient_G.get(0);
            gradient_B = Gradient_B.get(0);
            Gradient_R.clear();
            Gradient_G.clear();
            Gradient_B.clear();
            i = -1;
        }
        Log.d(TAG, "addOnePolylineWithGradientColors: (" + gradient_R + "," + gradient_G + "," + gradient_B + ")" + ">>i=" + i);
        return Color.argb(255,gradient_R,gradient_G,gradient_B);
    }

    //重置i坐标
    public void clearI(){
        if(i!=-1){
            Gradient_R.clear();
            Gradient_G.clear();
            Gradient_B.clear();
            i=-1;
        }
    }
}