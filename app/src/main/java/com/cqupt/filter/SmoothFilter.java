package com.cqupt.filter;

public class SmoothFilter {
	public float[] smoothFilter(float[] sampData,int dot){        //传入两个参数 1要处理的数据  2打点的个数
		int len = sampData.length;                                //len为传入数据的长度
		int add = (dot-1)/2;                                      //定义add
		float[] resultData = new float[sampData.length];          //定义resultData
		//补点
		for (int i = 0; i < add; i++) {                           //i《 add
			float sum1 = 0;
			float sum2 = 0;
			for (int j = 0; j < i+add+1; j++)  {                   // 
				sum1 = sum1+sampData[j];
			}
			resultData[i] = sum1/(i+add+1);
			
			for (int j = len-add-i-1; j <len ; j++) {
				sum2 = sum2+sampData[j];
			}
			resultData[len-1-i] = sum2/(i+add+1);
		}
		
		//平滑
		for (int i = add; i < len-add; i++) {
			float sum=0;
			for (int j = i-add; j < dot+i-add; j++) {
				sum=sum+sampData[j]; 
			}
			resultData[i]=sum/(2*add+1);
		}
		
		return resultData;
		
	}

}
