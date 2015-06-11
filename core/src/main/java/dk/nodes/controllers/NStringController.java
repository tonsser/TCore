package dk.nodes.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormatSymbols;

import dk.nodes.utils.NLog;

/**
 * @author Casper Rasmussen - 2012
 */
@Deprecated
public class NStringController {
	/**
	 * Use this method to check if string is not null and length is bigger than minimumsize 
	 * @param input
	 * @param minimumSize
	 * @return boolean
	 */
	public static boolean hasValue(String input, int minimumSize) {
		if(input!=null && input.length()>minimumSize)
			return true;
		else
			return false;
	}

	/**
	 * Use this method to check if the String is not null and length over 0
	 * @param input
	 * @return boolean
	 */
	public static boolean hasValue(String input) {
		return hasValue(input,0);
	}

	public static boolean hasValueAndNotNull(String input){
		if(input == null)
			return false;
		else if(input.length() == 0)
			return false;
		else if(input.equals("null"))
			return false;
		else
			return true;
	}
	/**
	 * This method will return the same string with all letters as lower case but first letter which is uppercase
	 * @param input
	 * @return
	 */
	public static String toStringWithCaptalizeFirstLetterOnly(String input){
		input = input.toLowerCase();
		return input.replaceFirst(String.valueOf(input.charAt(0)), String.valueOf(Character.toUpperCase(input
				.charAt(0))));
	}

	public static String convertStreamToString(InputStream is) {
		ByteArrayOutputStream oas = new ByteArrayOutputStream();
		try {
			copyStream(is, oas);
			String t = oas.toString();
		
			oas.close();
			oas = null;
			return t;
		} catch (IOException e) {
			NLog.e("NStringController convertStreamToString", e);
			try {
				oas.close();
				oas = null;
			} catch (IOException e1) {
			}
			return null;
		}
	}

	public static void copyStream(InputStream is, OutputStream os){
		final int buffer_size = 2048;
		try
		{
			byte[] bytes=new byte[buffer_size];
			for(;;)
			{
				int count=is.read(bytes, 0, buffer_size);
				if(count==-1)
					break;
				os.write(bytes, 0, count);
			}
		}
		catch(Exception ex){}
	}

	public static String removeSpaceInEnd(String input){
		String outputString = input;
		if(input.endsWith(" "))
			outputString = input.substring(0,input.length()-2);

		return outputString;
	}

	public static boolean isStringNumeric( String str )
	{
		if(str == null)
			return false;

		if(!hasValue(str))
			return false;

		DecimalFormatSymbols currentLocaleSymbols = DecimalFormatSymbols.getInstance();
		char localeMinusSign = currentLocaleSymbols.getMinusSign();

		if ( !Character.isDigit(str.charAt(0)) && str.charAt( 0 ) != localeMinusSign ) return false;

		boolean isDecimalSeparatorFound = false;
		char localeDecimalSeparator = currentLocaleSymbols.getDecimalSeparator();

		for ( char c : str.substring( 1 ).toCharArray() )
		{
			if ( !Character.isDigit(c) )
			{
				if ( c == localeDecimalSeparator && !isDecimalSeparatorFound )
				{
					isDecimalSeparatorFound = true;
					continue;
				}
				return false;
			}
		}
		return true;
	}
}
