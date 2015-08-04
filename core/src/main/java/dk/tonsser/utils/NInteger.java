package dk.tonsser.utils;

public class NInteger {

	public static int parseInt(String input,int defaultValue){
		try{
			return Integer.parseInt(input);
		}
		catch(Exception e){
			return defaultValue;
		}
	}
}
