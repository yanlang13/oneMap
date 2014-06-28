package com.example.onemap;

import java.util.ArrayList;
import java.util.HashMap;
import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

import de.micromata.opengis.kml.v_2_2_0.PolyStyle;

/**
 * parsing kmlString to polygonOptions and add polygonOptions to map.
 * 
 * @param kmlString
 * @param map
 */
public class TaskKmlToMap extends
		AsyncTask<String, Void, HashMap<String, PolygonOptions>> {
	private HashMap<String, PolygonOptions> polyStyle; // color and width
	private HashMap<String, PolygonOptions> polyDisplay; // ready to display(add
	// LatLng)
	private HashMap<String, PolygonOptions> polyDesc; // desc

	@Override
	protected HashMap<String, PolygonOptions> doInBackground(String... params) {
		polyStyle = new HashMap<String, PolygonOptions>();
		polyDisplay = new HashMap<String, PolygonOptions>();

		String kmlString = params[0];

		ParseKmlString pks = new ParseKmlString(kmlString);

		// 用for loop，來處理所有的polyStyle
		for (int index = 0; index < pks.getStyleLength(); index++) {
			// po儲存color and width
			PolygonOptions po = new PolygonOptions();
			po.fillColor(pks.getPolyColor(index));
			po.strokeColor(pks.getLineColor(index));
			po.strokeWidth(pks.getLineWidth(index));
			String key = pks.getPolyStyleId(index);
			// TODO colorMode 意思?

			polyStyle.put(key, po);
		}

		Log.d("mdb", "end of for loop");

		// 確認polyStyle有東西再動作，將圖徵與座標結合
		if (!polyStyle.isEmpty()) {
			Log.d("mdb", "=====start polyStyle=====");
			for (int index = 0; index < pks.getPlaceMarkLength(); index++) {
				PolygonOptions po = new PolygonOptions();

				PolygonOptions copyFrom = new PolygonOptions();

				// key:styleUrl，取出的polygonOptions包含color of polygon and line
				// 運用get...的方式，避開hashMap指向同一個object並同步修改的問題
				String styleUrl = pks.getStyleUrl(index);
				copyFrom = polyStyle.get(pks.transToStyleUrl(styleUrl));

				po.fillColor(copyFrom.getFillColor());
				po.strokeColor(copyFrom.getStrokeColor());
				po.strokeWidth(copyFrom.getStrokeWidth());

				ArrayList<LatLng> coordinates = pks.getCoordinates(index);
				po.addAll(coordinates);
				String key = pks.getPlaceMarkName(index);
				polyDisplay.put(key, po);
			}
		} // end of if
		Log.d("mdb", "=====end polyStyle=====");
		return polyDisplay;
	}
}// end of kmlToMap
