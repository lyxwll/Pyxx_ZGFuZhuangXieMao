package com.pyxx.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.pyxx.app.ShareApplication;
import com.pyxx.basefragment.BaseFragment;
import com.pyxx.basefragment.LoadMoreListFragment;
import com.pyxx.dao.DBHelper;
import com.pyxx.entity.Data;
import com.pyxx.entity.Listitem;
import com.pyxx.exceptions.DataException;
import com.pyxx.part_activiy.ZixunArticleActivity;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.PerfHelper;

public class ZiXunListFragment extends LoadMoreListFragment<Listitem> {
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
		final ZiXunListFragment tf = new ZiXunListFragment();
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
	}

	/**
	 * 列表项目展示
	 */
	@Override
	public View getListItemview(View view, final Listitem item, int position) {
		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(
					R.layout.listitem_zixun, null);
		}
		TextView title = (TextView) view.findViewById(R.id.listitem_title);
		TextView date = (TextView) view.findViewById(R.id.listitem_date);
		TextView des = (TextView) view.findViewById(R.id.listitem_summary);

		title.setText(item.title);
		date.setText(item.u_date);
		des.setText(item.shangjia);
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
		intent.setClass(mContext, ZixunArticleActivity.class);
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
				if (obj.has("brief")) {
					o.shangjia = obj.getString("brief");
				}
				if (obj.has("addTime")) {
					o.u_date = obj.getString("addTime");
				}
				if (obj.has("logo")) {
					o.icon = obj.getString("logo");
				}
				if (obj.has("source")) {
					o.other = obj.getString("source");
				}
				if (obj.has("imgs")) {
					o.img_list_1 = obj.getString("imgs");
				}
			} catch (Exception e) {
			}
			o.getMark();
			data.list.add(o);
		}
		return data;
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
