package com.pyxx.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pyxx.fragment.FirstListFragment;
import com.pyxx.fragment.TuiJianListFragment;
import com.pyxx.sildingmenu.SlidingFragmentActivity;
import com.pyxx.sildingmenu.SlidingMenu;
import com.pyxx.zhongguofuzhuangxiemao.R;

/**
 * 左右侧滑式布局
 * 
 * @author HeJian
 * 
 */
public class FragmentChangeLeftOrRightActivity extends SlidingFragmentActivity {

	private Fragment Center_mContent;
	private Fragment Left_Content;
	private Fragment Right_Content;

	private Fragment oldmContent;
	private int oldmContent_pos;
	public ImageView m_MoveView[];
	public ImageView m_OldMoveView;
	public ImageButton m_Oldclick;
	private Fragment frag = null;
	private RelativeLayout title_item;
	TextView title;
	private int nowFlag = 1;
	Intent intent = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setBehindContentView(R.layout.menu_frame);
		FragmentTransaction t = this.getSupportFragmentManager()
				.beginTransaction();
		/** 左边菜单栏 */
		//Left_Content = new ColorMenuFragment();
		t.replace(R.id.menu_frame, Left_Content);
		t.commit();
		SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		/** 设置滑动模式为左右滑动 */
		getSlidingMenu().setMode(SlidingMenu.LEFT);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		if (savedInstanceState != null)
			Center_mContent = getSupportFragmentManager().getFragment(
					savedInstanceState, "Center_mContent");
		if (Center_mContent == null)
			Center_mContent = TuiJianListFragment.newInstance(
					"TuiJianListFragment", "TuiJianListFragment",
					getString(R.string.url_sel_product)
							+ "commodity.menuId=6&commodity.category=1");
		setContentView(R.layout.content_frame);
		/** 加载中间Frag */
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.mainroot, Center_mContent).commit();
		this.findViewById(R.id.title_back).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						getSlidingMenu().toggle();
					}
				});
		this.findViewById(R.id.title_btn_right).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (nowFlag == 1) {
							intent = new Intent(
									FragmentChangeLeftOrRightActivity.this,
									SearchProductMainActivity.class);
							startActivity(intent);
						}
						if (nowFlag == 2) {
							intent = new Intent(
									FragmentChangeLeftOrRightActivity.this,
									SearchShopsMainActivity.class);
							startActivity(intent);
						}
						if (nowFlag == 3) {
							intent = new Intent(
									FragmentChangeLeftOrRightActivity.this,
									SearchProductMainActivity.class);
							startActivity(intent);
						}
						if (nowFlag == 4) {
							intent = new Intent(
									FragmentChangeLeftOrRightActivity.this,
									SearchZixunMainActivity.class);
							startActivity(intent);
						}
					}

				});
		// getSlidingMenu().setSecondaryMenu(R.layout.menu_frame_two);
		// getSlidingMenu().setSecondaryShadowDrawable(R.drawable.shadowright);
		// /** 右边菜单栏 */
		// Right_Content = new ColorMenuFragment();
		// getSupportFragmentManager().beginTransaction()
		// .replace(R.id.menu_frame_two, Right_Content).commit();
		// getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		title = (TextView) this.findViewById(R.id.title_title);

	}

	public void things(View view) {

	}

	public void noClick(View view) {// 防止点击下拉刷新出的时间整个区域报错

	}

	public void getNowFlag(int nowFlag) {
		this.nowFlag = nowFlag;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "Center_mContent",
				Center_mContent);
	}

	int oldposition = 0;

	public void switchContent(final Fragment fragment, final int position) {
		Center_mContent = fragment;
		getSlidingMenu().showContent();
		if (oldposition != position || position == 2) {

			Handler h = new Handler();
			h.postDelayed(new Runnable() {
				public void run() {
					FragmentTransaction ft = getSupportFragmentManager()
							.beginTransaction();

					oldposition = position;
					ft.replace(R.id.mainroot, fragment).commit();
					switch (position) {
					case 0:
						title.setText(R.string.main_1_title);
						invalidateOptionsMenu();
						break;
					case 1:
						title.setText(R.string.main_2_title);
						invalidateOptionsMenu();
						break;
					case 2:
						title.setText(R.string.main_3_title);
						invalidateOptionsMenu();
						break;
					case 3:
						title.setText(R.string.main_4_title);
						invalidateOptionsMenu();
						break;
					case 4:
						title.setText(R.string.main_5_title);
						invalidateOptionsMenu();
						break;
					case 5:
						title.setText(R.string.main_6_title);
						invalidateOptionsMenu();
						break;
					case 6:
						title.setText(R.string.main_6_title);
						invalidateOptionsMenu();
						break;

					default:
						break;
					}
				}
			}, 300);
		}
	}

	/**
	 * 收起软键盘并设置提示文字
	 */
	public void collapseSoftInputMethod() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& !getSlidingMenu().isMenuShowing()) {
			if (oldposition == 6) {
				switchContent(oldmContent, oldmContent_pos);
				return false;
			}
			new AlertDialog.Builder(this)
					.setTitle(R.string.exit)
					.setMessage(
							getString(
									R.string.exit_message,
									new String[] { getString(R.string.app_name) }))
					.setPositiveButton(R.string.done,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// Intent intent = new Intent(
									// BaseActivity.ACTIVITY_FINSH);
									// sendBroadcast(intent);
									finish();
								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
								}
							}).show();
			return false;
		}

		return super.onKeyDown(keyCode, event);
	}
}
