package com.pyxx.fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
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
import com.pyxx.part_activiy.AddOrderActivity;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.PerfHelper;

/**
 * 订单列表 显示Fragment
 * 
 * @author wll
 */
public class OrderListFragment extends LoadMoreListFragment<Listitem> {

	private RelativeLayout main_rl;
	private LinearLayout erji_rl;
	private Data allinfo = new Data();

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
		final OrderListFragment tf = new OrderListFragment();
		tf.initType(type, partType, urltype);
		return tf;
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
	public View getListItemview(View view, final Listitem item,
			final int position) {
		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(R.layout.order_list,
					null);
		}
		TextView number = (TextView) view.findViewById(R.id.listitem_number);
		TextView jiage = (TextView) view.findViewById(R.id.listitem_jiage);
		TextView addtime = (TextView) view.findViewById(R.id.listitem_addtime);

		number.setText(item.shangjia);
		jiage.setText(item.other2);
		addtime.setText(item.u_date);

		ImageView icon = (ImageView) view.findViewById(R.id.order_logo);
		if (item.img_list_2 != null && item.img_list_2.length() > 10) {
			ShareApplication.mImageWorker.loadImage(item.img_list_2, icon);
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
		intent.setClass(mContext, AddOrderActivity.class);
		intent.putExtra("item", item);
		intent.putExtra("statue", "2");
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
					new AlertDialog.Builder(getActivity())
							.setMessage("您确认要删除本条记录吗？")
							.setPositiveButton("确定", new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									mlistAdapter.datas.remove(arg2
											- mListview.getHeaderViewsCount());
									mlistAdapter.notifyDataSetChanged();
									DBHelper.getDBHelper().delete(
											"listgoodscar", "c_id=" + li.c_id,
											null);
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
				data.obj1 = jsonobj.getString("code");// 返回状态
				// 0，有数据成功返回，-1，-2，无数据返回，1，返回异常
				return data;
			} else {
				data.obj1 = jsonobj.getString("code");// 返回状态
			}
		}
		try {
			JSONArray array = jsonobj.getJSONArray("lists");
			for (int i = 0; i < array.length(); i++) {
				Listitem o = new Listitem();
				JSONObject js = array.getJSONObject(i);
				o.nid = js.getString("id");
				if (js.has("addTime")) {
					o.u_date = js.getString("addTime");
				}
				if (js.has("")) {

				}
				if (js.has("brief")) {
					o.des = js.getString("brief");
				}
				if (js.has("code")) {
					o.shangjia = js.getString("code");
				}
				if (js.has("express")) {
					o.other = js.getString("express");
				}
				if (js.has("addressId")) {
					o.other1 = js.getString("addressId");
				}
				if (js.has("total")) {
					o.other2 = js.getString("total");
				}
				if (js.has("shopList")) {
					o.img_list_2 = js.getString("shopList");
				}
				data.list.add(o);
			}
		} catch (Exception e) {

		}
		return data;
	}

}
