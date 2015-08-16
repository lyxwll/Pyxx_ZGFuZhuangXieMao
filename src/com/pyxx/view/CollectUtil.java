package com.pyxx.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.pyxx.entity.DBCollectUtil;
import com.pyxx.entity.Listitem;
import com.pyxx.loadimage.Utils;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.PerfHelper;

/**
 * 收藏工具类
 * 
 * @author wll
 */
public class CollectUtil {
	private Context mContext;
	private String userId;
	private boolean collect;
	private Listitem item;
	private View btn_shoucang;

	public CollectUtil(Context context, boolean collect, Listitem item,
			View btn_shoucang) {
		this.mContext = context;
		this.collect = collect;
		this.item = item;
		this.btn_shoucang = btn_shoucang;
		this.userId = PerfHelper.getStringData(PerfHelper.P_SHARE_USER_ID);
	}

	public void initCollect() {

		if (PerfHelper.getBooleanData(PerfHelper.P_USER_LOGIN)) {
			String strnid = item.nid;
			String listtype = item.list_type;
			collect = DBCollectUtil.exit("listitemcollect", "*",
					"list_type,nid", listtype + "," + strnid);
			if (collect) {
				btn_shoucang.setBackgroundResource(R.drawable.shoucang_tb3);
			} else {
				btn_shoucang.setBackgroundResource(R.drawable.shoucang_tb2);
			}
		}

		// 点击收藏美食
		btn_shoucang.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!PerfHelper.getBooleanData(PerfHelper.P_USER_LOGIN)) {
					Toast.makeText(mContext, "请登录后收藏", Toast.LENGTH_SHORT)
							.show();
				} else {
					if (collect) {
						new CustomAlertDialog.Builder(mContext)
								.setTitle("收藏提示")
								.setIcon(R.drawable.main_ic)
								.setMessage("是否取消收藏？")
								.setPositiveButton("是",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												String strnid = item.nid;
												String listtype = item.list_type;
												DBCollectUtil
														.cancle("listitemcollect",
																"list_type,nid",
																listtype
																		+ ","
																		+ strnid);
												collect = false;
												btn_shoucang
														.setBackgroundResource(R.drawable.shoucang_tb2);
												Utils.showToast("取消收藏成功");
											}
										})
								.setNegativeButton("否",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface arg0,
													int arg1) {
											}
										}).show();
					} else {
						new CustomAlertDialog.Builder(mContext)
								.setTitle("收藏提示")
								.setIcon(R.drawable.main_ic)
								.setMessage("是否收藏？")
								.setPositiveButton("是",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												DBCollectUtil.insert(item);
												collect = true;
												btn_shoucang
														.setBackgroundResource(R.drawable.shoucang_tb3);
												Utils.showToast("收藏成功");
											}
										})
								.setNegativeButton("否",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface arg0,
													int arg1) {
											}
										}).show();
					}
				}
			}
		});
	}

}
