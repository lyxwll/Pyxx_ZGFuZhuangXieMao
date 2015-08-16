package com.pyxx.fragment;

import java.util.ArrayList;

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
import com.pyxx.entity.Pictorial;
import com.pyxx.exceptions.DataException;
import com.pyxx.part_activiy.GoodsArticleActivity;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.PerfHelper;

public class RecomandProductListFragment extends LoadMoreListFragment<Pictorial> {
	public static String type="";
	/**
	 * fragment创建
	 * 
	 * @param type
	 *            当前列表SA类型
	 * @param partTyper
	 *            当前栏目大栏目类型
	 * @return
	 */
	private int size=2;
	
	public static BaseFragment<Pictorial> newInstance(String type,
			String partType, String urltype) {
		RecomandProductListFragment.type = type;
		final RecomandProductListFragment tf = new RecomandProductListFragment();
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
		((RelativeLayout) mMain_layout.findViewById(R.id.main_rl)).setVisibility(View.GONE);
		((LinearLayout) mMain_layout.findViewById(R.id.title_meishi_ll)).setVisibility(View.GONE);
	}
	/**
	 * 列表项目展示
	 */
	@Override
	public View getListItemview(View view, final Pictorial allPictorial, int position) {
		int count = allPictorial.piclist.size();
		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(
					R.layout.listitem_second, null);
		}
		RelativeLayout rl1 = (RelativeLayout) view
				.findViewById(R.id.view_rl1);
		RelativeLayout rl2 = (RelativeLayout) view
				.findViewById(R.id.view_rl2);

		ImageView iv1 = (ImageView) view.findViewById(R.id.listitem_icon1);
		ImageView iv2 = (ImageView) view.findViewById(R.id.listitem_icon2);

		TextView tv1 = (TextView) view.findViewById(R.id.listitem_jiage1);
		TextView tv2 = (TextView) view.findViewById(R.id.listitem_jiage2);
		
		TextView title1 = (TextView)view.findViewById(R.id.listitem_title1);
		TextView title2 = (TextView)view.findViewById(R.id.listitem_title2);

		for (int i = 0; i < count; i++) {
			if (count == 1) {
				rl1.setVisibility(View.VISIBLE);
				rl2.setVisibility(view.INVISIBLE);
			} else {
				rl1.setVisibility(View.VISIBLE);
				rl2.setVisibility(view.VISIBLE);
			}
			final Listitem p = (Listitem) allPictorial.piclist.get(i);
			switch (i) {
			case 0:
//				tv1.setText(Double.parseDouble(p.other) + "");
				tv1.setText(p.other);
				title1.setText(p.title);
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
						// TODO Auto-generated method stub
						Intent intent = new Intent();
						intent.setClass(mContext, GoodsArticleActivity.class);
						intent.putExtra("item", p);
						mContext.startActivity(intent);
					}
				});
				break;
			case 1:
				tv2.setText(p.other);
				title2.setText(p.title);
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
						// TODO Auto-generated method stub
						Intent intent = new Intent();
						intent.setClass(mContext, GoodsArticleActivity.class);
						intent.putExtra("item", p);
						mContext.startActivity(intent);
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
		// TODO Auto-generated method stub
		return true;
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
				if (obj.has("diges")) {
					o.shangjia = obj.getString("diges");
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
				if(obj.has("preference")){
					o.other = obj.getString("preference");
				}
				if(obj.has("typeId")){
					o.other1 = obj.getString("typeId");
				}
				o.list_type = "11";
				String str = type.replace("RecomandProductListFragment", "").trim();
				if(str!=null&&!"".equals(str)){
					o.show_type = "2";
				}else{
				o.show_type="1";
				}
			} catch (Exception e) {
			}
			o.getMark();
			data.list.add(o);
		}
		if(data.list.size()==0){
			if(mData!=null){
				if(mData.list.size()>0){
					mData.list.clear();
				}
			}
		}	
	
		Data d = data;
		if (null != d && null != d.list) {
			d.list = (ArrayList<Pictorial>) PictorialOperate.packing(d.list,
					size);
		}
		return d;
	}

}
