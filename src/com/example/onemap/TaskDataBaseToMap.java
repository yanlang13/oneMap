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

import android.R.plurals;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class TaskDataBaseToMap extends
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
		// List<Layer> layers = dbHelper.getAllLayer();

		List<PlaceMark> placeMarks = dbHelper.getDisplayPlaceMark();

		for (PlaceMark pm : placeMarks) {
			String styleLink = pm.getStyleLink();
			try {
				URL url = new URL(styleLink);
				File file = new File(url.toURI());
				String jsonString = FileUtils.readFileToString(file);

				JSONObject json = new JSONObject(jsonString);
				json.getString(COLOR_MODE);

				String coordinates = json.getString(COORDINATES);
				ArrayList<LatLng> latLngs = OtherTools
						.transCoorStringToLatLngs(coordinates);

				PolygonOptions po = new PolygonOptions();
				po.addAll(latLngs);
				po.fillColor(json.getInt(POLY_COLOR));
				po.strokeColor(json.getInt(LINE_COLOR));
				po.strokeWidth(json.getInt(LINE_WIDTH));

				String key = pm.getPlaceMarkName();
				pos.put(key, po);

			} catch (MalformedURLException e) {
				Log.d("mdb", "DataBaseToMap.class: " + e.toString());
			} catch (IOException e) {
				Log.d("mdb", "DataBaseToMap.class: " + e.toString());
			} catch (URISyntaxException e) {
				Log.d("mdb", "DataBaseToMap.class: " + e.toString());
			}// end of try

		}// end of for

		if (dbHelper != null) {
			dbHelper.close();
		}

		Log.d("mdb", "=====end of databaseToMap");

		return pos;
	}// end of doInBackground

}// end of DataBaseToMap

