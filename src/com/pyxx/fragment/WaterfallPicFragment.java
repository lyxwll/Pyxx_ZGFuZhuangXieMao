package com.pyxx.fragment;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pyxx.app.ShareApplication;
import com.pyxx.basefragment.BaseFragment;
import com.pyxx.dao.DBHelper;
import com.pyxx.dao.Urls;
import com.pyxx.datasource.DNDataSource;
import com.pyxx.entity.Data;
import com.pyxx.entity.Listitem;
import com.pyxx.exceptions.DataException;
import com.pyxx.loadimage.Utils;
import com.pyxx.view.OptionsContent;
import com.pyxx.view.OptionsContent.OptionsListener;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.FinalVariable;
import com.utils.PerfHelper;

public class WaterfallPicFragment extends BaseFragment<Listitem> implements
		OptionsListener {

	public RelativeLayout mMain_layout;// 布局显示
	public LinearLayout mContainers;
	public FrameLayout.LayoutParams itemparam;
	View mLoading;

	/**
	 * fragment创建
	 * 
	 * @param type
	 *            当前列表SA类型
	 * @param partTyper
	 *            当前栏目大栏目类型
	 * @return
	 */
	public static WaterfallPicFragment newInstance(String type,
			String parttype, String urltype) {
		final WaterfallPicFragment tf = new WaterfallPicFragment();
		tf.initType(type, parttype, urltype);
		return tf;
	}

	/**
	 * fragment创建
	 * 
	 * @param mType
	 *            当前列表SA类型
	 * @param partTyper
	 *            当前栏目大栏目类型
	 * @return
	 */
	OptionsContent elasticScrollView;
	LinearLayout left;
	LinearLayout reight;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup vgroup,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, vgroup, savedInstanceState);
		mContext = inflater.getContext();
		if (mMain_layout == null) {
			mContainers = new LinearLayout(mContext);
			mMain_layout = new RelativeLayout(mContext);
			elasticScrollView = new OptionsContent(mContext);
			elasticScrollView.mListener = this;
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.MATCH_PARENT);
			mMain_layout.addView(elasticScrollView.getScrollView(), lp);
			// mMain_layout.setLayoutParams(lp);
			// LinearLayout content = elasticScrollView.getContentView();
			mLoading = inflater.inflate(R.layout.loading_layout, null);
			mMain_layout.addView(mLoading, lp);
			// content.setOrientation(LinearLayout.VERTICAL);
			// for (int i = 1; i <= 50; i++) {
			// TextView tempTextView = new TextView(mContext);
			// tempTextView.setText("Text:" + i);
			// tempTextView.setPadding(0, 20, 0, 0);
			// content.addView(tempTextView);
			// }
			initListFragment(inflater);
			mContainers.addView(mMain_layout);
		} else {
			mContainers.removeAllViews();
			mContainers = new LinearLayout(getActivity());
			mContainers.addView(mMain_layout);
		}
		mLength = 16;
		return mContainers;
	}

	public void initListFragment(LayoutInflater inflater) {
		left = new LinearLayout(mContext);
		reight = new LinearLayout(mContext);
		LinearLayout content = elasticScrollView.getContentView();
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		left.setOrientation(LinearLayout.VERTICAL);
		reight.setOrientation(LinearLayout.VERTICAL);
		left.setGravity(Gravity.CENTER_HORIZONTAL);
		reight.setGravity(Gravity.CENTER_HORIZONTAL);
		lp.weight = 1;
		int h = PerfHelper.getIntData(PerfHelper.P_PHONE_W) * 194 / 640;
		int w = PerfHelper.getIntData(PerfHelper.P_PHONE_W) * 280 / 640;

		itemparam = new FrameLayout.LayoutParams(w, h);
		itemparam1 = new LinearLayout.LayoutParams(w,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		content.addView(left, lp);
		content.addView(reight, lp);
		addListener();
		initData();
		csl_n = (ColorStateList) getResources().getColorStateList(
				R.color.list_item_title_n);
	}

	public LinearLayout.LayoutParams itemparam1;

	@Override
	public void update() {
		if (mData != null && mData.list.size() > 0) {
			if (mPage == 0) {
				left.removeAllViews();
				reight.removeAllViews();
			}
			int count = mData.list.size();
			for (int i = 0; i < count; i++) {
				if (i % 2 == 0) {
					left.addView(getListItemview(null,
							(Listitem) mData.list.get(i), i));
				} else {
					reight.addView(getListItemview(null,
							(Listitem) mData.list.get(i), i));
				}
			}
			if (mData.list.size() <= mLength - 1) {
				elasticScrollView.removeFooter();
			}
		} else {

		}

	}

	@Override
	public View getListHeadview(Listitem item) {
		return null;
	}

	TextView[] tags = new TextView[5];
	ColorStateList csl_n;

	@Override
	public View getListItemview(View view, Listitem item, int position) {
		View viewitem = LayoutInflater.from(mContext).inflate(
				R.layout.listitem_map_line, null);
		ImageView icon = (ImageView) viewitem.findViewById(R.id.listitem_icon);
		TextView title = (TextView) viewitem.findViewById(R.id.listitem_title);
		TextView des = (TextView) viewitem.findViewById(R.id.listitem_des);
		TextView date = (TextView) viewitem.findViewById(R.id.listitem_date);

		icon.setLayoutParams(itemparam);
		title.setText(item.title);

		String[] str = item.other2.split(",");
		int count = str.length;

		date.setText(item.u_date.split(" ")[0]);
		des.setText(item.des);
		if (item.des.equals("")) {
			des.setVisibility(View.GONE);
		} else {
			des.setVisibility(View.VISIBLE);
		}
		ShareApplication.mImageWorker.loadImage(Urls.main + item.icon, icon);
		viewitem.setOnTouchListener(ont);
		viewitem.setTag(item);
		viewitem.setLayoutParams(itemparam1);
		return viewitem;
	}

	android.view.View.OnClickListener oncilc = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.putExtra("parttpye", v.getTag().toString());
			startActivity(intent);
		}
	};

	View view;
	OnTouchListener ont = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			view = v;
			gd.onTouchEvent(event);
			return true;
		}
	};

	@SuppressWarnings("deprecation")
	GestureDetector gd = new GestureDetector(new SimpleOnGestureListener() {

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			Listitem item = (Listitem) view.getTag();
			Intent intent = new Intent();
			intent.putExtra("item", item);
			intent.putExtra("type", "2");
			intent.putExtra("items", (Serializable) mData.list);
			intent.putExtra("position", mData.list.indexOf(item));
			startActivity(intent);
			return true;
		}

	});

	@Override
	public void findView() {

	}

	@Override
	public void addListener() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				mLoading.setVisibility(View.GONE);
				switch (msg.what) {
				case FinalVariable.addfoot:// 添加footer
					// if (mListview != null && mList_footer != null) {
					// if (mListview.getFooterViewsCount() == 0) {
					// mListview.addFooterView(mList_footer);
					// }
					// }
					elasticScrollView.addFooter();
					break;
				case FinalVariable.nomore:
					mHandler.sendEmptyMessage(FinalVariable.remove_footer);
					if (msg.obj != null) {
						Utils.showToast(msg.obj.toString());
						return;
					}
					Utils.showToast("没有更多数据了");
					break;
				case FinalVariable.remove_footer:// footer删除
					// initfooter();
					elasticScrollView.removeFooter();
					break;
				case FinalVariable.error:// 网络连接出错
					if (!Utils.isNetworkAvailable(mContext)) {
						Utils.showToast(R.string.network_error);
						return;
					}
					if (msg.obj != null) {
						Utils.showToast(msg.obj.toString());
						return;
					}
					Utils.showToast(R.string.network_error);

					break;
				case FinalVariable.first_update:
					if (mlistAdapter != null && mlistAdapter.datas != null) {
						mlistAdapter.datas.clear();
						mlistAdapter = null;
					}
					update();
					break;
				case FinalVariable.update:// 数据加载完成界面更新
					update();
					break;

				}
			}

		};
	}

	@Override
	public void loadmore() {
		if (isloading) {
			return;
		}
		isloading = true;
		super.loadMore();
	}

	@Override
	public void reflashing() {
		if (isloading) {
			return;
		}
		reFlush();
	}

	@Override
	public Data getDataFromNet(String url, String oldtype, int page, int count,
			boolean isfirst, String parttype) throws Exception {
		if (oldtype.startsWith(DBHelper.FAV_FLAG)) {
			return DNDataSource.list_Fav(
					oldtype.replace(DBHelper.FAV_FLAG, ""), page, count);
		}
		url = Urls.app_api + "?action=listsp";
		String json = DNDataSource.list_FromNET(url, oldtype, page, count,
				parttype, isfirst);
		Data data = parseJson(json);
		if (data != null && data.list != null && data.list.size() > 0) {
			if (isfirst) {
				DBHelper.getDBHelper().delete("listinfo", "listtype=?",
						new String[] { oldtype });
			}
			DBHelper.getDBHelper().insert(oldtype + page, json, oldtype);
		}
		return data;
	}

	@Override
	public Data parseJson(String strs) throws Exception {
		System.out.println(strs);
		Data d = new Data();
		JSONObject jsonobj = new JSONObject(strs);
		if (jsonobj.has("code")) {
			int code = jsonobj.getInt("code");
			if (code == 0) {
				throw new DataException(jsonobj.getString("msg"));
			}
		}
		JSONArray list = jsonobj.getJSONArray("list");
		int count = list.length();
		for (int i = 0; i < count; i++) {
			JSONObject obj = list.getJSONObject(i);
			Listitem item = new Listitem();
			item.icon = obj.getString("icon");
			item.des = obj.getString("des");
			item.title = obj.getString("title");
			item.author = obj.getString("author");
			item.other = obj.getString("quote");
			item.other1 = obj.getString("thumb2");
			item.other2 = obj.getString("keyword");
			item.nid = obj.getString("id");
			item.sa = mOldtype;
			item.u_date = obj.getString("adddate");
			item.getMark();
			d.list.add(item);
		}
		return d;
	}

}
