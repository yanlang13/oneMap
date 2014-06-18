package com.example.onemap;

import java.util.HashMap;

import android.content.Context;

import com.example.onemap.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

/**
 * context:作用中的activity，兩個goglemap
 */
public class SyncTools {
	public final GoogleMap upperMap;
	public final GoogleMap lowerMap;
	public final Context context;
	private boolean upperMapStopper = true;
	private boolean lowerMapStopper = true;
	private MapTools mapTools = new MapTools();

	// user的點擊位置，放到HashMap中，目標是一次只顯示一個。
	private HashMap<String, Circle> userCircle = new HashMap<String, Circle>();

	public SyncTools(Context context, GoogleMap upperMap, GoogleMap lowerMap) {
		this.context = context;
		this.upperMap = upperMap;
		this.lowerMap = lowerMap;
	}// end of SyncTools

	/**
	 * 同步移動cameraPosition
	 */
	public void syncTwoMapCameraPosition() { // call from setUpMapIfNeeded()
		upperMap.setOnCameraChangeListener(new OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition cameraPosition) {
				if (upperMapStopper) {
					// 停止lowerMap移動(不停止的話，系統會以為使用者一直操作)
					lowerMapStopper = false;
					lowerMap.moveCamera(CameraUpdateFactory
							.newCameraPosition(cameraPosition));
				}
				// 開啟upperMap的移動功能，讓下次移動能改變lower的位置
				upperMapStopper = true;
			}
		}); // end of upperMap.setOnCameraChangeListener

		lowerMap.setOnCameraChangeListener(new OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition cameraPosition) {
				if (lowerMapStopper) {
					upperMapStopper = false;
					upperMap.moveCamera(CameraUpdateFactory
							.newCameraPosition(cameraPosition));
				}
				lowerMapStopper = true;
			}
		});
	}// end of syncTwoMapCameraPosition()

	/**
	 * map onMapClick時，顯示點擊位置
	 */
	public void syncDisplayUserClicked() {
		upperMap.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng geoPoint) {
				// 1.顯示點擊位置，geoPoint為點擊位置
				createCircle(upperMap, geoPoint);
				createCircle(lowerMap, geoPoint);
			}
		});
		lowerMap.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng geoPoint) {
				// 1.顯示點擊位置
				createCircle(upperMap, geoPoint);
				createCircle(lowerMap, geoPoint);
			}
		});
	}// end of displayUserClicked()

	/**
	 * 在user點擊位置，顯示圓圈。透過location class讓這個circle不至於失控。
	 * 
	 * @param map
	 * @param geoPoint
	 */
	private void createCircle(GoogleMap map, LatLng geoPoint) {

		float viewDistance = mapTools.getViewRegionHorizontalDistance(map);
		double radius = viewDistance / 1000;

		CircleOptions circleOptions = new CircleOptions();
		circleOptions.center(geoPoint);
		circleOptions.radius(radius);
		// 要用getResources().getColor(R.color...)，才能正確獨到顏色。
		// 只用R.color不會顯示錯誤，但不會有顏色。
		circleOptions.fillColor(context.getResources().getColor(
				R.color.lava_red));
		circleOptions.strokeColor(context.getResources().getColor(
				R.color.lava_red));

		String key = String.valueOf(map.hashCode());
		if (userCircle.containsKey(key)) {
			userCircle.get(key).remove();
			Circle tempCircle = map.addCircle(circleOptions);
			userCircle.put(key, tempCircle);
		} else {
			Circle tempCircle = map.addCircle(circleOptions);
			userCircle.put(key, tempCircle);
		}
	}
}// end of syncTwoMap