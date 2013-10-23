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
package org.pathvisio.wpclient.validators;

/**
 * functions to validate the user input
 * @author SravanthiSinha
 *
 */
public class Validator {
	
	public static boolean CheckNonAlpha(String input) {
		boolean hasNonAlpha = input.matches("^.*[^a-zA-Z0-9 ].*$");
		return (!hasNonAlpha);
	}

	public static boolean CheckNonAlphaAllowColon(String input) {
		boolean hasNonAlpha = input.matches("^.*[^a-zA-Z0-9: ].*$");
		return (!hasNonAlpha);
	}
}
