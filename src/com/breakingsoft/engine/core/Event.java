package com.breakingsoft.engine.core;

/**
 * <p>Event object used in the engine's event system.</p>
 * <p> 
 * Every time an event is fired, an associated Event object is sent to 
 * the listeners.<br/>
 * An event has a type, a custom data and a reference to the sender.
 * </p>
 * @author Hadrien
 *
 */
public class Event {
	
	public Object sender;
	public Object data;
	public String type;
	
	/**
	 * Default constructor
	 * @param type
	 * @param data
	 * @param sender
	 */
	public Event(String type, Object sender, Object data){
		this.type = type;
		this.sender = sender;
		this.data = data;
	}
	
	public Event(String type, Object sender){
		this(type, sender, null);
	}
	
	public Event(){}
}
