package org.pathvisio.wpclient.validators;

public class Validator {
	public static  boolean CheckNonAlpha(String input)
	{

	boolean hasNonAlpha = input.matches("^.*[^a-zA-Z0-9 ].*$");
	return (!hasNonAlpha);
	}
	public static  boolean CheckNonAlphaAllowColon(String input)
	{

	boolean hasNonAlpha = input.matches("^.*[^a-zA-Z0-9: ].*$");
	return (!hasNonAlpha);
	}
}
