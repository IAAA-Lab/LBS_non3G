package com.iaaa.sensors;

import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Clase encargada de leer los valores del los sensores y ofrecer métodos para su acceso.
 * 
 * El funcionamiento es el siguiente: la clase se registra a los cambios de los valores de los sensores.
 * Cuando hay un cambio lee el nuevo valor y lo almacena en variables. Una vez leído se desregistra para no volver
 * a leerlos y así ahorra energía.
 * Una clase externa debe llamar al método startListening() periodicamente para actualizar los valores de los sensores.
 * @author Ricardo Pallás
 *
 */
public class SensorReader implements SensorEventListener {

	private  final String DEBUG_TAG = "SensorService";
	private SensorManager sensorManager = null; //SensorManager para interactuar con los snesores
	private Context context; //Contexto de la aplicación

	private SensorValue lastPressure = null; //Último valor de presión
	private SensorValue lastLight = null; //Último valor de luminosidad
	private SensorValue lastMagneticField = null; //Último valor leído del campo magnético

	/**
	 * Construye un objeto SensorReader, para leer los valores de los sensores
	 * del dispositivo.
	 */
	public SensorReader(Context context){
		Log.d(DEBUG_TAG, "SensorReader created");
		this.context = context;
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
	}
	/**
	 * Registra la clase para recibir eventos cuando cambian los valores
	 * de los sensores.
	 */
	public void startListening(){
		Log.d(DEBUG_TAG, "SensorReader start listening");
		//Obtiene la lista de sensores del dispositivo
		List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

		//Mostrar en el LOG todos los sensores devueltos
		/*for (Sensor sensor : sensors)
		{
			Log.d(DEBUG_TAG, sensor.getName()+ "   "+ sensor.getType());
		}*/
		//Se registra a los sensores de luz y presión (si existen en el dispositivo)
		for (Sensor sensor : sensors)
		{
			if(sensor.getType() == Sensor.TYPE_LIGHT || sensor.getType() == Sensor.TYPE_PRESSURE 
					|| sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){ 
				sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
			}
		}
	}
	/**
	 * Desegistra la clase de los eventos para dejar de leer los valores
	 * de los sensores y no afectar al consumo de energía.
	 * 
	 */
	public void stopListening(){
		sensorManager.unregisterListener(this);
	}

	/**
	 * Devuelve el último valor leído del barómetro
	 * @return Último valor leído del barómetro
	 */
	public SensorValue getLastPressure(){
		return lastPressure;
	}

	/**
	 * Devuelve el último valor leído del sensor de luz
	 * @return Último valor leído del densor de luz
	 */
	public SensorValue getLastLight(){
		return lastLight;
	}
	/**
	 * Devuelve el último valor leído del magnetómetro
	 * @return Último valor leído del magnetómetro
	 */
	public SensorValue getLastMagneticField(){
		return lastMagneticField;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// no se hace nada 

	}

	/**
	 * Método que se ejecuta cuando un sensor cambia su valor.
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		// Lanzar la tarea que obtiene los valores de los sensores
		new SensorEventLoggerTask().execute(event);

		// parar de escuchar este sensor
		sensorManager.unregisterListener(this,event.sensor);		
	}



	/**
	 * Clase privada que se ejecuta en otro hio de ejecución y lee
	 * los valores del sensor cuando hay un cambio.
	 * @author Ricardo Pallás
	 *
	 */
	private class SensorEventLoggerTask extends
	AsyncTask<SensorEvent, Void, Void> {
		@Override
		protected Void doInBackground(SensorEvent... events) {
			float sensorValue = 0;
			int sensorType = 0;
			String sensorName = "Unknown";
			String sensorUnits = "Unknown";

			for (SensorEvent singleEvent : events)
			{
				sensorValue = singleEvent.values[0]; //Valor del sensor leído
				sensorType = singleEvent.sensor.getType(); //tipo del sensor leído
				switch (sensorType) {
				case Sensor.TYPE_LIGHT:
					sensorName = "Light sensor";
					sensorUnits = "Lux";
					Log.d(DEBUG_TAG, "Nombre: "+sensorName+" Valor: "+sensorValue+" "+sensorUnits);
					//Se crea el objeto SensorValue
					lastLight = new SensorValue(sensorName, new float[]{sensorValue}, sensorUnits);		
					LastSensorRead.getInstance().addLightValue(lastLight);
					break;
				case Sensor.TYPE_PRESSURE:
					sensorName = "Pressure sensor";
					sensorUnits = "hPa";
					Log.d(DEBUG_TAG, "Nombre: "+sensorName+" Valor: "+sensorValue+" "+sensorUnits);
					//Se crea el objeto SensorValue
					lastPressure = new SensorValue(sensorName, new float[]{sensorValue}, sensorUnits);
					LastSensorRead.getInstance().addPressureValue(lastPressure);
					break;
				case Sensor.TYPE_MAGNETIC_FIELD:
					sensorName = "Magnetic field sensor";
					sensorUnits = "µT";
					Log.d(DEBUG_TAG, "Nombre: "+sensorName+" Valor: "+sensorValue+" "+sensorUnits);
					//Se crea el objeto SensorValue
					lastMagneticField = new SensorValue(sensorName, new float[]{sensorValue}, sensorUnits);
					LastSensorRead.getInstance().addMagneticFieldValue(lastMagneticField);
					break;
				default:
					break;
				}
			}
			return null;
		}
	}
}
