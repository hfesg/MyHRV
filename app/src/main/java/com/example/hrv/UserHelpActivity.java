package com.example.hrv;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * Created by xushuzhan on 2017/4/11.
 */

public class UserHelpActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_window_hlep);
    }
}
