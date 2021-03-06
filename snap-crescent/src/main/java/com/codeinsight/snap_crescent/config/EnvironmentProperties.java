package com.codeinsight.snap_crescent.config;

public class EnvironmentProperties {

	public static String SQL_DB_TYPE = System.getenv("SQL_DB_TYPE");
	public static String SQL_URL = System.getenv("SQL_URL");
	public static String SQL_USER = System.getenv("SQL_USER");
	public static String SQL_PASSWORD = System.getenv("SQL_PASSWORD");
	
	
    public static String STORAGE_PATH = System.getenv("STORAGE_PATH") == null? "/media/" : System.getenv("STORAGE_PATH");
	public static String DATA_PATH = "/data/";

}