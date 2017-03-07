package com.hrv;

import android.R.integer;

public class RR_Analysis {
     int ri,tp_n,n,realri;
     float[] RR = new float[256];
     float[] ssRR = new float[512];
     float[] realssRR = new float[512];
     float[] difRR = new float[255];
     float sum_RR,realsum_RR,sum_var,RRmean,RealRmean,Hrate,SumRR;
     double SDNN,RRvar,r_MSSD,NN50,pNN50;
     double tpv,vlf,lf,hf;
     
     float ms =  2.0f;
     double[] abs = new double[256];
     double[] hrx = new double[256];
     float[] f = new float[256];
     
     public float RS_RR(int[] input,int num){
    	 for (int i = 0; i < num-1; i++) {
    		 if (input[i+1]-input[i]>0) {
    			 realssRR[realri]=input[i+1]-input[i];
   			     realri++;     //ri加一了 			  
			} 
    		// System.out.println("ri  "+ri);
    		// System.out.println("ri  "+RR[i]);
		}
    	 for (int i = 0; i < realri; i++) {//ri减一
 			realsum_RR += realssRR[i];
 		}
     	// System.out.println("RR.length   " +RR.length);
     	// RRmean = sum_RR/256*ms;	
     	 RealRmean = realsum_RR/realri*ms;
     	 return RealRmean;
     } 
     public float[] S_RR(int[] input,int num){
    	 for (int i = 0; i < num-1; i++) {
    		 if (input[i+1]-input[i]>0) {
    			 ssRR[ri]=input[i+1]-input[i];
   			     ri++;     //ri加一了 			  
			} 
    		// System.out.println("ri  "+ri);
    		// System.out.println("ri  "+RR[i]);
		}
    	 for(int i = 0; i < ri-1; i++){
    		 if(n < 256){
    			 if((ssRR[i+1]-ssRR[i]<60&&ssRR[i+1]-ssRR[i]>-60)){
    				 RR[n] = ssRR[i+1];
    				 n++; 
    			 
    			 System.out.println(n+"   ssRR[N]   " + ssRR[n-1]); 
    			 System.out.println(n+"   RR[N]   " + RR[n-1]); 
    			 }
    		 }else{
    		      break;
    		 }
    	 }
    	 return RR;
     }
   //计算均值
     public float RR_mean(float[] rr) {
    	/* System.out.println("input length  "+input.length);
    	 System.out.println("num  "+num);*/
    	 
    	 for (int i = 0; i < rr.length; i++) {//ri减一
			sum_RR += rr[i];
		}
    	// System.out.println("RR.length   " +RR.length);
    	// RRmean = sum_RR/256*ms;	
    	 RRmean = sum_RR/rr.length*ms;
    	 return RRmean;
	}
     public float HRate(){
    	 Hrate = 1000/RRmean;
    	 return Hrate;
     }
  // 计算方差 
     public double RR_var() { 
    	 for (int i = 0; i < RR.length; i++) {
			sum_var +=(RR[i]*ms-RRmean)*(RR[i]*ms-RRmean);
		}
    	// RRvar = sum_var/256;
		RRvar = sum_var/RR.length;
		return RRvar;
	}
  // 计算SDNN  
     public double RR_SDNN(double var) { 
    	SDNN = Math.sqrt(var);
    	return SDNN;
	}
  // 计算NN50 
     public double RR_NN50() { 
    
     	for (int i = 0; i < RR.length-1; i++) {
     		difRR[i] = (float) ((RR[i+1] - RR[i])*ms);
			if (difRR[i]>50) {
				NN50 ++;
			}
		}
     	return NN50;
 	}
  // 计算PNN50
     public double RR_pNN50(double nn50){
    	 
    	// pNN50 = nn50/255;
    	 pNN50 = nn50/(RR.length-1);
    	 return pNN50;
    	 
     }
  // 计算MSSD  
     public double RR_MSSD(){
    	 double m,MSSDm = 0;
    	 for (int i = 0; i < RR.length-1; i++) {
			m = RR[i+1]*2-RR[i]*2;
			MSSDm += m*m;
		}
    	 r_MSSD = Math.sqrt(MSSDm/(RR.length-1));
    	return r_MSSD; 
     }
     public void RR_Power() throws Exception{
    	 for (int i = 0; i <RR.length; i++) {
			RR[i] =  (float) ((RR[i]- RRmean)*ms);
			
		}
    	 for (int i = 0; i < RR.length; i++) {
			f[i] = i*10000/sum_RR/25;
			//System.out.println(f[i]+" f ");
		}
    	 FFT ecgfft = new FFT();
    	 abs = ecgfft.fft(RR);
    	 for (int i = 0; i < abs.length; i++) {
			hrx[i] = abs[i]*abs[i]/abs.length/Hrate;
			//System.out.println("hrx  "+hrx[i]+"  ");
		}
    	 for (int i = 1; i < 127; i++) {
			if (f[i]>0 && f[i]<=0.4) {
				tpv = tpv + hrx[i];
				tp_n = tp_n + 1;
			}
    	 }
    	 for (int j = 0; j < 127; j++) {
			
			if (f[j]>=0.003 && f[j]<=0.04) {
				vlf = vlf + hrx[j];
			}
			if (f[j]>=0.04 && f[j]<=0.14) {
				lf = lf + hrx[j];
			}
			if (f[j]>=0.14 && f[j]<=0.4) {
				hf = hf + hrx[j];
			}
		}
    	 System.out.println("tp_n " + tp_n);
    	 System.out.println("sum_RR " + sum_RR);
    	 System.out.println("tpv  " + tpv);
    	 System.out.println("vlf  " + vlf);
    	 System.out.println("lf  " + lf);
    	 System.out.println("hf  " + hf);
    	 
    	 tpv =  tpv/256;
    	 vlf =  vlf/256;
    	 lf =   lf/256;
    	 hf =   hf/256;
    	  
     }
   
}
