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
 * Clase que representa la operación de DeviceSetHTTPConfig
 * @author Ricardo Pallás
 */
public class DeviceSetHTTPConfig extends OperationBase {

	/**
	 * Constructor del objeto operación DeviceSetHTTPConfig
	 */
	public DeviceSetHTTPConfig(){
		super();
		super.CMD = "DeviceACKRes";

	}

	/**
	 * Ejecuta la operación que fija el modo de operacion SMS de un nodo.
	 */
	@Override
	public String execute(JSONObject jsonObject, Context context) {

		//Extraer parametros
		String setDomainName = "";
		String setHttpTransmitPeriod = "";
		String setHttpHashKey = "";

		try {
			JSONArray parametros = jsonObject.getJSONArray("p");
			setDomainName = parametros.getString(0);
			setHttpTransmitPeriod = parametros.getString(1);
			setHttpHashKey = parametros.getString(2);


		} catch (JSONException e) {
			e.printStackTrace();
			//Devuelve un error
			return new DeviceErrorRes("DeviceSetHTTPConfig", "DEV_ERROR_FRAME", "Valores del parámetro p mal formados").execute(null,null);
		}


		//Obtener preferencias donde se almacena la configuracion SMS
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		//Se instancia un editor para editar las preferencias
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString("pref_domainNameType", setDomainName);
		editor.putString("pref_httpTransmitPeriodType", setHttpTransmitPeriod);
		editor.commit();


		//Generar respuesta
		String commandSource = "DeviceSetHTTPConfig";
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
