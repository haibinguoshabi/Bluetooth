package com.cloud.newbluetooth;

import java.io.PrintWriter;
import java.io.StringWriter;

import android.util.Log;

public class Utils {

	public static String ExceptionToString(Exception ex) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		return sw.toString();
	}
	
	public static String bytesToHexString(byte[] src){  
	    if (src == null || src.length <= 0) {  
	        return null;  
	    }  
	    StringBuilder sb = new StringBuilder();
	    for (int i = 0; i < src.length; i++) {  
	        int v = src[i] & 0xFF;  
	        String hv = Integer.toHexString(v);
	        String tmp = hv.length() < 2  ? "0"+hv :hv; 
	        
	        //int decimal = Integer.parseInt(tmp, 16);  
	          //convert the decimal to character  
	        //sb.append((char)decimal);
	        sb.append(tmp);
	          
	    }  
	    return sb.toString();  
	}  
}
