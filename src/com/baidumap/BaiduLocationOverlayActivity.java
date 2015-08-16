package com.baidumap;

/**
 * 结合定位SDK实现定位，并使用MyLocationOverlay绘制定位 位置, 同时展示如何使用自定义图标绘制并点击时弹出泡泡
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
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
import com.baidu.mapapi.search.MKTransitRoutePlan;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.pyxx.entity.BaseUtils;
import com.pyxx.entity.CityLifeApplication;
import com.pyxx.zhongguofuzhuangxiemao.R;

/**
 * 公交路线查询
 * 
 * @author wll
 */
public class BaiduLocationOverlayActivity extends Activity implements
		OnClickListener, OnDrawerOpenListener, OnDrawerCloseListener {

	// 动画view
	private View top_view;
	private View bottom_view;
	private Animation top_anim_in;
	private Animation top_anim_out;
	private Animation bottom_anim_in;
	private Animation bottom_anim_out;
	// 线路List
	private ListView map_line_list;
	private ListView map_juti_list;
	private List<HashMap<String, String>> line_map;
	private List<String> line_map_str;
	private List<MKTransitRoutePlan> line_mapPlan;
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
	private String cityName = "北京";// 当前所在城市名称

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CityLifeApplication app = (CityLifeApplication) this.getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(this);
			app.mBMapManager.init(CityLifeApplication.strKey,
					new CityLifeApplication.MyGeneralListener());
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.baidu_map_locationoverlay);

		findView();

		txtv_loadlook = (TextView) this.findViewById(R.id.txtv_loadlook_id);
		requestLocButton = (ImageView) findViewById(R.id.button1);
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
		mMapView.getController().setZoom(14);
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
			Toast.makeText(BaiduLocationOverlayActivity.this, "获取的定位信息为空",
					Toast.LENGTH_LONG).show();
		} else {
			travelMode = bundle.getString("travelMode");
			statrLongitude = bundle.getString("statrLongitude");
			startLatitude = bundle.getString("startLatitude");

			// statrLongitude = "108.969196";
			// startLatitude = "34.283975";

			endLongitude = bundle.getString("endLongitude");
			endLatitude = bundle.getString("endLatitude");

			// endLongitude = "108.95353";
			// endLatitude = "34.228268";

			cityName = bundle.getString("cityName");
			System.out.println("定位城市:" + cityName);
			if ("".equals(travelMode) || "".equals(statrLongitude)
					|| "".equals(startLatitude) || "".equals(endLongitude)
					|| "".equals(endLatitude) || "".equals(cityName)) {
				Toast.makeText(BaiduLocationOverlayActivity.this, "信息传递有空值！",
						Toast.LENGTH_LONG).show();
			} else {
				GeoPoint geoPoint = new GeoPoint(
						(int) ((Double.parseDouble(startLatitude)) * 1E6),
						(int) ((Double.parseDouble(statrLongitude)) * 1E6));
				// GeoPoint geoPoint = new GeoPoint((int) (d1 * 1E6),
				// (int) (d2 * 1E6));
				// 设置地图显示的中心点
				// MapController controller = mMapView.getController();
				mMapController.setCenter(geoPoint);
				// mMapView.getController().enableClick(true);
				// 设置地图的缩放值
				// mMapView.getController().setZoom(12);
				// mMapView.setBuiltInZoomControls(true);
				// mMapView.setDoubleClickZooming(true);
				// 初始化搜索模块，注册事件监听
				mSearch = new MKSearch();
				endNode = new MKPlanNode();
				startNode = new MKPlanNode();
				mSearch.init(app.mBMapManager, new myMKSearchListener());

				// 实际使用中请对起点终点城市进行正确的设定
				txtv_loadlook.setText(getResources().getString(
						R.string.map_circuit_bus));
				// 设置起点经纬度
				// startNode.name = startPlace;
				// endNode.name=endPlace;
				startNode.pt = new GeoPoint(
						(int) ((Double.parseDouble(startLatitude)) * 1E6),
						(int) ((Double.parseDouble(statrLongitude)) * 1E6));
				// 设置终点纬度进度
				endNode.pt = new GeoPoint(
						(int) ((Double.parseDouble(endLatitude)) * 1E6),
						(int) ((Double.parseDouble(endLongitude)) * 1E6));
				mSearch.transitSearch(cityName, startNode, endNode);
			}
		}
	}

	/**
	 * 交换线路
	 */
	public void changLine() {

	}

	/**
	 * 实例化
	 */
	public void findView() {
		line_map = new ArrayList<HashMap<String, String>>();
		line_mapPlan = new ArrayList<MKTransitRoutePlan>();
		map_line_list = (ListView) findViewById(R.id.map_line_list);
		map_juti_list = (ListView) findViewById(R.id.map_lines_list);
		((SlidingDrawer) findViewById(R.id.slidingdrawer))
				.setOnDrawerCloseListener(this);
		((SlidingDrawer) findViewById(R.id.slidingdrawer))
				.setOnDrawerOpenListener(this);
		map_line_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				line_map_str = new ArrayList<String>();
				TransitOverlay routeOverlay = new TransitOverlay(
						BaiduLocationOverlayActivity.this, mMapView);
				MKTransitRoutePlan plan = line_mapPlan.get(arg2);
				routeOverlay.setData(plan);
				mMapView.getOverlays().clear();
				mMapView.getOverlays().add(routeOverlay);
				mMapView.refresh();
				// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
				mMapView.getController().zoomToSpan(
						routeOverlay.getLatSpanE6(),
						routeOverlay.getLonSpanE6());
				// mMapView.getController().animateTo(res.getStart().pt);
				for (int z = 0; z < routeOverlay.getAllItem().size(); z++) {
					if (z == 0) {
					} else {
						line_map_str.add(routeOverlay.getItem(z).getTitle());
					}

				}
				HashMap<String, String> map = line_map.get(arg2);
				TextView date = (TextView) findViewById(R.id.handler_date);
				TextView title = (TextView) findViewById(R.id.handler_title);
				date.setText("全程约" + Integer.parseInt(map.get("time")) / 60
						+ "分钟");
				title.setText(map.get("title"));

				map_juti_list.setAdapter(new MapJutiAdapter());
				top_view.startAnimation(top_anim_out);
				bottom_view.startAnimation(bottom_anim_out);
			}
		});
		top_anim_in = AnimationUtils.loadAnimation(
				BaiduLocationOverlayActivity.this, R.anim.map_top_in);
		top_anim_out = AnimationUtils.loadAnimation(
				BaiduLocationOverlayActivity.this, R.anim.map_top_out);
		bottom_anim_in = AnimationUtils.loadAnimation(
				BaiduLocationOverlayActivity.this, R.anim.map_bottom_in);
		bottom_anim_out = AnimationUtils.loadAnimation(
				BaiduLocationOverlayActivity.this, R.anim.map_bottom_out);
		top_view = findViewById(R.id.map_top_views);
		bottom_view = findViewById(R.id.map_bottom_view);
		findViewById(R.id.map_change_line_btn).setOnClickListener(this);
		findViewById(R.id.map_back_img).setOnClickListener(this);
		findViewById(R.id.btn_bml_id).setOnClickListener(this);
		top_anim_in.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				top_view.setVisibility(View.VISIBLE);
				((ImageView) findViewById(R.id.map_back_img))
						.setClickable(false);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				((ImageView) findViewById(R.id.map_back_img))
						.setClickable(true);
			}
		});
		bottom_anim_in.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				bottom_view.setVisibility(View.VISIBLE);

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {

			}
		});
		top_anim_out.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				top_view.setVisibility(View.GONE);
			}
		});
		bottom_anim_out.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				bottom_view.setVisibility(View.GONE);
			}
		});
	}

	/**
	 * 按键处理
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.map_change_line_btn:

			break;
		case R.id.map_back_img:
			top_view.startAnimation(top_anim_in);
			bottom_view.startAnimation(bottom_anim_in);
			break;
		case R.id.btn_bml_id:
			finish();
			break;

		default:
			break;
		}

	}

	public class myMKSearchListener implements MKSearchListener {

		/*
		 * 步行
		 */
		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult res, int error) {
			if (error != 0 || res == null) {
				Toast.makeText(BaiduLocationOverlayActivity.this, "抱歉，未找到结果",
						Toast.LENGTH_SHORT).show();
				return;
			}
			RouteOverlay routeOverlay = new RouteOverlay(
					BaiduLocationOverlayActivity.this, mMapView);
			routeOverlay.setData(res.getPlan(0).getRoute(0));
			// mMapView.getOverlays().clear();
			mMapView.getOverlays().add(routeOverlay);
			mMapView.refresh();
			// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
			mMapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(),
					routeOverlay.getLonSpanE6());
			mMapView.getController().animateTo(res.getStart().pt);
		}

		/*
		 * 公交
		 */
		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult res, int error) {
			System.out.println("异常代码:" + error);
			if (error != 0 || res == null) {
				Toast.makeText(BaiduLocationOverlayActivity.this,
						"未找到搜索结果，请参照导航线路", Toast.LENGTH_SHORT).show();
				((TextView) findViewById(R.id.loading_text))
						.setText("未搜索到对应结果，请参照导航线路");
				findViewById(R.id.processbar).setVisibility(View.GONE);
				return;
			}

			int count = res.getNumPlan();
			line_mapPlan.clear();
			line_map.clear();
			for (int i = 0; i < count; i++) {
				HashMap<String, String> map = new HashMap<String, String>();
				MKTransitRoutePlan mkl = res.getPlan(i);
				map.put("title", mkl.getContent());
				map.put("time", mkl.getTime() + "");
				line_mapPlan.add(mkl);
				System.out.println("方案:" + mkl.getContent());
				// System.out.println("线路3--:" + route.getRouteType());
				line_map.add(map);
			}
			if (null != line_map && line_map.size() > 0) {
				map_line_list.setAdapter(new MapLineAdapter());
				findViewById(R.id.laoding_view).setVisibility(View.GONE);
			}

		}

		/*
		 * 自驾
		 */
		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult res, int error) {
			// 错误号可参考MKEvent中的定义
			if (error != 0 || res == null) {
				Toast.makeText(BaiduLocationOverlayActivity.this, "抱歉，未找到结果",
						Toast.LENGTH_SHORT).show();
				return;
			}
			RouteOverlay routeOverlay = new RouteOverlay(
					BaiduLocationOverlayActivity.this, mMapView);
			// 此处仅展示一个方案作为示例
			routeOverlay.setData(res.getPlan(0).getRoute(0));
			// mMapView.getOverlays().clear();
			mMapView.getOverlays().add(routeOverlay);
			mMapView.refresh();
			// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
			mMapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(),
					routeOverlay.getLonSpanE6());
			mMapView.getController().animateTo(res.getStart().pt);
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
			System.out.println("执行:-----------");
			arg0.getCurrentNumPois();

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
		Toast.makeText(BaiduLocationOverlayActivity.this, "正在定位…",
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
		isLocationClientStop = true;
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		isLocationClientStop = false;
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		if (mLocClient != null)
			mLocClient.stop();
		isLocationClientStop = true;
		mMapView.destroy();
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mMapView.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	class MapLineAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return line_map.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(
						BaiduLocationOverlayActivity.this).inflate(
						R.layout.listitem_map_line, null);
			}
			HashMap<String, String> map = line_map.get(position);
			TextView no = (TextView) convertView.findViewById(R.id.listitem_no);
			TextView date = (TextView) convertView
					.findViewById(R.id.listitem_date);
			TextView title = (TextView) convertView
					.findViewById(R.id.listitem_title);
			no.setText(position + 1 + "");
			date.setText("约" + Integer.parseInt(map.get("time")) / 60 + "分钟");
			title.setText(map.get("title"));
			return convertView;
		}

	}

	class MapJutiAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return line_map_str.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(
						BaiduLocationOverlayActivity.this).inflate(
						R.layout.listitem_map_line_str, null);
			}
			String titlestr = line_map_str.get(position);
			TextView title = (TextView) convertView
					.findViewById(R.id.listitem_title);
			title.setText(titlestr);
			return convertView;
		}

	}

	@Override
	public void onDrawerClosed() {
		((ImageView) findViewById(R.id.handler_img))
				.setImageResource(R.drawable.map_slid_head_h);
	}

	@Override
	public void onDrawerOpened() {
		((ImageView) findViewById(R.id.handler_img))
				.setImageResource(R.drawable.map_slid_head_n);

	}

}
