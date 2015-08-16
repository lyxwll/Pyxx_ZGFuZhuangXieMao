package com.pyxx.fragment;

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
import com.pyxx.entity.DBCollectUtil;
import com.pyxx.entity.Data;
import com.pyxx.entity.Listitem;
import com.pyxx.part_activiy.GoodsArticleActivity;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.PerfHelper;

public class CollectHuoDongListFragment extends LoadMoreListFragment<Listitem> {
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
		final CollectHuoDongListFragment tf = new CollectHuoDongListFragment();
		tf.initType(type, partType, urltype);
		return tf;
	}
	@Override
	public void findView() {
		 		super.findView();
		int w = (105 * PerfHelper.getIntData(PerfHelper.P_PHONE_W)) / 480;
//		showpop();
		mIcon_Layout = new LinearLayout.LayoutParams(w,
				LayoutParams.WRAP_CONTENT);
		mIcon_Layout.rightMargin = (8 * PerfHelper
				.getIntData(PerfHelper.P_PHONE_W)) / 480;
		
		main_rl = (RelativeLayout)mMain_layout.findViewById(R.id.main_rl);
		erji_rl = (LinearLayout)mMain_layout.findViewById(R.id.title_meishi_ll);
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
					R.layout.listitem_goods_easy, null);
		}
		TextView title = (TextView) view.findViewById(R.id.listitem_title);
		TextView jiage = (TextView) view.findViewById(R.id.listitem_jiage);
		title.setText(item.title);
		jiage.setText(item.other+"元   抢购价");
		
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
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(mContext, GoodsArticleActivity.class);
		intent.putExtra("item", item);
		startActivity(intent);
		return super.dealClick(item, position);
	}
	@Override
	public void addListener() {
		// TODO Auto-generated method stub
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
	     data = DBCollectUtil.selectlists("listitemcollect", "list_type,show_type", "11,3");
		return data;
	}

	
}
