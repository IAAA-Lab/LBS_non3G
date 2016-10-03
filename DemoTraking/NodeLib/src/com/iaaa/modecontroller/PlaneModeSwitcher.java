package com.iaaa.modecontroller;

import java.io.DataOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

/**
 * Clase encargada de encencder/apagar el modo avión del teléfono.
 * @author Ricardo Pallás
 *
 */
public class PlaneModeSwitcher{
	final static String DEBUG_TAG = "PlaneModeSwitcher";


	/**
	 * Activa el modo avión en el teléfono
	 */
	public static void setPlaneModeOn(){
		try{
			//Se obtiene una shell con permisos root
			Process su = Runtime.getRuntime().exec("su");
			DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());
			//se sobrescriben las configuraciones globales del dispositivo
			outputStream.writeBytes("settings put global airplane_mode_on 1\n");
			outputStream.flush();
			//Se manda un broadcast en el dispositivo para que se enteren de las nuevas configuraciones
			outputStream.writeBytes("am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true\n");
			outputStream.flush();
			//Se sale
			outputStream.writeBytes("exit\n");
			outputStream.flush();
			su.waitFor();
		}catch(IOException e){
			e.printStackTrace();
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		//Log.d(DEBUG_TAG, "Plane mode is now on");
	}
	/**
	 * Desactiva el modo avión en el teléfono
	 */
	public static void setPlaneModeOff(){
		try{
			//Se obtiene una shell con permisos root
			Process su = Runtime.getRuntime().exec("su");
			DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());
			//se sobrescriben las configuraciones globales del dispositivo
			outputStream.writeBytes("settings put global airplane_mode_on 0\n");
			outputStream.flush();
			//Se manda un broadcast en el dispositivo para que se enteren de las nuevas configuraciones
			outputStream.writeBytes("am broadcast -a android.intent.action.AIRPLANE_MODE --ez state false\n");
			outputStream.flush();
			//Se sale
			outputStream.writeBytes("exit\n");
			outputStream.flush();
			su.waitFor();
		}catch(IOException e){
			e.printStackTrace();
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		//Log.d(DEBUG_TAG, "Plane mode is now off");
	}

	/**
	 * Devuelve verdadero si el modo avión está encendido y falso en el caso contrario
	 * @return verdadero si el modo avión está encendido y falso en el caso contrario
	 */
	@SuppressLint("NewApi")
	public static boolean isAirplaneModeOn(Context context) {
		boolean isAirplaneOn;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
			isAirplaneOn =  Settings.System.getInt(context.getContentResolver(), 
					Settings.System.AIRPLANE_MODE_ON, 0) != 0;
			Log.d(DEBUG_TAG,"Modo avión activado?: " + isAirplaneOn);
			return isAirplaneOn;
		} else {
			isAirplaneOn = Settings.Global.getInt(context.getContentResolver(), 
					Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
			Log.d(DEBUG_TAG,"Modo avión activado?: " + isAirplaneOn);
			return isAirplaneOn;
		}       
	}


}
