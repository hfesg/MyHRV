package com.hrv;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.ScatterChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.hrv.R;

public class XindianActivity extends Activity {

	private Context context;// 用于获取上下文对象
	private Button back;

	// 心电原始图
	private LinearLayout mLayout;
	private XYSeries series, Rseries;// XY数据点r
	private XYMultipleSeriesDataset mDataset;// XY轴数据集
	private GraphicalView mViewChart;// 用于显示现行统计图
	private XYMultipleSeriesRenderer mXYRenderer;// 线性统计图主描绘器
	private String title = "心电";
	private int X = 1500;// X数据集大小
	private int Y, y;// = 3000;//
	private float[] origxindian = new float[3000];// 存储心电值
	private float[] xin = new float[2800]; // 存储画图的点
	private int len = 200;
	private int t = 0;
	private int k = 0;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.xindian);
		
		back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				XindianActivity.this.finish();
			}
		});
		
		context = getApplicationContext();// 获取上下文对象
		// 心电波形显示
		mLayout = (LinearLayout) findViewById(R.id.fenxichart);// 这里获得xy_chart的布局，下面会把图表画在这个布局里面
		series = new XYSeries(title);// 这个类用来放置曲线上的所有点，是一个点的集合，根据这些点画出曲线
		Rseries = new XYSeries("R波定位");
		mDataset = new XYMultipleSeriesDataset(); // 创建一个数据集的实例，这个数据集将被用来创建图表
		mDataset.addSeries(series);// 将点集添加到这个数据集中
		mDataset.addSeries(0, Rseries);
		int color = Color.BLUE;// 设置颜色
		PointStyle style = PointStyle.POINT;// 设置点的风格
		mXYRenderer = buildRenderer(color, style, true);
		mXYRenderer.setShowGrid(true);// 显示表格
		mXYRenderer.setGridColor(Color.GREEN);// 设置背景表格颜色 绿色，
		mXYRenderer.setXLabels(10); // 设置X轴标签字体大小
		mXYRenderer.setYLabels(10);
		mXYRenderer.setYLabelsAlign(Align.RIGHT);// 右对齐
		mXYRenderer.setShowLegend(false);// 不显示图例
		mXYRenderer.setZoomEnabled(false);
		mXYRenderer.setPanEnabled(true, false);
		mXYRenderer.setClickEnabled(false);
		mXYRenderer.setPointSize(5f);
		XYSeriesRenderer R_Renderer = new XYSeriesRenderer();
		R_Renderer.setColor(Color.RED);
		R_Renderer.setPointStyle(PointStyle.X);
		R_Renderer.setFillPoints(true);
		mXYRenderer.addSeriesRenderer(0, R_Renderer);
		setChartSettings(mXYRenderer, "", "时间", "幅度", 0, X, -20000, 40000,
				Color.WHITE, Color.WHITE);// 这个是采用官方APIdemo提供给的方法
											// 设置好图表的样式
		String[] types = new String[] { ScatterChart.TYPE, LineChart.TYPE };
		mViewChart = ChartFactory.getCombinedXYChartView(context, mDataset,
				mXYRenderer, types);// 通过ChartFactory生成图表

		mLayout.addView(mViewChart, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));// 将图表添加到布局中去
		
		
		Intent intent = getIntent();
		Bundle mBundle = intent.getExtras();
		origxindian = mBundle.getFloatArray("origxindian");
		
		for (int i = 0; i < xin.length; i++) {
			
			float sumYnn = 0;
			float meanYnn = 0;
			
			for (int j = len - 100; j < len; j++) {
				sumYnn = sumYnn + origxindian[j];
			}
			//一百个点的平均值
			meanYnn = (float) ((sumYnn / 100.0));
			if (len < origxindian.length) {
				if(k > 0 && k < xin.length){
					xin[t] = (origxindian[len] - meanYnn);
				}
				series.add(k, (xin[t]));
				len++;
				k++;
				t++;
			} else {
				return;
			}
		}
		mViewChart.invalidate();
	}

	protected XYMultipleSeriesRenderer buildRenderer(int color,
			PointStyle style, boolean fill) {// 设置图表中曲线本身的样式，包括颜色、点的大小以及线的粗细等
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		XYSeriesRenderer r = new XYSeriesRenderer();
		r.setColor(color);
		r.setPointStyle(style);
		r.setFillPoints(fill);
		r.setLineWidth(3);
		renderer.addSeriesRenderer(r);

		return renderer;
	}

	protected void setChartSettings(XYMultipleSeriesRenderer renderer,
			String title, String xTitle, String yTitle, double xMin,
			double xMax, double yMin, double yMax, int axesColor,
			int labelsColor) {// 设置主描绘器的各项属性，详情可阅读官方API文档
		renderer.setChartTitle(title);
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);
		renderer.setXAxisMin(xMin);
		renderer.setXAxisMax(xMax);
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		renderer.setAxesColor(axesColor);
		renderer.setLabelsColor(labelsColor);
	}
}
