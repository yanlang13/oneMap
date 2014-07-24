package com.example.onemap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.example.onemap.R;
import android.app.Activity;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		listLayers = new ArrayList<HashMap<String, Object>>();
		layers = new ArrayList<Layer>();
		dbHelper = new DBHelper(this);

		layerOfList = (ListView) findViewById(R.id.lv_layer_of_list);
		layers = dbHelper.getDisplayLayer();

		setListView();
		
		layerOfList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d("mdb", ""+position);
				return false;
			}
		
		});
	}// end of onCreate()

	@Override
	public void onPause() {
		super.onPause();
		if (dbHelper != null) {
			dbHelper.close();
		}
	}// end of onPause()

	// ========================================================================
	// ==== ONCREATE METHODS ==================================================
	// ========================================================================
	/**
	 * 透過extends simpleAadapter完成List View
	 */
	private void setListView() {
		for (int location = 0; location < layers.size(); location++) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put(LA_FIELD_LAYER_NAME, layers.get(location).getLayerName());
			item.put(LA_FIELD_CREATE_AT, layers.get(location).getCreateAt());
			item.put(LA_FIELD_DISPLAY, layers.get(location).getDisplay());
			listLayers.add(item);
		}

		String[] from = { LA_FIELD_LAYER_NAME, LA_FIELD_CREATE_AT,
				LA_FIELD_DISPLAY };

		int[] to = { R.id.tv_layers_info_layer_name,
				R.id.tv_layers_info_layer_create_at,
				R.id.tv_layers_info_layer_display };

		MySimepleAdapter simpleAdapter = new MySimepleAdapter(getApplication(),
				listLayers, R.layout.layers_infomation, from, to);

		layerOfList.setAdapter(simpleAdapter);

	}// end of setListView()

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
}// end of public class LayersList

// ========================================================================
// ==== CLASS =============================================================
// ========================================================================
/**
 * 主要是override getView，改變呈現方式(可上色)。
 */
class MySimepleAdapter extends SimpleAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<HashMap<String, Object>> listLayers;

	public MySimepleAdapter(Context context,
			List<HashMap<String, Object>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
		this.context = context;
		listLayers = data;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.layers_infomation, null);
		}

		TextView tvLayerName = (TextView) convertView
				.findViewById(R.id.tv_layers_info_layer_name);
		TextView tvDisplay = (TextView) convertView
				.findViewById(R.id.tv_layers_info_layer_display);
		TextView tvCreateAt = (TextView) convertView
				.findViewById(R.id.tv_layers_info_layer_create_at);

		HashMap<String, Object> item = listLayers.get(position);

		String layerName = item.get(LA_FIELD_LAYER_NAME).toString();
		String createAt = item.get(LA_FIELD_CREATE_AT).toString();
		String display = item.get(LA_FIELD_DISPLAY).toString();

		tvLayerName.setText(layerName);
		tvDisplay.setText(display);
		tvCreateAt.setText(createAt);

		// display的顏色
		if (display.equals("YES")) {
			tvDisplay.setTextColor(context.getResources().getColor(
					R.color.green_yellow));
		} else {
			tvDisplay.setTextColor(context.getResources().getColor(
					R.color.lava_red));
		}

//		ImageButton imageButton = (ImageButton) convertView
//				.findViewById(R.id.ib_layers_of_infi_info_button);
//
//		imageButton.setOnClickListener(new buttonClick(context, layerName));

		return convertView;
	}
}// end of class MySimepleAdapter

/**
 * click，傳入layerName來做確認
 */
class buttonClick implements OnClickListener {
	private String layerName;
	private Context context;

	buttonClick(Context context, String layerName) {
		this.layerName = layerName;
		this.context = context;
	}

	@Override
	public void onClick(View v) {
		
		Intent intent = new Intent(context, LayerInfoDetials.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
}// end of class ItemButtonClick

/**
 * edit, delete and update to server
 */
class myOnItemLongClickListener implements OnItemLongClickListener {
	private List<Layer> layers = new ArrayList<Layer>();

	public myOnItemLongClickListener(List<Layer> layers) {
		this.layers = layers;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		view.setBackgroundColor(Color.GRAY);
		Log.d("mdb", layers.get(position).getLayerName());
		return false;
	}
}// end of class myOnItemLongClickListener
