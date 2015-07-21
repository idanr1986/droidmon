package com.cuckoodroid.droidmon.utils;

import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;

import de.robv.android.xposed.XC_MethodHook.MethodHookParam;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Set;

import javax.crypto.Cipher;
import javax.net.ssl.HttpsURLConnection;



public class ParseGenerator {
	private static Gson gson = new Gson();

	public static Object parse(Object obj) throws Exception {
		Object returnObj=null;

		if(obj instanceof byte[])
			returnObj = byteArrayParse(obj);
		else if(obj instanceof HttpRequestBase)
			returnObj = httpRequestBaseParse(obj);
		else if(obj instanceof HttpResponse)
			returnObj = httpResponseParse(obj);
		else if(obj instanceof HttpURLConnection || obj instanceof HttpsURLConnection )
			returnObj = URLConnectionParse(obj);
		else if(obj instanceof URL)
			returnObj = URLParse(obj);
		else if(obj instanceof MessageDigest)
			returnObj = messageDigestParse(obj);
		else if(obj instanceof Cipher)
			returnObj = cipherParse(obj);
		else if(obj instanceof Intent)
			returnObj = intentParse(obj);
		else
			returnObj = genericParse(obj);

		return returnObj;
	}

	private static Object URLParse(Object obj) {
		URL url = (URL) obj;
		return url.toString();
	}

	private static String byteArrayParse(Object obj) {
		if(Strings.isAsciiPrintable((byte[]) obj))
			return new String((byte[])obj);
		else
			return HexDump.dumpHexString((byte[]) obj);
	}

	private static JSONObject cipherParse(Object obj) throws JSONException {
		JSONObject json=new JSONObject();
		JSONObject cipher = new JSONObject(gson.toJson(obj));
		json.put("mode", cipher.get("mode"));
		return json;
	}

	private static Object genericParse(Object obj) {
		try {
			return new JSONObject(gson.toJson(obj));
		} catch (Exception e) {
			try {
				return new JSONArray(gson.toJson(obj));
			} catch (Exception e1) {
				return obj.toString();
			}
		}
		
		
	}

	private static String httpRequestBaseParse(Object obj) throws IOException {
		HttpRequestBase request = (HttpRequestBase)obj;
		StringBuilder sb = new StringBuilder();

		Header[] headers = request.getAllHeaders();
		sb.append(request.getRequestLine().toString()+"\n");

		for (Header header : headers) {
			sb.append(header.getName() + ": " + header.getValue()+"\n");
		}

		sb.append("\n");

		if(request instanceof HttpPost)
			sb.append( EntityUtils.toString(((HttpPost) request).getEntity()));

		return sb.toString();
	}

	private static Object httpResponseParse(Object obj) throws IOException {
		HttpResponse response = (HttpResponse)obj;
		return response.getStatusLine().toString();
	}
	
	private static Object intentParse(Object obj){
		JSONObject json=new JSONObject();
		Intent intent = (Intent) obj;

		Bundle bundle = intent.getExtras();
		String cmp;
		try {
			cmp = intent.getComponent().flattenToString();
			json.put("cmp",cmp);
		} catch (Exception e1) {
		}

		try {
			String action = intent.getAction();
			json.put("act", action);
		} catch (Exception e1) {
		}
		JSONArray extraData = new JSONArray();

		try {
			Set<String> set = bundle.keySet();
			for (String key : set) {
				JSONObject extra= new JSONObject();
				extra.put("key",key);
				extra.put("value",bundle.get(key).toString());
				extraData.put(extra);
			}
			json.put("extras", extraData);
		} catch (Exception e) {
		}


		return json;
	}

	private static JSONObject URLConnectionParse(Object obj) throws IOException, JSONException {
		HttpURLConnection con = (HttpURLConnection) obj;
		JSONObject urlConnectionObject=new JSONObject();

		urlConnectionObject.put("response_code", con.getResponseCode());
		urlConnectionObject.put("response_message", con.getResponseMessage());
		urlConnectionObject.put("request_method",con.getRequestMethod());
		urlConnectionObject.put("url",con.getURL());
		urlConnectionObject.put("version","HTTP/1.1");

		return urlConnectionObject;
	}

	private static String messageDigestParse(Object obj) {
		return "";
	}
	
	public static JSONObject generateHookDataJson(MethodHookParam param,MethodApiType type) throws JSONException
	{
		JSONObject hookData= new JSONObject();
		hookData.put("class",param.method.getDeclaringClass().getName());
		hookData.put("method", param.method.getName());
		hookData.put("timestamp", System.currentTimeMillis());
		hookData.put("type", type.toString());
		return hookData;
	}

	public static JSONArray parseArgs(MethodHookParam param,JSONObject hookJson)
	{
		JSONArray args =  new JSONArray();
		for (Object object : (Object[]) param.args) {
			try {
				if(object!=null)
					args.put(ParseGenerator.parse(object));
			} catch (Exception e) {
				Logger.logShell("args error: " + e.getMessage()+" "+hookJson.toString());
			}
		}
		return args;
	}
	
	public static Object parseResults(MethodHookParam param,JSONObject hookJson)
	{
		try {
			return ParseGenerator.parse(param.getResult());
		} catch (Exception e) {
			Logger.logShell("result error: " + e.getMessage()+" "+hookJson.toString());
			return "";
		}
	}
	
	public static Object parseThis(MethodHookParam param,JSONObject hookJson)
	{
		try {
			return ParseGenerator.parse(param.thisObject);
		} catch (Exception e) {
			Logger.logShell("thisObject error: " + e.getMessage()+" "+hookJson.toString());
			return "";
		}
	}
	
	public static String parseRefelctionMethodName(MethodHookParam param,JSONObject hookJson) {
			Method method = (Method) param.thisObject;
			if(method!=null)
				return method.getName();
			else
				return "";

		
	}
	
	public static String parseRefelctionClassName(MethodHookParam param,JSONObject hookJson) {
			if(param.args[0]!=null)
				return param.args[0].getClass().getName();
			else
				return "";
			
		}
	
	public static JSONArray parseRefelctionArgs(MethodHookParam param,JSONObject hookJson) {
		JSONArray args =  new JSONArray();
		if((Object[]) param.args[1]!=null)
		{
			for (Object object : (Object[]) param.args[1]) {
				try {
					if(object!=null)
						args.put(ParseGenerator.parse(object));
				} catch (Exception e) {
					Logger.logShell("reflection args error: " + e.getMessage()+" "+hookJson.toString());
				}
			}
		}
		return args;
		
	}
}

