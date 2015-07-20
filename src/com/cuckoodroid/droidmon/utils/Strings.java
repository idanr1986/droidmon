package com.cuckoodroid.droidmon.utils;

public class Strings {
	 public static boolean isAsciiPrintable(byte[] byteArray) {
	      if (byteArray == null) {
	          return false;
	      }
	      int sz = byteArray.length;
	      for (int i = 0; i < sz; i++) {
	          if (isAsciiPrintable(byteArray[i]) == false) {
	        	  return false;
	          }
	      }
	      return true;
	  }
	  	
  public static boolean isAsciiPrintable(byte ch) {
      return ch >= 32 && ch < 127;
  }
}