package com.agenth.engine.core;

/**
 * Interface describing an event listener.<br/>
 * Event listener should listen to event emitters using EventEmitter.bind(). 
 * The emitter will call onEvent() with the appropriate Event object when 
 * an event this listener is listening to is fired.
 *
 */
public interface EventListener {
	/**
	 * Implement this method to do stuff when an event is triggered. 
	 * Use event.type() to get the type of event that was fired.
	 * @param evt
	 */
	public void onEvent(Event evt);
	
}
