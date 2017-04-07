package com.been;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps2d.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xushuzhan on 2016/11/16.
 * 用于记录一条轨迹，包括起点、终点、轨迹中间点、距离、耗时、平均速度、时间的been文件
 */

public class PathRecord {
    private AMapLocation mStartPoint;//起点
    private AMapLocation mEndPoint; //终点
    private List<AMapLocation> mPathLinePoints = new ArrayList<AMapLocation>();//路径集合
    private float mDistance;//距离
    private String mDuration;//持续时间
    private String mAverageSpeed;//平均速度
    private String mDate;//时间
    private int mId = 0;
    private long beginTime;
    private long endTime;
    private String studentId;
    private List<LatLng> mRunningLatlng = new ArrayList<LatLng>();//跑步的经纬度集合

    public PathRecord() {

    }

    public List<LatLng> getmRunningLatlng() {
//        for(int i = 0; i<mPathLinePoints.size(); i++ ){
//            LatLng latLng = new LatLng(mPathLinePoints.get(i).getLatitude(),mPathLinePoints.get(i).getLongitude());
//            mRunningLatlng.add(latLng);
//        }
        return mRunningLatlng;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public AMapLocation getStartPoint() {
        return mStartPoint;
    }

    public void setStartPoint(AMapLocation startpoint) {
        this.mStartPoint = startpoint;
    }

    public AMapLocation getEndpoint() {
        return mEndPoint;
    }

    public void setEndpoint(AMapLocation endpoint) {
        this.mEndPoint = endpoint;
    }

    public List<AMapLocation> getPathline() {
        return mPathLinePoints;
    }

    public void setPathLine(List<AMapLocation> pathline) {
        this.mPathLinePoints = pathline;
    }

    public float getDistance() {
        return mDistance;
    }

    public void setDistance(float distance) {
        this.mDistance = distance;
    }

    public String getDuration() {
        return mDuration;
    }

    public void setDuration(String duration) {
        this.mDuration = duration;
    }

    public String getAverageSpeed() {
        return mAverageSpeed;
    }

    public void setAverageSpeed(String averagespeed) {
        this.mAverageSpeed = averagespeed;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        this.mDate = date;
    }

    public void addpoint(AMapLocation point) {
        mPathLinePoints.add(point);
    }

    public void addLatLng(LatLng latLng){
        mRunningLatlng.add(latLng);
    }
    @Override
    public String toString() {
        return ("recordSize:" + getPathline().size() + ", ") +
                "distance:" + getDistance() + "m, " +
                "duration:" + getDuration() + "s";
    }

}
