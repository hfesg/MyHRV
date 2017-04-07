package com.example.hrv;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class HealthyTipsDetailActivity extends Activity {
    private static final String TAG = "HealthyTipsDetailActivi";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_healthy_tips_detail);
        int id = getIntent().getIntExtra("id",0);
        WebView webView = (WebView) findViewById(R.id.wv_heaithy_tips_detail);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);//适应屏幕
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        webView.loadUrl("http://www.bajiaolv.gq/api/Article/GetContext?id="+id);
         Log.d(TAG, "onCreate: "+"http://www.bajiaolv.gq/api/Article/GetContext?id="+id);
    }
}
