package com.example.onemap;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class TaskKmlToDataBase extends AsyncTask<Object, Void, Boolean> {
	private final static String YES = "YES";
	private DBHelper dbHelper;
	private ParseKmlString pks;
	private boolean singlePlaceMark; // 檢查placeMark是否不是JSONArray
	private File singleMap;
	private String layerName;
	private OtherTools otherTools;

	@Override
	protected Boolean doInBackground(Object... params) {
		Context context = (Context) params[0];
		layerName = (String) params[1];
		String kmlString = (String) params[2];

		otherTools = new OtherTools();
		pks = new ParseKmlString(kmlString);
		dbHelper = new DBHelper(context);
		Layer layer = new Layer();
		setSinglePlaceMark();

		// 確認sd卡能否讀取
		if (isExternalStorageWritable()) {
			// 輸出位置，預設位置為 /mnt/sdcard
			singleMap = new File(Environment.getExternalStorageDirectory()
					+ "/SingleMap/");
			if (!singleMap.exists()) {
				singleMap.mkdir();
			}
		} else {
			return false;
		}// end of if

		if (singlePlaceMark) { // plceMark is JSONObject
			layer.setLayerPlaceName(layerName + "_" + pks.getPlaceMarkName(0)); // LA_FIELD_LAYER_NAME
			layer.setlDesc("test");// LA_FIELD_DESC
			layer.setDisplay(YES);// LA_FIELD_DISPLAY
			layer.setPDesc("test1");// LA_FIELD_PDESC
			layer.setStyleLink(getStyleLink(0).toString());// LA_FIELD_S
			dbHelper.addLayer(layer);
		} else { // placeMark is JSONArray
			for (int index = 0; index < pks.getPlaceMarkLength(); index++) {
				layer.setLayerPlaceName(layerName + "_"
						+ pks.getPlaceMarkName(index)); // LA_FIELD_LAYER_NAME
				layer.setlDesc("test");// LA_FIELD_DESC
				layer.setDisplay(YES);// LA_FIELD_DISPLAY
				layer.setPDesc("test1");// LA_FIELD_PDESC
				layer.setStyleLink(getStyleLink(index).toString());// LA_FIELD_S
				dbHelper.addLayer(layer);
			}// end of for
		}// end of if

		return true;
	}// end of doInBackground

	/**
	 * @return if placeMark is JSONObject return true.
	 */
	private boolean setSinglePlaceMark() {
		if (pks.getPlaceMarkLength() == 0) {
			singlePlaceMark = true;
			return true;
		}
		singlePlaceMark = false;
		return false;
	}// end of setSinglePlaceMark()

	private URL getStyleLink(int index) {
		URL url = null;
		if (singlePlaceMark) {
			JSONObject styleContent = new JSONObject();
			// LA_FIELD_STYLE_LINK
			String styleUrl = pks.transToStyleUrl(pks.getStyleUrl(0));
			// 存放kml-style
			for (int index1 = 0; index1 < pks.getStyleLength(); index1++) {
				if (styleUrl.equals(pks.getPolyStyleId(index1))) {
					styleContent.put("polyColor", pks.getPolyColor(index1));
					styleContent.put("colorMode", pks.getColorMode(index1));
					styleContent.put("lineColor", pks.getLineColor(index1));
					styleContent.put("lineWidth", pks.getLineWidth(index1));
				}
			}
			// 存放kml-placemark-coordinates
			styleContent.put("coordinates", pks.getCoordinateString(0));
			String fileName = layerName + "_" + pks.getPlaceMarkName(0)
					+ ".txt";

			File result = otherTools.writeJsonToFile(singleMap, fileName,
					styleContent);
			try {
				return result.toURI().toURL();
			} catch (MalformedURLException e) {
				Log.d("mdb", "TaskKmlToDataBase.class" + e.toString());
			}
		} else { // placeMark is JSONArray
			String placeMarkStyleUrl = pks.getStyleUrl(index);

			String styleUrl = pks.transToStyleUrl(placeMarkStyleUrl);

			for (int index1 = 0; index1 < pks.getStyleLength(); index1++) {
				if (styleUrl.equals(pks.getPolyStyleId(index1))) {
					JSONObject styleContent = new JSONObject();
					Log.d("mdb", "====placeMarkId equal polyStyleId=====");
					styleContent.put("polyColor", pks.getPolyColor(index1));
					styleContent.put("colorMode", pks.getColorMode(index1));
					styleContent.put("lineColor", pks.getLineColor(index1));
					styleContent.put("lineWidth", pks.getLineWidth(index1));

					styleContent.put("coordinates",
							pks.getCoordinateString(index));
					String fileName = layerName + "_"
							+ pks.getPlaceMarkName(index) + ".txt";
					File result = otherTools.writeJsonToFile(singleMap,
							fileName, styleContent);
					try {
						return result.toURI().toURL();
					} catch (MalformedURLException e) {
						Log.d("mdb", "TaskKmlToDataBase.class" + e.toString());
					}// end of try
				}// end of if
			}// end of for
		}
		return url;
	}// end of getStyleLink()

	/**
	 * Checks if external storage is available for read and write
	 */
	private boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

}// end of class TaskKmlToDataBase
