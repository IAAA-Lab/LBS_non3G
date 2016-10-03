package com.iaaa.http;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService {
	public static final int NOTIFICATION_ID = 1;
	NotificationCompat.Builder builder;
	
	private OperationTraductor traductor;
	private HTTPSender sender;

	static final String TAG = "GcmIntentService";

	public GcmIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
			/*
			 * Filter messages based on message type. Since it is likely that GCM
			 * will be extended in the future with new message types, just ignore
			 * any message types you're not interested in, or that you don't
			 * recognize.
			 */
			if (GoogleCloudMessaging.
					MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {


				// Mensajes borrados
			} else if (GoogleCloudMessaging.
					MESSAGE_TYPE_DELETED.equals(messageType)) {

				// LLega un mensaje bueno
			} else if (GoogleCloudMessaging.
					MESSAGE_TYPE_MESSAGE.equals(messageType)) {

				//Procesar mensaje para invocar a una operación
				proccessMessage(extras.toString());
				Log.i(TAG,extras.toString());
			}
		}
		// Release the wake lock provided by the WakefulBroadcastReceiver.
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}


	private void proccessMessage(String msg) {
		//Invocar a una operacion y obtener el resultado
		
		//Enviar resutlado al servidor con 
		
		
	}
}