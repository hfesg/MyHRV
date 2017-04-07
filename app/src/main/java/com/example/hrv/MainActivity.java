package com.example.hrv;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Config;
import com.historyActivity.HistoryActivity;
import com.hrv.HRV;
import com.login.ClientUtil;
import com.login.LoadingActivity;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

public class MainActivity extends FragmentActivity {
    String uername;
    //四个模块
    LinearLayout first; //蕉绿一测试，精神压力测试
    LinearLayout second;	//数据分析
    LinearLayout third;		//运动减压
    LinearLayout fourth;	//健康小贴士

    ImageView user;//用户中心

    TextView total;//总里程
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        getData();
        initFourView();
    }

    public void getData() {
        Intent intent = getIntent();
        uername = intent.getStringExtra("user_name");
    }

    //初始化4个模块
    private void initFourView() {
        first = (LinearLayout) findViewById(R.id.first);
        first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, FirstActivity.class);
                intent.putExtra("user_name", uername);
                startActivity(intent);
            }
        });
        second = (LinearLayout) findViewById(R.id.second);
        second.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 启动等待对话框
                Intent intent = new Intent(MainActivity.this, LoadingActivity.class);
                intent.putExtra("loadMsg", "正在查询...");
                startActivity(intent);
                // 访问服务器,并将历史日期传递至记录界面
                queryDate();
            }
        });
        third = (LinearLayout) findViewById(R.id.third);
        third.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ThirdActivity.class));
            }
        });
        fourth = (LinearLayout) findViewById(R.id.fource);
        fourth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,FourthActivity.class));
            }
        });
        user = (ImageView) findViewById(R.id.iv_user);
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, HRV.class);
                intent.putExtra("user_name", uername);
                startActivity(intent);

            }
        });

        total = (TextView) findViewById(R.id.tv_total);
        total.setTypeface(Config.TYPEFACES.DINMITTELSCHRIFTSTD);
    }

    /**
     * 访问服务器，查询历史检测所有日期
     */
    private void queryDate() {

        String url = "QueryDateServlet";

        ClientUtil.post(url, null, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                if (statusCode == 200) {

                    // 获取服务器响应--历史检测所有日期，如："2016年03月20日:2016年03月25日:..."
                    String dateStr = new String(responseBody);
                    Log.i("info", "日期：" + dateStr);

                    if (dateStr.equals("登录超时，请重新登录！")) {
                        Toast.makeText(MainActivity.this, "查询失败，请重试！", Toast.LENGTH_LONG)
                                .show();
                        // 发送广播，关闭等待框
                        Intent mIntent = new Intent();
                        mIntent.setAction("com.hrv.load.FINISH");
                        sendBroadcast(mIntent);
                        return;
                    } else {
                        // 发送广播，关闭等待框
                        Intent intent = new Intent();
                        intent.setAction("com.hrv.load.FINISH");
                        sendBroadcast(intent);
                        // 跳转至历史查询界面
                        Intent intent2 = new Intent(MainActivity.this,
                                HistoryActivity.class);
                        intent2.putExtra("dateStr", dateStr);
                        startActivity(intent2);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] responseBody, Throwable error) {
                error.printStackTrace();
            }
        });
    }
}
