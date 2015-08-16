package com.pyxx.ui;

import com.pyxx.entity.BaseFragmentActivity;
import com.pyxx.fragment.SearchProductListFragment;
import com.pyxx.fragment.SearchZgspShopsListFragment;
import com.pyxx.loadimage.Utils;
import com.pyxx.zhongguofuzhuangxiemao.R;
import com.utils.PerfHelper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SearchProductMainActivity extends BaseFragmentActivity implements
		OnClickListener {

	String keyword = "";
	private Fragment m_list_frag = null;
	FragmentTransaction fragmentTransaction = null;
	private RelativeLayout main_rl;
	private EditText keyword_ed;
	private TextView back_btn;
	private View btn_search;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_main);
		main_rl = (RelativeLayout) this.findViewById(R.id.main_rl);
		btn_search = this.findViewById(R.id.btn_search);
		keyword_ed = (EditText) this.findViewById(R.id.keyword_ed);
		keyword_ed.addTextChangedListener(watcher);
		back_btn = (TextView) this.findViewById(R.id.title_back);
		back_btn.setOnClickListener(this);
		initFragment();
	}

	private TextWatcher watcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			keyword = keyword_ed.getText().toString().trim();
			if (keyword == null || "".equals(keyword)) {
				Utils.showToast("请输入需要检索的条件");
				return;
			} else {
				fragmentTransaction = getSupportFragmentManager()
						.beginTransaction();
				m_list_frag = SearchProductListFragment.newInstance(
						"SearchProductListFragment" + keyword,
						"SearchProductListFragment" + keyword,
						getString(R.string.url_sel_product)
								+ "commodity.menuId=6&" + keyword);
				fragmentTransaction.replace(R.id.part_search, m_list_frag)
						.commit();
			}

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	};

	public void initFragment() {
		fragmentTransaction = getSupportFragmentManager().beginTransaction();
		m_list_frag = SearchProductListFragment.newInstance(
				"SearchProductListFragment" + keyword,
				"SearchProductListFragment" + keyword,
				getString(R.string.url_sel_product) + "commodity.menuId=6&"
						+ keyword);
		fragmentTransaction.replace(R.id.part_search, m_list_frag).commit();

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.title_back) {
			this.finish();
		}

	}

}
