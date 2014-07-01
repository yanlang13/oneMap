package com.example.onemap;

/**
 * kml的placeMark
 * @author acer
 *
 */
public class KmlPlaceMark {
	private String id;
	private String layerName;
	private String placeMarkName;
	private String style;
	private String coordinates;
	private String desc;

	public KmlPlaceMark() {
	}

	public KmlPlaceMark(String layerName, String placeMarkName, String style,
			String coordinates, String desc) {
		this.layerName = layerName;
		this.style = style;
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

	public String getStyle() {
		return this.style;
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

	public void setStyle(String style) {
		this.style = style;
	}

	public void setCoordinates(String coordinates) {
		this.coordinates = coordinates;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}// end of placeMark
