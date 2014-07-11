package com.example.onemap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.example.onemap.R;
import com.google.android.gms.drive.internal.l;
import android.app.Activity;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;

import static com.example.onemap.AcrossConstants.LA_FIELD_LAYER_NAME;
import static com.example.onemap.AcrossConstants.LA_FIELD_DISPLAY;
import static com.example.onemap.AcrossConstants.LA_FIELD_CREATE_AT;

public class LayersList extends Activity {

	private DBHelper dbHelper;
	private ListView layerOfList;
	private List<HashMap<String, Object>> listLayers;
	private List<Layer> layers;

	// ========================================================================
	// ==== ACTIVITY LIFECYCLE ================================================
	// ========================================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layers_of_list);

		listLayers = new ArrayList<HashMap<String, Object>>();
		layers = new ArrayList<Layer>();
		dbHelper = new DBHelper(this);

		layerOfList = (ListView) findViewById(R.id.lv_layer_of_list);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// TODO SHOW LAYER INFO: NAME, DESC, UPLOAD STATE
		layers = dbHelper.getDisplayLayer();
		
		Log.d("mdb", "50");
		
		for (int location = 0; location < layers.size(); location++) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put(LA_FIELD_LAYER_NAME, "Layer Name: "
					+ layers.get(location).getLayerName());
			item.put(LA_FIELD_CREATE_AT, "Create at: "
					+ layers.get(location).getCreateAt());
			item.put(LA_FIELD_DISPLAY, "Display: "
					+ layers.get(location).getDisplay());
			listLayers.add(item);
		}
		
		Log.d("mdb", "63");
		
		String[] from = { LA_FIELD_LAYER_NAME, LA_FIELD_CREATE_AT,
				LA_FIELD_DISPLAY };
		
		int[] to = { R.id.tv_layers_info_layer_name,
				R.id.tv_layers_info_layer_create_at,
				R.id.tv_layers_info_layer_display };

	       SimpleAdapter simpleAdapter = new SimpleAdapter(getApplication(), listLayers,
                  R.layout.layers_infomation, from, to);

		 layerOfList.setAdapter(simpleAdapter);

		// TODO LONG CLICK: EDIT, DELETE, AND UPLOAD TO PARSE
	}// end of onCreate()

	@Override
	public void onPause() {
		super.onPause();
		if (dbHelper != null) {
			dbHelper.close();
		}
	}// end of onPause()

	// ========================================================================
	// ==== MENU ==============================================================
	// ========================================================================
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_null, menu);
		return true;
	} // end of onCreateOptionsMenu

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			Intent upIntent = NavUtils.getParentActivityIntent(this);
			if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
				TaskStackBuilder.create(this)
						.addNextIntentWithParentStack(upIntent)
						.startActivities();
			} else {
				NavUtils.navigateUpFromSameTask(LayersList.this);
			}
		}
		return super.onOptionsItemSelected(item);
	}// end of onOptionsItemSelected
		// ============================================================ MenuED
}
