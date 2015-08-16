package com.pyxx.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pyxx.app.ShareApplication;
import com.pyxx.datasource.DNDataSource;
import com.pyxx.entity.Data;
import com.pyxx.entity.Listitem;
import com.pyxx.fragment.FirstListFragment;
import com.pyxx.fragment.HuoDongListFragment;
import com.pyxx.fragment.ShopsListFragment;
import com.pyxx.fragment.ZxxxListFragment;
import com.pyxx.loadimage.Utils;
import com.pyxx.view.CustomAlertDialog;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.PerfHelper;

/**
 * 主界面布局
 * 
 * @author HeJian
 * 
 */
public class FragmentChangeActivity extends FragmentActivity {

	private Fragment Center_mContent;
	private Fragment oldmContent;
	private int oldmContent_pos;
	public ImageView m_MoveView[];
	public ImageView m_OldMoveView;
	public ImageButton m_Oldclick;
	private Fragment frag = null;

	private RelativeLayout title_item;
	private TextView cityButton;
	TextView title;
	private PopupWindow popup;
	TextView cityText;
	private ListView listView;
	private ListAdapter adapter;
	ArrayList<Listitem> list_category;
	private String categorysId = "0";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null)
			Center_mContent = getSupportFragmentManager().getFragment(
					savedInstanceState, "mContent");
		if (Center_mContent == null)
			Center_mContent = FirstListFragment.newInstance(
					"FirstListFragment", "FirstListFragment",
					getString(R.string.url_sel_type)
							+ "type.type=1&type.level=1");

		setContentView(R.layout.content_frame);

		/** 加载中间Frag */
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.main_content, Center_mContent).commit();

		m_MoveView = initMoveView(5);

		cityButton = (TextView) findViewById(R.id.shops_city);// 商家栏-城市选择按钮
		((ImageView) findViewById(R.id.title_back)).setVisibility(View.GONE);
		title = (TextView) findViewById(R.id.title_title);
		title_item = (RelativeLayout) findViewById(R.id.title_item);
		title.setText("首页");
		changePart(findViewById(R.id.m_part_1));

		LayoutInflater inflater = LayoutInflater
				.from(FragmentChangeActivity.this);
		View view = inflater.inflate(R.layout.shops_city_list, null);
		popup = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT, false);
		cityText = (TextView) view.findViewById(R.id.city_name);
		listView = (ListView) view.findViewById(R.id.shop_city_list);
		popup.setBackgroundDrawable(new BitmapDrawable());
		popup.setFocusable(true);
		popup.setTouchable(true);

	}

	public ImageView[] initMoveView(int sum) {
		ImageView[] m_MoveView = new ImageView[sum];
		return m_MoveView;
	}

	public void changePart(final View current_view) {
		if (m_Oldclick != null) {
			if (m_Oldclick.getId() == current_view.getId()) {
				return;
			}
		}
		if (m_Oldclick != null) {
			m_Oldclick.setBackgroundDrawable(null);
			switch (m_Oldclick.getId()) {
			case R.id.m_part_1:
				((ImageView) m_Oldclick)
						.setBackgroundResource(R.drawable.main_1_tb1);
				break;
			case R.id.m_part_2:
				((ImageView) m_Oldclick)
						.setBackgroundResource(R.drawable.main_2_tb1);
				break;
			case R.id.m_part_3:
				((ImageView) m_Oldclick)
						.setBackgroundResource(R.drawable.main_3_tb1);
				break;
			case R.id.m_part_4:
				((ImageView) m_Oldclick)
						.setBackgroundResource(R.drawable.main_4_tb1);
				break;
			case R.id.m_part_5:
				((ImageView) m_Oldclick)
						.setBackgroundResource(R.drawable.main_5_tb1);
				break;
			}
		}
		if (current_view.getId() == R.id.m_part_1) {
			startSlip(current_view, m_MoveView[0], 0);
		} else if (current_view.getId() == R.id.m_part_2) {
			startSlip(current_view, m_MoveView[1], 1);
		} else if (current_view.getId() == R.id.m_part_3) {
			startSlip(current_view, m_MoveView[2], 2);
		} else if (current_view.getId() == R.id.m_part_4) {
			startSlip(current_view, m_MoveView[3], 3);
		} else if (current_view.getId() == R.id.m_part_5) {
			startSlip(current_view, m_MoveView[4], 4);
		}
		m_Oldclick = (ImageButton) current_view;
	}

	public void things(View view) {

	}

	public void noClick(View view) {// 防止点击下拉刷新出的时间报错

	}

	/**
	 * 导行条滑动执行
	 * 
	 * @param current_View
	 */
	public void startSlip(final View current_View, final View current_MoveView,
			int postion) {
		update_view(current_View, postion);
	}

	Fragment findresult = null;

	public void update_view(final View view, final int position) {
		Utils.h.postDelayed(new Runnable() {
			@Override
			public void run() {

				FragmentTransaction fragmentTransaction = getSupportFragmentManager()
						.beginTransaction();
				findresult = getSupportFragmentManager().findFragmentByTag(
						view.getId() + "");

				switch (view.getId()) {
				case R.id.m_part_1:
					title.setText("首页");
					cityButton.setVisibility(View.GONE);
					((ImageButton) view)
							.setBackgroundResource(R.drawable.main_1_tb2);

					if (frag != null) {
						fragmentTransaction.detach(frag);
					}
					if (findresult != null) {
						fragmentTransaction.attach(findresult);
						frag = findresult;
						fragmentTransaction.commitAllowingStateLoss();
						return;
					}
					frag = FirstListFragment.newInstance("FirstListFragment",
							"FirstListFragment",
							getString(R.string.url_sel_type)
									+ "type.type=1&type.level=1");
					break;
				case R.id.m_part_2:
					title.setText("附近服装");
					cityButton.setVisibility(View.GONE);
					((ImageButton) view)
							.setBackgroundResource(R.drawable.main_2_tb2);
					if (frag != null) {
						fragmentTransaction.detach(frag);
					}
					if (findresult != null) {
						fragmentTransaction.attach(findresult);
						frag = findresult;
						fragmentTransaction.commitAllowingStateLoss();
						return;
					}
					frag = HuoDongListFragment
							.newInstance(
									"HuoDongListFragment",
									"HuoDongListFragment",
									getString(R.string.url_sel_product)
											+ "&commodity.menuId=12&commodity.lat="
											+ PerfHelper
													.getStringData(PerfHelper.P_GPS_LATI)
											+ "&commodity.lng="
											+ PerfHelper
													.getStringData(PerfHelper.P_GPS_LONG)
											+ "&sort=4");
					break;
				case R.id.m_part_3:
					title.setText("服装商家");
					cityButton.setVisibility(View.VISIBLE);
					cityButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {

							new SelectCity().execute((Void) null);

						}
					});

					((ImageButton) view)
							.setBackgroundResource(R.drawable.main_3_tb2);
					if (frag != null) {
						fragmentTransaction.detach(frag);
					}
					if (findresult != null) {
						fragmentTransaction.attach(findresult);
						frag = findresult;
						fragmentTransaction.commitAllowingStateLoss();
						return;
					}
					frag = ShopsListFragment.newInstance("ShopsListFragment",
							"ShopsListFragment",
							getString(R.string.url_sel_shops)
									+ "seller.menuId=11");
					break;
				case R.id.m_part_4:
					title.setText("服装资讯");
					cityButton.setVisibility(View.GONE);
					((ImageButton) view)
							.setBackgroundResource(R.drawable.main_4_tb2);
					if (frag != null) {
						fragmentTransaction.detach(frag);
					}
					if (findresult != null) {
						fragmentTransaction.attach(findresult);
						frag = findresult;
						fragmentTransaction.commitAllowingStateLoss();
						return;
					}
					frag = ZxxxListFragment.newInstance("ZxxxListFragment",
							"ZxxxListFragment",
							getString(R.string.url_sel_zixun)
									+ "info.menuId=14");
					break;
				case R.id.m_part_5:
					title.setText("更多");
					cityButton.setVisibility(View.GONE);
					((ImageButton) view)
							.setBackgroundResource(R.drawable.main_5_tb2);
					if (frag != null) {
						fragmentTransaction.detach(frag);
					}
					if (findresult != null) {
						fragmentTransaction.attach(findresult);
						frag = findresult;
						fragmentTransaction.commitAllowingStateLoss();
						return;
					}
					frag = new SettingActivty();
					break;
				}
				fragmentTransaction.add(R.id.main_content, frag, view.getId()
						+ "");
				fragmentTransaction.commitAllowingStateLoss();
			}
		}, 200);
	}

	/**
	 * 选择城市筛选
	 */
	class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<Listitem> listitems;
		private int selected1 = -1;

		public ListAdapter(Context context, List<Listitem> listitems) {
			this.inflater = LayoutInflater.from(context);
			this.listitems = listitems;
		}

		@Override
		public int getCount() {
			return listitems.size();
		}

		@Override
		public Object getItem(int position) {
			return listitems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder;
			if (convertView == null) {
				convertView = inflater
						.inflate(R.layout.shop_city_adapter, null);
				holder = new Holder();
				holder.cityName = (TextView) convertView
						.findViewById(R.id.city_name);
				holder.select = (ImageView) convertView
						.findViewById(R.id.choice_img);
				holder.cityItem = (RelativeLayout) convertView
						.findViewById(R.id.city_item);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}

			Listitem item = listitems.get(position);
			holder.select.setVisibility(View.GONE);
			if (selected1 == position) {
				holder.select.setVisibility(View.VISIBLE);
				holder.cityName.getResources()
						.getColor(R.color.main_head_color);
			}
			holder.cityName.setText(item.title);
			holder.cityItem.setOnClickListener(new TextViewListener(position,
					item));

			return convertView;
		}

		class Holder {
			TextView cityName;
			ImageView select;
			RelativeLayout cityItem;
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
				cityText.setText(item.title);
				cityButton.setText(item.title);
				selected1 = position;
				adapter.notifyDataSetChanged();
				if (popup.isShowing()) {
					// 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏
					popup.dismiss();
				} else {
					// 显示窗口
					popup.showAsDropDown(title);
				}

				if (frag == null)
					frag = ShopsListFragment.newInstance("ShopsListFragment",
							"ShopsListFragment",
							getString(R.string.url_sel_shops)
									+ "seller.menuId=11" + "&seller.cityId="
									+ categorysId);
				/** 加载中间Frag */
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.main_content, frag).commit();

			}
		}
	}

	class SelectCity extends AsyncTask<Void, Void, HashMap<String, Object>> {

		public SelectCity() {

		}

		@Override
		protected HashMap<String, Object> doInBackground(Void... params) {

			List<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("region.level", "1"));
			String json;

			String js = ShareApplication.share.getResources().getString(
					R.string.url_sel_city);
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
					if (obj.has("subtypes")) {// 子类型
						o.des = obj.getString("subtypes");
					}
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
			if (!Utils.isNetworkAvailable(FragmentChangeActivity.this)) {
				Utils.showToast(R.string.network_error);
				return;
			}
			if (result == null) {
				Toast.makeText(FragmentChangeActivity.this, "请求失败，请稍后再试",
						Toast.LENGTH_SHORT).show();
			} else if ("1".equals(result.get("responseCode"))) {
				list_category = (ArrayList<Listitem>) result.get("result");

				adapter = new ListAdapter(FragmentChangeActivity.this,
						list_category);
				listView.setAdapter(adapter);
				if (popup.isShowing()) {
					// 隐藏窗口，如果设置了点击窗口外消失即不需要此方式隐藏
					popup.dismiss();
				} else {
					// 显示窗口
					popup.showAsDropDown(title);
				}
			} else {
				Toast.makeText(FragmentChangeActivity.this, "查询省份失败",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent",
				Center_mContent);
	}

	int oldposition = 0;

	public void switchContent(final Fragment fragment, final int position) {
		Center_mContent = fragment;
		if (oldposition != position) {

			Handler h = new Handler();
			h.postDelayed(new Runnable() {
				public void run() {
					FragmentTransaction ft = getSupportFragmentManager()
							.beginTransaction();
					oldposition = position;
					ft.replace(R.id.main_content, fragment).commit();
					switch (position) {

					case 1:
						title.setText(R.string.main_1_title);
						invalidateOptionsMenu();
						break;
					case 2:
						title.setText(R.string.main_2_title);
						invalidateOptionsMenu();
						break;
					case 3:
						title.setText(R.string.main_3_title);
						invalidateOptionsMenu();
						break;
					case 4:
						title.setText(R.string.main_4_title);
						invalidateOptionsMenu();
						break;
					case 5:
						title.setText(R.string.main_5_title);
						invalidateOptionsMenu();
						break;
					case 6:
						title.setText(R.string.titlte_search);
						invalidateOptionsMenu();
						break;
					}
				}
			}, 300);
		}
	}

	/**
	 * 收起软键盘并设置提示文字
	 */
	public void collapseSoftInputMethod() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (oldposition == 6) {
				switchContent(oldmContent, oldmContent_pos);
				return false;
			}
			new CustomAlertDialog.Builder(this)
					.setTitle(R.string.exit)
					.setIcon(R.drawable.dialog_icon)
					.setMessage(
							getString(
									R.string.exit_message,
									new String[] { getString(R.string.app_name) }))
					.setPositiveButton(R.string.done,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									FragmentChangeActivity.this.finish();
								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
								}
							}).show();
			return false;
		}

		return super.onKeyDown(keyCode, event);
	}
}
