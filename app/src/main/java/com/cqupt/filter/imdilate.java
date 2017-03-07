package com.cqupt.filter;

public class imdilate {

	public static float []g={1,1,1,1,1,1,1,1,1,1,
		                     1,1,1,1,1,1,1,1,1,1,
		                     1,1,1,1,1,1,1,1,1,1,
		                     1,1,1,1,1,1,1,1,1,1,
		                     1,1,1,1,1,1,1,1,1,1,
		                     1,1,1,1,1,1,1,1,1,1,
		                     1,1,1,1,1,1,1,1,1,1,
		                     1,1,1,1,1,1,1,1,1,1,
		                     1,1,1,1,1,1,1,1,1,1,
		                     1,1,1,1,1,1,1,1,1,1,
		                     1,1,1,1,1,1,1,1,1,1,
		                     1,1,1,1,1,1,1,1,1,1,
		                     1,1,1,1,1,1,1,1,1,1,
		                     1,1,1,1,1,1,1,1,1,1
		                     }; // g[]为结构元素
	
    public float[] ECG_imdilate(float f[]){      // f[]为输入信号    
		
		// System.out.println("f[0]----------------->"+f[0]+"    "+f.length);              

	    float[] s=new float[f.length];// 返回膨胀后的数据
		
	    for(int i=0;i<f.length-g.length+1;i++){   // 定义域为 N+M-1     //---扩大，需要的删除位数是固定的       22位     
	    	 
	    	float []temp=new float[g.length]; //存储每次移动,计算的数值 ,用来得出最大值                
 
	    	for(int j=0;j<g.length;j++){  
	    		  temp[j]=f[i+j]+g[j];  
	        }    
	        	//System.out.println("*************");        	   	
	    	
	    	s[i+22]=Max(temp);
	    	   	    	    	    	    
	    }  	 
  
	    
	    // 补前22位 
	     for(int i=0;i<22;i++){
	    	
	    	float u=f[0];
	    	for(int j=0;j<23+i;j++){	
                  u=Math.max(u, f[j]);
                  //System.out.println("u*************>"+f[j]);
	    	}
	     	s[i]=u+1;  //  1  为结构元素的值
	    	
	     }
	    
	    // 补后22位 
	     /*
	     for(int j=f.length-26;j<f.length;j++){      //数组定义的时候本来是1440，但定义的是1444.
	 
	    	 System.out.println("f[j]*************>"+f[j]);
	     }
	     */ 
	    
	     for(int i=0;i<22;i++){
	    	 
	    	 float v=f[f.length-45+i];  float t=0;
	    	 for(int j=f.length-45+i;j<f.length;j++){
	    		 
	    		 t=Math.max(v, f[j]);
	    		 v=t;
	    	 }
	    	 //  System.out.println("v*************>"+(v+1)); 
               s[f.length-22+i]=v+1;
	     }
	     
    
	    /*
	    // 输出
	    for(int k=0;k<s.length;k++){
	    	   System.out.println("*************>"+s[k]);
	    }
	    */
	     
	    return s;
    }
    
    
	// 求最小值       
	public float Max(float[] temp){     
	    	
	   float t=temp[0];
	   
	   if(temp.length==1){ 
		   return t;	   
	   }else{
		   for(int i=0;i<temp.length;i++){
		       t=Math.max(t, temp[i]);
		   }  
	   }
	   return t;
    }
	
}
