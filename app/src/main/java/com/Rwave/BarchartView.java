package com.Rwave;



import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class BarchartView extends View{
	//private float[] data_screen ;  
    private float[] data_power ;  
    private float[] data_total ; 
    private float[] data_point;
    private int flag ;  
    private int margin ;  
  
   // private BarChart barchart ;  
      private Paint paint ;  
      public BarchartView(Context context,float[] bardata,float[] pointdata) {  
   	          super(context) ;  
   	            
   	           margin = 0 ;  
   	       //   barchart = new BarChart() ;  
   	       //   data_screen = new float[] {90, 65, 80, 115 } ;  
   	          data_power = bardata;  
   	          data_point = pointdata;
   	          data_total = new float[4] ;  
   	          for (int i = 0; i < 4; i++)  
   	              data_total[i] =  data_power[i]/2 ;  
   	          paint = new Paint() ;  
   	          paint.setAntiAlias(true) ;  
   	    }  
   	    
   	    //绘制坐标轴  
   	   public void drawAxis(Canvas canvas) {  
   	           paint.setColor(Color.BLACK) ;  
   	           paint.setStrokeWidth((float) 2.0) ;  
   	           paint.setTextSize(30);
   	         //作图xy轴
   	           canvas.drawLine(30, getHeight()-50, getWidth()/2-10, getHeight()-50, paint) ;  
   	           canvas.drawLine(30, 20, 30, getHeight()-40, paint) ;  
   	         //  canvas.drawLine(305, 295, 310, 300, paint) ;
   	           
   	        //绘制箭头 x轴
	           canvas.drawLine(getWidth()/2-15, getHeight()-55, getWidth()/2-10, getHeight()-50, paint);
	           canvas.drawLine(getWidth()/2-15, getHeight()-45, getWidth()/2-10, getHeight()-50, paint);
	         //绘制箭头 y轴
	           canvas.drawLine(25, 25, 30, 20, paint) ;
	           canvas.drawLine(35, 25, 30, 20, paint) ;
   	    
   	          int x = getWidth()/10 - 7 ;  
   	          int y = getHeight()-20 ;  
   	  
   	        for (int i = 0; i < 4; i++) {  
   	         switch (i) {
				case 0:canvas.drawText("TP", x, getHeight()-20, paint) ; break; 
				case 1:canvas.drawText("VLF", x, getHeight()-20, paint) ; break;
				case 2:canvas.drawText("LF", x, getHeight()-20, paint) ; break;
				case 3:canvas.drawText("HF", x, getHeight()-20, paint); break;
				default:
					break;
				}
   	          
   	               x += getWidth()/10 ;  
   	         }  

   	   //右图 xy轴
 	          canvas.drawLine(getWidth()/2+10, getHeight()-50, getWidth()-10, getHeight()-50, paint) ; 
 	          canvas.drawLine(getWidth()/2+10, 20, getWidth()/2+10, getHeight()-50, paint) ;
 	  //绘制箭头 x轴
	           canvas.drawLine(getWidth()-15, getHeight()-55, getWidth()-10, getHeight()-50, paint);
	           canvas.drawLine(getWidth()-15, getHeight()-45, getWidth()-10, getHeight()-50, paint);
	 //绘制箭头 y轴
	           canvas.drawLine(getWidth()/2+5, 25, getWidth()/2+10, 20, paint) ;
	           canvas.drawLine(getWidth()/2+15, 25, getWidth()/2+10, 20, paint) ;     
	//绘制45度的斜线
	           paint.setStrokeWidth(1) ; 
	           canvas.drawLine(getWidth()/2+10, getHeight()-50, getWidth()/2+360, getHeight()-400, paint) ;
	           int m = getWidth()/2+10 ;  
	   	       int n = getHeight()-50  ;  
	   	  
	   	        for (int i = 0; i < 5; i++) {  
	   	              canvas.drawText(200 * (i) + "", m, getHeight()-20, paint) ;  
	   	               m += getWidth()/10  ;  
	   	         }  
	   	         for (int i = 0; i < 4; i++) {  
	   	        	 n -= (getHeight()-50)/5;
	   	             canvas.drawText(200 * (i+1) + "", getWidth()/2+10, n, paint) ;  
	   	                
	   	           }  
   	        
   	       }  
   	    
   	      //绘制图表  
   	      public void drawChart(Canvas canvas) {     	        
   	              int temp = getWidth()/11 ;  
   	              for (int i = 0; i < 4; i++) { 
   	            	 switch (i) {
						case 0:
						case 2:paint.setColor(Color.GREEN) ; break;
						case 1:
						case 3: paint.setColor(Color.BLUE) ; break;
						default:
							break;
						}
//   	            	barchart.setH(data_total[i]) ;  
//   	            	barchart.setX(temp + 10 * 2 + margin) ;  
//   	            	barchart.drawSelf(canvas, paint) ;  
//   	                   margin = 20 ;  
//   	                 temp = barchart.getX() ; 
   	              canvas.drawRect(temp + margin, getHeight()-50 - data_total[i], 40 + temp  + margin, getHeight()-50 - 1, paint) ; 
   	            //  margin =30;
   	              temp = temp + getWidth()/10 + margin;
   	               }  
   	          }  
   	   public void drawHighLines(Canvas canvas) {  
   		 
	           paint.setColor(Color.BLACK) ;
	           paint.setAntiAlias(true);
	           paint.setStrokeWidth(3);  
	           for (int i = 0; i < data_point.length-1; i=i+2) {  
	              canvas.drawPoint((getWidth()/2+10)+data_point[i]*(getWidth()/2-10)/1000, (getHeight()-50)-data_point[i+1]*(getWidth()/2-10)/1000, paint);  
//	              canvas.drawText(data_total[i] + "", highPoints[i][0] - 10, 300 - highPoints[i][1] - 10,  
//	                      paint) ;  
	           }  
   	   }
   	     
   	   
   	     
   	    //view在创建的时候执行这个函数  
   	      @Override  
   	        
   	       public void onDraw(Canvas canvas) {  
   	          canvas.drawColor(Color.WHITE) ;  
   	          drawAxis(canvas) ;  
   	          drawChart(canvas) ;  
   	          drawHighLines(canvas) ;
      }  
//   	   public class BarChart {
//   		private final int w = 20 ;  
//   		   private float h ;  
//   		    private final int total_y = getHeight()-50 ;  
//   		    private int x ;  
//   		  
//   		   public int getX() {  
//   		        return x ;  
//   		    }  
//   		  
//   		    public void setX(int x) {  
//   		       this.x = x ;  
//   		    }  
//   		  
//   		    public float getH() {  
//   		        return h ;  
//   		    }  
//   		  
//   		    public void setH(float h) {  
//   		        this.h = h ;  
//   		    }  
//   		  
//   		    public void drawSelf(Canvas canvas, Paint paint) {  
//   		        canvas.drawRect(x, total_y - h, w + x, total_y - 1, paint) ;  
//   		    }  
//
//   	}
}
