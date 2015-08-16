package com.pyxx.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
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
import com.pyxx.datasource.DNDataSource;
import com.pyxx.entity.Data;
import com.pyxx.entity.Listitem;
import com.pyxx.loadimage.Utils;
import com.pyxx.part_activiy.ZixunArticleActivity;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.FinalVariable;
import com.utils.PerfHelper;

/**
 * 服装资讯 界面显示
 * 
 * @author wll
 */
public class ZxxxListFragment extends LoadMoreListFragment<Listitem> {

	private String part_name;
	public Data data2 = null;
	// LinearLayout linearLayout_gongqiu;

	RelativeLayout rl_quyu, rl_meishi, rl_main;
	LinearLayout rl_erji;

	TextView main_text;
	public List ls;
	public Listitem it;
	private RadioGroup head_group = null;
	private TextView head_title = null;
	private int nowCount = 0;
	private Gallery gallery = null;
	private String[] mps = null;
	private boolean flag = true;
	private Thread td = null;
	public static int COMPLETED = 1;

	/**
	 * fragment创建
	 * 
	 * @param type
	 *            当前列表SA类型
	 * @param partType
	 *            当前栏目大栏目类型
	 * @return
	 */
	public static BaseFragment<Listitem> newInstance(String type,
			String partType, String url) {
		final ZxxxListFragment tf = new ZxxxListFragment();
		tf.initType(type, partType, url);
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
		new SelectHeadView().execute((Void) null);
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

		rl_main = (RelativeLayout) mMain_layout.findViewById(R.id.main_rl);
		rl_main.setVisibility(View.GONE);
		rl_erji = (LinearLayout) mMain_layout
				.findViewById(R.id.title_meishi_ll);
		rl_erji.setVisibility(View.GONE);
		gallery = (Gallery) mMain_layout.findViewById(R.id.show_head_img);
		head_title = (TextView) mMain_layout.findViewById(R.id.head_title);
		head_group = (RadioGroup) mMain_layout.findViewById(R.id.head_group);

	}

	@Override
	public void reFlush() {
		super.reFlush();
		flag = true;
	}

	/**
	 * 
	 */
	public void initShowImg() {
		if (data2 != null && flag) {
			if (data2.list.size() > 0) {
				flag = false;
				head_group.removeAllViews();
				System.out.println("*************************"
						+ data2.list.size());
				for (int c = 0; c < data2.list.size(); c++) {
					ImageView rb = new ImageView(mContext);
					// rb.setLayoutParams(new RadioGroup.LayoutParams(
					// 20, 20));
					rb.setPadding(10, 0, 0, 0);
					rb.setImageResource(R.drawable.yuandian_tb2);
					// rb.setPadding(20, 0, 0, 0);
					// RadioButton rb = new RadioButton(mContext);
					// rb.setMaxWidth(2);
					// rb.setMaxHeight(2);
					// rb.setPadding(1, 0, 0, 0);
					// rb.setButtonDrawable(R.drawable.yuandian_tb2);
					head_group.addView(rb);
				}

				gallery.setAdapter(new ImageAdapter(mContext));
				gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent, View v,
							int position, long id) {
						nowCount = position;
						for (int d = 0; d < data2.list.size(); d++) {
							if (d == position) {
								ImageView rbs = (ImageView) head_group
										.getChildAt(position);
								rbs.setImageResource(R.drawable.yuandian_tb1);
								// RadioButton rbs =
								// (RadioButton)head_group.getChildAt(position);
								// rbs.setButtonDrawable(R.drawable.yuandian_tb1);
							} else {
								// RadioButton rbs2 =
								// (RadioButton)head_group.getChildAt(d);
								// rbs2.setButtonDrawable(R.drawable.yuandian_tb2);
								ImageView rbs2 = (ImageView) head_group
										.getChildAt(d);
								rbs2.setImageResource(R.drawable.yuandian_tb2);
							}
						}
						Listitem im = (Listitem) data2.list.get(position);
						head_title.setText(im.title.toString());
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// 这里不做响应
					}
				});
				gallery.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						Intent intent = new Intent();
						intent.setClass(mContext, ZixunArticleActivity.class);
						intent.putExtra("item", (Listitem) data2.list.get(arg2));
						startActivity(intent);
					}
				});
				if (td == null) {
					td = new WorkThread();
					td.start();
				}
			}
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == COMPLETED) {
				if (data2 != null) {
					if (nowCount >= data2.list.size() - 1) {
						nowCount = 0;
					} else {
						nowCount = nowCount + 1;
					}
					gallery.setSelection(nowCount);
				}
			}
		}
	};

	// 工作线程
	private class WorkThread extends Thread {
		@Override
		public void run() {
			// ......处理比较耗时的操作
			// 处理完成后给handler发送消息
			while (true) {
				try {
					Thread.sleep(3000);
					Message msg = new Message();
					msg.what = COMPLETED;
					handler.sendMessage(msg);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 列表项目展示
	 */
	TextView[] tags = new TextView[5];
	ColorStateList csl_r;
	ColorStateList csl_n;
	String juli_no = "未知";

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
		// if(data2!=null&&item!=null){
		// if(data2.list.size()!=0){
		// rl_main.setVisibility(View.VISIBLE);
		// if(position==0){
		// System.out.println("###########################sssss");
		// initShowImg();
		// }
		// }else{
		// rl_main.setVisibility(View.GONE);
		// }
		// }else{
		// rl_main.setVisibility(View.GONE);
		// }

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
		JSONArray jsonay2 = jsonobj.getJSONArray("lists");

		int count2 = jsonay2.length();

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
				if (obj.has("source")) {
					o.other3 = obj.getString("source");
				}
				if (obj.has("imgs")) {
					o.img_list_1 = obj.getString("imgs");
				}
				o.list_type = "14";
				o.show_type = "4";
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
		return data;
	}

	@Override
	public void onResume() {
		super.onResume();

		new SelectHeadView().execute((Void) null);

	}

	@Override
	public void onRefresh() {
		super.onRefresh();
		new SelectHeadView().execute((Void) null);
	}

	class SelectHeadView extends AsyncTask<Void, Void, HashMap<String, Object>> {

		public SelectHeadView() {
		}

		@Override
		protected HashMap<String, Object> doInBackground(Void... params) {

			List<NameValuePair> param = new ArrayList<NameValuePair>();
			String json;
			// 加入集合
			HashMap<String, Object> mhashmap = new HashMap<String, Object>();
			try {
				String jsurl = ShareApplication.share.getResources().getString(
						R.string.url_sel_zixun)
						+ "info.menuId=12";

				json = DNDataSource.list_FromNET(jsurl, param);

				Data date = parseJson(json);
				mhashmap.put("responseCode", date.obj1);

			} catch (Exception e) {
				e.printStackTrace();
				mhashmap = null;
			}
			return mhashmap;
		}

		@SuppressWarnings("unchecked")
		public Data parseJson(String json) throws Exception {
			Data data = new Data();
			data2 = new Data();
			JSONObject jsonobj = new JSONObject(json);
			data.obj1 = jsonobj.getString("code");
			try {
				JSONArray jsonay = jsonobj.getJSONArray("heads");

				int count = jsonay.length();
				mps = new String[count];
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
							mps[i] = obj.getString("logo");
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
						if (obj.has("source")) {
							o.other3 = obj.getString("source");
						}
						if (obj.has("imgs")) {
							o.img_list_1 = obj.getString("imgs");
						}
						o.list_type = "14";
						o.show_type = "4";
					} catch (Exception e) {
					}
					o.getMark();
					data2.list.add(o);
				}
			} catch (Exception e) {
				e.printStackTrace();
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

			if ("1".equals(result.get("responseCode"))) {
				if (data2 != null) {
					if (data2.list.size() != 0) {
						rl_main.setVisibility(View.VISIBLE);
						initShowImg();
					} else {
						rl_main.setVisibility(View.GONE);
					}
				} else {
					rl_main.setVisibility(View.GONE);
				}
			} else {

			}
		}
	}

	private class ImageAdapter extends BaseAdapter {
		private Context mContext;

		public ImageAdapter(Context context) {
			mContext = context;
		}

		public int getCount() {
			return mps.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView image = new ImageView(mContext);
			if (mps[position] != null && mps[position].length() > 10) {
				ShareApplication.mImageWorker.loadImage(mps[position], image);
				image.setVisibility(View.VISIBLE);
			} else {
				image.setImageResource(R.drawable.list_qst);
				image.setVisibility(View.VISIBLE);
			}
			// image.setAdjustViewBounds(true);
			image.setScaleType(ImageView.ScaleType.FIT_XY);
			image.setLayoutParams(new Gallery.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			return image;
		}
	}

}
