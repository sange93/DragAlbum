package com.sange.dragalbum.util;

import com.sange.album.PhotoItem;

import java.util.ArrayList;
import java.util.List;

public class MyUtil {
	public List<PhotoItem> moreItems(int qty, List<PhotoItem> Datas) {
		List<PhotoItem> items = new ArrayList<PhotoItem>();
		if(Datas!=null){
			items.addAll(Datas);
		}
		//添加null
		for (int i = Datas == null ? 0 : Datas.size(); i < qty; i++) {
			items.add(new PhotoItem());
		}
		return items;
	}
}
