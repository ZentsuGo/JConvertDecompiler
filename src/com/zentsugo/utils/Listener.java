package com.zentsugo.utils;

import java.io.File;
import java.util.LinkedList;

public class Listener {
	public static final String VERSION = "1.0.0";
	public static LinkedList<File> files = new LinkedList<File>();
	public static File importLocation = null;
	public static File exportLocation = null;
	public static File importFolderLocation = null;
	
	public static boolean compileMode = false;
	
	public static int floaded = 0;
}
