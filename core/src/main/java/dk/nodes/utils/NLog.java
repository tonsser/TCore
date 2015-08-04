package dk.nodes.utils;

import android.util.Log;

import java.util.Map;
import java.util.Map.Entry;

import dk.nodes.base.NBaseApplication;

public class NLog {

	private static String TEST = "test";

	public static void d(Map<String,String> map){
		for (Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			NLog.d(key,value);
		}
	}

	public static boolean isDebugEnabled(){
		if(NBaseApplication.getInstance() == null || NBaseApplication.getInstance().DEBUG)
			return true;
		else
			return false;
	}	

	public  static void d(String tag, String msg){
		if(NBaseApplication.getInstance() == null || NBaseApplication.getInstance().DEBUG)
			Log.d(tag, msg);
	}	

	public  static void d( String msg){
		String tag = "null";
		try{
			tag=  Thread.currentThread().getStackTrace()[2].getClass() +"  "+ Thread.currentThread().getStackTrace()[2].getMethodName();
		}
		catch(Exception e){
			e("NLOG e",e);
		}

		if(NBaseApplication.getInstance() == null || NBaseApplication.getInstance().DEBUG)
			Log.d(tag, msg);
	}

	public  static void w(String tag, String msg){
		if(NBaseApplication.getInstance() == null || NBaseApplication.getInstance().DEBUG)
			Log.w(tag, msg);

	}

	public  static void w( String msg){
		String tag = "null";
		try{
			tag=  Thread.currentThread().getStackTrace()[2].getClass() +"  "+ Thread.currentThread().getStackTrace()[2].getMethodName();
		}
		catch(Exception e){
			e("NLOG e",e);
		}

		if(NBaseApplication.getInstance() == null || NBaseApplication.getInstance().DEBUG)
			Log.w(tag, msg);
	}

	public  static void i(String tag, String msg){
		if(NBaseApplication.getInstance() == null || NBaseApplication.getInstance().DEBUG)
			Log.i(tag, msg);

	}

	public  static void i( String msg){
		String tag = "null";
		try{
			tag=  Thread.currentThread().getStackTrace()[2].getClass() +"  "+ Thread.currentThread().getStackTrace()[2].getMethodName();
		}
		catch(Exception e){
			e("NLOG e",e);
		}

		if(NBaseApplication.getInstance() == null || NBaseApplication.getInstance().DEBUG)
			Log.i(tag, msg);
	}

	public  static void v(String tag, String msg){
		if(NBaseApplication.getInstance() == null || NBaseApplication.getInstance().DEBUG)
			Log.v(tag, msg);
	}

	public  static void v( String msg){
		String tag = "null";
		try{
			tag=  Thread.currentThread().getStackTrace()[2].getClass() +"  "+ Thread.currentThread().getStackTrace()[2].getMethodName();
		}
		catch(Exception e){
			e("NLOG e",e);
		}

		if(NBaseApplication.getInstance() == null || NBaseApplication.getInstance().DEBUG)
			Log.v(tag, msg);
	}

	public  static void e(String tag, String msg){
		Log.e(tag, msg);
	}
	public  static void e(String tag, Exception e){
		Log.e(tag, e.toString());
		if(NBaseApplication.getInstance() == null || NBaseApplication.getInstance().DEBUG)
			e.printStackTrace();
	}

	public  static void e(Exception e){
		String tag = "null";
		try{
			tag=  Thread.currentThread().getStackTrace()[0].getClass() +"  "+ Thread.currentThread().getStackTrace()[0].getMethodName();
		}
		catch(Exception ee){
			e("NLOG e",e);
		}

		Log.e(tag, e.toString());
		if(NBaseApplication.getInstance() == null || NBaseApplication.getInstance().DEBUG)
			e.printStackTrace();
	}

	private static long lastTime = 0;
	private static long lastLineNumber = 0;

	
	/**
	 * For measuring performance easily (it displays delta time between p() calls.
	 * I have hardcoded the log-tag to "performance" for easy filtering in logcat.
	 * 
	 * Start by calling NLog.p(true) to start a new "session"
	 */
	public  static void p(){
		p(null);
	}

	/**
	 * For measuring performance easily (it displays delta time between p() calls.
	 * I have hardcoded the log-tag to "performance" for easy filtering in logcat.
	 * 
	 * Start by calling NLog.p(true) to start a new "session"
	 *
	 * @param reset will reset it, so you have a new performance "session".
	 */
	public static void p(boolean reset){
		p(reset, null);
	}

	/**
	 * For measuring performance easily (it displays delta time between p() calls.
	 * I have hardcoded the log-tag to "performance" for easy filtering in logcat.
	 * 
	 * Start by calling NLog.p(true) to start a new "session"
	 *
	 * @param msg optional message to be displayed at the end of the log-message
	 */
	public static void p(Object msg){
		p(false, msg);
	}

	/**
	 * For measuring performance easily (it displays delta time between p() calls.
	 * I have hardcoded the log-tag to "performance" for easy filtering in logcat.
	 * 
	 * Start by calling NLog.p(true) to start a new "session"
	 *
	 * @param reset will reset it, so you have a new performance "session".
	 * @param msg optional message to be displayed at the end of the log-message
	 */
	public static void p(boolean reset, Object msg){
		if(msg == null)
			msg = "";

		StackTraceElement[] ste = Thread.currentThread().getStackTrace();

		int entry = 3;

		String fullClassName = ste[entry].getClassName();
		String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
		String methodName = Thread.currentThread().getStackTrace()[entry].getMethodName();
		String lineNumber = String.valueOf(ste[entry].getLineNumber());
		
		while(ste.length > entry && methodName.equals("p")){
			entry ++;
			lineNumber = String.valueOf(ste[entry].getLineNumber());
			methodName = Thread.currentThread().getStackTrace()[entry].getMethodName();
			fullClassName = ste[entry].getClassName();
			className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
		}
		
		if(reset){
			lastTime = System.currentTimeMillis();
			lastLineNumber = 0;
			d("performance"," ");
			d("performance","---Start performance @"+lineNumber+" ("+className+"."+methodName+") "+msg+"---");
		}
		else{
			String delta = String.valueOf(System.currentTimeMillis() - lastTime);
			d("performance",delta+" ms. From "+lastLineNumber+" to "+lineNumber+" ("+className+"."+methodName+") "+msg);
		}

		lastLineNumber = ste[entry].getLineNumber();
		lastTime = System.currentTimeMillis();
	}

	public static void l(){
		System.currentTimeMillis();
		String tag = "null";
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		try{
			tag=  ste[3].getClassName();
		}
		catch(Exception e){
			e("NLOG e",e);
		}

		String fullClassName = ste[3].getClassName();
		String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
		String methodName = ste[3].getMethodName();
		String lineNumber = String.valueOf(ste[3].getLineNumber());

		d(tag, Thread.currentThread().getStackTrace()[3].getMethodName()+" at " + fullClassName + "." + methodName + "("
				+ className + ".java:" + lineNumber + ") ");
	}

	//We can't just call s(null) in this method, as it will mess up the stack-trace index in s(String message) unfortunately.

	/**
	 * This will print the stack-trace of where this method was called:
	 * 
	 * Prints: [METHODNAME] [STACKTRACE_ENTRY]
	 * 
	 * Only trace-entries which has a packagename of the same class as the class which calls this method will be printed in logcat.
	 * It also prints traces from NCore.
	 */
	public static void s(){
		String tag = "null";
		String packageName = "null";
		String thisPackageName = "null";
		String method = "null";

		StackTraceElement[] ste = Thread.currentThread().getStackTrace();

		int currentIndex = -1;
		for (int i = 0; i < ste.length; i++) {
			if (ste[i].getMethodName().compareTo("s") == 0)
			{
				currentIndex = i + 1;
				break;
			}
		}

		try{
			tag = ste[currentIndex].getClassName();
			method = ste[currentIndex].getMethodName();
			String packageArray[] = tag.split("\\.");
			packageName = packageArray[0]+"."+packageArray[1];
			String thisPackageArray[] = NLog.class.getName().split("\\.");
			thisPackageName = thisPackageArray[0]+"."+thisPackageArray[1];
		}
		catch(Exception e){
			e("NLOG e",e);
		}

		Log.i(tag, "* " + method);

		for(int i = currentIndex; i < Thread.currentThread().getStackTrace().length; i++){
			if(ste[i].getClassName().startsWith(packageName) || ste[i].getClassName().startsWith(thisPackageName)){

				String fullClassName = ste[i].getClassName();
				String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
				String methodName = ste[i].getMethodName();
				String lineNumber = String.valueOf(ste[i].getLineNumber());

				Log.i(tag, method + " at " + fullClassName + "." + methodName + "(" + className + ".java:" + lineNumber + ")");
			}
		}
	}

	/**
	 * This will print the stack-trace of where this method was called.
	 * 
	 * Prints: [METHODNAME] [MESSAGE] [STACKTRACE_ENTRY]
	 * 
	 * Only trace-entries which has a packagename of the same class as the class which calls this method will be printed in logcat.
	 * It also prints traces from NCore.
	 * 
	 * @param message String (e.g. a variable) that will be appended to the log-entry
	 */
	public static void s(Object message){

		String tag = "null";
		String method = "null";
		String packageName = "null";
		String thisPackageName = "null";

		StackTraceElement[] ste = Thread.currentThread().getStackTrace();

		int currentIndex = -1;
		for (int i = 0; i < ste.length; i++) {
			if (ste[i].getMethodName().compareTo("s") == 0)
			{
				currentIndex = i + 1;
				break;
			}
		}

		try{
			tag = ste[currentIndex].getClassName();
			method = ste[currentIndex].getMethodName();
			String packageArray[] = tag.split("\\.");
			packageName = packageArray[0]+"."+packageArray[1];
			if(message != null)
				message = " "+message;
			else
				message = "";
		}
		catch(Exception e){
			e("NLOG e",e);
		}

		Log.i(tag, "* " + method + message);

		for(int i = currentIndex; i < Thread.currentThread().getStackTrace().length; i++){
			if(ste[i].getClassName().startsWith(packageName) || ste[i].getClassName().startsWith(thisPackageName)){

				String fullClassName = ste[i].getClassName();
				String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
				String methodName = ste[i].getMethodName();
				String lineNumber = String.valueOf(ste[i].getLineNumber());

				Log.i(tag, method + " at " + fullClassName + "." + methodName + "(" + className + ".java:" + lineNumber + ") ");
			}
		}
	}


	public static void t(){
		NLog.d(TEST, "test");
	}

	public static void t(String s){
		NLog.d(TEST, s);
	}

	public static void t(int i){
		NLog.d(TEST, String.valueOf(i));
	}

	public static void t(float f){
		NLog.d(TEST, String.valueOf(f));
	}

	public static void t(double d){
		NLog.d(TEST, String.valueOf(d));
	}

	public static void t(boolean b){
		NLog.d(TEST, String.valueOf(b));
	}

}
