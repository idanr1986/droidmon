package com.cuckoodroid.droidmon;

import com.cuckoodroid.droidmon.utils.Logger;
import com.cuckoodroid.droidmon.utils.MethodApiType;
import android.os.Process;
import de.robv.android.xposed.XC_MethodHook;


public class MethodHookImpl extends XC_MethodHook{
	
	private String mClassName;
	private String mMethodName;
	private MethodApiType mType;
	private boolean mThisObject=false;

	public MethodHookImpl(String className, String methodName,boolean thisObject,MethodApiType type){
		mClassName = className;
		mMethodName = methodName;
		mThisObject=thisObject;
		mType=type;
	}

	public String getClassName(){
		return mClassName;
	}

	public String getMethodName(){
		return mMethodName;
	}

	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		
	}

	@Override
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {
		if (!param.hasThrowable())
			try {
				if (Process.myUid() <= 0)
					return;
				
				if (InstrumentationManager.TRACE) 
					traceMethod(param);
				else
					monitorMethod(param);
				
			} catch (Throwable ex) {
				throw ex;
			}
	}
	
	public void monitorMethod(MethodHookParam param)
	{
		try {
			if(param.method.getName().contains("invoke"))
				Logger.logReflectionMethod(param,mThisObject,mType);
			else if(param.method.getName().contains("write"))
				Logger.logProcessWriteMethod(param,mThisObject,mType);
			else if(param.method.getName().contains("read"))
				Logger.logProcessReadMethod(param,mThisObject,mType);
			else
				Logger.logGenericMethod(param,mThisObject,mType);
		} catch (Exception e) {
			Logger.logError(param.method.getDeclaringClass().getName()+"->"+param.method.getName());
		}
	}
	
	public void traceMethod(MethodHookParam param)
	{
		try {
			if(param.method.getName().contains("invoke"))
				Logger.logTraceReflectionMethod(param,mType);
			else
				Logger.logTraceMethod(param,mType);
		} catch (Exception e) {
			Logger.logError( param.method.getDeclaringClass().getName()+"->"+param.method.getName());
		}
	}
}
