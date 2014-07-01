package com.example.onemap;

import com.google.android.gms.maps.model.LatLngCreator;

import android.content.Context;
import android.os.AsyncTask;

public class TaskKmlToDataBase extends AsyncTask<Object, Void, String> {
	private final static String YES = "YES";
	private DBHelper dbHelper;

	@Override
	protected String doInBackground(Object... params) {
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

		KmlPlaceMark kpm = new KmlPlaceMark();
		for (int index = 0; index < pks.getPlaceMarkLength(); index++) {

			// PM_FIELD_LAYER_NAME
			kpm.setLayerName(layerName);

			// PM_FIELD_PLACEMARK_NAME
			String placeMarkName = pks.getPlaceMarkName(index);
			kpm.setPlaceMarkName(placeMarkName);

			// PM_FIELD_STYLE
			String placeMarkStyleUrl = pks.getStyleUrl(index);
			String styleUrl = pks.transToStyleUrl(placeMarkStyleUrl);

			for (int index1 = 0; index1 < pks.getStyleLength(); index1++) {
				if (styleUrl.equals(pks.getPolyStyleId(index1))) {
					JSONObject styleContent = new JSONObject();
					styleContent.put("polyColor", pks.getPolyColor(index1));
					styleContent.put("colorMode", pks.getColorMode(index1));
					styleContent.put("lineColor", pks.getLineColor(index1));
					styleContent.put("lineWidth", pks.getLineWidth(index1));
					kpm.setStyle(styleContent.toString());
				}
			}
			
			// PM_FIELD_COORDINATE
			kpm.setCoordinates(pks.getCoordinateString(index));
			// PM_FIELD_DESC
			kpm.setDesc("test");
			dbHelper.addPlaceMark(kpm);
		}

		return "end";
	}

	class test extends LatLngCreator {

	}
}
