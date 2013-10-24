// PathVisio WP Client
// Plugin that provides a WikiPathways client for PathVisio.
// Copyright 2013 developed for Google Summer of Code
//
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// 
// http://www.apache.org/licenses/LICENSE-2.0 
//  
// Unless required by applicable law or agreed to in writing, software 
// distributed under the License is distributed on an "AS IS" BASIS, 
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
// See the License for the specific language governing permissions and 
// limitations under the License.
//
package org.pathvisio.wpclient.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * file handling operations
 * @author mkutmon
 */
public class FileUtils {
	
	/**
	 * deletes a directory recursively
	 */
	public static void deleteDirectory(File directory) {
		final File[] files = directory.listFiles();
		for (final File file : files) {
			if(file.isDirectory()) {
				FileUtils.deleteDirectory(file);
			} else {
				file.delete();
			}
		}
		directory.delete();
	}
	
	public static String getTimeStamp() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		String formattedDate = sdf.format(date);
		return formattedDate;
	}
	
}
