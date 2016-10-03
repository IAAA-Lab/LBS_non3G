package com.geoslab.tracking.web.domain;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Clase que almacena un HashMap con Long y Semaphore. Cada elemento corresponde con 
 * el semáforo utilizado para sincronizar las peticiones del servidor a los nodos y 
 * las respuestas de éstos al mismo. Para cada comunicación se mantiene un semáforo 
 * único identificado por el valor de Long en el HashMap. Dicho valor se corresponde 
 * con el identificador de secuencia (seqId) de cada operación.
 * @author rafarn
 *
 */
public class SemaphoreList {

	// HashMap donde se almacenan los semáforos
	private HashMap<Long, Semaphore> semaphores;
	
	public String getContent(){
		String result = "--LIST--";
		for (Entry<Long, Semaphore> entry : semaphores.entrySet())
		    result += "\n" + Long.toString(entry.getKey()) + 
	    			  "/" + Integer.toString(entry.getValue().availablePermits());
		return result;
	}
	
	public SemaphoreList() {
		semaphores = new HashMap<Long, Semaphore>();
	}
	
	/**
	 * Crea un nuevo semáforo si no existe ya para el identificador que se pasa 
	 * como parámetro
	 * @param id: long que identifica el nuevo semáforo
	 */
	public void create(long id){
		if (!semaphores.containsKey(id))
			semaphores.put(id, new Semaphore(0));
	}
	
	/**
	 * Función que realiza la función acquire sobre el semáforo identificado por
	 * el valor de id
	 * @param id: long que identifica el semáforo sobre el que se quiere realizar
	 * 			  el bloqueo
	 * @return boolean: devuelve true si se ha producido timeout o false si el semáforo
	 * 					ha sido liberado correctamente
	 */
	public boolean acquire(long id){
		create(id);
		try {
			boolean flag = semaphores.get(id).tryAcquire(10, TimeUnit.SECONDS);
			if (flag)
				// Released
				return false;
			else
				return true;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return true;
		}
	}
	
	/**
	 * Función que realiza la función release sobre el semáforo identificado por
	 * el valor de id
	 * @param id: long que identifica el semáforo sobre el que se quiere realizar
	 * 			  el desbloqueo
	 */
	public void release(long id){
		if (semaphores.containsKey(id))
			semaphores.get(id).release(1);
	}
	
	/**
	 * Función que elimina un semáforo
	 * @param id: identificador del semáforo que se desea eliminar
	 */
	public void delete(long id){
		if (semaphores.containsKey(id))
			semaphores.remove(id);
	}
}
