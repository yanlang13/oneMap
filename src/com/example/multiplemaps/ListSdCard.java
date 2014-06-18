package com.example.multiplemaps;

import java.io.File;
import java.text.BreakIterator;
import java.util.ArrayList;

import de.micromata.opengis.kml.v_2_2_0.Lod;
import android.app.Activity;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ListSdCard extends Activity {

	private ArrayList<String> folderList;
	private File file;
	private ListView lv;

	// ============================================================ onCreate ING
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d("mdb", "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_sd_card);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		lv = (ListView) findViewById(R.id.lv_list_sd_card);

		folderList = new ArrayList<String>();

		// TODO 強化進入ExternalStorageDirectory的問題判斷(androdi developer)
		String root_sd = Environment.getExternalStorageDirectory().toString();

		// TODO isExternalStorageEmulated ()，來判斷是否是虛擬的sd Card

		// folder = new File(root_sd + "/external_sd");

		file = new File(root_sd);

		if (file.exists() && file.canRead()) {
			getDirAndKml(file, lv);

			// 點擊folder進入下一層
			lv.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					setPageUp(position);
					// 取得路徑，轉為file
					File temp_file = new File(file, folderList.get(position));
					// 如果是folder，就在做List
					if (temp_file.isDirectory()) {
						file = new File(file, folderList.get(position));
						getDirAndKml(file, lv);
					}
				}
			});// end of setOnItemClickListener

		} else {
			Toast.makeText(ListSdCard.this, "error to access sd card",
					Toast.LENGTH_LONG).show();
		}// end of if
	}// end of onCreate

	// ============================================================ onCreate ED

	// ============================================================ Method ING

	/**
	 * 如果到達emulate的最上層就返回main，如果不是在做listView
	 * 
	 * @param ListView的onItemClick的position
	 */
	private void setPageUp(int position) {
		pageup: if (position == 0) {
			if (getParentPath(file)) {
				startActivity(new Intent(ListSdCard.this, MainActivity.class));
				break pageup;
			}
			// 如果
			String pageUp = file.getParent().toString();
			file = new File(pageUp);
			getDirAndKml(file, lv);
		}
	}// end of setPageUp

	/**
	 * @param file
	 * @return true file的parentPath為/storage/emulated
	 */
	private boolean getParentPath(File file) {
		// emulate SD Card
		if (file.getParent().toString().equals("/storage/emulated")) {
			return true;
		}
		return false;
	}// end of getParentPath

	/**
	 * @param directory
	 *            (folder)
	 * @param listView
	 */
	private void getDirAndKml(File directory, ListView listView) {
		
		folderList.clear();
		
		//TODO 將KML FILE放到最前面，其餘資料夾做字母排序
		
		// 插入BACK TO UPPER FOLDER到LIST的第一個欄位
		folderList.add(0, "page up.../");

		File dirArray[] = directory.listFiles();
		for (File f : dirArray) {
			// 移除.開頭的檔案(參考ES File Express做處理)
			if (!f.getName().startsWith(".")) {
				if (f.isDirectory()) {
					// StringBulider用來串接字串可以加快效率
					// 但如果是單一statement就免了
					folderList.add(f.getName() + "/");
				} else {
					// 只找kml檔案
					if (f.getName().endsWith(".kml")) {
						folderList.add(f.getName());
					}
				}
			}// end of if
		}// end of for
		listView.setAdapter(new ArrayAdapter<String>(ListSdCard.this,
				android.R.layout.simple_list_item_1, folderList));
	}// end of getAllFilesOfDir

	// ============================================================ MethodED

	// ============================================================ Menu
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
				NavUtils.navigateUpFromSameTask(ListSdCard.this);
			}
		}
		return super.onOptionsItemSelected(item);
	}// end of onOptionsItemSelected

	// ============================================================ MenuED
}// end of ListSdCard
