package com.pyxx.part_activiy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pyxx.zhongguofuzhuangxiemao.R;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class DetailListAdapter extends BaseExpandableListAdapter {

	private LayoutInflater layoutInflater;
	private Context mContext;
	private List<Map<String, Object>> parentList = new ArrayList<Map<String, Object>>();
	private List<List<Map<String, Object>>> childList = new ArrayList<List<Map<String, Object>>>();
	private Map<String, Object> mapAddress = new HashMap<String, Object>();
	private boolean flag = false;
	private Handler mhandler;

	public DetailListAdapter(Context mContext,
			List<Map<String, Object>> parentList,
			List<List<Map<String, Object>>> childList,
			Map<String, Object> mapAddress, Handler mhandler) {
		this.mContext = mContext;
		this.parentList = parentList;
		this.childList = childList;
		this.mapAddress = mapAddress;
		this.layoutInflater = LayoutInflater.from(mContext);
		this.mhandler = mhandler;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return childList.get(groupPosition).get(childPosition).get("Title")
				.toString();
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = layoutInflater.inflate(
					R.layout.detail_grade_list_child, null);
		}
		convertView.setBackgroundResource(R.drawable.list_selector);
		final ImageView head = (ImageView) convertView
				.findViewById(R.id.detail_xlcxlayoutimageview);
		head.setImageResource(Integer.valueOf(childList.get(groupPosition)
				.get(childPosition).get("Head").toString()));
		final TextView title = (TextView) convertView
				.findViewById(R.id.detail_layout_xlcxtextview);
		title.setText(childList.get(groupPosition).get(childPosition)
				.get("Title").toString());
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mhandler.obtainMessage(childPosition).sendToTarget();
			}
		});
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return childList.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return parentList.get(groupPosition).get("List").toString();
	}

	@Override
	public int getGroupCount() {
		return parentList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	private ImageView imgView;

	@Override
	public View getGroupView(final int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = layoutInflater.inflate(
					R.layout.detail_grade_list_parent, null);
		}
		convertView.setBackgroundResource(R.drawable.list_selector);

		// 利用boolean flag设置右侧图片是否向下，默认向下；
		if (flag) {

			imgView = (ImageView) convertView
					.findViewById(R.id.detail_layout_jiantou);
			imgView.setBackgroundResource(R.drawable.jiantou2);
			flag = false;
		} else {
			imgView = (ImageView) convertView
					.findViewById(R.id.detail_layout_jiantou);
			imgView.setBackgroundResource(R.drawable.jiantou);

			flag = true;
		}

		final TextView list = (TextView) convertView
				.findViewById(R.id.detail_layout_dztextview);

		list.setText(parentList.get(groupPosition).get("List").toString());
		return convertView;
	}

	@Override
	public boolean hasStableIds() {

		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
