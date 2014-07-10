package com.example.onemap;

public class Layer {
	private String id;
	private String layerName;
	private String lDesc;
	private String display;
	private String createAt;
	
	public Layer() {
		
	}

	public Layer(String LayerName, String desciption, String display,
			String pDesc, String styleLink, String CreateAt) {
		super();
		this.layerName = LayerName;
		this.lDesc = desciption;
		this.display = display;
		this.createAt = createAt;
	}

	public String toString() {
		return String.format(
				"[Id: %s,Title: %s, LDesc: %s, Display: %s , CreateAt: %s]",
				id, layerName, lDesc, display, createAt);
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
	
	public String getCreateAt(){
		return createAt;
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

	public void setLDesc(String description) {
		this.lDesc = description;
	}

	public void setDisplay(String display) {
		this.display = display;
	}
	
	/**
	 * set dataTime from OtherTools.get
	 */
	public void setCreateAt(String createAt){
		this.createAt = createAt;
	}
	
}
