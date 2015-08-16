package com.pyxx.part_activiy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidumap.BaiduLocationDriveAndWalk;
import com.baidumap.BaiduLocationAddrActivity;
import com.baidumap.BaiduLocationOverlayActivity;
import com.pyxx.app.ShareApplication;
import com.pyxx.datasource.DNDataSource;
import com.pyxx.entity.DBCollectUtil;
import com.pyxx.entity.Data;
import com.pyxx.entity.Listitem;
import com.pyxx.loadimage.Utils;
import com.pyxx.view.CustomAlertDialog;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.PerfHelper;

/**
 * 商家详情界面
 * 
 * @author wll
 */
public class ShopsArticleEasyActivity extends Activity {
	
	private View btn_back;// 返回按钮
	private View btn_pinglun;// 评论按钮
	private TextView txt_title, txt_addr, txt_sjjs, txt_cpjs, btn_shoucang,
			pl_count, text_user1, text_user2, text_user3, text_user1nr,
			text_user2nr, text_user3nr, date_1, date_2, date_3;
	private View xx_v1, xx_v2;
	private Button pl_btn;
	private ImageView img;
	private TextView phone;
	private RelativeLayout plnr_rl, zwpl_rl, dingwei_rl, xianlu_rl;
	Listitem select_entity;
	private String userId = "";
	boolean collect = false;
	private String str_reviews = "";
	String iscollect = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_article_shops_easy);

		if (getIntent().getExtras() != null) {
			select_entity = (Listitem) getIntent().getExtras().get("item");
		}

		new SelectReviews2().execute((Void) null);

		userId = PerfHelper.getStringData(PerfHelper.P_USERID);
		findViewById(R.id.title_back).setVisibility(View.VISIBLE);

		// 初始化所有控件
		initView();

		// 获取定位需要的信息；
		statrLongitude = PerfHelper.getStringData(PerfHelper.P_GPS_LONG) + "";
		startLatitude = PerfHelper.getStringData(PerfHelper.P_GPS_LATI) + "";
		endLongitude = select_entity.longitude;
		endLatitude = select_entity.latitude;
		address = select_entity.address;

		if (PerfHelper.getBooleanData(PerfHelper.P_USER_LOGIN)) {
			String strnid = select_entity.nid;
			String listtype = select_entity.list_type;
			collect = DBCollectUtil.exit("listitemcollect", "*",
					"list_type,nid", listtype + "," + strnid);
			if (collect) {
				btn_shoucang.setBackgroundResource(R.drawable.shoucang_tb2);
			} else {
				btn_shoucang.setBackgroundResource(R.drawable.shoucang_tb1);
			}
		}

		// 返回按钮
		btn_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ShopsArticleEasyActivity.this.finish();
			}
		});
		
		// 点击评论事件
		btn_pinglun.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if ("".equals(userId) || "游客".equals(userId)) {
					Utils.showToast("请先登录再评论");
				} else {
					Intent intent = new Intent();
					intent.setClass(ShopsArticleEasyActivity.this, Review.class);
					intent.putExtra("item", select_entity);
					intent.putExtra("img_list", select_entity.icon.split(","));
					startActivity(intent);
				}
			}
		});

		// 点击收藏美食
		btn_shoucang.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if ("".equals(userId) || "游客".equals(userId)) {
					Toast.makeText(ShopsArticleEasyActivity.this, "请登录后收藏",
							Toast.LENGTH_SHORT).show();
				} else {
					if (collect) {
						new CustomAlertDialog.Builder(
								ShopsArticleEasyActivity.this)
								.setTitle("提示")
								.setMessage("是否取消收藏？")
								.setIcon(R.drawable.dialog_icon)
								.setPositiveButton("是",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// Utils.showProcessDialog(
												// ShopsArticleEasyActivity.this,
												// "正在取消收藏...");
												String strnid = select_entity.nid;
												String listtype = select_entity.list_type;
												DBCollectUtil
														.cancle("listitemcollect",
																"list_type,nid",
																listtype
																		+ ","
																		+ strnid);
												collect = false;
												btn_shoucang
														.setBackgroundResource(R.drawable.shoucang_tb1);
												Utils.showToast("取消收藏成功");
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
						new CustomAlertDialog.Builder(
								ShopsArticleEasyActivity.this)
								.setTitle("提示")
								.setMessage("是否收藏？")
								.setIcon(R.drawable.dialog_icon)
								.setPositiveButton("是",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												DBCollectUtil
														.insert(select_entity);
												collect = true;
												btn_shoucang
														.setBackgroundResource(R.drawable.shoucang_tb2);
												Utils.showToast("收藏成功");
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
				intent.setClass(ShopsArticleEasyActivity.this,
						ShowImgActivity.class);
				intent.putExtra("item", select_entity);
				intent.putExtra("img_list", select_entity.icon.split(","));
				startActivity(intent);
			}
		});
	}

	public void initView() {
		btn_back = this.findViewById(R.id.title_back);// 返回按钮
		TextView title = (TextView) this.findViewById(R.id.title_title);
		title.setVisibility(View.GONE);
		btn_pinglun = this.findViewById(R.id.title_btn_pl);
		btn_pinglun.setBackgroundResource(R.drawable.pltb_selector);
		btn_pinglun.setVisibility(View.VISIBLE);
		btn_shoucang = (TextView) this.findViewById(R.id.title_btn_right);
		btn_shoucang.setVisibility(View.VISIBLE);
		btn_shoucang.setBackgroundResource(R.drawable.shoucang_selector);
		txt_title = (TextView) this.findViewById(R.id.title_zbcy_tv);// 美食标题
		txt_sjjs = (TextView) this.findViewById(R.id.jsnr_tv2);// 商家介绍
		txt_cpjs = (TextView) this.findViewById(R.id.jsnr_tv);// 产品介绍
		txt_addr = (TextView) this.findViewById(R.id.dw_tv);// 美食地址

		img = (ImageView) this.findViewById(R.id.img_zbcy_iv);
		text_user1 = (TextView) this.findViewById(R.id.plnr_1);
		text_user1nr = (TextView) this.findViewById(R.id.plnr_1_tv);
		text_user2 = (TextView) this.findViewById(R.id.plnr_2);
		text_user2nr = (TextView) this.findViewById(R.id.plnr_2_tv);
		text_user3 = (TextView) this.findViewById(R.id.plnr_3);
		text_user3nr = (TextView) this.findViewById(R.id.plnr_3_tv);
		date_1 = (TextView) this.findViewById(R.id.date_text1);
		date_2 = (TextView) this.findViewById(R.id.date_text2);
		date_3 = (TextView) this.findViewById(R.id.date_text3);
		pl_count = (TextView) this.findViewById(R.id.plcount_tv);

		dingwei_rl = (RelativeLayout) this.findViewById(R.id.dingwei_rl);// 查看地图
		xianlu_rl = (RelativeLayout) this.findViewById(R.id.xianlu_rl);

		xx_v1 = (View) this.findViewById(R.id.xuxian_ve6);
		xx_v2 = (View) this.findViewById(R.id.xuxian_ve7);

		zwpl_rl = (RelativeLayout) this.findViewById(R.id.zwpl_rl);
		plnr_rl = (RelativeLayout) this.findViewById(R.id.plnr_rl);
		pl_btn = (Button) this.findViewById(R.id.ckqbpl_tv);
		phone = (TextView) this.findViewById(R.id.phone_text);

		phone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
						+ select_entity.phone)); // 从后台接受手机号码
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});

		// 点击定位,查看商家位置
		dingwei_rl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intents = new Intent();
				intents.setClass(ShopsArticleEasyActivity.this,
						BaiduLocationAddrActivity.class);
				intents.putExtra("item", select_entity);
				startActivity(intents);
			}
		});

		// 路线查询
		xianlu_rl.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog();
			}
		});

		if (select_entity != null) {

			phone.setText(select_entity.phone);
			txt_title.setText(select_entity.title);
			txt_sjjs.setText(select_entity.des);
			txt_cpjs.setText(select_entity.shangjia);
			txt_addr.setText(select_entity.address);

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
		}
	}

	private String[] items = new String[] { "公交线路", "驾车线路", "步行线路" };
	String travelMode = null, cityName, startPlace, endPlace, statrLongitude,
			startLatitude, endLongitude, endLatitude, address;

	/**
	 * 路线查询的dialog
	 */
	private void showDialog() {

		new CustomAlertDialog.Builder(this)
				.setTitle("请选择路线")
				.setIcon(R.drawable.dialog_icon)
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
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
								Toast.makeText(ShopsArticleEasyActivity.this,
										"对不起由于无法获取相关定位信息，无法使用该功能",
										Toast.LENGTH_LONG).show();
							} else {
								toMapActivity(cityName, travelMode,
										statrLongitude, startLatitude,
										endLongitude, endLatitude, address,
										ShopsArticleEasyActivity.this);
							}
							break;
						case 1:
							// 驾车线路
							travelMode = "drive";

							if (statrLongitude.equals("null")
									|| statrLongitude.trim().length() <= 0
									|| startLatitude.equals("null")
									|| startLatitude.trim().length() <= 0
									|| endLongitude.equals("null")
									|| endLongitude.trim().length() <= 0
									|| endLatitude.equals("null")
									|| endLatitude.trim().length() <= 0) {
								Toast.makeText(ShopsArticleEasyActivity.this,
										"对不起由于无法获取相关定位信息，无法使用该功能",
										Toast.LENGTH_LONG).show();
							} else {
								toMapActivity(cityName, travelMode,
										statrLongitude, startLatitude,
										endLongitude, endLatitude, address,
										ShopsArticleEasyActivity.this);
							}
							break;
						case 2:
							// 步行线路
							travelMode = "walk";
							if (statrLongitude.equals("null")
									|| statrLongitude.trim().length() <= 0
									|| startLatitude.equals("null")
									|| startLatitude.trim().length() <= 0
									|| endLongitude.equals("null")
									|| endLongitude.trim().length() <= 0
									|| endLatitude.equals("null")
									|| endLatitude.trim().length() <= 0) {
								Toast.makeText(ShopsArticleEasyActivity.this,
										"对不起由于无法获取相关定位信息，无法使用该功能",
										Toast.LENGTH_LONG).show();
							} else {
								toMapActivity(cityName, travelMode,
										statrLongitude, startLatitude,
										endLongitude, endLatitude, address,
										ShopsArticleEasyActivity.this);
							}
							break;
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
	}

	/**
	 * 路线选择 方法
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
			Intent intent = new Intent();
			System.out.println("----------:" + travelMode);
			if (travelMode.equals("walk") || travelMode.equals("drive")) {
				intent.setClass(ShopsArticleEasyActivity.this,
						BaiduLocationDriveAndWalk.class);
			} else {
				intent.setClass(ShopsArticleEasyActivity.this,
						BaiduLocationOverlayActivity.class);
			}
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

	/**
	 * 显示评论
	 */
	public void showReviews() {
		JSONArray array = null;
		try {
			array = new JSONArray(str_reviews);
		} catch (Exception e) {
			System.out.println(e);
		}
		int len = 0;
		if (array != null) {
			len = array.length();
		}
		pl_count.setText("用户评论" + len + "条");
		if (len >= 3) {
			plnr_rl.setVisibility(View.VISIBLE);
			zwpl_rl.setVisibility(View.GONE);
			pl_btn.setVisibility(View.VISIBLE);
			pl_btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent i = new Intent();
					i.putExtra("allInfo", str_reviews);
					i.setClass(ShopsArticleEasyActivity.this,
							ReviewAllInfoActivity.class);
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
				text_user1.setText(m1.getString("nick") + " :");
				text_user2.setText(m2.getString("nick") + " :");
				text_user3.setText(m3.getString("nick") + " :");
				text_user1nr.setText(m1.getString("content"));
				text_user2nr.setText(m2.getString("content"));
				text_user3nr.setText(m3.getString("content"));
				date_1.setText(m1.getString("addTime"));
				date_2.setText(m2.getString("addTime"));
				date_3.setText(m3.getString("addTime"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (len == 0) {
			plnr_rl.setVisibility(View.GONE);
			zwpl_rl.setVisibility(View.VISIBLE);
		}
		if (len == 1) {
			JSONObject m1;
			try {
				m1 = array.getJSONObject(0);
				plnr_rl.setVisibility(View.VISIBLE);
				zwpl_rl.setVisibility(View.GONE);
				text_user1.setText(m1.getString("nick") + " :");
				text_user2.setVisibility(View.GONE);
				text_user3.setVisibility(View.GONE);
				text_user1nr.setText(m1.getString("content"));
				text_user2nr.setVisibility(View.GONE);
				text_user3nr.setVisibility(View.GONE);
				date_1.setText(m1.getString("addTime"));
				date_2.setVisibility(View.GONE);
				date_3.setVisibility(View.GONE);
				xx_v1.setVisibility(View.GONE);
				xx_v2.setVisibility(View.GONE);
				pl_btn.setVisibility(View.GONE);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
		if (len == 2) {
			JSONObject m1;
			JSONObject m2;
			try {
				m1 = array.getJSONObject(0);
				m2 = array.getJSONObject(1);
				plnr_rl.setVisibility(View.VISIBLE);
				zwpl_rl.setVisibility(View.GONE);
				text_user1.setText(m1.getString("nick") + " :");
				text_user2.setText(m2.getString("nick") + " :");
				text_user3.setVisibility(View.GONE);
				text_user1nr.setText(m1.getString("content"));
				text_user2nr.setText(m2.getString("content"));
				text_user3nr.setVisibility(View.GONE);
				date_1.setText(m1.getString("addTime"));
				date_2.setText(m2.getString("addTime"));
				date_3.setVisibility(View.GONE);
				xx_v2.setVisibility(View.GONE);
				pl_btn.setVisibility(View.GONE);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * 获取评论信息
	 * 
	 * @author wll
	 */
	class SelectReviews2 extends AsyncTask<Void, Void, HashMap<String, Object>> {

		/**
		 * 获取评论信息
		 */
		public SelectReviews2() {
		}

		@Override
		protected HashMap<String, Object> doInBackground(Void... params) {
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			String json;

			String js = ShareApplication.share.getResources().getString(
					R.string.url_sel_review);
			// 加入集合
			HashMap<String, Object> mhashmap = new HashMap<String, Object>();
			try {
				json = DNDataSource.list_FromNET(js + "comment.menuId="
						+ select_entity.list_type + "&comment.pid="
						+ select_entity.nid, param);
				// PerfHelper.getStringData(PerfHelper.P_USERID)
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
			str_reviews = jsonobj.getString("lists");
			System.out.println("请求返回：" + json);
			return data;
		}

		@Override
		protected void onPostExecute(HashMap<String, Object> result) {
			super.onPostExecute(result);
			Utils.dismissProcessDialog();
			// 无网提示；
			if (!Utils.isNetworkAvailable(ShopsArticleEasyActivity.this)) {
				Utils.showToast(R.string.network_error);
				return;
			}
			// if (result == null) {
			// Toast.makeText(ShopsArticleEasyActivity.this, "评论查询失败",
			// Toast.LENGTH_SHORT).show();
			// } else if ("0".equals(result.get("responseCode"))) {
			showReviews();
			// } else {
			// // Toast.makeText(ShopsArticleEasyActivity.this, "评论查询失败",
			// Toast.LENGTH_SHORT).show();
			// }
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		showReviews();
	}

}
