package com.example.onemap;

import android.provider.BaseColumns;

/**
 * 提供dbhelper的String
 * 
 * @author acer
 * 
 */
public class AcrossConstants implements BaseColumns {
	public static final String DATABASE_NAME = "oneMaps.db";
	public static final int DATABASE_VERSION = 1;
	public static final String TABLE_LAYERS = "Layers";
	public static final String TABLE_PLACE = "PlaceMarks";

	// =======================================================================
	// ===== COLUMN OF LAYERS ================================================
	// =======================================================================
	public static final String LA_FIELD_ID = "_id";
	public static final String LA_FIELD_LAYER_NAME = "LayerName";
	public static final String LA_FIELD_LAYER_SIZE = "LayerSize";
	public static final String LA_FIELD_LDESC = "LayerDescription";
	public static final String LA_FIELD_DISPLAY = "Display";
	public static final String LA_FIELD_CREATE_AT = "CreateAt";

	// =======================================================================
	// ===== COLUMN OF PLACEMARK =============================================
	// =======================================================================
	public static final String PM_FIELD_ID = "_id";
	public static final String PM_FIELD_LAYER_NAME = "LayerName";
	public static final String PM_FIELD_PLACEMARK_NAME = "PlaceMarkName";
	public static final String PM_FIELD_DESC = "PlaceMarkDescription";
	public static final String PM_FIELD_DISPLAY = "Display";
	public static final String PM_FIELD_STYLELINK = "StyleLink";

	// =======================================================================
	// ==== COLUMN LIST ======================================================
	// =======================================================================
	public static final String[] LA_COLUMNS = { LA_FIELD_ID,
			LA_FIELD_LAYER_NAME, LA_FIELD_LAYER_SIZE, LA_FIELD_LDESC,
			LA_FIELD_DISPLAY, LA_FIELD_CREATE_AT };

	public static final String[] PM_COLUMNS = { PM_FIELD_ID,
			PM_FIELD_LAYER_NAME, PM_FIELD_PLACEMARK_NAME, PM_FIELD_DESC,
			PM_FIELD_DISPLAY, PM_FIELD_STYLELINK };

	// =======================================================================
	// ===== SQLITE SYNTAX ===================================================
	// =======================================================================
	// TODO CREATE FROM COLUMN (該起上傳功能的前置)
	public static final String INIT_LA_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_LAYERS + " (" + " _id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ LA_FIELD_LAYER_NAME + " TEXT, " + LA_FIELD_LAYER_SIZE
			+ " INTEGER, " + LA_FIELD_LDESC + " TEXT, " + LA_FIELD_CREATE_AT
			+ " TEXT, " + LA_FIELD_DISPLAY + " TEXT);";

	public static final String INIT_PM_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_PLACE + " (" + " _id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ PM_FIELD_LAYER_NAME + " TEXT, " + PM_FIELD_PLACEMARK_NAME
			+ " TEXT, " + PM_FIELD_DESC + " TEXT, " + PM_FIELD_DISPLAY
			+ " TEXT, " + PM_FIELD_STYLELINK + " TEXT);";

	public static final String DROP_LA_TABLE = "DROP TABLE IF EXISTS "
			+ TABLE_LAYERS;
	public static final String DROP_PM_TABLE = "DROP TABLE IF EXISTS "
			+ TABLE_PLACE;
}// end of AcrossConstants
