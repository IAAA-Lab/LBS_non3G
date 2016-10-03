package com.iaaa.events;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.iaaa.appnode.HashCalculator;
import com.iaaa.json.JSONHelper;
/**
 * Clase que representa el evento DeviceDescription
 * @author Ricardo Pallás
 */
public class DeviceDescription extends EventBase {
	
	/**
	 * Constructor del objeto operación DevicePing
	 */
	public DeviceDescription(){
		super();
		super.EVT = "DeviceDescriptionEvent";
		
	}
	/**
	 * Ejecuta el evento que manda la descripcion del nodo
	 */
	@Override
	public String execute(Context context) {
		
		//Obtener las preferencias donde se almacena la info del nodo
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		String deviceID = sharedPref.getString("pref_powerDeviceIdType", "");
		String deviceVersion = sharedPref.getString("pref_powerDeviceVersionType", "");
		
		//Generar respuesta

		String deviceDescription = "ID: "+deviceID+ " Version: "+deviceVersion;
		super.p = new String[1];
		super.p[0] = deviceDescription;

		
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
