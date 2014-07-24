package com.example.onemap;

import android.app.Activity;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

public class LayerInfoDetials extends Activity {

	// ========================================================================
	// ==== ACTIVITY LIFECYCLE ================================================
	// ========================================================================
	@Override
	protected void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		setContentView(R.layout.layer_info_detials);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// TODO 抓BUNDLE的layerName在做查詢，然後放詳細的placeMarks資料進來。

	}// end of onCreate

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
				NavUtils.navigateUpFromSameTask(LayerInfoDetials.this);
			}
		}
		return super.onOptionsItemSelected(item);
	}// end of onOptionsItemSelected
} // end of public class LayerInfoDetials
