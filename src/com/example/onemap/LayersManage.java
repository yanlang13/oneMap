package com.example.onemap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import com.example.onemap.JSONObject;
import com.example.onemap.R;
import android.app.Activity;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import static com.example.onemap.AcrossConstants.DATABASE_NAME;

;

public class LayersManage extends Activity {
	private Spinner spUMap;
	private TextView tvKML, tvDisplay;
	private DefaultSettings ds;
	private DBHelper dbHelper;

	// 測試用，input的polygon file name
	private String INPUT_KML_FILE = "水資源局轄區範圍圖.kml";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layers_manage);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		spUMap = (Spinner) findViewById(R.id.sp_manage_base_map);
		tvDisplay = (TextView) findViewById(R.id.tv_manage_layers_display);
		tvDisplay.setText("");
		tvKML = (TextView) findViewById(R.id.tv_manage_GeoJSON);

		ds = new DefaultSettings(LayersManage.this);
		dbHelper = new DBHelper(getApplicationContext());

		// textview scrolling, 搭配.xml的 android:scrollbars = "vertical"
		tvKML.setMovementMethod(new ScrollingMovementMethod());

		setSpinnerInfo();
		setTVDisplay();

		// TODO add layers to map (button)
		// TODO remove layers from map (button)

	}// end of onCreate

	// =========================================================================
	// ======== ONCREATE METHODS ===============================================
	// =========================================================================

	private void setTVDisplay() {
		List<Layer> layers = dbHelper.getDisplayLayer();
		for (int i = 0; i < layers.size(); i++) {
			String displayLayer = layers.get(i).getLayerName();
			int number = i + 1;
			int size = layers.get(i).getLayerSize();
			// 如果不確定string.format會是英文的話，就要加入Locale.getDefault()
			if (size > 1) {
				String show = String.format(Locale.getDefault(),
						"[%d] %s, %d polygons.\n", number, displayLayer, size);
				tvDisplay.append(show);
			} else {
				String show = String.format(Locale.getDefault(),
						"[%d] %s, %d polygon.\n", number, displayLayer, size);
				tvDisplay.append(show);
			}
		}// end of for
	}// end of setTVDisplay()

	/**
	 * 下拉選單前的呈現方式
	 */
	private void setSpinnerInfo() {
		String[] test = getResources().getStringArray(R.array.google_map);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, test);
		// 下拉後的呈現方式
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spUMap.setAdapter(adapter);
		// 設定預設的選擇項目
		spUMap.setSelection(ds.getBaseMapSpineerPosition());
		spUMap.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				ds.setBaseMap(position + 1);
				ds.setBaseMapSpineerPosition(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}// end of spinnerSelected

	// =========================================================================
	// ====== BUTTON METHODS ===================================================
	// =========================================================================
	public void parseKMLtoText(View view) { // onClick
		int PRETTY_PRINT_INDENT_FACTOR = 4;
		try {
			File sd = Environment.getExternalStorageDirectory();
			File kmlFrom = new File(sd, INPUT_KML_FILE);
			Toast.makeText(LayersManage.this,
					"input file is " + INPUT_KML_FILE, Toast.LENGTH_SHORT)
					.show();
			File txt = new File(sd, "xmlParsed.txt");

			BufferedReader br = new BufferedReader(new FileReader(kmlFrom));
			String line;
			StringBuilder sb = new StringBuilder();

			BufferedWriter bw = new BufferedWriter(new FileWriter(txt));

			while ((line = br.readLine()) != null) {
				sb.append(line.trim());
			}
			br.close();

			// github下載的JSONObject
			JSONObject xmlJSONObj = XML.toJSONObject(sb.toString());
			String jsonPrettyPrintString = xmlJSONObj
					.toString(PRETTY_PRINT_INDENT_FACTOR);
			tvKML.setText(jsonPrettyPrintString);
			bw.write(jsonPrettyPrintString);
			bw.close();

		} catch (IOException e) {
			Log.d("mdb", "LayoutManage, " + e.toString());
		} catch (JSONException e) {
			Log.d("mdb", e.toString());
		}
	}

	public void exportDatabase(View view) {
		OtherTools.copyDBtoSDcard();
		Toast.makeText(LayersManage.this, "export db to SD card",
				Toast.LENGTH_SHORT).show();
	}// end of exportDatabase

	public void deleteDatabase(View view) {
		LayersManage.this.deleteDatabase(DATABASE_NAME);
		Toast.makeText(LayersManage.this, "delete database", Toast.LENGTH_SHORT)
				.show();
	}

	// =========================================================================
	// ======== MENU ===========================================================
	// =========================================================================
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
				NavUtils.navigateUpFromSameTask(LayersManage.this);
			}
		}
		return super.onOptionsItemSelected(item);
	}// end of onOptionsItemSelected
}// end of class LayersManage
