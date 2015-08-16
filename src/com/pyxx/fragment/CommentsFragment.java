package com.pyxx.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.pyxx.basefragment.LoadMoreListFragment;
import com.pyxx.dao.MySSLSocketFactory;
import com.pyxx.entity.Data;
import com.pyxx.entity.Items;
import com.pyxx.exceptions.DataException;
import com.pyxx.zhongguofuzhuangxiemao.R;

/**
 * 评论列表
 * 
 * @author HeJian
 * 
 */
public class CommentsFragment extends LoadMoreListFragment<Items> {
	public static final String ARTICLE_ID = "article_id";
	public String mArticlid;

	// fragment创建
	public static CommentsFragment newInstance(String type) {
		CommentsFragment tf = new CommentsFragment();
		tf.init(type);
		return tf;
	}

	public void init(String type) {
		mArticlid = type;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		outState.putString(ARTICLE_ID, mArticlid);
		super.onSaveInstanceState(outState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup vgroup,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (mArticlid == null && (savedInstanceState != null)
				&& savedInstanceState.containsKey(ARTICLE_ID)) {
			mArticlid = savedInstanceState.getString(ARTICLE_ID);
		}
		return super.onCreateView(inflater, vgroup, savedInstanceState);
	}

	@Override
	public Data getDataFromNet(String Url, String oldtype, int page, int count,
			boolean isfirst, String parttype) throws Exception {
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		String strs = MySSLSocketFactory.getinfo(
				getResources().getString(R.string.citylife_getComment_list_url)
						+ "sellerId=" + mArticlid + "&pageNo=" + (page + 1)
						+ "&pageNum=" + count, param);
		return parseJson(strs);
	}

	@Override
	public boolean dealClick(Items item, int position) {
		// TODO Auto-generated method stub
		return true;
	}

	public Data parseJson(String strs) throws Exception {
		Data d = new Data();
		JSONObject list = new JSONObject(strs);
		if (list.has("responseCode")) {
			int code = list.getInt("responseCode");
			if (code != 0) {
				throw new DataException("暂无评论返回");
			}
		}
		JSONArray json = list.getJSONArray("results");
		String how = "";
		if (list.has("countNum")) {
			how = list.getString("countNum");
		}
		ArrayList al = new ArrayList();
		int count = json.length();
		for (int i = 0; i < count; i++) {
			JSONObject obj = json.getJSONObject(i);
			Items o = new Items();
			try {
				o.nid = obj.getString("id");
				o.title = obj.getString("nickName");
				o.des = obj.getString("conent");
				o.level = obj.getString("level");
				o.u_date = obj.getString("addtime");
				o.icon = obj.getString("avatar");
			} catch (Exception e) {
				// TODO: handle exception
			}
			al.add(o);
		}
		d.list = al;
		d.date = how;
		return d;
	}

	@Override
	public Data getDataFromDB(String oldtype, int page, int count,
			String parttype) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getListItemview(View itemView, Items item, int position) {
		if (itemView == null) {
			itemView = LayoutInflater.from(getActivity()).inflate(
					R.layout.listitem_comment, null);
		}
		final TextView title = (TextView) itemView
				.findViewById(R.id.article_comment_title);
		TextView content = (TextView) itemView
				.findViewById(R.id.article_comment_des);
		TextView time = (TextView) itemView
				.findViewById(R.id.article_comment_time);
		RatingBar tb = (RatingBar) itemView
				.findViewById(R.id.comment_layout_listview_ratingbar);
		String des = item.des;
		tb.setRating(Float.parseFloat(item.level));
		if (des == null || "null".equals(des)) {
			des = "";
		}
		// if (position % 2 == 0) {
		// itemView.setBackgroundColor(mContext.getResources().getColor(
		// R.color.d_two));
		// } else {
		// itemView.setBackgroundColor(Color.WHITE);
		// }
		content.setText(des);
		title.setText(item.title);
		time.setText(item.u_date);
		return itemView;
	}

	public OnReflashTotal mOnReflashTotal;

	@Override
	public void update() {
		mOnReflashTotal.onReflash(mData);
		super.update();
	}

	public interface OnReflashTotal {
		public void onReflash(Data data);
	}
}
