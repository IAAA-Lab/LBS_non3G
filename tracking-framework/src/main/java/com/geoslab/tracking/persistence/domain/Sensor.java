package com.geoslab.tracking.persistence.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.web.bind.annotation.RequestParam;

@Entity
@Table(name = "SENSORS")
public class Sensor{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;					// Id del nodo en la base de datos (necesario)? TODO
	
	private long sensorId;
	private int type;
	
	private boolean status;				// Indica si el sensor está activo o no
	
	// Información del sensor
	private String sensorDataUnits;
	private String sensorDataType;
	private String sensorDataUncertainity;
	private String sensorDataLowerRange;
	private String sensorDataUpperRange;
	private String sensorDataChannels;
	private String sensorDataPacketFormat;
	
	@ManyToOne
    @JoinColumn(name="node")
    private Node node;
	
	/**********************/
	/**    Constructor    */
	/**********************/
	public Sensor() {}
	
	public Sensor(long sensorId, int type, Node node, boolean status){
		this.sensorId = sensorId;
		this.type = type;
		this.node = node;
		this.status = status;
	}
	
	/**********************/
	/** Getters & Setters */
	/**********************/
	
	public long getSensorId() {
		return sensorId;
	}
	
	public void setSensorId(long sensorId){
		this.sensorId = sensorId;
	}
	
	public long getType() {
		return type;
	}
	
	public void setType(int type){
		this.type = type;
	}
	
	public boolean getStatus() {
		return status;
	}
	
	public void setStatus(boolean status){
		this.status = status;
	}
	
	public String getSensorDataUnits(){
		return sensorDataUnits;
	}
	
	public void setSensorDataUnits(String sensorDataUnits){
		this.sensorDataUnits = sensorDataUnits;
	}
	
	public String getSensorDataType(){
		return sensorDataType;
	}
	
	public void setSensorDataType(String sensorDataType){
		this.sensorDataType = sensorDataType;
	}
	
	public String getSensorDataUncertainity(){
		return sensorDataUncertainity;
	}
	
	public void setSensorDataUncertainity(String sensorDataUncertainity){
		this.sensorDataUncertainity = sensorDataUncertainity;
	}
	
	public String getSensorDataLowerRange(){
		return sensorDataLowerRange;
	}
	
	public void setSensorDataLowerRange(String sensorDataLowerRange){
		this.sensorDataLowerRange = sensorDataLowerRange;
	}
	
	public String getSensorDataUpperRange(){
		return sensorDataUpperRange;
	}
	
	public void setSensorDataUpperRange(String sensorDataUpperRange){
		this.sensorDataUpperRange = sensorDataUpperRange;
	}
	
	public String getSensorDataChannels(){
		return sensorDataChannels;
	}
	
	public void setSensorDataChannels(String sensorDataChannels){
		this.sensorDataChannels = sensorDataChannels;
	}
	
	public String getSensorDataPacketFormat(){
		return sensorDataPacketFormat;
	}
	
	public void setSensorDataPacketFormat(String sensorDataPacketFormat){
		this.sensorDataPacketFormat = sensorDataPacketFormat;
	}
	
}
