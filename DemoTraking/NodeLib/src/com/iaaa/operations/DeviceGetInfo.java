package com.iaaa.operations;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.iaaa.appnode.HashCalculator;
import com.iaaa.json.JSONHelper;

/**
 * Clase que representa la operación de DeviceGetInfo
 * @author Ricardo Pallas
 */
public class DeviceGetInfo extends OperationBase {

	/**
	 * Constructor del objeto operacion DeviceGetInfo
	 */
	public DeviceGetInfo(){
		super();
		super.CMD = "DeviceGetInfoRes";

	}

	/**
	 * Ejecuta la operación que devuelve la información acerca de un nodo.
	 */
	@Override
	public String execute(JSONObject jsonObject, Context context) {

		//Recupero las preferencias para saber la info del nodo
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		String deviceID = sharedPref.getString("pref_powerDeviceIdType", "1");
		String deviceVersion = sharedPref.getString("pref_powerDeviceVersionType", "0.2");

		//Generar respuesta

		super.p = new String[2];
		super.p[0] = deviceID;
		super.p[1] = deviceVersion;


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
