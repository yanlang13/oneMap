package com.example.onemap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.example.onemap.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
		layers = dbHelper.getAllLayers();
	}// end of onCreate()

	@Override
	protected void onResume() {
		super.onResume();
		setListView();
		// 這邊的context不能使用getApplicationContext()，因為一個ACTIVITY對應一個WINDOWS
		layerOfList.setOnItemLongClickListener(new myOnItemLongClickListener(
				LayersList.this, layers));
	}// end of onResume()

	@Override
	public void onPause() {
		super.onPause();
		if (dbHelper != null) {
			dbHelper.close();
		}
	}// end of onPause()
		// ========================================================================
		// ==== ONCREATE METHODS
		// ==================================================
		// ========================================================================

	/**
	 * 透過extends simpleAadapter完成List View
	 */
	private void setListView() {
		// TODO 可以透過sqlite的ORDER BY 來調整順序。
		for (int location = 0; location < layers.size(); location++) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put(LA_FIELD_LAYER_NAME, layers.get(location).getLayerName());
			item.put(LA_FIELD_CREATE_AT, layers.get(location).getCreateAt());
			Log.d("mdb", "ca:　" +layers.get(location).getCreateAt());
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

		return convertView;
	}
}// end of class MySimepleAdapter

/**
 * edit, delete and update to server
 */
class myOnItemLongClickListener implements OnItemLongClickListener {
	private DBHelper dbHelper;
	private List<Layer> layers = new ArrayList<Layer>();;
	private Context context;
	final String[] edit = new String[] { "edit", "update layer to server",
			"delete" };

	public myOnItemLongClickListener(Context context, List<Layer> layers) {
		this.context = context;
		this.layers = layers;
		dbHelper = new DBHelper(context);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		final String layerName = layers.get(position).getLayerName();

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(layerName);
		builder.setItems(edit, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {
					Intent intent = new Intent(context, LayerInfoDetials.class);
					Bundle bundle = new Bundle();
					bundle.putString(LA_FIELD_LAYER_NAME, layerName);
					intent.putExtras(bundle);
					context.startActivity(intent);
				} else if (which == 1) {
					// TODO update data to server
					Toast.makeText(context, layerName + " is updated.",
							Toast.LENGTH_SHORT).show();
				} else if (which == 2) {
					dbHelper.deleteLayerRow(layerName);
					dbHelper.deletePlaceMarkRows(layerName);
					Toast.makeText(context, layerName + " is deleted.",
							Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(context, LayersList.class);
					context.startActivity(intent);
				} else {
				}
			}
		});
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
		return false;
	}// end of public boolean onItemLongClick
}// end of class myOnItemLongClickListener
