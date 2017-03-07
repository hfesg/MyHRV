package com.historyActivity;

import java.util.ArrayList;

import org.apache.http.Header;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hrv.R;
import com.hrv.RecordDetial;
import com.login.ClientUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class PanelActivity extends Activity {
	private ListView listView;
	private ArrayList<String> data;
	private String inferData;
	private String sandianData;
	private String pointStr;
	private String timedata;
	private String datedata;
	private ProgressDialog progressDialog = null;
	private Button exit;
	private MyAdapter adapter;
	private TextView dateText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_panel);
		
		//关闭等待加载界面
		Intent mIntent = new Intent();
		mIntent.setAction("com.hrv.load.FINISH");
		sendBroadcast(mIntent);
		
		listView = (ListView) findViewById(R.id.listview);
		exit = (Button) findViewById(R.id.exit_panel);
		dateText = (TextView) findViewById(R.id.date_text);
		Intent intent = getIntent();
		data = intent.getStringArrayListExtra("timeList");
		datedata = intent.getStringExtra("datedata");
		dateText.setText(datedata);
		adapter = new MyAdapter(this);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				progressDialog = ProgressDialog.show(PanelActivity.this, "请稍等..", "记录查询中.",
						false, true);// 对话框 显示界面

				String timeText = adapter.getItem(position).toString();
				timedata = timeText;
				Log.i("hrv", timeText);
				queryResult(timedata);
			}
		});
		
		exit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PanelActivity.this.finish();
			}
		});
	}
	
	
	
	static class ViewHolder{
//		public ImageView img;
		public TextView title;
//		public TextView info;
	}
	
	public class MyAdapter extends BaseAdapter{
		
		private LayoutInflater mInflater = null;
        private MyAdapter(Context context)
        {
            //根据context上下文加载布局，这里的是Demo17Activity本身，即this
            this.mInflater = LayoutInflater.from(context);
        }

		@Override
		public int getCount() {
			//在此适配器中所代表的数据集中的条目数
            return data.size();
		}

		@Override
		public Object getItem(int position) {
			//获取数据集中与指定索引对应的时间
			String timeStr = data.get(position);
            return timeStr;
		}

		@Override
		public long getItemId(int position) {
			//获取在列表中与指定索引对应的行id
            return position;
		}

		//获取一个在数据集中指定索引的视图来显示数据
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
            //如果缓存convertView为空，则需要创建View
            if(convertView == null)
            {
                holder = new ViewHolder();
                //根据自定义的Item布局加载布局
                convertView = mInflater.inflate(R.layout.panel_list_item, null);
//                holder.img = (ImageView)convertView.findViewById(R.id.img);
                holder.title = (TextView)convertView.findViewById(R.id.tv);
//                holder.info = (TextView)convertView.findViewById(R.id.info);
                //将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
                convertView.setTag(holder);
            }else
            {
                holder = (ViewHolder)convertView.getTag();
            }
//            holder.img.setBackgroundResource((Integer)data.get(position).get("img"));
            holder.title.setText(data.get(position).toString());
//            holder.info.setText((String)data.get(position).get("info"));
                                                             
            return convertView;
		}
		
	}
	
	
	/**
	 * 根据记录时间查询结果，并跳转至详情显示界面
	 * @param time--检测时间
	 */
	private void queryResult(String time)
	{
		String url = "QueryResultServlet";
		RequestParams params = new RequestParams();
		params.put("time", time);
		
		ClientUtil.post(url, params, new AsyncHttpResponseHandler()
		{

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody)
			{
				if (statusCode == 200)
				{
					
			        String result = new String(responseBody); 
			        System.out.println("查询结果为："+result);
			        String[] splitStr = new String[5];  
			        int i = 0;  
			        while (true) {  
			  
			            int j = result.indexOf("|");// 找分隔符的位置  
			            if (j == -1) {// 没有分隔符存在  
			                splitStr[i] = result;  
			                break;  
			            }  
			            splitStr[i] = result.substring(0, j);// 找到分隔符，截取子字符串  
			            i++;  
			            result = result.substring(j + 1); // 剩下需要处理的字符串  
			        }  
			        
			        if(splitStr[0].equals("Ecg")){
			        	inferData = splitStr[1];
			        	Log.i("info", inferData);
			        	sandianData = splitStr[2];
			        	Log.i("info", sandianData);
			        	pointStr = splitStr[3];
			        	Log.i("info", pointStr);
			        
			        
				        Intent intent = new Intent(PanelActivity.this, RecordDetial.class);
				        intent.putExtra("isDown", "notDown");
				        intent.putExtra("time",timedata);
				        intent.putExtra("date", datedata);
				        intent.putExtra("inferData",inferData);
						intent.putExtra("sandianData", sandianData);
						intent.putExtra("pointStr", pointStr);
						startActivity(intent);
						progressDialog.dismiss();
			        }else{
			        	System.out.println("没有数据！！");
			        	return;
			        }
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error)
			{
//				error.printStackTrace();
				
				System.out.println("发送时间查询失败！！");
			}
		});
	}

}
