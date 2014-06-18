package com.example.onemap;

import com.example.onemap.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * 這個class用來處理drawerLyout的adapter，主要是改寫getView這個methods
 * getView會一個一個list創造(超過頁面放到recycle)，所以可以用position取值
 */
public class DrawerArrayAdapter extends ArrayAdapter<DrawerListDetails> {
	private Context context;

	public DrawerArrayAdapter(Context context, DrawerListDetails[] details) {
		super(context, R.layout.drawer_list_item, R.id.tv_drawer_drawerTitle, details);
		this.context = context;
	}// end of DrawerArrayAdapter

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// getSystemService需多加善用，可取得很多android背後運行的資源
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.drawer_list_item, parent, false);

		DrawerListDetails detail = getItem(position);
		TextView title = (TextView) view.findViewById(R.id.tv_drawer_drawerTitle);
		title.setText(detail.title);
		//XML: android:drawableLeft的code版
		title.setCompoundDrawablesRelativeWithIntrinsicBounds(detail.image, 0,
				0, 0);
		return view;
	}// end of setTitleId();
}// DrawerArrayAdapter
