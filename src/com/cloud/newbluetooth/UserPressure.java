package com.cloud.newbluetooth;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UserPressure {

	private String userName;
	private int highPressure;
	private int lowPressure;
	private int maibo;
	private Date dateTime;
	
	public String getUserName() {
		return this.userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public int getHighPressure() {
		return this.highPressure;
	}
	public void setHighPressure(int highPressure) {
		this.highPressure = highPressure;
	}
	
	public int getLowPressure() {
		return this.lowPressure;
	}
	public void setLowPressure(int lowPressure) {
		this.lowPressure = lowPressure;
	}
	
	public int getMaibo() {
		return this.maibo;
	}
	public void setMaibo(int maibo) {
		this.maibo = maibo;
	}
	
	public Date getDateTime() {
		return this.dateTime;
	}
	
	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}
	
	public String toString() {
		SimpleDateFormat dateToStr = new SimpleDateFormat ("yyyy-MM-dd HH:mm");
		
		String s = "用户名:"+ userName +", 高压:" + highPressure + ",  低压:" + lowPressure
		+", 脉搏:"+maibo + ",时间:"+ dateToStr.format(dateTime);
		System.out.println(s);
		return s;
	}
}
