package com.network;

import com.been.HeathyTipsBeen;

import retrofit2.http.GET;
import rx.Observable;


/**
 * Created by xushuzhan on 2017/3/23.
 */

public interface ApiService {
    //获取文章列表
    @GET("api/Article")
    Observable<HeathyTipsBeen> getTipsList();

}
