package com.iaaa.message;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.telephony.SmsManager;
import android.util.Log;

import com.iaaa.data.DBMessageAdapter;
import com.iaaa.modecontroller.PlaneModeSwitcher;
import com.iaaa.stats.Stats;

/**
 * CLase encargada en enviar SMS
 * @author Ricardo Pallás
 *
 */
public class SMSSender {

	private Context context; //Contexto de la aplicación
	private final String DEBUG_TAG = "SMS_SENDER";
	private static String SENT = "SMS_SENT"; // Nombre del filtro para para saber si se ha enviado un sms
	private static int MAX_SMS_MESSAGE_LENGTH = 160; //Máxima longitud de un mensaje

	private Stats stats;
	/**
	 * Constructor de la clase.
	 * @param context Contexto de la aplicación
	 */
	public SMSSender(Context context){
		this.context = context;
		stats = new Stats(context);
	}



	/**
	 * 
	 * Si la red está conectada envía el mensaje SMS directamente. En caso contrario,
	 * almacena el mensaje en la base de datos, para que otro método lo envíe más tarde.
	 * @param phoneNumber 
	 * Número de teléfono al cual enviar el mensaje
	 * @param text 
	 * Texto del mensaje a enviar
	 * <dt><b>Precondition:</b><dd>
	 * phoneNumber y text no pueden ser nulos
	 * @throws NullPointerException
	 * Lanza una NullPointerException si alguno o los dos parámetros son nulos.
	 * @throws SmsTooLongException
	 * Lanza una SmsTooLongException, si el texto que se intenta enviar supera los 160 caracteres.
	 */
	public void sendMessage(String phoneNumber, String text) throws NullPointerException, SmsTooLongException{
		//Comprobar modo avión
		if(PlaneModeSwitcher.isAirplaneModeOn(context)){
			//La señal de red es muy baja para poder mandar un SMS	
			storeMessage(phoneNumber, text, false); //se almacena el mensaje para ser enviado más adelante		
		}
		else{
			//modo avión no activo, luego debería haber red
			sendMessageNow(phoneNumber, text); //se envía el mensaje
		}
	}

	/**
	 * 
	 * Envía un mensaje SMS con el contenido de text al número phoneNumber.
	 * @param phoneNumber 
	 * Número de teléfono al cual enviar el mensaje
	 * @param text 
	 * Texto del mensaje a enviar
	 * <dt><b>Precondition:</b><dd>
	 * phoneNumber y text no pueden ser nulos
	 * @throws NullPointerException
	 * Lanza una NullPointerException si alguno o los dos parámetros son nulos.
	 * @throws SmsTooLongException
	 * Lanza una SmsTooLongException, si el texto que se intenta enviar supera los 160 caracteres.
	 */
	public void sendMessageNow(final String phoneNumber, final String text) throws NullPointerException, SmsTooLongException{
		
		long time= System.currentTimeMillis();// para diferenciar los mensajes en el siguiente broadcast;
		
		//Registrar receiver para saber si el mensaje se ha enviado o ha sido error
		BroadcastReceiver sentSmsReceiver = new BroadcastReceiver(){
			
			
			
			@Override
			public void onReceive(Context context, Intent intent) {
				switch (getResultCode())
				{
				case Activity.RESULT_OK:
					Log.d(DEBUG_TAG, "SMS sent succesfully");
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					Log.d(DEBUG_TAG, "Error while sending message: generic failure");
					storeMessage(phoneNumber, text, false);
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					Log.d(DEBUG_TAG, "Error while sending message: no service");
					storeMessage(phoneNumber, text, false);
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					Log.d(DEBUG_TAG, "Error while sending message: null PDU");
					storeMessage(phoneNumber, text, false);
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					Log.d(DEBUG_TAG, "Error while sending message: radio off");
					storeMessage(phoneNumber, text, false);
					break;
				}
				//Se desregistra el receiver para el SMS enviado
				context.unregisterReceiver(this);
			}
		};
		context.registerReceiver(sentSmsReceiver, new IntentFilter(SENT + time));
		
		//Comprobación parámetros
		if(phoneNumber==null || text==null){
			throw new NullPointerException("phoneNumber and text can't be null");
		}
		int length = text.length(); //longitud del mensaje
		if(length > MAX_SMS_MESSAGE_LENGTH) {
			Log.d("DEBUG_TAG", "Mensaje más grande de 160, no se puede enviar.");
			throw new SmsTooLongException("text longer than 160 chars");

		}
		//Se crean PendingIntents que se lanzarán cuando el mensaje está enviado y haya sido recibido
		PendingIntent sentPI = PendingIntent.getBroadcast(context.getApplicationContext(),0, new Intent(SENT + time),0);

		//Se crea una instancia smsManager para el envío del sms
		SmsManager smsManager = SmsManager.getDefault();

		//Envía el SMS
		smsManager.sendTextMessage(phoneNumber, null, text, sentPI, null);
		//Anotar el mensaje enviado en las estadísticas
		stats.sentMessage();

	}

	/**
	 * Almacena en la base de datos el mensaje compuesto
	 * por una fecha actual, el número de teléfono y el texto del mensaje y un flag
	 * indicando que no ha sido enviado.
	 * @param phoneNumber Número de teléfono al cual se envía el mensaje
	 * @param text Cuerpo del mensaje
	 */
	public void storeMessage(String phoneNumber, String text, boolean sent){
		DBMessageAdapter db = new DBMessageAdapter(context);
		db.open();
		db.insertMessage(phoneNumber, text, sent);
		db.close();
	}

	/**
	 * Recupera los mensajes almacenados en la base de datos que no han sido enviados,
	 * los intenta enviar todos y en caso de éxito, los marca como enviados en la base
	 * de datos.
	 */
	public void sendStoredMessages(){
		DBMessageAdapter db = new DBMessageAdapter(context);
		db.open();
		//recuperar mensajes que no han sido enviados
		Cursor messageList = db.getUnsentMessages();
		//comprobar si hay red

		//enviar mensaje uno a uno y actualizar bd uno a uno

		if(messageList!=null){
			while (messageList.moveToNext() && !PlaneModeSwitcher.isAirplaneModeOn(context)) {
				//mandar mensaje
				try {
					sendMessageNow(messageList.getString(2), messageList.getString(3));
				} catch (Exception e) {} 
				//actualizar bd
				db.updateMessage(messageList.getInt(0), true);
			}

		}
		db.close();
	}


}
