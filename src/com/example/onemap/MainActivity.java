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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.security.auth.PrivateCredentialPermission;

import org.apache.commons.io.FileUtils;

import com.example.onemap.MapTools;
import com.google.android.gms.ads.a;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.a.d;
import com.google.android.gms.internal.ig;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.VisibleRegion;

import android.R.integer;
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
import android.view.SurfaceHolder;
import android.view.SurfaceView;
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

	// 用在canvas
	private Bitmap bitmap;

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

		PolygonToMap = new PolygonOptions();
		polygonList = new ArrayList<PolygonOptions>();
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

		// 簡單抓經緯座標，輔助canvas的設定。
		map.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng point) {
				Toast.makeText(getApplicationContext(), point + "",
						Toast.LENGTH_SHORT).show();
			}
		});
		map.setOnCameraChangeListener(new zoomCheckListener(map));

	}// end of onResume()

	@Override
	public void onPause() {
		super.onPause();
		// 關閉各種被開啟的服務
		if (mLocationClient != null) {
			mLocationClient.disconnect();
		}

	}// end of onPause()

	@Override
	protected void onStop() {
		super.onStop();
		// 儲存activity的內容
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
	 * 偵測zoom level有沒有縮放大於1，有的話就重畫canvas
	 * 
	 */
	class zoomCheckListener implements OnCameraChangeListener {
		private float zoomLevel;

		public zoomCheckListener(GoogleMap map) {
			this.zoomLevel = map.getCameraPosition().zoom;
		}

		@Override
		public void onCameraChange(CameraPosition position) {
			float change = position.zoom - zoomLevel;
			if (change > 1 || change < -1) {
				map.clear();
				new DrawByBitmap().execute(getApplicationContext());
				this.zoomLevel = zoomLevel + change;
			}
		}
	}// end of zoomCheckListener

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

	private void setUpLocationClientIfNeeded() {
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
	// ======== ASYNCTACK CLASS AND METHODS FOR THEM ===========================
	// =========================================================================
	/**
	 * LatLng轉point再畫圖，非常慢。
	 */
	private class DrawByBitmap extends TaskDrawLayerByBitmap {
		long startTime;
		String spentTime;

		@Override
		protected void onPreExecute() {
			progressDialog.show();
			progressDialog.setCanceledOnTouchOutside(false);
			startTime = System.currentTimeMillis();
		}

		@Override
		protected void onPostExecute(HashMap<String, ArrayList<LatLng>> layers) {
			spentTime = OtherTools.getOperationTime(startTime);
			Log.d("mdb", "===== backGround running: " + spentTime);

			Projection projection = map.getProjection();

			// 確認BITMAP跟螢幕大小的關係，然後以此推算出座標點的位置。
			VisibleRegion vr = map.getProjection().getVisibleRegion();
			Point pointFarLeft = projection.toScreenLocation(vr.farLeft);
			Point pointNearRight = projection.toScreenLocation(vr.nearRight);
			double width = Math.abs(pointFarLeft.x - pointNearRight.x);
			double length = Math.abs(pointFarLeft.y - pointNearRight.y);
			Bitmap bitmap = Bitmap.createBitmap((int) width, (int) length,
					Bitmap.Config.ARGB_4444);

			// 比較座標
			Point tempPointF;

			Paint paint = new Paint();
			paint.setColor(Color.RED);
			paint.setStyle(Paint.Style.STROKE); // 設置空心
			Path path = new Path();

			// TODO 改善效率，主要慢在這邊。開THREAD處理
			// TODO 加BASIN發生問題，原因未知。
			// 將地理座標轉為點螢幕座標
			for (ArrayList<LatLng> latlngs : layers.values()) {
				for (int index = 0; index < latlngs.size(); index++) {
					// 先比較座標，再看要不要轉換成點座標，可增加速度
					if (positionInScreen(map, latlngs.get(index))) {
						// TODO 感覺可以另存剩下的點，平行移動時舊的保留，新的再加入。
						tempPointF = projection.toScreenLocation(latlngs
								.get(index));
						if (index == 0) {
							path.moveTo(tempPointF.x, tempPointF.y);
						} else {
							// TODO 修正path.moveTo的錯誤
							// TODO 運用起點等於終點的方式，抓po做contains，或是用LatlngBound來做
							// inside的上一個點如果不在地圖內的話，就moveTo
							if (!positionInScreen(map, latlngs.get(index - 1))) {
								Point outsidePoint = projection
										.toScreenLocation(latlngs
												.get(index - 1));
								path.moveTo(outsidePoint.x, outsidePoint.y);
								path.lineTo(tempPointF.x, tempPointF.y);
							} else {
								path.lineTo(tempPointF.x, tempPointF.y);
							}
						}// end of if
					}// end of if
				}// end of for
			}// end of for

			spentTime = OtherTools.getOperationTime(startTime);
			Log.d("mdb", "===== Latlng to Path: " + spentTime);

			Canvas canvas = new Canvas(bitmap);
			canvas.drawPath(path, paint);

			// TODO 改善bitmap的創造過程
			GroundOverlayOptions ground = new GroundOverlayOptions();
			ground.image(BitmapDescriptorFactory.fromBitmap(bitmap));
			ground.positionFromBounds(map.getProjection().getVisibleRegion().latLngBounds);
			map.addGroundOverlay(ground);

			spentTime = OtherTools.getOperationTime(startTime);
			Log.d("mdb", "===== add to map: " + spentTime);

			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}// end of if

			spentTime = OtherTools.getOperationTime(startTime);
			Log.d("mdb", "===== Total: " + spentTime);

		}// end of onPostExecute
	}// end of TaskDrawByBitmap

	/**
	 * 看LatLng的座標點位，有沒有在顯示中的地圖上。
	 * 
	 * @param map
	 * @param position
	 * @return if bounds contains position return true;
	 */
	private boolean positionInScreen(GoogleMap map, LatLng position) {
		boolean inScreen = false;
		LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
		if (bounds.contains(position)) {
			inScreen = true;
		} else {
			inScreen = false;
		}
		return inScreen;
	}// end of positionInScreen

	/**
	 * 運用Latlng來畫點(座標偏差問題，畫面大小問題)
	 */
	private class DrawByBitmapLatlng extends TaskDrawLayerByBitmap {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		long startTime, endTime, spentTime;

		@Override
		protected void onPreExecute() {
			progressDialog.show();
			progressDialog.setCanceledOnTouchOutside(false);
			startTime = System.currentTimeMillis();
		}

		@Override
		protected void onPostExecute(HashMap<String, ArrayList<LatLng>> layers) {
			Iterator<String> iterator = layers.keySet().iterator();
			VisibleRegion vr = map.getProjection().getVisibleRegion();

			Paint paint = new Paint();
			paint.setColor(Color.RED);
			paint.setStyle(Paint.Style.STROKE);// 設置空心
			Path path = new Path();

			// 將螢幕座標轉為地理座標
			double farX = vr.farLeft.longitude;
			double farY = vr.farLeft.latitude;
			double nearX = vr.nearRight.longitude;
			double nearY = vr.nearRight.latitude;
			double width = Math.abs(farX - nearX);
			double length = Math.abs(farY - nearY);

			Log.d("mdb", vr.farLeft.toString() + " " + vr.nearRight.toString());
			Log.d("mdb", "width= " + width * 100 + " ,length= " + length * 100);

			// 如果距離過大，用*100會出現OOM
			bitmap = Bitmap.createBitmap((int) width * 100, (int) length * 100,
					Bitmap.Config.ARGB_8888);

			double longitude;
			double latitude;
			float x;
			float y;

			// TODO 感覺是誤差?
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				ArrayList<LatLng> latlngs = layers.get(key);
				for (int index = 0; index < latlngs.size(); index++) {
					longitude = latlngs.get(index).longitude;
					latitude = latlngs.get(index).latitude;
					x = (float) Math.abs(longitude - farX) * 100;
					y = (float) Math.abs(latitude - farY) * 100;
					Log.d("mdb", "x= " + x + ", y= " + y);
					if (index == 0) {
						path.moveTo(0, 0);
						// path.moveTo((float) farX, (float) farY);
						path.lineTo(x, y);
					} else {
						path.lineTo(x, y);
					}
				}
				// TODO 確認右下角的位置
				path.lineTo((float) width * 100, (float) length * 100);
			}
			path.close();

			Canvas canvas = new Canvas(bitmap);
			canvas.drawPath(path, paint);

			// TODO 改善bitmap的創造
			GroundOverlayOptions ground = new GroundOverlayOptions();
			ground.image(BitmapDescriptorFactory.fromBitmap(bitmap));
			ground.positionFromBounds(map.getProjection().getVisibleRegion().latLngBounds);
			map.addGroundOverlay(ground);

			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}// end of if
			endTime = System.currentTimeMillis();
			spentTime = endTime - startTime;
			Long minius = (spentTime / 1000) / 60;
			Long seconds = (spentTime / 1000) % 60;
			Log.d("mdb", "spentTime: " + minius + ":" + seconds);
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
			// TODO 可透過locationManager.getLastKnownLocation()來加快載入速度
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
}// end of MainActivity

