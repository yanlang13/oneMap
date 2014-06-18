package com.example.onemap;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NONE;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_TERRAIN;
import com.example.onemap.MapTools;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements ConnectionCallbacks,
		LocationListener, OnMyLocationButtonClickListener,
		OnConnectionFailedListener {
	private MapTools mapTools = new MapTools();
	private ProgressDialog progressDialog;
	private GoogleMap map;

	private final String THE_LAST_CP = "TheLastCameraPosition";

	private LocationClient mLocationClient;
	// 處理LocationClient的品質
	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(5000).setFastestInterval(16)
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	private EditText etSearch; // 接收輸入的地址

	// 有關sliding menu
	private DrawerLayout drawerLayout;
	private ListView drawerList; // listView的view
	private ActionBarDrawerToggle actionBarDrawerToggle; // drawerLayout的listener

	private DefaultSettings ds; // 存取各種基本設定

	// 測試用，input的polygon file name
	private String INPUT_KML_FILE = "twopolygon.kml";

	// ====================================================================Declared

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ds = new DefaultSettings(MainActivity.this);
		setContentView(R.layout.single_maps);
		progressDialog = new ProgressDialog(this);
		setLeftDrawer();
	}// end of onCreate

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		// 寫入設定的actionBarDrawerToggle
		actionBarDrawerToggle.syncState();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpSingleMapIfNeeded(ds.getBaseMap());

		setUpLocationClientIfNeeded();
	}// end of onResume()

	@Override
	public void onPause() {
		super.onPause();
		if (mLocationClient != null) {
			mLocationClient.disconnect();
		}

	}// end of onPause()

	@Override
	protected void onStop() {
		super.onStop();
		mapTools.saveTheLastCameraPosition(getApplicationContext(), map,
				THE_LAST_CP);
	}// end of onStop

	// onConfigurationChanged
	// 是指狀態改變時(ex:跳出鍵盤、螢幕旋轉等)，會導致activity被destory後再重新設定actionBarDrawerToggle的
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		actionBarDrawerToggle.onConfigurationChanged(newConfig);
	}

	// ====================================================================onCreating
	private void setLeftDrawer() {
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerList = (ListView) findViewById(R.id.left_drawer);
		// START=>Push object to x-axis position at the start of its container,
		// not changing its size.
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		drawerList.setAdapter(new DrawerArrayAdapter(this, DrawerList.LIST));
		drawerList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {// layoutSetting
					startActivity(new Intent(MainActivity.this,
							LayersManage.class));
				} else if (position == 1) {
					startActivity(new Intent(MainActivity.this,
							LayersList.class));
				}
				drawerLayout.closeDrawers();
			}
		});// end of drawerList.setOnItemClickListener

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon/*
		// ic_drawer是取代up的drawer
		// 因為actionBarDrawerToggle已經implement了
		// DrawerLayout.DrawerListener，所以可以override DrawerListener的method
		// ic_drawer的顯示位置，是交由.png檔所決定
		actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerOpened(View drawerView) {
				// 因為drawer所以改變了menu，會再call onCreateOptionsMenu
				invalidateOptionsMenu();
			}

			public void onDrawerClosed(View drawerView) {
				// 因為drawer所以改變了menu，會再call onCreateOptionsMenu
				invalidateOptionsMenu();
			}
		};
		// 讀入actionBarDrawerToggle
		drawerLayout.setDrawerListener(actionBarDrawerToggle);

		// 未使用，看起來是確保selectItem不會出錯
		// if (savedInstanceState == null) {
		// selectItem(0);
		// }
	}// end of setLeftDrawer()

	// ====================================================================onCreated

	// ====================================================================onResuming

	/**
	 * @param position
	 *            選擇哪一個base map。
	 */
	private void setUpSingleMapIfNeeded(int position) {
		if (map == null) {
			map = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.single_OneMap)).getMap();
			if (map != null) {
				mapTools.callTheLastCameraPosition(getApplicationContext(),
						map, THE_LAST_CP);
				setBaseMap(map, position);

				// TODO 開啟DATABASE把DISPLAY TRUE的KML放入地圖
			}
		}
		map.setMyLocationEnabled(true);
		map.setOnMyLocationButtonClickListener(this);
	}

	private void setUpLocationClientIfNeeded() { // call from onResume
		if (mLocationClient == null) {
			// ConnectionCallback and OnConnectionFailedListener
			mLocationClient = new LocationClient(getApplicationContext(), this,
					this);
		}
	}// end of setUpLocationClientIfNeeded()

	/**
	 * 接收databases的mapTitle來改變地圖的layoutType
	 */
	private void setBaseMap(GoogleMap gMap, int position) {
		if (position == 1) {
			gMap.setMapType(MAP_TYPE_NONE);
		} else if (position == 2) {
			gMap.setMapType(MAP_TYPE_NORMAL);
		} else if (position == 3) {
			gMap.setMapType(MAP_TYPE_HYBRID);
		} else if (position == 4) {
			gMap.setMapType(MAP_TYPE_SATELLITE);
		} else if (position == 5) {
			gMap.setMapType(MAP_TYPE_TERRAIN);
		} else {
			Log.d("mdg", "MainActivity class" + "googleMap Error");
		}
	}// end of setMapLayoutType

	// ====================================================================onResumed

	// ====================================================================onStoping

	// ====================================================================onStopinged

	// ====================================================================MenuING
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	} // end of onCreateOptionsMenu

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
		boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
		menu.setGroupVisible(R.id.all_actions, !drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		// if it returns true, then it has handled the app icon touch event

		if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		} else if (id == R.id.action_search) {
			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
					MainActivity.this);
			LayoutInflater inflater = this.getLayoutInflater();
			View dialogView = inflater.inflate(R.layout.search_action, null);
			// 取得輸入的地址
			etSearch = (EditText) dialogView
					.findViewById(R.id.et_search_address_input);
			alertBuilder.setView(dialogView);
			alertBuilder.setPositiveButton("Search",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Ensure that a Geocoder services is available
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD
									&& Geocoder.isPresent()) {
								new GetAddressTask().execute(etSearch.getText()
										.toString());
							}
						}
					});

			alertBuilder.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
			AlertDialog alertDialog = alertBuilder.create();
			alertDialog.setCanceledOnTouchOutside(false);
			alertDialog.show();
			return true;
		} else if (id == R.id.action_kml_to_map) {
			startActivity(new Intent(MainActivity.this, ListSdCard.class));
			return true;
		}// end of if id == ?
		return super.onOptionsItemSelected(item);
	}// end of onOptionsItemSelected

	// ====================================================================MenuED

	// ====================================================================Classing
	/**
	 * 取得地址的座標，然後moveCameraTo
	 * 
	 * @PARM String address
	 */
	private class GetAddressTask extends TaskAddress {
		@Override
		protected void onPreExecute() {
			progressDialog.show();
			progressDialog.setCanceledOnTouchOutside(false);
		}

		@Override
		protected void onPostExecute(LatLngBounds bounds) {
			if (bounds != null) {
				// bounds, pidding
				String snippet = etSearch.getText().toString();
				LatLng position = bounds.getCenter();
				map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
				mapTools.displayBoundMarker(map, position, snippet);
				progressDialog.dismiss();
			} else {
				Toast.makeText(MainActivity.this, "wrong address format",
						Toast.LENGTH_SHORT).show();
				progressDialog.dismiss();
			}
		}// end of onPostExecute
	}// end of GetAddressTask

	/**
	 * parsing kml file and display in map
	 * 
	 * @PARM context
	 * @PARM kml file
	 */
	private class AddInputTask extends TaskAddInput {
		@Override
		protected void onPreExecute() {
			progressDialog.show();
		}

		@Override
		protected void onPostExecute(Object object) {
			if (object == null) {
				Toast.makeText(MainActivity.this, "check your input source",
						Toast.LENGTH_LONG).show();
			} else {
				PolygonOptions po = (PolygonOptions) object;

				// TODO 不要clear
				map.clear();
				map.addPolygon(po);  

				final PolygonOptions po1 = po;
				// TODO 點擊POLYGON時，顯示INFOWINDOW
				map.setOnMapLongClickListener(new OnMapLongClickListener() {
					@Override
					public void onMapLongClick(LatLng point) {
						if (mapTools.containsInPolygon(point, po1)) {
							Toast.makeText(MainActivity.this,
									"point in polygon", Toast.LENGTH_SHORT)
									.show();
							map.setInfoWindowAdapter(new AdapterPolygonInfoWindow(
									getLayoutInflater()));
							MarkerOptions mo = new MarkerOptions();
							mo.position(point);
							map.addMarker(mo).showInfoWindow();
						} else {
							Toast.makeText(MainActivity.this,
									"point isn't in polygon",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
			progressDialog.dismiss();
		}
	}// end of AddInputTask

	// ====================================================================Classed

	// ====================================================================MethodING

	// ====================================================================MethodED

	// ====================================================================Overriding
	@Override
	// ConnectionCallbacks
	public void onConnected(Bundle arg0) {
		// this 是指LocationListener
		mLocationClient.requestLocationUpdates(REQUEST, this);
	}// end of onConnected

	@Override
	// ConnectionCallbacks
	public void onDisconnected() {
		Toast.makeText(getApplication(), "LocationClient is disconnected",
				Toast.LENGTH_SHORT).show();
	}// end of onDisconnected

	@Override
	// LocationListener
	public void onLocationChanged(Location locaion) {
	}// end of on onLocationChanged

	@Override
	// OnMyLocationButtonClickListener
	public boolean onMyLocationButtonClick() {
		// The default behavior is for the camera move such that it is centered
		// on the user location.
		// 先確認GPS和network定位的服務有無開啟，locationManager也是另一種開啟GPS定位的方法
		LocationManager status = (LocationManager) getApplicationContext()
				.getSystemService(Context.LOCATION_SERVICE);
		if (status.isProviderEnabled(LocationManager.GPS_PROVIDER)
				|| status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			// 連接服務，等待 onConnected時再將locationRequest的設定值交出
			mLocationClient.connect();
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					MainActivity.this);
			builder.setTitle("Waring");
			builder.setMessage("GPS services are turned off on your device. Do you want to go to yout Location settings now?");
			builder.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 這裡的settings是android.provider.Settings
							startActivity(new Intent(
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

	@Override
	// OnConnectionFailedListener
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.d("mdb", "in onConnectionFailed");
	}// end of onConnectionFailed

	// ====================================================================OverrideD
}// end of MainActivity

