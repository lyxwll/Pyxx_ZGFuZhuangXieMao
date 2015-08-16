package com.pyxx.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pyxx.app.ShareApplication;
import com.pyxx.basefragment.BaseFragment;
import com.pyxx.basefragment.LoadMoreListFragment;
import com.pyxx.dao.DBHelper;
import com.pyxx.datasource.DNDataSource;
import com.pyxx.entity.Data;
import com.pyxx.entity.Listitem;
import com.pyxx.exceptions.DataException;
import com.pyxx.loadimage.Utils;
import com.pyxx.part_activiy.SecondFzActivity;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.PerfHelper;

/**
 * 首页列表 Fragment
 * 
 * @author wll
 */
public class FirstListFragment extends LoadMoreListFragment<Listitem> {

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

	public int nowImg;

	private PopupWindow pop;
	ArrayList<Listitem> list_categorys, list_category;
	String[][] str_categorys, str_category;
	private String categorysId = "0", categoryId = "0";
	private ListView fz_list_left, fz_list_right;
	private int selected1 = -1;
	private ListAdapter adapter;
	private ListAdapter2 adapter2;
	private WindowManager windowManager;
	private Display display;
	private String info = "";

	private EditText keyword_ed;// 请输入关键字
	private TextView search_btn;// 搜索按钮

	public static BaseFragment<Listitem> newInstance(String type,
			String partType, String urltype) {

		final FirstListFragment fragment = new FirstListFragment();
		fragment.initType(type, partType, urltype);

		return fragment;
	}

	@Override
	public void findView() {
		super.findView();

		int width = (105 * PerfHelper.getIntData(PerfHelper.P_PHONE_W)) / 480;
		mIcon_Layout = new LinearLayout.LayoutParams(width,
				LayoutParams.WRAP_CONTENT);
		mIcon_Layout.rightMargin = (8 * PerfHelper
				.getIntData(PerfHelper.P_PHONE_W)) / 480;

		main_rl = (RelativeLayout) mMain_layout.findViewById(R.id.main_rl);
		erji_rl = (LinearLayout) mMain_layout
				.findViewById(R.id.title_meishi_ll);
		main_rl.setVisibility(View.GONE);
		erji_rl.setVisibility(View.GONE);

		windowManager = getActivity().getWindowManager();
		display = windowManager.getDefaultDisplay();
		
		LayoutInflater inflater = LayoutInflater
				.from(mMain_layout.getContext());
		// 加载PopupWindow的布局
		View vi = inflater.inflate(R.layout.fz_list, null);
		pop = new PopupWindow(vi, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT, false);
		fz_list_left = (ListView) vi.findViewById(R.id.fz_list_left);
		fz_list_right = (ListView) vi.findViewById(R.id.fz_list_right);
		keyword_ed = (EditText) vi.findViewById(R.id.keyword_ed);
		search_btn = (TextView) vi.findViewById(R.id.search_btn);

		// 需要设置一下此参数，点击外边可消失
		pop.setBackgroundDrawable(new BitmapDrawable());
		// 设置此参数获得焦点，否则无法点击
		pop.setFocusable(true);
		// 设置触摸窗口就消失
		pop.setTouchable(true);
		// //设置点击窗口外边窗口消失
		// pop.setOutsideTouchable(true);

		search_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String keyword = keyword_ed.getText().toString().trim();
				if (keyword == null || keyword.equals("")) {
					Utils.showToast("搜索关键字不能为空");
				} else {
					Intent intent = new Intent();
					intent.setClass(mContext, SecondFzActivity.class);
					intent.putExtra("categorysId", categorysId);
					intent.putExtra("categoryId", categoryId);
					intent.putExtra("keyword", keyword);
					startActivity(intent);
				}
			}
		});
	}

	/**
	 * 列表项目展示
	 */
	@Override
	public View getListItemview(View view, final Listitem item, int position) {
		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(
					R.layout.listitem_first, null);
		}
		TextView title = (TextView) view.findViewById(R.id.listitem_title);
		TextView des = (TextView) view.findViewById(R.id.listitem_category);
		title.setText(item.title);
		des.setText(item.des);

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
		selected1 = position - 1;
		categorysId = item.nid;

		new SelectFz().execute((Void) null);

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

	class SelectFz extends AsyncTask<Void, Void, HashMap<String, Object>> {

		public SelectFz() {
		}

		@Override
		protected HashMap<String, Object> doInBackground(Void... params) {

			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("type.level", "1"));
			param.add(new BasicNameValuePair("type.type", "1"));
			String json;

			String js = ShareApplication.share.getResources().getString(
					R.string.url_sel_type);
			// 加入集合
			HashMap<String, Object> mhashmap = new HashMap<String, Object>();
			try {
				json = DNDataSource.list_FromNET(js, param);
				if (ShareApplication.debug) {
					// System.out.println("用户登录返回:" + json);

				}
				Data date = parseJson(json);
				mhashmap.put("responseCode", date.obj1);
				mhashmap.put("result", date.list);
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
					data.obj1 = jsonobj.getString("code");// 返回状态
					// 0，有数据成功返回，-1，-2，无数据返回，1，返回异常
					return data;
				} else {
					data.obj1 = jsonobj.getString("code");// 返回状态
				}
			}
			JSONArray jsonay = jsonobj.getJSONArray("lists");
			int count = jsonay.length();
			for (int i = 0; i < count; i++) {
				Listitem o = new Listitem();
				JSONObject obj = jsonay.getJSONObject(i);
				o.nid = obj.getString("id");
				try {
					if (obj.has("name")) {// 名称
						o.title = obj.getString("name");
					}
					if (obj.has("logo")) {// 图标
						o.icon = obj.getString("logo");
					}
					if (obj.has("subtypes")) {// 子类型
						o.des = obj.getString("subtypes");
					}
				} catch (Exception e) {
				}
				o.getMark();
				// o.list_type = obj.getString("type");
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
				return;
			}
			if (result == null) {
				Toast.makeText(mContext, "请求失败，请稍后再试", Toast.LENGTH_SHORT)
						.show();
			} else if ("1".equals(result.get("responseCode"))) {
				list_categorys = (ArrayList<Listitem>) result.get("result");

				adapter = new ListAdapter(mContext, list_categorys);
				fz_list_left.setAdapter(adapter);
				if (pop.isShowing()) {
					// 隐藏窗口，如果设置了点击窗口外消失即不需要此方式隐藏
					pop.dismiss();
				} else {
					// 显示窗口
					pop.showAsDropDown(erji_rl);
				}

			} else {
				Toast.makeText(mContext, "查询失败", Toast.LENGTH_SHORT).show();
			}
		}
	}

	class SelectFz2 extends AsyncTask<Void, Void, HashMap<String, Object>> {
		private String pid;

		public SelectFz2(String pid) {
			this.pid = pid;
		}

		@Override
		protected HashMap<String, Object> doInBackground(Void... params) {

			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("type.level", "2"));
			param.add(new BasicNameValuePair("type.type", "1"));
			param.add(new BasicNameValuePair("type.pid", pid));
			String json;

			String js = ShareApplication.share.getResources().getString(
					R.string.url_sel_type);
			// 加入集合
			HashMap<String, Object> mhashmap = new HashMap<String, Object>();
			try {
				json = DNDataSource.list_FromNET(js, param);
				if (ShareApplication.debug) {
					// System.out.println("用户登录返回:" + json);

				}
				Data date = parseJson(json);
				mhashmap.put("responseCode", date.obj1);
				mhashmap.put("result", date.list);
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
					data.obj1 = jsonobj.getString("code");// 返回状态
					// 0，有数据成功返回，-1，-2，无数据返回，1，返回异常
					return data;
				} else {
					data.obj1 = jsonobj.getString("code");// 返回状态
				}
			}
			JSONArray jsonay = jsonobj.getJSONArray("lists");
			int count = jsonay.length();
			for (int i = 0; i < count; i++) {
				Listitem o = new Listitem();
				JSONObject obj = jsonay.getJSONObject(i);
				o.nid = obj.getString("id");
				try {
					if (obj.has("name")) {// 排序
						o.title = obj.getString("name");
					}
					if (obj.has("logo")) {// 排序
						o.icon = obj.getString("logo");
					}
				} catch (Exception e) {
				}
				o.getMark();
				// o.list_type = obj.getString("type");
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
				return;
			}
			if (result == null) {
				Toast.makeText(mContext, "请求失败，请稍后再试", Toast.LENGTH_SHORT)
						.show();
			} else if ("1".equals(result.get("responseCode"))) {
				list_category = (ArrayList<Listitem>) result.get("result");

				adapter2 = new ListAdapter2(mContext, list_category);
				fz_list_right.setAdapter(adapter2);

			} else {
				Toast.makeText(mContext, "查询失败", Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * ListViewAdapter
	 * 
	 * @author sy
	 * 
	 */
	private class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<Listitem> lists;

		public ListAdapter(Context context, List<Listitem> lists) {

			this.inflater = LayoutInflater.from(context);
			this.lists = lists;
		}

		@Override
		public int getCount() {
			return lists.size();
		}

		@Override
		public Object getItem(int position) {

			return lists.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.fz_list_item, null);
				holder = new ViewHolder();
				holder.fzName = (TextView) convertView.findViewById(R.id.name);
				holder.iv = (ImageView) convertView
						.findViewById(R.id.selected_iv);
				holder.fzItem = (LinearLayout) convertView
						.findViewById(R.id.ll_fz_list_item);
				holder.fzItem.setLayoutParams(new RelativeLayout.LayoutParams(
						display.getWidth() / 2, display.getHeight() / 9));

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			Listitem li = lists.get(position);
			holder.iv.setVisibility(View.GONE);
			if (selected1 == position) {
				holder.iv.setVisibility(View.VISIBLE);

				new SelectFz2(li.nid).execute((Void) null);
			}

			holder.fzName.setText(li.title);
			holder.fzItem
					.setOnClickListener(new TextViewListener(position, li));

			return convertView;
		}

		private class ViewHolder {
			TextView fzName;
			ImageView iv;
			LinearLayout fzItem;
		}

		private class TextViewListener implements View.OnClickListener {
			int position;
			Listitem item;

			public TextViewListener(int position, Listitem item) {
				this.position = position;
				this.item = item;
			}

			public void onClick(View v) {
				categorysId = item.nid;
				selected1 = position;
				adapter.notifyDataSetChanged();

				new SelectFz2(item.nid).execute((Void) null);
			}
		}
	}

	/**
	 * ListViewAdapter
	 * 
	 * @author sy
	 * 
	 */
	private class ListAdapter2 extends BaseAdapter {
		private LayoutInflater inflater;
		private List<Listitem> lists;

		public ListAdapter2(Context context, List<Listitem> lists) {

			this.inflater = LayoutInflater.from(context);
			this.lists = lists;
		}

		@Override
		public int getCount() {
			return lists.size();
		}

		@Override
		public Object getItem(int position) {
			return lists.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.fz_list_item2, null);
				holder = new ViewHolder();
				holder.fzName = (TextView) convertView.findViewById(R.id.name);
				holder.fzItem = (LinearLayout) convertView
						.findViewById(R.id.ll_fz_list_item);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			Listitem li = lists.get(position);

			holder.fzName.setText(li.title);
			holder.fzItem
					.setOnClickListener(new TextViewListener(position, li));

			return convertView;
		}

		private class ViewHolder {
			TextView fzName;
			LinearLayout fzItem;
		}

		private class TextViewListener implements View.OnClickListener {
			int position;
			Listitem item;

			public TextViewListener(int position, Listitem item) {
				this.position = position;
				this.item = item;
			}

			public void onClick(View v) {
				categoryId = item.nid;
				if (pop.isShowing()) {
					// 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏
					pop.dismiss();
				} else {
					// 显示窗口
					pop.showAsDropDown(erji_rl);
				}
				Intent intent = new Intent();
				intent.setClass(mContext, SecondFzActivity.class);
				intent.putExtra("categorysId", categorysId);
				intent.putExtra("categoryId", categoryId);
				intent.putExtra("keyword", item.title);
				startActivity(intent);
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
		info = jsonay.toString();
		int count = jsonay.length();
		for (int i = 0; i < count; i++) {
			Listitem o = new Listitem();
			JSONObject obj = jsonay.getJSONObject(i);
			o.nid = obj.getString("id");
			o.title = obj.getString("name");
			o.icon = obj.getString("logo");

			if (obj.has("subtypes")) {// 排序
				o.des = obj.getString("subtypes");
			}
			o.getMark();
			data.list.add(o);
		}
		return data;
	}

}
