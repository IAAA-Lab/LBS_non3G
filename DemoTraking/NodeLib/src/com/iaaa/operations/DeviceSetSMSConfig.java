package com.iaaa.operations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.iaaa.appnode.HashCalculator;
import com.iaaa.json.JSONHelper;

/**
 * Clase que representa la operación de DeviceSetSMSConfig
 * @author Ricardo Pallás
 */
public class DeviceSetSMSConfig extends OperationBase {

	/**
	 * Constructor del objeto operación DeviceSetSMSConfig
	 */
	public DeviceSetSMSConfig(){
		super();
		super.CMD = "DeviceACKRes";

	}

	/**
	 * Ejecuta la operación que fija el modo de operación SMS de un nodo.
	 */
	@Override
	public String execute(JSONObject jsonObject, Context context) {

		//Extraer parametros
		String setPhoneNumber = "";
		String setSmsPollTime = "";
		String setSmsTransmitPeriod = "";
		String setSmsHashKey = "";

		try {
			JSONArray parametros = jsonObject.getJSONArray("p");
			setPhoneNumber = parametros.getString(0);
			setSmsPollTime = parametros.getString(1); 
			setSmsTransmitPeriod = parametros.getString(2);
			setSmsHashKey = parametros.getString(3);

		} catch (JSONException e) {
			e.printStackTrace();
			//Devuelve un error
			return new DeviceErrorRes("DeviceSetSMSConfig", "DEV_ERROR_FRAME", "Valores del parámetro p mal formados").execute(null,null);
		}


		//Obtener preferencias donde se almacena la configuracion SMS
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		//Se instancia un editor para editar las preferencias
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString("pref_smsPollTimeType", setSmsPollTime);
		editor.putString("pref_smsTransmitPeriodType", setSmsTransmitPeriod);
		editor.putString("pref_phoneNumberType", setPhoneNumber);
		editor.commit();


		//Generar respuesta

		String commandSource = "DeviceSetSMSConfig";
		super.p = new String[1];
		super.p[0] = commandSource;


		//Convertir a JSON String
		JSONHelper jsonHelper = new JSONHelper();
		String result = jsonHelper.operationToJSONString(this);

		//calcular HASH
		HashCalculator hashCalculator = new HashCalculator();
		String hash = hashCalculator.calculateHash(result);

		//actualizar JSON String con el hash
		super.h = hash;
		result = jsonHelper.operationToJSONString(this);
		
		//Devolver respuesta
		return result;
	}


}
