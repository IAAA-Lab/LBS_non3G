package com.iaaa.events;

import com.iaaa.appnode.HashCalculator;
import com.iaaa.json.JSONHelper;

/**
 * Clase que representa el evento DeviceError
 * @author Ricardo Pallas
 */
public class DeviceError {

	
	private String[] p; //Array con los par�metros del error
	private String h = null; //Hash del mensaje
	private String EVT = "DeviceErrorEvent"; //Nombre del evento

	/**
	 * Constructor de la clase DeviceError
	 * @param errorCode código de error especificado en el protocolo
	 * @param additionalInfo Información adicional del error.
	 */
	public DeviceError(String deviceErrorCode, String additionalInfo){
		p = new String[2];
		p[0] = deviceErrorCode;
		p[1] = additionalInfo;
	}
	
	public String execute(){
		//Generar respuesta

		//Convertir a JSON String
		JSONHelper jsonHelper = new JSONHelper();
		String result = jsonHelper.operationToJSONString(this);
		
		//calcular HASH
		HashCalculator hashCalculator = new HashCalculator();
		String hash = hashCalculator.calculateHash(result);

		//actualizar JSON String con el hash
		h = hash;
		result = jsonHelper.operationToJSONString(this);
		
		//Devolver respuesta
		return result;
	}
	
}
