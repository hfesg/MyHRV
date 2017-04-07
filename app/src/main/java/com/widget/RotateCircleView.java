package com.widget;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;


import com.Config;

import java.text.DecimalFormat;

import static android.graphics.Paint.Cap.ROUND;

/**
 * Created by xushuzhan on 2016/12/3.
 */

public class RotateCircleView extends ImageView {
    private static final String TAG = "CircleView";
    float i = 0f;
    float speed = 1.5f; //旋转的速度
    float x = 0f;   //控制旋转的自变量

    Matrix mMatrix;
    Paint mPaint;
    SweepGradient mShader;
    RectF rectF;

    float width; //View的宽度
    float height;//View的高度

    float layout_width;//View所占空间的宽度
    float layout_height;//View所占空间的高度

    float center_beginX;//在中心绘制矩形的时候的起始X坐标
    float center_beginY;//在中心绘制矩形的时候的起始Y坐标

    BlurMaskFilter filter1;
    BlurMaskFilter filter2;
    // 渐变圆周颜色数组
    int[] gradientColorArray = new int[]{Color.parseColor("#E29587"),Color.parseColor("#D66D75")};
    //每种渐变颜色相对的比重
    float positions[] = {0.6f, 1.0f};

    String timeRest = "00.00";//设置剩余时间
    
    float textSize;

    public RotateCircleView(Context context) {
        this(context, null);
    }

    public RotateCircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RotateCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public RotateCircleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPaint == null) {
            width = height = Math.min(getMeasuredHeight(), getMeasuredWidth());
            layout_width = getWidth();
            layout_height = getHeight();
            center_beginX = layout_width / 2 - width / 2 + 50;
            center_beginY = layout_height / 2 - height / 2 + 50;

            mMatrix = new Matrix();
            mPaint = new Paint();
            filter1 = new BlurMaskFilter(width / 50, BlurMaskFilter.Blur.NORMAL);
            filter2 = new BlurMaskFilter(1, BlurMaskFilter.Blur.NORMAL);
            rectF = new RectF(center_beginX, center_beginY, center_beginX + width - 100, center_beginY + height - 100);
            mShader = new SweepGradient(layout_width / 2, layout_height / 2, gradientColorArray, positions);
        }
        mPaint.setStrokeWidth(width / 38);
        mPaint.setColor(Color.parseColor("#8954AA"));
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        i += speed;

        //画渐变的弧
        mPaint.reset();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.LTGRAY);
        mPaint.setStrokeWidth(width / 38);
        //设置渐变类型：SweepGradient为梯度渐变
        mPaint.setShader(mShader);
        //canvas.save()以上的部分不会被旋转
        canvas.save();
        mMatrix.setRotate(i, layout_width / 2, layout_height / 2);
        canvas.concat(mMatrix);
        //画渐变的弧
        //canvas.drawArc(new RectF(50,50,width-50,height-50),180,360,false,paint);
        canvas.drawArc(rectF, 180, 360, false, mPaint);
        //画旋转的小圆圈
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#D66D75"));


        mPaint.setMaskFilter(filter1);
        canvas.drawCircle(center_beginX + width - 100, center_beginY + (height / 2) - 50, width / 26, mPaint);
//        mPaint.setMaskFilter(filter2);
//        canvas.drawCircle(center_beginX + width - 100, center_beginY + (height / 2) - 50, width / 26, mPaint);

        i = i == 360 ? 0 : i;
        canvas.restore();

        mPaint.reset();

        mPaint.setTextSize(width / 4f);
        mPaint.setColor(Color.parseColor("#130F56"));
        mPaint.setAntiAlias(true);
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float textHeight = (-fontMetrics.ascent - fontMetrics.descent) / 2;
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setStrokeCap(ROUND);
        mPaint.setTypeface(Config.TYPEFACES.DINMITTELSCHRIFTSTD);
        canvas.drawText("04:34", layout_width / 2, layout_height / 2 + textHeight, mPaint);

        mPaint.reset();
        mPaint.setColor(Color.parseColor("#878DAC"));
        mPaint.setTextSize(width / 20);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mPaint.setAntiAlias(true);
        mPaint.setTextAlign(Paint.Align.CENTER);

        canvas.drawText("剩余时间", layout_width / 2, (layout_height + 50) / 4, mPaint);

//        mPaint.setTextSize(width / 10);
//        mPaint.setTypeface(Config.TYPEFACES.DINMITTELSCHRIFTSTD);
//        mPaint.setColor(Color.parseColor("#ABB0CD"));
//        canvas.drawText("KM", layout_width / 2, layout_height - height / 4.5f, mPaint);
        postInvalidateDelayed(20);
    }

    public void setTimeRest(String min ,String sec) {
        this.timeRest = min+":"+sec;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
