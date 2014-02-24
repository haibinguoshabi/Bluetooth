package com.cloud.newbluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class BluetoothData extends Activity{

	private ListView listView;
	private ArrayAdapter<String> adtDevices;
	private List<String> lstDatas = new ArrayList<String>();
	private DataHandler dataHandler;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bluetooth_text);
		
		
		listView = (ListView)this.findViewById(R.id.lvDatas);
		adtDevices = new ArrayAdapter<String>(BluetoothData.this,
				android.R.layout.simple_list_item_1, lstDatas);
		listView.setAdapter(adtDevices);
		
		
		try {
			InputStream stream = NewBlueTooth.btSocket.getInputStream();
			OutputStream outputStream = NewBlueTooth.btSocket.getOutputStream();
			
			if (stream != null ) {
				dataHandler = new DataHandler(stream,outputStream);
				dataHandler.Start(mHandler,1000);
			}
		}catch(Exception e) {
			String stackTrace = Utils.ExceptionToString(e);
			Log.e("thread start error",e.getMessage() +"\n" + stackTrace);
		}
	}
	
	private Handler mHandler = new Handler(){
	    public void handleMessage (Message msg) {//此方法在ui线程运行  
            switch(msg.what) {  
            case 0:
            	String s = (String)msg.obj;
            	lstDatas.add(s);
            	adtDevices.notifyDataSetChanged();
                break;  
  
            }  
        }  
	};
	
	@Override
	public void onDestroy() {
		dataHandler.Stop();
		try {
			Log.e("socket","close");
			NewBlueTooth.btSocket.close();
		} catch (IOException e) {
			//e.printStackTrace();
			String stackTrace = Utils.ExceptionToString(e);
			Log.e("socket close error",e.getMessage() +"\n" + stackTrace);
		}
        super.onDestroy();  
	}
}
