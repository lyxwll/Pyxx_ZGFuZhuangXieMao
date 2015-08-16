package com.pyxx.ui;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.pyxx.app.ShareApplication;
import com.pyxx.baseui.BaseInitAcitvity;
import com.pyxx.dao.DBHelper;
import com.pyxx.datasource.DNDataSource;
import com.pyxx.entity.CityLifeApplication;
import com.pyxx.entity.Data;
import com.pyxx.entity.Listitem;
import com.pyxx.loadimage.Utils;
import com.pyxx.part_asynctask.GetAllAreaThread;
import com.pyxx.part_asynctask.GetAllCityThread;
import com.pyxx.part_asynctask.GetAllProvinceThread;
import com.pyxx.part_asynctask.GetAllTypeThread;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.FinalVariable;
import com.utils.PerfHelper;

/**
 * 初始化 程序界面
 * 
 * @author wll
 */

public class InitAcitvity extends BaseInitAcitvity {
	/**
	 * 注：在这个方法必须被调用，并在这个方法里面初始化应用程序信息，如PID，缓存文件名等
	 */
	Context mContext;
	boolean isfrist = true;
	private ImageView init_log;

	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(arg0);

		setContentView(R.layout.activity_init);

		mContext = this;
		new GetAllTypeThread().start();
		new GetAllProvinceThread().start();
		new GetAllCityThread().start();
		new GetAllAreaThread().start();
		// initDataUserInfo();
		// if (PerfHelper.getStringData(PerfHelper.P_USERID).equals("")) {
		// PerfHelper.setInfo(PerfHelper.P_USERID,
		// "游客");29.567342ddddddddddddddddddddd106.572127
		// }
	}

	/**
	 * 初始化客户端信息
	 */
	public void initDataUserInfo() {
		/**
		 * 打开GPS定位完成广播
		 */
		setGPSFinish();
		PerfHelper.setInfo(PerfHelper.P_PHONE_W, getResources()
				.getDisplayMetrics().widthPixels);
		PerfHelper.setInfo(PerfHelper.P_PHONE_H, getResources()
				.getDisplayMetrics().heightPixels);

		/**
		 * 开始定位
		 */
		// GpsUtils.getLocation();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case FinalVariable.addfoot:// 添加footer
				break;
			case FinalVariable.nomore:
				break;
			case FinalVariable.remove_footer:// footer删除
				break;
			case FinalVariable.error:// 网络连接出错
				if (!Utils.isNetworkAvailable(InitAcitvity.this)) {
					Utils.showToast(R.string.network_error);
					finish();
					return;
				}
				Utils.showToast(R.string.server_error);
				finish();
				break;
			case FinalVariable.update:// 数据加载完成界面更新
				begin_StartActivity();
				break;
			case FinalVariable.first_update:// 数据加载完成界面更新
				if (!citylistjson.equals("")) {
					getDate(mOldtype, mPage, true);
				}
				break;
			}
			super.handleMessage(msg);
		}
	};
	int i = 0;

	// 跳转至主界面
	public void begin_StartActivity() {
		Intent i = new Intent();
		i.setClass(InitAcitvity.this, FragmentChangeActivity.class);
		this.startActivity(i);
		finish();
	}

	public int mPage = 1;
	public int mLength = 20;
	public int mFooter_limit = mLength;

	public String mOldtype = "citys_list";
	public String mParttype = "citys_list";
	public Data mData = null;

	/**
	 * 加载城市列表
	 */
	public void initData() {
		new Thread() {
			@Override
			public void run() {
				boolean isDB = true;
				try {
					String d = getDataFromDB(mOldtype, mPage, mLength,
							mParttype);
					if (d != null && d.length() > 0) {
						citylistjson = d;
						isDB = false;
						mHandler.sendEmptyMessage(FinalVariable.first_update);
					}
					getDataFromNet(
							getResources().getString(
									R.string.citylife_citys_list_url),
							mOldtype, mPage, mLength, true, mParttype);
					if (isDB)
						mHandler.sendEmptyMessage(FinalVariable.first_update);
				} catch (Exception e) {
					mHandler.sendEmptyMessage(FinalVariable.error);
				} finally {
				}
			}
		}.start();
	}

	private String citylistjson = "";

	public String getDataFromDB(String oldtype, int page, int count,
			String parttype) throws Exception {
		String json = DNDataSource.list_FromDB(oldtype, page, count, parttype);
		if (json == null || "".equals(json) || "null".equals(json)) {
			return null;
		}
		return json;
	}

	public void getDataFromNet(String url, String oldtype, int page, int count,
			boolean isfirst, String parttype) throws Exception {
		citylistjson = DNDataSource.CityLift_list_FromNET(url, oldtype, page,
				count, parttype, isfirst);
		if (citylistjson != null && citylistjson.length() > 0) {
			if (isfirst) {
				DBHelper.getDBHelper().delete("listinfo", "listtype=?",
						new String[] { oldtype });
			}
			DBHelper.getDBHelper()
					.insert(oldtype + page, citylistjson, oldtype);
		}
		if (ShareApplication.debug)
			System.out.println("城市列表返回:" + citylistjson);
	}

	public void getDate(String oldtype, int page, boolean isfirst) {
		try {
			mData = parseJson(citylistjson);
			if (ShareApplication.debug)
				System.out.println("当前城市ID"
						+ PerfHelper.getStringData(PerfHelper.P_CITY_No));
			mHandler.sendEmptyMessage(FinalVariable.update);
		} catch (Exception e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(FinalVariable.error);
		}

	}

	public Data parseJson(String json) throws Exception {
		Data data = new Data();
		JSONObject jsonobj = new JSONObject(json);
		if (jsonobj.has("responseCode")) {
			int code = jsonobj.getInt("responseCode");
			if (code != 0) {
				mHandler.sendEmptyMessage(FinalVariable.error);
			}
		}
		JSONArray jsonay = jsonobj.getJSONArray("results");
		int count = jsonay.length();
		for (int i = 0; i < count; i++) {
			Listitem o = new Listitem();
			JSONObject obj = jsonay.getJSONObject(i);
			o.nid = obj.getString("id");
			try {
				if (obj.has("cityName")) {
					o.title = obj.getString("cityName");
				}
				if (obj.has("cityWord")) {
					o.des = obj.getString("cityWord");
				}
				if (obj.has("author")) {
					o.author = obj.getString("author");
				}
			} catch (Exception e) {
				mHandler.sendEmptyMessage(FinalVariable.error);
			}
			if ((PerfHelper.getStringData(PerfHelper.P_CITY))
					.startsWith(o.title)) {
				PerfHelper.setInfo(PerfHelper.P_CITY_No, o.nid);
				PerfHelper.setInfo(PerfHelper.P_CITY, o.title);
			}

			o.getMark();
			data.list.add(o);
		}

		return data;
	}

	/**
	 * 根据坐标查询城市
	 */
	private MKSearch mSearch;
	private CityLifeApplication ca;
	private GeoPoint gt;

	public void getCity() {
		ca = (CityLifeApplication) getApplication();
		if (ca.mBMapManager == null) {
			ca.mBMapManager = new BMapManager(this);
			ca.mBMapManager.init(CityLifeApplication.strKey,
					new CityLifeApplication.MyGeneralListener());
		}
		String gps_long = PerfHelper.getStringData(PerfHelper.P_GPS_LONG);
		String gps_lati = PerfHelper.getStringData(PerfHelper.P_GPS_LATI);
		gt = new GeoPoint((int) (Double.parseDouble(gps_lati) * 1e6),
				(int) (Double.parseDouble(gps_long) * 1e6));
		mSearch = new MKSearch();// 获取城市
		mSearch.init(ca.mBMapManager, new myMKSearchListener());
		mSearch.reverseGeocode(gt);
	}

	public class myMKSearchListener implements MKSearchListener {

		@Override
		public void onGetAddrResult(MKAddrInfo result, int iError) {
			if (iError != 0 || result == null) {
			} else {
				if (PerfHelper.getStringData(PerfHelper.P_CITY).equals("")) {
					PerfHelper.setInfo(PerfHelper.P_CITY,
							result.addressComponents.city);// 城市
				}
				PerfHelper.setInfo(PerfHelper.P_NOW_CITY,
						result.addressComponents.city);// 城
				initData();
				// districtName = result.addressComponents.district;// 地区
				// streetName = result.addressComponents.street;// 街道
				// streetNum = result.addressComponents.streetNumber;// 街道号码
				// 设置当前城市；
				// Public_Info.getInstance().setCity_name(cityName);
				// 设置获取城市地址为true;
				if (ShareApplication.debug)
					System.out.println("通过百度地图获取当前城市："
							+ result.addressComponents.city);
			}
		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
		}

		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
		}

		@Override
		public void onGetPoiDetailSearchResult(int arg0, int arg1) {
		}

		@Override
		public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
		}

		@Override
		public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
		}

		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
		}

		@Override
		public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1,
				int arg2) {

		}
	}

	@Override
	public void gpslocateend() {
		begin_StartActivity();
	}

}
