package com.iaaa.operations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.iaaa.appnode.HashCalculator;
import com.iaaa.json.JSONHelper;

/**
 * Clase que representa la operación de LocationSetRefreshRate
 * @author Ricardo Pallas
 */
public class LocationSetRefreshRate extends OperationBase {

	
	/**
	 * Constructor del objeto operación LocationSetRefreshRate
	 */
	public LocationSetRefreshRate(){
		super();
		super.CMD = "DeviceACKRes";
		
	}
	
	/**
	 * Ejecuta la operación que fija la tasa de refresco del nodo.
	 */
	@Override
	public String execute(JSONObject jsonObject, Context context) {
		
		//Extraer parametros
		String setLocationRefreshRate = "";
		
		try {
			JSONArray parametros = jsonObject.getJSONArray("p");
			setLocationRefreshRate = parametros.getString(0);
		} catch (JSONException e) {
			e.printStackTrace();
			//Devuelve un error
			return new DeviceErrorRes("LocationSet", "DEV_ERROR_FRAME", "Valores del parámetro p mal formados").execute(null,null);
		}
		
		
		//Obtener preferencias donde se almacena la configuracion SMS
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		//Se instancia un editor para editar las preferencias
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString("pref_refreshRateType", setLocationRefreshRate);
		editor.commit();
		
		
		//Generar respuesta

		String commandSource = "LocationSetRefreshRate";
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
		//Enviar respuesta
		return result;
	}
	

}
