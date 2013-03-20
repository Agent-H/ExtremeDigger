package com.agenth.engine.physics;

public class CollisionEventData {
	
	public Physic.Side side;
	public Physic other;
	
	public CollisionEventData(){
		
	}
	
	public CollisionEventData(Physic other, Physic.Side side){
		this.side = side;
		this.other = other;
	}
}
