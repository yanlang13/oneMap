package com.example.onemap;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import static com.example.onemap.AcrossConstants.*;

public class LayerInfoCursorAdapter extends CursorAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	

	public LayerInfoCursorAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		Log.d("mdb", "=====LayerInfoCursorAdapter=====");
		mContext = context;
		mInflater = LayoutInflater.from(context);
	}// end of LayerInfoCursorAdapter

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		Log.d("mdb", "=====newView=====");
		// 一般都这样写，返回列表行元素，注意这里返回的就是bindView中的view
		return mInflater.inflate(R.layout.layers_infomation, parent, false);
	}// end of newView

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		Log.d("mdb", "=====bindView=====");
		String layerName = cursor.getString(cursor
				.getColumnIndex(LA_FIELD_LAYER_NAME));
		String display = cursor.getString(cursor
				.getColumnIndex(LA_FIELD_DISPLAY));
		String createAt = cursor.getString(cursor
				.getColumnIndex(LA_FIELD_CREATE_AT));

		TextView tvLayerName = (TextView) view
				.findViewById(R.id.tv_layers_info_layer_name);
		TextView tvDisplay = (TextView) view
				.findViewById(R.id.tv_layers_info_layer_display);
		TextView tvCreateAt = (TextView) view
				.findViewById(R.id.tv_layers_info_layer_create_at);

		tvLayerName.setText("Layer Name: " + layerName);
		tvLayerName.setText("Display: " + display);
		tvLayerName.setText("Create at: " + createAt);

	}// end of bindView
}// end of LayerInfoCursorAdapter extends CursorAdapter
