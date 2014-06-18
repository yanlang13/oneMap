package com.example.multiplemaps;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 透過sharedPrederences存取各種defaultSettings。
 */
public class DefaultSettings {
	private static final String BASE_MAP = "Base Map";
	private static final String UPPER_MAP_SPINNER_POSITION = "Upper Spinner Position";
	private SharedPreferences sharedSettings; // 各種UI設定存檔
	private SharedPreferences.Editor defaultSettings;// 各種UI設定存檔

	public DefaultSettings(Context context) {
		this.sharedSettings = context.getSharedPreferences("DefaultSettings",
				Context.MODE_PRIVATE);
		this.defaultSettings = sharedSettings.edit();
	}
	/**
	 * 設定base map(google)
	 * @param position
	 */
	public void setBaseMap(int position) {
		defaultSettings.putInt(BASE_MAP, position);
		defaultSettings.commit();
	}// end of setUpperMapLayout
	
	/**
	 * 取得base map(google)
	 * @return
	 */
	public int getBaseMap() {
		return sharedSettings.getInt(BASE_MAP, 1);
	}// end of getUpperMapLayout

	/**
	 * @param position spinner list的位置
	 */
	public void setBaseMapSpineerPosition(int position) {
		defaultSettings.putInt(UPPER_MAP_SPINNER_POSITION, position);
		defaultSettings.commit();
	}// end of setUpperSpinnerPosition
	
	/**
	 * @return position spinner list的位置
	 */
	public int getBaseMapSpineerPosition() {
		return sharedSettings.getInt(UPPER_MAP_SPINNER_POSITION, 1);
	}// end of getUpperSpinnerPosition
	

}// end of DefaultSettings
