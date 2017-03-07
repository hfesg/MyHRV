package com.cqupt.filter;

public class Morphology {
	
	
	private imerode  rode=new imerode();
	private imdilate dilate=new imdilate();
	
	// 开运算----------先腐蚀      后膨胀   
	public float[] Open(float f[]){  
		
		float[] s=new float[f.length];// 返回     开运算     后的数据                

		s=dilate.ECG_imdilate(rode.ECG_imerode(f));
		/*
		for(int i=0;i<s.length;i++){
			//System.out.println("*************"+s[i]);  
		}
		*/
		
		return s;
	}   
	
	// 闭运算----------先膨胀      后腐蚀  
	public float[] Close(float f[]){  
		
		float[] s=new float[f.length];// 返回     闭运算     后的数据
		
	    s=rode.ECG_imerode(dilate.ECG_imdilate(f));
		
	    /*
		for(int i=0;i<s.length;i++){
			System.out.println("*************"+s[i]);  
		}	
		*/
		return s;
	}

	
	// 开-闭运算
	public float[] Open_close(float f[]){ 
		
		float[] s=new float[f.length];// 返回     开-闭运算     后的数据	
		
		s=Close(Open(f));
		/*
		for(int i=0;i<s.length;i++){
			System.out.println("Open_close*************"+s[i]);  
		}
		*/
		
		return s;
	}
	
	// 闭-开运算
	public float[] Close_open(float f[]){ 
		
		float[] s=new float[f.length];// 返回     开-闭运算     后的数据	
		
		s=Open(Close(f));
		/*
		for(int i=0;i<s.length;i++){
			System.out.println("Open_close*************"+s[i]);  
		}
		*/
		return s;
	}
	
	
	
	//  【（开-闭）+（闭-开）】/2
	public float[]  Morphology_Fep_baseLine(float f[]){ 
		
		float[] s=new float[f.length];
		float[] s_Open_close=new float[f.length];
		float[] Close_open=new float[f.length];
		
		s_Open_close=Open_close(f);
		Close_open=Close_open(f);
		

		for(int i=0;i<f.length;i++){
			
			 s[i]=(float)(f[i]-(s_Open_close[i]+Close_open[i])*0.5);

			 //System.out.println("Morphology_Fep_baseLine*************"+s[i]);  
		}
		//System.out.println("Morphology_Fep_baseLine*************"+s.length);  
		
	    return s;
	}
	
	

}
