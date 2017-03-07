package com.Rwave;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

public class XYChartView extends View{
	 
	int dateY[] = new int[]{10,20,30,40,50,60};
	float[] data_power ;// = new float[] {1.2f,1.3f,2.3f} ; 
	float RRmean;
	public XYChartView(Context context,float[] xydata,float junzhi) {
		super(context, null);
		data_power = xydata;
		RRmean = junzhi;
		}

		public XYChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		}
		/*
		* 自定义控件一般都会重载onDraw(Canvas canvas)方法，来绘制自己想要的图形
		*/
		@Override
		public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint linePaint = new Paint();
		Paint linePaint1 = new Paint();
		Paint textPaint = new Paint();
		Paint xyChartPaint = new Paint();
		Paint chartLinePaint = new Paint();
		
		xyChartPaint.setStrokeWidth((float) 2.0);
		
		//设置绘制模式为-虚线作为背景线。   
		PathEffect effect = new DashPathEffect(new float[] { 6, 6, 6, 6, 6}, 2);

		//背景虚线路径.   
		Path path = new Path();  

		//只是绘制的XY轴 
		linePaint.setStyle(Style.STROKE);   
		linePaint.setStrokeWidth((float)1.0);   
		linePaint.setColor(Color.BLACK); //设置为黑笔  
		linePaint.setAntiAlias(true);// 锯齿不显示   
		//XY刻度上的字 
		textPaint.setStyle(Style.FILL);// 设置非填充   
		textPaint.setStrokeWidth(1);// 笔宽5像素   
		textPaint.setColor(Color.BLACK);// 设置为黑笔   
		textPaint.setAntiAlias(true);// 锯齿不显示   
		textPaint.setTextAlign(Align.CENTER);   
		textPaint.setTextSize(18);  
		//绘制XY轴上的字：Y开关状态、X时间 
		xyChartPaint.setStyle(Style.FILL);   
		xyChartPaint.setStrokeWidth(1);   
		xyChartPaint.setColor(Color.BLUE);   
		xyChartPaint.setAntiAlias(true);   
		xyChartPaint.setTextAlign(Align.CENTER);   
		xyChartPaint.setTextSize(28);    
		//绘制的折线 
		chartLinePaint.setStyle(Style.FILL);   
		chartLinePaint.setStrokeWidth(1);   
		chartLinePaint.setColor(Color.BLUE);//(1)绿色   
		chartLinePaint.setAntiAlias(true);   

		//基准点。   
		float gridX = 40;   
		float gridY = getHeight();   
		//XY间隔。   
		float xSpace = (float) (getWidth()/256);   
		//画Y轴(带箭头)。   
		canvas.drawLine(gridX, gridY-20-10, gridX, 10, linePaint);   
		canvas.drawLine(gridX, 10, gridX-6, 14+10, linePaint);//Y轴箭头。   
		canvas.drawLine(gridX, 10, gridX+6, 14+10, linePaint);   
		//画Y轴名字。 
		//由于是竖直显示的，先以原点顺时针旋转90度后为新的坐标系 
		canvas.rotate(-90); 
		//当xyChartPaint的setTextAlign（）设置为center时第二、三个参数代表这四个字中点所在的xy坐标 
		canvas.drawText("RR", -((float)(getHeight()-60)-15-5 - 1/((float)1.6*1) * (getHeight()-60)), gridX-15, xyChartPaint);   
		canvas.rotate(90); //改变了坐标系还要再改过来  
		float y = 0,y1=0; 
		//画X轴。   
		y = gridY-20;   
		canvas.drawLine(gridX, y-10, getWidth(), y-10, linePaint);//X轴.   
		canvas.drawLine(getWidth(), y-10, getWidth()-14, y-6-10, linePaint);//X轴箭头。   
		canvas.drawLine(getWidth(), y-10, getWidth()-14, y+6-10, linePaint);   
		//画背景虚线，一条(因为除去了X轴)，画Y轴刻度 
		y = (float)(getHeight()/2);//-15-5 - 1/((float)1.6*1) * (getHeight()+20);//虚线的Y，开关是开的时候的Y。   
		linePaint.setPathEffect(effect);//设法虚线间隔样式。   

		//画除X轴之外的------背景虚线一条-------       
		path.moveTo(gridX, y);//背景【虚线起点】。   
		path.lineTo(getWidth(), y);//背景【虚线终点】。   
		canvas.drawPath(path, linePaint);    
		  
		//画Y轴刻度。   
		canvas.drawText("0", gridX-6-7, gridY-20, textPaint); 
		canvas.drawText("间期", gridX-6-9, y+15, textPaint); 
		canvas.drawText("平均", gridX-6-9, y, textPaint); 
		   
		//绘制X刻度坐标。   
		float x = 0;   
		//if(dateX[0] != null) { //用X来判断，就是用来如果刚开始的点数少于7个则从左到右递增，从而没有了刚开始的几个虚点；（因为X和Y的数组初始化时都没赋值，所以刚开始的时候用这个就可以判断数组中到底几个点）  
//		    for(int n = 0; n < dateX.length; n++) {   
//		        //取X刻度坐标.   
//		        x = gridX + (n) * xSpace;//在原点(0,0)处也画刻度（不画的话就是n+1）,向右移动一个跨度。   
//		        //画X轴具体刻度值。   
//		        if(dateX[n] != null) {         
		//canvas.drawLine(x, gridY-30, x, gridY-18, linePaint);//短X刻度。   
//		            canvas.drawText(dateX[n], x, gridY+5, textPaint);//X具体刻度值。 
//		        }   
//		    }   
		//}   
		   
		//起始点。   
		float lastPointX = 40; //前一个点  
		float lastPointY = (float) (data_power[0]*2/1000.0*getHeight()/(RRmean*2/1000.0));   
		float currentPointX = 0;//当前点 
		float currentPointY = 0;   
	  
		    //1.绘制折线。   
		    for(int n = 1; n < data_power.length; n++) {   
		        //get current point   
		        currentPointX =  n * xSpace + gridX;   
		        currentPointY =  (float) (data_power[n]*2/1000.0*getHeight()/(RRmean*2/1000.0));   
                
		        canvas.drawLine(lastPointX, lastPointY, currentPointX, currentPointY, chartLinePaint);//第一条线[蓝色]       
                
		      
		        lastPointX = currentPointX;   
		        lastPointY = currentPointY;   
		    }   
		  
		//画X轴名字。   
		canvas.drawText("时间", getWidth()-30, getHeight()-40, xyChartPaint); 							
		}	
}
