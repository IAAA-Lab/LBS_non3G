package com.iaaa.message;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.iaaa.appnode.Config;
import com.iaaa.data.DBMessageAdapter;

/**
 * Servicio Android que gestiona los SMS recibidos
 * 
 * Una vez que recibe un mensaje, se lo pasa al objeto OperationService, el cual
 * es el encargado de ejecutar la operación que corresponde a ese mensaje.
 * 
 * Controla el periodo de envío/recibo de SMS según el modo de energía actual
 * 
 * @author Ricardo Pallás
 *
 */
public class MessageService extends Service {



	private final String DEBUG_TAG = "Message_Lib";
	private static int MAX_SMS_MESSAGE_LENGTH = 160; //Máxima longitud de un mensaje
	private DBMessageAdapter db; //Para trabajar con los mensajes en la base de datos

	IntentFilter intentFilter; //Filtro para la recepción de SMS
	IntentFilter sentSmsIntentFilter; //Filtro para la confirmación de mensajes enviados
	SMSOperationWrapper operationWrapper; //Para invocar a las operaciones
	SMSSender smsSender; //clase para enviar SMS al servidor




	/*
	 * BroadcastReceiver que recibe los mensajes SMS entrantes 
	 */
	private BroadcastReceiver smsReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent){
			//SMS recibido
			String body = intent.getExtras().getString("smsBody");
			String address = intent.getExtras().getString("smsAddress");
			Log.d(DEBUG_TAG,"SMS recibido de : "+address + ": "+body);

			//Creo el operationService
			operationWrapper = new SMSOperationWrapper();
			//String para almacenar el resultado de la operacion
			String opResult;
			//Se ejecuta la operacion
			opResult = operationWrapper.executeOperation(body,getApplicationContext());
			Log.d(DEBUG_TAG,"Resultado de la operación : "+opResult);

			//envio el mensaje con la respuesta a la operación

			try {
				String serverPhoneNumber = Config.getServerPhoneNumber(context);
				smsSender.sendMessage(serverPhoneNumber, opResult);
			} catch (Exception e) {
				e.printStackTrace();
			} 



		}
	};





	@Override
	public void onCreate() {
		Log.d(DEBUG_TAG,"Message service started");

		//añadir el intent filter para  recibir los sms
		intentFilter = new IntentFilter();
		intentFilter.addAction("SMS_RECEIVED_ACTION");
		//Registrar el receptor para recibir los sms
		registerReceiver(smsReceiver,intentFilter);

		//Borrar de la base de datos todos los mensajes enviados
		db = new DBMessageAdapter(this);
		db.open();
		db.deleteSentMessages();
		Log.d(DEBUG_TAG, "Mensajes enviados, borrados de la base de datos");
		db.close();
		
		Log.d("NUMERO DEL SERVIDOR",Config.getServerPhoneNumber(this));

	}


	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(DEBUG_TAG,"Service destroyed");
		//Se anula el registro del receptor para no recibir sms
		unregisterReceiver(smsReceiver);


	}

}
