package com.example.hrv;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.Config;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.model.Polyline;
import com.amap.api.maps2d.model.PolylineOptions;
import com.been.PathRecord;
import com.example.hrv.R;
import com.utils.TimeRecorder;
import com.utils.Utils;

import java.text.DecimalFormat;

public class ThirdActivity extends Activity implements LocationSource, AMapLocationListener, TimeRecorder.OnTimePlusOneSecondListener {
    private AMap mAMap;
    private MapView mMapView;
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    public Polyline polyline;
    private PathRecord mPathRecord;

    private ImageView stopRunning;
    private TextView runningDistance;
    private TextView runningDuration;

    private AlertDialog exitDialog;
    public LatLng oldLatlng;
    private int mDuration;
    private float mDistance;
    private TimeRecorder mTimeRecorder;

    public boolean isFirstRecord = true;
    private static final String TAG = "ThirdActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_third);
        mMapView = (MapView) findViewById(R.id.map_running);
        mMapView.onCreate(savedInstanceState);
        init();//初始化地图对象
        initView();
    }
    private void initView() {
        Button back = (Button) findViewById(R.id.fanhui);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        stopRunning = (ImageView) findViewById(R.id.iv_stop_running);
        stopRunning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        runningDistance = (TextView) findViewById(R.id.tv_begin_running_distance);
        runningDistance.setTypeface(Config.TYPEFACES.DINMITTELSCHRIFTSTD);
        runningDistance.setText("0.00");
        runningDuration = (TextView) findViewById(R.id.tv_begin_running_duration);
        runningDuration.setTypeface(Config.TYPEFACES.DINMITTELSCHRIFTSTD);
        runningDuration.setText("00:00:00");
    }

    private void init() {
        if (mAMap == null) {
            mAMap = mMapView.getMap();
            setUpMap();
        }
        mDuration = 0;
        mTimeRecorder = new TimeRecorder();
        mTimeRecorder.addOnTimePlusOneSecondListener(this);
        mTimeRecorder.addOnTimePlusOneSecondListener(this);//添加时间通知回调
    }

    /**
     * 设置一些map的属性
     */
    private void setUpMap() {
        //自定义定位的小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.pic_guilder));//设置图标
        myLocationStyle.strokeColor(Color.WHITE);//圆形边框颜色
        myLocationStyle.radiusFillColor(Color.parseColor("#5eaae6c0"));//圆形的填充颜色
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细

        mAMap.setMyLocationStyle(myLocationStyle);

        mAMap.setLocationSource(this);
        mAMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        mAMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        mAMap.moveCamera(CameraUpdateFactory.zoomTo(18));  //设置缩放级别
        mAMap.getUiSettings().setZoomControlsEnabled(false);

    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {

            mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点

            //当前的经纬度
            LatLng newLatlng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            mAMap.moveCamera(CameraUpdateFactory.changeLatLng(newLatlng));

            if (oldLatlng != newLatlng && aMapLocation.getAccuracy() <= 50) {
                //判断是否为第一次定位经纬度
                if (isFirstRecord) {
                    mTimeRecorder.switchStatus(true);
                    oldLatlng = newLatlng;
                    isFirstRecord = false;
                    //开始定位的时间，之后会放在开始跑步的方法里
                    mPathRecord = new PathRecord();
                }
                PolylineOptions polylineOptions = new PolylineOptions().add(oldLatlng, newLatlng).width(10).color(Color.parseColor("#93F9B9"));
                oldLatlng = newLatlng;
                mAMap.addPolyline(polylineOptions);
                mPathRecord.addpoint(aMapLocation);
            }
            Log.d(TAG, "onLocationChanged: 精度" + aMapLocation.getAccuracy());
            Log.d(TAG, "onLocationChanged: " + aMapLocation.getAddress());
            mDistance = Utils.getDistance(mPathRecord.getPathline());
            runningDistance.setText(Utils.saveXDecimal(2, mDistance));

        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        startLocation();
    }
    private void startLocation() {
        if (mLocationClient == null) {
            Log.d(TAG, "startLocation: ");
            mLocationClient = new AMapLocationClient(getApplicationContext());
            mLocationOption = new AMapLocationClientOption();
            // 设置定位监听
            mLocationClient.setLocationListener(this);
            // 设置为高精度定位模式(高精度模式)
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

            mLocationOption.setInterval(1500);
            // 设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，

            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mLocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        Log.d(TAG, "deactivate: ");
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
    }

    @Override
    public void onTimePlusOneSecond(int count) {
        Log.d(TAG, "onTimePlusOneSecond: " + count);
        mDuration++;
        if (runningDuration != null) {
            runningDuration.post(new Runnable() {
                @Override
                public void run() {
                    runningDuration.setText(Utils.parseTime(mDuration));
                }
            });
        }
    }
    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁

        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务
        mTimeRecorder.destroy();
    }
}
