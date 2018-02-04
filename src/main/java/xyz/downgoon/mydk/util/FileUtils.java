package xyz.downgoon.mydk.util;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

public class FileUtils {

	/**
	 * Locating a file in a classpath
	 * 
	 * @param classpathFileName
	 *            file name in classpath format. e.g. log4j.properties or
	 *            com/downgoon/log4j.properties
	 * @return absolute file location. if file not found, then return NULL
	 */
	public static File classpathFile(String classpathFileName) {
		URL url = Thread.currentThread().getContextClassLoader().getResource(classpathFileName);
		return url == null ? null : new File(url.getFile());
	}

	/**
	 * Tests whether the file in a classpath exists or not.
	 * 
	 * @param classpathFileName
	 *            file name in classpath format. e.g. log4j.properties or
	 *            com/downgoon/log4j.properties
	 * @return return true, if found, otherwise false.
	 */
	public static boolean hasClasspathFile(String classpathFileName) {
		return classpathFile(classpathFileName) != null;
	}

	/**
	 * Locating a file in a classpath
	 * 
	 * @param classpathFileName
	 *            file name in classpath format. e.g. log4j.properties or
	 *            com/downgoon/log4j.properties
	 * @return InputStream of the file. if file not found, then return NULL
	 */
	public static InputStream classpathStream(String classpathFileName) {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(classpathFileName);
	}

}
