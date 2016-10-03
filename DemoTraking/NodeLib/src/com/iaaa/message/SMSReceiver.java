package com.iaaa.message;

import com.iaaa.stats.Stats;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

/**
 * Clase que recibe los SMS y los manda en un broadcast intent que recibe la clase MessageSerivce.
 * 
 * Nota:
 * Se le debe dar máxima prioridad en el manifest para que sea la primera en recibir los mensajes y no la app por defecto 
 * del sistema operativo Android.
 * 
 * @author Ricardo Pallás
 */
public class SMSReceiver extends BroadcastReceiver {



	private static final String SMS_EXTRA_NAME = "pdus";
	private Stats stats;

	@Override
	public void onReceive(Context context, Intent intent) {

		//Almacenar los sms recibidos
		Bundle extras = intent.getExtras();

		SmsMessage[] msgs = null;
		String messages = "";
		String body = "";
		String address = "";

		if (extras != null) {

			//se recupera el array del sms recibido
			Object[] smsExtra = (Object[]) extras.get(SMS_EXTRA_NAME);

			msgs = new SmsMessage[smsExtra.length];
			//Si el mensaje es mas de 160 caracteres se recibe como mas mensajes
			for (int i=0; i<msgs.length; i++){

				msgs[i] = SmsMessage.createFromPdu((byte[]) smsExtra[i]);

				body = msgs[i].getMessageBody().toString();
				address  = msgs[i].getOriginatingAddress();

				/*messages += "SMS from " + address + " :\n";                    
                messages += body + "\n";*/

				//Almaceno el mensaje en la base de datos

			}

			//Muestro el sms recibido en un toast
			Toast.makeText(context,  body, Toast.LENGTH_LONG).show();


			//se debería lanzar el servicio por si no está lanzado?
			
			
			//Envio el intent broadcast para que  MessageService reciba el sms
			Intent broadcastIntent = new Intent();
			broadcastIntent.setAction("SMS_RECEIVED_ACTION");
			broadcastIntent.putExtra("smsBody", body);
			broadcastIntent.putExtra("smsAddress", address);
			context.sendBroadcast(broadcastIntent);
			//Aborto el broadcast a otras aplicaciones, para que no llege a la app de mensajes del dispositivo
			this.abortBroadcast();
			
			//Anotar mensaje enviado en las estadásticas
			stats = new Stats(context);
			stats.receivedMessage();
		}

	}




}
