package com.geoslab.tracking.persistence.domain;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "SENSORDATA")
public class SensorData extends Measurement{
	
	private float[] data;
	
	@ManyToOne
    @JoinColumn(name="sensor")
    private Sensor sensor;
	
	@ManyToOne
    @JoinColumn(name="node")
    private Node node;
	
	/**********************/
	/**    Constructor    */
	/**********************/
	public SensorData() {}
	
	public SensorData(float[] data, Sensor sensor, Node node, long utcTime) {
		this.data = data;
		this.sensor = sensor;
		this.node = node;
		this.time = utcTime;
	}

	/**********************/
	/** Getters & Setters */
	/**********************/
	
	public float[] getData() {
		return data;
	}
	
	public void setData(float[] data){
		this.data = data;
	}
	
	public Sensor getSensor(){
		return sensor;
	}
	
	public void setSensor(Sensor sensor){
		this.sensor = sensor;
	}
	
	public Node getNode(){
		return node;
	}
	
	public void setNode(Node node){
		this.node = node;
	}
}
