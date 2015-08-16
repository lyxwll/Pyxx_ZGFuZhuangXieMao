package com.pyxx.part_activiy;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnGroupExpandListener;


import com.baidumap.BaiduLocationOverlayActivity;
import com.baidumap.BaiduLocationAddrActivity;

import com.pyxx.app.ShareApplication;
import com.pyxx.dao.DBHelper;
import com.pyxx.datasource.DNDataSource;
import com.pyxx.entity.Data;
import com.pyxx.entity.Listitem;
import com.pyxx.loadimage.Utils;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.PerfHelper;

public class ShopsArticleActivity extends Activity {
	private View btn_back;// 返回按钮
	private View btn_pinglun;//评论按钮
	private TextView txt_title, txt_value, txt_addr, txt_sjjs,sjjs_tv,
			txt_cpjs,btn_shoucang,pl_count,text_user1,text_user2,text_user3,text_user1nr,text_user2nr,text_user3nr,text_phone;
	private View xx_v1,xx_v2;
	private Button pl_btn;
	private ImageView img;
	private TextView phone;
	private RelativeLayout plnr_rl,zwpl_rl,dingwei_rl,xianlu_rl;
	private TabHost current_tabhost;
	private ExpandableListView eblist_routeQuery;
	private String[] listName, childTitle;
	Listitem select_entity;
	private String userId = "";
	boolean collect = false;
	private  String isCollect=null;
	private int[] headImage = new int[] { R.drawable.gjlx, R.drawable.zjlx,
			R.drawable.bx };
	private String str_reviews="";
	String iscollect = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_article_shops_easy);

		if (getIntent().getExtras() != null) {
			select_entity = (Listitem) getIntent().getExtras().get("item");
			isCollect = (String) getIntent().getExtras().getString("isCollect");
		}
		new SelectReviews2().execute((Void)null);
		if(isCollect!=null){
			if(Integer.parseInt(isCollect)==1){
			collect = true;
			}
		}
		userId = PerfHelper.getStringData(PerfHelper.P_USERID);
		findViewById(R.id.title_back).setVisibility(View.VISIBLE);
        
		// 初始化所有控件
		initView();
		
		List<Map<String, Object>> parentList = getParentList();
		List<List<Map<String, Object>>> childList = getChildList();
		// TODO 给线路查询绑定值 null传递的值为商家的地址信息 用来实现线路查询的东西
		DetailListAdapter detailListAdapter = new DetailListAdapter(
				ShopsArticleActivity.this, parentList, childList, null, handler);
		eblist_routeQuery.setAdapter(detailListAdapter);
		eblist_routeQuery.setGroupIndicator(null);
		eblist_routeQuery.setDivider(null);

		eblist_routeQuery.setOnGroupExpandListener(new OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {

			}
		});

		// 获取定位需要的信息；
		statrLongitude = PerfHelper.getStringData(PerfHelper.P_GPS_LONG) + "";
		startLatitude = PerfHelper.getStringData(PerfHelper.P_GPS_LATI) + "";
		endLongitude = select_entity.longitude;
		endLatitude = select_entity.latitude;
		address = select_entity.address;		
		
		
		
		if (PerfHelper.getBooleanData(PerfHelper.P_USER_LOGIN)) {
			String strnid = select_entity.nid;
			String listtype = select_entity.list_type;			
			iscollect = DBHelper.getDBHelper().select("listshops", "*", "nid=? and list_type=?", new String[] { strnid,listtype});
			if(!"".equals(iscollect)){
				collect = true;
				btn_shoucang.setBackgroundResource(R.drawable.shoucang_tb3);
			}
		}		
		
		// 控件对应注册事件
		// 返回按钮
		btn_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ShopsArticleActivity.this.finish();
			}
		});
         //点击评论事件
		btn_pinglun.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if("".equals(userId) || "游客".equals(userId)){
					Utils.showToast("请先登录再评论");
				}else{
					Intent intent = new Intent();
					intent.setClass(ShopsArticleActivity.this,
							Review.class);
					intent.putExtra("item", select_entity);
					intent.putExtra("img_list", select_entity.icon.split(","));
					startActivity(intent);		
				}
			}
		});
		//点击定位
		dingwei_rl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intents = new Intent();
				intents.setClass(ShopsArticleActivity.this,BaiduLocationAddrActivity.class);
				intents.putExtra("item", select_entity);
				startActivity(intents);
			}
		});
		
		//点击收藏美食
		btn_shoucang.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if ("".equals(userId) || "游客".equals(userId)) {
					Toast.makeText(ShopsArticleActivity.this, "请登录后收藏",
							Toast.LENGTH_SHORT).show();
				} else {
					if (collect) {
						new AlertDialog.Builder(ShopsArticleActivity.this)
								.setMessage("是否取消收藏？")
								.setPositiveButton("是",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
//												Utils.showProcessDialog(
//														ShopsArticleActivity.this,
//														"正在取消收藏...");
												String strnid = select_entity.nid;
												String listtype = select_entity.list_type;
												DBHelper.getDBHelper().delete("listshops", "nid=? and list_type=?",
														new String[] { strnid,listtype});
												collect = false;
												iscollect = "";
												btn_shoucang.setBackgroundResource(R.drawable.shoucang_tb1);
												Utils.showToast("取消收藏成功");
												// TODO
//												new delectCollect()
//														.execute((Void) null);
											}
										})
								.setNegativeButton("否",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface arg0,
													int arg1) {
											}
										}).show();
					} else {
						new AlertDialog.Builder(ShopsArticleActivity.this)
								.setMessage("是否收藏？")
								.setPositiveButton("是",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
//												Utils.showProcessDialog(
//														ShopsArticleActivity.this,
//														"正在收藏...");
//												List<Listitem> list = new ArrayList<Listitem>();
//												list.add(select_entity);
												       String insertDataSql = "INSERT INTO listshops(icon,nid,title,des,phone,u_date,other,address,latitude,longitude,other1,list_type) VALUES('"+select_entity.icon+"','"+select_entity.nid+"','"+select_entity.title+"','"+select_entity.des+"','"+select_entity.phone+"','"+select_entity.u_date+"','"+select_entity.other+"','"+select_entity.address+"','"+select_entity.latitude+"','"+select_entity.longitude+"','"+select_entity.other1+"','"+select_entity.list_type+"')";
												       DBHelper.getDBHelper().getReadableDatabase().execSQL(insertDataSql);
												       iscollect = "1";
												       collect = true;
														btn_shoucang.setBackgroundResource(R.drawable.shoucang_tb3);
												       Utils.showToast("收藏成功");
												
//												try {
//													DBHelper.getDBHelper().insertObject(select_entity,"listshops");
//												} catch (Exception e) {
//													// TODO Auto-generated catch block
//													e.printStackTrace();
//												}
//												new CollectAync()
//														.execute((Void) null);

											}
										})
								.setNegativeButton("否",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface arg0,
													int arg1) {
											}
										}).show();
					}
				}

			}
		});
		// 点击查询美食图片
		img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 点击进入到另外一个控件，查看更多的新闻图片，通过Gallery实现
				Intent intent = new Intent();
				intent.setClass(ShopsArticleActivity.this,
						ShowImgActivity.class);
				intent.putExtra("item", select_entity);
				intent.putExtra("img_list", select_entity.icon.split(","));
				startActivity(intent);
			}
		});
	}

	
	
	/**
	 * TODO 页面跳转，判断是否有空值
	 * 
	 * @param cityName
	 * @param travelMode
	 * @param statrLongitude
	 * @param startLatitude
	 * @param endLongitude
	 * @param endLatitude
	 */
	public void toMapActivity(String cityName, String travelMode,
			String statrLongitude, String startLatitude, String endLongitude,
			String endLatitude, String address, Context context) {
		if ("".equals(travelMode) || "".equals(statrLongitude)
				|| "".equals(startLatitude) || "".equals(endLongitude)
				|| "".equals(endLatitude) || "".equals(cityName)
				|| "".equals(address)) {
			Toast.makeText(context, "传递地址信息不能有空值", Toast.LENGTH_LONG).show();
		} else {
			Intent intent = new Intent(context,
					BaiduLocationOverlayActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("cityName", cityName);
			bundle.putString("travelMode", travelMode);
			bundle.putString("statrLongitude", statrLongitude);
			bundle.putString("startLatitude", startLatitude);
			bundle.putString("endLongitude", endLongitude);
			bundle.putString("endLatitude", endLatitude);
			bundle.putString("address", address);
			intent.putExtra("map", bundle);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
	}

	String travelMode = null, cityName, startPlace, endPlace, statrLongitude,
			startLatitude, endLongitude, endLatitude, address;

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			// System.out.println("handler:" + msg.what);
			switch (msg.what) {
			case 0:

				// 公交线路
				travelMode = "bus";
				cityName = "重庆市";
				if (statrLongitude.equals("null")
						|| statrLongitude.trim().length() <= 0
						|| startLatitude.equals("null")
						|| startLatitude.trim().length() <= 0
						|| endLongitude.equals("null")
						|| endLongitude.trim().length() <= 0
						|| endLatitude.equals("null")
						|| endLatitude.trim().length() <= 0) {
					Toast.makeText(ShopsArticleActivity.this,
							"对不起由于无法获取相关定位信息，无法使用该功能", Toast.LENGTH_LONG)
							.show();
				} else {
					toMapActivity(cityName, travelMode, statrLongitude,
							startLatitude, endLongitude, endLatitude, address,
							ShopsArticleActivity.this);
				}
				break;

			case 1:
				// 驾车线路
				travelMode = "drive";
				cityName = "重庆市";// TODO 添加城市传值

				if (statrLongitude.equals("null")
						|| statrLongitude.trim().length() <= 0
						|| startLatitude.equals("null")
						|| startLatitude.trim().length() <= 0
						|| endLongitude.equals("null")
						|| endLongitude.trim().length() <= 0
						|| endLatitude.equals("null")
						|| endLatitude.trim().length() <= 0) {
					Toast.makeText(ShopsArticleActivity.this,
							"对不起由于无法获取相关定位信息，无法使用该功能", Toast.LENGTH_LONG)
							.show();
				} else {
					toMapActivity(cityName, travelMode, statrLongitude,
							startLatitude, endLongitude, endLatitude, address,
							ShopsArticleActivity.this);

				}
				break;

			case 2:
				// 步行线路
				travelMode = "walk";
				cityName = "重庆市";// TODO 添加城市传值
				if (statrLongitude.equals("null")
						|| statrLongitude.trim().length() <= 0
						|| startLatitude.equals("null")
						|| startLatitude.trim().length() <= 0
						|| endLongitude.equals("null")
						|| endLongitude.trim().length() <= 0
						|| endLatitude.equals("null")
						|| endLatitude.trim().length() <= 0) {
					Toast.makeText(ShopsArticleActivity.this,
							"对不起由于无法获取相关定位信息，无法使用该功能", Toast.LENGTH_LONG)
							.show();
				} else {
					toMapActivity(cityName, travelMode, statrLongitude,
							startLatitude, endLongitude, endLatitude, address,
							ShopsArticleActivity.this);
				}

				break;

			default:
				break;
			}
		};
	};		
	
	
	public void initView() {
		btn_back = this.findViewById(R.id.title_back);// 返回按钮
		TextView title = (TextView)this.findViewById(R.id.title_title);
		title.setVisibility(View.GONE);
		btn_pinglun = this.findViewById(R.id.title_btn_pl);
		btn_pinglun.setBackgroundResource(R.drawable.pltb_selector);
		btn_shoucang = (TextView)this.findViewById(R.id.title_btn_right);
		btn_shoucang.setVisibility(View.VISIBLE);
		btn_shoucang.setBackgroundResource(R.drawable.shoucang_selector);
		txt_title = (TextView) this
				.findViewById(R.id.title_zbcy_tv);// 美食标题
		txt_value = (TextView) this
				.findViewById(R.id.youhui_text);// 优惠
		txt_sjjs = (TextView) this
				.findViewById(R.id.jsnr_tv);// 商家介绍
		sjjs_tv = (TextView) this.findViewById(R.id.sjjs_tv);
		if("6".equals(select_entity.list_type)){
			sjjs_tv.setText("产品介绍");
		}
		if("7".equals(select_entity.list_type)){
			sjjs_tv.setText("商家介绍");
		}
		txt_addr = (TextView) this
				.findViewById(R.id.dw_tv);// 美食地址
		img = (ImageView) this.findViewById(R.id.img_zbcy_iv);
        text_user1 = (TextView)this.findViewById(R.id.plnr_1);
        text_user1nr = (TextView)this.findViewById(R.id.plnr_1_tv);
        text_user2 = (TextView)this.findViewById(R.id.plnr_2);
        text_user2nr = (TextView)this.findViewById(R.id.plnr_2_tv);
        text_user3 = (TextView)this.findViewById(R.id.plnr_3);
        text_user3nr = (TextView)this.findViewById(R.id.plnr_3_tv);
        pl_count = (TextView)this.findViewById(R.id.plcount_tv);
        dingwei_rl = (RelativeLayout)this.findViewById(R.id.dingwei_rl);
//        xianlu_rl = (RelativeLayout)this.findViewById(R.id.xianlu_rl);
		eblist_routeQuery = (ExpandableListView) findViewById(R.id.expandableListView);
		listName = getResources().getStringArray(R.array.routeQuery);
		childTitle = getResources().getStringArray(R.array.queryMode);
		
        xx_v1 = (View)this.findViewById(R.id.xuxian_ve6);
        xx_v2 = (View)this.findViewById(R.id.xuxian_ve7);
        
        zwpl_rl = (RelativeLayout)this.findViewById(R.id.zwpl_rl);
        plnr_rl = (RelativeLayout)this.findViewById(R.id.plnr_rl);
        pl_btn = (Button)this.findViewById(R.id.ckqbpl_tv);
		phone = (TextView)this.findViewById(R.id.phone_text);
		
		
		phone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
						+select_entity.phone));  //从后台接受手机号码
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);					
			}
		});
		
		if (select_entity != null) {

			txt_title.setText(select_entity.title);
            txt_value.setText(Double.parseDouble(select_entity.other)*100+"");
            txt_sjjs.setText(select_entity.des);
            txt_addr.setText(select_entity.address);
            text_phone.setText(select_entity.phone);

			// System.out.println("img_url:" + img_url);

			// 新方法
			// bitmap加载就这一行代码，display还有其他重载，详情查看源码
			if (select_entity.icon != null && select_entity.icon.length() > 10) {
				String img_url = select_entity.icon.split(",")[0].trim();
				ShareApplication.mImageWorker.loadImage(img_url, img);
				img.setVisibility(View.VISIBLE);
			} else {
				img.setImageResource(R.drawable.list_qst);
				img.setVisibility(View.VISIBLE);
			}
			/*
			 * 旧方法； BitmapHelper.fetchImage2(this, img_url.trim(), new
			 * OnFetchCompleteListener2() {
			 * 
			 * @Override public void onFetchComplete(Object arg0, Bitmap arg1) {
			 * // TODO Auto-generated method stub if (arg1 != null) {
			 * img_news.setImageBitmap(arg1); } else {
			 * 
			 * // 无数据默认显示的图片 Bitmap
			 * bitmap=BitmapFactory.decodeResource(InformationMsgActivity
			 * .this.getResources(), R.drawable.listview_for_default);
			 * img_news.setImageBitmap(bitmap);
			 * 
			 * img_news.setImageDrawable(InformationMsgActivity.this.getResources
			 * ().getDrawable(R.drawable.listview_for_default));
			 * 
			 * 
			 * } } });
			 */
		}
		
        
	}
	public void showReviews(){
		JSONObject jsonobj=null;
		try {
			 jsonobj = new JSONObject(str_reviews);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		JSONArray array=null;
		try{
         array = jsonobj.getJSONArray("results");
		}catch(Exception e){
			System.out.println(e);
		}
		int len = 0 ;
		if(array!=null){
          len = array.length();
		}
        pl_count.setText("评论"+len+"条");
        if(len>=3){
        	plnr_rl.setVisibility(View.VISIBLE);
        	zwpl_rl.setVisibility(View.GONE);
        	pl_btn.setVisibility(View.VISIBLE);
        	pl_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent i = new Intent();
					i.putExtra("allInfo", str_reviews);
					i.setClass(ShopsArticleActivity.this, ReviewAllInfoActivity.class);
					startActivity(i);
				}
			});
			JSONObject m1;
			JSONObject m2;
			JSONObject m3;
			try {
				m1 = array.getJSONObject(0);
				m2 = array.getJSONObject(1);
				m3 = array.getJSONObject(2);
				text_user1.setText(m1.getString("nickname")+" :");
				text_user2.setText(m2.getString("nickname")+" :");
				text_user3.setText(m3.getString("nickname")+" :");
				text_user1nr.setText(m1.getString("content"));
				text_user2nr.setText(m2.getString("content"));
				text_user3nr.setText(m3.getString("content"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        if(len==0){
        	plnr_rl.setVisibility(View.GONE);
        	zwpl_rl.setVisibility(View.VISIBLE);
        }
        if(len==1){
			JSONObject m1;
			try {
				m1 = array.getJSONObject(0);
				plnr_rl.setVisibility(View.VISIBLE);
				zwpl_rl.setVisibility(View.GONE);  
				text_user1.setText(m1.getString("nickname")+" :");
				text_user2.setVisibility(View.GONE);
				text_user3.setVisibility(View.GONE);
				text_user1nr.setText(m1.getString("content"));
				text_user2nr.setVisibility(View.GONE);
				text_user3nr.setVisibility(View.GONE);
				xx_v1.setVisibility(View.GONE);
				xx_v2.setVisibility(View.GONE);
				pl_btn.setVisibility(View.GONE);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
        }
        if(len==2){
			JSONObject m1;
			JSONObject m2;
			try {
				m1 = array.getJSONObject(0);
				m2 = array.getJSONObject(1);
				plnr_rl.setVisibility(View.VISIBLE);
				zwpl_rl.setVisibility(View.GONE); 
				text_user1.setText(m1.getString("nickname")+" :");
				text_user2.setText(m2.getString("nickname")+" :");
				text_user3.setVisibility(View.GONE);
				text_user1nr.setText(m1.getString("content"));
				text_user2nr.setText(m2.getString("content"));
				text_user3nr.setVisibility(View.GONE);
				xx_v2.setVisibility(View.GONE);
				pl_btn.setVisibility(View.GONE);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
        }		
	}
	
	public List<Map<String, Object>> getParentList() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < listName.length; i++) {
			Map<String, Object> curGroupMap = new HashMap<String, Object>();
			list.add(curGroupMap);
			curGroupMap.put("List", listName[i]);
		}
		return list;
	}

	public List<List<Map<String, Object>>> getChildList() {
		List<List<Map<String, Object>>> list = new ArrayList<List<Map<String, Object>>>();
		for (int i = 0; i < listName.length; i++) {
			List<Map<String, Object>> children = new ArrayList<Map<String, Object>>();
			for (int j = 0; j < childTitle.length; j++) {
				Map<String, Object> curChildMap = new HashMap<String, Object>();
				children.add(curChildMap);
				curChildMap.put("Title", childTitle[j]);
				curChildMap.put("Head", headImage[j]);
			}
			list.add(children);
		}
		return list;

	}
	
	class SelectReviews2 extends AsyncTask<Void, Void, HashMap<String, Object>> {
		
		public SelectReviews2() {
		}
		
		@Override
		protected HashMap<String, Object> doInBackground(Void... params) {
//			System.out.println("请求返回：1111111111111111111111");
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			String json;
			
			String js = ShareApplication.share.getResources().getString(
					R.string.url_sel_review);
			// 加入集合
			HashMap<String, Object> mhashmap = new HashMap<String, Object>();
			try {
				json = DNDataSource.list_FromNET(js+"&objectId="+select_entity.nid+"&type=1", param);
//				PerfHelper.getStringData(PerfHelper.P_USERID)
				str_reviews = json;
				if (ShareApplication.debug) {
					// System.out.println("用户登录返回:" + json);
					
				}
				Data date = parseJson(json);
				mhashmap.put("responseCode", date.obj1);
			} catch (Exception e) {
				e.printStackTrace();
				mhashmap = null;
			}
			return mhashmap;
		}
		
		public Data parseJson(String json) throws Exception {
			Data data = new Data();
			JSONObject jsonobj = new JSONObject(json);
			if (jsonobj.has("responseCode")) {
				int code = jsonobj.getInt("responseCode");
				if (code != 0) {
					data.obj1 = jsonobj.getString("responseCode");// 返回状态
					// 0，有数据成功返回，-1，-2，无数据返回，1，返回异常
					return data;
				} else {
					data.obj1 = jsonobj.getString("responseCode");// 返回状态
				}
			}
			System.out.println("请求返回："+json);
			return data;
		}
		
		@Override
		protected void onPostExecute(HashMap<String, Object> result) {
			super.onPostExecute(result);
			Utils.dismissProcessDialog();
			System.out.println("请求返回：22222222222222222222222222222222222222");
			// 无网提示；
			if (!Utils.isNetworkAvailable(ShopsArticleActivity.this)) {
				Utils.showToast(R.string.network_error);
				return;
			}
//			if (result == null) {
//				Toast.makeText(ShopsArticleActivity.this, "评论查询失败",
//						Toast.LENGTH_SHORT).show();
//			} else if ("0".equals(result.get("responseCode"))) {
				showReviews();
//			} else {
////				Toast.makeText(ShopsArticleActivity.this, "评论查询失败",
//						Toast.LENGTH_SHORT).show();
//			}
		}
	}	
	
//	/**
//	 * 收藏
//	 * 
//	 * @author Administrator
//	 * 
//	 */
//	class CollectAync extends AsyncTask<Void, Void, HashMap<String, Object>> {
//
//		// boolean isFav;
//
//		public CollectAync() {
//		}
//
//		@Override
//		protected HashMap<String, Object> doInBackground(Void... params) {
//			// http://192.168.1.11:8080/Mineralsa/ket/addComment.action?userId=4&ishave=1&supplyId=2&relevanceId=1
//			// TODO
//			List<NameValuePair> param = new ArrayList<NameValuePair>();
//			// 用户id
//			param.add(new BasicNameValuePair("userId", userId));
//			// 收藏信息id
//			param.add(new BasicNameValuePair("collectId", select_entity.nid));
//			param.add(new BasicNameValuePair("type", select_entity.list_type));
//			String json = "";
//			String js = ShareApplication.share.getResources().getString(
//					R.string.about);
//			// 加入集合
//			HashMap<String, Object> mhashmap = new HashMap<String, Object>();
//			try {
//				json = DNDataSource.list_FromNET(js, param);
//				if (ShareApplication.debug) {
//					System.out.println("收藏返回:" + json);
//				}
//				Data date = parseJson(json);
//				mhashmap.put("responseCode", date.obj1);
//			} catch (NotFoundException e) {
//				e.printStackTrace();
//				mhashmap = null;
//			} catch (Exception e) {
//				e.printStackTrace();
//				mhashmap = null;
//			}
//			return mhashmap;
//		}
//
//		public Data parseJson(String json) throws Exception {
//			Data data = new Data();
//			JSONObject jsonobj = new JSONObject(json);
//			if (jsonobj.has("responseCode")) {
//				int code = jsonobj.getInt("responseCode");
//				if (code != 0) {
//					data.obj1 = jsonobj.getString("responseCode");// 总数
//					return data;
//				} else {
//					data.obj1 = jsonobj.getString("responseCode");// 总数
//				}
//			}
////			if (jsonobj.has("countNum")) {
////				data.obj = jsonobj.getString("countNum");// 总数
////			}
//
//			return data;
//		}
//
//		@Override
//		protected void onPostExecute(HashMap<String, Object> result) {
//			super.onPostExecute(result);
//			Utils.dismissProcessDialog();
//			if ("0".equals(result.get("responseCode"))) {
//				Toast.makeText(ShopsArticleActivity.this, "信息收藏成功", Toast.LENGTH_SHORT)
//						.show();
////				btn_fav.setImageResource(R.drawable.faved_btn_s);
//				collect = true;
//			} else if ("1".equals(result.get("responseCode"))) {
//				Toast.makeText(ShopsArticleActivity.this, "收藏失败", Toast.LENGTH_SHORT)
//						.show();
//			} else if("2".equals(result.get("responseCode"))){
//				Toast.makeText(ShopsArticleActivity.this, "已经收藏", Toast.LENGTH_SHORT)
//				.show();
//				
//			}
//
//		}
//	}
//
//	/**
//	 * 查询是否收藏
//	 * 
//	 * @author Administrator
//	 * 
//	 */
//	class inquiryCollect extends AsyncTask<Void, Void, HashMap<String, Object>> {
//
//		public inquiryCollect() {
//		}
//
//		@Override
//		protected HashMap<String, Object> doInBackground(Void... params) {
//
//			List<NameValuePair> param = new ArrayList<NameValuePair>();
//			String json;
//
//			String js = ShareApplication.share.getResources().getString(
//					R.string.about);
//			// 加入集合
//			HashMap<String, Object> mhashmap = new HashMap<String, Object>();
//			try {
//				json = DNDataSource.list_FromNET(js + "userId=" + userId
//						+ "&collectId=" + select_entity.nid + "&type="
//						+ select_entity.list_type, param);
//				if (ShareApplication.debug) {
//					System.out.println("判断是否收藏返回:" + json);
//				}
//				String str = parseJson(json);
//				mhashmap.put("isCollect", str);
//			} catch (Exception e) {
//				e.printStackTrace();
//				mhashmap = null;
//			}
//			return mhashmap;
//		}
//
//		@SuppressWarnings("unchecked")
//		public String parseJson(String json) throws Exception {
//			String str = "";
//
//			JSONObject jsonobj = new JSONObject(json);
//			if (jsonobj.has("responseCode")) {
//				str = jsonobj.getString("responseCode");
//			}
//			return str;
//		}
//
//		@Override
//		protected void onPostExecute(HashMap<String, Object> result) {
//			super.onPostExecute(result);
//			Utils.dismissProcessDialog();
//			// 无网提示；
//			if (!Utils.isNetworkAvailable(ShopsArticleActivity.this)) {
//				Utils.showToast(R.string.network_error);
//				return;
//			}
//			if (result == null) {
//				Toast.makeText(ShopsArticleActivity.this, "请求失败，请稍后再试",
//						Toast.LENGTH_SHORT).show();
//			} else if ("1".equals(result.get("isCollect").toString())) {
////				btn_fav.setImageResource(R.drawable.faved_btn_s);
//				collect = true;
//			} else {
////				btn_fav.setImageResource(R.drawable.faved_btn_n);
//			}
//		}
//	}
//
//	/**
//	 * 删除收藏信息
//	 * 
//	 * @author Administrator
//	 * 
//	 */
//	class delectCollect extends AsyncTask<Void, Void, HashMap<String, Object>> {
//
//		public delectCollect() {
//		}
//
//		@Override
//		protected HashMap<String, Object> doInBackground(Void... params) {
//
//			List<NameValuePair> param = new ArrayList<NameValuePair>();
//			String json;
//			String js = ShareApplication.share.getResources().getString(
//					R.string.about);
//			// 加入集合
//			HashMap<String, Object> mhashmap = new HashMap<String, Object>();
//			try {
//				json = DNDataSource.list_FromNET(js + "userId=" + userId
//						+ "&collectId=" + select_entity.nid + "&type="
//						+ select_entity.list_type, param);
//				if (ShareApplication.debug) {
//					System.out.println("删除收藏返回:" + json);
//
//				}
//				String str = parseJson(json);
//				mhashmap.put("responseCode", str);
//			} catch (Exception e) {
//				e.printStackTrace();
//				mhashmap = null;
//			}
//			return mhashmap;
//		}
//
//		@SuppressWarnings("unchecked")
//		public String parseJson(String json) throws Exception {
//			String str = "";
//			JSONObject jsonobj = new JSONObject(json);
//			if (jsonobj.has("responseCode")) {
//				str = jsonobj.getString("responseCode");
//			}
//			return str;
//		}
//
//		@Override
//		protected void onPostExecute(HashMap<String, Object> result) {
//			super.onPostExecute(result);
//			Utils.dismissProcessDialog();
//			// 无网提示；
//			if (!Utils.isNetworkAvailable(ShopsArticleActivity.this)) {
//				Utils.showToast(R.string.network_error);
//				return;
//			}
//			if (result == null) {
//				Toast.makeText(ShopsArticleActivity.this, "请求失败，请稍后再试",
//						Toast.LENGTH_SHORT).show();
//			} else if ("0".equals(result.get("responseCode").toString())) {
//				Toast.makeText(ShopsArticleActivity.this, "取消收藏成功", Toast.LENGTH_SHORT)
//						.show();
//				collect = false;
//			} else {
//				Toast.makeText(ShopsArticleActivity.this, "取消收藏失败", Toast.LENGTH_SHORT)
//						.show();
//			}
//		}
//	}	
}
