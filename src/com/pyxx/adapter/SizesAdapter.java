package com.pyxx.adapter;

import java.util.List;

import com.pyxx.entity.Listitem;
import com.pyxx.zhongguofuzhuangxiemao.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SizesAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<Listitem> listitems;
	private int selected = -1;

	public SizesAdapter(Context context, List<Listitem> listitems) {
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
			holder = new Holder();
			convertView = inflater.inflate(R.layout.horizontal_size_layout,
					null);
			holder.nameView = (TextView) convertView
					.findViewById(R.id.list_name);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		Listitem item = listitems.get(position);
		holder.nameView.setText(item.title);
		if (position == selected) {
			convertView.setSelected(true);
		} else {
			convertView.setSelected(false);
		}

		return convertView;
	}

	class Holder {
		TextView nameView;
	}

	public void setSelectIndex(int i) {
		selected = i;
	}

}
