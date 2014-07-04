package com.example.onemap;

public class Layer {
	private String id;
	private String layerPlaceName;
	private String lDesc;
	private String display;
	private String pDesc;
	private String styleLink;

	public Layer() {
	}

	public Layer(String LayerPlaceName, String desciption, String display,
			String pDesc, String styleLink) {
		super();
		this.layerPlaceName = LayerPlaceName;
		this.lDesc = desciption;
		this.display = display;
		this.pDesc = pDesc;
		this.styleLink = styleLink;
	}

	public String toString() {
		return String
				.format("[Id: %s,Title: %s, LDesc: %s, Display: %s, PDesc: %s, StyleLink: %s ]",
						id, layerPlaceName, lDesc, display, pDesc, styleLink);
	}

	public String getId() {
		return id;
	}

	public String getLayerPlaceName() {
		return layerPlaceName;
	}

	public String getDesc() {
		return lDesc;
	}

	public String getDisplay() {
		return display;
	}

	public String getPDesc() {
		return pDesc;
	}

	public String getStyleLink() {
		return styleLink;
	}

	/**
	 * 不要自己設id，dbHepler會幫忙自動設定
	 */
	public void setId(String id) {
		this.id = id;
	}

	public void setLayerPlaceName(String layerPlaceName) {
		this.layerPlaceName = layerPlaceName;
	}

	public void setlDesc(String description) {
		this.lDesc = description;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public void setPDesc(String pDesc) {
		this.pDesc = pDesc;
	}

	public void setStyleLink(String styleLink) {
		this.styleLink = styleLink;
	}
}
