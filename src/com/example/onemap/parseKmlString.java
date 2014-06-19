package com.example.onemap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.google.android.gms.maps.model.LatLng;

import android.graphics.Color;
import android.util.Log;

/**
 * 將kml的String轉成JSON formats
 * 
 */
public class parseKmlString {
	private JSONObject jsonObject;
	private JSONObject document; // 使用document開始取得檔案

	public parseKmlString(String kmlString) {
		// github下載的JSONObject
		jsonObject = XML.toJSONObject(kmlString);

		if (hasDocument()) {
			document = jsonObject.getJSONObject("kml")
					.getJSONObject("Document");
		}
	}
	
	/**
	 * 檢查kml file kml-Document-Style & Folder
	 * @return true = KML(my format) ; false = not;
	 */
	public boolean checkKmlFormat() {
		if (jsonObject.has("kml")) {
			if (jsonObject.getJSONObject("kml").has("Document")) {
				if (document.has("Style") && document.has("Folder")) {
					return true;
				}
			}
		}
		return false;
	}// end of hasStyleAndFolder

	/**
	 * @return true = KML ; false = not;
	 */
	public boolean hasDocument() {
		if (jsonObject.has("kml")) {
			if (jsonObject.getJSONObject("kml").has("Document")) {
				return true;
			}
		}
		return false;
	}// end of hasDocument

	/**
	 * get data from kml coordinates
	 * 
	 * @return ArrayList LatLng
	 */
	public ArrayList<LatLng> getCoordinates() {
		if (hasDocument()) {
			try {
				String coordinates = document.getJSONObject("Placemark")
						.getJSONObject("Polygon")
						.getJSONObject("outerBoundaryIs")
						.getJSONObject("LinearRing").getString("coordinates");
				// Log.d("mdb", "getCoordinates: " + coordinates);

				// 取出的kmlString轉為list，split用 | 分隔使用的分隔符號
				List<String> listStringCoordinates = new ArrayList<String>(
						Arrays.asList(coordinates.split(",| ")));

				ArrayList<LatLng> latLngs = new ArrayList<LatLng>();

				int length = listStringCoordinates.size();

				for (int i = 0; i < length - 1; i += 3) {
					// 取kmlString的coordinates 轉double
					// d1為longitude
					double longitude = Double.valueOf(listStringCoordinates
							.get(i));
					// d2為latitude
					double latitude = Double.valueOf(listStringCoordinates
							.get(i + 1));
					LatLng latLng = new LatLng(latitude, longitude);

					// 放LatLng到ArrayList
					latLngs.add(latLng);
				}
				return latLngs;
			} catch (JSONException e) {
				Log.d("mdb", "parserkmlString class," + e.toString());
				return null;
			}
		} else {
			// TODO 如果沒有DOCUMENT的coordinates，就是拿掉document的jsonarray
			return null;
		}
	}// end of getCoordiantes

	/**
	 * get data from kml description
	 * 
	 * @return String
	 */
	public String getDescription() {
		if (hasDocument()) {
			try {
				String description = document.getJSONObject("Placemark")
						.getString("description");
				return description;
			} catch (JSONException e) {
				Log.d("mdb", "parserkmlString class," + e.toString());
				return null;
			}
		} else {
			// TODO 如果沒有DOCUMENT的description
			return null;
		}
	}// end of getDescription

	/**
	 * get data from kml Style_id
	 * 
	 * @return int ARGB color
	 */
	public String[] getDrawId() {
		if (hasDocument()) {
			document.length();
			document.getJSONArray("id");
			return null;
		} else {
			return null;
		}

	}// end of getDrawId

	/**
	 * get data from kml PoltStyle
	 * 
	 * @return int ARGB color
	 */
	public int getPolyColor() {
		if (hasDocument()) {
			try {
				JSONObject style = document.getJSONArray("Style")
						.getJSONObject(1);
				String abgr = style.getJSONObject("PolyStyle").getString(
						"color");
				return kmlColorToARGB(abgr);

			} catch (JSONException e) {
				Log.d("mdb", "parserkmlString class," + e.toString());
				return (Integer) null;
			} catch (IllegalArgumentException e) { // the colorString can't be
													// parsed
				Log.d("mdb", "parserkmlString class," + e.toString());
				return (Integer) null;
			}
		} else {
			// TODO 如果沒有DOCUMENT的get
			return (Integer) null;
		}
	}// end of getpolyColor()

	/**
	 * get data from kml LineStyle
	 * 
	 * @return int color
	 */
	public int getLineColor() {
		if (hasDocument()) {
			try {
				JSONObject style = document.getJSONArray("Style")
						.getJSONObject(1);
				String abgr = style.getJSONObject("LineStyle").getString(
						"color");
				return kmlColorToARGB(abgr);

			} catch (JSONException e) {
				Log.d("mdb", "parserkmlString class," + e.toString());
				return (Integer) null;
			} catch (IllegalArgumentException e) { // the colorString can't be
													// parsed
				Log.d("mdb", "parserkmlString class," + e.toString());
				return (Integer) null;
			}
		} else {
			// TODO 如果沒有DOCUMENT的get
			return (Integer) null;
		}
	}// end of getLineColor()

	/**
	 * get data from kml LineStyle
	 * 
	 * @return float width
	 */
	public float getLineWidth() {
		if (hasDocument()) {
			try {
				JSONObject style = document.getJSONArray("Style")
						.getJSONObject(1);
				int width = style.getJSONObject("LineStyle").getInt("width");
				return Float.valueOf(width);

			} catch (JSONException e) {
				Log.d("mdb", "parserkmlString class," + e.toString());
				return (Float) null;
			} catch (IllegalArgumentException e) { // the colorString can't be
													// parsed
				Log.d("mdb", "parserkmlString class," + e.toString());
				return (Float) null;
			}
		} else {
			// TODO 如果沒有DOCUMENT的get
			return (Integer) null;
		}
	}// end of getLineColor()

	// =============================================================priavteMethodsing
	private int kmlColorToARGB(String abgr) {
		String stringAlpha = abgr.substring(0, 2);
		String strinfBlue = abgr.substring(2, 4);
		String stringGreen = abgr.substring(4, 6);
		String strinfRed = abgr.substring(6);

		// 主要是透過parseColor將StringARGB轉為int
		int argb = Color.parseColor("#" + stringAlpha + strinfRed + stringGreen
				+ strinfBlue);
		return argb;
	}
	// =============================================================priavteMethodsed

}// end of parseKmlString

