package com.example.onemap;

import java.util.List;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class TaskKmlToDataBase extends AsyncTask<Object, Void, String> {
	private final static String YES = "YES";
	private DBHelper dbHelper;

	@Override
	protected String doInBackground(Object... params) {
		Log.d("mdb", "===== do in TaskKmlToDataBase=====");
		// TODO parsing KML to database
		Context context = (Context) params[0];
		String layerName = (String) params[1];
		String kmlString = (String) params[2];

		ParseKmlString pks = new ParseKmlString(kmlString);
		dbHelper = new DBHelper(context);
		
		// TABLE_LAYERS
		Layer layer = new Layer();

		// LA_FIELD_LAYER_NAME
		layer.setLayerName(layerName);

		// LA_FIELD_DESC
		layer.setDesc("test");

		// LA_FIELD_DISPLAY
		layer.setDisplay(YES);
		dbHelper.addLayer(layer);
		
		
		Log.d("mdb", "===== kmlStyle =====");
		// TABLE_STYLE
		KmlStyle ks = new KmlStyle();
		for (int index = 0; index < pks.getStyleLength(); index++) {
			// ST_FIELD_LAYER_NAME
			ks.setLayerName(layerName);

			// ST_FIELD_STYLE_NAME
			String styleName = pks.getPolyStyleId(index);
			ks.setStyleName(styleName);

			// ST_FIELD_STYLE_CONTENT
			JSONObject styleContent = new JSONObject();
			PolygonOptions po = new PolygonOptions();
			po.fillColor(pks.getPolyColor(index));
			po.strokeColor(pks.getLineColor(index));
			po.strokeWidth(pks.getLineWidth(index));
			styleContent.put(styleName, po);
			ks.setStyleContent(styleContent.toString());
			dbHelper.addKmlStyle(ks);
		}

		Log.d("mdb", "===== kmlPlaceMark =====");
		KmlPlaceMark kpm = new KmlPlaceMark();
		for (int index = 0; index < pks.getPlaceMarkLength(); index++) {

			// PM_FIELD_LAYER_NAME
			kpm.setLayerName(layerName);

			// PM_FIELD_PLACEMARK_NAME
			String placeMarkName = pks.getPlaceMarkName(index);
			kpm.setPlaceMarkName(placeMarkName);

			// PM_FIELD_STYLEURL
			String placeMarkStyleUrl = pks.getStyleUrl(index);
			String styleUrl = pks.transToStyleUrl(placeMarkStyleUrl);
			kpm.setStyleUrl(styleUrl);

			// PM_FIELD_COORDINATE
			List<LatLng> coordinates = pks.getCoordinates(index);
			JSONObject coordinate = new JSONObject();
			coordinate.put(placeMarkName, coordinates);
			kpm.setCoordinates(coordinate.toString());

			// PM_FIELD_DESC
			kpm.setDesc("test");	
			dbHelper.addPlaceMark(kpm);
		}

		return "end";
	}

}
