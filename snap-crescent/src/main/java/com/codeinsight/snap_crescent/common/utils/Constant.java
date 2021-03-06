package com.codeinsight.snap_crescent.common.utils;

public class Constant {

	public static final String DEMO_ADDRESS = "demo.snapcrescent.com";
	public static final String DB_MYSQL = "MYSQL";
	public static final String DB_SQLITE = "SQLITE";

	public static final String METADATA_FILE_NAME = "File Name";
	public static final String METADATA_FILE_SIZE = "File Size";
	public static final String METADATA_FILE_MODIFIED_DATE = "File Modified Date";
	public static final String METADATA_FILE_TYPE_NAME = "Detected File Type Name";
	public static final String METADATA_FILE_TYPE_LONG_NAME = "Detected File Type Long Name";
	public static final String METADATA_MIME_TYPE = "Detected MIME Type";
	public static final String METADATA_FILE_EXTENSION = "Expected File Name Extension";
	public static final String METADATA_IMAGE_HEIGHT = "Image Height";
	public static final String METADATA_IMAGE_WIDTH = "Image Width";
	public static final String METADATA_CREATED_DATE = "Date/Time";
	public static final String METADATA_MODEL = "Model";
	public static final String METADATA_FSTOP = "F-Number";
	
	public static final String PHOTO_FOLDER = "/photos/";
	public static final String VIDEO_FOLDER = "/videos/";
	public static final String THUMBNAIL_FOLDER = "/thumbnails/";

	public static final String SIMPLE_DATE_FORMAT = "yyyy:mm:dd hh:mm:ss";

	public static enum FILE_TYPE {
		PHOTO, VIDEO, THUMBNAIL
	}
	
	public enum ResultType {
		OPTION, SEARCH, FULL;
	}

}
