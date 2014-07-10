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
	private Layer layer;
	private PlaceMark placeMark;
	private ParseKmlString pks;
	private boolean singlePlaceMark; // 檢查placeMark是否不是JSONArray
	private File singleMap;
	private String layerName;

	@Override
	protected Boolean doInBackground(Object... params) {
		Log.d("mdb", "=====doInBackgroud=====");
		Context context = (Context) params[0];
		layerName = (String) params[1];
		String kmlString = (String) params[2];
		pks = new ParseKmlString(kmlString);
		dbHelper = new DBHelper(context);
		layer = new Layer();
		placeMark = new PlaceMark();
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
			layer.setLayerName(layerName); // LA_FIELD_LAYER_NAME
			layer.setLDesc("test");// LA_FIELD_DESC
			layer.setDisplay(YES);// LA_FIELD_DISPLAY
			layer.setCreateAt(OtherTools.getDateTime());// LA_FIELD_CREATE_AT
			dbHelper.addLayer(layer);

			placeMark.setLayerName(layerName);
			placeMark.setPlaceMarkName(pks.getPlaceMarkName(0));
			placeMark.setPDesc("test");
			placeMark.setDisplay(YES);
			placeMark.setStyleLink(getStyleLink(0).toString());
			dbHelper.addPlaceMark(placeMark);

		} else { // placeMark is JSONArray
			layer.setLayerName(layerName);
			layer.setLDesc("test");
			layer.setDisplay(YES);
			layer.setCreateAt(OtherTools.getDateTime());
			dbHelper.addLayer(layer);

			for (int index = 0; index < pks.getPlaceMarkLength(); index++) {
				placeMark.setLayerName(layerName);
				placeMark.setPlaceMarkName(pks.getPlaceMarkName(index));
				placeMark.setPDesc("test");
				placeMark.setDisplay(YES);
				placeMark.setStyleLink(getStyleLink(index).toString());
				dbHelper.addPlaceMark(placeMark);
			}// end of for
		}// end of if
		return true;
	}// end of doInBackground

	// METHOD =================================================================

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

			File result = OtherTools.writeJsonToFile(singleMap, fileName,
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

					// 如果colorMode是random就幫它設定顏色。
					String colorMode = pks.getColorMode(index1);
					if (colorMode.equals("random")) {
						styleContent.put("colorMode", pks.getColorMode(index1));

						// TODO 還有些問題，色彩起點不符原本的。另外會有白色不透明(結束點)
						// 起始的第一個顏色
						int startPolyColor = pks.getPolyColor(index1);
						Log.d("mdb", "startPolyColor: " + startPolyColor);

						// 總共的顏色數量
						int colorNumber = pks.getPlaceMarkLength();
						Log.d("mdb",
								"pks.getPlaceMarkLength(): "
										+ pks.getPlaceMarkLength());

						// 最後一個顏色()
						int endPolyColor = OtherTools
								.kmlColorToARGB("82000000");
						Log.d("mdb", "OtherTools.kmlColorToARGB(82000000)"
								+ OtherTools.kmlColorToARGB("82000000"));

						int newPolyColor = startPolyColor
								- ((startPolyColor - endPolyColor) / colorNumber)
								* (index + 1);

						styleContent.put("polyColor", newPolyColor);
					} else {
						styleContent.put("polyColor", pks.getPolyColor(index1));
						styleContent.put("colorMode", pks.getColorMode(index1));
					}

					styleContent.put("lineColor", pks.getLineColor(index1));
					styleContent.put("lineWidth", pks.getLineWidth(index1));

					styleContent.put("coordinates",
							pks.getCoordinateString(index));
					String fileName = layerName + "_"
							+ pks.getPlaceMarkName(index) + ".txt";
					File result = OtherTools.writeJsonToFile(singleMap,
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
