package com.hrv;

import java.util.ArrayList;
import java.util.List;

public class LocalMaximaFinder{
	

	  public LocalMaximaFinder(){
		
	    }
	
	
      public static int[] findLocalMaxima(float[] signal){
		
		
		if(signal == null || signal.length == 0){
			return null;                                  //判断数据是否为空或者长度为0 的情况
			//return 0;                                      //返回值类型为数组，则可以返回为null，如果返回值类型为int，则int默认为0
		}
		
		//算法用到的参数
		double breth_max = -350000;
		double breth_min = 380000;
		int tendency = 1;
		int maxipos = 0;
		int max_i=0, max_x=0;
		int dotcount = 1;
		int yx = 0, ppgspan=200;
		
		//list是在算法执行过程中用于存放找到的极大值位置的链表（因为预先不知道有多少个，链表可以动态扩容）
		List<Integer> list = new ArrayList<Integer>();
		
		// localMaximaLocations是最终返回的数组
		int[] localMaximaLocations = null;
		
		for(int i=1; i<=signal.length; ++i){
						
			if(tendency == 1){
				if(signal[i-1] > breth_max){
					breth_max = signal[i-1];
					maxipos = i;
					max_i = 0;
					max_x = max_x + 1;
					
				}else if(signal[i-1] < breth_max){
					max_i = max_i + 1;
				}
			}
			
			if(tendency == 2){
				if(signal[i-1]<breth_min){
					breth_min = signal[i-1];
					max_i = 0;
					max_x = max_x + 1;
				}else if(signal[i-1]>breth_min){
					max_i = max_i + 1;
				}
			}
			 
			if(max_i>=250 && tendency==2){
				tendency = max_x>=dotcount?1:tendency;
				breth_min = 380000;
				
				
				max_i = 0;                //这两句务必放在tendency赋值之后
				max_x = 0;
			}
			
			if(max_i>=400 && tendency==1 && max_x>=dotcount){
				if(yx != 0){
					if(maxipos-yx>0.3*ppgspan){
						list.add(maxipos);                      // 这里把找到的极大值点放到链表中
						ppgspan = maxipos - yx;
						yx = maxipos;
					}
					
					tendency = 2;
					max_i = 0;
					max_x = 0;
					breth_max = -350000;	
				}else{
					yx = maxipos;
				}
			}
			
		}
		
		// 这里将链表中的内容拷贝到数组中，并将其返回
		localMaximaLocations = new int[list.size()];
		
		for(int i=0; i<localMaximaLocations.length; ++i){
			localMaximaLocations[i] = list.get(i).intValue();
		}
		
		  return localMaximaLocations;                      //返回该段数据中的每一个极大值
		 //return localMaximaLocations.length;
	}
	
	
		
}