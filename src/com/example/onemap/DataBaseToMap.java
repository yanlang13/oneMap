package com.example.onemap;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class DataBaseToMap extends
		AsyncTask<Context, Void, HashMap<String, PolygonOptions>> {

	private final static String LINE_WIDTH = "lineWidth";
	private final static String COLOR_MODE = "colorMode";
	private final static String POLY_COLOR = "polyColor";
	private final static String LINE_COLOR = "lineColor";
	private final static String COORDINATES = "coordinates";

	private DBHelper dbHelper;
	private HashMap<String, PolygonOptions> pos;

	@Override
	protected HashMap<String, PolygonOptions> doInBackground(Context... params) {
		Context context = params[0];
		dbHelper = new DBHelper(context);
		pos = new HashMap<String, PolygonOptions>();
		// 抓database裡面的layers
//		List<Layer> layers = dbHelper.getAllLayer();
//
//		for (Layer l : layers) {
//			// TODO 判斷是否需要新增到hashMap
//			// 要display的圖才放到showLayers
//			if (l.getDisplay().equals("YES")) {
//				String styleLink = l.getStyleLink();
//				try {
//					URL url = new URL(styleLink);
//					File file = new File(url.toURI());
//					String jsonString = FileUtils.readFileToString(file);
//
//					JSONObject json = new JSONObject(jsonString);
//					json.getString(COLOR_MODE);
//
//					String coordinates = json.getString(COORDINATES);
//					ArrayList<LatLng> latLngs = transCoorStringToLatLngs(coordinates);
//
//					PolygonOptions po = new PolygonOptions();
//					po.addAll(latLngs);
//					po.fillColor(json.getInt(POLY_COLOR));
//					po.strokeColor(json.getInt(LINE_COLOR));
//					po.strokeWidth(json.getInt(LINE_WIDTH));
//
//					String key = l.getLayerPlaceName();
//					pos.put(key, po);
//
//				} catch (MalformedURLException e) {
//					Log.d("mdb", "DataBaseToMap.class: " + e.toString());
//				} catch (IOException e) {
//					Log.d("mdb", "DataBaseToMap.class: " + e.toString());
//				} catch (URISyntaxException e) {
//					Log.d("mdb", "DataBaseToMap.class: " + e.toString());
//				}// end of try
//			}// end of if
//		}// end of for

		if (dbHelper != null) {
			dbHelper.close();
		}

		Log.d("mdb", "=====end of databaseToMap");

		return pos;
	}// end of doInBackground

	/**
	 * copy from ParsingKmlString
	 * 
	 * @param coordinates
	 * @return
	 */
	private ArrayList<LatLng> transCoorStringToLatLngs(String coordinates) {
		// 取出的kmlString轉為list，split用 | 分隔使用的分隔符號
		List<String> listStringCoordinates = new ArrayList<String>(
				Arrays.asList(coordinates.split(",| ")));

		ArrayList<LatLng> latLngs = new ArrayList<LatLng>();

		int length = listStringCoordinates.size();

		for (int i = 0; i < length - 1; i += 3) {
			// 取kmlString的coordinates 轉double
			// d1為longitude
			double longitude = Double.valueOf(listStringCoordinates.get(i));
			// d2為latitude
			double latitude = Double.valueOf(listStringCoordinates.get(i + 1));
			LatLng latLng = new LatLng(latitude, longitude);

			// 放LatLng到ArrayList
			latLngs.add(latLng);
		}
		return latLngs;
	}// end of transCoorStringToLatLngs
}// end of DataBaseToMap

