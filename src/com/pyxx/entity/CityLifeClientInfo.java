package com.pyxx.entity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.pyxx.app.ShareApplication;
import com.pyxx.dao.ClientInfo;
import com.pyxx.dao.DBHelper;
import com.pyxx.dao.JsonDao;
import com.pyxx.datasource.DNDataSource;
import com.pyxx.loadimage.Utils;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.FinalVariable;
import com.utils.JniUtils;
import com.utils.PerfHelper;

public class CityLifeClientInfo extends ClientInfo {

	/**
	 * 栏目初始化操作
	 */
	public static int mPage = 1;
	public static int mLength = 20;
	public static int mFooter_limit = mLength;

	public static String mOldtype = "part_list";
	public static String mParttype = "part_list";

	public static String getDataFromNet(String url, String oldtype, int page,
			int count, boolean isfirst, String parttype) throws Exception {
		String citylistjson = DNDataSource.CityLift_list_FromNET(url, oldtype,
				page, count, parttype, isfirst);
		if (ShareApplication.debug)
			System.out.println("城市列表返回:" + citylistjson);
		return citylistjson;
	}

	/**
	 * 获取二级栏目跟新
	 * 
	 * @param json
	 * @param mhandler
	 * @throws Exception
	 */
	public static void initparts_bianming(String json, String parttype)
			throws Exception {

		DBHelper db = DBHelper.getDBHelper();
		JSONObject jsonobj = new JSONObject(json);
		if (jsonobj.has("responseCode")) {
			int code = jsonobj.getInt("responseCode");
			if (code != 0) {
				return;
				// mhandler.sendEmptyMessage(FinalVariable.error);
			}
		}
		List<part> design_patrs = new ArrayList<part>();
		JSONArray jsonay = jsonobj.getJSONArray("results");
		int count = jsonay.length();
		for (int i = 0; i < count; i++) {
			part o = new part();
			JSONObject obj = jsonay.getJSONObject(i);
			o.part_type = parttype
					+ PerfHelper.getStringData(PerfHelper.P_CITY_No);
			o.part_index = i;
			o.part_sa = obj.getString("id");
			try {
				if (obj.has("name")) {
					o.part_name = obj.getString("name");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			design_patrs.add(o);
		}
		db.insert(design_patrs, "part_list", part.class);
	}

	public static void initparts_nearby(String json, String parttype)
			throws Exception {

		DBHelper db = DBHelper.getDBHelper();
		JSONObject jsonobj = new JSONObject(json);
		if (jsonobj.has("responseCode")) {
			int code = jsonobj.getInt("responseCode");
			if (code != 0) {
				return;
				// mhandler.sendEmptyMessage(FinalVariable.error);
			}
		}
		List<part> design_patrs = new ArrayList<part>();
		String time = PerfHelper.getStringData("nearbytime")
				+ PerfHelper.getStringData(PerfHelper.P_CITY_No);
		if (jsonobj.has("areaName")) {
			if (!time.equals(jsonobj.getString("lastTime"))) {
				PerfHelper.setInfo(
						"nearbytime"
								+ PerfHelper
										.getStringData(PerfHelper.P_CITY_No),
						jsonobj.getString("areaName"));
			} else {
				return;
			}

		}
		JSONArray jsonay = jsonobj.getJSONArray("area");
		int count = jsonay.length();
		part o1 = new part();
		o1.part_type = parttype
				+ PerfHelper.getStringData(PerfHelper.P_CITY_No);
		o1.part_index = 0;
		o1.part_sa = "1";
		o1.part_name = "全部";
		design_patrs.add(o1);
		for (int i = 0; i < count; i++) {
			part o = new part();
			JSONObject obj = jsonay.getJSONObject(i);
			o.part_type = parttype
					+ PerfHelper.getStringData(PerfHelper.P_CITY_No);
			o.part_index = i + 1;
			o.part_sa = obj.getString("id");
			try {
				if (obj.has("areaName")) {
					o.part_name = obj.getString("areaName");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			design_patrs.add(o);
		}

		db.insert(design_patrs, "part_list", part.class);
	}

	/**
	 * 版本更新检测
	 */
	public static void citylife_check_update(final Context context) {
		new Thread() {
			@Override
			public void run() {
				try {
					final Version v = citylife_check_update(FinalVariable.pid);
					if (v != null) {
						if (!"1".equals(v.available)) {
							return;
						}
						Utils.h.post(new Runnable() {
							@Override
							public void run() {
								if ("0".equals(v.force)) {
									new AlertDialog.Builder(context)
											.setMessage(v.alert)
											.setNegativeButton(
													R.string.cancel,
													new DialogInterface.OnClickListener() {
														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
														}
													})
											.setPositiveButton(
													R.string.checkupdate_getnew,
													new DialogInterface.OnClickListener() {
														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															if (!v.update_url
																	.startsWith("http://")) {
																Utils.showToast("参数错误");
																return;
															}
															Uri uri = Uri
																	.parse(v.update_url);
															Intent it = new Intent(
																	Intent.ACTION_VIEW,
																	uri);
															try {
																context.startActivity(it);
															} catch (Exception e) {
																// TODO
																// Auto-generated
																// catch block
																Utils.showToast("请安装浏览起");
																e.printStackTrace();
															}
														}
													}).show();
								} else {
									new AlertDialog.Builder(context)
											.setMessage(v.alert)
											.setNegativeButton(
													R.string.checkupdate_getnew,
													new DialogInterface.OnClickListener() {
														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															if (!v.update_url
																	.startsWith("http://")) {
																Utils.showToast("参数错误");
																return;
															}
															Uri uri = Uri
																	.parse(v.update_url);
															Intent it = new Intent(
																	Intent.ACTION_VIEW,
																	uri);
															try {
																context.startActivity(it);
															} catch (Exception e) {
																// TODO
																// Auto-generated
																// catch block
																Utils.showToast("请安装浏览起");
																e.printStackTrace();
															}
															// Urls.close(AbsMainActivity.this);
														}
													}).show();
								}
							}
						});
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}.start();
	}

	public static Version citylife_check_update(String pid) throws Exception {
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		String s = ShareApplication.getVersion();
		param.add(new BasicNameValuePair("versionNum", ShareApplication
				.getVersion()));
		param.add(new BasicNameValuePair("appType", "2"));// 设备类型 1ios,2android
		param.add(new BasicNameValuePair("mobile", Build.MODEL));
		param.add(new BasicNameValuePair("e", JniUtils.getkey()));
		Version v = (Version) JsonDao.getJsonObject(
				ShareApplication.share.getResources().getString(
						R.string.citylife_checkVersion_list_url), param,
				Version.class);
		return v;
	}

}
