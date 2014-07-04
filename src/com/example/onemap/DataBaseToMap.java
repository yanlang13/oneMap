package com.example.onemap;

import java.util.HashMap;
import java.util.List;

import com.google.android.gms.maps.model.PolygonOptions;
import android.content.Context;
import android.os.AsyncTask;

public class DataBaseToMap extends
		AsyncTask<Context, Void, HashMap<String, PolygonOptions>> {

	private DBHelper dbHelper;
	private HashMap<String, PolygonOptions> pos;

	@Override
	protected HashMap<String, PolygonOptions> doInBackground(Context... params) {

		Context context = params[0];
		dbHelper = new DBHelper(context);
		pos = new HashMap<String, PolygonOptions>();
		// 抓database裡面的layers
		List<Layer> layers = dbHelper.getAllLayer();

		for (Layer l : layers) {
			// TODO 判斷是否需要新增到hashMap
			// 要display的圖才放到showLayers
			if (l.getDisplay().equals("YES")) {
				String layerName = l.getLayerName();

				// 傳回hashMap型態
				HashMap<String, PolygonOptions> pos1 = dbHelper
						.getPolygon(layerName);
				pos.putAll(pos1);
			}
		}// end of for

		if (dbHelper != null) {
			dbHelper.close();
		}
		return pos;
	}// end of doInBackground
}// end of DataBaseToMap

