package com.pyxx.part_activiy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.pyxx.adapter.ColorsAdapter;
import com.pyxx.adapter.SizesAdapter;
import com.pyxx.app.ShareApplication;
import com.pyxx.datasource.DNDataSource;
import com.pyxx.entity.Data;
import com.pyxx.entity.DisplayUtil;
import com.pyxx.entity.Listitem;
import com.pyxx.loadimage.Utils;
import com.pyxx.view.CollectUtil;
import com.pyxx.view.HorizontalListView;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.PerfHelper;

/**
 * 商品详情界面
 * 
 * @author wll
 */
public class GoodsArticleActivity extends Activity {

	private View btn_back;// 返回按钮
	private View btn_shoucang;// 收藏按钮
	private TextView txt_title, txt_value, txt_share, txt_phone,
			txt_shops_name, txt_summary;
	private TextView count_text, gwc_btn;

	private RelativeLayout collect_rl;

	Listitem select_entity;
	private GridView list_imgs = null;
	private ListAdapter adapter;
	String[] allImgs;
	private WindowManager windowManager;
	private Display display;
	private TextView jrgwc_btn, ljgm_btn;
	private LinearLayout liear = null;
	private int leng = 0;

	private PopupWindow popup;
	HorizontalListView sizeListView, colorListView;
	ColorsAdapter colorAdapter;
	SizesAdapter sizeAdapter;
	ArrayList<Listitem> size_category;
	ArrayList<Listitem> color_category;
	private int total = 1;// 商品数量
	private Button confirmBtn;
	private TextView textCount, textPrice;

	private static final String FILE_NAME = "/main_ico.jpg";
	private String testImage;
	private String colorId = "0";
	private String sizeId = "0";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 接收传从上一个页面传过来的值
		if (getIntent().getExtras() != null) {
			select_entity = (Listitem) getIntent().getExtras().get("item");
		}

		setContentView(R.layout.activity_article_goods);

		windowManager = this.getWindowManager();
		display = windowManager.getDefaultDisplay();
		findViewById(R.id.goods_detail_back).setVisibility(View.VISIBLE);

		// 初始化所有控件
		initView();

		// 控件对应注册事件
		// 返回按钮
		btn_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GoodsArticleActivity.this.finish();
			}
		});

		// 加入购物车
		jrgwc_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!PerfHelper.getBooleanData(PerfHelper.P_USER_LOGIN)) {
					Utils.showToast("请先登录再加入购物车");
				} else {

					ShowPoppu(v);
					/*
					 * Intent intent = new Intent();
					 * intent.setClass(GoodsArticleActivity.this,
					 * AddGoodsActivity.class); intent.putExtra("item",
					 * select_entity); startActivity(intent);
					 */
				}
			}
		});

		// 立即购买
		ljgm_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!PerfHelper.getBooleanData(PerfHelper.P_USER_LOGIN)) {
					Utils.showToast("请先登录再购买");
				} else {
					Intent intent = new Intent();
					intent.setClass(GoodsArticleActivity.this,
							AddGoodsActivity.class);
					intent.putExtra("item", select_entity);
					intent.putExtra("statue", "2");
					startActivity(intent);
				}
			}
		});

		// 查看购物车
		gwc_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!PerfHelper.getBooleanData(PerfHelper.P_USER_LOGIN)) {
					Utils.showToast("请先登录查看购物车");
				} else {
					if (PerfHelper.getStringData(PerfHelper.P_SHARE_SEX) == null
							|| "".equals(PerfHelper
									.getStringData(PerfHelper.P_SHARE_SEX))
							|| PerfHelper.getStringData(PerfHelper.P_SHARE_SEX) == "0") {
						Utils.showToast("您还没有添加购物信息");
					} else {
						Intent intent = new Intent();
						intent.setClass(GoodsArticleActivity.this,
								ShopCarActivity.class);
						intent.putExtra("item", select_entity);
						startActivity(intent);
					}
				}
			}
		});
		// 分享按钮
		txt_share.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				showShare();

			}
		});

		// 点击查询美食图片
		// img.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // 点击进入到另外一个控件，查看更多的新闻图片，通过Gallery实现
		// Intent intent = new Intent();
		// intent.setClass(GoodsArticleActivity.this,
		// ShowImgActivity.class);
		// intent.putExtra("item", select_entity);
		// intent.putExtra("img_list", select_entity.icon.split(","));
		// startActivity(intent);
		// }
		// });
	}

	public void initView() {
		btn_back = this.findViewById(R.id.goods_detail_back);// 返回按钮

		btn_shoucang = this.findViewById(R.id.shoucang_iv);// 收藏按钮
		new CollectUtil(GoodsArticleActivity.this, true, select_entity,
				btn_shoucang).initCollect();

		txt_title = (TextView) this.findViewById(R.id.title_zbcy_tv);// 美食标题
		txt_value = (TextView) this.findViewById(R.id.jiage_tv);// 价格
		txt_share = (TextView) this.findViewById(R.id.title_share);// 分享按钮

		gwc_btn = (TextView) findViewById(R.id.gwc_btn);// 购物车
		count_text = (TextView) findViewById(R.id.count_text);// 商品数量

		jrgwc_btn = (TextView) this.findViewById(R.id.jrgwc_btn); // 加入购物车按钮
		ljgm_btn = (TextView) this.findViewById(R.id.ljgm_btn); // 立即购买按钮

		collect_rl = (RelativeLayout) this.findViewById(R.id.collect_rl);// 收藏按钮

		txt_phone = (TextView) this.findViewById(R.id.phone_text);// 联系电话
		txt_shops_name = (TextView) this.findViewById(R.id.shops_name_text);// 商家名称
		txt_summary = (TextView) this.findViewById(R.id.summary_text);// 商家简介

		ImageView goods_icon = (ImageView) this.findViewById(R.id.goods_icon);
		if (select_entity.icon != null && select_entity.icon.length() > 10) {
			String img_url = select_entity.icon.split(",")[0].trim();
			ShareApplication.mImageWorker.loadImage(img_url, goods_icon);
			goods_icon.setVisibility(View.VISIBLE);
		} else {
			goods_icon.setImageResource(R.drawable.list_qst);
			goods_icon.setBackgroundResource(R.drawable.list_qst);
			goods_icon.setVisibility(View.VISIBLE);
		}
		// 图片点击查看
		goods_icon.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(GoodsArticleActivity.this,
						ShowBigImgActivity.class);
				intent.putExtra("item", select_entity);
				intent.putExtra("nowCount", 1);
				startActivity(intent);
			}
		});

		/*
		 * if (PerfHelper.getStringData(PerfHelper.P_SHARE_SEX) == null ||
		 * "".equals(PerfHelper.getStringData(PerfHelper.P_SHARE_SEX))) {
		 * PerfHelper.setInfo(PerfHelper.P_SHARE_SEX, "");
		 * count_text.setVisibility(View.INVISIBLE); } else {
		 * count_text.setVisibility(View.VISIBLE); count_text
		 * .setText(PerfHelper.getStringData(PerfHelper.P_SHARE_SEX)); }
		 */
		if (!PerfHelper.getBooleanData(PerfHelper.P_USER_LOGIN)) {
			count_text.setText("0");
		}

		if (select_entity != null) {

			txt_title.setText(select_entity.title);
			txt_value.setText("￥ " + select_entity.other);
			txt_summary.setText(select_entity.shangjia);
			txt_shops_name.setText("商家名称：" + select_entity.other2);
			txt_phone.setText("联系电话：" + select_entity.phone);

		}
	}

	/**
	 * 显示分享dialog
	 */
	private void showShare() {

		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();

		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		oks.setSilent(false); // 显示编辑页面
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(getString(R.string.share));

		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl("http://api.tcshenghuo.org:9999/ZGClothingAndShoes/");

		// text是分享文本，所有平台都需要这个字段
		oks.setText(select_entity.title + select_entity.icon);
		// imageUrl是分享的图片
		oks.setImageUrl(select_entity.icon);
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		oks.setImagePath(testImage);
		oks.setFilePath(testImage);

		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://api.tcshenghuo.org:9999/ZGClothingAndShoes/");

		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		// oks.setComment("我是测试评论文本");

		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl("http://api.tcshenghuo.org:9999/ZGClothingAndShoes/");

		// 令编辑页面显示为Dialog模式
		oks.setDialogMode();

		// 启动分享GUI
		oks.show(this);
	}

	/**
	 * 加入购物车PopupWindow
	 * 
	 * @param view
	 */
	public void ShowPoppu(View view) {
		LayoutInflater inflater = LayoutInflater.from(this);
		final View viewContent = inflater.inflate(R.layout.bottom_popupwindow,
				null);
		popup = new PopupWindow(viewContent, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);

		// 这个是为了点击“返回Back”也能使popup消失，并且并不会影响背景
		popup.setBackgroundDrawable(new BitmapDrawable());
		// 加上这个popupwindow中的ListView才可以接收点击事件
		popup.setFocusable(true);
		popup.setOutsideTouchable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		popup.setAnimationStyle(R.style.AnimBottom);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		// 设置SelectPicPopupWindow弹出窗体的背景
		popup.setBackgroundDrawable(dw);

		popup.showAtLocation(view, Gravity.BOTTOM, 0, 0);
		viewContent.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int height = viewContent.findViewById(R.id.pop_layout).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						popup.dismiss();
					}
				}
				return true;
			}
		});

		textCount = (TextView) viewContent.findViewById(R.id.popup_count);
		textPrice = (TextView) viewContent.findViewById(R.id.popup_jiage);
		textPrice.setText(select_entity.other);
		confirmBtn = (Button) viewContent.findViewById(R.id.popup_add_btn);
		sizeListView = (HorizontalListView) viewContent
				.findViewById(R.id.size_listview);
		colorListView = (HorizontalListView) viewContent
				.findViewById(R.id.color_listview);
		sizeListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long id) {
				Object object = adapter.getAdapter().getItem(position);
				if (object instanceof Listitem) {
					Listitem listitem = (Listitem) object;
					sizeId = listitem.nid;
				}
				sizeAdapter.setSelectIndex(position);
				sizeAdapter.notifyDataSetChanged();
			}
		});

		colorListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long id) {
				Object object = adapter.getAdapter().getItem(position);
				if (object instanceof Listitem) {
					Listitem listitem = (Listitem) object;
					colorId = listitem.nid;
				}
				colorAdapter.setSelectIndex(position);
				colorAdapter.notifyDataSetChanged();
			}
		});

		new SelectSize().execute((Void) null);
		new SelectColor().execute((Void) null);

		// 加(+)按钮
		viewContent.findViewById(R.id.popup_jia).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						total += 1;
						textCount.setText(total + "");
						textPrice.setText("￥" + total
								* (Double.parseDouble(select_entity.other)));
					}
				});
		// 减(-)按钮
		viewContent.findViewById(R.id.popup_jian).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (total > 1) {
							total -= 1;
						} else {
							total = 1;
						}
						textCount.setText(total + "");
						textPrice.setText("￥" + total
								* (Double.parseDouble(select_entity.other)));
					}
				});
		// 确认添加到购物车
		confirmBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				new AddShopsCar().execute((Void) null);

			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (PerfHelper.getBooleanData(PerfHelper.P_USER_LOGIN)) {
			if (!"".equals(PerfHelper.getStringData(PerfHelper.P_SHARE_SEX))
					&& PerfHelper.getStringData(PerfHelper.P_SHARE_SEX) != null) {
				count_text.setVisibility(View.VISIBLE);
				count_text.setText(PerfHelper
						.getStringData(PerfHelper.P_SHARE_SEX));
			} else {
				count_text.setText("0");
			}
		}
	}

	/**
	 * 查看图片 ListAdapter
	 * 
	 * @author sy
	 * 
	 */
	private class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private String[] imgs;

		public ListAdapter(Context context, String[] imgs) {

			this.inflater = LayoutInflater.from(context);
			this.imgs = imgs;
		}

		@Override
		public int getCount() {
			return imgs.length;
		}

		@Override
		public Object getItem(int position) {

			return imgs[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.imgs_item, null);
				holder = new ViewHolder();
				holder.iv = (ImageView) convertView.findViewById(R.id.img_iv);
				holder.rl_imgs = (RelativeLayout) convertView
						.findViewById(R.id.rl_img_item);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (imgs[position] != null && imgs[position].length() > 10) {
				String[] iconlist = imgs[position].split(",");
				ShareApplication.mImageWorker.loadImage(iconlist[0], holder.iv);
			} else {
				holder.iv.setImageResource(R.drawable.list_qst);
			}
			holder.iv.setVisibility(View.VISIBLE);
			holder.rl_imgs.setOnClickListener(new TextViewListener(position));

			return convertView;
		}

		private class ViewHolder {
			ImageView iv;
			RelativeLayout rl_imgs;
		}

		private class TextViewListener implements View.OnClickListener {
			int position;

			public TextViewListener(int position) {
				this.position = position;
			}

			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(GoodsArticleActivity.this,
						ShowBigImgActivity.class);
				intent.putExtra("allImgs", allImgs);
				intent.putExtra("nowCount", position);
				startActivity(intent);
			}
		}
	}

	/**
	 * 查询商品图片
	 * 
	 * @author wll
	 */
	class SelectImgs extends AsyncTask<Void, Void, HashMap<String, Object>> {

		public SelectImgs() {
		}

		@Override
		protected HashMap<String, Object> doInBackground(Void... params) {

			List<NameValuePair> param = new ArrayList<NameValuePair>();
			String json;
			// 加入集合
			HashMap<String, Object> mhashmap = new HashMap<String, Object>();
			try {
				String jsurl = ShareApplication.share.getResources().getString(
						R.string.url_sel_imgs)
						+ "img.menuId="
						+ select_entity.list_type
						+ "&img.pid="
						+ select_entity.nid;
				json = DNDataSource.list_FromNET(jsurl, param);
				if (ShareApplication.debug) {
					System.out.println("图片轮播返回:" + json);
				}
				Data date = parseJson(json);
				mhashmap.put("responseCode", date.obj1);

			} catch (Exception e) {
				e.printStackTrace();
				mhashmap = null;
			}
			return mhashmap;
		}

		@SuppressWarnings("unchecked")
		public Data parseJson(String json) throws Exception {
			Data data = new Data();
			JSONObject jsonobj = new JSONObject(json);
			data.obj1 = jsonobj.getString("code");
			try {
				JSONArray array = jsonobj.getJSONArray("lists");
				if (array != null) {
					allImgs = new String[array.length() + 1];
					allImgs[0] = select_entity.icon;
					for (int i = 1; i < array.length() + 1; i++) {
						JSONObject js = array.getJSONObject(i);
						allImgs[i] = js.getString("url");
					}
				} else {
					if (select_entity.icon != null
							&& !"".equals(select_entity.icon)) {
						allImgs = new String[1];
						allImgs[0] = select_entity.icon;
					} else {
						allImgs = new String[0];
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return data;
		}

		@Override
		protected void onPostExecute(HashMap<String, Object> result) {
			super.onPostExecute(result);
			Utils.dismissProcessDialog();
			// 无网提示；
			if (!Utils.isNetworkAvailable(GoodsArticleActivity.this)) {
				Utils.showToast(R.string.network_error);
				return;
			}

			if ("1".equals(result.get("responseCode"))) {
				leng = allImgs.length;

				if (leng * DisplayUtil.dip2px(GoodsArticleActivity.this, 150) < display
						.getWidth()) {
					liear.setLayoutParams(new FrameLayout.LayoutParams(display
							.getWidth(), DisplayUtil.dip2px(
							GoodsArticleActivity.this, 150)));
				} else {
					liear.setLayoutParams(new FrameLayout.LayoutParams(leng
							* DisplayUtil
									.dip2px(GoodsArticleActivity.this, 160),
							DisplayUtil.dip2px(GoodsArticleActivity.this, 150)));
				}
				adapter = new ListAdapter(GoodsArticleActivity.this, allImgs);
				list_imgs.setAdapter(adapter);
			} else {
			}
		}
	}

	/**
	 * 查询商品尺寸
	 * 
	 * @author wll
	 */
	class SelectSize extends AsyncTask<Void, Void, HashMap<String, Object>> {

		public SelectSize() {

		}

		@Override
		protected HashMap<String, Object> doInBackground(Void... params) {

			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("commodity.id", select_entity.nid));
			pairs.add(new BasicNameValuePair("commodity.type", "3"));

			String json;
			String url = ShareApplication.share.getResources().getString(
					R.string.url_get_shopsize);
			// 加入集合
			HashMap<String, Object> mHashMap = new HashMap<String, Object>();
			try {
				json = DNDataSource.list_FromNET(url, pairs);
				if (ShareApplication.debug) {
					System.out.println("商品尺寸返回:" + json);
				}
				Data data = parseJson(json);
				mHashMap.put("responseCode", data.obj1);
				mHashMap.put("result", data.list);
			} catch (Exception e) {
				e.printStackTrace();
				mHashMap = null;
			}
			return mHashMap;
		}

		public Data parseJson(String json) throws Exception {
			Data data = new Data();
			JSONObject object = new JSONObject(json);
			if (object.has("code")) {
				int code = object.getInt("code");
				if (code != 1) {
					data.obj1 = object.getString("code");
					return data;
				} else {
					data.obj1 = object.getString("code");
				}
			}
			JSONArray jsonArray = object.getJSONArray("lists");
			int count = jsonArray.length();
			for (int i = 0; i < count; i++) {
				Listitem list = new Listitem();
				JSONObject jsonOb = jsonArray.getJSONObject(i);
				list.nid = jsonOb.getString("id");
				try {
					if (jsonOb.has("name")) {
						list.title = jsonOb.getString("name");
					}
					if (jsonOb.has("type")) {
						list.other = object.getString("type");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				list.getMark();
				data.list.add(list);
			}
			return data;
		}

		@Override
		protected void onPostExecute(HashMap<String, Object> result) {
			super.onPostExecute(result);
			Utils.dismissProcessDialog();
			// 无网提示；
			if (!Utils.isNetworkAvailable(GoodsArticleActivity.this)) {
				Utils.showToast(R.string.network_error);
				return;
			}
			if (result == null) {
				Toast.makeText(GoodsArticleActivity.this, "请求失败，请稍后再试",
						Toast.LENGTH_SHORT).show();
			} else if ("1".equals(result.get("responseCode"))) {
				size_category = (ArrayList<Listitem>) result.get("result");
				sizeAdapter = new SizesAdapter(GoodsArticleActivity.this,
						size_category);
				sizeListView.setAdapter(sizeAdapter);
			} else {
				Toast.makeText(GoodsArticleActivity.this, "查询商品尺寸失败",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * 查询商品颜色
	 * 
	 * @author wll
	 */
	class SelectColor extends AsyncTask<Void, Void, HashMap<String, Object>> {

		public SelectColor() {

		}

		@Override
		protected HashMap<String, Object> doInBackground(Void... params) {

			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("commodity.id", select_entity.nid));
			pairs.add(new BasicNameValuePair("commodity.type", "2"));

			String json;
			String url = ShareApplication.share.getResources().getString(
					R.string.url_get_shopsize);
			// 加入集合
			HashMap<String, Object> mHashMap = new HashMap<String, Object>();
			try {
				json = DNDataSource.list_FromNET(url, pairs);
				if (ShareApplication.debug) {
					System.out.println("商品尺寸返回:" + json);
				}
				Data data = parseJson(json);
				mHashMap.put("responseCode", data.obj1);
				mHashMap.put("result", data.list);
			} catch (Exception e) {
				e.printStackTrace();
				mHashMap = null;
			}
			return mHashMap;
		}

		public Data parseJson(String json) throws Exception {
			Data data = new Data();
			JSONObject object = new JSONObject(json);
			if (object.has("code")) {
				int code = object.getInt("code");
				if (code != 1) {
					data.obj1 = object.getString("code");
					return data;
				} else {
					data.obj1 = object.getString("code");
				}
			}
			JSONArray jsonArray = object.getJSONArray("lists");
			int count = jsonArray.length();
			for (int i = 0; i < count; i++) {
				Listitem list = new Listitem();
				JSONObject jsonOb = jsonArray.getJSONObject(i);
				list.nid = jsonOb.getString("id");
				try {
					if (jsonOb.has("name")) {
						list.title = jsonOb.getString("name");
					}
					if (jsonOb.has("type")) {
						list.other = object.getString("type");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				list.getMark();
				data.list.add(list);
			}
			return data;
		}

		@Override
		protected void onPostExecute(HashMap<String, Object> result) {
			super.onPostExecute(result);
			Utils.dismissProcessDialog();
			// 无网提示；
			if (!Utils.isNetworkAvailable(GoodsArticleActivity.this)) {
				Utils.showToast(R.string.network_error);
				return;
			}
			if (result == null) {
				Toast.makeText(GoodsArticleActivity.this, "请求失败，请稍后再试",
						Toast.LENGTH_SHORT).show();
			} else if ("1".equals(result.get("responseCode"))) {
				color_category = (ArrayList<Listitem>) result.get("result");
				colorAdapter = new ColorsAdapter(GoodsArticleActivity.this,
						color_category);
				colorListView.setAdapter(colorAdapter);
			} else {
				Toast.makeText(GoodsArticleActivity.this, "查询商品颜色失败",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * 加入购物车 数据
	 * 
	 * @author wll
	 */
	class AddShopsCar extends AsyncTask<Void, Void, HashMap<String, Object>> {

		/**
		 * 添加商品至购物车
		 */
		public AddShopsCar() {
		}

		@Override
		protected HashMap<String, Object> doInBackground(Void... params) {

			List<NameValuePair> param = new ArrayList<NameValuePair>();
			String json;

			String js = ShareApplication.share.getResources().getString(
					R.string.url_save_shopscar);
			// 加入集合
			HashMap<String, Object> mhashmap = new HashMap<String, Object>();
			try {
				json = DNDataSource.list_FromNET(js + "shop.userId="
						+ PerfHelper.getStringData(PerfHelper.P_USERID)
						+ "&shop.commodityId=" + select_entity.nid
						+ "&shop.menuId=" + select_entity.fuwu + "&shop.num="
						+ total + "&shop.types=" + sizeId + "," + colorId,
						param);
				Data date = parseJson(json);
				mhashmap.put("responseCode", date.obj1);
			} catch (Exception e) {
				e.printStackTrace();
				mhashmap = null;
			}
			return mhashmap;
		}

		// @SuppressWarnings("unchecked")
		public Data parseJson(String json) throws Exception {
			Data data = new Data();
			JSONObject jsonobj = new JSONObject(json);
			if (jsonobj.has("code")) {
				int code = jsonobj.getInt("code");
				if (code != 1) {
					data.obj1 = jsonobj.getString("code");// 返回状态
					// 0，有数据成功返回，-1，-2，无数据返回，1，返回异常
					return data;
				} else {
					data.obj1 = jsonobj.getString("code");// 返回状态
				}
			}
			return data;
		}

		@Override
		protected void onPostExecute(HashMap<String, Object> result) {
			super.onPostExecute(result);
			Utils.dismissProcessDialog();
			// 无网提示；
			if (!Utils.isNetworkAvailable(GoodsArticleActivity.this)) {
				Utils.showToast(R.string.network_error);
				return;
			}
			if (result == null) {
				Toast.makeText(GoodsArticleActivity.this, "加入购物车失败",
						Toast.LENGTH_SHORT).show();
			} else if ("1".equals(result.get("responseCode"))) {
				Toast.makeText(GoodsArticleActivity.this, "加入购物车成功",
						Toast.LENGTH_SHORT).show();
				if (PerfHelper.getStringData(PerfHelper.P_SHARE_SEX) == null
						|| "".equals(PerfHelper
								.getStringData(PerfHelper.P_SHARE_SEX))) {
					PerfHelper.setInfo(PerfHelper.P_SHARE_SEX, "1");
				} else {
					String strss = PerfHelper
							.getStringData(PerfHelper.P_SHARE_SEX);
					int allcount = Integer.parseInt(strss) + 1;
					PerfHelper.setInfo(PerfHelper.P_SHARE_SEX, allcount + "");
				}

				if (PerfHelper.getStringData(PerfHelper.P_SHARE_SEX) == null
						|| "".equals(PerfHelper
								.getStringData(PerfHelper.P_SHARE_SEX))) {
					PerfHelper.setInfo(PerfHelper.P_SHARE_SEX, "");
					count_text.setVisibility(View.INVISIBLE);
				} else {
					count_text.setVisibility(View.VISIBLE);
					count_text.setText(PerfHelper
							.getStringData(PerfHelper.P_SHARE_SEX));
				}
				popup.dismiss();
			} else {
				Toast.makeText(GoodsArticleActivity.this, "加入购物车失败",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

}
