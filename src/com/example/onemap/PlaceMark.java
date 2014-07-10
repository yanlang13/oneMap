package com.example.onemap;

/**
 * @author acer
 * 
 */
public class PlaceMark {
	private String id;
	private String layerName;
	private String placeMarkName;
	private String pDesc;
	private String display;
	private String styleLink;

	public PlaceMark() {
	}

	public PlaceMark(String layerName, String placeMarkName, String pDesc,
			String display, String styleLink) {
		this.layerName = layerName;
		this.placeMarkName = placeMarkName;
		this.pDesc = pDesc;
		this.display = display;
		this.styleLink = styleLink;
	}

	public String getId() {
		return this.id;
	}
	
	public String getLayerName() {
		return this.layerName;
	}

	public String getPlaceMarkName() {
		return this.placeMarkName;
	}

	public String getPDesc() {
		return this.pDesc;
	}
	

	public String getDisplay() {
		return this.display;
	}

	public String getStyleLink() {
		return this.styleLink;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}

	public void setPlaceMarkName(String placeMarkName) {
		this.placeMarkName = placeMarkName;
	}

	public void setPDesc(String pDesc) {
		this.pDesc = pDesc;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	
	public void setStyleLink(String styleLink) {
		this.styleLink = styleLink;
	}
}// end of placeMark