package com.iaaa.demotracking;

import java.util.Date;
/**
 * Clase que representa un punto en el mapa
 * @author ricardop
 *
 */
public class Point {

	private int id;
	private Date fecha;
	private double latitud;
	private double longtiud;

	public Point(int id, Date fecha, double latitud, double longitud) {
		this.id = id;
		this.fecha = fecha;
		this.latitud = latitud;
		this.longtiud = longitud;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the fecha
	 */
	public Date getFecha() {
		return fecha;
	}

	/**
	 * @param fecha
	 *            the fecha to set
	 */
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	/**
	 * @return the latitud
	 */
	public double getLatitud() {
		return latitud;
	}

	/**
	 * @param latitud
	 *            the latitud to set
	 */
	public void setLatitud(double latitud) {
		this.latitud = latitud;
	}

	/**
	 * @return the longtiud
	 */
	public double getLongtiud() {
		return longtiud;
	}

	/**
	 * @param longtiud
	 *            the longtiud to set
	 */
	public void setLongtiud(double longtiud) {
		this.longtiud = longtiud;
	}

}
