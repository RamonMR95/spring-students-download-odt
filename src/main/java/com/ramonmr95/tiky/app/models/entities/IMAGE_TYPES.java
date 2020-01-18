package com.ramonmr95.tiky.app.models.entities;

import java.io.Serializable;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

public enum IMAGE_TYPES implements Serializable {

	PICTURE_TYPE_JPEG("jpeg", XWPFDocument.PICTURE_TYPE_JPEG),
	PICTURE_TYPE_JPG("jpg", XWPFDocument.PICTURE_TYPE_JPEG),
	PICTURE_TYPE_PNG("png",  XWPFDocument.PICTURE_TYPE_PNG),
	PICTURE_TYPE_GIF("gif",  XWPFDocument.PICTURE_TYPE_GIF);

	private String ext;
	private int type;

	IMAGE_TYPES(String ext, int type) {
		this.ext = ext;
		this.type = type;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
    public static int getTypes(String ext) {
        for (IMAGE_TYPES types : IMAGE_TYPES.values()) {
            if (types.ext.equalsIgnoreCase(ext)) {
            	return types.getType();
            }
        }
        return XWPFDocument.PICTURE_TYPE_JPEG;
    }
	
}
