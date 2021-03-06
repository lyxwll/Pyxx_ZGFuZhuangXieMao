package com.pyxx.part_asynctask;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.res.Resources.NotFoundException;

import com.pyxx.app.ShareApplication;
import com.pyxx.dao.DBHelper;
import com.pyxx.datasource.DNDataSource;
import com.pyxx.entity.Data;
import com.pyxx.entity.part;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.PerfHelper;

/**
 * 获取二级标题
 * 
 * @author Administrator
 * 
 */
public class GetAllAreaThread extends Thread {

	public GetAllAreaThread() {
	}

	@Override
	public void run() {
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("region.level", "3"));
		String json;
		try {
			json = DNDataSource.list_FromNET(ShareApplication.share
					.getResources().getString(R.string.url_sel_city), param);
			if (ShareApplication.debug) {
				System.out.println("所有区县接口返回:" + json);

			}

			parseJson(json);
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.run();
	}

	public Data parseJson(String json) throws Exception {
		Data data = new Data();

		JSONObject jsonobj = new JSONObject(json);
		try {
			if (jsonobj.has("code")) {
				int code = jsonobj.getInt("code");
				if (code != 1) {
					data.obj1 = jsonobj.getString("code");// 返回状态

					// 0，有数据成功返回，-1，-2，无数据返回，1，返回异常

				} else {
					data.obj1 = jsonobj.getString("code");// 返回状态

				}
			}
		} catch (Exception e) {
		}
		if (data != null) {
			initparts_nearby(json, "all_area");
		}

		return data;
	}

	public void initparts_nearby(String json, String parttype) throws Exception {

		DBHelper db = DBHelper.getDBHelper();
		JSONObject jsonobj = new JSONObject(json);
		if (jsonobj.has("code")) {
			int code = jsonobj.getInt("code");
			if (code != 1) {
				return;
				// mhandler.sendEmptyMessage(FinalVariable.error);
			}
		}
		List<part> design_patrs = new ArrayList<part>();

		JSONArray jsonay = jsonobj.getJSONArray("lists");
		int count = jsonay.length();
		for (int i = 0; i < count; i++) {
			part o = new part();
			JSONObject obj = jsonay.getJSONObject(i);
			o.part_type = parttype;
			o.part_index = i + 1;
			o.part_sa = obj.getString("id");
			o.part_choise = obj.getString("pid");
			try {
				if (obj.has("name")) {
					o.part_name = obj.getString("name");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			design_patrs.add(o);
		}

		db.delete("part_list", "part_type=?", new String[] { parttype
				+ PerfHelper.getStringData(PerfHelper.P_CITY_No) });
		db.insert(design_patrs, "part_list", part.class);
	}

}