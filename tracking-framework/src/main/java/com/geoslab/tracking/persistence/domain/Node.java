package com.geoslab.tracking.persistence.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Clase que modela los objetos de tipo Nodo
 * 
 * @author rafarn
 * 
 */

@Entity
@Table(name = "NODES")
public class Node {

	@Id
	private long nodeId;				// Identificador único del nodo
	
	private String status;				// Estado del Nodo: puede ser alive, active o dead
	
	private String deviceVersion;
	
	private int mode;
	
	private String phoneNumber;			// Número de teléfono del nodo
	private String server_phoneNumber;  // Número de teléfono del servidor
	private String smsPollTime;
	private String smsTransmitPeriod;

	private String cloudId;				// Id del servicio de mensajería en la nube (GCM)
	private String server_ip;			// Dirección IP del servidor
	private String httpTransmitPeriod;

	private String deviceDescription;

	private String locationMode;
	private String locationSysRef;
	private String locationDataType;
	private String locationRefreshRate;

	private String powerMode;
	private String powerDataUnits;
	private String powerDataType;
	private int powerMaximum;
	private int powerMinimum;

	/**********************/
	/** Getters & Setters */
	/**********************/
	
	public long getNodeId() {
		return nodeId;
	}

	public void setNodeId(long node_id) {
		this.nodeId = node_id;
	}
	
	public String getStatus(){
		return status;
	}
	
	public void setStatus(String status){
		this.status = status;
	}

	public String getDeviceVersion() {
		return deviceVersion;
	}

	public void setDeviceVersion(String deviceVersion) {
		this.deviceVersion = deviceVersion;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public String getServerPhoneNumber() {
		return server_phoneNumber;
	}

	public void setServerPhoneNumber(String server_phoneNumber) {
		this.server_phoneNumber = server_phoneNumber;
	}

	public String getSmsPollTime() {
		return smsPollTime;
	}

	public void setSmsPollTime(String smsPollTime) {
		this.smsPollTime = smsPollTime;
	}

	public String getSmsTransmitPeriod() {
		return smsTransmitPeriod;
	}

	public void setSmsTransmitPeriod(String smsTransmitPeriod) {
		this.smsTransmitPeriod = smsTransmitPeriod;
	}
	
	public String getCloudId(){
		return cloudId;
	}
	
	public void setCloudId(String cloudId){
		this.cloudId = cloudId;
	}
	
	public String getServerIp(){
		return server_ip;
	}
	
	public void setServerIp(String server_ip){
		this.server_ip = server_ip;
	}
	
	public String getHttpTransmitPeriod(){
		return httpTransmitPeriod;
	}
	
	public void setHttpTransmitPeriod(String httpTransmitPeriod){
		this.httpTransmitPeriod = httpTransmitPeriod;
	}

	public String getDeviceDescription() {
		return deviceDescription;
	}

	public void setDeviceDescription(String deviceDescription) {
		this.deviceDescription = deviceDescription;
	}

	public String getLocationMode() {
		return locationMode;
	}

	public void setLocationMode(String locationMode) {
		this.locationMode = locationMode;
	}

	public String getLocationSysRef() {
		return locationSysRef;
	}

	public void setLocationSysRef(String locationSysRef) {
		this.locationSysRef = locationSysRef;
	}

	public String getLocationDataType() {
		return locationDataType;
	}

	public void setLocationDataType(String locationDataType) {
		this.locationDataType = locationDataType;
	}

	public String getLocationRefreshRate() {
		return locationRefreshRate;
	}

	public void setLocationRefreshRate(String locationRefreshRate) {
		this.locationRefreshRate = locationRefreshRate;
	}

	public String getPowerMode() {
		return powerMode;
	}

	public void setPowerMode(String powerMode) {
		this.powerMode = powerMode;
	}

	public String getPowerDataUnits() {
		return powerDataUnits;
	}

	public void setPowerDataUnits(String powerDataUnits) {
		this.powerDataUnits = powerDataUnits;
	}

	public String getPowerDataType() {
		return powerDataType;
	}

	public void setPowerDataType(String powerDataType) {
		this.powerDataType = powerDataType;
	}

	public int getPowerMaximum() {
		return powerMaximum;
	}

	public void setPowerMaximum(int powerMaximum) {
		this.powerMaximum = powerMaximum;
	}

	public int getPowerMinimum() {
		return powerMinimum;
	}

	public void setPowerMinimum(int powerMinimum) {
		this.powerMinimum = powerMinimum;
	}

}
