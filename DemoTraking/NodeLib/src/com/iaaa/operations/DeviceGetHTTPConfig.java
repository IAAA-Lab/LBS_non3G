package com.iaaa.operations;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.iaaa.appnode.HashCalculator;
import com.iaaa.json.JSONHelper;

/**
 * Clase que representa la operación de DeviceGetHTTPConfig
 * @author Ricardo Pallas
 */
public class DeviceGetHTTPConfig extends OperationBase {

	/**
	 * Constructor del objeto operación DeviceGetHTTPConfig
	 */
	public DeviceGetHTTPConfig(){
		super();
		super.CMD = "DeviceHTTPConfigRes";

	}

	/**
	 * Ejecuta la operación que devuelve la configuración HTTP del nodo.
	 */
	@Override
	public String execute(JSONObject jsonObject, Context context) {

		//Obtener las preferencias donde se almacena la info de configuracion sms
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		String domainName = sharedPref.getString("pref_domainNameType", "");
		String httpTransmitPeriod = sharedPref.getString("pref_httpTransmitPeriodType", "");

		//Generar respuesta
		String httpHashKey = "smsHashKey";

		super.p = new String[3];
		super.p[0] = domainName;
		super.p[1] = httpTransmitPeriod;
		super.p[2] = httpHashKey;



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
