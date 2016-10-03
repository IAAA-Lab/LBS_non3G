package com.geoslab.tracking.persistence.domain;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "POWERS")
public class Power extends Measurement{
	
	private int level;
	
	@ManyToOne
    @JoinColumn(name="node")
    private Node node;
	
	/**********************/
	/**    Constructor    */
	/**********************/
	public Power() {}
	
	public Power(int level, Node node, long time) {
		this.level = level;
		this.node = node;
		this.time = time;
	}
	
	/**********************/
	/** Getters & Setters */
	/**********************/
	
	public int getLevel(){
		return level;
	}
	
	public void setLevel(int level){
		this.level = level;
	}
	
	public Node getNode(){
		return node;
	}
	
	public void setNode(Node node){
		this.node = node;
	}
}
