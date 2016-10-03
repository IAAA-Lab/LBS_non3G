package com.iaaa.operations;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.iaaa.appnode.HashCalculator;
import com.iaaa.json.JSONHelper;


/**
 * Clase que representa la operación de DeviceGetMode
 * @author Ricardo Pallas 
 */
public class DeviceGetMode extends OperationBase {

	/**
	 * Constructor del objeto operación DeviceGetMode
	 */
	public DeviceGetMode(){
		super();
		super.CMD = "DeviceModeRes";

	}

	/**
	 * Ejecuta la operación que devuelve el modo de operación del nodo.
	 */
	@Override
	public String execute(JSONObject jsonObject, Context context) {

		//Obtener preferencias porque especifican el powermode
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		String mode = sharedPref.getString("pref_powerModeType", "1");

		//Generar respuesta
		super.p = new String[1];
		super.p[0] = mode;


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
