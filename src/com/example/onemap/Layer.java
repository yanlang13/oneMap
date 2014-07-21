package com.example.onemap;

import java.util.Locale;

public class Layer {
	private String id;
	private String layerName;
	private int layerSize;
	private String lDesc;
	private String display;
	private String createAt;

	public Layer() {
		
	}

	public Layer(String layerName, int layerSize,String desciption, String display,
			String pDesc, String styleLink, String CreateAt) {
		super();
		this.layerName = layerName;
		this.layerSize = layerSize;
		this.lDesc = desciption;
		this.display = display;
		this.createAt = createAt;
	}

	public String toString() {
		return String.format(Locale.getDefault(),
				"[Id: %s,LayerName: %s,LayerSize: %s, LDesc: %s, Display: %s , CreateAt: %s]",
				id, layerName,layerSize, lDesc, display, createAt);
	}

	public String getId() {
		return id;
	}

	public String getLayerName() {
		return layerName;
	}
	
	public int getLayerSize(){
		return layerSize;
	}

	public String getDesc() {
		return lDesc;
	}

	public String getDisplay() {
		return display;
	}

	public String getCreateAt() {
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
	
	public void setLayerSize(int size){
		this.layerSize = size;
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
	public void setCreateAt(String createAt) {
		this.createAt = createAt;
	}

}
