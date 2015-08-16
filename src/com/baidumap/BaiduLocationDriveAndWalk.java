package com.baidumap;

/**
 * 结合定位SDK实现定位，并使用MyLocationOverlay绘制定位位置 同时展示如何使用自定义图标绘制并点击时弹出泡泡
 * 
 */
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.map.TransitOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.pyxx.entity.BaseUtils;
import com.pyxx.entity.CityLifeApplication;
import com.pyxx.zhongguofuzhuangxiemao.R;

/**
 * 驾车||步行 百度地图路线
 * 
 * @author wll
 */
public class BaiduLocationDriveAndWalk extends Activity {

	// 定位相关
	LocationClient mLocClient;
	LocationData locData = null;
	public MyLocationListenner myListener = new MyLocationListenner();

	// 定位图层
	locationOverlay myLocationOverlay = null;

	// 弹出泡泡图层
	private PopupOverlay pop = null;// 弹出泡泡图层，浏览节点时使用
	private TextView popupText = null;// 泡泡view
	private View viewCache = null;

	// 地图相关，使用继承MapView的MyLocationMapView目的是重写touch事件实现泡泡处理
	// 如果不处理touch事件，则无需继承，直接使用MapView即可
	// MyLocationMapView mMapView = null; // 地图View
	private MapView mMapView = null;

	private MapController mMapController = null;

	// UI相关
	ImageView requestLocButton = null;
	boolean isRequest = false;// 是否手动触发请求定位
	boolean isFirstLoc = true;// 是否首次定位
	boolean isLocationClientStop = false;
	private TextView txtv_loadlook;

	// 三条路线查询相关
	private MKSearch mSearch = null;
	private MKPlanNode endNode, startNode;
	private String travelMode = "";
	private String statrLongitude = "", startLatitude = "";// 出发地点的经纬度（用户当前位置的经纬度）
	private String endLongitude = "", endLatitude = "";// 目的地的经纬度（商家的经纬度）
	private String cityName = "重庆";// 当前所在城市名称
	RouteOverlay routeOverlay;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("my", "map_onCreate");
		super.onCreate(savedInstanceState);

		CityLifeApplication app = (CityLifeApplication) this.getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(this);
			app.mBMapManager.init(CityLifeApplication.strKey,
					new CityLifeApplication.MyGeneralListener());
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.baidu_map_locationoverlay_old);

		txtv_loadlook = (TextView) this.findViewById(R.id.txtv_loadlook_id);

		requestLocButton = (ImageView) findViewById(R.id.button1);// 定位按钮
		OnClickListener btnClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 手动定位请求
				requestLocClick();
			}
		};
		requestLocButton.setOnClickListener(btnClickListener);
		
		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);
		mMapController = mMapView.getController();
		mMapView.getController().setZoom(16);
		mMapView.getController().enableClick(true);
		mMapView.setBuiltInZoomControls(true);
		
		// 创建 弹出泡泡图层
		createPaopao();
		
		// 定位初始化
		mLocClient = new LocationClient(this);
		locData = new LocationData();
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(5000);
		mLocClient.setLocOption(option);
		mLocClient.start();

		// 定位图层初始化
		myLocationOverlay = new locationOverlay(mMapView);
		// 设置定位数据
		myLocationOverlay.setData(locData);
		// 添加定位图层
		mMapView.getOverlays().add(myLocationOverlay);
		myLocationOverlay.enableCompass();
		// 修改定位数据后刷新图层生效
		mMapView.refresh();

		Intent intent = this.getIntent();
		Bundle bundle = intent.getBundleExtra("map");
		if ("".equals(bundle) || bundle == null) {
			Toast.makeText(BaiduLocationDriveAndWalk.this, "获取的定位信息为空",
					Toast.LENGTH_LONG).show();
		} else {
			travelMode = bundle.getString("travelMode");
			statrLongitude = bundle.getString("statrLongitude");
			startLatitude = bundle.getString("startLatitude");
			endLongitude = bundle.getString("endLongitude");
			endLatitude = bundle.getString("endLatitude");
			cityName = bundle.getString("cityName");

			// statrLongitude = "106.56491499999993";// 黄花园
			// startLatitude = "29.563287";
			// endLongitude = "106.56990199999996";// 南坪
			// endLatitude = "29.525969";
			Log.i("my", "BaiduLocationDriveAndWalk");

			if ("".equals(travelMode) || "".equals(statrLongitude)
					|| "".equals(startLatitude) || "".equals(endLongitude)
					|| "".equals(endLatitude) || "".equals(cityName)) {
				Toast.makeText(BaiduLocationDriveAndWalk.this, "信息传递有空值！",
						Toast.LENGTH_LONG).show();
			} else {
				GeoPoint geoPoint = new GeoPoint(
						(int) ((Double.parseDouble(startLatitude)) * 1E6),
						(int) ((Double.parseDouble(statrLongitude)) * 1E6));
				// 设置地图显示的中心点
				// MapController controller = mMapView.getController();
				mMapController.setCenter(geoPoint);
				// mMapView.getController().enableClick(true);
				// 设置地图的缩放值
				// mMapView.getController().setZoom(12);
				// mMapView.setBuiltInZoomControls(true);
				// mMapView.setDoubleClickZooming(true);
				// 初始化搜索模块，注册事件监听
				Log.i("my", "BaiduLocationDriveAndWalk_Test111");
				mSearch = new MKSearch();
				endNode = new MKPlanNode();
				startNode = new MKPlanNode();
				mSearch.init(app.mBMapManager, new myMKSearchListener());

				// 实际使用中请对起点终点城市进行正确的设定
				if ("drive".equals(travelMode)) {
					Log.i("my", "BaiduLocationDriveAndWalk_Test333");
					txtv_loadlook.setText(getResources().getString(
							R.string.map_circuit_drive));
					// 设置起点经纬度
					startNode.pt = new GeoPoint(
							(int) ((Double.parseDouble(startLatitude)) * 1E6),
							(int) ((Double.parseDouble(statrLongitude)) * 1E6));
					// 设置终点经纬度
					endNode.pt = new GeoPoint(
							(int) ((Double.parseDouble(endLatitude)) * 1E6),
							(int) ((Double.parseDouble(endLongitude)) * 1E6));
					mSearch.drivingSearch(null, startNode, null, endNode);

				} else if ("bus".equals(travelMode)) {
					txtv_loadlook.setText(getResources().getString(
							R.string.map_circuit_bus));
					// 设置起点经纬度
					// startNode.name = startPlace;
					// endNode.name=endPlace;
					// 这里有修改
					startNode.pt = new GeoPoint(
							(int) ((Double.parseDouble(startLatitude)) * 1E6),
							(int) ((Double.parseDouble(statrLongitude)) * 1E6));
					// 设置终点经纬度
					endNode.pt = new GeoPoint(
							(int) ((Double.parseDouble(endLatitude)) * 1E6),
							(int) ((Double.parseDouble(endLongitude)) * 1E6));
					mSearch.transitSearch(cityName, startNode, endNode);

				} else if ("walk".equals(travelMode)) {
					txtv_loadlook.setText(getResources().getString(
							R.string.map_circuit_walk));
					// 设置起点经纬度
					startNode.pt = new GeoPoint(
							(int) ((Double.parseDouble(startLatitude)) * 1E6),
							(int) ((Double.parseDouble(statrLongitude)) * 1E6));
					// 设置终点经纬度
					endNode.pt = new GeoPoint(
							(int) ((Double.parseDouble(endLatitude)) * 1E6),
							(int) ((Double.parseDouble(endLongitude)) * 1E6));
					mSearch.walkingSearch(null, startNode, null, endNode);
				}
			}
		}

		findViewById(R.id.btn_bml_id).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			mMapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(),
					routeOverlay.getLonSpanE6());
			Log.i("my", "handler");
		};
	};

	public class myMKSearchListener implements MKSearchListener {
		/*
		 * 步行
		 */
		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult res, int error) {
			if (error != 0 || res == null) {
				Toast.makeText(BaiduLocationDriveAndWalk.this, "抱歉，未找到结果",
						Toast.LENGTH_SHORT).show();
				return;
			}
			routeOverlay = new RouteOverlay(
					BaiduLocationDriveAndWalk.this, mMapView);
			routeOverlay.setData(res.getPlan(0).getRoute(0));
			mMapView.getOverlays().clear();
			mMapView.getOverlays().add(routeOverlay);
			mMapView.refresh();
			handler.sendEmptyMessage(10);
			// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
			// mMapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(),
			// routeOverlay.getLonSpanE6());
			mMapView.getController().animateTo(res.getStart().pt);
		}

		/*
		 * 公交
		 */
		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult res, int error) {
			if (error != 0 || res == null) {
				Toast.makeText(BaiduLocationDriveAndWalk.this,
						"由于离目标地较近，无法查到对应线路，请参照步行线路", Toast.LENGTH_SHORT).show();
				return;
			}
			TransitOverlay routeOverlay = new TransitOverlay(
					BaiduLocationDriveAndWalk.this, mMapView);
			// 此处仅展示一个方案作为示例
			routeOverlay.setData(res.getPlan(0));
			mMapView.getOverlays().clear();
			mMapView.getOverlays().add(routeOverlay);
			mMapView.refresh();
			// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
			// mMapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(),
			// routeOverlay.getLonSpanE6());
			mMapView.getController().animateTo(res.getStart().pt);
		}

		/*
		 * 自驾
		 */
		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult res, int error) {
			// 错误号可参考MKEvent中的定义
			Log.i("my", "BaiduLocationDriveAndWalk_Test222");
			if (error != 0 || res == null) {
				Toast.makeText(BaiduLocationDriveAndWalk.this, "抱歉，未找到结果",
						Toast.LENGTH_SHORT).show();
				return;
			}
			routeOverlay = new RouteOverlay(
					BaiduLocationDriveAndWalk.this, mMapView);
			Log.i("my", "BaiduLocationDriveAndWalk_Test444");
			// 此处仅展示一个方案作为示例
			routeOverlay.setData(res.getPlan(0).getRoute(0));
			Log.i("my", "BaiduLocationDriveAndWalk_Test555");
			mMapView.getOverlays().clear();
			mMapView.getOverlays().add(routeOverlay);
			Log.i("my", "BaiduLocationDriveAndWalk_Test666");
			mMapView.refresh();
			// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
			Log.i("my", "BaiduLocationDriveAndWalk_Test777");
			handler.sendEmptyMessage(10);
			// mMapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(),
			// routeOverlay.getLonSpanE6());
			Log.i("my", "BaiduLocationDriveAndWalk_Test888");
			mMapView.getController().animateTo(res.getStart().pt);
			Log.i("my", "BaiduLocationDriveAndWalk_Test999");
		}

		@Override
		public void onGetAddrResult(MKAddrInfo arg0, int arg1) {

		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult res, int error) {
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
		public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1,
				int arg2) {

		}
	}

	/**
	 * 手动触发一次定位请求
	 */
	public void requestLocClick() {
		isRequest = true;
		mLocClient.requestLocation();
		Toast.makeText(BaiduLocationDriveAndWalk.this, "正在定位…",
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * 修改位置图标
	 * 
	 * @param marker
	 */
	public void modifyLocationOverlayIcon(Drawable marker) {
		// myLocationOverlay.setMarker() 此方法对应百度地图2.1.2版本才有
		// 当传入marker为null时，使用默认图标绘制
		myLocationOverlay.setMarker(marker);

		// 修改图层，需要刷新MapView生效
		mMapView.refresh();
	}

	/**
	 * 创建弹出泡泡图层
	 */
	public void createPaopao() {
		viewCache = getLayoutInflater()
				.inflate(R.layout.custom_text_view, null);
		popupText = (TextView) viewCache.findViewById(R.id.textcache);
		// 泡泡点击响应回调
		PopupClickListener popListener = new PopupClickListener() {
			@Override
			public void onClickedPopup(int index) {
				Log.v("click", "clickapoapo");
			}
		};
		pop = new PopupOverlay(mMapView, popListener);
		MyLocationMapView.pop = pop;
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null || isLocationClientStop)
				return;
			locData.latitude = location.getLatitude();
			locData.longitude = location.getLongitude();
			// 如果不显示定位精度圈，将accuracy赋值为0即可
			locData.accuracy = location.getRadius();
			locData.direction = location.getDerect();
			// 更新定位数据
			myLocationOverlay.setData(locData);
			// 更新图层数据执行刷新后生效
			mMapView.refresh();
			// 是手动触发请求或首次定位时，移动到定位点
			if (isRequest || isFirstLoc) {
				// 移动地图到定位点
				mMapController.animateTo(new GeoPoint(
						(int) (locData.latitude * 1e6),
						(int) (locData.longitude * 1e6)));
				isRequest = false;
			}
			// 首次定位完成
			isFirstLoc = false;
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}

	// 继承MyLocationOverlay重写dispatchTap实现点击处理
	public class locationOverlay extends MyLocationOverlay {

		public locationOverlay(MapView mapView) {

			super(mapView);
		}

		@Override
		protected boolean dispatchTap() {
			// 处理点击事件,弹出泡泡
			popupText.setBackgroundResource(R.drawable.popup);
			popupText.setText("我的位置");
			pop.showPopup(BaseUtils.getBitmapFromView(popupText), new GeoPoint(
					(int) (locData.latitude * 1e6),
					(int) (locData.longitude * 1e6)), 8);
			return true;
		}
	}

	@Override
	protected void onPause() {
		Log.i("my", "map_onPause");
		isLocationClientStop = false;
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		Log.i("my", "map_onResume");
		isLocationClientStop = false;
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		Log.i("my", "map_onDestroy");
		// 退出时销毁定位
		if (mLocClient != null)
			mLocClient.stop();
		isLocationClientStop = true;
		Log.i("my", "map_onDestroy+isLocationClientStop:"
				+ isLocationClientStop);
		mMapView.destroy();
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.i("my", "map_onSaveInstanceState");
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.i("my", "map_onRestoreInstanceState");
		super.onRestoreInstanceState(savedInstanceState);
		mMapView.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
