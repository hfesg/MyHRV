package com.cqupt.filter;

public class imerode {
	
	// 腐蚀    
	
	public static float []g={1,1,1,1,1,1,1,1,1,1,
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
	
	public float[] ECG_imerode(float f[]){      // f[]为输入信号
		
		System.out.println("----------------->");     

	    float[] s=new float[f.length];// 返回腐蚀后的数据
		
	    for(int i=0;i<f.length-g.length+1;i++){   // 定义域为 N-M+1     //---缩小，需要的补位数是固定的       22位
	    	float []temp=new float[g.length]; //存储每次移动计算的数值 ,用来得出最小值                
 
	    	    for(int j=0;j<g.length;j++){  
	    		
	    		   temp[j]=f[i+j]-g[j];  

	    	    }    
	        	//System.out.println("*************");  
        	    s[i+22]=Min(temp);
	    }  	
 
	    // 补前22位---使用信号源的前22位
	    for(int i=0;i<22;i++){
	    	s[i]=s[22];	 
	    }
	                       //System.out.println("s[f.length-g.length+22]*************"+s[f.length-g.length]);  
	                       //System.out.println("s[f.length-g.length+23]*************"+s[f.length-g.length+23]);  
	    // 补后22位---使用信号源的后22位
	    for(int i=f.length-g.length+1;i<f.length-g.length+1+22;i++){
	    	s[i+22]=s[f.length-g.length+22];	 
	    }
        
        /*
	    for(int k=0;k<s.length;k++){
	    	  System.out.println("*************>"+s[k]);
	    }
        */ 
	   
	    return s;
	}
	
    // 求最小值       
    public float Min(float[] temp){     
    	
    	float t=temp[0];
    	if(temp.length==1){
    		
    		return t;
    	}else {
    	    for(int i=0;i<temp.length;i++){
    		
    		    t=Math.min(t, temp[i]);
            }   	
    	}
    	return t;
    }
	
	
	/**
	 * @param args
	 */
    /*
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		imerode rode= new imerode();
		
		float[] f={7,9,8,3,8,9,9};  // 信号             ------------长度 N 固定  
		float[] g={-3,0,-3};        // 结构元素   ------------长度 M 固定
		
		rode.ECG_imerode(f, g);
 
	}
	*/
}
