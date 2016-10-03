package com.geoslab.tracking.persistence.domain;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Clase abstracta que representa las mediciones hechas por los nodos
 * del sistema. Agrupa la funcionalidad común a todos los tipos de
 * mediciones diferentes que existen.
 * 
 * @author rafarn
 *
 */

@MappedSuperclass
public abstract class Measurement {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	protected long time;
	
	public long getId() {
		return id;
	}

	public long getTime(){
		return time;
	}
	
	public void setTime(long time){
		this.time = time;
	}

}
