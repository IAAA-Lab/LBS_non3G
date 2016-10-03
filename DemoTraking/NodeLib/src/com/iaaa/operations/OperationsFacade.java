package com.iaaa.operations;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

/**
 * Clase que ofrece un conjunto de operaciones especificadas en el protocolo SMS.
 * Las operaciones reciben un JSONObject conteniendo la operación invocada y sus parámetros (la manda el servidor en SMS).
 * @author Ricardo Pallás
 */
public class OperationsFacade implements Operations{
	final String DEBUG_TAG = "NodeExecution";
	Operation command;
	
	public OperationsFacade(){
		
	}
	
	//Location commands
	
	@Override
	public String locationGetInfo(JSONObject jsonObject, Context context) {
		Log.d(DEBUG_TAG, "LocationGetInfo");
		command = new LocationGetInfo();
		String result = command.execute(jsonObject, context);
		return result;
	}

	@Override
	public String locationGet(JSONObject jsonObject, Context context) {
		Log.d(DEBUG_TAG, "LocationGet");
		command = new LocationGet();
		String result = command.execute(jsonObject, context);
		return result;
	}

	@Override
	public String locationSet(JSONObject jsonObject, Context context) {
		Log.d(DEBUG_TAG, "LocationSet");
		command = new LocationSet();
		String result = command.execute(jsonObject, context);
		return result;
	}

	@Override
	public String locationGetRefreshRate(JSONObject jsonObject, Context context) {
		Log.d(DEBUG_TAG, "LocationGetRefreshRate");
		command = new LocationGetRefreshRate();
		String result = command.execute(jsonObject, context);
		return result;
	}

	@Override
	public String locationSetRefreshRate(JSONObject jsonObject, Context context) {
		Log.d(DEBUG_TAG, "LocationSetRefreshRate");
		command = new LocationSetRefreshRate();
		String result = command.execute(jsonObject, context);
		return result;
	}

	//Power commands
	@Override
	public String powerGetInfo(JSONObject jsonObject, Context context) {
		Log.d(DEBUG_TAG, "PowerGetInfo");
		command = new PowerGetInfo();
		String result = command.execute(jsonObject, context);
		return result;
	}

	@Override
	public String powerGetLevel(JSONObject jsonObject, Context context) {
		Log.d(DEBUG_TAG, "PowerGetLevel");
		command = new PowerGetLevel();
		String result = command.execute(jsonObject, context);
		return result;
	}

	
	//Device commands
	@Override
	public String deviceGetInfo(JSONObject jsonObject, Context context) {
		Log.d(DEBUG_TAG, "DeviceGetInfo");
		command = new DeviceGetInfo();
		String result = command.execute(jsonObject, context);
		return result;
	}

	@Override
	public String devicePing(JSONObject jsonObject, Context context) {
		Log.d(DEBUG_TAG, "DevicePing");
		command = new DevicePing();
		String result = command.execute(jsonObject, context);
		return result;
	}

	@Override
	public String deviceReset(JSONObject jsonObject, Context context) {
		Log.d(DEBUG_TAG, "DeviceReset");
		command = new DeviceReset();
		String result = command.execute(jsonObject, context);
		return result;
	}

	@Override
	public String deviceGetMode(JSONObject jsonObject, Context context) {
		Log.d(DEBUG_TAG, "DeviceGetMode");
		command = new DeviceGetMode();
		String result = command.execute(jsonObject, context);
		return result;
	}

	@Override
	public String deviceSetMode(JSONObject jsonObject, Context context) {
		Log.d(DEBUG_TAG, "DeviceSetMode");
		command = new DeviceSetMode();
		String result = command.execute(jsonObject, context);
		return result;
	}

	@Override
	public String deviceGetSMSConfig(JSONObject jsonObject, Context context) {
		Log.d(DEBUG_TAG, "DeviceGetSMSConfig");
		command = new DeviceGetSMSConfig();
		String result = command.execute(jsonObject, context);
		return result;
	}

	@Override
	public String deviceSetSMSConfig(JSONObject jsonObject, Context context) {
		Log.d(DEBUG_TAG, "DeviceSetSMSConfig");
		command = new DeviceSetSMSConfig();
		String result = command.execute(jsonObject, context);
		return result;
	}

	@Override
	public String deviceGetHTTPConfig(JSONObject jsonObject, Context context) {
		Log.d(DEBUG_TAG, "DeviceGetHTTPConfig");
		command = new DeviceGetHTTPConfig();
		String result = command.execute(jsonObject, context);
		return result;
	}

	@Override
	public String deviceSetHTTPConfig(JSONObject jsonObject, Context context) {
		Log.d(DEBUG_TAG, "DeviceSetHTTPConfig");
		command = new DeviceSetHTTPConfig();
		String result = command.execute(jsonObject, context);
		return result;
	}
	


}
