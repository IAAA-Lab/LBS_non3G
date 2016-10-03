package com.iaaa.operations;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import com.iaaa.appnode.HashCalculator;
import com.iaaa.json.JSONHelper;

/**
 * Clase que representa la operación de DeviceGetSMSConfig
 * @author Ricardo Pallás
 */
public class DeviceGetSMSConfig extends OperationBase {

	/**
	 * Constructor del objeto operación DeviceGetSMSConfig
	 */
	public DeviceGetSMSConfig(){
		super();
		super.CMD = "DeviceSMSConfigRes";

	}

	/**
	 * Ejecuta la operación que devuelve la configuración SMS del nodo.
	 */
	@Override
	public String execute(JSONObject jsonObject, Context context) {

		//Obtener las preferencias donde se almacena la info de configuracion sms
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		String smsPollTime = sharedPref.getString("pref_smsPollTimeType", "");
		String smsTransmitPeriod = sharedPref.getString("pref_smsTransmitPeriodType", "");
		String phoneNumber = sharedPref.getString("pref_phoneNumberType", "");

		//Generar respuesta
		String smsHashKey = "smsHashKey";

		super.p = new String[4];
		super.p[0] = phoneNumber;
		super.p[1] = smsPollTime;
		super.p[2] = smsTransmitPeriod;
		super.p[3] = smsHashKey;


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
