package com.example.onemap;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NONE;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_TERRAIN;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.security.auth.PrivateCredentialPermission;

import org.apache.commons.io.FileUtils;

import com.example.onemap.MapTools;
import com.google.android.gms.ads.a;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

	PolygonOptions PolygonToMap;

	List<PolygonOptions> polygonList;

	List<PolygonOptions> bigPO;
	List<PolygonOptions> medPO;
	List<PolygonOptions> smaPO;

	// =========================================================================
	// ============ ACTIVITY LIFECYCLE =========================================
	// =========================================================================

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ds = new DefaultSettings(MainActivity.this);
		setContentView(R.layout.single_maps);
		progressDialog = new ProgressDialog(this);

		// MainActivity.this.deleteDatabase("oneMaps.db");
		setLeftDrawer();
		bigPO = new ArrayList<PolygonOptions>();
		medPO = new ArrayList<PolygonOptions>();
		smaPO = new ArrayList<PolygonOptions>();

		PolygonToMap = new PolygonOptions();
		polygonList = new ArrayList<PolygonOptions>();

		// new GetPolygonFromDB().execute(getApplicationContext());

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

	// =========================================================================
	// =========== ONCREATE METHODS ============================================
	// =========================================================================
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

	// =========================================================================
	// ======= ONRESUME METHODS=================================================
	// =========================================================================
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

			}// end of if

			map.setMyLocationEnabled(true);
			map.setOnMyLocationButtonClickListener(this);

			// TODO 持續測試THREAD

		}// end of if
	}// end of setUpSingleMapIfNeeded

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

	// =========================================================================
	// ========== MENU =========================================================
	// =========================================================================
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
		} else if (id == R.id.action_test) {
			new DrawByBitmap().execute(getApplicationContext());
			// new GetPolygonFromDB().execute(getApplicationContext());
			return true;
		}// end of if id == ?
		return super.onOptionsItemSelected(item);
	}// end of onOptionsItemSelected

	// =========================================================================
	// ======== ASYNCTACK CLASS ================================================
	// =========================================================================
	private class DrawByBitmap extends TaskDrawLayerByBitmap {
		@Override
		protected void onPreExecute() {
			progressDialog.show();
			progressDialog.setCanceledOnTouchOutside(false);
		}

		@Override
		protected void onPostExecute(HashMap<String, ArrayList<LatLng>> layers) {
			Iterator<String> iterator = layers.keySet().iterator();
			Projection projection = map.getProjection();
			VisibleRegion vr = map.getProjection().getVisibleRegion();
			Point farLeft = projection.toScreenLocation(vr.farLeft);
			Point nearRight = projection.toScreenLocation(vr.nearRight);
			double width = Math.abs(farLeft.x - nearRight.x);
			double length = Math.abs(nearRight.y - farLeft.y);

			Bitmap bitmap = Bitmap.createBitmap((int) width, (int) length,
					Bitmap.Config.ARGB_8888);
			Paint paint = new Paint();
			paint.setColor(Color.BLACK);
			paint.setStyle(Paint.Style.STROKE);// 設置空心
			Path path = new Path();

			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				ArrayList<LatLng> latlngs = layers.get(key);
				// TODO 確認BITMAP跟螢幕大小的關係，然後以此推算出座標點的位置。
				// Bitmap設定的大小，會影響path點座標
				for (int index = 0; index < latlngs.size(); index++) {
					// TODO 這邊如果點是在畫面外，就不會畫了。了解原因~
					Point point = projection.toScreenLocation(latlngs
							.get(index));
					if (index == 0) {
						path.moveTo(point.x, point.y);
					} else {
						// TODO 做判斷式，在畫面外就不要LINETO了
						path.lineTo(point.x, point.y);
					}
				}
				path.close();
			}

			Canvas canvas = new Canvas(bitmap);
			canvas.drawPath(path, paint);
			GroundOverlayOptions ground = new GroundOverlayOptions();
			ground.image(BitmapDescriptorFactory.fromBitmap(bitmap));
			ground.positionFromBounds(map.getProjection().getVisibleRegion().latLngBounds);
			map.addGroundOverlay(ground);
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}// end of if
		}// end of onPostExecute
	}// end of TaskDrawByBitmap

	/**
	 * @author acer
	 */
	private class GetPolygonFromDB extends TaskDataBaseToMap {
		@Override
		protected void onPreExecute() {
			progressDialog.show();
			progressDialog.setCanceledOnTouchOutside(false);
		}

		@Override
		protected void onPostExecute(HashMap<String, PolygonOptions> pos) {
			// TODO 究竟是每個task的stack大小、LOOP的stack大小、Latlng大小的問題?
			Log.d("mdb", "pos.size():" + pos.size());
			Iterator<String> iterator = pos.keySet().iterator();

			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				PolygonOptions options = pos.get(key);
				map.addPolygon(options);
				System.gc();
			}
			pos.clear();

			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}// end of if
		}// end of onPostExecute
	}// end of GetPolygonFromDB

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
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
			} else {
				Toast.makeText(MainActivity.this, "wrong address format",
						Toast.LENGTH_SHORT).show();
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
			}
		}// end of onPostExecute
	}// end of GetAddressTask

	// =========================================================================
	// ===== OVERRIDE ==========================================================
	// =========================================================================
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

	// =========================================================================
	// ======== ABOUT CANVAS CLASS =============================================
	// =========================================================================
	private void drawOnCanvas() {

	}// end of drawOnCanvas()

	private Bitmap decodeFile(File f) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);
			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 70;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale++;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}
}// end of MainActivity

