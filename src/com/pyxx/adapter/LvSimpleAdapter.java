package com.pyxx.adapter;

import java.util.List;
import java.util.Map;

import com.pyxx.zhongguofuzhuangxiemao.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

public class LvSimpleAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	public ProgressBar pb;
	public ImageView iv;
	Bitmap bitmap;
	private List<Map<String, Object>> list;
	private int layoutID;
	private String from[];
	private int to[];

	public LvSimpleAdapter(Context context, List<Map<String, Object>> list,
			int layoutID, String flag[], int ItemIDs[], String name) {
		this.mInflater = LayoutInflater.from(context);
		this.list = list;
		this.layoutID = layoutID;
		this.from = flag;
		this.to = ItemIDs;
	}

	public int getCount() {
		return list.size();
	}

	public Object getItem(int arg0) {
		return 0;
	}

	public long getItemId(int arg0) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = mInflater.inflate(layoutID, null);
		for (int i = 0; i < from.length; i++) {
			if (convertView.findViewById(to[i]) instanceof ImageView) {
				ImageView iv = (ImageView) convertView.findViewById(to[i]);
				if ("true".equals(list.get(position).get(from[i]))) {
					iv.setBackgroundResource(R.drawable.listview_02);
				} else {
					iv.setBackgroundResource(R.drawable.listview_01);
				}

			} else if (convertView.findViewById(to[i]) instanceof TextView) {
				TextView tv = (TextView) convertView.findViewById(to[i]);
				tv.setText((String) list.get(position).get(from[i]));
			} else if (convertView.findViewById(to[i]) instanceof RatingBar) {
				RatingBar rb = (RatingBar) convertView.findViewById(to[i]);
				String str = list.get(position).get(from[i]).toString();
				rb.setRating(Float.parseFloat(str));
			}
		}
		return convertView;
	}
}