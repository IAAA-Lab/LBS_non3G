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
 * Clase que representa la operación de DeviceSetMode
 * @author Ricardo Pallás
 * 
 */
public class DeviceSetMode extends OperationBase {

	/**
	 * Constructor del objeto operaciónDeviceSetMode
	 */
	public DeviceSetMode(){
		super();
		super.CMD = "DeviceACKRes";
		
	}

	/**
	 * Ejecuta la operaciónque fija el modo de operación del nodo.
	 */
	@Override
	public String execute(JSONObject jsonObject, Context context) {
		
		//Extraer parametros
		String setMode = "0";
		
		try {
			JSONArray parametros = jsonObject.getJSONArray("p");
			setMode = parametros.getString(0);

		} catch (JSONException e) {
			e.printStackTrace();
			//Devuelve un error
			return new DeviceErrorRes("DeviceSetMode", "DEV_ERROR_FRAME", "Valores del parámetro p mal formados").execute(null,null);
		}
		
		
		//Obtener preferencias donde se almacena el mode
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		//Se instancia un editor para editar las preferencias
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString("pref_powerModeType", setMode);
		editor.commit();

		
		
		//Generar respuesta
		String commandSource = "DeviceSetMode";
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
