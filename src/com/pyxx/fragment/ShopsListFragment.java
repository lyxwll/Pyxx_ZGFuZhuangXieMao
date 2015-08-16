package com.pyxx.fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pyxx.app.ShareApplication;
import com.pyxx.basefragment.BaseFragment;
import com.pyxx.basefragment.LoadMoreListFragment;
import com.pyxx.dao.DBHelper;
import com.pyxx.entity.Data;
import com.pyxx.entity.Listitem;
import com.pyxx.exceptions.DataException;
import com.pyxx.loadimage.Utils;
import com.pyxx.part_activiy.ShopsArticleEasyActivity;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.PerfHelper;

/**
 * 服装商家 界面
 * 
 * @author wll
 */
public class ShopsListFragment extends LoadMoreListFragment<Listitem> {
	private RelativeLayout main_rl;
	private LinearLayout erji_rl;

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
		final ShopsListFragment fragment = new ShopsListFragment();
		fragment.initType(type, partType, urltype);
		return fragment;
	}

	@Override
	public void findView() {
		super.findView();
		int w = (105 * PerfHelper.getIntData(PerfHelper.P_PHONE_W)) / 480;
		// showpop();
		mIcon_Layout = new LinearLayout.LayoutParams(w,
				LayoutParams.WRAP_CONTENT);
		mIcon_Layout.rightMargin = (8 * PerfHelper
				.getIntData(PerfHelper.P_PHONE_W)) / 480;

		main_rl = (RelativeLayout) mMain_layout.findViewById(R.id.main_rl);
		erji_rl = (LinearLayout) mMain_layout
				.findViewById(R.id.title_meishi_ll);
		main_rl.setVisibility(View.GONE);
		erji_rl.setVisibility(View.GONE);
	}

	/**
	 * 列表项目展示
	 */
	@Override
	public View getListItemview(View view, final Listitem item, int position) {
		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(
					R.layout.listitem_shops2, null);
		}
		TextView title = (TextView) view.findViewById(R.id.listitem_title);
		TextView phone = (TextView) view.findViewById(R.id.listitem_phone);
		TextView des = (TextView) view.findViewById(R.id.listitem_summary);
		// TextView juli = (TextView)view.findViewById(R.id.listitem_juli);
		// TextView youhui = (TextView)view.findViewById(R.id.listitem_youhui);
		// phone.setText(item.phone);
		title.setText(item.title);
		des.setText(item.des);

		phone.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (item.phone != null && !item.phone.equals("")) {
					Intent intent = new Intent(Intent.ACTION_DIAL, Uri
							.parse("tel:" + item.phone)); // 从后台接受手机号码
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				} else {
					Utils.showToast("电话为空");
				}
			}
		});

		// String ff = PerfHelper.getStringData(PerfHelper.P_GPS_LONG);
		// String fl = PerfHelper.getStringData(PerfHelper.P_GPS_LATI);
		// if (PerfHelper.getBooleanData(PerfHelper.P_GPS_YES)) {
		// String distance_str = null;
		// Double distance = null;
		// distance = GpsUtils.getDistance(Double.parseDouble(item.longitude),
		// Double.parseDouble(item.latitude), Double
		// .parseDouble(PerfHelper
		// .getStringData(PerfHelper.P_GPS_LONG)),
		// Double.parseDouble(PerfHelper
		// .getStringData(PerfHelper.P_GPS_LATI)));
		// distance_str = distance != null ? distance.toString() : null;
		// if (distance_str != null) {
		// int first_index = distance_str.indexOf(".");
		// distance_str = first_index > 0 ? distance_str.substring(0,
		// first_index) : distance_str;
		// }
		// if (Integer.valueOf(distance_str) > 1000) {
		// DecimalFormat dt = (DecimalFormat) DecimalFormat.getInstance(); //
		// 获得格式化类对象
		// dt.applyPattern("0.00");// 设置小数点位数(两位) 余下的会四舍五入
		// Double size = (Double.valueOf(distance_str) / 1000);
		// if (size > 100) {
		// juli.setText("未知");
		// } else {
		// juli.setText(dt.format(size) + "km");
		// }
		//
		// juli.setTextSize(12f);
		// //
		// view_holder.textv_sc_listv_distance.setText((Integer.valueOf(distance_str)/1000)
		// // + "km");
		// }
		// // 没有返回米；
		// else {
		// juli.setText(distance_str + "m");
		// }
		// } else {
		// juli.setText("未知距离");
		// }
		//
		// youhui.setText(Double.parseDouble(item.other)*100+"");
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
		intent.setClass(mContext, ShopsArticleEasyActivity.class);
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
				if (obj.has("name")) {
					o.title = obj.getString("name");
				}
				if (obj.has("sellerBrief")) {
					o.des = obj.getString("sellerBrief");
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
				if (obj.has("productBrief")) {
					o.shangjia = obj.getString("productBrief");
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
				o.list_type = "11";
				o.show_type = "3";
			} catch (Exception e) {
				e.printStackTrace();
			}
			o.getMark();
			data.list.add(o);
		}
		return data;
	}

}
