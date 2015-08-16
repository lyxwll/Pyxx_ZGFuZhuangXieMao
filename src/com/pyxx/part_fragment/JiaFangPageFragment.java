package com.pyxx.part_fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.pyxx.app.ShareApplication;
import com.pyxx.datasource.DNDataSource;
import com.pyxx.entity.Data;
import com.pyxx.entity.Listitem;
import com.pyxx.exceptions.DataException;
import com.pyxx.loadimage.Utils;
import com.pyxx.part_activiy.GoodsArticleActivity;
import com.pyxx.view.GalleryUtils;
import com.pyxx.zhongguofuzhuangxiemao.R;

@SuppressLint("ValidFragment")
public class JiaFangPageFragment extends Fragment implements OnClickListener {

	public View mMain_layout;// 布局显示
	public RelativeLayout mContainers, zhiwei_rl, hangye_rl, city_rl;
	public Context mContext;
	FragmentTransaction fragmentTransaction = null;
	int sre;
	private boolean flag = true;
	private LinearLayout main_ll;

	public JiaFangPageFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// if (savedInstanceState != null)
		// sre = savedInstanceState.getInt("icon_url");
		mContext = inflater.getContext();

		if (mMain_layout == null) {
			mContainers = new RelativeLayout(mContext);
			// mMain_layout = inflater.inflate(R.layout.goods_jfyp, null);
			initListFragment(inflater);
			mContainers.addView(mMain_layout);
		} else {
			mContainers.removeAllViews();
			mContainers = new RelativeLayout(getActivity());
			mContainers.addView(mMain_layout);
		}
		return mContainers;
	}

	public void initListFragment(LayoutInflater inflater) {
		init();
	}

	public void things(View view) {

	}

	public void init() {
		/*
		 * mMain_layout.findViewById(R.id.btn_1).setOnClickListener(this);
		 * mMain_layout.findViewById(R.id.btn_2).setOnClickListener(this);
		 * mMain_layout.findViewById(R.id.btn_3).setOnClickListener(this);
		 * mMain_layout.findViewById(R.id.btn_4).setOnClickListener(this);
		 * mMain_layout.findViewById(R.id.btn_5).setOnClickListener(this);
		 * mMain_layout.findViewById(R.id.btn_6).setOnClickListener(this);
		 * mMain_layout.findViewById(R.id.btn_7).setOnClickListener(this);
		 * mMain_layout.findViewById(R.id.btn_8).setOnClickListener(this);
		 * 
		 * main_ll = (LinearLayout)mMain_layout.findViewById(R.id.main_ll1);
		 */
		new SelectHeadProduct().execute((Void) null);
	}

	Intent intent;

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		/*
		 * case R.id.btn_1: intent = new Intent(mContext,JiaFangActivity.class);
		 * intent.putExtra("statue", "1"); startActivity(intent); break; case
		 * R.id.btn_2: intent = new Intent(mContext,JiaFangActivity.class);
		 * intent.putExtra("statue", "2"); startActivity(intent); break; case
		 * R.id.btn_3: intent = new Intent(mContext,JiaFangActivity.class);
		 * intent.putExtra("statue", "3"); startActivity(intent); break; case
		 * R.id.btn_4: intent = new Intent(mContext,JiaFangActivity.class);
		 * intent.putExtra("statue", "4"); startActivity(intent); break; case
		 * R.id.btn_5: intent = new Intent(mContext,JiaFangActivity.class);
		 * intent.putExtra("statue", "5"); startActivity(intent); break; case
		 * R.id.btn_6: intent = new Intent(mContext,JiaFangActivity.class);
		 * intent.putExtra("statue", "6"); startActivity(intent); break; case
		 * R.id.btn_7: intent = new Intent(mContext,JiaFangActivity.class);
		 * intent.putExtra("statue", "7"); startActivity(intent); break; case
		 * R.id.btn_8: intent = new Intent(mContext,JiaFangActivity.class);
		 * intent.putExtra("statue", "8"); startActivity(intent); break;
		 */
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	class SelectHeadProduct extends
			AsyncTask<Void, Void, HashMap<String, Object>> {

		public SelectHeadProduct() {
		}

		@Override
		protected HashMap<String, Object> doInBackground(Void... params) {

			List<NameValuePair> param = new ArrayList<NameValuePair>();
			String json;
			// 加入集合
			HashMap<String, Object> mhashmap = new HashMap<String, Object>();
			try {
				String jsurl = ShareApplication.share.getResources().getString(
						R.string.url_sel_product)
						+ "commodity.menuId=11";

				json = DNDataSource.list_FromNET(jsurl, param);

				if (ShareApplication.debug) {
					System.out.println("注册商家返回:" + json);
				}
				Data date = parseJson(json);
				mhashmap.put("responseCode", date.obj1);
				mhashmap.put("list", date.list);

			} catch (Exception e) {
				e.printStackTrace();
				mhashmap = null;
			}
			return mhashmap;
		}

		@SuppressWarnings("unchecked")
		public Data parseJson(String json) throws Exception {
			Data data = new Data();
			JSONObject jsonobj = new JSONObject(json);
			if (jsonobj.has("code")) {
				int code = jsonobj.getInt("code");
				if (code != 1) {
					throw new DataException("数据获取异常");
				}
				data.obj1 = jsonobj.getString("code");
			}
			JSONArray jsonay = jsonobj.getJSONArray("heads");
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
					if (obj.has("price")) {
						o.other = obj.getString("price");
					}
					if (obj.has("typeId")) {
						o.other1 = obj.getString("typeId");
					}
					o.list_type = "11";
					o.show_type = "3";
				} catch (Exception e) {
				}
				o.getMark();
				data.list.add(o);
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
				main_ll.setVisibility(View.GONE);
				return;
			}

			if ("1".equals(result.get("responseCode"))) {
				if (result.get("list") != null) {
					main_ll.setVisibility(View.VISIBLE);
					new GalleryUtils(mContext,
							(List<Listitem>) result.get("list"), flag,
							GoodsArticleActivity.class, main_ll, true)
							.initShowImg();
				} else {
					main_ll.setVisibility(View.GONE);
				}
			} else {
				main_ll.setVisibility(View.GONE);
			}
		}

	}

}
