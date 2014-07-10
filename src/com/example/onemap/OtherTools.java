package com.example.onemap;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.google.android.gms.maps.model.LatLng;

import android.graphics.Color;
import android.os.Environment;
import android.util.Log;

public class OtherTools {
	private static final String DATABASE_NAME = "oneMaps.db";

	/**
	 * 輸入file轉為string format
	 * 
	 * @param file
	 * @return string
	 */
	public static String fileToString(File file) {
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

	/**
	 * 壓縮string
	 * 
	 * @param string
	 * @return
	 */
	public static String compressString(String string) {
		String outStr = string;
		try {
			if (string == null || string.length() == 0) {
				return string;
			}
			Log.d("mdb", "String length : " + string.length());
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			GZIPOutputStream gzip = new GZIPOutputStream(out);
			gzip.write(string.getBytes());
			gzip.close();
			outStr = out.toString("ISO-8859-1");
			Log.d("mdb", "Output String lenght : " + outStr.length());
		} catch (IOException e) {
			Log.d("mdb", "OtherTools CLAss:" + e.toString());
		}
		return outStr;
	}// end of compressString(String string)

	/**
	 * 解壓string
	 * 
	 * @param string
	 * @return
	 */
	public static String decompressString(String string) {
		String outStr = string;
		try {
			if (string == null || string.length() == 0) {
				return string;
			}
			GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(
					string.getBytes("ISO-8859-1")));
			BufferedReader bf = new BufferedReader(new InputStreamReader(gis,
					"ISO-8859-1"));
			outStr = "";
			String line;
			while ((line = bf.readLine()) != null) {
				outStr += line;
			}
		} catch (IOException e) {
			Log.d("mdb", "OtherTools CLAss:" + e.toString());
		}
		return outStr;
	}// end of decompressString(String string)

	/**
	 * 將jsonObject寫到txt檔案
	 * 
	 * @param dirPath
	 * @param fileName
	 * @param jsonObject
	 * @return
	 */
	public static File writeJsonToFile(File dirPath, String fileName,
			JSONObject jsonObject) {
		File txtFile = new File(dirPath, fileName);
		String styleContnet = jsonObject.toString();
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(txtFile);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			osw.write(styleContnet);
			osw.close();
			fos.close();
		} catch (FileNotFoundException e) {
			Log.d("mdb", "TaskKmlToDataBase.class" + e.toString());
		} catch (IOException e) {
			Log.d("mdb", "TaskKmlToDataBase.class" + e.toString());
		}
		return txtFile;
	}// end of writeJSONObjecToTxt

	/**
	 * translate ABGR(String) to ARGB(int)
	 * 
	 * @param abgr
	 * @return
	 */
	public static int kmlColorToARGB(String abgr) {
		String stringAlpha = abgr.substring(0, 2);
		String strinfBlue = abgr.substring(2, 4);
		String stringGreen = abgr.substring(4, 6);
		String strinfRed = abgr.substring(6);

		// 主要是透過parseColor將StringARGB轉為int
		int argb = Color.parseColor("#" + stringAlpha + strinfRed + stringGreen
				+ strinfBlue);
		return argb;
	}// end of kmlColorToARGB

	/**
	 * get date time YYYY-MM-DD HH:mm:ss
	 */
	public static String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}// end of getDataTime
	
	/**
	 * Translation KML Coordinate String to LatLngs (copy from ParsingKmlString)
	 * 
	 * @param coordinates
	 * @return
	 */
	public static ArrayList<LatLng> transCoorStringToLatLngs(String coordinates) {
		// 取出的kmlString轉為list，split用 | 分隔使用的分隔符號
		List<String> listStringCoordinates = new ArrayList<String>(
				Arrays.asList(coordinates.split(",| ")));

		ArrayList<LatLng> latLngs = new ArrayList<LatLng>();

		int length = listStringCoordinates.size();

		for (int i = 0; i < length - 1; i += 3) {
			// 取kmlString的coordinates 轉double
			// d1為longitude
			double longitude = Double.valueOf(listStringCoordinates.get(i));
			// d2為latitude
			double latitude = Double.valueOf(listStringCoordinates.get(i + 1));
			LatLng latLng = new LatLng(latitude, longitude);

			// 放LatLng到ArrayList
			latLngs.add(latLng);
		}
		return latLngs;
	}// end of transCoorStringToLatLngs
}// end of Class OtherTools
