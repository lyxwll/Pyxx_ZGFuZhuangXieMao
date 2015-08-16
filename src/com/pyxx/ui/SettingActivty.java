package com.pyxx.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pyxx.app.ShareApplication;
import com.pyxx.dao.DBHelper;
import com.pyxx.datasource.DNDataSource;
import com.pyxx.entity.Data;
import com.pyxx.loadimage.Utils;
import com.pyxx.part_activiy.AddAddrActivity;
import com.pyxx.part_activiy.OrderActivity;
import com.pyxx.part_activiy.ShopCarActivity;
import com.pyxx.setting.FeedBack;
import com.pyxx.setting.Version;
import com.pyxx.view.CustomAlertDialog;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.FileUtils;
import com.utils.FinalVariable;
import com.utils.PerfHelper;

@SuppressLint("ValidFragment")
/**
 * 更多界面
 * @author wll
 */
public class SettingActivty extends Fragment implements OnClickListener {

	public View mMain_layout, mParents;// 布局显示
	public RelativeLayout mContainers;
	public Context mContext;
	public Button zx_btn;
	private String userName;
	private TextView userName_tv, count_text;
	private ImageView user_pic;// 点击头像登录
	int sre;

	public SettingActivty() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null)
			sre = savedInstanceState.getInt("icon_url");
		mContext = inflater.getContext();

		if (mMain_layout == null) {
			mContainers = new RelativeLayout(mContext);

			mMain_layout = inflater.inflate(R.layout.setting, null);

			initListFragment(inflater);
			mContainers.addView(mMain_layout);
		} else {
			mContainers.removeAllViews();
			mContainers = new RelativeLayout(getActivity());
			mContainers.addView(mMain_layout);
		}
		return mContainers;
	}

	public void initListFragment(LayoutInflater inflater) {
		init();
		if (PerfHelper.getBooleanData(PerfHelper.P_USER_LOGIN)) {
			new selectWaitOrdeCount().execute((Void) null);
		} else {
			count_text.setText("0");
		}
	}

	TextView push_state;
	TextView text;

	public void init() {
		size = 0;
		mMain_layout.findViewById(R.id.user_pic).setOnClickListener(this);
		mMain_layout.findViewById(R.id.setting_wait_order).setOnClickListener(
				this);
		mMain_layout.findViewById(R.id.setting_my_order).setOnClickListener(
				this);
		mMain_layout.findViewById(R.id.setting_collect)
				.setOnClickListener(this);
		mMain_layout.findViewById(R.id.setting_addr).setOnClickListener(this);
		mMain_layout.findViewById(R.id.setting_feedback).setOnClickListener(
				this);
		mMain_layout.findViewById(R.id.setting_kefu).setOnClickListener(this);
		mMain_layout.findViewById(R.id.setting_gwc).setOnClickListener(this);

		zx_btn = (Button) mMain_layout.findViewById(R.id.cancle_btn);// 注销
		zx_btn.setOnClickListener(this);

		mMain_layout.findViewById(R.id.setting_about).setOnClickListener(this);
		mMain_layout.findViewById(R.id.setting_clear).setOnClickListener(this);
		userName_tv = ((TextView) mMain_layout.findViewById(R.id.userName_text));
		user_pic = ((ImageView) mMain_layout.findViewById(R.id.user_pic));
		text = (TextView) mMain_layout.findViewById(R.id.st_clear_size);
		count_text = (TextView) mMain_layout.findViewById(R.id.order_count);// 订单数

		getFilesInfo(FileUtils.sdPath);

		if (!PerfHelper.getBooleanData(PerfHelper.P_USER_LOGIN)) {
			zx_btn.setVisibility(View.GONE);
			userName_tv.setText("点击登录");
		} else {
			zx_btn.setVisibility(View.VISIBLE);
			userName_tv.setText(PerfHelper
					.getStringData(PerfHelper.P_SHARE_NAME));
		}
	}

	Intent intent = null;

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.setting_gwc:
			if (!PerfHelper.getBooleanData(PerfHelper.P_USER_LOGIN)) {
				Utils.showToast("请先登录查看购物车");
			} else {
				if (PerfHelper.getStringData(PerfHelper.P_SHARE_SEX) == null
						|| "".equals(PerfHelper
								.getStringData(PerfHelper.P_SHARE_SEX))
						|| PerfHelper.getStringData(PerfHelper.P_SHARE_SEX) == "0") {
					Utils.showToast("您还没有添加购物信息");
				} else {
					Intent intent1 = new Intent();
					intent1.setClass(mContext, ShopCarActivity.class);
					startActivity(intent1);
				}
			}
			break;
		case R.id.cancle_btn:// 注销
			userName_tv.setText("点击登录");
			user_pic.setImageResource(R.drawable.default_head_bg);
			PerfHelper.setInfo(PerfHelper.P_USERID, "");
			PerfHelper.setInfo(PerfHelper.P_SHARE_NAME, "");
			PerfHelper.setInfo(PerfHelper.P_SHARE_EMAIL, "");
			PerfHelper.setInfo(PerfHelper.P_USER_LOGIN, false);
			Utils.showToast("注销成功");
			zx_btn.setVisibility(View.GONE);
			count_text.setText("0");
			break;
		// case R.id.setting_kefu:
		// intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
		// + "4000-666-058")); // 从后台接受手机号码
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// startActivity(intent);
		// break;
		case R.id.user_pic:// 登录
			if (!PerfHelper.getBooleanData(PerfHelper.P_USER_LOGIN)) {
				intent = new Intent();
				intent.setClass(mContext, SettingUserLoginActivity.class);
				startActivity(intent);
			} else {
			}
			break;
		case R.id.setting_wait_order:
			if (PerfHelper.getBooleanData(PerfHelper.P_USER_LOGIN)) {
				intent = new Intent();
				intent.setClass(mContext, OrderActivity.class);
				intent.putExtra("type", "1");
				startActivity(intent);
			} else {
				Utils.showToast("请先登录");
			}
			break;
		case R.id.setting_my_order:
			if (PerfHelper.getBooleanData(PerfHelper.P_USER_LOGIN)) {
				intent = new Intent();
				intent.setClass(mContext, OrderActivity.class);
				intent.putExtra("type", "2");
				startActivity(intent);
			} else {
				Utils.showToast("请先登录");
			}
			break;
		case R.id.setting_collect:
			if (PerfHelper.getBooleanData(PerfHelper.P_USER_LOGIN)) {
				intent = new Intent();
				intent.setClass(mContext, CollectActivity.class);
				startActivity(intent);
			} else {
				Utils.showToast("请先登录");
			}
			break;
		case R.id.setting_addr:// 收货地址管理
			if (PerfHelper.getBooleanData(PerfHelper.P_USER_LOGIN)) {
				intent = new Intent();
				intent.setClass(mContext, AddAddrActivity.class);
				startActivity(intent);
			} else {
				Utils.showToast("请先登录");
			}
			break;
		case R.id.setting_feedback:
			intent = new Intent();
			intent.setClass(mContext, FeedBack.class);
			startActivity(intent);
			break;
		case R.id.setting_about:
			intent = new Intent();
			intent.setClass(mContext, Version.class);
			startActivity(intent);
			break;
		case R.id.setting_clear:
			new CustomAlertDialog.Builder(mContext)
					.setTitle("清理提示")
					.setIcon(R.drawable.main_ic)
					.setMessage("是否清除缓存？")
					.setPositiveButton("是",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Utils.showProcessDialog(mContext,
											"正在清除缓存...");
									new Thread() {
										public void run() {

											ShareApplication.mImageWorker.mImageCache
													.clearCaches();
											deleteFilesInfo(FileUtils.sdPath);
											DBHelper db = DBHelper
													.getDBHelper();
											String[] upgrade = ShareApplication.share
													.getResources()
													.getStringArray(
															R.array.app_sql_delete);
											db.deleteall(upgrade);
											Utils.dismissProcessDialog();
											Utils.showToast("缓存清理完毕");
											Utils.h.post(new Runnable() {
												@Override
												public void run() {
													text.setText("0kb");
												}
											});
										};
									}.start();
								}
							})
					.setNegativeButton("否",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
								}
							}).show();
			break;
		case R.id.st_push_state:
			if (PerfHelper.getBooleanData(PerfHelper.P_PUSH)) {
				push_state.setBackgroundResource(R.drawable.bind_tuijian_close);
				PerfHelper.setInfo(PerfHelper.P_PUSH, false);
			} else {
				push_state.setBackgroundResource(R.drawable.bind_tuijian_open);
				PerfHelper.setInfo(PerfHelper.P_PUSH, true);
			}
			break;
		case R.id.setting_share:
			// intent = new Intent();
			// intent.setAction(getResources().getString(
			// R.string.activity_st_share_manager));
			// startActivity(intent);
			break;
		default:// 分享界面事件
			// if (PerfHelper.getBooleanData(PerfHelper.P_SHARE_STATE
			// + view.getTag())) {
			// WeiboShareDao.bind_unbinded(view.getTag().toString(), handler);
			// } else {
			// String pushbind =
			// "http://push.cms.palmtrends.com/wb/bind_v2.php?pid="
			// + FinalVariable.pid
			// + "&cid=3"
			// + "&uid="
			// + PerfHelper.getStringData(PerfHelper.P_USERID);
			// Intent intent = new Intent();
			// intent.putExtra("m_mainurl", pushbind + "&sname="
			// + view.getTag().toString());
			// intent.putExtra("sname", view.getTag().toString());
			// intent.setAction(getResources().getString(
			// R.string.activity_share_bind));
			// startActivity(intent);
			// }
			break;
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case FinalVariable.vb_success:
				// View btn = manager_bangding_title.findViewWithTag(msg.obj
				// .toString());
				// btn.setBackgroundResource(R.drawable.bind_bind);
				// initShare();
				Utils.showToast("解除绑定成功");
				break;
			case FinalVariable.vb_error:
				Utils.showToast("解除绑定失败");
				break;
			}
		};
	};

	public void deleteFilesInfo(String str) {
		File f = new File(str);
		if (f.listFiles() != null) {
			for (File file : f.listFiles()) {
				if (file.isDirectory()) {
					deleteFilesInfo(file.getAbsolutePath());
				} else {
					file.delete();
				}
			}
		}
	}

	long size = 0;

	private void getFilesInfo(String str) {

		File f = new File(str);
		if (f.listFiles() != null) {
			for (File file : f.listFiles()) {
				if (file.isDirectory()) {
					getFilesInfo(file.getAbsolutePath());
				} else {
					size = file.length();
				}
			}
			text.setText(FileUtils.formatFileSize(size));
		}
	}

	// 分享设置
	LinearLayout manager_bangding_title;

	@Override
	public void onResume() {
		super.onResume();
		userName = PerfHelper.getStringData(PerfHelper.P_SHARE_NAME);
		if (userName.equals("") || userName.equals("p_share_name_setting")) {
			userName_tv.setText("点击登录");
			user_pic.setImageResource(R.drawable.default_head_bg);
			zx_btn.setVisibility(View.GONE);
		} else {
			userName_tv.setText(PerfHelper
					.getStringData(PerfHelper.P_SHARE_NAME));
			zx_btn.setVisibility(View.VISIBLE);
			if (PerfHelper.getBooleanData(PerfHelper.P_USER_LOGIN)) {
				new selectWaitOrdeCount().execute((Void) null);
			}
			// userName_tv.setOnClickListener(new View.OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// userName_tv.setText("点击登录");
			// user_pic.setImageResource(R.drawable.default_head_bg);
			// PerfHelper.setInfo(PerfHelper.P_USERID,"");
			// PerfHelper.setInfo(PerfHelper.P_SHARE_NAME, "");
			// PerfHelper.setInfo(PerfHelper.P_USER_LOGIN, false);
			// Utils.showToast("注销成功");
			// }
			// });

		}
	}

	/**
	 * 待付款订单
	 * 
	 * @author wll
	 */
	class selectWaitOrdeCount extends
			AsyncTask<Void, Void, HashMap<String, Object>> {

		/**
		 * 待付款订单
		 */
		public selectWaitOrdeCount() {
		}

		@Override
		protected HashMap<String, Object> doInBackground(Void... params) {

			List<NameValuePair> param = new ArrayList<NameValuePair>();
			String json;

			String js = ShareApplication.share.getResources().getString(
					R.string.url_sel_order);
			// 加入集合
			HashMap<String, Object> mhashmap = new HashMap<String, Object>();
			try {
				json = DNDataSource.list_FromNET(js + "userOrder.userId="
						+ PerfHelper.getStringData(PerfHelper.P_USERID)
						+ "&userOrder.status=1", param);
				Data date = parseJson(json);
				mhashmap.put("responseCode", date.obj1);
				mhashmap.put("size", date.obj);
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
			try {
				JSONArray array = jsonobj.getJSONArray("lists");
				data.obj = array.length();
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
			if (!Utils.isNetworkAvailable(mContext)) {
				Utils.showToast(R.string.network_error);
				return;
			}
			if (result == null) {
				Toast.makeText(mContext, "操作失败", Toast.LENGTH_SHORT).show();
			} else if ("1".equals(result.get("responseCode"))) {
				count_text.setText(result.get("size") + "");
			} else {
				Toast.makeText(mContext, "获取代付款订单数量失败", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	/**
	 * 检测更新
	 * 
	 * @author wll
	 */
	class sellectUpdate extends AsyncTask<Void, Void, HashMap<String, Object>> {

		public sellectUpdate() {
		}

		@Override
		protected HashMap<String, Object> doInBackground(Void... params) {

			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("appType", "1"));
			param.add(new BasicNameValuePair("versionNum", ShareApplication
					.getVersion()));
			String json;
			// 加入集合
			HashMap<String, Object> mhashmap = new HashMap<String, Object>();
			try {
				json = DNDataSource.list_FromNET(
						getResources().getString(R.string.activity_all_finish),
						param);
				if (ShareApplication.debug) {
				}
				Data date = parseJson(json);
				mhashmap.put("responseCode", date.obj1);
			} catch (NotFoundException e) {
				e.printStackTrace();
				mhashmap = null;
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
					data.obj = jsonobj.getString("url");
				}
			}
			return data;
		}

		@Override
		protected void onPostExecute(HashMap<String, Object> result) {
			super.onPostExecute(result);
			Utils.dismissProcessDialog();
			// 无网提示；
			if (!Utils.isNetworkAvailable(mContext)) {
				Utils.showToast(R.string.network_error);
				return;
			}
			if (result == null) {
				Utils.showToast("提交失败，请稍后再试");
			} else if ("0".equals(result.get("responseCode"))) {
				Utils.showToast("有更新版本信息");
			} else if ("1".equals(result.get("responseCode"))) {
				Utils.showToast("您的客户端已是最新版本");
			} else {
				Utils.showToast("检测失败，请稍后再试");
			}
		}
	}
}
