package com.agenth.engine.core;

import java.util.HashMap;

import android.util.Log;

import com.agenth.engine.util.LinkedList;

/**
 * An Entity is a basic object which does absolutely nothing.
 * Any functionality should be added by components using require().
 * <p>
 * You can subclass this to define default entities for your game or create a component
 * if you want to define a specific functionality.
 * </p>
 * @author Hadrien
 *
 */
public class Entity extends EventEmitter{
	
	private HashMap<String, Component> mComponents = new HashMap<String, Component>();
	
	private final String mId;
	
	private Game mGame;
	
	private LinkedList<Entity>.Entry mEntry;
	
	public Entity(Game game){
		this(game, "");		
	}
	public Entity(Game game, String id){
		mGame = game;
		mId = id;
	}
	
	/**
	 * Requires multiple components.
	 * @param components
	 * 		list of components formatted as "Component1 Component2 ..."
	 * @return an array of components instances ordered as in the parameter. If a component couldn't be found, 
	 * a null case stands at it's place and indexes are preserved.
	 */
	public Component[] require(String component){
		
		String[] components = component.split(" ");
		Component[] ret = new Component[components.length];
		
		for(int i = 0 ; i < components.length ; i++){
			ret[i] = requireOne(components[i]);
		}
		return ret;
	}
	
	/**
	 * Requires one component only. 
	 * <p>
	 * The component parameter should contain only one component name. 
	 * So no spaces are allowed though the string is not checked.
	 * </p>
	 * @param component
	 * 		Name of the component to require
	 * @return Instance of the component
	 */
	public Component requireOne(String component){
		if(mComponents.containsKey(component)){
			return mComponents.get(component);
		}
		
		return addComponent(component);
	}
	
	/**
	 * Adds a new component to this entity. It should not be 
	 * called if component is already used therefore it is private.
	 * @param id Component id
	 * @return returns the added component
	 */
	private Component addComponent(String id){
		Component c = game().createComponent(id, this);
		if(c != null){
			mComponents.put(id, c);
		}
		else{
			Log.e(this.getClass().getName(), "Ce component n'existe pas : "+id);
		}
		
		return c;
	}
	
	/**
	 * Checks if this entity has a component.
	 * @param compId
	 * 		Name of the component to check
	 * @return true if this entity has the component
	 */
	public boolean has(String compId){
		return (mComponents.containsKey(compId));
	}
	
	/**
	 * Returns entity's id
	 */
	public String id(){ return mId;	}
	
	/**
	 * Internal method used to store a reference to the linked list entry in the game.
	 * It allows the entity to be removed fast from the game.
	 * @param entry
	 */
	void setLinkedListEntry(LinkedList<Entity>.Entry entry){
		mEntry = entry;
	}
	
	/**
	 * <p>
	 * Inserts this entity to the game. By default an entity 
	 * is not in the game and you have to call this method in order to add it.
	 * </p>
	 * <p>
	 * It emits an "inserted" event
	 * </p>
	 */
	public void insertToGame(){
		game().addEntity(this);
		
		postEvent("inserted");
	}
	
	/**
	 * <p>
	 * Removes the entity from the game but does not destroy it. You can still use insertToGame() after.
	 * </p>
	 * <p>
	 * Emits a "removed" event
	 * </p>
	 */
	public void removeFromGame(){
		if(mEntry != null){
			mEntry.remove();
			mEntry = null;
		}

		postEvent("removed");
	}
	
	/**
	 * Getter to access game context attached to this entity
	 * @return Game instance
	 */
	public Game game(){
		return mGame;
	}
}
