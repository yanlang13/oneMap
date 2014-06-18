package com.example.multiplemaps;

import android.app.Activity;

/**
 * drawer中的list的detail
 * 需求為picture、png、activity
 */
public class DrawerListDetails {
	public final String title;
	public final int image;
	public final Class<? extends Activity> activityClass;
	public DrawerListDetails(String title,int image, Class<? extends Activity> activityClass){
		this.image = image;
		this.title = title;
		this.activityClass = activityClass;
	}// end of DrawerListDetials
	
	
}// end of DrawerListDetials
