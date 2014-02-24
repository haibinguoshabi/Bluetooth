package com.cloud.newbluetooth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

public class NewBlueTooth extends Activity {

	private static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
	//private static final String HW_PREFIX = "HHW-UART";
	private static final String HW_PREFIX = "";
	private static final String PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST";
	private Button btnSearch; 
	private BluetoothAdapter btnAdapt;
	private Switch tbtnSwitch;
	
	private List<String> lstDevices = new ArrayList<String>();
	private ListView lvBTDevices;
	private ArrayAdapter<String> adtDevices;
	
	
	public static BluetoothSocket btSocket;
	
	BluetoothAdapter mBluetoothAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_blue_tooth);
		

		btnSearch = (Button)this.findViewById(R.id.btnSearch);
		tbtnSwitch = (Switch)this.findViewById(R.id.tbtnSwitch);
		btnSearch.setOnClickListener(new ClickEvent());
		
		lvBTDevices = (ListView) this.findViewById(R.id.lvDevices);
		adtDevices = new ArrayAdapter<String>(NewBlueTooth.this,
				android.R.layout.simple_list_item_1, lstDevices);
		lvBTDevices.setAdapter(adtDevices);
		lvBTDevices.setOnItemClickListener(new ItemClickEvent());
		
		mBluetoothAdapter =  BluetoothAdapter.getDefaultAdapter(); 
		if (mBluetoothAdapter == null) { 
	        Toast.makeText(this, "本机没有找到蓝牙硬件或驱动！", Toast.LENGTH_SHORT).show(); 
	        finish(); 
	    } 
		
		tbtnSwitch.setChecked(mBluetoothAdapter.isEnabled());
		
		tbtnSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {              
            @Override  
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            	  
                 if(isChecked)  {
                     Toast.makeText(getApplicationContext(), "Bluetooth Enable", Toast.LENGTH_SHORT).show();
                       
                     if (!mBluetoothAdapter.isEnabled()) { 
                    	 mBluetoothAdapter.enable();
                     } 
                 }
                 else   {
                     Toast.makeText(getApplicationContext(), "Bluetooth Disable", Toast.LENGTH_SHORT).show();
                     if (mBluetoothAdapter.isEnabled()) {
                    	 mBluetoothAdapter.disable();
                     }
                 }
             }  
		});  
		
		
		
		btnAdapt= BluetoothAdapter.getDefaultAdapter();
		
		IntentFilter intent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		intent.addAction(PAIRING_REQUEST);
		intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		Intent in = registerReceiver(searchDevices, intent);
	}
	
	
	private final BroadcastReceiver searchDevices = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
				btnSearch.setText("Searching Bluetooth Device...");
				btnSearch.setEnabled(false);
				lstDevices.clear();
				adtDevices.notifyDataSetChanged();
				return;
			}
			if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				Toast.makeText(NewBlueTooth.this, "Search Bluetooth Device Finished!", 5000).show();
				btnSearch.setText("Search Bluetooth Device");
				btnSearch.setEnabled(true);
				//btnSearch.setActivated(true);
				return;
			}
			Bundle b = intent.getExtras();
			Object[] lstName = b.keySet().toArray();
			Log.e("receiver","receiver new bluetooth device");

			for (int i = 0; i < lstName.length; i++) {
				String keyName = lstName[i].toString();
				Log.e(keyName, String.valueOf(b.get(keyName)));
			}
			if (action.equals("android.bluetooth.device.action.PAIRING_REQUEST")) {
				Log.d("pairing","pairing_request");
				/*Pairing setPin and cancel*/
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				
				try {
                   // ClsUtils.setPin(device.getClass(), device, "1234");
					ClsUtils.createBond(device.getClass(), device);
					ClsUtils.cancelPairingUserInput(device.getClass(), device);
				} catch(Exception e) {
					String stackTrace = Utils.ExceptionToString(e);
					Log.e("pairing error",e.getMessage() +"\n" + stackTrace);
				}
			}
			
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				if (lstDevices.size() > 0) {
					return;
				}
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				String deviceName = device.getName();
				if (deviceName.startsWith(HW_PREFIX)) {
					if (device.getBondState() == BluetoothDevice.BOND_NONE) {
						try {
							//auto match
						  //ClsUtils.setPin(device.getClass(), device, "1234");
						  ClsUtils.createBond(device.getClass(), device);
						}catch(Exception e) {
							String stackTrace = Utils.ExceptionToString(e);
							Log.e("action found error",e.getMessage() +"\n" + stackTrace);
						}
					}
					String isBond = device.getBondState() == BluetoothDevice.BOND_NONE ? "unpair" :"paired";
					String str= isBond + "|" +device.getName() + "|" + device.getAddress();
					if (lstDevices.indexOf(str) == -1)// 
						lstDevices.add(str); // 
					adtDevices.notifyDataSetChanged();
					
					return;
				}	
			}
			
			if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
					String str=  "unpair|" +device.getName() + "|" + device.getAddress();
					String newStr = "paired|" +device.getName() + "|" + device.getAddress();
					int index = lstDevices.indexOf(str) ;
					if (index != -1) {
						lstDevices.set(index, newStr);
						adtDevices.notifyDataSetChanged();
						
						Log.e("auto match","success");
						Toast.makeText(NewBlueTooth.this, "Auto Match "+device.getName() + " Success", 5000).show();
					}
				}
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_blue_tooth, menu);
		return true;
	}
	
	@Override
	public void onDestroy() {
		Log.e("destroy","destroy");
		unregisterReceiver(searchDevices);
		super.onDestroy();
	}
	
	

	class ItemClickEvent implements AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if(btnAdapt.isDiscovering()) {
				btnAdapt.cancelDiscovery();  
			}
            String str = lstDevices.get(arg2);  
            String[] values = str.split("\\|");  
            String address=values[2];  
  
            BluetoothDevice btDevice = btnAdapt.getRemoteDevice(address);  
            try { 
                if(values[0].equals("paired"))  
                {  
                	UUID uuid = UUID.fromString(SPP_UUID);
                	btSocket = btDevice.createRfcommSocketToServiceRecord(uuid);
                	btSocket.connect();
                	
                	//new Intent
                	Intent intent = new Intent();
                	intent.setClass(NewBlueTooth.this, BluetoothData.class);
                	startActivity(intent);
                }  
            } catch (Exception e) {  
                // TODO Auto-generated catch block  
            	String stackTrace = Utils.ExceptionToString(e);
				Log.e("socket connect error",e.getMessage() +"\n" + stackTrace);
            }  
			
		
		}
		
	}
	
	class ClickEvent implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (v == btnSearch) {
				Log.e("click","" + btnAdapt.getState());
				if (btnAdapt.getState() == BluetoothAdapter.STATE_OFF) {// 
					Toast.makeText(NewBlueTooth.this, "please enable bluetooth", 1000).show();
					return;
				}
				setTitle("bluetooth device:" + btnAdapt.getAddress());
				//lstDevices.clear();
				btnAdapt.startDiscovery();
			}
		}
	}

}
