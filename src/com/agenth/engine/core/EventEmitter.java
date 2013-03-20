package com.agenth.engine.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Base class for event emitting objects.
 * 
 * Attach listeners using bind(type, listener). Fire events using postEvent(). Detach listeners using unbind().
 * @author Hadrien
 *
 */
public class EventEmitter {

	private HashMap<String, LinkedList<EventListener> > mListeners = new HashMap<String, LinkedList<EventListener>>();
	
	private Event mEvent;
	
	public EventEmitter(){
		mEvent = new Event();
	}
	
	/**
	 * Sends an event to all listeners bound to this type of event.<br />
	 * An event object is created and it's sender field is filled with this.
	 * 
	 * @param type
	 * 		Type of event to fire
	 * @param data
	 * 		Data to attach to this event.
	 */
	public void postEvent(String type, Object data){
		
		mEvent.type = type;
		mEvent.data = data;
		mEvent.sender = this;
		
		LinkedList<EventListener> list = mListeners.get(type);
		
		if(list != null){
			Iterator<EventListener> it = list.iterator();
			while(it.hasNext()){
				EventListener l = it.next();
				l.onEvent(mEvent);
			}
		}
	}
	
	/**
	 * Shorthand method for postEvent(type, null);
	 * @param type
	 * 		Type of event to send. No data will be attached to the event
	 */
	public void postEvent(String type){
		this.postEvent(type, null);
	}
	
	/**
	 * Add an event listener for the specified type of event
	 * @param type
	 * 		Event type
	 * @param listener
	 * 		EventListener to attach
	 */
	public void bind(String type, EventListener listener){
		String[] types = type.split(" ");
		
		for(int i = 0 ; i < types.length ; i++){
			if(!mListeners.containsKey(types[i])){
				mListeners.put(types[i], new LinkedList<EventListener>());
			}
			
			mListeners.get(types[i]).add(listener);
		}
	}
	
	/**
	 * Removes a listener for the specified type of event.
	 * @param type
	 * 		Type of event the listener should no longer listen to
	 * @param listener
	 * 		Listener to detach
	 */
	public void unbind(String type, EventListener listener){
		LinkedList<EventListener> list = mListeners.get(type);
		
		if(list != null){
			list.remove(listener);
		}
	}
}
