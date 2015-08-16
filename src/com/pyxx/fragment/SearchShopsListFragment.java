package com.pyxx.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pyxx.baseview.SecondScrollView;
import com.pyxx.dao.DBHelper;
import com.pyxx.entity.part;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.PerfHelper;

public class SearchShopsListFragment extends Fragment {
	String parttype;
	private static final String KEY_CONTENT = "FJPartFragment:parttype";

	// fragment创建
	public static SearchShopsListFragment newInstance(String type) {

		final SearchShopsListFragment tf = new SearchShopsListFragment();
		tf.init(type);

		return tf;
	}

	public void init(String type) {
		parttype = type;
	}

	public SearchShopsListFragment() {

	}

	boolean isfirst = false;

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (!isfirst) {
			main_view.onFinishInflate();
			isfirst = true;
		}
		super.onViewCreated(view, savedInstanceState);
	}

	LinearLayout containers;
	FavoritSecondViews main_view;
	private View mview;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup view,
			Bundle savedInstanceState) {
		if (parttype == null && (savedInstanceState != null)
				&& savedInstanceState.containsKey(KEY_CONTENT)) {
			parttype = savedInstanceState.getString(KEY_CONTENT);
		}
		if (main_view == null) {
			main_view = new FavoritSecondViews((FragmentActivity) getActivity());
			containers = new LinearLayout(getActivity());
			containers.addView(main_view);
		} else {
			if (containers != null) {
				containers.removeAllViews();
			}
			containers = new LinearLayout(getActivity());
			containers.addView(main_view);
			// changePart(second_pasue);
		}

		return containers;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_CONTENT, parttype);
	}

	public void setSecondGone(String part) {
		// TODO Auto-generated method stub
		if (part.equals("all_city")) {
			main_view.findViewById(R.id.second_layout).setVisibility(
					View.VISIBLE);
		} else {
			main_view.findViewById(R.id.second_layout).setVisibility(View.GONE);
		}

	}

	public class FavoritSecondViews extends SecondScrollView {

		public FavoritSecondViews(Context context) {
			super(context);
		}

		@Override
		public void initSecondPart() {
			// TODO Auto-generated method stub
			boolean isfirst = true;
			parts = getParts();
			second_items = (LinearLayout) secondscroll
					.findViewById(R.id.second_items);
			second_move_items = (LinearLayout) secondscroll
					.findViewById(R.id.move_items);
			second_move_items.removeAllViews();
			second_items.removeAllViews();
			this.removeAllViews();
			int count = parts.size();
			content_id = Math.abs(parts.get(0).part_type.hashCode());
			content.setId(content_id);
			addLayout();
			all_frag = new Fragment[count];
			for (int i = 0; i < count; i++) {
				final View v = getItem(i, parts.get(i));
				v.setTag(parts.get(i).part_sa + "#" + i);
				if (isfirst) {
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							changePart(v);
						}
					}, 500);
					mview = v;
					isfirst = false;
				}
				second_items.addView(v);
				v.setOnClickListener(this);
				if (hasAnimation) {
					v.measure(init_w, init_h);
					int width = v.getMeasuredWidth();
					int height = v.getMeasuredHeight();
					init_layoutparam = getInit_Secondlayoutparam(width,
							height + 10);
					View v_Move = getItem_move(i, parts.get(i));
					v_Move.setLayoutParams(init_layoutparam);
					v_Move.setTag(i + "");
					v_Move.setVisibility(View.INVISIBLE);
					second_move_items.addView(v_Move);
				}
			}
			old_move_item = second_move_items.findViewWithTag(0 + "");
			if (old_move_item != null) {
				old_move_item.setVisibility(View.VISIBLE);
			}
			// 添加滑动背景
			if (second_canscroll) {
				final GestureDetector mGestureDetector = new GestureDetector(// 手势控制事件
						new SimpleOnGestureListener() {
							@Override
							public boolean onScroll(MotionEvent e1,
									MotionEvent e2, float distanceX,
									float distanceY) {
								if (second_items.getWidth() == (second_content
										.getScrollX() + second_content
										.getWidth())) {
									second_right.setVisibility(View.INVISIBLE);
								} else {
									if (0 == second_content.getScrollX()) {
										second_left
												.setVisibility(View.INVISIBLE);
									} else {
										second_left.setVisibility(View.VISIBLE);
										second_right
												.setVisibility(View.VISIBLE);
									}
								}
								return super.onScroll(e1, e2, distanceX,
										distanceY);
							}
						});

				second_content.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View arg0, MotionEvent arg1) {
						mGestureDetector.onTouchEvent(arg1);
						return false;
					}
				});
			}

			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					// setSecondGone(parttype);
				}
			}, 500);
			
		}

		@Override
		public void onFinishInflate() {// 滚动设置及动画设置
			setsecond_HasAnimation(true);
			setsecond_Canscroll(true);
			super.onFinishInflate();
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			super.onClick(v);
			mview = v;

		}

		public View getItem(int index, part info) {
			TextView vi = (TextView) LayoutInflater.from(this.getContext())
					.inflate(R.layout.second_text, null);
			vi.setText(info.part_name);
			vi.setPadding(14, 7, 14, 5);
			return vi;
		}

		@Override
		public View getItem_move(int index, part info) {
			ImageView iv = new ImageView(this.getContext());
			iv.setBackgroundResource(R.drawable.move_bg);
			return iv;
		}

		/**
		 * 改变选择效果
		 * 
		 * @param current
		 */
		public void changeStyle(View current) {
			((TextView) current).setTextColor(getResources().getColor(
					android.R.color.white));
			if (old_item != null)
				((TextView) old_item).setTextColor(getResources().getColor(
						R.color.white));
			if (old_item != null && current.getTag().equals(old_item.getTag())) {
				return;
			}
		}

		public List<part> getParts() {
			List<part> parts = new ArrayList<part>();
			try {
				parts = DBHelper.getDBHelper().select("part_list", part.class,
						"part_type='" + parttype + "'", 0, 100);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("parts:" + parts.size());
			return parts;
		}

		@Override
		public Fragment initFragment(int index, String fragmentinfo) {
			    super.initFragment(index, fragmentinfo);
				String typeId = parts.get(index).part_sa;
				String typeName = parts.get(index).part_name;
				String str = getString(R.string.url_sel_shops)+"&lat="+PerfHelper.getStringData(PerfHelper.P_GPS_LATI)+"&lng="+PerfHelper.getStringData(PerfHelper.P_GPS_LONG)+"&areaId="+typeId;
				return 	ShopsListFragment.newInstance(typeId, typeName,
						getString(R.string.url_sel_shops)+"&lat="+PerfHelper.getStringData(PerfHelper.P_GPS_LATI)+"&lng="+PerfHelper.getStringData(PerfHelper.P_GPS_LONG)+"&areaId="+typeId);		
		}

	}

	String nowcity = PerfHelper.getStringData(PerfHelper.P_CITY);

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (main_view != null
				&& nowcity.equals(PerfHelper.getStringData(PerfHelper.P_CITY))
				&& mview != null) {
			main_view.changePart(mview);
			((TextView) mview).setTextColor(getResources().getColor(
					android.R.color.white));
		}

	}
}
