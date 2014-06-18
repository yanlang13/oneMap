package com.example.onemap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;

import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;

public class classOnMyOnMyLocationButtonClickListener implements
		OnMyLocationButtonClickListener {
	private Context context;
	private LocationClient locationClient;

	public classOnMyOnMyLocationButtonClickListener(Context context,
			LocationClient locationClient) {
		this.context = context;
		this.locationClient = locationClient;
	}

	@Override
	// OnMyLocationButtonClickListener
	public boolean onMyLocationButtonClick() {
		// The default behavior is for the camera move such that it is centered
		// on the user location.
		// 先確認GPS和network定位的服務有無開啟，locationManager也是另一種開啟GPS定位的方法
		LocationManager status = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		if (status.isProviderEnabled(LocationManager.GPS_PROVIDER)
				|| status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			// 連接服務，等待 onConnected時再將locationRequest的設定值交出
			locationClient.connect();
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("Waring");
			builder.setMessage("GPS services are turned off on your device. Do you want to go to yout Location settings now?");
			builder.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 這裡的settings是android.provider.Settings
							context.startActivity(new Intent(
									Settings.ACTION_LOCATION_SOURCE_SETTINGS));
						}
					});
			builder.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
			AlertDialog alertDialog = builder.create();
			alertDialog.setCanceledOnTouchOutside(false);
			alertDialog.show();
		}
		// Return false so that we don't consume the event and the default
		// behavior still occurs
		// (the camera animates to the user's current position).
		return false;
	}// end of onMyLocationButtonClick()

}
