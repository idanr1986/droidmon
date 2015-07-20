package com.cuckoodroid.droidmon.utils;

import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.Gson;
import de.robv.android.xposed.XC_MethodHook.MethodHookParam;
import de.robv.android.xposed.XposedBridge;

public class Logger {
	public static final Gson gson = new Gson();
	//private static PrintWriter logWriter = null;
	
	//private static final int MAX_LOGFILE_SIZE = 40*1024; 
	public static String LOGTAG_SHELL= "Droidmon-shell-";
	public static String LOGTAG_WORKFLOW = "Droidmon-apimonitor-";
	public static String LOGTAG_ERROR = "Droidmon-error";
	public static String LOG_FILE ="/droidmon.log";
	public static String LOG_FILE_OLD ="/droidmon_old.log";
	public static String PACKAGENAME;

	/*public static PrintWriter getLogWriter()
	{
		if(logWriter == null)
		{
			try {
				File logFile = new File(Environment.getExternalStorageDirectory().getPath()+LOG_FILE);
				if (logFile.length() > MAX_LOGFILE_SIZE)
					logFile.renameTo(new File(Environment.getExternalStorageDirectory().getPath()+LOG_FILE_OLD));
				logWriter = new PrintWriter(new FileWriter(logFile, true));
				logFile.setReadable(true, false);
				logFile.setWritable(true, false);
			} catch (IOException e) {
				log(e.getMessage());
			}
		}
		return logWriter;
	}*/
	
	public static void logHook(JSONObject hookData){
		XposedBridge.log(LOGTAG_WORKFLOW+PACKAGENAME+":"+hookData.toString());
		//log(LOGTAG_WORKFLOW+PACKAGENAME+":"+hookData.toString());
	}

	public static void logShell(String message){
		XposedBridge.log(LOGTAG_SHELL+PACKAGENAME+":"+message);
		//log(LOGTAG_SHELL+PACKAGENAME+":"+message);
	}

	public static void logError(String message){
		XposedBridge.log(LOGTAG_ERROR+":"+message);
		//log(LOGTAG_ERROR+":"+message);
	}
	
	/*public synchronized static void log(String text) {
		Log.d("Droidmon", text);
		PrintWriter logger = getLogWriter();
		if (logger != null) {
			logger.println(text);
			logger.flush();
		}
	}*/
	
	public static void logProcessWriteMethod(MethodHookParam param, boolean mThisObject, MethodApiType mType) throws JSONException {
		if(param.thisObject == null)
			return;
		if(param.thisObject.getClass().toString().contains("ProcessOutputStream"))
		{
			JSONObject hookData=ParseGenerator.generateHookDataJson(param,mType);
			hookData.put("buffer", new String((byte[])param.args[0],(Integer)param.args[1],(Integer)param.args[2]).trim());
			Logger.logHook(hookData);
		}
		
	}
	
	public static void logProcessReadMethod(MethodHookParam param, boolean mThisObject, MethodApiType mType) throws JSONException {
		if(param.thisObject == null)
			return;
		if(param.thisObject.getClass().toString().contains("ProcessInputStream"))
		{
			JSONObject hookData=ParseGenerator.generateHookDataJson(param,mType);
			hookData.put("buffer", new String((byte[])param.args[0],(Integer)param.args[1],(Integer)param.args[2]).trim());
			Logger.logHook(hookData);
		}
		
	}
	
	public static void logGenericMethod(MethodHookParam param, boolean mThisObject, MethodApiType mType) throws JSONException {
		JSONObject hookJson = ParseGenerator.generateHookDataJson(param,mType);
		
		if(param.args!=null)
			hookJson.put("args", ParseGenerator.parseArgs(param,hookJson));
		if(param.getResult()!=null)
			hookJson.put("result", ParseGenerator.parseResults(param,hookJson));
		if(param.thisObject!=null && mThisObject)
			hookJson.put("this",ParseGenerator.parseThis(param,hookJson));
		
		Logger.logHook(hookJson);
		
	}
	
	public static void logReflectionMethod(MethodHookParam param, boolean mThisObject, MethodApiType mType) throws JSONException {
		JSONObject hookJson = ParseGenerator.generateHookDataJson(param,mType);
		
		hookJson.put("hooked_class", ParseGenerator.parseRefelctionClassName(param, hookJson));
		hookJson.put("hooked_method", ParseGenerator.parseRefelctionMethodName(param, hookJson));
		if(param.args!=null)
			hookJson.put("args", ParseGenerator.parseRefelctionArgs(param,hookJson));
		if(param.getResult()!=null)
			hookJson.put("result", ParseGenerator.parseResults(param,hookJson));
		
		Logger.logHook(hookJson);
	}
	
	public static void logTraceReflectionMethod(MethodHookParam param, MethodApiType mType) throws JSONException {
		JSONObject hookJson = ParseGenerator.generateHookDataJson(param,mType);
		
		hookJson.put("hooked_method", ParseGenerator.parseRefelctionMethodName(param, hookJson));
		hookJson.put("hooked_class", ParseGenerator.parseRefelctionClassName(param, hookJson));
		
		Logger.logHook(hookJson);
		
	}
	
	public static void logTraceMethod(MethodHookParam param, MethodApiType mType) throws JSONException {
		Logger.logHook(ParseGenerator.generateHookDataJson(param,mType));
		
	}
	
	
	


}
