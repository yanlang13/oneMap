package com.example.multiplemaps;

import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

/**
 * 點擊polygon顯示infoWindows
 * @param layoutInflater
 * 
 */
public class AdapterPolygonInfoWindow implements InfoWindowAdapter {
	private LayoutInflater layoutInflater;

	public AdapterPolygonInfoWindow(LayoutInflater layoutInflater) {
		this.layoutInflater = layoutInflater;
	}

	public View getInfoWindow(Marker marker) {
		// 這邊return view的話，要完全自製infoWindow (call first)
		return null;
	}

	public View getInfoContents(Marker marker) {
		// 這邊return view的話，會有default的設計(白底,called if getInfoWodow return null
		View v = layoutInflater.inflate(R.layout.drawer_list_item, null);
		//TODO 一次只顯示一個marker，最好是marker消失，然後polygon置中顯示
		return v;
	}
}// end of InfoWindowAdapter
