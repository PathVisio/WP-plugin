package org.pathvisio.wpclient.utils;

import java.io.File;

public class FileUtils {
	
	/**
	 * deletes a directory recursively
	 */
	public static void deleteDirectory(File directory) {
		final File[] files = directory.listFiles();
		for (final File file : files) {
			if(file.isDirectory()) {
				deleteDirectory(file);
			} else {
				file.delete();
			}
		}
		directory.delete();
	}
	
}
