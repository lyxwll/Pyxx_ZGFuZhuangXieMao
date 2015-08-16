package com.pyxx.fragment;

import java.util.ArrayList;
import java.util.List;

import com.pyxx.entity.Listitem;
import com.pyxx.entity.Pictorial;

/**
 * 装箱、拆箱操作
 * 
 * @author Clude
 * @time 2012-1-5 AM 11:35:05
 */
public class PictorialOperate {
	/**
	 * 数据装箱
	 * 
	 * @param data
	 * @return
	 */
	public static List<Pictorial> packing(List<Listitem> data, int lenght) {
		List<Pictorial> drawList = new ArrayList<Pictorial>();// 箱子
		int len = data.size();
		int count = len / lenght;
		int i = 0;
		for (i = 0; i < count; i++) {
			Pictorial p = new Pictorial();
			p.piclist = data.subList(lenght * i, lenght * (i + 1));
			drawList.add(p);
		}
		if ((len % lenght == 0 ? 0 : 1) == 1) {
			Pictorial p = new Pictorial();
			p.piclist = data.subList(lenght * i, len);
			drawList.add(p);
		}
		return drawList;
	}

	/**
	 * 数据拆箱
	 * 
	 * @param data
	 * @return
	 */
	public static List<Listitem> unboxing(List<Pictorial> data) {
		int len = data.size();
		List<Listitem> allListitems = new ArrayList<Listitem>();
		for (int i = 0; i < len; i++) {
			allListitems.addAll(data.get(i).piclist);
		}
		return allListitems;
	}
}
