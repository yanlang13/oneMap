package com.example.multiplemaps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.AsyncTask;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * 輸入地址回傳CamerPosition，四個參數分別為context, maxZoomLevel, minZoomLevel, addressInput
 */
public class TaskAddress extends AsyncTask<Object, Void, LatLngBounds> {
	@Override
	protected LatLngBounds doInBackground(Object... params) {
		// 需要return null or CameraPosition
		// 用params[i]來抓取輸入的值
		String addressInput = (String) params[0];

		try {
			
			String address = URLEncoder.encode(addressInput, "UTF-8");
			final String urlStr = String
					.format("http://maps.googleapis.com/maps/api/geocode/json?address=%s&sensor=false",
							address);
			// 定義url
			URL url = new URL(urlStr);
			// 連線
			URLConnection connection = url.openConnection();

			// 將url回傳的結果，寫成StringBuilder
			BufferedReader buffer = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			StringBuilder stringBuilder = new StringBuilder();
			String line;
			while ((line = buffer.readLine()) != null) {
				stringBuilder.append(line);
			}
			//轉成JSONObject format
			JSONObject data = new JSONObject(stringBuilder.toString());
			
			//取值，array(results)下的第一個內容下的geometry中的viewport-southwest
			JSONObject jSouthwest = data.getJSONArray("results")
					.getJSONObject(0).getJSONObject("geometry")
					.getJSONObject("viewport").getJSONObject("southwest");
			
			
			JSONObject jNortheast = data.getJSONArray("results")
					.getJSONObject(0).getJSONObject("geometry")
					.getJSONObject("viewport").getJSONObject("northeast");
			
			//轉存為latLng是為了放入bounds，再return
			LatLng southwest = new LatLng(jSouthwest.getDouble("lat"),
					jSouthwest.getDouble("lng"));
			LatLng northeast = new LatLng(jNortheast.getDouble("lat"),
					jNortheast.getDouble("lng"));

			LatLngBounds bounds = new LatLngBounds(southwest, northeast);
			return bounds;

		} catch (UnsupportedEncodingException e) {// URLEncoder
			e.printStackTrace();
		} catch (MalformedURLException e) { // URL
			e.printStackTrace();
		} catch (IOException e) { // URLConnection
			e.printStackTrace();
		} catch (JSONException e) {// JSONObject
			e.printStackTrace();
		}
		return null;
	}// end of doInBackground
}// end of GetAdressTask