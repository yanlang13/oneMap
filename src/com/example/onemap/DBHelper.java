package com.example.onemap;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;
import static com.example.onemap.AcrossConstants.*;

public class DBHelper extends SQLiteOpenHelper {

	private Context context;

	// 用在確認是否重複，ListSdCard使用
	private boolean duplicate = false;

	// ====================================================================
	// ======== DATABASE ==================================================
	// ====================================================================
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 只有當getRead/Writable...時才會做onCreate
		Log.d("mdb", "=====DBHepler onCreate=====");
		db.execSQL(INIT_LA_TABLE);
		db.execSQL(INIT_PM_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d("mdb", "db onUpgrade");
		db.execSQL(DROP_LA_TABLE);
		db.execSQL(DROP_PM_TABLE);
	}

	// ====================================================================
	// ====== LAYERS TABLE METHODS ========================================
	// ====================================================================

	public void addLayer(Layer layer) {
		// 1. get reference to writable DB
		// 這邊duplicateCheck會關閉db，所以將db放到裡面，開關才不會錯誤
		if (!duplicateCheck(layer.getLayerName())) {
			SQLiteDatabase db = this.getWritableDatabase();
			// 2. create ContentValues to add key "column"/value
			ContentValues values = new ContentValues();
			values.put(LA_FIELD_LAYER_NAME, layer.getLayerName());
			values.put(LA_FIELD_LAYER_SIZE, layer.getLayerSize());
			values.put(LA_FIELD_LDESC, layer.getDesc());
			values.put(LA_FIELD_DISPLAY, layer.getDisplay());
			values.put(LA_FIELD_CREATE_AT, layer.getCreateAt());
			// 3. insert
			db.insert(TABLE_LAYERS, null, values);
			// 4. close
			db.close();
			duplicate = false;
		} else {
			duplicate = true;
			Toast.makeText(context, R.string.duplicate_name, Toast.LENGTH_SHORT)
					.show();
		}
	}// end of addLayer

	/**
	 * @param layerName
	 * @return layer
	 */
	public Layer getLayer(String layerName) {
		// 1. get reference to readable DB
		SQLiteDatabase db = this.getReadableDatabase();
		Layer layer = new Layer();
		// 2. build query
		try {
			Cursor cursor = db.query(TABLE_LAYERS, // a. table
					LA_COLUMNS, // b. column names
					LA_FIELD_LAYER_NAME + "=?", // c. selections
					new String[] { layerName }, // d. selections args
					null, // e. group by
					null, // f. having
					null, // g. order by
					null); // h. limit
			// 3. if we got results get the first one
			if (cursor != null) {
				cursor.moveToFirst();
			}
			// 4. build book object
			layer.setId(cursor.getString(0));
			layer.setLayerName(cursor.getString(1));
			layer.setLayerSize(cursor.getInt(2));
			layer.setLDesc(cursor.getString(3));
			layer.setDisplay(cursor.getString(4));
			layer.setCreateAt(cursor.getString(5));
			// 5. return book
		} catch (CursorIndexOutOfBoundsException e) {
			Log.d("mdb", "DBHelper Class, " + "Error:" + e.toString());
		}
		db.close();
		return layer;
	}

	/**
	 * return List<Layer>
	 */
	public List<Layer> getAllLayers() {
		List<Layer> layers = new ArrayList<Layer>();
		// 1. build the query
		String query = "SELECT  * FROM " + TABLE_LAYERS;

		// 2. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		// 3. go over each row, build Layer and add it to list

		try {
			if (cursor.moveToFirst()) {
				do {
					Layer layer = new Layer();
					layer.setId(cursor.getString(0));
					layer.setLayerName(cursor.getString(1));
					layer.setLayerSize(cursor.getInt(2));
					layer.setLDesc(cursor.getString(3));
					layer.setCreateAt(cursor.getString(4));
					layer.setDisplay(cursor.getString(5));
					// Add book to books
					layers.add(layer);
				} while (cursor.moveToNext());
			}
			cursor.close();
		} catch (IllegalStateException e) {
			Log.d("mdb", "DBHelper Class, " + "Error:" + e.toString());
		}
		db.close();
		// return books
		return layers;
	}// end of getAllLayer()

	/**
	 * 根據layerName來刪除該項
	 */
	public void deleteLayerRow(String layerName) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_LAYERS, LA_FIELD_LAYER_NAME + " = ?",
				new String[] { String.valueOf(layerName) });
		db.close();
	}// end of deleteLayerRow

	/**
	 * @param oldLayer
	 *            使用getLayer(int id)來取得
	 * @param newLayer
	 */
	public void updateLayerRow(Layer oldLayer, Layer newLayer) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(LA_FIELD_LAYER_NAME, newLayer.getLayerName());
		values.put(LA_FIELD_LAYER_SIZE, newLayer.getLayerSize());
		values.put(LA_FIELD_LDESC, newLayer.getDesc());
		values.put(LA_FIELD_DISPLAY, newLayer.getDisplay());
		values.put(LA_FIELD_CREATE_AT, newLayer.getCreateAt());

		db.update(TABLE_LAYERS, values, LA_FIELD_LAYER_NAME + "=?",
				new String[] { String.valueOf(oldLayer.getLayerName()) });
		db.close();
	}// end of updateLayer

	/**
	 * 取得LA_FIELD_DISPLAY = YES的List
	 * 
	 * @return
	 */
	public List<Layer> getDisplayLayers() {
		List<Layer> layers = new ArrayList<Layer>();
		SQLiteDatabase db = this.getReadableDatabase();
		try {
			Cursor cursor = db.query(TABLE_LAYERS, LA_COLUMNS, LA_FIELD_DISPLAY
					+ "=?", // c. selections
					new String[] { "YES" }, // d. selections args
					null, // e. group by
					null, // f. having
					null, // g. order by
					null); // h. limit
			// 3. if we got results get the first one
			if (cursor.moveToFirst()) {
				do {
					Layer layer = new Layer();
					layer.setId(cursor.getString(0));
					layer.setLayerName(cursor.getString(1));
					layer.setLayerSize(cursor.getInt(2));
					layer.setLDesc(cursor.getString(3));
					layer.setCreateAt(cursor.getString(4));
					layer.setDisplay(cursor.getString(5));
					layers.add(layer);

				} while (cursor.moveToNext());
			}
			cursor.close();
		} catch (CursorIndexOutOfBoundsException e) {
			Log.d("mdb", "DBHelper Class, " + "Error:" + e.toString());
		}
		db.close();
		return layers;
	}// end of List<Layer> getDisplayLayer()

	/**
	 * 取得LA_FIELD_DISPLAY = YES的List
	 * 
	 * @return
	 */
	public List<Layer> getOffLayers() {
		List<Layer> layers = new ArrayList<Layer>();
		SQLiteDatabase db = this.getReadableDatabase();
		try {
			Cursor cursor = db.query(TABLE_LAYERS, LA_COLUMNS, LA_FIELD_DISPLAY
					+ "=?", // c. selections
					new String[] { "NO" }, // d. selections args
					null, // e. group by
					null, // f. having
					null, // g. order by
					null); // h. limit
			// 3. if we got results get the first one
			if (cursor.moveToFirst()) {
				do {
					Layer layer = new Layer();
					layer.setId(cursor.getString(0));
					layer.setLayerName(cursor.getString(1));
					layer.setLayerSize(cursor.getInt(2));
					layer.setLDesc(cursor.getString(3));
					layer.setDisplay(cursor.getString(4));
					layer.setCreateAt(cursor.getString(5));
					layers.add(layer);

				} while (cursor.moveToNext());
			}
			cursor.close();
		} catch (CursorIndexOutOfBoundsException e) {
			Log.d("mdb", "DBHelper Class, " + "Error:" + e.toString());
		}
		db.close();
		return layers;
	}// end of List<Layer> getDisplayLayer()

	/**
	 * 只改變layer的display狀態
	 * 
	 * @param LayerName
	 */
	public void resetDisplay(String layerName, String yesOrNo) {
		SQLiteDatabase db = this.getWritableDatabase();
		DBHelper dbHelper = new DBHelper(context);
		Layer Layer = dbHelper.getLayer(layerName);
		ContentValues values = new ContentValues();
		values.put(LA_FIELD_LAYER_NAME, Layer.getLayerName());
		values.put(LA_FIELD_LAYER_SIZE, Layer.getLayerSize());
		values.put(LA_FIELD_LDESC, Layer.getDesc());
		values.put(LA_FIELD_DISPLAY, yesOrNo);
		values.put(LA_FIELD_CREATE_AT, Layer.getCreateAt());

		db.update(TABLE_LAYERS, values, LA_FIELD_LAYER_NAME + "=?",
				new String[] { String.valueOf(layerName) });
		dbHelper.close();
		db.close();
	}// end of resetDisplay

	// =======================================================================
	// ==========METHODS FOR LAYERS ==========================================
	// =======================================================================

	/**
	 * 傳入的是kml的檔名，比對的是layerName，如果layerName有重複，則傳回true，反之則false
	 */
	public boolean duplicateCheck(String layerName) {
		// getAllLayer() 有開關db
		List<Layer> layers = getAllLayers();
		for (Layer l : layers) {
			if (layerName.equals(l.getLayerName())) {
				return true;
			}
		}
		return false;
	}// end of boolean duplicateCheck

	/**
	 * Layers的總數
	 * 
	 * @return
	 */
	public int getLayerCount() {
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "SELECT  * FROM " + TABLE_LAYERS;
		Cursor cursor = db.rawQuery(query, null);
		int count = cursor.getCount();
		cursor.close();
		db.close();
		// return count
		return count;
	}// end of getLayerCount

	/**
	 * @return true if duplicate title
	 */
	public boolean getDuplicate() {
		return duplicate;
	}// end of getDuplicate

	// ========================================================================
	// ========== PLACE TABLE METHODS =========================================
	// ========================================================================
	/**
	 * 
	 * @param placeMark
	 */
	public void addPlaceMark(PlaceMark placeMark) {
		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();
		// 2. create ContentValues to add key "column"/value
		ContentValues values = new ContentValues();
		values.put(PM_FIELD_LAYER_NAME, placeMark.getLayerName());
		values.put(PM_FIELD_PLACEMARK_NAME, placeMark.getPlaceMarkName());
		values.put(PM_FIELD_DESC, placeMark.getPDesc());
		values.put(PM_FIELD_DISPLAY, placeMark.getDisplay());
		values.put(PM_FIELD_STYLELINK, placeMark.getStyleLink());

		// 3. insert
		db.insert(TABLE_PLACE, null, values);
		// 4. close
		db.close();
	}// end of addPlaceMark

	/**
	 * 取得PM_FIELD_DISPLAY = YES的 List
	 * 
	 * @return
	 */
	public List<PlaceMark> getDisplayPlaceMark() {
		List<PlaceMark> placeMarks = new ArrayList<PlaceMark>();

		SQLiteDatabase db = this.getReadableDatabase();
		try {
			Cursor cursor = db.query(TABLE_PLACE, PM_COLUMNS, PM_FIELD_DISPLAY
					+ "=?", // c. selections
					new String[] { "YES" }, // d. selections args
					null, // e. group by
					null, // f. having
					null, // g. order by
					null); // h. limit
			// 3. if we got results get the first one
			if (cursor.moveToFirst()) {
				do {
					PlaceMark placeMark = new PlaceMark();
					placeMark.setId(cursor.getString(0));
					placeMark.setLayerName(cursor.getString(1));
					placeMark.setPlaceMarkName(cursor.getString(2));
					placeMark.setPDesc(cursor.getString(3));
					placeMark.setDisplay(cursor.getString(4));
					placeMark.setStyleLink(cursor.getString(5));
					placeMarks.add(placeMark);

				} while (cursor.moveToNext());
			}

			cursor.close();

		} catch (CursorIndexOutOfBoundsException e) {
			Log.d("mdb", "DBHelper Class, " + "Error:" + e.toString());
		}
		db.close();
		return placeMarks;
	}// end of List<PlaceMark> getDisplayPlaceMark()

	/**
	 * DELETE PLACEMAKRS BY LAYERNAME
	 * 
	 * @param layerName
	 */
	public void deletePlaceMarkRows(String layerName) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_PLACE, PM_FIELD_LAYER_NAME + " = ?",
				new String[] { String.valueOf(layerName) });
		db.close();
	}// end of deletePlaceMarkRows

	public List<PlaceMark> getPlaceMarksWithSameLayerName(String layerName) {
		List<PlaceMark> placeMarks = new ArrayList<PlaceMark>();
		SQLiteDatabase db = this.getReadableDatabase();
		try {
			Cursor cursor = db.query(TABLE_PLACE, PM_COLUMNS,
					PM_FIELD_LAYER_NAME + "=?", // c. selections
					new String[] { layerName }, // d. selections args
					null, // e. group by
					null, // f. having
					null, // g. order by
					null); // h. limit
			// 3. if we got results get the first one
			if (cursor.moveToFirst()) {
				do {
					PlaceMark placeMark = new PlaceMark();
					placeMark.setId(cursor.getString(0));
					placeMark.setLayerName(cursor.getString(1));
					placeMark.setPlaceMarkName(cursor.getString(2));
					placeMark.setPDesc(cursor.getString(3));
					placeMark.setDisplay(cursor.getString(4));
					placeMark.setStyleLink(cursor.getString(5));
					placeMarks.add(placeMark);

				} while (cursor.moveToNext());
			}
			cursor.close();
		} catch (CursorIndexOutOfBoundsException e) {
			Log.d("mdb", "DBHelper Class, " + "Error:" + e.toString());
		}
		db.close();
		return placeMarks;
	}// end of getPlaceMarksWithSameLayerName

	/**
	 * 根據layerName，改變placeMarks的display狀態
	 * 
	 * @param LayerName
	 */
	public void resetPlaceMarkDisplay(String layerName, String yesOrNo) {

		SQLiteDatabase db = this.getWritableDatabase();
		DBHelper dbHelper = new DBHelper(context);

		List<PlaceMark> placeMarks = dbHelper
				.getPlaceMarksWithSameLayerName(layerName);

		for (PlaceMark placeMark : placeMarks) {
			ContentValues values = new ContentValues();
			values.put(PM_FIELD_LAYER_NAME, placeMark.getLayerName());
			values.put(PM_FIELD_PLACEMARK_NAME, placeMark.getPlaceMarkName());
			values.put(PM_FIELD_DESC, placeMark.getPDesc());
			values.put(PM_FIELD_DISPLAY, yesOrNo);
			values.put(PM_FIELD_STYLELINK, placeMark.getStyleLink());

			db.update(
					TABLE_PLACE,
					values,
					PM_FIELD_PLACEMARK_NAME + "=?",
					new String[] { String.valueOf(placeMark.getPlaceMarkName()) });
		}
		dbHelper.close();
		db.close();
	}// end of resetDisplay

}// end of DBHelper
