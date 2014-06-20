package com.example.onemap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.google.android.gms.maps.model.LatLng;

import android.R.integer;
import android.graphics.Color;
import android.util.Log;

/**
 * 將KML的String轉成JSON formats
 * 
 */
public class ParseKmlString {
	private JSONObject jsonObject;
	private JSONObject document; // 使用document開始取得檔案

	private static final String KML = "kml";
	private static final String DOCUMENT = "Document";

	private static final String STYLE = "Style";
	private static final String ID = "id";
	private static final String POLYSTYLE = "PolyStyle";
	private static final String COLOR = "color";
	private static final String LINESTYLE = "LineStyle";
	private static final String WIDTH = "width";

	private static final String FOLDER = "Folder";
	private static final String PLACEMARK = "Placemark";
	private static final String MULTI_GEOMETRY = "MultiGeometry";
	private static final String POLYGON = "Polygon";
	private static final String OUTER_BOUNDARY_IS = "outerBoundaryIs";
	private static final String LINEAR_RING = "LinearRing";
	private static final String COORDINATES = "coordinates";
	private static final String STYLE_URL = "styleUrl";

	private static final String NAME = "name";

	public ParseKmlString(String kmlString) {
		// github下載的JSONObject
		jsonObject = XML.toJSONObject(kmlString);

		if (checkKmlFormat()) {
			document = jsonObject.getJSONObject(KML).getJSONObject(DOCUMENT);
		}
	}// end of parseKmlString

	/**
	 * @return true = KML ; false = not;
	 */
	public boolean hasDocument() {
		if (jsonObject.has(KML)) {
			if (jsonObject.getJSONObject(KML).has(DOCUMENT)) {
				return true;
			}
		}
		return false;
	}// end of hasDocument

	/**
	 * 檢查kml file kml-Document-Style & Folder
	 * 
	 * @return true = KML(my format) ; false = not;
	 */
	public boolean checkKmlFormat() {
		if (jsonObject.has(KML)) {
			if (jsonObject.getJSONObject(KML).has(DOCUMENT)) {
				document = jsonObject.getJSONObject(KML)
						.getJSONObject(DOCUMENT);
				if (document.has(STYLE) && document.has(FOLDER)) {
					return true;
				}
			}
		}
		return false;
	}// end of hasStyleAndFolder

	/**
	 * get data from kml description
	 * 
	 * @return String
	 */
	public String getDescription() {
		if (hasDocument()) {
			try {
				String description = document.getJSONObject(PLACEMARK)
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
	 * @param index
	 * @return id: kml-Document-Style-id
	 */
	public String getPolyStyleId(int index) {
		return document.getJSONArray(STYLE).getJSONObject(index).getString(ID);
	}// end of getPolyStyleId

	/**
	 * @param index
	 * @return ARGB color
	 */
	public int getPolyColor(int index) {
		JSONObject polyStyle = document.getJSONArray(STYLE)
				.getJSONObject(index).getJSONObject(POLYSTYLE);
		String abgr = polyStyle.getString(COLOR);
		return kmlColorToARGB(abgr);
	}// end of getPoltColor

	/**
	 * @param index
	 * @return ARGB color
	 */
	public int getLineColor(int index) {
		JSONObject LineStyle = document.getJSONArray(STYLE)
				.getJSONObject(index).getJSONObject(LINESTYLE);
		String abgr = LineStyle.getString(COLOR);
		return kmlColorToARGB(abgr);
	}// end of getPoltColor

	/**
	 * 
	 * @param index
	 * @return width
	 */
	public float getLineWidth(int index) {
		JSONObject LineStyle = document.getJSONArray(STYLE)
				.getJSONObject(index).getJSONObject(LINESTYLE);
		int width = LineStyle.getInt(WIDTH);
		return Float.valueOf(width);
	}// end of getLineWidth

	/**
	 * length從1開始算
	 */
	public int getStyleLength() {
		return document.getJSONArray(STYLE).length();
	}// end of getJsonObejctLength

	/**folder-placeMark
	 * length從1開始算
	 */
	public int getPlaceMarkLength() {
		return document.getJSONObject(FOLDER).getJSONArray(PLACEMARK).length();
	}// end of getJsonObejctLength
	
	
	/**
	 * @param index
	 * @return ArrayList
	 */
	public ArrayList<LatLng> getCoordinates(int index) {
		String coordinates = document.getJSONObject(FOLDER)
				.getJSONArray(PLACEMARK).getJSONObject(index)
				.getJSONObject(MULTI_GEOMETRY).getJSONObject(POLYGON)
				.getJSONObject(OUTER_BOUNDARY_IS).getJSONObject(LINEAR_RING)
				.getString(COORDINATES);

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
	}// end of getCoordinates
	
	/**
	 * 用來連接LatLng和polyStyle
	 * @param index
	 * @return
	 */
	public String getStyleUrl(int index){
		String styleUrl = document.getJSONObject(FOLDER)
				.getJSONArray(PLACEMARK).getJSONObject(index)
				.getString(STYLE_URL);
		return styleUrl.substring(1);
	}// end of getStleUrl
	
	/**
	 * 
	 * @param index
	 * @return
	 */
	public String getPlaceMarkName(int index){
		String folderName = document.getJSONObject(FOLDER)
				.getJSONArray(PLACEMARK).getJSONObject(index)
				.getString(NAME);
		return folderName;
	}
	
	// =============================================================priavteMethodsing
	/**
	 * translate ABGR to ARGB
	 * 
	 * @param abgr
	 * @return
	 */
	private int kmlColorToARGB(String abgr) {
		String stringAlpha = abgr.substring(0, 2);
		String strinfBlue = abgr.substring(2, 4);
		String stringGreen = abgr.substring(4, 6);
		String strinfRed = abgr.substring(6);

		// 主要是透過parseColor將StringARGB轉為int
		int argb = Color.parseColor("#" + stringAlpha + strinfRed + stringGreen
				+ strinfBlue);
		return argb;
	}// end of kmlColorToARGB
		// =============================================================priavteMethodsed

}// end of parseKmlString

