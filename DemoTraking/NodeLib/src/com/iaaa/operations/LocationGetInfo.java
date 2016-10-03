package com.iaaa.operations;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.iaaa.appnode.HashCalculator;
import com.iaaa.json.JSONHelper;

/**
 * Clase que representa la operación de LocationGetInfo
 * @author Ricardo Pallás
 */
public class LocationGetInfo extends OperationBase {

	/**
	 * Constructor del objeto operación LocationGetInfo
	 */
	public LocationGetInfo(){
		super();
		super.CMD = "LocationInfoRes";
		
	}

	/**
	 * Ejecuta la operación que devuelve información acerca del nodo.
	 * Información devuelta: locationMode, locationSysRef, locationDataType.
	 */
	@Override
	public String execute(JSONObject jsonObject, Context context) {
		
		//Obtener las preferencias donde se almacena la info de configuracion sms
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		String locationMode = sharedPref.getString("pref_locationModeType", "");
		String locationSysRef = sharedPref.getString("pref_locationSysRefType", "");


		//Generar respuesta

		String locationDataType = "locationDataType";
		super.p = new String[3];
		super.p[0] = locationMode;
		super.p[1] = locationSysRef;
		super.p[2] = locationDataType;
		
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
