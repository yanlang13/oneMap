package com.example.onemap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class OtherTools {
	private static final String DATABASE_NAME = "oneMaps.db";
	
	/**
	 * 輸入file轉為string format
	 * @param file
	 * @return string
	 */
	public static String fileToString(File file){
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			String line;
			StringBuilder sb = new StringBuilder();

			while ((line = br.readLine()) != null) {
				sb.append(line.trim());
			}
			br.close();
			return sb.toString();

		} catch (FileNotFoundException e) {
			Log.d("mdb", "TaskAddInput, " + e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			Log.d("mdb", "TaskAddInput, " + e.toString());
			e.printStackTrace();
		}
		return null;
	}// end of kmlToString
	
	
	/**
	 * 路徑都是寫死的 => onemap/database/ to sd card 
	 */
	public static void copyDBtoSDcard() {
		// 輸出位置，預設位置為 /mnt/sdcard
		File sd = Environment.getExternalStorageDirectory();

		// 取得系統的資料擺放目錄，預設位置為 /data
		File data = Environment.getDataDirectory();
		FileChannel source = null;
		FileChannel destination = null;
		// 存放路徑+檔名

		String currentDBPath = "/data/" + "com.example.onemap" + "/databases/"
				+ DATABASE_NAME;

		// 輸出檔名
		String backupDBPath = DATABASE_NAME;

		// new File的兩個params組合起來就是路徑
		File currentDB = new File(data, currentDBPath);

		// 如果database存在
		if (currentDB.exists()) {
			File backupDB = new File(sd, backupDBPath);

			if (backupDB != null) { // backupDB.exist()
				backupDB.delete();
				Log.d("mdb", "delete database file");
			}
			try {
				// file channel 要跟fileinput/outputStream做組合使用
				source = new FileInputStream(currentDB).getChannel();
				destination = new FileOutputStream(backupDB).getChannel();
				// read and copy by byte
				destination.transferFrom(source, 0, source.size());
				source.close();
				destination.close();
				Log.d("mdb", "file exported");
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			Log.d("mdb", "database is not exist.");
		}

	}// end of copyDBtoSDcard

}
