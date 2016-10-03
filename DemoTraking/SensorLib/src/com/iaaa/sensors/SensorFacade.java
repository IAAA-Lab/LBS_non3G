package com.iaaa.sensors;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.util.Log;

/**
 * Clase que ofrece una interfaz para acceder a los valores de los sensores del dispositivo.
 * @author Ricardo Pallás
 *
 */
public class SensorFacade {
	private Context context; //Contexto de la aplicación
	private SensorReader sensorReader; //Objeto para leer sensores
	private final String DEBUG_TAG = "SensorLib";

	//Atributos para lanzar eventos de manera periódica
	static ScheduledExecutorService executor;
	static Runnable runnable;
	static ScheduledFuture<?> future;

	/**
	 * Constructor del objeto facade
	 * @param context Contexto de la aplicación
	 */
	public SensorFacade(Context context){
		this.context = context;
		sensorReader = new SensorReader(context);
	}
	
	/**
	 * Devuelve un objeto SensorValue con el último valor
	 * leído del barómetro.
	 * @return Último valor leído del barómetro
	 */
	public SensorValue getPressure(){
		return sensorReader.getLastPressure();
	}
	/**
	 * Devuelve un objeto SensorValue con el último valor
	 * leído del sensor de luz.
	 * @return Último valor leído del sensor de luz
	 */
	public SensorValue getLight(){
		return sensorReader.getLastLight();
	}
	
	/**
	 * Devuelve un objeto SensorValue con el último valor 
	 * leído del sensor de campo magnético.
	 * @return Último valor leído del sensor de campo magnético.
	 */
	public SensorValue getMagneticField(){
		return sensorReader.getLastMagneticField();
	}

	/**
	 * Actualiza la lectura de los sensores del sensorReader cada [interval] segundos
	 * @param interval Intervalo de actualización de la lectura de los sensores en segundos.
	 */
	public void setSensorListening(int interval){
		listenSensors(interval);
	}
	/**
	 * Para la lectura periódica de sensores.
	 */
	public void stopSensorListening(){
		boolean res = future.cancel(false);
		Log.d(DEBUG_TAG,"Listening sensors canceled: " + res);
	}

	/**
	 * Lanza la lectura de los sensores cada [refresRate] segundos
	 * @param refreshRate intervalo de lectura de sensores en segundos
	 */
	private void listenSensors(int refreshRate){
		//Cancelar tarea previa si la hay
		if(future!=null){
			boolean res = future.cancel(false);
			System.out.println("Previous ListenSensors stopped: " + res);
		}
		//Programar la nueva tarea con el nuevo intervalo
		executor = Executors.newScheduledThreadPool(1);
		runnable = new Runnable() {
			@Override
			public void run() {
				sensorReader.startListening();
			}
		};
		//Se ha ejecutado la tarea, se vuelve a programar otra igual dentro de [refreshRate] segundos
		future = executor.scheduleWithFixedDelay(runnable, 0, refreshRate, TimeUnit.SECONDS);
	}
}
