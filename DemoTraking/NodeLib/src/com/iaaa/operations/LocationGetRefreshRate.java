package com.iaaa.operations;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.iaaa.appnode.Config;
import com.iaaa.appnode.HashCalculator;
import com.iaaa.json.JSONHelper;

/**
 * Clase que representa la operación de LocationGetRefreshRate
 * @author Ricardo Pallás
 */
public class LocationGetRefreshRate extends OperationBase {

	/**
	 * Constructor del objeto operación LocationGetRefreshRate
	 */
	public LocationGetRefreshRate(){
		super();
		super.CMD = "LocationRefreshRateRes";
		
	}

	/**
	 * Ejecuta la operación que devuelve la tasa de refresco de un nodo.
	 */
	@Override
	public String execute(JSONObject jsonObject, Context context) {
		
		//Obtener las preferencias donde se almacena la info de configuracion sms
		String locationRefreshRate = Config.getLocationRefreshRate(context)+""; 
		
		//Generar respuesta
		super.p = new String[1];
		super.p[0] = locationRefreshRate;
		
		//Convertir a JSON String
		JSONHelper jsonHelper = new JSONHelper();
		String result = jsonHelper.operationToJSONString(this);
		
		//calcular HASH
		HashCalculator hashCalculator = new HashCalculator();
		String hash = hashCalculator.calculateHash(result);

		//actualizar JSON String con el hash
		super.h = hash;
		result = jsonHelper.operationToJSONString(this);
		
		//Devolver resultado
		return result;
	}
	

}
