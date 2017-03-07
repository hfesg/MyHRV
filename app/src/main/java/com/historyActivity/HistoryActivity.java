package com.historyActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hrv.R;
import com.hrv.HRV;
import com.hrv.RecordDetial;
import com.login.ClientUtil;
import com.login.LoadingActivity;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class HistoryActivity extends Activity {
	
	
	private ArrayList<String> dateArray = new ArrayList<String>() ;
	private Context mContext;
	private Button fanhui;
//	private Button calendar;
	private List<String> parentList;
	private Map<String, List<String>> childMap;
	private ExpandableListView mExList;
	private ExpandableAdapter mAdapter;
	private String[] dates;
	private String inferData;
	private String sandianData;
	private String pointStr;
	private String timedata;
	private String datedata;
	private ProgressDialog progressDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_history);
		
		fanhui = (Button) findViewById(R.id.fanhui);
//		calendar = (Button) findViewById(R.id.rili);
		fanhui.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				HistoryActivity.this.finish();	
			}
		});
		
//		calendar.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
////				Intent intent = new Intent(HistoryActivity.this,);	
//			}
//		});
		
		//初始化数据
		initData();
		mExList = (ExpandableListView) findViewById(R.id.listview);
		mExList.setGroupIndicator(null);
		mAdapter = new ExpandableAdapter(HistoryActivity.this);
		mExList.setAdapter(mAdapter);
		
		//设置子项监听事件
		mExList.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				
				progressDialog = ProgressDialog.show(HistoryActivity.this, "请稍等..", "记录查询中.",
						false, true);// 对话框 显示界面
				String childStr = mAdapter.getChild(groupPosition, childPosition).toString();
				String parentStr = mAdapter.getGroup(groupPosition).toString();
				System.out.println("查询日期为："+parentStr);
				System.out.println("查询时间为："+childStr);
				
				datedata = parentStr;
				timedata = childStr;
				//获取历史记录数据
				queryResult(childStr);
				return false;
			}
		});
		//设置父项监听事件
		mExList.setOnGroupClickListener(new OnGroupClickListener() {
			
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				String parentStr = mAdapter.getGroup(groupPosition).toString();
				
				datedata = parentStr;
//				timeList.remove(groupPosition);
				//查询该日期下所有的历史检测时间
				queryTime(datedata);
				return false;
			}
		});
		
		//设置父项展开事件,同一时间只能展开一个父 项
		mExList.setOnGroupExpandListener(new OnGroupExpandListener() {
			
			@Override
			public void onGroupExpand(int groupPosition) {
				for (int i = 0; i < mAdapter.getGroupCount(); i++) {
					if (groupPosition != i) {
						mExList.collapseGroup(i);
					}
				}
			}
		});
	}
	
	/**
	 * 初始化数据
	 */
	private void initData(){
		
		String date = null;
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		date = bundle.getString("dateStr");
		System.out.println("dateStr:"+date);
		parentList = new ArrayList<String>();
		childMap = new HashMap<String, List<String>>();
		
		
		if(date != null && date.length() <= 11){
			parentList.add(date);
			queryTime(date);
			Log.i("info", "只有一个记录日期："+date);
		}else{
			dates = date.split(":");
			for(int i=0; i<dates.length;i++){
				Log.i("info", "初始化数据，得到日期:"+dates[i]);
			}
			
			for(int i=0;i < dates.length; i++){
				parentList.add(dates[i]);
				List<String> timeList2 = new ArrayList<String>();
				timeList2.add(" ");
				childMap.put(dates[i], timeList2);
			}
		}
	}
	
	/**
	 * 根据日期向服务器发送查询请求，返回该日期下所有历史检测时间
	 * @param date--检测日期
	 */
	private void queryTime(final String date)
	{
		String url = "QueryTimeServlet";
		RequestParams params = new RequestParams();
		params.put("date", date);
		// 开启等待框
		Intent intent = new Intent(HistoryActivity.this, LoadingActivity.class);
		intent.putExtra("loadMsg", "正在查询...");
		startActivity(intent);
		
		ClientUtil.post(url, params, new AsyncHttpResponseHandler()
		{

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody)
			{
				if (statusCode == 200)
				{
					String timeStr = new String(responseBody);
					List<String> timeList = new ArrayList<String>();
					if(timeStr !=null && timeStr.length() <= 5){
						timeList.add(timeStr);
						childMap.put(date, timeList);
						//更新适配器数据
						mAdapter.notifyDataSetChanged();
//						Log.i("info", "时间为："+timeStr);
					}else{
						String[] times;
						times = timeStr.split(":");
						
						for(int i=0; i<times.length;i++){
							timeList.add(times[i]);
//							Log.i("info", "时间为："+times[i]);
						}
						childMap.put(date, timeList);
						//更新适配器数据
						mAdapter.notifyDataSetChanged();
						// 发送广播，关闭等待框
						Intent mIntent = new Intent();
						mIntent.setAction("com.hrv.load.FINISH");
						sendBroadcast(mIntent);
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error)
			{
				error.printStackTrace();
			}
		});
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
			        
			        
				        Intent intent = new Intent(HistoryActivity.this, RecordDetial.class);
				        intent.putExtra("isDown", "notDown");
				        intent.putExtra("time",timedata);
				        intent.putExtra("date", datedata);
				        intent.putExtra("inferData",inferData);
						intent.putExtra("sandianData", sandianData);
						intent.putExtra("pointStr", pointStr);
						startActivity(intent);
						progressDialog.dismiss();
						clearData();
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
	
	private void clearData(){
		timedata = null;
		datedata = null;
		inferData = null;
		sandianData = null;
		pointStr = null;
	}

	/**
	 * ExpandableListView的Adapter--构建数据适配器
	 *
	 */
	class ExpandableAdapter extends BaseExpandableListAdapter {
		private Activity activity;

		public ExpandableAdapter(Activity activity) {
			this.activity = activity;
		}
		
		//得到子item需要关联的数据

		public Object getChild(int groupPosition, int childPosition) {
			
			String parentKey = parentList.get(groupPosition);
			return (childMap.get(parentKey).get(childPosition));
		}
		//得到子item的ID
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}
		//获取当前父item下的子item的个数
		public int getChildrenCount(int groupPosition) {
			String key = parentList.get(groupPosition);
            int size=childMap.get(key).size();
            return size;
		}
		//设置子item的组件
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			String key = HistoryActivity.this.parentList.get(groupPosition);
            String info = childMap.get(key).get(childPosition);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) HistoryActivity.this
                        .getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_child, null);
            }
            TextView tv = (TextView) convertView
                    .findViewById(R.id.child_textview);
            tv.setText(info);
            return convertView;
		}

		//获取当前父item的数据
		public Object getGroup(int groupPosition) {
			return parentList.get(groupPosition);
		}

		public int getGroupCount() {
			return parentList.size();
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		//设置父item组件
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) HistoryActivity.this
                        .getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_parent, null);
            }
            TextView tv = (TextView) convertView
                    .findViewById(R.id.parent_textview);
            tv.setText(HistoryActivity.this.parentList.get(groupPosition));
            ImageView heart = (ImageView)convertView.findViewById(R.id.image_heart);
            return convertView;
		}

		public boolean hasStableIds() {
			return true;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		clearData();
		System.out.println("数据清零。");
	}
	
	
}
