package com.iaaa.operations;

import org.json.JSONObject;

import android.content.Context;
/**
 * Interfaz que ofrece las operaciones que tiene un nodo según el protocolo.
 * @author Ricardo Pallás
 *
 */
public interface Operations {
	
	//Location Commands
	/**
	 * Ejecuta la operación de LocationGetInfo
	 * @param jsonObject Petición del servidor
	 * @param context Contexto de la aplicación
	 * @return Devuelve en formato JSON la respuesta para ser enviada al servidor,
	 * que incluye los parámetros de configuración de localización del dispositivo
	 */
	public String locationGetInfo(JSONObject jsonObject, Context context);
	/**
	 * Ejecuta la operación de LocationGet
	 * @param jsonObject Petición del servidor
	 * @param context Contexto de la aplicación
	 * @return Devuelve en formato JSON la respuesta para ser enviada al servidor,
	 * que incluye la localización del dispositivo.
	 */
	public String locationGet(JSONObject jsonObject, Context context);
	/**
	 * Ejecuta la operación de LocationSet, que fija la localización del dispositivo.
	 * @param jsonObject Petición del servidor
	 * @param context Contexto de la aplicación
	 * @return Devuelve en formato JSON la respuesta para ser enviada al servidor,
	 * que incluye un ACK para que el servidor sepa que se ha ejecutado la operación.
	 */
	public String locationSet(JSONObject jsonObject, Context context);
	/**
	 * Ejecuta la operación de LocationGetRefreshRate
	 * @param jsonObject Petición del servidor
	 * @param context Contexto de la aplicación
	 * @return Devuelve en formato JSON la respuesta para ser enviada al servidor,
	 * que incluye la tasa de resfresco de la localización
	 */
	public String locationGetRefreshRate(JSONObject jsonObject, Context context);
	/**
	 * Ejecuta la operación de LocationSetRefreshRate, que fija la tasa de refresco de
	 * la localización del dispositivo
	 * @param jsonObject Petición del servidor
	 * @param context Contexto de la aplicación
	 * @return Devuelve en formato JSON la respuesta para ser enviada al servidor,
	 * que incluye un ACK para que el servidor sepa que se ha ejecutado la operación.
	 */
	public String locationSetRefreshRate(JSONObject jsonObject, Context context);
	
	
	
	
	
	
	//Power Commands
	/**
	 * Ejecuta la operación de PowerGetInfo
	 * @param jsonObject Petición del servidor
	 * @param context Contexto de la aplicación
	 * @return Devuelve en formato JSON la respuesta para ser enviada al servidor,
	 * que incluye la información de energía y batería del dispositivo.
	 */
	public String powerGetInfo(JSONObject jsonObject, Context context);
	/**
	 * Ejecuta la operación de PowerGetLevel
	 * @param jsonObject Petición del servidor
	 * @param context Contexto de la aplicación
	 * @return Devuelve en formato JSON la respuesta para ser enviada al servidor,
	 * que incluye la el nivel de batería del dispositivo.
	 */
	public String powerGetLevel(JSONObject jsonObject, Context context);
	
	
	
	
	
	//Device Commands
	/**
	 * Ejecuta la operación de DeviceGetInfo
	 * @param jsonObject Petición del servidor
	 * @param context Contexto de la aplicación
	 * @return Devuelve en formato JSON la respuesta para ser enviada al servidor,
	 * que incluye la ID y versión del dispositivo.
	 */
	public String deviceGetInfo(JSONObject jsonObject, Context context);
	/**
	 * Ejecuta la operación de DevicePing
	 * @param jsonObject Petición del servidor
	 * @param context Contexto de la aplicación
	 * @return Devuelve en formato JSON la respuesta para ser enviada al servidor,
	 * que incluye un ACK confirmando que el dipositivo está vivo.
	 */
	public String devicePing(JSONObject jsonObject, Context context);
	/**
	 * Ejecuta la operación de DeviceReset, que resetea las configuraciones del dispositivo
	 * a las por defecto
	 * @param jsonObject Petición del servidor
	 * @param context Contexto de la aplicación
	 * @return Devuelve en formato JSON la respuesta para ser enviada al servidor,
	 * que incluye un ACK confirmando que la operación se ha ejecutado.
	 */
	public String deviceReset(JSONObject jsonObject, Context context);
	/**
	 * Ejecuta la operación de DeviceGetMode
	 * @param jsonObject Petición del servidor
	 * @param context Contexto de la aplicación
	 * @return Devuelve en formato JSON la respuesta para ser enviada al servidor,
	 * que incluye el modo de energía en el cual se está ejecutando el dispositivo.
	 */
	public String deviceGetMode(JSONObject jsonObject, Context context);
	/**
	 * Ejecuta la operación de DeviceSetMode, que cambia el modo de energía del dispositivo
	 * @param jsonObject Petición del servidor
	 * @param context Contexto de la aplicación
	 * @return Devuelve en formato JSON la respuesta para ser enviada al servidor,
	 * que incluye un ACK confirmando que la operación se ha ejecutado.
	 */
	public String deviceSetMode(JSONObject jsonObject, Context context);
	/**
	 * Ejecuta la operación de DeviceGetSMSConfig
	 * @param context Contexto de la aplicación
	 * @return Devuelve en formato JSON la respuesta para ser enviada al servidor,
	 * que incluye información de la configuración de comunicaciones SMS del dispositivo.
	 */
	public String deviceGetSMSConfig(JSONObject jsonObject, Context context);
	/**
	 * Ejecuta la operación de DeviceSetSMSConfig, que cambia las configuraciones de comunicación
	 * SMS del dispositivo
	 * @param jsonObject Petición del servidor
	 * @param context Contexto de la aplicación
	 * @return Devuelve en formato JSON la respuesta para ser enviada al servidor,
	 * que incluye un ACK confirmando que la operación se ha ejecutado.
	 */
	public String deviceSetSMSConfig(JSONObject jsonObject, Context context);
	/**
	 * Ejecuta la operación de DeviceGetHTTPConfig
	 * @param context Contexto de la aplicación
	 * @return Devuelve en formato JSON la respuesta para ser enviada al servidor,
	 * que incluye información de la configuración de comunicaciones HTTP del dispositivo.
	 */
	public String deviceGetHTTPConfig(JSONObject jsonObject, Context context);
	/**
	 * Ejecuta la operación de DeviceSetHTTPConfig, que cambia las configuraciones de comunicación
	 * HTTP del dispositivo
	 * @param jsonObject Petición del servidor
	 * @param context Contexto de la aplicación
	 * @return Devuelve en formato JSON la respuesta para ser enviada al servidor,
	 * que incluye un ACK confirmando que la operación se ha ejecutado.
	 */
	public String deviceSetHTTPConfig(JSONObject jsonObject, Context context);
	
		
}
