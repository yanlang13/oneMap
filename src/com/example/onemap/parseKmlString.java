package com.example.onemap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.google.android.gms.maps.model.LatLng;
import android.graphics.Color;

/**
 * 將KML的String轉成JSON formats
 * 
 */
public class ParseKmlString {
	private JSONObject jsonObject;
	private JSONObject document;
	private JSONObject placeMark;
	private JSONArray optPlaceMark;
	private JSONObject style;
	private JSONArray optStyle;
	private JSONObject styleMap;
	private JSONArray optStyleMap;

	private static final String KML = "kml";
	private static final String DOCUMENT = "Document";

	private static final String STYLE = "Style";
	private static final String ID = "id";
	private static final String POLYSTYLE = "PolyStyle";
	private static final String COLOR_MODE = "colorMode";
	private static final String COLOR = "color";
	private static final String NO_COLOR = "no color";
	private static final String LINESTYLE = "LineStyle";
	private static final String WIDTH = "width";

	private static final String STYLE_MAP = "StyleMap";
	private static final String PAIR = "Pair";

	private static final String FOLDER = "Folder";
	private static final String PLACEMARK = "Placemark";
	private static final String MULTI_GEOMETRY = "MultiGeometry";
	private static final String POLYGON = "Polygon";
	private static final String OUTER_BOUNDARY_IS = "outerBoundaryIs";
	private static final String LINEAR_RING = "LinearRing";
	private static final String COORDINATES = "coordinates";
	private static final String STYLE_URL = "styleUrl";

	private static final String NAME = "name";

	private boolean kmlHasStyleMap = true;
	private boolean styleIsJSONObject = false;
	private boolean styleMapIsJSONObject = false;
	private boolean placeMarkIsJSONObject = false;

	public ParseKmlString(String kmlString) {
		// github下載的JSONObject
		jsonObject = XML.toJSONObject(kmlString);
		checkKmlFormat();
	}// end of parseKmlString

	/**
	 * 檢查kml file kml-Document-Style & Folder 檢查kml file
	 * kml-Folder-Document-Style & Folder
	 * 
	 * @return true = KML(my format) ; false = not;
	 */
	public boolean checkKmlFormat() {
		if (jsonObject.has(KML)) {
			if (jsonObject.getJSONObject(KML).has(DOCUMENT)) {
				// KML- document- style
				document = jsonObject.getJSONObject(KML)
						.getJSONObject(DOCUMENT);

				optStyle = document.optJSONArray(STYLE);
				if (optStyle == null) {
					style = document.getJSONObject(STYLE);
				}

				// 如果styleMap is not jsonArray 才做styleMap
				optStyleMap = document.optJSONArray(STYLE_MAP);
				if (optStyleMap == null) {
					if (document.optJSONObject(STYLE_MAP) != null) {
						styleMap = document.getJSONObject(STYLE_MAP);
					} else {
						kmlHasStyleMap = false;
					}
				}

				// KML - document - folder -placeMark or document - placeMark
				if (document.has(FOLDER)) {
					if (document.getJSONObject(FOLDER).getJSONArray(PLACEMARK) != null) {
						optPlaceMark = document.getJSONObject(FOLDER)
								.getJSONArray(PLACEMARK);
					}
				} else {
					if (document.optJSONArray(PLACEMARK) != null) {
						optPlaceMark = document.optJSONArray(PLACEMARK);
					} else {
						placeMarkIsJSONObject = true;
						placeMark = document.getJSONObject(PLACEMARK);
					}

				}
				return true;
			} else if (jsonObject.getJSONObject(KML).has(FOLDER)) {
				// KML- folder - document - style, styleMap and placeMark
				document = jsonObject.getJSONObject(KML).getJSONObject(FOLDER)
						.getJSONObject(DOCUMENT);

				optPlaceMark = document.getJSONArray(PLACEMARK);

				optStyle = document.getJSONArray(STYLE);
				optStyle = document.optJSONArray(STYLE);

				optStyleMap = document.optJSONArray(STYLE_MAP);

				// 如果styleMap is not jsonArray 才做styleMap
				if (optStyleMap == null) {
					styleMap = document.getJSONObject(STYLE_MAP);
				}
				return true;
			}
		}
		return false;
	}// end of hasStyleAndFolder

	/**
	 * 對應到DataBase的ST_FIELD_STYLE_NAME
	 * 
	 * @param index
	 * @return id: kml-Document-Style-id
	 */
	public String getPolyStyleId(int index) {
		int length = getStyleLength();
		if (length == 1) {
			return style.getString(ID);
		} else {
			return optStyle.getJSONObject(index).getString(ID);
		}
	}// end of getPolyStyleId

	/**
	 * @param index
	 * @return ARGB color
	 */
	public int getPolyColor(int index) {
		String abgr;
		if (styleIsJSONObject) {
			JSONObject polyStyle = style.getJSONObject(POLYSTYLE);
			abgr = polyStyle.getString(COLOR);
		} else {
			JSONObject polyStyle = optStyle.getJSONObject(index).getJSONObject(
					POLYSTYLE);
			abgr = polyStyle.getString(COLOR);
		}

		return kmlColorToARGB(abgr);
	}// end of getPoltColor

	public String getColorMode(int index) {
		String colorMode = null;
		if (styleIsJSONObject) {
			JSONObject polyStyle = style.getJSONObject(POLYSTYLE);
			colorMode = polyStyle.optString(COLOR_MODE);
		}
		return colorMode;

	}// end of getColorMode

	/**
	 * @param index
	 * @return ARGB color
	 */
	public int getLineColor(int index) {
		String abgr;
		if (styleIsJSONObject) {
			// 沒有LINESTYLE
			if (style.optJSONObject(LINESTYLE) == null) {
				abgr = "64FFFFFF";
			} else {
				String color = style.optJSONObject(LINESTYLE).optString(COLOR,
						NO_COLOR);
				if (color.equals(NO_COLOR)) {
					abgr = "64FFFFFF";
				} else {
					JSONObject LineStyle = style.getJSONObject(LINESTYLE);
					abgr = LineStyle.getString(COLOR);
				}
			}
		} else {

			// 沒有LINESTYLE
			if (optStyle.getJSONObject(index).optJSONObject(LINESTYLE) == null) {
				abgr = "64FFFFFF";
			} else {
				String color = optStyle.getJSONObject(index)
						.optJSONObject(LINESTYLE).optString(COLOR, NO_COLOR);
				if (color.equals(NO_COLOR)) {
					abgr = "64FFFFFF";
				} else {
					JSONObject LineStyle = optStyle.getJSONObject(index)
							.getJSONObject(LINESTYLE);

					abgr = LineStyle.getString(COLOR);
				}
			}
		}
		return kmlColorToARGB(abgr);
	}// end of getPoltColor

	/**
	 * @param index
	 * @return width
	 */
	public float getLineWidth(int index) {
		int width;
		if (styleIsJSONObject) {
			if (style.optJSONObject(LINESTYLE) == null) {
				width = 0;
			} else {
				JSONObject LineStyle = style.getJSONObject(LINESTYLE);
				width = LineStyle.getInt(WIDTH);
			}
		} else {

			if (optStyle.getJSONObject(index).optJSONObject(LINESTYLE) == null) {
				width = 0;
			} else {
				JSONObject LineStyle = optStyle.getJSONObject(index)
						.getJSONObject(LINESTYLE);
				width = LineStyle.getInt(WIDTH);
			}
		}
		return Float.valueOf(width);
	}// end of getLineWidth

	/**
	 * getStyleUrl後接著做
	 * 
	 * @param placeMarkStyleUrl
	 * @return
	 */
	public String transToStyleUrl(String placeMarkStyleUrl) {
		int length = getStyleMapLength();
		String result = placeMarkStyleUrl;
		// if name: IF

		if (kmlHasStyleMap) {
			IF: if (styleMapIsJSONObject) {
				// 先做如果sytleMap不是陣列的情況
				String styleMapId = styleMap.getString(ID);
				if (placeMarkStyleUrl.equals(styleMapId)) {
					String styleUrl = styleMap.getJSONArray(PAIR)
							.getJSONObject(0).getString(STYLE_URL);
					result = styleUrl.substring(1);
				}
			} else {
				// styleMap如果是陣列的話
				for (int index = 0; index < length; index++) {
					String styleMapId = document.getJSONArray(STYLE_MAP)
							.getJSONObject(index).getString(ID);
					if (placeMarkStyleUrl.equals(styleMapId)) {
						String styleUrl = document.getJSONArray(STYLE_MAP)
								.getJSONObject(index).getJSONArray(PAIR)
								.getJSONObject(0).getString(STYLE_URL);
						result = styleUrl.substring(1);
						break IF;
					}
				}
			}
		}
		return result;
	}

	/**
	 * length從1開始算
	 */
	public int getStyleMapLength() {
		if (optStyleMap == null) {
			styleMapIsJSONObject = true;
			return 0;
		}
		return optStyleMap.length();
	}

	/**
	 * length從1開始算
	 */
	public int getStyleLength() {
		if (optStyle == null) {
			styleIsJSONObject = true;
			return 1;
		}
		return optStyle.length();
	}// end of getJsonObejctLength

	/**
	 * folder-placeMark length從1開始算
	 */
	public int getPlaceMarkLength() {
		if (optPlaceMark == null) {
			return 0;
		}
		return optPlaceMark.length();
	}// end of getJsonObejctLength

	/**
	 * @param index
	 * @return ArrayList
	 */
	public ArrayList<LatLng> getCoordinates(int index) {
		String coordinates;
		if (placeMarkIsJSONObject) {
			coordinates = placeMark.getJSONObject(POLYGON)
					.getJSONObject(OUTER_BOUNDARY_IS)
					.getJSONObject(LINEAR_RING).getString(COORDINATES);
		} else {
			if (optPlaceMark.getJSONObject(index).optJSONObject(MULTI_GEOMETRY) == null) {
				coordinates = optPlaceMark.getJSONObject(index)
						.getJSONObject(POLYGON)
						.getJSONObject(OUTER_BOUNDARY_IS)
						.getJSONObject(LINEAR_RING).getString(COORDINATES);
			} else {
				coordinates = optPlaceMark.getJSONObject(index)
						.getJSONObject(MULTI_GEOMETRY).getJSONObject(POLYGON)
						.getJSONObject(OUTER_BOUNDARY_IS)
						.getJSONObject(LINEAR_RING).getString(COORDINATES);
			}
		}
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
	 * 只取出string，未刪除z值
	 * 
	 * @param index
	 * @return
	 */
	public String getCoordinateString(int index) {
		String coordinatesString;
		if (placeMarkIsJSONObject) {
			coordinatesString = placeMark.getJSONObject(POLYGON)
					.getJSONObject(OUTER_BOUNDARY_IS)
					.getJSONObject(LINEAR_RING).getString(COORDINATES);
		} else {

			if (optPlaceMark.getJSONObject(index).optJSONObject(MULTI_GEOMETRY) == null) {
				coordinatesString = optPlaceMark.getJSONObject(index)
						.getJSONObject(POLYGON)
						.getJSONObject(OUTER_BOUNDARY_IS)
						.getJSONObject(LINEAR_RING).getString(COORDINATES);
			} else {
				coordinatesString = optPlaceMark.getJSONObject(index)
						.getJSONObject(MULTI_GEOMETRY).getJSONObject(POLYGON)
						.getJSONObject(OUTER_BOUNDARY_IS)
						.getJSONObject(LINEAR_RING).getString(COORDINATES);
			}
		}
		return coordinatesString;
	}

	/**
	 * 用來連接LatLng和polyStyle，getSylteUrl後必做transToStyleUrl
	 * 
	 * @param index
	 * @return
	 */
	public String getStyleUrl(int index) {
		String styleUrl;
		if (placeMarkIsJSONObject) {
			styleUrl = placeMark.getString(STYLE_URL);
		} else {
			styleUrl = optPlaceMark.getJSONObject(index).getString(STYLE_URL);
		}
		return styleUrl.substring(1);
	}// end of getStleUrl

	/**
	 * 
	 * @param index
	 * @return
	 */
	public String getPlaceMarkName(int index) {
		String folderName;
		if (placeMarkIsJSONObject) {
			folderName = placeMark.optString(NAME);
		} else {
			folderName = optPlaceMark.getJSONObject(index).optString(NAME);
		}
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

