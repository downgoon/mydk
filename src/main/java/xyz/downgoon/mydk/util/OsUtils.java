package xyz.downgoon.mydk.util;

import com.sun.javafx.PlatformUtil;

@SuppressWarnings("restriction")
public class OsUtils {

	public static boolean isWindows() {
		return PlatformUtil.isWindows();
	}
	
	public static boolean isMac() {
		return PlatformUtil.isMac();
	}
	
	public static boolean isLinux() {
		return PlatformUtil.isLinux();
	}
	
	public static String osName() {
		return  System.getProperty("os.name");
	}
	
}
