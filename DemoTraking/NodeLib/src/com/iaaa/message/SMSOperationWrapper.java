package com.iaaa.message;

import org.json.JSONObject;

import android.content.Context;

import com.iaaa.json.JSONHelper;
import com.iaaa.operations.DeviceErrorRes;
import com.iaaa.operations.Operations;
import com.iaaa.operations.OperationsFacade;

/**
 * Clase encargada de seleccionar y ejecutar la operación adecuada que invoca el servidor via SMS
 * 
 * @author Ricardo Pallás
 */
public class SMSOperationWrapper {

	//Location Commands (Requests)
	final String LOCATION_GET_INFO = "LocationGetInfoReq";
	final String LOCATION_GET = "LocationGetReq";
	final String LOCATION_SET = "LocationSetReq";
	final String LOCATION_GET_REFRESH_RATE = "LocationGetRefreshRateReq";
	final String LOCATION_SET_REFRESH_RATE = "LocationSetRefreshRateReq";

	//Power Commands (Requests)
	final String POWER_GET_INFO = "PowerGetInfoReq";
	final String POWER_GET_LEVEL = "PowerGetLevelReq";

	//Device Commands (Requests)
	final String DEVICE_GET_INFO = "DeviceGetInfoReq";
	final String DEVICE_PING = "DevicePingReq";
	final String DEVICE_RESET = "DeviceResetReq";
	final String DEVICE_GET_MODE = "DeviceGetModeReq";
	final String DEVICE_SET_MODE = "DeviceSetModeReq";
	final String DEVICE_GET_SMS_CONFIG = "DeviceSMSConfigReq";
	final String DEVICE_SET_SMS_CONFIG = "DeviceSetSMSConfigReq";
	final String DEVICE_GET_HTTP_CONFIG = "DeviceHTTPConfigReq";
	final String DEVICE_SET_HTTP_CONFIG = "DeviceSetHTTPConfigReq";

	public SMSOperationWrapper(){

	}

	/**
	 * 
	 * Ejecuta la operación que que es llamada a través del SMS enviado por el servidor. 
	 * @param smsMessage
	 * String JSON que llega en el SMS mandado por el servidor indicando la operación a ejecutar
	 * <dt><b>Precondition:</b><dd>
	 * smsMessage no puede ser nulo
	 * @return Devuelve un string con la respuesta en formato JSON (del protocolo) producida 
	 * al ejecutar la operación
	 */
	public String executeOperation(String smsMessage, Context context){

		//Se transforma el string JSON en un objeto JSON
		JSONHelper jsonHelper = new JSONHelper();
		JSONObject peticionObj = jsonHelper.convertToJSONObject(smsMessage);

		if(peticionObj!=null){ 
			String commandName = jsonHelper.getCommandName(peticionObj);

			if(commandName!=null){

				//String que contendrá la respuesta de la operaci�n en formato JSON para mandarla al servidor por SMS
				String result = "";

				//Se crea el objeto facade de ofrece las operaciones
				Operations facade = new OperationsFacade();

				//Se llama a la operaci�n dependiendo del comando del mensaje
				//Location commands
				if(commandName.equals(LOCATION_GET_INFO)){
					result = facade.locationGetInfo(peticionObj, context);
				}
				else if(commandName.equals(LOCATION_GET)){
					result = facade.locationGet(peticionObj, context);
				}
				else if(commandName.equals(LOCATION_SET)){
					result = facade.locationSet(peticionObj, context);
				}
				else if(commandName.equals(LOCATION_GET_REFRESH_RATE)){
					result = facade.locationGetRefreshRate(peticionObj, context);
				}
				else if(commandName.equals(LOCATION_SET_REFRESH_RATE)){
					result = facade.locationSetRefreshRate(peticionObj, context);
				}
				//Power commands
				else if(commandName.equals(POWER_GET_INFO)){
					result = facade.powerGetInfo(peticionObj, context);
				}
				else if(commandName.equals(POWER_GET_LEVEL)){
					result = facade.powerGetLevel(peticionObj, context);
				}
				//Device commands
				else if(commandName.equals(DEVICE_GET_INFO)){
					result = facade.deviceGetInfo(peticionObj, context);
				}
				else if(commandName.equals(DEVICE_PING)){
					result = facade.devicePing(peticionObj, context);
				}
				else if(commandName.equals(DEVICE_RESET)){
					result = facade.deviceReset(peticionObj, context);
				}
				else if(commandName.equals(DEVICE_GET_MODE)){
					result = facade.deviceGetMode(peticionObj, context);
				}
				else if(commandName.equals(DEVICE_SET_MODE)){
					result = facade.deviceSetMode(peticionObj, context);
				}
				else if(commandName.equals(DEVICE_GET_SMS_CONFIG)){
					result = facade.deviceGetSMSConfig(peticionObj, context);
				}
				else if(commandName.equals(DEVICE_SET_SMS_CONFIG)){
					result = facade.deviceSetSMSConfig(peticionObj, context);
				}
				else if(commandName.equals(DEVICE_GET_HTTP_CONFIG)){
					result = facade.deviceGetHTTPConfig(peticionObj, context);
				}
				else if(commandName.equals(DEVICE_SET_HTTP_CONFIG)){
					result = facade.deviceSetHTTPConfig(peticionObj, context);
				}
				//ERRORES
				else {
					DeviceErrorRes err = new DeviceErrorRes("unknown", "DEV_ERROR_NOT_IMPLEMENTED", "");
					return jsonHelper.operationToJSONString(err);
				}
				if (result == null){
					DeviceErrorRes err = new DeviceErrorRes("unknown", "DEV_ERROR_FRAME", "");
					return jsonHelper.operationToJSONString(err);
				}
				else {
					return result;
				}
			}
			else { //operacion incorrecta, es JSON pero no tiene nombre CMD de operacion
				DeviceErrorRes err = new DeviceErrorRes("unknown", "DEV_ERROR_NOT_IMPLEMENTED", "");
				return jsonHelper.operationToJSONString(err);
			}
		}
		else { //operación incorrecta, no es JSON
			DeviceErrorRes err = new DeviceErrorRes("unknown", "DEV_ERROR_FRAME", "");
			return jsonHelper.operationToJSONString(err);
		}
	}

}
