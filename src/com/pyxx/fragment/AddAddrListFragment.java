package com.pyxx.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.pyxx.app.ShareApplication;
import com.pyxx.basefragment.BaseFragment;
import com.pyxx.basefragment.LoadMoreListFragment;
import com.pyxx.dao.DBHelper;
import com.pyxx.datasource.DNDataSource;
import com.pyxx.entity.Data;
import com.pyxx.entity.Listitem;
import com.pyxx.exceptions.DataException;
import com.pyxx.loadimage.Utils;
import com.pyxx.part_activiy.AddAddrActivity;
import com.pyxx.part_activiy.WriteAddrActivity;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.PerfHelper;

/**
 * 添加地址后 显示的地址列表
 * 
 * @author wll
 */
public class AddAddrListFragment extends LoadMoreListFragment<Listitem> {

	private RelativeLayout main_rl;
	private LinearLayout erji_rl;
	private ImageView selected_iv;
	private TextView bianji_tv, delete_tv, default_tv;
	private static AddAddrActivity aat = null;

	private String addrId;

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
			String partType, String urltype, AddAddrActivity aat) {

		final AddAddrListFragment fragment = new AddAddrListFragment();
		AddAddrListFragment.aat = aat;
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
	public View getListItemview(View view, final Listitem item,
			final int position) {
		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(R.layout.addaddr_list,
					null);
		}
		selected_iv = (ImageView) view.findViewById(R.id.addr_iv);
		bianji_tv = (TextView) view.findViewById(R.id.bianji_tv);
		delete_tv = (TextView) view.findViewById(R.id.delete_tv);
		default_tv = (TextView) view.findViewById(R.id.addr_tv);
		TextView addr_tv = (TextView) view.findViewById(R.id.listitem_addr);
		TextView name_tv = (TextView) view.findViewById(R.id.listitem_name);
		TextView phone_tv = (TextView) view.findViewById(R.id.listitem_phone);

		addr_tv.setText(item.address);
		name_tv.setText(item.title);
		phone_tv.setText(item.phone);

		if ("1".equals(item.sa)) {
			selected_iv.setImageResource(R.drawable.selected2);
			default_tv.setText("默认地址");
		} else {
			selected_iv.setImageResource(R.drawable.selected1);
			default_tv.setText("设为默认");
		}
		// 选址默认地址
		selected_iv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if ("1".equals(item.sa)) {
					selected_iv.setImageResource(R.drawable.selected2);
					default_tv.setText("默认地址");
				} else {

					addrId = item.nid;
					new UpdateAddr().execute((Void) null);
				}
			}
		});
		// 编辑地址
		bianji_tv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// aat.finish();
				Intent intent = new Intent();
				intent.setClass(mContext, WriteAddrActivity.class);
				intent.putExtra("item", item);
				startActivity(intent);

			}
		});
		// 删除地址
		delete_tv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				addrId = item.nid;
				new DeleteAddr().execute((Void) null);
			}
		});

		view.findViewById(R.id.listitem_select);

		return view;
	}

	@Override
	public boolean dealClick(Listitem item, int position) {
		// Intent intent = new Intent();
		// intent.setClass(mContext, ZixunArticleActivity.class);
		// intent.putExtra("item", item);
		// startActivity(intent);
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
	 * Json 数据解析
	 */
	public Data parseJson(String json) throws Exception {
		Data data = new Data();
		int code = -1;
		JSONObject jsonobj = new JSONObject(json);
		if (jsonobj.has("code")) {
			code = jsonobj.getInt("code");
			if (code == 0) {
				throw new DataException("数据获取异常");
			} else if (code == 2) {
				aat.method();
			}
		}

		if (code == 1) {
			JSONArray jsonay = jsonobj.getJSONArray("lists");
			int count = jsonay.length();
			for (int i = 0; i < count; i++) {
				Listitem o = new Listitem();
				JSONObject obj = jsonay.getJSONObject(i);
				o.nid = obj.getString("id");

				try {
					if (obj.has("address")) {
						o.address = obj.getString("address");
					}
					if (obj.has("name")) {
						o.title = obj.getString("name");
					}
					if (obj.has("tel")) {
						o.phone = obj.getString("tel");
					}
					if (obj.has("addTime")) {
						o.u_date = obj.getString("addTime");
					}
					if (obj.has("email")) {
						o.other = obj.getString("email");
					}
					if (obj.has("postcode")) {
						o.other1 = obj.getString("postcode");
					}
					if (obj.has("provinceId")) {
						o.other2 = obj.getString("provinceId");
					}
					if (obj.has("cityId")) {
						o.other3 = obj.getString("cityId");
					}
					if (obj.has("areaId")) {
						o.fuwu = obj.getString("areaId");
					}
					if (obj.has("isDefualt")) {
						o.sa = obj.getString("isDefualt");// 是否为默认图标
					}

				} catch (Exception e) {
				}
				o.getMark();
				data.list.add(o);
			}
		}
		return data;
	}

	/**
	 * 删除地址
	 * 
	 * @author wll
	 */
	class DeleteAddr extends AsyncTask<Void, Void, HashMap<String, Object>> {
		Listitem o = new Listitem();

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Utils.showProcessDialog(mContext, "正在删除...");
		}

		/**
		 * 删除地址
		 */
		public DeleteAddr() {
		}

		@Override
		protected HashMap<String, Object> doInBackground(Void... params) {
			// 如果当前没有完成城市信息就停在这；一直到城市信息完成后，再执行后面的；
			ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();

			BasicNameValuePair mPair01 = new BasicNameValuePair("address.id",
					addrId);
			BasicNameValuePair mPair02 = new BasicNameValuePair(
					"address.userId",
					PerfHelper.getStringData(PerfHelper.P_USERID));
			// 加入集合
			list.add(mPair01);
			list.add(mPair02);

			String json = "";
			HashMap<String, Object> mhashmap = new HashMap<String, Object>();
			try {
				json = DNDataSource.list_FromNET(
						getResources().getString(R.string.url_del_consignee),
						list);
				if (ShareApplication.debug) {
					System.out.println("删除地址返回:" + json);
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
			if (!Utils.isNetworkAvailable(mContext)) {
				Utils.showToast(R.string.network_error);
				return;
			}
			if (result == null) {
				Toast.makeText(mContext, "请求失败，请稍后再试", Toast.LENGTH_SHORT)
						.show();
			} else if ("1".equals(result.get("responseCode"))) {
				Utils.showToast("删除成功");
				aat.finish();
				Intent intent = new Intent();
				intent.setClass(mContext, AddAddrActivity.class);
				startActivity(intent);
			} else {
				Toast.makeText(mContext, "删除失败", Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * 更新地址
	 * 
	 * @author wll
	 */
	class UpdateAddr extends AsyncTask<Void, Void, HashMap<String, Object>> {

		Listitem o = new Listitem();

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Utils.showProcessDialog(mContext, "正在修改...");
		}

		/**
		 * 选择默认地址 更新地址列表
		 */
		public UpdateAddr() {

		}

		@Override
		protected HashMap<String, Object> doInBackground(Void... params) {
			// 如果当前没有完成城市信息就停在这；一直到城市信息完成后，再执行后面的；
			ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();

			BasicNameValuePair mPair07 = new BasicNameValuePair(
					"address.userId",
					PerfHelper.getStringData(PerfHelper.P_USERID));
			BasicNameValuePair mPair08 = new BasicNameValuePair("address.id",
					addrId);

			list.add(mPair07);
			list.add(mPair08);

			String json = "";
			HashMap<String, Object> mhashmap = new HashMap<String, Object>();
			try {
				json = DNDataSource.list_FromNET(
						getResources().getString(R.string.url_update_default),
						list);
				if (ShareApplication.debug) {
					System.out.println("收货地址返回:" + json);
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
			if (!Utils.isNetworkAvailable(mContext)) {
				Utils.showToast(R.string.network_error);
				return;
			}
			if (result == null) {
				Toast.makeText(mContext, "请求失败，请稍后再试", Toast.LENGTH_SHORT)
						.show();
			} else if ("1".equals(result.get("responseCode"))) {
				Utils.showToast("设置成功");
				aat.finish();
				Intent intent = new Intent();
				intent.setClass(mContext, AddAddrActivity.class);
				startActivity(intent);
			} else {
				Toast.makeText(mContext, "设置失败", Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * 加载二级栏目(展示弹出框)
	 */
	private void showpop() {
		mMain_layout.findViewById(R.id.seccond_item)
				.setVisibility(View.VISIBLE);
		final View mark = mMain_layout.findViewById(R.id.second_part_1_mark);
		/** 设置二级栏目弹出框 */
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.list_grid, null);
		// 生成动态数组，并且转入数据
		ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
		String[] secondtitle = getResources()
				.getStringArray(R.array.secondtype);
		for (int i = 0; i < 10; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemText", secondtitle[i]);// 按序号做ItemText
			lstImageItem.add(map);
		}
		// 生成适配器的ImageItem <====> 动态数组的元素，两者一一对应
		SimpleAdapter saImageItems = new SimpleAdapter(mContext, lstImageItem,
				R.layout.listitem_second_grid, new String[] { "ItemText" },
				new int[] { R.id.second_item_grid_text });
		// 创建PopupWindow对象
		final PopupWindow pop = new PopupWindow(view,
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, false);
		GridView grid = (GridView) view.findViewById(R.id.second_item_grid);
		// 添加并且显示
		grid.setAdapter(saImageItems);
		grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

			}
		});
		// 需要设置一下此参数，点击外边可消失
		pop.setBackgroundDrawable(new BitmapDrawable());
		// 设置点击窗口外边窗口消失
		pop.setOutsideTouchable(true);
		// 设置此参数获得焦点，否则无法点击
		pop.setFocusable(true);

		// 加载动画
		final Animation animation_n = AnimationUtils.loadAnimation(mContext,
				R.animator.second_mark_rotate_n);
		// 以下两个属性设置位移动画的停止
		animation_n.setFillEnabled(true);
		animation_n.setFillAfter(true);
		// 加载动画
		final Animation animation_h = AnimationUtils.loadAnimation(mContext,
				R.animator.second_mark_rotate_h);
		// 以下两个属性设置位移动画的停止
		animation_h.setFillEnabled(true);
		animation_h.setFillAfter(true);
		view.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (pop.isShowing()) {
					// 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏
					pop.dismiss();
				} else {
					// 显示窗口
					pop.showAsDropDown(v);
					mark.startAnimation(animation_n);
				}
			}
		});
		pop.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				mark.startAnimation(animation_h);
			}
		});
		mMain_layout.findViewById(R.id.seccond_item).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (pop.isShowing()) {
							// 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏
							pop.dismiss();
						} else {
							// 显示窗口
							pop.showAsDropDown(v);
							mark.startAnimation(animation_n);
						}

					}
				});

	}

	class GridAdpter extends BaseAdapter {

		public GridAdpter(ArrayList<HashMap<String, String>> str) {
		}

		@Override
		public int getCount() {
			return 0;
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
			return null;
		}
	}

}
