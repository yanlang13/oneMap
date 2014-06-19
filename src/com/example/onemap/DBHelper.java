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
	private static final String TABLE_LAYERS = "layers";
	// 欄位1，自動產生
	private static final String FIELD_ID = "id";
	// 欄位2, string
	private static final String FIELD_TITLE = "LayerTitle";
	// 欄位3, string
	private static final String FIELD_DESC = "LayerDesc";
	// 欄位4, string
	private static final String FIELD_KML_STRING = "KmlString";
	// 欄位5, string, true and false
	private static final String FIELD_DISPLAY = "Display";

	final static String[] COLUMNS = { FIELD_ID, FIELD_TITLE, FIELD_DESC,
			FIELD_KML_STRING, FIELD_DISPLAY };

	final static String INIT_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_LAYERS + " (" + " id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ FIELD_TITLE + " TEXT, " + FIELD_DESC + " TEXT, "
			+ FIELD_KML_STRING + " TEXT, " + FIELD_DISPLAY + " TEXT);";

	final static String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_LAYERS;


	private Context context;
	
	//用在確認是否重複，ListSdCard使用
	private boolean duplicate = false;

	// =================================================================

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DB_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 只有當getRead/Writable...時才會做onCreate
		Log.d("mdb", "DBHepler onCreate");
		db.execSQL(INIT_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d("mdb", "db onUpgrade");
		db.execSQL(DROP_TABLE);
	}

	// ==============================================================DBControl
	public void addLayer(Layer layer) {
		// 1. get reference to writable DB
		// 這邊duplicateCheck會關閉db，所以將db放到裡面，開關才不會錯誤
		if (!duplicateCheck(layer.getTitle())) {
			SQLiteDatabase db = this.getWritableDatabase();
			// 2. create ContentValues to add key "column"/value
			ContentValues values = new ContentValues();
			values.put(FIELD_TITLE, layer.getTitle());
			values.put(FIELD_DESC, layer.getDesc());
			values.put(FIELD_KML_STRING, layer.getKmlString());
			values.put(FIELD_DISPLAY, layer.getDisplay());
			// 3. insert
			db.insert(TABLE_LAYERS, null, values);
			// 4. close
			db.close();
			duplicate = false;
		}else{
			duplicate = true;
			Toast.makeText(context, R.string.duplicate_name, Toast.LENGTH_SHORT).show();
		}
	}// end of addLayer

	/**
	 * @param title
	 * @return layer
	 */
	public Layer getLater(String title) {
		// 1. get reference to readable DB
		SQLiteDatabase db = this.getReadableDatabase();
		Layer layer = new Layer();
		// 2. build query
		try {
			Cursor cursor = db.query(TABLE_LAYERS, // a. table
					COLUMNS, // b. column names
					FIELD_TITLE + "=?", // c. selections
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
			layer.setTitle(cursor.getString(1));
			layer.setDesc(cursor.getString(2));
			layer.setKmlString(cursor.getString(3));
			layer.setDisplay(cursor.getString(4));
			// 5. return book
		} catch (CursorIndexOutOfBoundsException e) {
			Log.d("mdb", "DBHelper Class, " + "Error:" + e.toString());
		}
		db.close();
		return layer;
	}

	public Layer getLayer(int id) {
		// 1. get reference to readable DB
		SQLiteDatabase db = this.getReadableDatabase();

		Layer layer = new Layer();
		// 2. build query
		try {
			Cursor cursor = db.query(TABLE_LAYERS, // a. table
					COLUMNS, // b. column names
					FIELD_ID + "=?", // c. selections
					new String[] { String.valueOf(id) }, // d. selections args
					null, // e. group by
					null, // f. having
					null, // g. order by
					null); // h. limit
			// 3. if we got results get the first one
			if (cursor != null)
				cursor.moveToFirst();
			// 4. build book object
			layer.setId(cursor.getString(0));
			layer.setTitle(cursor.getString(1));
			layer.setDesc(cursor.getString(2));
			layer.setKmlString(cursor.getString(3));
			layer.setDisplay(cursor.getString(4));
			// 5. return book
		} catch (CursorIndexOutOfBoundsException e) {
			Log.d("mdb", "DBHelper Class, " + "Error:" + e.toString());
		}

		db.close();
		return layer;

	}// end of getLayer

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
					layer.setTitle(cursor.getString(1));
					layer.setDesc(cursor.getString(2));
					layer.setKmlString(cursor.getString(3));
					layer.setDisplay(cursor.getString(4));
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
		db.delete(TABLE_LAYERS, FIELD_ID + " = ?",
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
		values.put(FIELD_TITLE, newLayer.getTitle());
		values.put(FIELD_DESC, newLayer.getDesc());
		values.put(FIELD_KML_STRING, newLayer.getKmlString());
		values.put(FIELD_DISPLAY, newLayer.getDisplay());
		db.update(TABLE_LAYERS, values, FIELD_ID + "=?",
				new String[] { String.valueOf(oldLayer.getId()) });
		db.close();
	}// end of updateLayer

	/**
	 * 如果Title有重複，則傳回true，反之則false
	 */
	public boolean duplicateCheck(String title) {
		// getAllLayer() 有開關db
		List<Layer> layers = getAllLayer();
		for (Layer l : layers) {
			if (title.equals(l.getTitle())) {
				Log.d("mdb", "Duplicate Title");
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
	public boolean getDuplicate(){
		return duplicate;
	}// end of getDuplicate
	// ==============================================================DBControled
}
