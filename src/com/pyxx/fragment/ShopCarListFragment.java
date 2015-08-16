package com.pyxx.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pyxx.app.ShareApplication;
import com.pyxx.basefragment.BaseFragment;
import com.pyxx.basefragment.LoadMoreListFragment;
import com.pyxx.dao.DBHelper;
import com.pyxx.datasource.DNDataSource;
import com.pyxx.entity.Data;
import com.pyxx.entity.Listitem;
import com.pyxx.exceptions.DataException;
import com.pyxx.loadimage.Utils;
import com.pyxx.part_activiy.ShopCarActivity;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.PerfHelper;

/**
 * 购物车 商品列表显示
 * 
 * @author wll
 */
public class ShopCarListFragment extends LoadMoreListFragment<Listitem> {

	private RelativeLayout main_rl;
	private LinearLayout erji_rl;
	private List<Integer> list = new ArrayList<Integer>();

	private Data allinfo = new Data();
	private List<Listitem> lis = new ArrayList<Listitem>();
	private boolean flag = false;
	public static ShopCarActivity sca;
	public static boolean all = false;
	private Double money = 0.0;
	private static String bianji = "";
	private String ids = "";

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
			String partType, String urltype, ShopCarActivity sca, boolean all) {
		final ShopCarListFragment fragment = new ShopCarListFragment();
		ShopCarListFragment.sca = sca;
		ShopCarListFragment.all = all;
		ShopCarListFragment.bianji = type;
		fragment.initType(type, partType, urltype);
		return fragment;
	}

	@Override
	public void findView() {
		super.findView();
		int w = (105 * PerfHelper.getIntData(PerfHelper.P_PHONE_W)) / 480;

		mIcon_Layout = new LinearLayout.LayoutParams(w,
				LayoutParams.WRAP_CONTENT);
		mIcon_Layout.rightMargin = (8 * PerfHelper
				.getIntData(PerfHelper.P_PHONE_W)) / 480;

		sca.method(ShopCarListFragment.this);

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
			view = LayoutInflater.from(mContext).inflate(
					R.layout.shop_car_list, null);
		}
		TextView title = (TextView) view.findViewById(R.id.shop_list_title);
		TextView jiage = (TextView) view.findViewById(R.id.shop_list_value);//商品价格
		TextView count = (TextView) view.findViewById(R.id.shop_list_count);
		final ImageView select_iv = (ImageView) view
				.findViewById(R.id.listitem_select);
		
		if (all) {
			sca.method1(money + "");
			sca.method3(lis);
			select_iv.setImageResource(R.drawable.selected2);
		} else {
			sca.method1("0.0");
			sca.method3(lis);
			select_iv.setImageResource(R.drawable.selected1);
		}

		Button del_btn = (Button) view.findViewById(R.id.delete_btn);
		// 当处于编辑时显示删除按钮
		if ("bianji".equals(bianji)) {
			del_btn.setVisibility(view.VISIBLE);
			del_btn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					ids = item.nid;
					//删除购物车商品
					new delShopsCarGoods().execute((Void) null);
				}
			});
		} else {
			del_btn.setVisibility(view.GONE);
		}

		// 选择购物车商品
		select_iv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				money = 0.0;
				if (all) {
					sca.method2();
					all = false;
				}
				Double totalvalue = 0.0;
				for (int i = 0; i < list.size(); i++) {
					if ((Integer) list.get(i) == position) {
						select_iv.setImageResource(R.drawable.selected1);
						list.remove(i);
						flag = true;
						break;
					}
				}
				if (!flag) {
					list.add(position);
					select_iv.setImageResource(R.drawable.selected2);
				} else {
					flag = false;
				}
				lis = new ArrayList<Listitem>();
				for (int j = 0; j < list.size(); j++) {
					Listitem li = (Listitem) allinfo.list.get((Integer) list
							.get(j));
					lis.add(li);
					money += Double.parseDouble(li.other)
							* (Integer.parseInt(li.fuwu));
				}
				sca.method1(money + "");
				sca.method3(lis);
			}
		});
		// if (DBHelper.getDBHelper().counts("readitem", .
		// "n_mark='" + item.n_mark + "' and read='true'") > 0
		// && !mOldtype.startsWith(DBHelper.FAV_FLAG)) {
		// title.setTextColor(mContext.getResources().getColor(R.color.readed));
		// } else {
		// title.setTextColor(Color.BLACK);
		// }
		title.setText(item.title);
		count.setText(item.fuwu);
		jiage.setText("￥" + item.other);
		
		ImageView icon = (ImageView) view.findViewById(R.id.shop_list_icon);
		if (item.icon != null && item.icon.length() > 10) {
			ShareApplication.mImageWorker.loadImage(item.icon, icon);
			icon.setVisibility(View.VISIBLE);
		} else {
			icon.setImageResource(R.drawable.list_qst);
			icon.setVisibility(View.VISIBLE);
		}
		return view;
	}

	public void method() {
		Double total = 0.0;
		for (int m = 0; m < allinfo.list.size(); m++) {
			Listitem li = (Listitem) allinfo.list.get(m);
			total += Double.parseDouble(li.other) * (Integer.parseInt(li.fuwu));
		}
		sca.method1(total + "");
	}

	@Override
	public void reFlush() {
		super.reFlush();
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
					AlertDialog ad = new AlertDialog.Builder(getActivity())
							.setMessage("您确认要删除本条记录吗？")
							.setPositiveButton("确定", new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									mlistAdapter.datas.remove(arg2
											- mListview.getHeaderViewsCount());
									mlistAdapter.notifyDataSetChanged();
									DBHelper.getDBHelper().delete(
											"listgoodscar", "nid=?",
											new String[] { li.nid });
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
	 * 删除购物车的 商品
	 * 
	 * @author wll
	 */
	class delShopsCarGoods extends
			AsyncTask<Void, Void, HashMap<String, Object>> {

		/**
		 * 删除购物车的 商品列表
		 */
		public delShopsCarGoods() {
		}

		@Override
		protected HashMap<String, Object> doInBackground(Void... params) {

			List<NameValuePair> param = new ArrayList<NameValuePair>();
			String json;

			String js = ShareApplication.share.getResources().getString(
					R.string.url_del_shopscargoods);
			// 加入集合
			HashMap<String, Object> mhashmap = new HashMap<String, Object>();
			try {
				json = DNDataSource.list_FromNET(js + "shop.userId="
						+ PerfHelper.getStringData(PerfHelper.P_USERID)
						+ "&shop.ids=" + ids, param);
				Data date = parseJson(json);
				mhashmap.put("responseCode", date.obj1);
			} catch (Exception e) {
				e.printStackTrace();
				mhashmap = null;
			}
			return mhashmap;
		}

		// @SuppressWarnings("unchecked")
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
				Toast.makeText(mContext, "删除失败", Toast.LENGTH_SHORT).show();
			} else if ("1".equals(result.get("responseCode"))) {
				Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
				int cou = Integer.parseInt(PerfHelper
						.getStringData(PerfHelper.P_SHARE_SEX)) - 1;
				PerfHelper.setInfo(PerfHelper.P_SHARE_SEX, "" + cou);
				sca.method4();
			} else {
				Toast.makeText(mContext, "删除失败", Toast.LENGTH_SHORT).show();
			}
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
				if (obj.has("name")) {
					o.title = obj.getString("name");
				}
				if (obj.has("logo")) {
					o.icon = obj.getString("logo");
				}
				if (obj.has("addTime")) {
					o.u_date = obj.getString("addTime");
				}
				if (obj.has("brief")) {
					o.shangjia = obj.getString("brief");
				}
				if (obj.has("price")) {
					o.other = obj.getString("price");
				}
				if (obj.has("num")) {
					o.fuwu = obj.getString("num");
				}
			} catch (Exception e) {
			}
			o.getMark();
			data.list.add(o);
		}
		if (data.list.size() == 0) {
			if (mData != null) {
				if (mData.list.size() > 0) {
					mData.list.clear();
				}
			}
		}
		int carcount = data.list.size();
		PerfHelper.setInfo(PerfHelper.P_SHARE_SEX, "" + carcount);
		allinfo = data;
		if (all) {
			list.clear();
			lis.clear();
			money = 0.0;
			for (int i = 0; i < data.list.size(); i++) {
				Listitem li = (Listitem) allinfo.list.get(i);
				list.add(i);
				lis.add(li);
				money += Double.parseDouble(li.other)
						* (Integer.parseInt(li.fuwu));
			}
		}
		return data;
	}

}
