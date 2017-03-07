package com.hrv;



public class R_Location {
	
	//int[] R_input = new int[51200];
	float[] input = new float[256000];
	float[] iinput = new float[25600];
	
	int t,u;
	int fs = 500;
	public float[] R_dif(float[] R_input) {  //前向差分，算出每个差分的值
		float[] R_dif_data = new float[256000];
		for (int i = 5300; i < 5650; i++) {
			R_dif_data[i] = R_input[i] - R_input[i+1];
			
		}
		
		return R_dif_data;	
	}
	public float[] R_dif(float[] R_input,int n) {  //向后移动100点 后  前向差分
		float[] R_dif_data = new float[256000];
		//int w=0;
		for (int i = n; i < n+500; i++) {
			R_dif_data[i] = R_input[i] - R_input[i+1];
			//w++;
			//System.out.println("R_dif_data[i] "+R_dif_data[i]);
		}
		
		return R_dif_data;	
	}
	public int compare_R_dif_maxdata(float[] R_input,int n) {   //  计算差分值中的最大值
		float[] compare_data = new float[R_input.length];
		//iinput = R_input;
		//u=m;
		//compare_data = R_dif(iinput,u);
		compare_data = R_input;
		float temp_max = compare_data[n];
		int adr = 0;
		for (int i = n; i < n+500; i++) {        //175是0.35*fs
			if (temp_max < compare_data[i]|| temp_max == compare_data[i]) {
				temp_max = compare_data[i];
				adr = i;
			}
		}
		return adr;
	
	}
	public int compare_R_dif_mindata(float[] R_input,int n) {   //  计算差分值中的最大值
		float[] compare_data = new float[R_input.length];
		//iinput = R_input;
		//u=m;
		//compare_data = R_dif(iinput,u);
		compare_data = R_input;
		float temp_max = compare_data[n];
		int adr = 0;
		for (int i = n; i < n+500; i++) {
			if (temp_max > compare_data[i]|| temp_max == compare_data[i]) {
				temp_max = compare_data[i];
				adr = i;
			}
		}
		return adr;
	
	}
	public float[] R_dif(float[] R_input,int n,int p) {  //向后移动100点 后  前向差分
		float[] R_dif_data = new float[256000];
	//	int w = 0;
		for (int i = n+175; i < n+p+175; i++) {     // 175是0.35*fs
			R_dif_data[i] = R_input[i] - R_input[i+1];
			
			//System.out.println("R_dif_data[i]     "+ i +   R_dif_data[i]);
			//w++;
		}
		
		return R_dif_data;	
	}
	public int compare_R_dif_maxdata(float[] R_input,int n,int p) {   //  计算差分值中的最大值
		float[] compare_data = new float[R_input.length];
		iinput = R_input;
		//u=m;
		//compare_data = R_dif(iinput,u);
		compare_data = R_input;
		float temp_max = compare_data[n+175];
		int adr = 0;
		for (int i = n+175; i < n+p+175; i++) {        //175是0.35*fs
			if (temp_max < compare_data[i]|| temp_max == compare_data[i]) {
				temp_max = compare_data[i];
				adr = i;
			}
		}
		return adr;
	
	}
	public int compare_R_dif_mindata(float[] R_input,int n,int p) {   //  计算差分值中的最大值
		float[] compare_data = new float[R_input.length];
		iinput = R_input;
		//u=m;
		//compare_data = R_dif(iinput,u);
		compare_data = R_input;
		float temp_max = compare_data[n+175];
		int adr = 0;
		for (int i = n+175; i < n+p+175; i++) {
			if (temp_max > compare_data[i]|| temp_max == compare_data[i]) {
				temp_max = compare_data[i];
				adr = i;
			}
		}
		return adr;
	
	}
	
	public int R_adr_data(float[] R_input,int min,int max){  //由差分最大值点，向前搜索50个点，找出R波的最大值点
		int R_adr=0;
		input = R_input;
	//	t=m;
	//	int i = compare_R_dif_data(input,t);
		float R_max = input[min-1];
		if (max>min) {          // m大于50的点
		//	float R_max = R_input[min];
			for (int j = min; j < max; j++) {
				if (R_max < R_input[j]) {
					R_max = R_input[j];
					R_adr = j;
				}
			}
			if(R_adr == 0){
				R_adr = max;
				//System.out.println("Radr    "+ R_adr);
			}
		}else {  if(max<50){
				for (int j = 0; j < max ; j++) {
					if (R_max < R_input[j]) {
						R_max = R_input[j];
						R_adr = j;
					//	System.out.println("Radr    "+ j);
					  }
				   }
			     }else{
				   for (int j = max-50; j < max ; j++) {
					  if (R_max < R_input[j]) {
						R_max = R_input[j];
						R_adr = j;
					//	System.out.println("Radr    "+ j);
					  }
				   }
			   }
		
			
			if(R_adr == 0){
				R_adr = max;
				//System.out.println("Radr    "+ R_adr);
			}
		}
			
		
		
		return R_adr;
	}
	 public float[] sort_ECG_data(float[] f){
		 int q =0;
		 float[] sort_abs_data=new float[1000];
		 for (int i = 1000; i < 2000; i++) {
			  sort_abs_data[q] = f[i];
			  q++;
		}
		    // sort_abs_data = f;

			for(int i=999;(i>0||i==0);i--){
				 
				 for(int j=0;j<i;j++){
					 float temp;
					 if(sort_abs_data[j]<sort_abs_data[j+1]){
					 temp= sort_abs_data[j];
					 sort_abs_data[j]=sort_abs_data[j+1];
					 sort_abs_data[j+1]=temp;
					 }
				 }
				
			 } 
	
             //  sort_abs_data=f;
         return sort_abs_data;
	 }
	 public float mean_R_TH(float[] f) {
    	        float[] R_data = new float[1000];
				R_data = sort_ECG_data(f);
				float R_sum=0.0f, R_minsum = 0.0f,R_minmean = 0.0f;
				float R_mean=0.0f,R_mean_th=0.0f;
				for (int i = 0; i < 10; i++) {
					R_sum +=R_data[i];
				}
				R_mean=R_sum/10;
				System.out.println("Rmean"+R_mean);
				for(int p = 999; p>989;p--){
					R_minsum += R_data[p];
				}
				R_minmean = R_minsum/10;
				System.out.println("Rminmean"+R_minmean);
		        R_mean_th=R_mean-R_minmean;
		        System.out.println("R_mean_TH"+R_mean_th);
		        return R_mean_th;
	}
 
	 // 计算    TH   TH=ave_max_10*0.75   
	 public float get_TH(float[] f,int[] r,int w ){ //计算阈值
		 int sum=0;  
		 float TH=0.0f;
		
		 if (w <=4) {
			 for(int i=0;i<w;i++){
				 sum+=f[r[i]];
			 }
			 TH=(float) (sum/w*0.65);
		}else {
			for(int i=w-1;i>w-5;i--){
				 sum+=f[r[i]];
			 }
			TH=(float)(sum/4*0.65);
		}
		 
		 
		
		 return TH;
	 }
	 public float compare_max_in_data(float[] R_input,int n) {   //  计算读入数据的中的最大值
			float[] compare_data = new float[R_input.length];
		
			//u=m;
			//compare_data = R_dif(iinput,u);
			compare_data = R_input;
			float temp_in_max = R_input[n];
			int adr = 0;
			
			
			for (int i = n ; i < n + 50; i++) {
			
				if (temp_in_max < R_input[i]) {
					temp_in_max = R_input[i];
				//	adr = i;
				
				}
				
			}
			return temp_in_max;
		
		}
	
	public float compare_max_data(float[] R_input) {   //  计算读入数据的中的最大值
		//float[] compare_data = new float[R_input.length];
	
		//u=m;
		//compare_data = R_dif(iinput,u);
		//compare_data = R_input;
		/*float temp_max = R_input[100];
		int adr = 0;
		for (int i = 101; i < 1100; i++) {
			if (temp_max < R_input[i]|| temp_max == R_input[i]) {
				temp_max = R_input[i];
				adr = i;
			}
			//System.out.println("compare_data2     "+ i +   R_input[i]);
		}*/
		float[] R_max_data = new float[1000];
		R_max_data = sort_ECG_data(R_input);
		float R_sum=0.0f;
		float R_max_mean=0.0f;
		for (int i = 0; i < 10; i++) {
			R_sum +=R_max_data[i];
		}
		R_max_mean=R_sum/10;
		System.out.println("R-max-mean"+R_max_mean);
		return R_max_mean;
	
	}

}
