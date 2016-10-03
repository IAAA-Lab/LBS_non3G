package com.iaaa.appnode;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Clase que ofrece una interfaz de métodos de lectura/escritura de las preferencias de Android
 * @author Ricardo Pallás
 *
 */
public class Config {



	/**
	 * Devuelve el modo de ejecución almacenado en las preferencias de la aplicación
	 * @param context Contexto de la aplicación
	 * @return El id del modo
	 * ID's del modo:
	 * 	0 - The device is waiting to be configured
	 * 	1 - Extreme Loew Power Mode
	 * 	2 - Very Low Power Mode
	 * 	3 - Low Power Mode
	 * 	4 - Always Locate mode
	 * 	5 - Custom
	 * 
	 */
	public static int getPowerMode(Context context) {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(context.getApplicationContext());
		String locationRefreshRate = sharedPref.getString(
				"pref_powerModeType", "");

		//Si las preferencias no estan cargadas (primera vez que se ejecuta) obtener el por defecto
		int modeId = Integer.parseInt(context.getResources().getString(R.string.pref_powerModeTypes_default));
		//Si ya están cargadas obtenerlo de la configuracion
		if(!locationRefreshRate.equals("")){
			modeId = Integer.parseInt(locationRefreshRate);
		}
		return modeId;
	}

	/**
	 * Fija el modo actual de ejecución a [poderMode], guardándolo en 
	 * las preferencias. Si el valor no está entre 0 y 5 (incluídos) no 
	 * se hace nada.
	 * @param powerMode Valor del 0 al 5 (incluídos) que indican el modo a fijar.
	 */
	public static void setPowerMode(Context context, int powerMode){
		if(powerMode>=0 && powerMode <= 5){
			//Obtener preferencias donde se almacena el mode
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
			//Se instancia un editor para editar las preferencias
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString("pref_powerModeType", powerMode+"");
			editor.commit();
		}
	}

	/**
	 * Devuelve, en segundos, el periodo de transmisión de SMS
	 * Sólo utilizar en power mode 5 (custom)
	 * @param context Contexto de la aplicación
	 * @return Periodo de transmisión de SMS en segundos
	 */
	public static int getSmsTransmitPeriod(Context context){
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(context.getApplicationContext());
		String smsTransmitPeriodType = sharedPref.getString(
				"pref_smsTransmitPeriodType", "");

		//Si las preferencias no están cargadas se asigna un valor por defecto
		int transmitPeriodTime = 900;
		//Si ya están cargadas obtenerlo de la configuracion
		if(!smsTransmitPeriodType.equals("")){
			transmitPeriodTime = Integer.parseInt(smsTransmitPeriodType);
		}
		return transmitPeriodTime;
	}
	/**
	 * Devuelve, en segundos, el periodo de escucha de SMS
	 * Sólo utilizar en power mode 5 (custom)
	 * @param context Contexto de la aplicación
	 * @return Periodo de escucha de SMS, en segundos
	 */
	public static int getSmsPollTime(Context context){
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(context);
		String pollTimeType = sharedPref.getString(
				"pref_smsPollTimeType", "");

		//Si las preferencias no están cargadas se asigna un valor por defecto
		int pollTime = 900;
		//Si ya están cargadas obtenerlo de la configuracion
		if(!pollTimeType.equals("")){
			pollTime = Integer.parseInt(pollTimeType);
		}
		return pollTime;
	}

	/**
	 * Fija en las preferencias, el valor de pref_smsPollTimeType a [value]
	 * @param value
	 * @param context
	 */
	public static void setSmsPollTime(int value, Context context){
		//Obtener preferencias donde se almacena la configuracion SMS
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		//Se instancia un editor para editar las preferencias
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString("pref_smsPollTimeType", value+"");
		editor.commit();
	}

	/**
	 * Devuelve la tasa de refresco de la localización (LocationRefreshRate), dependiendo
	 * del modo de energía. En caso de que, el modo de energía sea Custom Mode (código 5),
	 * se busca la tasa introducida por el usuario.
	 * @param context Contexto de la aplicación
	 * @return LocationRefreshRate en segundos.
	 */
	public static int getLocationRefreshRate(Context context){
		int powerMode = getPowerMode(context);
		int refreshRate = 0;
		switch (powerMode) {
		case 0:
			//GSM always on
			//Nada
			break;
		case 1:
			//Extreme power low mode
			refreshRate = 86400;
			break;
		case 2:
			//Very low power mode
			refreshRate = 900;
			break;
		case 3:
			//Low power mode
			refreshRate = 900;
			break;
		case 4:
			//Always locate mode
			refreshRate = 60;
			break;
		case 5:
			//Custom mode
			refreshRate = getCustomRefreshRate(context);
			break;
		default:
			break;
		}
		return refreshRate;
	}

	/**
	 * Devuelve el locationRefreshRate fijado por el usuario en las configuraciones.
	 * Sólo debe ser usado si el modo de energía actual es el 5 (Custom Mode)
	 * @param context Contexto de la aplicación
	 * @return locationRefreshRate en segundos
	 */
	public static int getCustomRefreshRate(Context context){
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(context);
		String refreshRateType = sharedPref.getString(
				"pref_refreshRateType", "");

		//Si las preferencias no están cargadas se asigna un valor por defecto
		int refreshRate = 900;
		//Si ya están cargadas obtenerlo de la configuracion
		if(!refreshRateType.equals("")){
			refreshRate = Integer.parseInt(refreshRateType);
		}
		return refreshRate;
	}



	/**
	 * Devuelve el intervalo de lanzamiento del evento location
	 * @return Intervalo en segundos de refresco.
	 */
	public static int getLocationEventRefreshRate(Context context) {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(context);
		String locationRefreshRate = sharedPref.getString(
				"pref_refreshRateType", "");
		//Si las preferencias no estan cargadas (primera vez que se ejecuta) obtener el por defecto
		int refreshRate = Integer.parseInt(context.getResources().getString(R.string.pref_refreshRateType_default));
		//Si ya están cargadas obtenerlo de la configuracion
		if(!locationRefreshRate.equals("")){
			refreshRate = Integer.parseInt(locationRefreshRate);
		}
		return refreshRate;
	}

	/**
	 * Devuelve la tasa de refresco de obtención de valores de los sensores.
	 * @param context Contexto de la aplicación
	 * @return Tasa de refresco de obtención de valores de los sensores.
	 */
	public static int getSensorRefreshRate(Context context){
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(context);
		String sensorRefreshRate = sharedPref.getString(
				"pref_sensorRefreshRateType", "");
		//Si las preferencias no estan cargadas (primera vez que se ejecuta) obtener el por defecto
		int refreshRate = Integer.parseInt(context.getResources().getString(R.string.pref_sensorRefreshRateType_default));
		//Si ya están cargadas obtenerlo de la configuracion
		if(!sensorRefreshRate.equals("")){
			refreshRate = Integer.parseInt(sensorRefreshRate);
		}
		return refreshRate;
	}
	
	/**
	 * Devuelve un String que contiene el número de teléfono del servidor.
	 * @param context Contexto de la aplicación
	 * @return Número de teléfono del servidor.
	 */
	public static String getServerPhoneNumber(Context context) {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(context.getApplicationContext());
		String phoneNumber = sharedPref.getString(
				"pref_serverPhoneNumberType", "628481666");
		
		return phoneNumber;
	}

}
