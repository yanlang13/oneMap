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

public class DBHelper extends SQLiteOpenHelper {
	final static int DB_VERSION = 1;
	private static final String DATABASE_NAME = "oneMaps.db";

	private static final String TABLE_LAYERS = "Layers";
	private static final String TABLE_PLACE = "PlaceMarks";

	// column of Layers
	private static final String LA_FIELD_ID = "id";
	private static final String LA_FIELD_LAYER_NAME = "LayerName";
	private static final String LA_FIELD_LDESC = "LayerDescription";
	private static final String LA_FIELD_DISPLAY = "Display";
	private static final String LA_FIELD_CREATE_AT = "CreateAt";

	// column of PlaceMarks
	private static final String PM_FIELD_ID = "id";
	private static final String PM_FIELD_LAYER_NAME = "LayerName";
	private static final String PM_FIELD_PLACEMARK_NAME = "PlaceMarkName";
	private static final String PM_FIELD_DESC = "PlaceMarkDescription";
	private static final String PM_FIELD_DISPLAY = "Display";
	private static final String PM_FIELD_STYLELINK = "StyleLink";

	final static String[] LA_COLUMNS = { LA_FIELD_ID, LA_FIELD_LAYER_NAME,
			LA_FIELD_LDESC, LA_FIELD_DISPLAY, LA_FIELD_CREATE_AT };

	final static String[] PM_COLUMNS = { PM_FIELD_ID, PM_FIELD_LAYER_NAME,
			PM_FIELD_PLACEMARK_NAME, PM_FIELD_DESC, PM_FIELD_DISPLAY,
			PM_FIELD_STYLELINK };

	final static String INIT_LA_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_LAYERS + " (" + " id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ LA_FIELD_LAYER_NAME + " TEXT, " + LA_FIELD_LDESC + " TEXT, "
			+ LA_FIELD_CREATE_AT + " TEXT, " + LA_FIELD_DISPLAY + " TEXT);";

	final static String INIT_PM_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_PLACE + " (" + " id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ PM_FIELD_LAYER_NAME + " TEXT, " + PM_FIELD_PLACEMARK_NAME
			+ " TEXT, " + PM_FIELD_DESC + " TEXT, " + PM_FIELD_DISPLAY
			+ " TEXT, " + PM_FIELD_STYLELINK + " TEXT);";

	final static String DROP_LA_TABLE = "DROP TABLE IF EXISTS " + TABLE_LAYERS;
	final static String DROP_PM_TABLE = "DROP TABLE IF EXISTS " + TABLE_PLACE;

	private Context context;

	// 用在確認是否重複，ListSdCard使用
	private boolean duplicate = false;

	// =================================================================

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DB_VERSION);
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

	// ======================== LAYERS table methods ========================

	public void addLayer(Layer layer) {
		// 1. get reference to writable DB
		// 這邊duplicateCheck會關閉db，所以將db放到裡面，開關才不會錯誤
		if (!duplicateCheck(layer.getLayerName())) {
			SQLiteDatabase db = this.getWritableDatabase();
			// 2. create ContentValues to add key "column"/value
			ContentValues values = new ContentValues();
			values.put(LA_FIELD_LAYER_NAME, layer.getLayerName());
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
	 * @param title
	 * @return layer
	 */
	public Layer getLayer(String title) {
		// 1. get reference to readable DB
		SQLiteDatabase db = this.getReadableDatabase();
		Layer layer = new Layer();
		// 2. build query
		try {
			Cursor cursor = db.query(TABLE_LAYERS, // a. table
					LA_COLUMNS, // b. column names
					LA_FIELD_LAYER_NAME + "=?", // c. selections
					new String[] { title }, // d. selections args
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
			layer.setLDesc(cursor.getString(2));
			layer.setDisplay(cursor.getString(3));
			layer.setCreateAt(cursor.getString(4));
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
	public List<Layer> getAllLayer() {
		List<Layer> layers = new ArrayList<Layer>();
		// 1. build the query
		String query = "SELECT  * FROM " + TABLE_LAYERS;

		// 2. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		// 3. go over each row, build Layer and add it to list

		Layer layer = null;
		try {
			if (cursor.moveToFirst()) {
				do {
					layer = new Layer();
					layer.setId(cursor.getString(0));
					layer.setLayerName(cursor.getString(1));
					layer.setLDesc(cursor.getString(2));
					layer.setDisplay(cursor.getString(3));
					layer.setCreateAt(cursor.getString(4));
					// Add book to books
					layers.add(layer);
				} while (cursor.moveToNext());
			}
		} catch (IllegalStateException e) {
			Log.d("mdb", "DBHelper Class, " + "Error:" + e.toString());
		}
		db.close();
		// return books
		return layers;
	}// end of getAllLayer()

	public void deleteLayerRow(Layer layer) {
		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_LAYERS, LA_FIELD_ID + " = ?",
				new String[] { String.valueOf(layer.getId()) });

		// 3. close
		db.close();
	}// end of deleteLayer

	/**
	 * @param oldLayer
	 *            使用getLayer(int id)來取得
	 * @param newLayer
	 */
	public void updateLayerRow(Layer oldLayer, Layer newLayer) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(LA_FIELD_LAYER_NAME, newLayer.getLayerName());
		values.put(LA_FIELD_LDESC, newLayer.getDesc());
		values.put(LA_FIELD_DISPLAY, newLayer.getDisplay());
		values.put(LA_FIELD_CREATE_AT, newLayer.getCreateAt());

		db.update(TABLE_LAYERS, values, LA_FIELD_ID + "=?",
				new String[] { String.valueOf(oldLayer.getId()) });
		db.close();
	}// end of updateLayer

	/**
	 * 傳入的是kml的檔名，比對的是layerName，如果layerName有重複，則傳回true，反之則false
	 */
	public boolean duplicateCheck(String layerName) {
		// getAllLayer() 有開關db
		List<Layer> layers = getAllLayer();
		for (Layer l : layers) {
			if (layerName.equals(l.getLayerName())) {
				return true;
			}
		}
		return false;
	}

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

	// ======================== PLACE table methods ========================
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

}// end of DBHelper
