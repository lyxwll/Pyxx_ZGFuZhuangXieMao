package com.pyxx.part_activiy;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.pyxx.app.ShareApplication;
import com.pyxx.entity.Listitem;
import com.pyxx.zhongguofuzhuangxiemao.R;

/**
 * 资讯详情 界面
 * 
 * @author wll
 */
public class ZixunArticleActivity extends Activity {
	private ImageView btn_back;// 返回按钮
	private TextView txt_title, txt_time, txt_summary, txt_source, txt_content;
	private ImageView img_news;
	Listitem select_entity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_article_zixun);

		if (getIntent().getExtras() != null) {
			select_entity = (Listitem) getIntent().getExtras().get("item");
		}

		findViewById(R.id.title_back).setVisibility(View.VISIBLE);
		findViewById(R.id.title_btn_right).setVisibility(View.GONE);

		((TextView) findViewById(R.id.title_title)).setText("资讯详情");
		// 初始化所有控件
		initView();
		// 控件对应注册事件
		// 返回按钮
		btn_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ZixunArticleActivity.this.finish();
			}
		});
	}

	public void initView() {
		btn_back = (ImageView) this.findViewById(R.id.title_back);// 返回按钮
		btn_back.setImageResource(R.drawable.return_tb1);
		txt_title = (TextView) this
				.findViewById(R.id.txtview_information_title_id);// 资讯标题
		txt_time = (TextView) this
				.findViewById(R.id.txtview_information_time_id);// 发布时间
		txt_summary = (TextView) this
				.findViewById(R.id.txtview_information_summary_id);// 资讯摘要
		txt_content = (TextView) this
				.findViewById(R.id.txtview_information_news_id);// 资讯内容
		txt_source = (TextView) this
				.findViewById(R.id.txtview_information_source_id);// 来源
		img_news = (ImageView) this.findViewById(R.id.imgview_information_id);

		img_news.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Intent intent = new Intent();
				// intent.setClass(ZixunArticleActivity.this,
				// ShowImgActivity.class);
				// intent.putExtra("item", select_entity);
				// intent.putExtra("img_list", select_entity.icon.split(","));
				// startActivity(intent);
			}
		});

		if (select_entity != null) {

			txt_title.setText(select_entity.title);
			txt_time.setText(select_entity.u_date);
			txt_summary.setText(select_entity.other1);
			txt_content.setText(select_entity.other2);
			txt_source.setText("来源 | " + select_entity.other3);

			// System.out.println("img_url:" + img_url);

			// 新方法
			// bitmap加载就这一行代码，display还有其他重载，详情查看源码
			if (select_entity.icon != null && select_entity.icon.length() > 10) {
				String img_url = select_entity.icon.split(",")[0].trim();
				ShareApplication.mImageWorker.loadImage(img_url, img_news);
				img_news.setVisibility(View.VISIBLE);
			} else {
				img_news.setImageResource(R.drawable.list_qst);
				img_news.setVisibility(View.VISIBLE);
			}
			/*
			 * 旧方法； BitmapHelper.fetchImage2(this, img_url.trim(), new
			 * OnFetchCompleteListener2() {
			 * 
			 * @Override public void onFetchComplete(Object arg0, Bitmap arg1) {
			 * img_news.setImageBitmap(arg1); } else {
			 * 
			 * // 无数据默认显示的图片 Bitmap
			 * bitmap=BitmapFactory.decodeResource(InformationMsgActivity
			 * .this.getResources(), R.drawable.listview_for_default);
			 * img_news.setImageBitmap(bitmap);
			 * 
			 * img_news.setImageDrawable(InformationMsgActivity.this.getResources
			 * ().getDrawable(R.drawable.listview_for_default));
			 * 
			 * 
			 * } } });
			 */
		}
	}

}
