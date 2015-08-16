package com.pyxx.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pyxx.app.ShareApplication;
import com.pyxx.basefragment.BaseFragment;
import com.pyxx.basefragment.LoadMoreListFragment;
import com.pyxx.dao.DBHelper;
import com.pyxx.datasource.DNDataSource;
import com.pyxx.entity.Data;
import com.pyxx.entity.Listitem;
import com.pyxx.exceptions.DataException;
import com.pyxx.loadimage.Utils;
import com.pyxx.part_activiy.GoodsArticleActivity;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.PerfHelper;

public class FenLeiListFragment extends LoadMoreListFragment<Listitem> {

	RelativeLayout rl_quyu, rl_meishi, rl_main, rl_type, rl_city, rl_paixu;
	private TextView type_text, city_text, paixu_text;
	LinearLayout rl_erji;
	private String typeId = "", cityId = "", sortId = "";
	private static String infoId = "";
	private List<Listitem> list_types, list_citys, all_citys;
	private PopupWindow pop;
	private View vi;
	private LayoutInflater inflater;
	private ListView sel_lv;
	private int selected1 = -1, selected2 = -1, selected3 = -1;
	private ListAdapter typeAdapter;
	private ListAdapter2 cityAdapter;
	private ListAdapter3 paixuAdapter;
	private String[] paixu = new String[] { "人气最高", "评价最高", "离我最近", "最新发布" };
	private Data data2 = null;
	boolean cityFlag = false;

	/**
	 * fragment创建
	 * 
	 * @param type
	 *            当前列表SA类型
	 * @param partTyper
	 *            当前栏目大栏目类型
	 * @return
	 */
	public static BaseFragment<Listitem> newInstance(String type,
			String partType, String urltype) {
		infoId = type;
		final FenLeiListFragment tf = new FenLeiListFragment();
		tf.initType(type, partType, urltype);
		return tf;
	}

	@Override
	public void findView() {
		super.findView();
		int w = (105 * PerfHelper.getIntData(PerfHelper.P_PHONE_W)) / 480;
		// showpop();
		typeId = infoId.replace("FenLeiListFragment", "").trim();
		mIcon_Layout = new LinearLayout.LayoutParams(w,
				LayoutParams.WRAP_CONTENT);
		mIcon_Layout.rightMargin = (8 * PerfHelper
				.getIntData(PerfHelper.P_PHONE_W)) / 480;

		getTypes();
		// getCitys();
		list_citys = new ArrayList<Listitem>();
		Listitem ls = new Listitem();
		ls.nid = "";
		ls.title = "全部";
		list_citys.add(ls);
		new selAllExitCity().execute((Void) null);
		inflater = LayoutInflater.from(getActivity());
		vi = inflater.inflate(R.layout.list_type, null);
		pop = new PopupWindow(vi, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT, true);
		sel_lv = (ListView) vi.findViewById(R.id.type_lv);
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setFocusable(true);
		pop.setTouchable(true);

		example();
	}

	public void example() {
		rl_main = (RelativeLayout) mMain_layout.findViewById(R.id.main_rl);
		rl_erji = (LinearLayout) mMain_layout
				.findViewById(R.id.title_meishi_ll);
		rl_main.setVisibility(View.GONE);
		rl_erji.setVisibility(View.VISIBLE);

		rl_type = (RelativeLayout) mMain_layout
				.findViewById(R.id.title_type_rl);
		rl_city = (RelativeLayout) mMain_layout
				.findViewById(R.id.title_city_rl);
		rl_paixu = (RelativeLayout) mMain_layout
				.findViewById(R.id.title_paixu_rl);

		type_text = (TextView) mMain_layout.findViewById(R.id.title_type_text);
		city_text = (TextView) mMain_layout.findViewById(R.id.title_city_text);
		paixu_text = (TextView) mMain_layout
				.findViewById(R.id.title_paixu_text);

		for (int c = 0; c < list_types.size(); c++) {
			Listitem itm = list_types.get(c);
			if (typeId.equals(itm.nid)) {
				type_text.setText(itm.title);
			}
		}

		type_text.setTextColor(Color.BLACK);
		city_text.setTextColor(getResources().getColor(R.color.erji_text));
		paixu_text.setTextColor(getResources().getColor(R.color.erji_text));

		initClik();
	}

	@Override
	public Data getDataFromNet(String url, String oldtype, int page, int count,
			boolean isfirst, String parttype) throws Exception {
		return super.getDataFromNet(
				getString(R.string.url_sel_product)
						+ "commodity.menuId=6&commodity.typeId=" + typeId
						+ "&commodity.cityId=" + cityId + "&sort=" + sortId
						+ "&commodity.lng="
						+ PerfHelper.getStringData(PerfHelper.P_GPS_LONG)
						+ "&commodity.lat="
						+ PerfHelper.getStringData(PerfHelper.P_GPS_LATI),
				oldtype, page, count, isfirst, parttype);
	}

	public void initClik() {
		rl_type.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				type_text.setTextColor(Color.BLACK);
				city_text.setTextColor(getResources().getColor(
						R.color.erji_text));
				paixu_text.setTextColor(getResources().getColor(
						R.color.erji_text));
				if (pop.isShowing()) {
					// 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏
					pop.dismiss();
				} else {
					// 显示窗口
					pop.showAsDropDown(v);
				}
				typeAdapter = new ListAdapter(mContext, list_types);
				sel_lv.setAdapter(typeAdapter);
			}
		});
		rl_city.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				type_text.setTextColor(getResources().getColor(
						R.color.erji_text));
				city_text.setTextColor(Color.BLACK);
				paixu_text.setTextColor(getResources().getColor(
						R.color.erji_text));
				if (pop.isShowing()) {
					// 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏
					pop.dismiss();
				} else {
					// 显示窗口
					pop.showAsDropDown(v);
				}
				cityAdapter = new ListAdapter2(mContext, list_citys);
				sel_lv.setAdapter(cityAdapter);

			}
		});
		rl_paixu.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				type_text.setTextColor(getResources().getColor(
						R.color.erji_text));
				city_text.setTextColor(getResources().getColor(
						R.color.erji_text));
				paixu_text.setTextColor(Color.BLACK);
				if (pop.isShowing()) {
					// 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏
					pop.dismiss();
				} else {
					// 显示窗口
					pop.showAsDropDown(v);
				}
				paixuAdapter = new ListAdapter3(mContext, paixu);
				sel_lv.setAdapter(paixuAdapter);
			}
		});
	}

	public void keyword_update(String type, String partType) {
		this.mOldtype = type;
		this.mParttype = partType;
		new Thread(new Runnable() {
			@Override
			public void run() {
				reFlush();
			}
		}).start();
	}

	public void getTypes() {
		list_types = new ArrayList<Listitem>();
		Cursor cursor = DBHelper
				.getDBHelper()
				.getReadableDatabase()
				.rawQuery(
						"SELECT * FROM part_list where part_type='"
								+ "all_type" + "'", null);
		if (cursor == null) {

		} else {
			while (cursor.moveToNext()) {
				Listitem item = new Listitem();
				item.c_id = cursor.getInt(0);
				item.title = cursor.getString(1);
				item.nid = cursor.getString(2);
				item.getMark();
				list_types.add(item);
			}
		}
		cursor.close();
	}

	public void getCitys() {
		list_citys = new ArrayList<Listitem>();
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
				list_citys.add(item);
			}
		}
		cursor.close();
	}

	/**
	 * ListViewAdapter
	 * 
	 * @author sy
	 * 
	 */
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
			Listitem li = list_types.get(position);
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
			TextView iv;

			public TextViewListener(int position, TextView iv) {
				this.position = position;
				this.iv = iv;
			}

			public void onClick(View v) {
				if (pop.isShowing()) {
					// 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏
					pop.dismiss();
				} else {
					// 显示窗口
					pop.showAsDropDown(v);
				}
				Listitem li1 = list_types.get(position);
				selected1 = position;
				typeId = li1.nid;
				type_text.setText(li1.title);
				keyword_update("", "");
			}
		}
	}

	/**
	 * ListViewAdapter
	 * 
	 * @author sy
	 * 
	 */
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
			Listitem li = list_citys.get(position);
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
			TextView iv;

			public TextViewListener(int position, TextView iv) {
				this.position = position;
				this.iv = iv;
			}

			public void onClick(View v) {
				if (pop.isShowing()) {
					// 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏
					pop.dismiss();
				} else {
					// 显示窗口
					pop.showAsDropDown(v);
				}
				Listitem li2 = list_citys.get(position);
				selected2 = position;
				cityId = li2.nid;
				keyword_update("", "");
			}
		}
	}

	/**
	 * ListViewAdapter
	 * 
	 * @author sy
	 * 
	 */
	private class ListAdapter3 extends BaseAdapter {
		private LayoutInflater inflater;
		private String[] list;

		public ListAdapter3(Context context, String[] list) {

			this.inflater = LayoutInflater.from(context);
			this.list = list;

		}

		@Override
		public int getCount() {
			return list.length;
		}

		@Override
		public Object getItem(int position) {
			return list[position];
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
			holder.fenleiName.setText(paixu[position]);
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
			TextView iv;

			public TextViewListener(int position, TextView iv) {
				this.position = position;
				this.iv = iv;
			}

			public void onClick(View v) {
				if (pop.isShowing()) {
					// 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏
					pop.dismiss();
				} else {
					// 显示窗口
					pop.showAsDropDown(v);
				}
				selected3 = position;
				sortId = position + 2 + "";
				keyword_update("", "");
			}
		}
	}

	/**
	 * 列表项目展示
	 */
	@Override
	public View getListItemview(View view, final Listitem item, int position) {
		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(
					R.layout.listitem_shops_easy, null);
		}
		TextView title = (TextView) view.findViewById(R.id.listitem_title);
		ImageView phone = (ImageView) view.findViewById(R.id.listitem_phone);
		TextView des = (TextView) view.findViewById(R.id.listitem_summary);
		title.setText(item.title);
		des.setText(item.des);

		phone.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
						+ item.phone));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});

		ImageView icon = (ImageView) view.findViewById(R.id.listitem_icon);
		if (item.icon != null && item.icon.length() > 10) {
			ShareApplication.mImageWorker.loadImage(item.icon, icon);
			icon.setVisibility(View.VISIBLE);
		} else {
			icon.setImageResource(R.drawable.list_qst);
			icon.setVisibility(View.VISIBLE);
		}
		return view;
	}

	@Override
	public boolean dealClick(Listitem item, int position) {
		Intent intent = new Intent();
		intent.setClass(mContext, GoodsArticleActivity.class);
		intent.putExtra("item", item);
		startActivity(intent);
		return super.dealClick(item, position);
	}

	@Override
	public void addListener() {
		super.addListener();
		if (mOldtype.startsWith(DBHelper.FAV_FLAG)) {
			mListview.setOnItemLongClickListener(new OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
						final int arg2, long arg3) {
					final Listitem li = (Listitem) arg0.getItemAtPosition(arg2);
					AlertDialog ad = new AlertDialog.Builder(getActivity())
							.setMessage("您确认要删除本条记录吗？")
							.setPositiveButton("确定", new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									mlistAdapter.datas.remove(arg2
											- mListview.getHeaderViewsCount());
									mlistAdapter.notifyDataSetChanged();
									DBHelper.getDBHelper().delete("listitemfa",
											"n_mark=?",
											new String[] { li.n_mark });
								}
							}).setNegativeButton("取消", new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).show();
					return false;
				}
			});
		}
	}

	/**
	 * Json解析
	 */
	public Data parseJson(String json) throws Exception {
		Data data = new Data();
		JSONObject jsonobj = new JSONObject(json);
		if (jsonobj.has("code")) {
			int code = jsonobj.getInt("code");
			if (code != 1) {
				throw new DataException("数据获取异常");
			}
		}
		JSONArray jsonay = jsonobj.getJSONArray("lists");
		int count = jsonay.length();
		for (int i = 0; i < count; i++) {
			Listitem o = new Listitem();
			JSONObject obj = jsonay.getJSONObject(i);
			o.nid = obj.getString("id");

			try {
				if (obj.has("title")) {
					o.title = obj.getString("title");
				}
				if (obj.has("content")) {
					o.des = obj.getString("content");
				}
				if (obj.has("tel")) {
					o.phone = obj.getString("tel");
				}
				if (obj.has("addTime")) {
					o.u_date = obj.getString("addTime");
				}
				if (obj.has("logo")) {
					o.icon = obj.getString("logo");
				}
				if (obj.has("digest")) {
					o.shangjia = obj.getString("digest");
				}
				if (obj.has("address")) {
					o.address = obj.getString("address");
				}
				if (obj.has("lat")) {
					o.latitude = obj.getString("lat");
				}
				if (obj.has("lng")) {
					o.longitude = obj.getString("lng");
				}
				if (obj.has("price")) {
					o.other = obj.getString("price");
				}
				if (obj.has("typeId")) {
					o.other1 = obj.getString("typeId");
				}
				o.list_type = "6";
				o.show_type = "3";
			} catch (Exception e) {
			}
			o.getMark();
			data.list.add(o);
		}
		if (data.list.size() == 0) {
			if (mData != null) {
				if (mData.list.size() > 0) {
					mData.list.clear();
				}
			}
		}
		return data;
	}

	class selAllExitCity extends AsyncTask<Void, Void, HashMap<String, Object>> {

		// boolean isFav;

		public selAllExitCity() {
		}

		@Override
		protected HashMap<String, Object> doInBackground(Void... params) {
			// http://192.168.1.11:8080/Mineralsa/ket/addComment.action?userId=4&ishave=1&supplyId=2&relevanceId=1
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("commodity.menuId", "6"));
			String json = "";
			String js = ShareApplication.share.getResources().getString(
					R.string.url_sel_product);
			// 加入集合
			HashMap<String, Object> mhashmap = new HashMap<String, Object>();
			try {
				json = DNDataSource.list_FromNET(js, param);
				if (ShareApplication.debug) {
					System.out.println("收藏返回:" + json);
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
			if (jsonobj.has("code")) {
				int code = jsonobj.getInt("code");
				if (code == 0) {
					throw new DataException(jsonobj.getString("msg"));
				}
			}
			JSONArray jsonay = null;
			try {
				jsonay = jsonobj.getJSONArray("lists");
			} catch (Exception e) {
				System.out.println(e);
			}
			int count = jsonay.length();
			for (int i = 0; i < count; i++) {
				Listitem o = new Listitem();
				JSONObject obj = jsonay.getJSONObject(i);
				o.nid = obj.getString("id");
				o.title = obj.getString("title");
				try {
					String cid = obj.getString("cityId");
					boolean flag = false;
					for (int x = 0; x < list_citys.size(); x++) {
						Listitem it1 = list_citys.get(x);
						if (cid.equals(it1.nid)) {
							flag = true;
						}
					}
					if (flag) {

					} else {
						Listitem ltm = new Listitem();
						ltm.nid = obj.getString("cityId");
						ltm.title = obj.getString("cityName");
						list_citys.add(ltm);
					}
				} catch (Exception e) {
				}
				o.getMark();
				// o.list_type = obj.getString("type");
				data.list.add(o);
			}
			return data;
		}

		@Override
		protected void onPostExecute(HashMap<String, Object> result) {
			super.onPostExecute(result);
			Utils.dismissProcessDialog();
			if (result == null) {
				Utils.showToast("信息获取失败");
			}
			if ("1".equals(result.get("responseCode"))) {
				cityFlag = true;
			} else {
			}
		}
	}

}
