package com.pyxx.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
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
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import com.pyxx.app.ShareApplication;
import com.pyxx.basefragment.BaseFragment;
import com.pyxx.basefragment.LoadMoreListFragment;
import com.pyxx.dao.DBHelper;
import com.pyxx.entity.Data;
import com.pyxx.entity.Listitem;
import com.pyxx.entity.Pictorial;
import com.pyxx.part_activiy.GoodsArticleActivity;
import com.pyxx.part_activiy.ShopsArticleActivity;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.PerfHelper;

public class MyCollectListFragment extends LoadMoreListFragment<Pictorial> {
	/**
	 * fragment创建
	 * 
	 * @param type
	 *            当前列表SA类型
	 * @param partTyper
	 *            当前栏目大栏目类型
	 * @return
	 */
	private int size = 3;

	public static BaseFragment<Pictorial> newInstance(String type,
			String partType, String urltype) {
		final MyCollectListFragment tf = new MyCollectListFragment();
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
	public View getListItemview(View view, final Pictorial allPictorial,
			int position) {
		int count = allPictorial.piclist.size();
		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(
					R.layout.mycollect_list, null);
		}
		RelativeLayout rl1 = (RelativeLayout) view
				.findViewById(R.id.nxms_view_rl1);
		RelativeLayout rl2 = (RelativeLayout) view
				.findViewById(R.id.nxms_view_rl2);
		RelativeLayout rl3 = (RelativeLayout) view
				.findViewById(R.id.nxms_view_rl3);

		ImageView iv1 = (ImageView) view.findViewById(R.id.listitem_icon1);
		ImageView iv2 = (ImageView) view.findViewById(R.id.listitem_icon2);
		ImageView iv3 = (ImageView) view.findViewById(R.id.listitem_icon3);

		for (int i = 0; i < count; i++) {
			if (count == 1) {
				rl2.setVisibility(View.INVISIBLE);
				rl3.setVisibility(View.INVISIBLE);
			} else if (count == 2) {
				rl1.setVisibility(View.VISIBLE);
				rl2.setVisibility(View.VISIBLE);
				rl3.setVisibility(View.INVISIBLE);
			} else {
				rl1.setVisibility(View.VISIBLE);
				rl2.setVisibility(View.VISIBLE);
				rl3.setVisibility(View.VISIBLE);
			}
			final Listitem p = (Listitem) allPictorial.piclist.get(i);
			switch (i) {
			case 0:
				if (p.icon != null && p.icon.length() > 10) {
					String[] iconlist = p.icon.split(",");
					ShareApplication.mImageWorker.loadImage(iconlist[0], iv1);
				} else {
					iv1.setImageResource(R.drawable.list_qst);
				}
				iv1.setVisibility(View.VISIBLE);
				iv1.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if ("1".equals(p.list_type)) {
							Intent intent = new Intent();
							intent.setClass(mContext,
									ShopsArticleActivity.class);
							intent.putExtra("item", p);
							mContext.startActivity(intent);
						} else {
							Intent intent = new Intent();
							intent.setClass(mContext,
									GoodsArticleActivity.class);
							intent.putExtra("item", p);
							mContext.startActivity(intent);
						}
					}
				});
				break;
			case 1:
				if (p.icon != null && p.icon.length() > 10) {
					String[] iconlist = p.icon.split(",");
					ShareApplication.mImageWorker.loadImage(iconlist[0], iv2);
				} else {
					iv2.setImageResource(R.drawable.list_qst);
				}
				iv2.setVisibility(View.VISIBLE);
				iv2.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if ("1".equals(p.list_type)) {
							Intent intent = new Intent();
							intent.setClass(mContext,
									ShopsArticleActivity.class);
							intent.putExtra("item", p);
							mContext.startActivity(intent);
						} else {
							Intent intent = new Intent();
							intent.setClass(mContext,
									GoodsArticleActivity.class);
							intent.putExtra("item", p);
							mContext.startActivity(intent);
						}
					}
				});
				break;
			case 2:
				if (p.icon != null && p.icon.length() > 10) {
					String[] iconlist = p.icon.split(",");
					ShareApplication.mImageWorker.loadImage(iconlist[0], iv3);
				} else {
					iv3.setImageResource(R.drawable.list_qst);
				}
				iv3.setVisibility(View.VISIBLE);
				iv3.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if ("1".equals(p.list_type)) {
							Intent intent = new Intent();
							intent.setClass(mContext,
									ShopsArticleActivity.class);
							intent.putExtra("item", p);
							mContext.startActivity(intent);
						} else {
							Intent intent = new Intent();
							intent.setClass(mContext,
									GoodsArticleActivity.class);
							intent.putExtra("item", p);
							mContext.startActivity(intent);
						}
					}
				});
				break;

			default:
				break;
			}
		}
		return view;
	}

	@Override
	public boolean dealClick(Pictorial item, int position) {
		return true;
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
		Cursor cursor = DBHelper.getDBHelper().getReadableDatabase()
				.rawQuery("SELECT * FROM listshops", null);
		Cursor cursor2 = DBHelper.getDBHelper().getReadableDatabase()
				.rawQuery("SELECT * FROM listgoods", null);
		if (cursor == null) {

		} else {
			while (cursor.moveToNext()) {
				Listitem item = new Listitem();
				item.c_id = cursor.getInt(0);
				item.icon = cursor.getString(1);
				item.nid = cursor.getString(2);
				item.title = cursor.getString(3);
				item.des = cursor.getString(4);
				item.phone = cursor.getString(5);
				item.u_date = cursor.getString(6);
				item.other = cursor.getString(7);
				item.address = cursor.getString(8);
				item.latitude = cursor.getString(9);
				item.longitude = cursor.getString(10);
				item.other1 = cursor.getString(11);
				item.list_type = cursor.getString(12);
				item.getMark();
				data.list.add(item);
			}
		}
		if (cursor2 == null) {

		} else {
			while (cursor2.moveToNext()) {
				Listitem item2 = new Listitem();
				item2.c_id = cursor2.getInt(0);
				item2.icon = cursor2.getString(1);
				item2.nid = cursor2.getString(2);
				item2.title = cursor2.getString(3);
				item2.des = cursor2.getString(4);
				item2.phone = cursor2.getString(5);
				item2.u_date = cursor2.getString(6);
				item2.other = cursor2.getString(7);
				item2.other1 = cursor2.getString(8);
				item2.other2 = cursor2.getString(9);
				item2.other3 = cursor2.getString(10);
				item2.fuwu = cursor2.getString(11);
				item2.img_list_1 = cursor2.getString(12);
				item2.isad = cursor2.getString(13);
				item2.ishead = cursor2.getString(14);
				item2.sa = cursor2.getString(15);
				item2.shangjia = cursor2.getString(16);
				item2.list_type = cursor2.getString(17);
				item2.getMark();
				data.list.add(item2);
			}
		}
		Data d = data;
		if (null != d && null != d.list) {
			d.list = (ArrayList<Pictorial>) PictorialOperate.packing(d.list,
					size);
		}
		return d;
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
