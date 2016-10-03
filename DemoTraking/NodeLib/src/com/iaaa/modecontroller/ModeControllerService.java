package com.iaaa.modecontroller;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.iaaa.appnode.Config;
import com.iaaa.message.SMSSender;

/**
 * Servicio Android que controla el encendido/apagado peródico de la red móvil, dependiendo
 * de la configuración de la aplicación.
 * 
 * (Sólo funciona en dispositivos con acceso root)
 * 
 * @author Ricardo Pallás
 *
 */
public class ModeControllerService extends Service{

	/**
	 * TODO - LISTENER SMSPOLLTIME Y TRANSMITPERIOD
	 */

	private final static String DEBUG_TAG = "ModeControllerService"; //Etiqueta de depuración
	private OnSharedPreferenceChangeListener listener; //Para escuchar cambios en la configuracion
	private Context context = this;
	private final int TIME_NETWORK_ON = 120; //Segundos por defecto en los cuales la red debe de estar encendida

	//Atributos para lanzar tareas de manera periódica
	static ScheduledExecutorService executor;
	static Runnable runnable;
	static Runnable runnableOn;
	static ScheduledFuture<?> future;
	static ScheduledFuture<?> futureOn;
	
	//Atributo para enviar poder Enviar todos los sms de la base de datos (cuando se recupera la red)
	private SMSSender sms;

	/*
	 * Intervalos de envío y recibo de mensajes en segundos.
	 * 
	 * Nota: smsPollTime <= smsTransmitPeriod, en caso contrario no tendría sentido. 
	 * Porque si, por ejemplo, mandas mensajes cada hora (smsTransmitPeriod) y recibes cada día (smsPollTime),
	 * cuando conectes la red, cada hora, para enviar mensajes, automáticamente vas a recibir mensajes (porque se
	 * ha conectado la red), y se estarían recibiendo cada hora, no cada día.
	 */
	private int smsPollTime = 0;
	private int smsTransmitPeriod = 0;

	@Override
	public void onCreate() {
		super.onCreate();
		context = this;
		Log.d(DEBUG_TAG,"Mode controller started");
		//Instanciar el objeto de SMS
		sms = new SMSSender(context);
		startSwitchingPlaneMode(60,TIME_NETWORK_ON); //TODO config.getDefaultMode

		//Obtener preferencias para registrar el listener que escucha cambios de configuración
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		//Listener
		listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
			/*
			 * Cuando cambia la configuracion lee el nuevo valor de modo de operación,
			 * cancela la ejecución previa del evento de localizacion y lo vuelve 
			 * a lanzar con el intervalo actualizado. 
			 */
			public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
				//Si la preferencia cambiada es powerModeType se lee
				if (key.equals("pref_powerModeType")) {
					//Obtener el nuevo power mode
					int powerId = Config.getPowerMode(context);
					Log.d(DEBUG_TAG, "Power mode changed to " + powerId);

					switch (powerId) {
					case 0:
						//GSM always on
						//Se cancela el intervalo de conexión y desconexión por si lo había
						stopSwitchingPlaneMode();
						//Se quita el modo avión 
						PlaneModeSwitcher.setPlaneModeOff();
						break;
					case 1:
						//Extreme power low mode
						smsPollTime=86400; //segundos
						smsTransmitPeriod = 86400; //Segundos
						changeDelay(TIME_NETWORK_ON, 180);
						break;
					case 2:
						//Very low power mode
						smsPollTime=180; //segundos
						smsTransmitPeriod = 86400; //Segundos
						changeDelay(TIME_NETWORK_ON, 120);
						break;
					case 3:
						//Low power mode
						smsPollTime=120; //segundos
						smsTransmitPeriod = 900; //Segundos
						changeDelay(TIME_NETWORK_ON, 60);
						break;
					case 4:
						//Always locate mode

						smsPollTime=0; //segundos, always ON
						smsTransmitPeriod = 60; //Segundos


						//Primera version always ON
						//Se cancela el intervalo de conexión y desconexión por si lo había
						stopSwitchingPlaneMode();
						//Se quita el modo avión 
						PlaneModeSwitcher.setPlaneModeOff();


						break;
					case 5:
						//Custom mode
						//hay que leer los valores en las preferencias
						smsPollTime=Config.getSmsPollTime(context);
						smsTransmitPeriod=Config.getSmsTransmitPeriod(context);

						//Comprobar que smsPollTime <= smsTransmitPeriod, y si no es así, cambiarlo
						if(smsPollTime>smsTransmitPeriod){
							smsPollTime = smsTransmitPeriod;
							Config.setSmsPollTime(smsPollTime,context);
						}
						changeDelay(TIME_NETWORK_ON, smsPollTime);
						break;
					default:
						break;
					}
				}

			}
		};
		prefs.registerOnSharedPreferenceChangeListener(listener);
	}
	@Override
	public void onDestroy() {
		Log.d(DEBUG_TAG, "Mode controller destroyed");
		//Cancelar ejecución de tareas pendientes
		stopSwitchingPlaneMode();

	}

	/**
	 * Conecta y desconecta todas las conexiones del teléfono (mediante el modo avión), en intervalos.
	 * Se ajusta con parámetros los intervalos en los cuales la red debe estar encendida y apagada.
	 * @param timeNetworkOn Tiempo en el cual la red debe de estar encendida
	 * @param timeNetworkOff Tiempo en el cual la red debe estar apagada
	 * 
	 * <p>Ejemplo: con los parámetros 30 y 60 respectivamente, la red estaría encendida
	 * 30 segundos y después se apagaría durante 60 segundos, luego se volvería a enceder
	 * 30 segundos para volverse a apagar 60 segundos, y así sucesivamente hasta que se llame
	 * al método stopSwitchingPlaneMode(), que parará el ciclo. </p>
	 */
	public void startSwitchingPlaneMode(int timeNetworkOn, int timeNetworkOff){
		if(timeNetworkOff == 0){
			//red siempre encendida
			PlaneModeSwitcher.setPlaneModeOff();
		}
		else {
			executor = Executors.newScheduledThreadPool(2);
			runnable = new Runnable() {

				@Override
				public void run() {
					Log.d(DEBUG_TAG,"running RED ON");
					//Se desactiva el modo avión
					PlaneModeSwitcher.setPlaneModeOff();
					
					
					/*
					 * Se intentan mandar los mensajes que estan almacenados en BD
					 * Se lanza la tarea en 20 segundos para dar tiempo a recuperar la red
					 */
					new Timer().schedule(new TimerTask() {          
					    @Override
					    public void run() {
					        sms.sendStoredMessages();     
					    	Log.d(DEBUG_TAG,"Mandando los mensajes almacenados");
					    }
					}, 20000);
					
					
				}
			};
			runnableOn = new Runnable() {

				@Override
				public void run() {
					Log.d(DEBUG_TAG,"running RED OFF");
					//Se activa el modo avión
					PlaneModeSwitcher.setPlaneModeOn();
				}
			};
			int rate = timeNetworkOn + timeNetworkOff;
			future = executor.scheduleAtFixedRate(runnable, 0, rate, TimeUnit.SECONDS);
			futureOn = executor.scheduleAtFixedRate(runnableOn, timeNetworkOn, rate, TimeUnit.SECONDS);
		}
	}

	/*
	 * Cambia los tiempos de red encendida y red apagada cuando el ciclo está activo.
	 * Es decir se ha llamado al método startSwitchingPlaneMode y se quieren cambiar
	 * los intervalos en caliente.
	 */
	public  static void changeDelay(int timeNetworkOn, int timeNetworkOff) {
		if(timeNetworkOff == 0){
			//red siempre encendida
			PlaneModeSwitcher.setPlaneModeOff();
		}
		else{
			if(future!=null){
				future.cancel(true);
			}
			if(futureOn!=null){
				futureOn.cancel(true);
			}
			int rate = timeNetworkOn + timeNetworkOff;
			future = executor.scheduleAtFixedRate(runnable, 0, rate, TimeUnit.SECONDS);
			futureOn = executor.scheduleAtFixedRate(runnableOn, timeNetworkOn, rate, TimeUnit.SECONDS);
		}
		Log.d(DEBUG_TAG, "Intervals changed to: networkOn="+ timeNetworkOn+" & networkOff="+timeNetworkOff+" seconds");
	}

	/*
	 * Para la tarea startSwitchingPlaneMode
	 */
	public void stopSwitchingPlaneMode(){
		if(future!=null){
			future.cancel(true);
			//quitar modo avión por si estaba encendido
			Log.d(DEBUG_TAG, "Plane Mode Switching Stopped");
		}
		if(futureOn!=null){
			futureOn.cancel(true);
		}
		PlaneModeSwitcher.setPlaneModeOff();

	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
