package com.pyxx.part_activiy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pyxx.app.ShareApplication;
import com.pyxx.dao.DBHelper;
import com.pyxx.datasource.DNDataSource;
import com.pyxx.entity.BaseFragmentActivity;
import com.pyxx.entity.CommonUtil;
import com.pyxx.entity.Data;
import com.pyxx.entity.Listitem;
import com.pyxx.loadimage.Utils;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.PerfHelper;

/**
 * 添加新地址或编辑收货地址
 * 
 * @author wll
 */
public class WriteAddrActivity extends BaseFragmentActivity implements
		OnClickListener {
	private TextView submit_tv, addr_area;
	private EditText addr_name, addr_email, addr_phone, addr_youbian,
			addr_addr;
	private RelativeLayout area_rl;
	FragmentTransaction fragmentTransaction;
	Fragment m_list_frag_1 = null;
	private String str_name = "", str_province = "", str_city = "",
			str_area = "", str_addr = "", str_email = "", str_phone = "",
			str_youbian = "", addrId = "";
	private boolean flag = true;
	private Listitem item = null;

	private PopupWindow pop;
	private View vi;
	private LayoutInflater inflater;
	private ListView province_lv, city_lv, area_lv;
	private int selected1 = -1, selected2 = -1, selected3 = -1;
	private ListAdapter provinceAdapter;
	private ListAdapter2 cityAdapter;
	private ListAdapter3 areaAdapter;

	private List<Listitem> list_province, list_area, list_city,
			list_city_now = new ArrayList<Listitem>(),
			list_area_now = new ArrayList<Listitem>();
	private String provinceId = "", cityId = "", areaId = "";

	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		if (getIntent() != null) {
			if (!"".equals(getIntent().getExtras().get("item"))) {
				item = (Listitem) getIntent().getExtras().get("item");
			}
		}

		setContentView(R.layout.writeaddr);

		getProvinces();
		getCitys();
		getAreas();

		inflater = LayoutInflater.from(this);
		vi = inflater.inflate(R.layout.list_quyu, null);
		pop = new PopupWindow(vi, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT, true);
		province_lv = (ListView) vi.findViewById(R.id.province_lv);
		city_lv = (ListView) vi.findViewById(R.id.city_lv);
		area_lv = (ListView) vi.findViewById(R.id.area_lv);
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setFocusable(true);
		pop.setTouchable(true);

		init();
	}

	public void init() {
		findViewById(R.id.title_back).setOnClickListener(this);
		submit_tv = (TextView) findViewById(R.id.title_right);
		submit_tv.setOnClickListener(this);

		area_rl = (RelativeLayout) findViewById(R.id.addr_area_rl);
		area_rl.setOnClickListener(this);

		addr_name = (EditText) findViewById(R.id.addr_name_et);
		addr_addr = (EditText) findViewById(R.id.addr_addr_et);
		addr_email = (EditText) findViewById(R.id.addr_email_et);
		addr_area = (TextView) findViewById(R.id.addr_area_text);
		addr_phone = (EditText) findViewById(R.id.addr_phone_et);
		addr_youbian = (EditText) findViewById(R.id.addr_youbian_et);

		if (item != null) {
			addr_name.setText(item.title);
			addr_addr.setText(item.address);
			addr_email.setText(item.other);
			for (int i = 0; i < list_province.size(); i++) {
				Listitem o = (Listitem) list_province.get(i);
				if (o.nid.equals(item.other2)) {
					provinceId = o.nid;
					str_province = o.title;
				}
			}
			for (int j = 0; j < list_city.size(); j++) {
				Listitem o = (Listitem) list_city.get(j);
				if (o.nid.equals(item.other3)) {
					cityId = o.nid;
					str_city = o.title;
				}
			}
			for (int k = 0; k < list_area.size(); k++) {
				Listitem o = (Listitem) list_area.get(k);
				if (o.nid.equals(item.fuwu)) {
					areaId = o.nid;
					str_area = o.title;
				}
			}
			addr_area.setText(str_province + "," + str_city + "," + str_area);
			addr_phone.setText(item.phone);
			addr_youbian.setText(item.other1);
			addrId = item.nid;
		}

	}

	public void getProvinces() {
		list_province = new ArrayList<Listitem>();
		Cursor cursor = DBHelper
				.getDBHelper()
				.getReadableDatabase()
				.rawQuery(
						"SELECT * FROM part_list where part_type='"
								+ "all_province" + "'", null);
		if (cursor == null) {

		} else {
			while (cursor.moveToNext()) {
				Listitem item = new Listitem();
				item.c_id = cursor.getInt(0);
				item.title = cursor.getString(1);
				item.nid = cursor.getString(2);
				item.des = cursor.getString(3);
				item.getMark();
				list_province.add(item);
			}
		}
		cursor.close();
	}

	public void getCitys() {
		list_city = new ArrayList<Listitem>();
		Cursor cursor = DBHelper
				.getDBHelper()
				.getReadableDatabase()
				.rawQuery(
						"SELECT * FROM part_list where part_type='"
								+ "all_city" + "'", null);
		if (cursor == null) {

		} else {
			while (cursor.moveToNext()) {
				Listitem item = new Listitem();
				item.c_id = cursor.getInt(0);
				item.title = cursor.getString(1);
				item.nid = cursor.getString(2);
				item.des = cursor.getString(3);
				item.getMark();
				list_city.add(item);
			}
		}
		cursor.close();
	}

	public void getAreas() {
		list_area = new ArrayList<Listitem>();
		Cursor cursor = DBHelper
				.getDBHelper()
				.getReadableDatabase()
				.rawQuery(
						"SELECT * FROM part_list where part_type='"
								+ "all_area" + "'", null);
		if (cursor == null) {

		} else {
			while (cursor.moveToNext()) {
				Listitem item = new Listitem();
				item.c_id = cursor.getInt(0);
				item.title = cursor.getString(1);
				item.nid = cursor.getString(2);
				item.des = cursor.getString(3);
				item.getMark();
				list_area.add(item);
			}
		}
		cursor.close();
	}

	public void things(View view) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_back:
			WriteAddrActivity.this.finish();
			break;
		case R.id.addr_area_rl:
			if (pop.isShowing()) {
				// 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏
				pop.dismiss();
			} else {
				// 显示窗口
				pop.showAsDropDown(v);
			}
			provinceAdapter = new ListAdapter(this, list_province);
			province_lv.setAdapter(provinceAdapter);
			break;

		case R.id.title_right:
			if (flag) {
				flag = false;
				str_name = addr_name.getText().toString();
				str_area = addr_area.getText().toString();
				str_addr = addr_addr.getText().toString();
				str_email = addr_email.getText().toString();
				str_phone = addr_phone.getText().toString();
				str_youbian = addr_youbian.getText().toString();
				if ("".equals(str_name) || "".equals(str_area)
						|| "".equals(str_addr) || "".equals(str_email)
						|| "".equals(str_phone) || "".equals(str_youbian)) {
					Utils.showToast("信息填写不完整");
					flag = true;
				} else if (!CommonUtil.emailFormat(str_email)) {
					Utils.showToast("输入的邮箱格式不正确");
					flag = true;
				} else if (!CommonUtil.isMobileNO(str_phone)) {
					Utils.showToast("输入的手机格式不正确");
					flag = true;
				} else {
					new SaveAddr().execute((Void) null);
				}
			} else {
				Utils.showToast("请不要重复提交");
			}
			break;
		}
	}

	private class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List list;

		public ListAdapter(Context context, List list) {

			this.inflater = LayoutInflater.from(context);
			this.list = list;

		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.list_type_item2, null);
				holder = new ViewHolder();
				holder.fenleiName = (TextView) convertView
						.findViewById(R.id.name_text);
				holder.selected_iv = (TextView) convertView
						.findViewById(R.id.selected_tv);
				holder.fenleiItem = (RelativeLayout) convertView
						.findViewById(R.id.name_rl);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.fenleiName.setTextColor(getResources().getColor(
					R.color.erji_text));
			holder.selected_iv.setVisibility(View.GONE);
			holder.fenleiItem.setBackgroundColor(getResources().getColor(
					R.color.shezhi_bg));
			if (selected1 == position) {
				holder.fenleiName.setTextColor(Color.BLACK);
				holder.selected_iv.setVisibility(View.VISIBLE);
				holder.fenleiItem.setBackgroundColor(getResources().getColor(
						R.color.shezhi_bg2));
			}
			Listitem li = list_province.get(position);
			holder.fenleiName.setText(li.title);
			holder.fenleiItem.setOnClickListener(new TextViewListener(position,
					holder.fenleiName));

			return convertView;
		}

		private class ViewHolder {
			TextView fenleiName;
			TextView selected_iv;
			RelativeLayout fenleiItem;
		}

		private class TextViewListener implements View.OnClickListener {
			int position;

			public TextViewListener(int position, TextView iv) {
				this.position = position;
			}

			public void onClick(View v) {
				Listitem li1 = list_province.get(position);
				selected1 = position;
				provinceAdapter = new ListAdapter(WriteAddrActivity.this,
						list_province);
				province_lv.setAdapter(provinceAdapter);
				provinceId = li1.nid;
				// System.out.println("######################################"+provinceId);
				str_province = li1.title;
				if (list_city_now.size() > 0) {
					list_city_now.clear();
				}
				for (int a = 0; a < list_city.size(); a++) {
					Listitem o = (Listitem) list_city.get(a);
					if (provinceId.equals(o.des)) {
						list_city_now.add(o);
					}
				}

				cityAdapter = new ListAdapter2(WriteAddrActivity.this,
						list_city_now);
				city_lv.setAdapter(cityAdapter);
			}
		}
	}

	private class ListAdapter2 extends BaseAdapter {
		private LayoutInflater inflater;
		private List list;

		public ListAdapter2(Context context, List list) {

			this.inflater = LayoutInflater.from(context);
			this.list = list;

		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.list_type_item2, null);
				holder = new ViewHolder();
				holder.fenleiName = (TextView) convertView
						.findViewById(R.id.name_text);
				holder.selected_iv = (TextView) convertView
						.findViewById(R.id.selected_tv);
				holder.fenleiItem = (RelativeLayout) convertView
						.findViewById(R.id.name_rl);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.fenleiName.setTextColor(getResources().getColor(
					R.color.erji_text));
			holder.selected_iv.setVisibility(View.GONE);
			holder.fenleiItem.setBackgroundColor(getResources().getColor(
					R.color.shezhi_bg));
			if (selected2 == position) {
				holder.fenleiName.setTextColor(Color.BLACK);
				holder.selected_iv.setVisibility(View.VISIBLE);
				holder.fenleiItem.setBackgroundColor(getResources().getColor(
						R.color.shezhi_bg2));
			}
			Listitem li = list_city_now.get(position);
			holder.fenleiName.setText(li.title);
			holder.fenleiItem.setOnClickListener(new TextViewListener(position,
					holder.fenleiName));

			return convertView;
		}

		private class ViewHolder {
			TextView fenleiName;
			TextView selected_iv;
			RelativeLayout fenleiItem;
		}

		private class TextViewListener implements View.OnClickListener {
			int position;

			public TextViewListener(int position, TextView iv) {
				this.position = position;
			}

			public void onClick(View v) {
				Listitem li2 = list_city_now.get(position);
				selected2 = position;
				cityAdapter = new ListAdapter2(WriteAddrActivity.this,
						list_city_now);
				city_lv.setAdapter(cityAdapter);
				cityId = li2.nid;
				str_city = li2.title;
				if (list_area_now.size() > 0) {
					list_area_now.clear();
				}
				for (int a = 0; a < list_area.size(); a++) {
					Listitem o = (Listitem) list_area.get(a);
					if (cityId.equals(o.des) || cityId == o.des) {
						list_area_now.add(o);
					}
				}
				areaAdapter = new ListAdapter3(WriteAddrActivity.this,
						list_area_now);
				area_lv.setAdapter(areaAdapter);
			}
		}
	}

	private class ListAdapter3 extends BaseAdapter {
		private LayoutInflater inflater;
		private List list;

		public ListAdapter3(Context context, List list) {

			this.inflater = LayoutInflater.from(context);
			this.list = list;

		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.list_type_item2, null);
				holder = new ViewHolder();
				holder.fenleiName = (TextView) convertView
						.findViewById(R.id.name_text);
				holder.selected_iv = (TextView) convertView
						.findViewById(R.id.selected_tv);
				holder.fenleiItem = (RelativeLayout) convertView
						.findViewById(R.id.name_rl);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.fenleiName.setTextColor(getResources().getColor(
					R.color.erji_text));
			holder.selected_iv.setVisibility(View.GONE);
			holder.fenleiItem.setBackgroundColor(getResources().getColor(
					R.color.shezhi_bg));
			if (selected3 == position) {
				holder.fenleiName.setTextColor(Color.BLACK);
				holder.selected_iv.setVisibility(View.VISIBLE);
				holder.fenleiItem.setBackgroundColor(getResources().getColor(
						R.color.shezhi_bg2));
			}
			Listitem li = list_area_now.get(position);
			holder.fenleiName.setText(li.title);
			holder.fenleiItem.setOnClickListener(new TextViewListener(position,
					holder.fenleiName));

			return convertView;
		}

		private class ViewHolder {
			TextView fenleiName;
			TextView selected_iv;
			RelativeLayout fenleiItem;
		}

		private class TextViewListener implements View.OnClickListener {
			int position;

			public TextViewListener(int position, TextView iv) {
				this.position = position;
			}

			public void onClick(View v) {

				Listitem li3 = list_area_now.get(position);
				selected3 = position;
				areaId = li3.nid;
				str_area = li3.title;
				addr_area.setText(str_province + "," + str_city + ","
						+ str_area);

				if (pop.isShowing()) {
					// 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏
					pop.dismiss();
				} else {
					// 显示窗口
					pop.showAsDropDown(v);
				}

			}
		}
	}

	class SaveAddr extends AsyncTask<Void, Void, HashMap<String, Object>> {
		Listitem o = new Listitem();

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Utils.showProcessDialog(WriteAddrActivity.this, "正在保存...");
		}

		public SaveAddr() {
		}

		@Override
		protected HashMap<String, Object> doInBackground(Void... params) {
			// 如果当前没有完成城市信息就停在这；一直到城市信息完成后，再执行后面的；
			ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();

			BasicNameValuePair mPair01 = new BasicNameValuePair("address.name",
					str_name);
			BasicNameValuePair mPair02 = new BasicNameValuePair(
					"address.provinceId", provinceId);
			BasicNameValuePair mPair03 = new BasicNameValuePair(
					"address.address", str_addr);
			BasicNameValuePair mPair04 = new BasicNameValuePair(
					"address.email", str_email);
			BasicNameValuePair mPair05 = new BasicNameValuePair("address.tel",
					str_phone);
			BasicNameValuePair mPair06 = new BasicNameValuePair(
					"address.postcode", str_youbian);
			BasicNameValuePair mPair07 = new BasicNameValuePair(
					"address.userId",
					PerfHelper.getStringData(PerfHelper.P_USERID));
			BasicNameValuePair mPair08 = new BasicNameValuePair("address.id",
					addrId);
			BasicNameValuePair mPair09 = new BasicNameValuePair(
					"address.cityId", cityId);
			BasicNameValuePair mPair10 = new BasicNameValuePair(
					"address.areaId", areaId);
			BasicNameValuePair mPair11 = null;
			if (item != null) {
				mPair11 = new BasicNameValuePair("address.isDefualt", item.sa);
			} else {
				mPair11 = new BasicNameValuePair("address.isDefualt", "1");
			}
			// 加入集合
			list.add(mPair01);
			list.add(mPair02);
			list.add(mPair03);
			list.add(mPair04);
			list.add(mPair05);
			list.add(mPair06);
			list.add(mPair07);
			list.add(mPair08);
			list.add(mPair09);
			list.add(mPair10);
			list.add(mPair11);

			String json = "";
			HashMap<String, Object> mhashmap = new HashMap<String, Object>();
			try {
				json = DNDataSource.list_FromNET(
						getResources().getString(
								R.string.url_saveorupdate_consignee), list);
				if (ShareApplication.debug) {
					System.out.println("添加地址保存返回:" + json);
				}
				Data date = parseJson(json);
				mhashmap.put("responseCode", date.obj1);
				mhashmap.put("results", date.list);
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
			if (!Utils.isNetworkAvailable(WriteAddrActivity.this)) {
				Utils.showToast(R.string.network_error);
				return;
			}
			if (result == null) {
				Toast.makeText(WriteAddrActivity.this, "请求失败，请稍后再试",
						Toast.LENGTH_SHORT).show();
			} else if ("1".equals(result.get("responseCode"))) {
				WriteAddrActivity.this.finish();// 关闭子窗口ChildActivity
				Utils.showToast("保存成功");
				Intent intent = new Intent();
				intent.setClass(WriteAddrActivity.this, AddAddrActivity.class);
				startActivity(intent);
			} else {
				Toast.makeText(WriteAddrActivity.this, "保存失败",
						Toast.LENGTH_SHORT).show();
			}
			flag = true;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// switch (resultCode) {
		// case 1:
		// if (data != null) {
		// fragmentTransaction = getSupportFragmentManager()
		// .beginTransaction();
		// Fragment m_list_frag_4 = CollectSupplyFragment.newInstance("22",
		// "22");
		// fragmentTransaction.replace(R.id.part_content, m_list_frag_4)
		// .commitAllowingStateLoss();
		// supply_rl.setBackgroundResource(R.drawable.second_bg2);
		// buy_rl.setBackgroundResource(R.drawable.second_bg1);
		// company_rl.setBackgroundResource(R.drawable.second_bg1);
		// }
		// break;
		// }
	}
}
