package com.example.multiplemaps;

public class Layer {
	private String id;
	private String title;
	private String desc;
	private String kmlString;
	private String display;

	public Layer() {
	}

	public Layer(String title, String desc, String kmlString, String display) {
		super();
		this.title = title;
		this.desc = desc;
		this.kmlString = kmlString;
		this.display = display;
	}

	@Override
	public String toString() {
		return String.format(
				"[Id: %s,Title: %s, Description: %s, Display: %s, kmlString: %s]",
				id, title, desc, display, kmlString);
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDesc() {
		return desc;
	}

	public String getKmlString() {
		return kmlString;
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

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setKmlString(String kmlString) {
		this.kmlString = kmlString;
	}

	public void setDisplay(String display) {
		this.display = display;
	}
}
