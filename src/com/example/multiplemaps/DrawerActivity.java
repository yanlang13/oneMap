package com.example.multiplemaps;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * 這個沒有被call!!!
 * @author acer
 *
 */
public class DrawerActivity extends Activity {
	// 有關sliding menu
	private DrawerLayout drawerLayout;
	private ListView drawerList; // listView的view
	private ActionBarDrawerToggle actionBarDrawerToggle; // drawerLayout的listener

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("mdb", "DrawerActivity");
		super.onCreate(savedInstanceState);

		setLeftDrawer();
		Log.d("mdb", "LeftDrawer");
	}

	private void setLeftDrawer() {
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerList = (ListView) findViewById(R.id.left_drawer);
		// set a custom shadow that overlays the main content when the drawer
		// opens
		// START=>Push object to x-axis position at the start of its container,
		// not changing its size.
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// set up the drawer's list view with items and click listener
		drawerList.setAdapter(new DrawerArrayAdapter(this, DrawerList.LIST));
		// drawerList.setAdapter(new ArrayAdapter<String>(this,
		// R.layout.drawer_list_item, drawerTitles));
		drawerList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {// layoutSetting
					startActivity(new Intent(getApplicationContext(),
							LayersManage.class));
				} else if (position == 1) {
					startActivity(new Intent(getApplicationContext(),
							LayersList.class));
				}
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

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
		boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		menu.findItem(R.id.action_search).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	// onConfigurationChanged
	// 是指狀態改變時(ex:跳出鍵盤、螢幕旋轉等)，會導致activity被destory後再重新設定actionBarDrawerToggle的
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		actionBarDrawerToggle.onConfigurationChanged(newConfig);
	}

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
}// end of DrawerActivity
