package com.pyxx.fragment;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pyxx.app.ShareApplication;
import com.pyxx.basefragment.LoadMoreListFragment;
import com.pyxx.dao.Urls;
import com.pyxx.entity.Data;
import com.pyxx.entity.Listitem;
import com.pyxx.entity.Pictorial;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.PerfHelper;

/**
 * 注：由于列表每个项目中有两条信息，所以需要重新设置图片列表长度。在获取数据对将count值进行加倍
 * 
 * @author lxx
 * 
 */
public class PicListFragment extends LoadMoreListFragment<Pictorial> {
	Drawable defaultimage;

	private int size = 2;
	AbsListView.LayoutParams list_param;
	LinearLayout.LayoutParams lin_param;
	LinearLayout.LayoutParams fra_param;

	// fragment创建
	public static PicListFragment newInstance(String type, String parttype,String urltype) {
		final PicListFragment tf = new PicListFragment();
		tf.initType(type, parttype,urltype);
		return tf;
	}

	public PicListFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup containers,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, containers, savedInstanceState);
	}

	@Override
	public void findView() {
		super.findView();
		// TODO Auto-generated method stub
		// defaultimage = resource.getDrawable(R.drawable.list_item_default);
		// setSecond(false);//设置是否显示二级栏目
		int itemtotalheight = 254 * PerfHelper.getIntData(PerfHelper.P_PHONE_W) / 720;
		list_param = new AbsListView.LayoutParams(
				PerfHelper.getIntData(PerfHelper.P_PHONE_W) / size,
				itemtotalheight);
		int lin_param_w = 371 * PerfHelper.getIntData(PerfHelper.P_PHONE_W) / 720;
		int lin_param_h = 354 * PerfHelper.getIntData(PerfHelper.P_PHONE_W) / 720;
		lin_param = new LinearLayout.LayoutParams(lin_param_w, lin_param_h);
		int fra_param_w = 275 * PerfHelper.getIntData(PerfHelper.P_PHONE_W) / 720;
		int fra_param_h = 282 * PerfHelper.getIntData(PerfHelper.P_PHONE_W) / 720;
		fra_param = new LinearLayout.LayoutParams(fra_param_w, fra_param_h);
		fra_param.gravity = Gravity.CENTER_HORIZONTAL;
		this.mLength = 8;//
		mFooter_limit = 8;
	}

	View listhead;

	@Override
	public View getListHeadview(Pictorial item) {
		return null;
	}

	@Override
	public boolean dealClick(Pictorial item, int position) {
		// TODO Auto-generated method stub
		return true;
	}

	static class Viewitem {
		ImageView image;
		TextView text;
	}

	static class hold {
		public Viewitem[] vs;
	}

	@Override
	public View getListItemview(View itemView, Pictorial allPictorial,
			int position) {
		int count = allPictorial.piclist.size();
		hold h = null;
		if (itemView != null) {
			h = (hold) itemView.getTag();
		}
		if (h == null || h.vs.length != size || count != size) {
			itemView = new LinearLayout(mContext);
			h = new hold();
			h.vs = new Viewitem[count];
			for (int i = 0; i < count; i++) {
				Viewitem vi = new Viewitem();
				View item = LayoutInflater.from(mContext).inflate(
						R.layout.listitem_conveniencelist, null);
				item.setLayoutParams(list_param);
				// item.findViewById(R.id.listitem_pic_icon_bg).setLayoutParams(
				// lin_param);
				vi.image = (ImageView) item.findViewById(R.id.listitem_icon);
				vi.image.setLayoutParams(fra_param);
				vi.text = (TextView) item.findViewById(R.id.listitem_title);
				h.vs[i] = vi;
				vi.image.setTag(allPictorial.piclist.get(i));
				((LinearLayout) itemView).addView(item);
				vi.image.setOnClickListener(clickListener);
			}
			itemView.setTag(h);
		}
		for (int i = 0; i < count; i++) {
			Listitem p = allPictorial.piclist.get(i);
			Viewitem iv = h.vs[i];
			iv.text.setText(p.title);
			iv.image.setTag(allPictorial.piclist.get(i));
			ShareApplication.mImageWorker.loadImage(Urls.main + p.icon,
					iv.image);
		}
		return itemView;
	}

	public void jump(Listitem item) {
		Intent intent = new Intent();
		intent.putExtra("item", item);
		startActivity(intent);
	}

	OnClickListener clickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Listitem p1 = (Listitem) v.getTag();
			jump(p1);
		}
	};

	@Override
	public Data getDataFromDB(String oldtype, int page, int count,
			String parttype) throws Exception {
		// TODO Auto-generated method stub\
		Data data = super.getDataFromDB(oldtype, page, count, parttype);
		data.list = (ArrayList<Pictorial>) PictorialOperate.packing(data.list,
				size);
		return data;
	}

	@Override
	public Data getDataFromNet(String url, String oldtype, int page, int count,
			boolean isfirst, String parttype) throws Exception {
		// TODO Auto-generated method stub
		Data data = super.getDataFromNet(url, oldtype, page, count, isfirst,
				parttype);
		data.list = (ArrayList<Pictorial>) PictorialOperate.packing(data.list,
				size);
		return data;
	}
}
