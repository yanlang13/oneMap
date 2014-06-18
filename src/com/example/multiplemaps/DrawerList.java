package com.example.multiplemaps;

/**
 * drawerLayout的list內容
 */
public final class DrawerList {
	private DrawerList() {
	}

	public static final DrawerListDetails[] LIST = {
			new DrawerListDetails("Manage Layers",
					R.drawable.drawer_layout_setting, LayersManage.class),
			new DrawerListDetails("List Layers",
					R.drawable.drawer_layout_setting, LayersList.class) };
}// end of DrawerList
