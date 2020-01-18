package com.ramonmr95.tiky.app.models.entities;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

public enum IMAGE_TYPES {

	JPEG(XWPFDocument.PICTURE_TYPE_JPEG, "jpeg"),
	JPG(XWPFDocument.PICTURE_TYPE_JPEG, "jpg"),
	PNG(XWPFDocument.PICTURE_TYPE_PNG, "png"),
	GIF(XWPFDocument.PICTURE_TYPE_GIF, "gif");

	private String ext;
	private int type;

	IMAGE_TYPES(int type, String ext) {
		this.type = type;
		this.ext = ext;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

}
