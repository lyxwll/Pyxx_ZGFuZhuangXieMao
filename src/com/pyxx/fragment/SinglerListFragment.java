package com.pyxx.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pyxx.app.ShareApplication;
import com.pyxx.basefragment.BaseFragment;
import com.pyxx.basefragment.LoadMoreListFragment;
import com.pyxx.dao.DBHelper;
import com.pyxx.entity.Data;
import com.pyxx.entity.Listitem;
import com.pyxx.exceptions.DataException;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.PerfHelper;

public class SinglerListFragment extends LoadMoreListFragment<Listitem> {

	/**
	 * fragment创建
	 * 
	 * @param type
	 *            当前列表SA类型
	 * @param partTyper
	 *            当前栏目大栏目类型
	 * @return
	 */
	public static SinglerListFragment newInstance(String type, String parttype,String urltype) {
		final SinglerListFragment tf = new SinglerListFragment();
		tf.initType(type, parttype,urltype);
		return tf;
	}

	@Override
	public void findView() {
		// TODO Auto-generated method stub
		super.findView();
		int w = (105 * PerfHelper.getIntData(PerfHelper.P_PHONE_W)) / 480;
		mIcon_Layout = new LinearLayout.LayoutParams(w,
				LayoutParams.WRAP_CONTENT);
		mIcon_Layout.rightMargin = (8 * PerfHelper
				.getIntData(PerfHelper.P_PHONE_W)) / 480;
	}

	/**
	 * 列表项目展示
	 */
	@Override
	public View getListItemview(View view, Listitem item, int position) {
		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(
					R.layout.listitem_singlerlist, null);
		}
		TextView title = (TextView) view.findViewById(R.id.listitem_title);
		TextView date = (TextView) view.findViewById(R.id.listitem_date);
		TextView des = (TextView) view.findViewById(R.id.listitem_des);
		if (DBHelper.getDBHelper().counts("readitem",
				"n_mark='" + item.n_mark + "' and read='true'") > 0
				&& !mOldtype.startsWith(DBHelper.FAV_FLAG)) {
			title.setTextColor(mContext.getResources().getColor(R.color.readed));
		} else {
			title.setTextColor(Color.BLACK);
		}
		title.setText(item.title);
		date.setText(item.u_date);
		des.setText(item.des);
		ImageView icon = (ImageView) view.findViewById(R.id.listitem_icon);
		icon.setLayoutParams(mIcon_Layout);
		if (item.icon != null && item.icon.length() > 10) {
			ShareApplication.mImageWorker.loadImage(item.icon, icon);
			icon.setVisibility(View.VISIBLE);
		} else {
			icon.setImageDrawable(null);
			icon.setVisibility(View.GONE);
		}
		return view;
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
			if (code == 0) {
				throw new DataException(jsonobj.getString("msg"));
			}
		}
		JSONArray jsonay = jsonobj.getJSONArray("list");
		if (jsonobj.has("head")) {
			try {
				JSONArray ja = jsonobj.getJSONArray("head");
				if (ja.length() == 1) {
					JSONObject jsonhead = ja.getJSONObject(0);
					Listitem head1 = new Listitem();
					head1.nid = jsonhead.getString("id");
					head1.title = jsonhead.getString("title");
					head1.des = jsonhead.getString("des");
					head1.icon = jsonhead.getString("icon");
					head1.getMark();
					head1.ishead = "true";
					data.obj = head1;
					data.headtype = 0;
				} else {
					int count = ja.length();
					List<Listitem> li = new ArrayList<Listitem>();
					for (int i = 0; i < count; i++) {
						JSONObject jsonhead = ja.getJSONObject(i);
						Listitem head1 = new Listitem();
						head1.nid = jsonhead.getString("id");
						head1.title = jsonhead.getString("title");
						head1.des = jsonhead.getString("des");
						head1.icon = jsonhead.getString("icon");
						head1.ishead = "true";
						head1.getMark();
						li.add(head1);
					}
					data.obj = li;
					data.headtype = 2;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		int count = jsonay.length();
		for (int i = 0; i < count; i++) {
			Listitem o = new Listitem();
			JSONObject obj = jsonay.getJSONObject(i);
			o.nid = obj.getString("id");
			o.title = obj.getString("title");
			o.other1 = obj.getString("tag");
			try {
				if (obj.has("des")) {
					o.des = obj.getString("des");
				}
				if (obj.has("adddate")) {
					o.u_date = obj.getString("adddate");
				}
				o.icon = obj.getString("icon");
			} catch (Exception e) {
			}
			o.getMark();
			// o.list_type = obj.getString("type");
			data.list.add(o);
		}
		return data;
	}
}
