package com.iaaa.http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Clase que traduce el formato JSON devuelto por las operaciones (el formato
 * para mandar los mensajes), al formato utilizado por los comandos HTTP
 * @author Ricardo Pallás
 *
 */
public class OperationTraductor {

	public OperationTraductor(){

	}

	/**
	 * Transforma el resultado de una operación, pasado como parámetro como
	 * un JSONObject, al formato utilizado por los comandos HTTP y lo devuelve
	 * en un String.
	 * @param json Objecto JSON que representa el resultado de una operación
	 * @return String con la operación transformada al formato de los comandos HTTP
	 */
	public String translateOperationFromJson(JSONObject json) {
		String result = "";
		String keyName = "CMD", valueName = "", keyP = "p", keyH = "h", valueH = "";
		JSONArray valueP = null;
		try {
			valueName = json.getString(keyName);
			valueP = json.getJSONArray(keyP);
			valueH = json.getString(keyH);
		} catch (Exception e) {
			e.printStackTrace();
		}

		result = keyName + "=" + valueName + "&" + translateP(valueName, valueP) + "&"
				+ keyH + "=" + valueH;

		return result;
	}
	
	/**
	 * Transforma el resultado de un evento, pasado como parámetro como
	 * un JSONObject, al formato utilizado por los comandos HTTP y lo devuelve
	 * en un String.
	 * @param json Objecto JSON que representa el resultado de un evento
	 * @return String con la operación transformada al formato de los comandos HTTP
	 */
	public String translateEventFromJson(JSONObject json){
		
		String result = "";
		String keyName = "EVT", valueName = "", keyP = "p", keyH = "h", valueH = "";
		JSONArray valueP = null;
		try {
			valueName = json.getString(keyName);
			valueP = json.getJSONArray(keyP);
			valueH = json.getString(keyH);
		} catch (Exception e) {
			e.printStackTrace();
		}

		result = keyName + "=" + valueName + "&" + translateP(valueName, valueP) + "&"
				+ keyH + "=" + valueH;

		return result;
	}
	

	/*
	 * Transfrorma el parámetro P en una cadena para ser concatenada en la
	 * peticion http. Ejemplo: devuelve Nombre=valor&Nombre2=valor2
	 */
	private String translateP(String cmd, JSONArray p) {
		try {
			if (cmd.equals("DeviceGetInfoRes")) {

				return "deviceId=" + p.getString(0) + "&deviceVersion=" + p.getString(1);

			}
			else if(cmd.equals("DeviceACKRes")){
				return "commandSource=" + p.getString(0);
			}
			else if(cmd.equals("DeviceModeRes")){
				return "mode=" + p.getString(0);
			}
			else if(cmd.equals("DeviceSMSConfigRes")){
				return "phoneNumber=" + p.getString(0) + "&smsPollTime=" + p.getString(1) +
						"&smsTransmitPeriod=" + p.getString(2) + "&smsHashKey=" + p.getString(3);
			}
			else if(cmd.equals("DeviceHTTPConfigRes")){
				return "domainName=" + p.getString(0) + "&httpTransmitPeriod=" + p.getString(1) +
						"&httpHashKey=" + p.getString(2);
			}
			else if(cmd.equals("LocationInfoRes")){
				//Errata el en protocolo
				return "locationMode=" + p.getString(0) + "&locationSysRef=" + p.getString(1) +
						"&locationDataType=" + p.getString(2);
			}
			else if(cmd.equals("LocationRes")){
				return "latitude=" + p.getString(0) + "&nsIndicator=" + p.getString(1) + "&longitude=" + p.getString(2) +
						"&ewIndicator=" + p.getString(3) + "&utcTime=" + p.getString(4);
			}
			else if(cmd.equals("LocationRefreshRateRes")){
				return "locationRefreshRate=" + p.getString(0);
			}
			else if(cmd.equals("PowerInfoRes")){
				return "powerMode=" + p.getString(0) + "&powerDataUnits=" + p.getString(1) + "&powerDataType=" + p.getString(2) +
						"&powerMinimum=" + p.getString(3) + "&powerMaximum=" + p.getString(4);
			}
			else if(cmd.equals("PowerLevelRes")){
				return "powerLevel=" + p.getString(0);
			}
			//Respuesta de error
			else if(cmd.equals("DeviceErrorRes")){
				return "commandSource=" + p.getString(0) + "&deviceErrorCode=" + p.getString(1) +
						"&additionalInfo=" + p.getString(2);
			}
			//EVENTOS
			
			else if(cmd.equals("PowerLowEvent")){
				return "powerLevel="+p.getString(0);
			}
			else if(cmd.equals("DeviceErrorEvent")){
				return "deviceErrorCode=" + p.getString(0) + "&additionalInfo=" + p.getString(1);
			}
			else if(cmd.equals("DeviceDescriptionEvent")){
				return "deviceDescription=" + p.getString(0);
			}
			else if(cmd.equals("LocationEvent")){
				return "latitude=" + p.getString(0) + "&nsIndicator=" + p.getString(1) + "&longitude=" + p.getString(2) +
						"&ewIndicator=" + p.getString(3) + "&utcTime=" + p.getString(4);
			}
			
			
		} catch (JSONException e) {

			e.printStackTrace();
		}
		return "";
	}
}
