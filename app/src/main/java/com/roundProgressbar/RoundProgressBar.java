package com.roundProgressbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.example.hrv.R;

public class RoundProgressBar extends View {

	private Paint defaultRoundPaint;
	public static Paint progresRoundPaint;
	private Paint progresRoundPaint2;
	private Paint percenTextPaint;
	private int defaultRoundColor;
	private int roundProgressColor;
	private int roundProgressWidth;
	private int percenTextColor;
	private float percenTextSize;
	private float roundWidth;
	private int max;
	private int progress;
	private RectF oval;

	public RoundProgressBar(Context context) {
		this(context, null);
	}

	public RoundProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RoundProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		// 加载xml自定义属性
		int green = getResources().getColor(R.color.green);
		TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
				R.styleable.RoundProgressBar);
		defaultRoundColor = mTypedArray.getColor(
				R.styleable.RoundProgressBar_defaultRoundColor, Color.WHITE);
		roundProgressColor = mTypedArray.getColor(
				R.styleable.RoundProgressBar_roundProgressColor, green);
		percenTextColor = mTypedArray.getColor(
				R.styleable.RoundProgressBar_percenTextColor, Color.WHITE);
		percenTextSize = mTypedArray.getDimension(
				R.styleable.RoundProgressBar_percenTextSize, 38);

		Util.dip2px(
				context,
				roundWidth = mTypedArray.getDimension(
						R.styleable.RoundProgressBar_roundWidth, 25));
		max = mTypedArray.getInteger(R.styleable.RoundProgressBar_max, 60);
		mTypedArray.recycle();// 使用缓存
		// 初始化
		initProgressBar();

	}

	@SuppressLint("NewApi")
	private void initProgressBar() {
		setLayerType(LAYER_TYPE_SOFTWARE, null);

		defaultRoundPaint = new Paint();
		defaultRoundPaint.setAntiAlias(true);
		defaultRoundPaint.setColor(defaultRoundColor);
		defaultRoundPaint.setStyle(Paint.Style.STROKE);
		defaultRoundPaint.setStrokeWidth(roundWidth);

		progresRoundPaint = new Paint();
		progresRoundPaint.setAntiAlias(true);
		progresRoundPaint.setStyle(Paint.Style.STROKE);
		progresRoundPaint.setStrokeWidth(roundWidth);

		progresRoundPaint2 = new Paint();
		progresRoundPaint2.setAntiAlias(true);
		progresRoundPaint2.setStyle(Paint.Style.STROKE);
		progresRoundPaint2.setStrokeWidth(roundWidth);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 计算中心点
		int centre = getWidth() / 2;
		// 计算半径
		int radius = (int) (centre - roundWidth / 2);
		// 绘制默认环形进度
		canvas.drawCircle(centre, centre, radius, defaultRoundPaint);

		// 绘制环形进度
		if (oval == null) {
			oval = new RectF(centre - radius, centre - radius, centre + radius,
					centre + radius);
		}
		progresRoundPaint.setColor(roundProgressColor);

		canvas.drawArc(oval, -90, 360 * progress / max, false,
				progresRoundPaint);
	}
	
	public void setMax(int max) {
		if (max < 0) {
			max = 60;
		}
		this.max = max;
	}
	
	public void setProgress(int progress) {
		if (progress < 0) {
			progress = 0;
		}
		if (progress > max) {
			progress = max;
		}
		if (progress <= max) {
			this.progress = progress;
			
			postInvalidate();
		}
	}
	
	public void setRoundWidth(float roundWidth) {
		this.roundWidth = roundWidth;
	}
	
	public void setDefaultRoundColor(int cricleColor) {
		this.defaultRoundColor = cricleColor;
	}
	
	public void setRoundProgressColor(int roundProgressColor) {
		this.roundProgressColor = roundProgressColor;
	}
	
	public void setPercenTextColor(int percenTextColor) {
		this.percenTextColor = percenTextColor;
	}

	public void setPercenTextSize(float percenTextSize) {
		this.percenTextSize = percenTextSize;
	}

}
