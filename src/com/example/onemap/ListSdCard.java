package com.example.onemap;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import com.example.onemap.R;
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

	// 處理Directory的排列，KML - DIR (A to Z)
	private ArrayList<String> KmlList, DirList;

	// 寫kml file的資料到database
	private DBHelper dbHelper;

	// ============================================================ onCreate ING
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_sd_card);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		lv = (ListView) findViewById(R.id.lv_list_sd_card);

		folderList = new ArrayList<String>();
		KmlList = new ArrayList<String>();
		DirList = new ArrayList<String>();
		dbHelper = new DBHelper(this);

		// TODO 強化進入ExternalStorageDirectory的問題判斷(androdi developer)
		String root_sd = Environment.getExternalStorageDirectory().toString();

		// TODO isExternalStorageEmulated ()，來判斷是否是虛擬的sd Card
		// folder = new File(root_sd + "/external_sd");

		file = new File(root_sd);

		if (file.exists() && file.canRead()) {
			getKmlAndDir(file, lv);
			// 點擊folder進入下一層
			lv.setOnItemClickListener(new MyOnItemClickListener());
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
			// 如果到達emulate的進入點，就直接回到mainActivity
			String pageUp = file.getParent().toString();
			file = new File(pageUp);
			getKmlAndDir(file, lv);
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
	private void getKmlAndDir(File directory, ListView listView) {

		clearAllList();

		// 插入BACK TO UPPER FOLDER到LIST的第一個欄位
		folderList.add(0, "page up.../");

		File dirArray[] = directory.listFiles();

		for (File f : dirArray) {
			String fileName = f.getName();
			if (!fileName.startsWith(".")) {
				if (f.isDirectory()) {
					// StringBulider用來串接字串可以加快效率
					// 但如果是單一statement就免了
					DirList.add(fileName + "/");
				} else if (fileName.endsWith(".kml")) {
					KmlList.add(fileName);
				} else {
					DirList.add(fileName);
				}
			}
		}// end of for

		sortAndAddToList(KmlList);
		sortAndAddToList(DirList);

		listView.setAdapter(new ArrayAdapter<String>(ListSdCard.this,
				android.R.layout.simple_list_item_1, folderList));
	}// end of getAllFilesOfDir

	/**
	 * clear folderList, DirList, KmlList
	 */
	private void clearAllList() {
		folderList.clear();
		DirList.clear();
		KmlList.clear();

	}// end of tempListClear

	/**
	 * sort string by A to Z
	 * 
	 * @param arrayList
	 *            (String)
	 */
	private void sortAndAddToList(ArrayList<String> arrayList) {
		Collections.sort(arrayList);
		// 如果用folderList.addAll(temp_kml)就無法點擊顯示
		for (String s : arrayList) {
			folderList.add(s);
		}
	}// end of sortAndAddToList

	// ============================================================ MethodED

	// ============================================================ class ING

	/**
	 * listView item click
	 * 
	 * @author acer
	 * 
	 */
	class MyOnItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			setPageUp(position);
			// 取得路徑，轉為file
			File tempFile = new File(file, folderList.get(position));

			// 如果是folder，就在做List
			if (tempFile.isDirectory()) {
				file = new File(file, folderList.get(position));
				getKmlAndDir(file, lv);
			}

			// 如果是點擊了kml就存檔，然後回到mainActivity
			String fileName = tempFile.getName();
			if (fileName.endsWith(".kml")) {
				// 取出kml file的檔案名稱 (.kml不要)
				String title = fileName.substring(0, fileName.length() - 4);
				String kmlString = OtherTools.fileToString(tempFile);
				
				//如果不是kML FILE就不用新增到資料庫了
				parseKmlString pks = new parseKmlString(kmlString);
				if (pks.hasDocument()) {
					Layer layer = new Layer();
					layer.setTitle(title);
					layer.setKmlString(kmlString);
					layer.setDisplay("true");
					dbHelper.addLayer(layer);
					
					// 沒有重複，就跳回mainActivity
					if (!dbHelper.getDuplicate()) {
						startActivity(new Intent(getApplicationContext(),
								MainActivity.class));
					} else {
						Toast.makeText(ListSdCard.this, "duplicate KML",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(ListSdCard.this, "not a correct format",
							Toast.LENGTH_SHORT).show();
				}
				Log.d("mdb", "stroage kml file to database");
			}// end of if
		}// end of onItemClick
	}// end of MyOnItemClickListener

	// ============================================================ class ED

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
