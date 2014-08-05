package com.example.onemap;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.io.FileUtils;
import com.google.android.gms.maps.model.LatLng;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class TaskDrawLayerByBitmap extends
		AsyncTask<Context, Void, HashMap<String, ArrayList<LatLng>>> {

	private DBHelper dbHelper;
	private List<PlaceMark> placeMarks;
	private HashMap<String, ArrayList<LatLng>> layers;

	@Override
	protected HashMap<String, ArrayList<LatLng>> doInBackground(
			Context... params) {
		Context context = params[0];
		dbHelper = new DBHelper(context);
		placeMarks = new ArrayList<PlaceMark>();
		placeMarks = dbHelper.getDisplayPlaceMark();
		
		//TODO 改善HashMap的速率
		// 設好HashMap的Size能加快速度(少了resize的過程)
		layers = new HashMap<String, ArrayList<LatLng>>(placeMarks.size() * 2);

		for (PlaceMark placeMark : placeMarks) {
			String styleLink = placeMark.getStyleLink();
			try {
				URL url = new URL(styleLink);
				File file = new File(url.toURI());
				String jsonString = FileUtils.readFileToString(file);
				JSONObject json = new JSONObject(jsonString);
				String coordinates = json.getString("coordinates");
				ArrayList<LatLng> latLngs = OtherTools
						.transCoorStringToLatLngs(coordinates);
				layers.put(placeMark.getPlaceMarkName(), latLngs);
			} catch (MalformedURLException e) {
				Log.d("mdb", "DataBaseToMap.class: " + e.toString());
			} catch (IOException e) {
				Log.d("mdb", "DataBaseToMap.class: " + e.toString());
			} catch (URISyntaxException e) {
				Log.d("mdb", "DataBaseToMap.class: " + e.toString());
			}// end of try
		}// end of for
		Log.d("mdb", "=====end of doInBackground=====");
		return layers;
	}// end of doInBackground
}// end of public class TaskDrawLayerByBitmap
