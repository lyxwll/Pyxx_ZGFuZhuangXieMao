package com.pyxx.fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.PerfHelper;

/**
 * 查看清单 列表显示
 * 
 * @author wll
 */
public class PaymentListFragment extends LoadMoreListFragment<Listitem> {

	private RelativeLayout main_rl;
	private LinearLayout erji_rl;
	private Data allinfo = new Data();
	private static String info = "";

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
			String partType, String urltype, String info) {
		final PaymentListFragment tf = new PaymentListFragment();
		tf.initType(type, partType, urltype);
		PaymentListFragment.info = info;
		return tf;
	}

	@Override
	public void findView() {
		super.findView();
		int w = (105 * PerfHelper.getIntData(PerfHelper.P_PHONE_W)) / 480;
		// showpop();
		mIcon_Layout = new LinearLayout.LayoutParams(w,
				LayoutParams.WRAP_CONTENT);
		mIcon_Layout.rightMargin = (8 * PerfHelper
				.getIntData(PerfHelper.P_PHONE_W)) / 480;
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
			view = LayoutInflater.from(mContext).inflate(R.layout.payment_list,
					null);
		}
		TextView title = (TextView) view.findViewById(R.id.listitem_title);
		TextView jiage = (TextView) view.findViewById(R.id.listitem_value);
		TextView count = (TextView) view.findViewById(R.id.listitem_count);
		view.findViewById(R.id.listitem_select);

		title.setText(item.title);
		count.setText("数量：" + item.other);
		jiage.setText("单价:￥" + item.other1);

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
					new AlertDialog.Builder(getActivity())
							.setMessage("您确认要删除本条记录吗？")
							.setPositiveButton("确定", new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									mlistAdapter.datas.remove(arg2
											- mListview.getHeaderViewsCount());
									mlistAdapter.notifyDataSetChanged();
									DBHelper.getDBHelper().delete(
											"listgoodscar", "c_id=" + li.c_id,
											null);
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
		if (info == null || "".equals(info)) {

		} else {
			JSONArray arry = new JSONArray(info);
			for (int i = 0; i < arry.length(); i++) {

				JSONObject jso = arry.getJSONObject(i);
				Listitem item = new Listitem();
				item.icon = jso.getString("logo");
				item.nid = jso.getString("id");
				item.title = jso.getString("name");
				item.other = jso.getString("num");
				item.other1 = jso.getString("price");
				item.getMark();
				data.list.add(item);
			}
		}
		allinfo = data;
		return data;
	}
}
