package com.example.onemap;

import java.util.HashMap;
import java.util.List;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

/**
 * @author acer 有關google map的各種tools
 */
public class MapTools {

	// using in save and call TheLastCameraPosition
	private static final String Lat = "Latitude";
	private static final String Lng = "Langitude";
	private static final String Tilt = "Tilt";
	private static final String Bearing = "Bearing";
	private static final String Zoom = "Zoom";

	private HashMap<String, Marker> centerMarker = new HashMap<String, Marker>();

	/**
	 * 將關閉時的CameraPosition存到SharedPreferences中。
	 * 
	 * @param context
	 * @param map
	 * @param SPName
	 */
	public void saveTheLastCameraPosition(Context context, GoogleMap map,
			String SPName) {
		CameraPosition cp = map.getCameraPosition();
		SharedPreferences sp = context.getSharedPreferences(SPName,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor spe = sp.edit();
		spe.putString(Lat, String.valueOf(cp.target.latitude));
		spe.putString(Lng, String.valueOf(cp.target.longitude));
		spe.putFloat(Tilt, cp.tilt);
		spe.putFloat(Bearing, cp.bearing);
		spe.putFloat(Zoom, cp.zoom);
		spe.commit();
	} // end of saveTheLastCameraPosition()

	/**
	 * 將SharedPreferences中的CameraPosition叫回。
	 */
	public void callTheLastCameraPosition(Context context, GoogleMap map,
			String SPName) { // call from setUpMapIfNeeded()
		SharedPreferences sp = context.getSharedPreferences(SPName,
				Context.MODE_PRIVATE);
		double latitude = Double.valueOf(sp.getString(Lat, "0"));
		double longitude = Double.valueOf(sp.getString(Lng, "0"));

		float tilt = sp.getFloat(Tilt, 0);
		float bearing = sp.getFloat(Bearing, 0);
		float zoom = sp.getFloat(Zoom, 0);
		LatLng target = new LatLng(latitude, longitude);
		CameraPosition cp = new CameraPosition(target, zoom, tilt, bearing);
		map.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
	}// end of callTheLastCameraPostion()

	/**
	 * 取得當下ViewRegin的兩邊距離
	 * 
	 * @param map
	 * @return float (m)
	 */
	public float getViewRegionHorizontalDistance(GoogleMap map) {
		// "left"是為lfetLocation命名
		Location leftLocation = new Location("left");
		// getProjection用來轉換螢幕座標(pixels)與地圖座標(LatLng)
		// getVisibleRegion(): four-sided polygon that is visible in a map's
		// camera.
		leftLocation
				.setLatitude(map.getProjection().getVisibleRegion().farLeft.latitude);
		leftLocation
				.setLongitude(map.getProjection().getVisibleRegion().farLeft.longitude);

		Location rightLocation = new Location("rifht");
		rightLocation
				.setLatitude(map.getProjection().getVisibleRegion().farRight.latitude);
		rightLocation
				.setLongitude(map.getProjection().getVisibleRegion().farRight.longitude);
		return leftLocation.distanceTo(rightLocation);
	}// end of getViewRegionHorizontalDistance

	/**
	 * 做中心marker
	 * 
	 * @param map
	 * @param position
	 * @param snippet
	 */
	public void displayBoundMarker(GoogleMap map, LatLng position,
			String snippet) {
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.snippet(snippet);
		markerOptions.title("Input:");
		markerOptions.infoWindowAnchor(0.5f, 0.5f);
		markerOptions.position(position);

		// 運用每個 obejct獨有的hashCode作為建立HashMap的Key，可以獨立存取值。
		String key = String.valueOf(map.hashCode());
		if (centerMarker.containsKey(key)) {
			centerMarker.get(key).remove();
			Marker tempMarker = map.addMarker(markerOptions);
			centerMarker.put(key, tempMarker);
		} else {
			Marker tempMarker = map.addMarker(markerOptions);
			centerMarker.put(key, tempMarker);
		}
	}// end of displayBoundMarker

	/**
	 * point in polygon
	 * 
	 * @param point
	 *            點擊的位置
	 * @param po
	 *            polygonOptions
	 * @return if point in polygon return true, else return false;
	 */
	public boolean containsInPolygon(LatLng point, PolygonOptions po) {
		boolean oddTransitions = false;
		List<LatLng> verticesPolygon = po.getPoints();
		float x = (float) point.latitude;
		float y = (float) point.longitude;

		int verticesSize = po.getPoints().size();
		float[] polyX = new float[verticesSize];
		float[] polyY = new float[verticesSize];

		for (int i = 0; i < verticesSize; i++) {
			polyX[i] = (float) verticesPolygon.get(i).latitude;
			polyY[i] = (float) verticesPolygon.get(i).longitude;
		}

		for (int i = 0, j = verticesSize - 1; i < verticesSize; j = i++) {
			if ((polyY[i] < y && polyY[j] >= y)
					|| (polyY[j] < y && polyY[i] >= y)
					&& (polyX[i] <= x || polyX[j] <= x)) {
				if (polyX[i] + (y - polyY[i]) / (polyY[j] - polyY[i])
						* (polyX[j] - polyX[i]) < x) {
					oddTransitions = !oddTransitions;
				}
			}
		}
		return oddTransitions;
	}// end of checkPointInPolygon
}
