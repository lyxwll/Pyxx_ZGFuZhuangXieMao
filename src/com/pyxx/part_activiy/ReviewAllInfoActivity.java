package com.pyxx.part_activiy;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pyxx.entity.BaseFragmentActivity;
import com.pyxx.zhongguofuzhuangxiemao.R;

/**
 * 查看所有评论
 * 
 * @author wll
 */
public class ReviewAllInfoActivity extends BaseFragmentActivity {

	private ListAdapter adapter;
	String allInfo = null;
	private ListView reviewList;
	private TextView bt_text, count_text;
	private ImageView return_text;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.review_list);

		if (getIntent().getExtras() != null) {
			allInfo = (String) getIntent().getExtras().get("allInfo");
		}
		JSONArray array = null;
		try {
			array = new JSONArray(allInfo);
		} catch (Exception e) {
			System.out.println(e);
		}
		bt_text = (TextView) findViewById(R.id.title_title);
		bt_text.setText("评论");
		count_text = (TextView) findViewById(R.id.title_btn_right);
		count_text.setVisibility(View.VISIBLE);
		count_text.setText(array.length() + "条");
		return_text = (ImageView) findViewById(R.id.title_back);
		return_text.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		reviewList = (ListView) findViewById(R.id.review_list);

		adapter = new ListAdapter(this, array);
		reviewList.setAdapter(adapter);
	}

	/**
	 * ListViewAdapter
	 * 
	 * @author sy
	 * 
	 */
	private class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private JSONArray list;

		public ListAdapter(Context context, JSONArray list) {

			this.inflater = LayoutInflater.from(context);
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.length();
		}

		@Override
		public Object getItem(int position) {
			Object obj = null;
			try {
				obj = list.getJSONObject(position);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return obj;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.review_list_item, null);
				holder = new ViewHolder();
				holder.userName = (TextView) convertView
						.findViewById(R.id.plnr_tv);
				holder.content = (TextView) convertView
						.findViewById(R.id.plnr_text_tv);
				holder.date = (TextView) convertView
						.findViewById(R.id.date_text);
				holder.xx = (ImageView) convertView.findViewById(R.id.xx_v1);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			try {
				holder.userName.setText(list.getJSONObject(position).getString(
						"nick")
						+ " :");
				holder.content.setText(list.getJSONObject(position).getString(
						"content"));
				holder.date.setText(list.getJSONObject(position).getString(
						"addTime"));
				int lenxx = getCount() - 1;
				// if(position== lenxx){
				// holder.xx.setVisibility(View.GONE);
				// }
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return convertView;
		}

		private class ViewHolder {
			TextView userName;
			TextView content;
			TextView date;
			ImageView xx;
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	public void things(View view) {
	}

	public interface resume_up {
		void up();
	}

	resume_up upone;

	public void setUp(resume_up ups) {
		upone = ups;
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
}
