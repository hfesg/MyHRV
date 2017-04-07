package com.network;

import com.been.HeathyTipsBeen;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by xushuzhan on 2017/3/23.
 */

public class RequestManager {


    private static final int DEFAULT_TIMEOUT = 10;

    private Retrofit retrofit;

    private ApiService apiServerce;

    //构造方法私有
    private RequestManager() {
        //手动创建一个OkHttpClient并设置超时时间
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        retrofit = new Retrofit.Builder()
                .client(httpClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(API.BASEURL)
                .build();

        apiServerce = retrofit.create(ApiService.class);

    }

    //单例
    private static class SingletonHolder {
        private static final RequestManager INSTANCE = new RequestManager();
    }

    //获取单例
    public static RequestManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 用于获取健康贴士列表
     *
     * @param subscriber 调用时传过来的观察者对象
     */

    public void getHealthyTips(Subscriber<HeathyTipsBeen> subscriber) {
        apiServerce.getTipsList()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }



}
