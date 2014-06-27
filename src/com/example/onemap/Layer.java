package com.example.onemap;

public class Layer {
	private String id;
	private String layerName;
	private String lDesc;
	private String display;

	public Layer() {
	}

	public Layer(String LayerName, String desciption, String display) {
		super();
		this.layerName = LayerName;
		this.lDesc = desciption;
		this.display = display;
	}

	@Override
	public String toString() {
		return String.format(
				"[Id: %s,Title: %s, Description: %s, Display: %s]", id, layerName,
				lDesc, display);
	}

	public String getId() {
		return id;
	}

	public String getLayerName() {
		return layerName;
	}

	public String getDesc() {
		return lDesc;
	}

	public String getDisplay() {
		return display;
	}

	/**
	 * 不要自己設id，dbHepler會幫忙自動設定
	 */
	public void setId(String id) {
		this.id = id;
	}

	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}

	public void setDesc(String description) {
		this.lDesc = description;
	}

	public void setDisplay(String display) {
		this.display = display;
	}
}
