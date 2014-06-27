package com.example.onemap;

/**
 * kml的placeMark
 * @author acer
 *
 */
public class PlaceMark {
	private String id;
	private String layerName;
	private String placeMarkName;
	private String styleUrl;
	private String coordinates;
	private String desc;

	public PlaceMark() {
	}

	public PlaceMark(String layerName, String placeMarkName, String styleUrl,
			String coordinates, String desc) {
		this.layerName = layerName;
		this.styleUrl = styleUrl;
		this.placeMarkName = placeMarkName;
		this.coordinates = coordinates;
		this.desc = desc;
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

	public String getStyleUrl() {
		return this.styleUrl;
	}

	public String getCoordinates() {
		return this.coordinates;
	}

	public String getDesc() {
		return this.desc;
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

	public void setStyleUrl(String styleUrl) {
		this.styleUrl = styleUrl;
	}

	public void setCoordinates(String coordinates) {
		this.coordinates = coordinates;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}// end of placeMark
