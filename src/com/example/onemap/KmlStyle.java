package com.example.onemap;

public class KmlStyle {
	private String id;
	private String layerName;
	private String styleName;
	private String styleContent;

	public KmlStyle() {

	}

	public KmlStyle(String id, String layerName, String styleName,
			String styleContent) {
		this.id = id;
		this.layerName = layerName;
		this.styleName = styleName;
		this.styleContent = styleContent;
	}

	public String getId() {
		return this.id;
	}

	public String getLayerName() {
		return this.layerName;
	}

	public String getStyleName() {
		return this.styleName;
	}

	public String getStyleContent() {
		return this.styleContent;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}

	public void setStyleName(String styleName) {
		this.styleName = styleName;
	}

	public void setStyleContent(String styleContent) {
		this.styleContent = styleContent;
	}
}// end of KmlStyle
