package com.iaaa.operations;

import org.json.JSONObject;

import android.content.Context;

import com.iaaa.appnode.HashCalculator;
import com.iaaa.json.JSONHelper;

/**
 * Clase que representa la operación de error DeviceErrorRes
 * @author Ricardo Pallas
 */
public class DeviceErrorRes extends ErrorBase {

	/**
	 * Constructor del objeto DeviceErrorRes
	 * 
	 * @param commandSource Operación que ha generado el error
	 * @param deviceErrorCode Código de error
	 * @param additionalInfo Comentario adicional acerca del error producido.
	 */
	public DeviceErrorRes(String commandSource, String deviceErrorCode, String additionalInfo){
		super();
		super.CMD = "DeviceErrorRes";
		super.p = new String[3];
		super.p[0] = commandSource;
		super.p[1] = deviceErrorCode;
		super.p[2] = additionalInfo;
	}

	/**
	 * Transforma el objeto en un string JSON con los parámetros adecuados y
	 * lo devuelve.
	 * @return String JSON con el error (con el formato DeviceErrorRes del protocolo).
	 */
	public String execute(JSONObject jsonObject, Context context) {	
		//Convertir a JSON String
		JSONHelper jsonHelper = new JSONHelper();
		String result = jsonHelper.operationToJSONString(this);

		//calcular HASH
		HashCalculator hashCalculator = new HashCalculator();
		String hash = hashCalculator.calculateHash(result);

		//actualizar JSON String con el hash
		super.h = hash;
		result = jsonHelper.operationToJSONString(this);
		
		return result;
	}



}
