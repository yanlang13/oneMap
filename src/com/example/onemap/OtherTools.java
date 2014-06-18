package com.example.onemap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import android.os.Environment;
import android.util.Log;

public class OtherTools {
	private static final String DATABASE_NAME = "oneMaps.db";

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
		
		//如果database不存在
		if (!currentDB.exists()) {
			Log.d("mdb", "database is not exist.");
		}

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
		
		
	}// end of copyDBtoSDcard

}
