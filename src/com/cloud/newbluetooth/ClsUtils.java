package com.cloud.newbluetooth;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

public class ClsUtils {

	public static boolean createBond(Class btClass,BluetoothDevice btDevice) throws Exception {
		Method createBondMethod = btClass.getMethod("createBond");
		Boolean returnValue = (Boolean)createBondMethod.invoke(btDevice);
		return returnValue.booleanValue();
	}
	
	public static boolean removeBond(Class btClass,BluetoothDevice btDevice) throws Exception {
		Method removeBondMethod = btClass.getMethod("removeBond");
		Boolean returnValue = (Boolean)removeBondMethod.invoke(btDevice);
		return returnValue.booleanValue();
	}
	
	public static boolean setPin(Class btClass,BluetoothDevice btDevice,String str) throws Exception {
		Method setPinMethod = btClass.getMethod("setPin",new Class[] {byte[].class});
		Boolean returnValue = (Boolean)setPinMethod.invoke(btDevice,new Object[]{str.getBytes()});
		Log.e("returnValue", ""+returnValue);
		return returnValue;
	}
	
	public static boolean cancelPairingUserInput(Class btClass,BluetoothDevice btDevice) throws Exception{
		Method cancelPairingUserInputMethod = btClass.getMethod("cancelPairingUserInput");
		Boolean returnValue = (Boolean)cancelPairingUserInputMethod.invoke(btDevice);
		Log.e("returnValue", ""+returnValue);
		return returnValue;
	}
	
	 // cancel pair
    public static boolean cancelBondProcess(Class btClass,BluetoothDevice device)
    throws Exception
    {
        Method createBondMethod = btClass.getMethod("cancelBondProcess");
        Boolean returnValue = (Boolean) createBondMethod.invoke(device);
        return returnValue.booleanValue();
    }

	

    public static void printAllInform(Class clsShow) {  
        try {  
            // get all method  
            Method[] hideMethod = clsShow.getMethods();  
            int i = 0;  
            for (; i < hideMethod.length; i++) {  
                Log.e("method name", hideMethod[i].getName());  
            }  
            // get all constants  
            Field[] allFields = clsShow.getFields();  
            for (i = 0; i < allFields.length; i++) {  
                Log.e("Field name", allFields[i].getName());  
            }  
        } catch (SecurityException e) {  
            // throw new RuntimeException(e.getMessage());  
            e.printStackTrace();  
        } catch (IllegalArgumentException e) {  
            // throw new RuntimeException(e.getMessage());  
            e.printStackTrace();  
        } catch (Exception e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
    }
}
