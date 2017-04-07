package com;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;


public class Config {

    public static class TYPEFACES {
        public static Typeface DINMITTELSCHRIFTSTD;
    }

    public static void init(Context context) {//Do some inits here
        TYPEFACES.DINMITTELSCHRIFTSTD = Typeface.createFromAsset(context.getAssets(), "fonts/dinmittelschriftStd.otf");//初始化字体
    }
}
