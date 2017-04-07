package com.example.hrv;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.been.HeathyTipsBeen;
import com.bumptech.glide.Glide;
import com.example.hrv.adapter.CommonAdapter;
import com.example.hrv.viewholder.CommonViewHolder;
import com.network.RequestManager;

import java.util.ArrayList;

import rx.Subscriber;

public class FourthActivity extends Activity {
    private static final String TAG = "FourthActivity";
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置不显示标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_fourth);
        recyclerView = (RecyclerView) findViewById(R.id.rv_fourth_activity);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        findViewById(R.id.fanhui).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getHeathyTips();

    }

    private void getHeathyTips() {
        Subscriber<HeathyTipsBeen> subscriber = new Subscriber<HeathyTipsBeen>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onNext(HeathyTipsBeen heathyTipsBeen) {
                Log.d(TAG, "onNext: " + heathyTipsBeen.getData().get(0).getName());
                recyclerView.setAdapter(new CommonAdapter<HeathyTipsBeen.DataBean>(FourthActivity.this, R.layout.item_health_tips, heathyTipsBeen.getData()) {
                    @Override
                    public void convert(CommonViewHolder holder, final HeathyTipsBeen.DataBean dataBean) {
                        holder.setText(R.id.tv_healthy_tips_list_item_title, dataBean.getName());
                        if (dataBean.getResume().toString().length() > 34) {
                            holder.setText(R.id.tv_healthy_tips_list_content, dataBean.getResume().substring(0, 35) + "...");
                        }
                        holder.setText(R.id.tv_resource, dataBean.getAuthor());
                        Glide.with(FourthActivity.this)
                                .load("http://www.bajiaolv.gq/api/Article/GetImg?imgCode=" + dataBean.getId() + "-1")
                                .into((ImageView) holder.getView(R.id.iv_healthy_tips_list_pic));
                        holder.setItemOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(FourthActivity.this, HealthyTipsDetailActivity.class);
                                intent.putExtra("id", dataBean.getId());
                                startActivity(intent);
                            }
                        });

                    }
                });
            }
        };
        RequestManager.getInstance().getHealthyTips(subscriber);
    }


}
