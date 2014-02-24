package com.cloud.newbluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class DataHandler {
	private boolean isRecording = false;
	private InputStream input= null;
	private OutputStream output = null;
	
	public DataHandler(InputStream input,OutputStream output) {
		this.input = input;
		this.output = output;
	}
	
	public void Start(Handler mHandler,int wait) {
		this.isRecording = true;
		new DrawThread(mHandler,wait).start();
	}
	
	public void Stop() {
		this.isRecording = false;
	}
	
	
	class DrawThread extends Thread {
		private int wait;
		private Handler mHandler;
		
		private  String hexString="0123456789ABCDEF";
		
		public DrawThread(Handler mHandler,int wait) {
			this.mHandler = mHandler;
			this.wait = wait;
		}
		
		private  int encode(String s) {
			int ret = -1;
			for(int i=0;i<s.length();i+=2) {
				ret =hexString.indexOf(s.charAt(i))<< 4 |hexString.indexOf(s.charAt(i+1));
			}
			return ret;
		}
		
		@SuppressWarnings("deprecation")
		private UserPressure parseData(String s) {
			int startIndex = 9 *2;
			String userName = "";
			UserPressure up = new UserPressure();
			//解析用户
			int val = encode(String.valueOf(new char[]{s.charAt(startIndex) ,s.charAt(startIndex+1)}));
			if (val == 0x81) {
				up.setUserName("用户2");
			} else if(val == 0x80) {
				up.setUserName("用户1");
			} else {
				up.setUserName("未知用户");
			}
			
			//高压
			startIndex += 2;
			val = encode(String.valueOf(new char[]{s.charAt(startIndex) ,s.charAt(startIndex+1)}));
			up.setHighPressure(val);
			
			//低压
			startIndex += 2;
			val = encode(String.valueOf(new char[]{s.charAt(startIndex) ,s.charAt(startIndex+1)}));
			up.setLowPressure(val);
			
			//脉搏
			startIndex += 2;
			val = encode(String.valueOf(new char[]{s.charAt(startIndex) ,s.charAt(startIndex+1)}));
			up.setMaibo(val);
			
			//年
			startIndex += 2;
			int year = 2000+encode(String.valueOf(new char[]{s.charAt(startIndex) ,s.charAt(startIndex+1)}));
			
			
			startIndex += 2;
			int rdA = encode(String.valueOf(new char[]{s.charAt(startIndex) ,s.charAt(startIndex+1)}));
			
			startIndex += 2;
			int rdO =encode(String.valueOf(new char[]{s.charAt(startIndex) ,s.charAt(startIndex+1)}));
			
			startIndex += 2;
			int min =encode(String.valueOf(new char[]{s.charAt(startIndex) ,s.charAt(startIndex+1)}));
			
			int month = (rdA & 0xc0)/64 + (rdO & 0xc0)/16;
			int day = rdA & 0x3f;
			int hour = rdO & 0x3f;
			System.out.println("year:"+year+"month:"+month+",day:"+day+",hour:"+hour+",minute:"+min);
			
			Calendar calendar = Calendar.getInstance();
			calendar.set(year, month-1, day,hour,0);
			Date date = calendar.getTime();
			
			up.setDateTime(date);
			
			return up;
		}
		
		public void run() {
			StringBuilder sb= new StringBuilder();
			
			StringBuilder rsBuilder = new StringBuilder();
			int[] rs=new int[]{0xfe,0x81,0x00,0x00,0x00,0x01};
			for(int i=0;i<rs.length;i++) {
				rsBuilder.append(Integer.valueOf(String.valueOf(rs[i]),16));
			}
			String t3="0xfe,0x81,0x00,0x00,0x00,0x01";
			try {
				output.write(t3.toString().getBytes());
			}catch(Exception ex) {
				Log.e("output", ex.getMessage());
				ex.printStackTrace();
			}
			
			
			while (isRecording) {
				try {
					byte[] tmp = new byte[1024];
					
					Log.e("before read","test");
					int len = input.read(tmp);
					Log.e("available", String.valueOf(len));
					if (len >0 ) {
						byte[] btBuf = new byte[len];
						System.arraycopy(tmp, 0, btBuf, 0, btBuf.length);
						String s = Utils.bytesToHexString(btBuf).toUpperCase();
						System.out.println(s);
						
						while (s.length() > 0) {
							int startIndex = s.indexOf("FF");
							int endIndex = s.indexOf("FE");
							if (sb.length() > 0) {
								//find FE string
								sb.append(s.subSequence(0, endIndex+2));
								s = s.substring(endIndex+2);
								System.out.println("part parse success:"+sb.toString());
								System.out.println("part String:"+s);
								//resultStr.add(sb.toString());
							
								
								 mHandler.obtainMessage(0,parseData(sb.toString().toUpperCase()).toString()).sendToTarget();
								 
								sb = new StringBuilder();
								continue;
								//redo
							}
							if (startIndex >=0) {
								if (endIndex >=0 && startIndex < endIndex) {
									//if exist full data in this string
									String tStr = s.substring(startIndex, endIndex+2);
								
									 mHandler.obtainMessage(0,parseData(tStr.toUpperCase()).toString()).sendToTarget();
									
									System.out.println("parse success:"+s.substring(startIndex, endIndex+2));
									System.out.println("endIndex:"+endIndex);
									if (endIndex+2 < s.length()) {
										s = s.substring(endIndex+2);
									} else {
										s = "";
									}
								} else {
									//else keep part of string
									sb.append(s.substring(startIndex));
									//exit
									break;
								}
							} else{
								break;
							}
						}
						//lstData.add("中文"+(new String(btBuf,"GBK")));
						//lstData.add(s);
						
						
					}
					Thread.sleep(wait);
				}catch(IOException e) {
					String stackTrace = Utils.ExceptionToString(e);
					Log.e("read io error",e.getMessage() +"\n" + stackTrace);
					break;
				} catch(Exception ex) {
					String stackTrace = Utils.ExceptionToString(ex);
					Log.e("error",ex.getMessage() +"\n" + stackTrace);
				}
			}
		}
	}
	

}
