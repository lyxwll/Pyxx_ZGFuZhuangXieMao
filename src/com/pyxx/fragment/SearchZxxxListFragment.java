package com.pyxx.fragment;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pyxx.app.ShareApplication;
import com.pyxx.basefragment.BaseFragment;
import com.pyxx.basefragment.LoadMoreListFragment;
import com.pyxx.dao.DBHelper;
import com.pyxx.entity.Data;
import com.pyxx.entity.Listitem;
import com.pyxx.part_activiy.ZixunArticleActivity;
import com.pyxx.view.TitleWebView;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.FinalVariable;
import com.utils.PerfHelper;

public class SearchZxxxListFragment extends LoadMoreListFragment<Listitem>{
	
	private String part_name;
	public Data data2 = null;
	// LinearLayout linearLayout_gongqiu;

	RelativeLayout rl_quyu, rl_meishi,rl_main;
	LinearLayout rl_erji;
	

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
			String partType,String url) {
		final SearchZxxxListFragment tf = new SearchZxxxListFragment();
		tf.initType(type, partType,url);
		return tf;
	}

	RelativeLayout.LayoutParams mLa;
	RelativeLayout.LayoutParams relal;
	LinearLayout.LayoutParams frla;

	public void keyword_update(String type, String partType) {
		this.mOldtype = type;
		this.mParttype = partType;
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				reFlush();
			}
		}).start();
	}

	@Override
	public void findView() {
		super.findView();
		csl_r = (ColorStateList) getResources().getColorStateList(
				R.color.list_item_title_r);
		csl_n = (ColorStateList) getResources().getColorStateList(
				R.color.list_item_title_n);
		int h = (330 * (PerfHelper.getIntData(PerfHelper.P_PHONE_W))) / 640;
		mHead_Layout = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.FILL_PARENT, h);

		int w = (120 * PerfHelper.getIntData(PerfHelper.P_PHONE_W)) / 480;
		h = (120 * PerfHelper.getIntData(PerfHelper.P_PHONE_W)) / 480;
		mIcon_Layout = new LinearLayout.LayoutParams(w, h);
		mLa = new RelativeLayout.LayoutParams(w, h);
		frla = new LinearLayout.LayoutParams(w, h);
		w = (120 * PerfHelper.getIntData(PerfHelper.P_PHONE_W)) / 480;
		h = (120 * PerfHelper.getIntData(PerfHelper.P_PHONE_W)) / 480;
		example();
		relal = new RelativeLayout.LayoutParams(w, h);
		relal.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		relal.rightMargin = 10;
		try {
			part_name = DBHelper.getDBHelper().select("part_list", "part_name",
					"part_sa=?", new String[] { mOldtype });
		} catch (Exception e) {
			e.printStackTrace();
		}
		// mListview.di

	}

	public void example() {
	    rl_main =(RelativeLayout)mMain_layout.findViewById(R.id.main_rl);
        rl_main.setVisibility(View.GONE);
        rl_erji = (LinearLayout)mMain_layout.findViewById(R.id.title_meishi_ll);
        rl_erji.setVisibility(View.GONE);
	}

	
	

	/**
	 * 列表项目展示
	 */
	TextView[] tags = new TextView[5];
	ColorStateList csl_r;
	ColorStateList csl_n;
	String juli_no = "未知";

	// TODO
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
		des.setText(item.other1);
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

	@Override
	public boolean dealClick(Listitem item, int position) {
		if (mlistAdapter != null && mlistAdapter.datas.size() > 0
				&& null != item) {
			Intent intent = new Intent();
				intent.setClass(mContext, ZixunArticleActivity.class);
			intent.putExtra("item", item);
			startActivity(intent);
			return true;
		}
		return false;
	}

	public Data parseJson(String json) throws Exception {
		Data data = new Data();
		JSONObject jsonobj = new JSONObject(json);
		if (jsonobj.has("code")) {
			int code = jsonobj.getInt("code");
			if (code != 1) {
				mHandler.sendEmptyMessage(FinalVariable.error);
				return data;
			}
		}
		System.out.println(json);
		JSONArray jsonay = jsonobj.getJSONArray("heads");
		
		JSONArray jsonay2 = jsonobj.getJSONArray("lists");

		int count = jsonay.length();
		int count2 = jsonay2.length();
		for (int i = 0; i < count; i++) {
			Listitem o = new Listitem();
			JSONObject obj = jsonay.getJSONObject(i);
			o.nid = obj.getString("id");
			o.sa = mOldtype + "_" + part_name;
			try {
				if (obj.has("addTime")) { // 发布时间
					o.u_date = obj.getString("addTime");
				}
				if (obj.has("logo")) { // icon
					o.icon = obj.getString("logo");
				}
				if (obj.has("title")) { // 标题
					o.title = obj.getString("title");
				}
				if (obj.has("digest")) { // 资讯摘要
					o.other1 = obj.getString("digest");
				}
				if (obj.has("content")) { // 资讯内容
					o.other2 = obj.getString("content");
				}
				if(obj.has("source")){
					o.other3 = obj.getString("source");
				}
				if(obj.has("imgs")){
					o.img_list_1 = obj.getString("imgs");
				}
				o.list_type = "5";
				o.show_type = "4";
			} catch (Exception e) {
			}
			o.getMark();
			data.list.add(o);
		}
		for (int j = 0; j < count2; j++) {
			Listitem o = new Listitem();
			JSONObject obj = jsonay2.getJSONObject(j);
			o.nid = obj.getString("id");
			o.sa = mOldtype + "_" + part_name;
			try {
				if (obj.has("addTime")) { // 发布时间
					o.u_date = obj.getString("addTime");
				}
				if (obj.has("logo")) { // icon
					o.icon = obj.getString("logo");
				}
				if (obj.has("title")) { // 标题
					o.title = obj.getString("title");
				}
				if (obj.has("digest")) { // 资讯摘要
					o.other1 = obj.getString("digest");
				}
				if (obj.has("content")) { // 资讯内容
					o.other2 = obj.getString("content");
				}
				if(obj.has("source")){
					o.other3 = obj.getString("source");
				}
				if(obj.has("imgs")){
					o.img_list_1 = obj.getString("imgs");
				}
				o.list_type = "5";
				o.show_type = "4";
			} catch (Exception e) {
			}
			o.getMark();
			data.list.add(o);
		}
		return data;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub	
		super.onResume();
	}
	
}
