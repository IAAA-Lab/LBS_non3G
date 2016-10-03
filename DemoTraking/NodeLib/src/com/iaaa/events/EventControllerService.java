package com.iaaa.events;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.iaaa.appnode.Config;
import com.iaaa.location.LocationFacade;
import com.iaaa.message.SMSSender;
import com.iaaa.message.SmsTooLongException;
import com.iaaa.sensors.SensorFacade;

/**
 * Clase que controla el envío de eventos en intervalos de tiempos y ofrece
 * métodos para enviar eventos.
 * 
 * @author Ricardo Pallás 
 */
public class EventControllerService extends Service {


	final String DEBUG_TAG = "EventControllerService";
	private Context context = this;
	private OnSharedPreferenceChangeListener listener; //Para escuchar cambios en la configuración

	//Atributos para lanzar eventos de manera periódica
	static ScheduledExecutorService executor;
	static Runnable runnable;
	static ScheduledFuture<?> future;

	//Atributos para lanzar eventos de sensores de manera periódica
	static ScheduledExecutorService sensorExecutor;
	static Runnable sensorRunnable;
	static ScheduledFuture<?> sensorFuture;

	SensorFacade sensorFacade;
	//Atributo de la librería de lectura de sensores
	private LocationFacade locationFacade;

	//Atriuto para mandar los eventos por SMS
	private SMSSender sms;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(DEBUG_TAG,"Event controller started");
		//Instancia el SMSSender
		sms = new SMSSender(context);
		//Instancia el LocationFacade
		locationFacade = new LocationFacade(context);
		//Pone a escuchar la localización con el refresh según el modo
		locationFacade.startLocationListening(Config.getLocationRefreshRate(this));
		//Lanzar evento de localización periódico
		startLocationEvent();
		//Lanzar lectura de sensores periódica
		sensorFacade = new SensorFacade(context);
		sensorFacade.setSensorListening(Config.getSensorRefreshRate(this));
		//Obtener preferencias para registrar el listener que escucha cambios de configuración
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		//Listener
		listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
			/*
			 * Cuando cambia la configuracion lee el nuevo valor de refreshRate,
			 * cancela la ejecución previa del evento de localizacion y lo vuelve 
			 * a lanzar con el intervalo actualizado. 
			 */
			public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
				//Cambio en la tasa de refresco de lanzamiento del evento
				if (key.equals("pref_refreshRateType")) {
					//Obtiene el nuevo intervalo
					int refreshRate = Config.getLocationEventRefreshRate(context);
					Log.d(DEBUG_TAG, "Event's refresh rate changed to " + refreshRate);
					//Cambia el intervalo de ejecución
					changeDelay(refreshRate);
				}

				//Cambio en la tasa de refresco de obtención de localización
				if (key.equals("pref_powerModeType")) {
					//Se cambia el locationRefreshRate
					locationFacade.changeRefreshRate(Config.getLocationRefreshRate(context));
				}
				
				//Cambio en la tasa de lectura de sensores
				if (key.equals("pref_sensorRefreshRateType")) {
					//Obtiene el nuevo intervalo
					int refreshRate = Config.getSensorRefreshRate(context);
					Log.d(DEBUG_TAG, "Sensor's refresh rate changed to " + refreshRate);
					//Se cambia el intervalo de ejecucción
					sensorFacade.setSensorListening(refreshRate);
				}

			}
		};
		prefs.registerOnSharedPreferenceChangeListener(listener);

		//Lanza el listener de batería baja que lanza el evento PowerLow
		startPowerLowEvent();
		//Enviar el evento DeviceDescription al servidor
		Log.d(DEBUG_TAG, deviceDescription()); 


	}
	@Override
	public void onDestroy() {
		Log.d(DEBUG_TAG, "Event controller destroyed");
		//Cancelar ejecución de eventos pendientes
		future.cancel(false);
		//Dejar de esuchar la localización
		locationFacade.stopLocationListening();
		sensorFacade.stopSensorListening();
	}

	/**
	 * 
	 * @return La respuesta en un String con formato JSON del evento descripci�n
	 */
	public String deviceDescription() {
		return new DeviceDescription().execute(this);
	}

	/**
	 * 
	 * @return La respuesta en un String con formato JSON del evento de error
	 */
	public String deviceError(String deviceErrorCode, String additionalInfo) {
		return new DeviceError(deviceErrorCode, additionalInfo).execute();

	}

	/**
	 * Programa el evento de localización, que enviar la localización del nodo
	 * en un intervalo de tiempo definido en la configuracion (preferencias).
	 */
	public void startLocationEvent() {
		//Obtener el refresco
		int refreshRate = Config.getLocationEventRefreshRate(this);
		//Lanzar el evento repetidamente
		reStartLocationEvent(refreshRate);
	}

	/**
	 * Lanza un evento de localización y lo vuelve a programar el lanzamiento del siguiente
	 * evento dentro de [refreshRate] segundos
	 * @param refreshRate intervalo de lanzamiento del siguiente evento en segundos
	 */
	private void reStartLocationEvent(int refreshRate){
		//Lanzar el evento LocationEvent
		executor = Executors.newScheduledThreadPool(1);
		runnable = new Runnable() {
			@Override
			public void run() {
				String event = new LocationEvent().execute(context,locationFacade);
				Log.d("LocationEvent", event );
				String phoneNumber = Config.getServerPhoneNumber(context);
				/*try {
					sms.sendMessage(phoneNumber, event);
				} catch (NullPointerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SmsTooLongException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
			}
		};
		//Se ha ejecutado el evento, volver a programar otro evento.
		future = executor.scheduleWithFixedDelay(runnable, 0, refreshRate, TimeUnit.SECONDS);
	}

	/*
	 * Cambia el intervalo de ejecución del evento LocationEvent a [newDelay] en segundos.
	 */
	private  static void changeDelay(int newDelay) {
		boolean res = future.cancel(false);

		System.out.println("Previous task canceled: " + res);

		future = executor.scheduleWithFixedDelay(runnable, 0, newDelay, TimeUnit.SECONDS);
	}



	/**
	 * Registra un BroadcastReceiver que recibe el evento de batería baja. Una
	 * vez recibido ejecuta el evento del protocolo PowerLow y envía el
	 * resultado al servidor por SMS
	 */
	private void startPowerLowEvent() {
		BroadcastReceiver batteryInfoReceiverLevel;
		batteryInfoReceiverLevel = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// Power low event
				Log.d("Event controller", "POWER LOW EVENT");
				// Se manda el evento por SMS
				String result = new PowerLow().execute(context
						.getApplicationContext());
				/*try {
					sms.sendMessage("628481666", result);
				} catch (NullPointerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SmsTooLongException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
			}
		};
		// register your Receiver after initialization
		this.getApplicationContext().registerReceiver(
				batteryInfoReceiverLevel,
				new IntentFilter("android.intent.action.BATTERY_LOW"));
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/**
	 * Programa el evento de localización, que enviar la localización del nodo
	 * en un intervalo de tiempo definido en la configuracion (preferencias).
	 */
	public void startSensorEvent() {
		//Obtener el refresco
		int refreshRate = Config.getSensorRefreshRate(this);
		//Lanzar el evento repetidamente
		reStartSensorEvent(refreshRate);
	}

	/**
	 * Lanza un evento de sensores y lo vuelve a programar el lanzamiento del siguiente
	 * evento dentro de [refreshRate] segundos
	 * @param refreshRate intervalo de lanzamiento del siguiente evento en segundos
	 */
	private void reStartSensorEvent(int refreshRate){
		//Lanzar el evento LocationEvent
		sensorExecutor = Executors.newScheduledThreadPool(1);
		sensorRunnable = new Runnable() {
			@Override
			public void run() {
				Log.d("Leyendo sensores", "Leyendo sensores" );
				//sensorFacade = new SensorFacade(context);
				//sensorFacade.setSensorListening(refreshRate);

			}
		};
		//Se ha ejecutado el evento, volver a programar otro evento.
		sensorFuture = sensorExecutor.scheduleWithFixedDelay(sensorRunnable, 0, refreshRate, TimeUnit.SECONDS);
	}

	/*
	 * Cambia el intervalo de ejecución del evento LocationEvent a [newDelay] en segundos.
	 */
	private  static void changeSensorDelay(int newDelay) {
		boolean res = sensorFuture.cancel(false);

		System.out.println("Previous task canceled: " + res);

		sensorFuture = sensorExecutor.scheduleWithFixedDelay(sensorRunnable, 0, newDelay, TimeUnit.SECONDS);
	}

}
