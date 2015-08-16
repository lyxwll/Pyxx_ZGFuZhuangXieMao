package com.pyxx.part_fragment;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pyxx.app.ShareApplication;
import com.pyxx.dao.DBHelper;
import com.pyxx.datasource.DNDataSource;
import com.pyxx.entity.Data;
import com.pyxx.entity.Listitem;
import com.pyxx.exceptions.DataException;
import com.pyxx.fragment.SearchZgspShopsListFragment;
import com.pyxx.loadimage.Utils;
import com.pyxx.part_activiy.AddAddrActivity;
import com.pyxx.part_activiy.AddOrderActivity;
import com.pyxx.part_activiy.GoodsArticleActivity;
import com.pyxx.part_activiy.JiaFangActivity;
import com.pyxx.part_activiy.MyCollectActivity;
import com.pyxx.part_activiy.OrderActivity;
import com.pyxx.part_activiy.PaymentActivity;
import com.pyxx.part_activiy.ShowBigImgActivity;
import com.pyxx.setting.FeedBack;
import com.pyxx.setting.Version;
import com.pyxx.view.GalleryUtils;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.FileUtils;
import com.utils.FinalVariable;
import com.utils.PerfHelper;

@SuppressLint("ValidFragment")
public class FirstPageFragment extends Fragment implements OnClickListener {

	public View mMain_layout;// 布局显示
	public RelativeLayout mContainers, zhiwei_rl, hangye_rl, city_rl;
	public Context mContext;
	FragmentTransaction fragmentTransaction = null;
	int sre;
	private boolean flag = true;
	private LinearLayout main_ll;

	public FirstPageFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// if (savedInstanceState != null)
		// sre = savedInstanceState.getInt("icon_url");
		mContext = inflater.getContext();

		if (mMain_layout == null) {
			mContainers = new RelativeLayout(mContext);
			mMain_layout = inflater.inflate(R.layout.first_page, null);
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
		/*mMain_layout.findViewById(R.id.btn_1).setOnClickListener(this);
		mMain_layout.findViewById(R.id.btn_2).setOnClickListener(this);
		mMain_layout.findViewById(R.id.btn_3).setOnClickListener(this);
		mMain_layout.findViewById(R.id.btn_4).setOnClickListener(this);
		mMain_layout.findViewById(R.id.btn_5).setOnClickListener(this);
		mMain_layout.findViewById(R.id.btn_6).setOnClickListener(this);
		mMain_layout.findViewById(R.id.btn_7).setOnClickListener(this);
		mMain_layout.findViewById(R.id.btn_8).setOnClickListener(this);

		main_ll = (LinearLayout) mMain_layout.findViewById(R.id.main_ll1);*/
		new SelectHeadProduct().execute((Void) null);
	}

	Intent intent;

	@Override
	public void onClick(View view) {
		switch (view.getId()) {

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
