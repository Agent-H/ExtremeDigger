package com.breakingsoft.engine.core;

/**
 * This class defines a component.
 * 
 * <p>Components are reusable pieces of code attached to an entity. <br/>
 * Each component should add one specific characteristic to an entity.
 * </p>
 * <p>
 * Never directly call component's constructor. Instead, use entity.require() or entity.requireOne()
 * require() allows you to require multiple components, whereas require only accepts one component.
 * </p>
 * <pre>
 * example : 
 *  TwoDimention my2D = (TwoDimention) entity.require("2D Graphics")[0];
 *  //Equivalent :
 *  TwoDimention my2D = (TwoDimention) entity.require("2D");
 *  entity.require("Graphic");
 * </pre>
 * <p>
 * At some point, you will have to create your own components to specify general behaviors in your game.
 * <ul>
 * <li>Your component might require other components. Use require() and requireOne(). Calling require or requireOne()
 * in a Component is equal to calling owner().require() or owner().requireOne().</li>
 * 
 * <li>The owner() method allows you to access the entity owning your component.</li>
 * 
 * <li>Each entity can have only one instance of one type of component. Multiple calls to require() will always return the same
 * instance of a specific component. Moreover, two entities cannot share one component's instance.</li>
 * 
 * <li>In order to make your component usable in the game, you should call Game.registerComponent(). See Game for more details.</li>
 * </ul>
 * </p>
 * <p>
 * Methods that does not have to return anything should return this in order to allow chained calls.
 * </p>
 *
 */
public abstract class Component{
	
	private Game mGame;
	private Entity mEntity;
	
	/**
	 * Default constructor. You may redefine it with the same parameters and not add any other constructors.
	 * Moreover, never construct components by yourself, let the game do it when necessary.
	 * 
	 * @param game
	 * 		the game in which the component is created
	 * @param entity
	 * 		entity that owns the component
	 */
	public Component(Game game, Entity entity){
		mGame = game;
		mEntity = entity;
	}
	
	/**
	 * Returns entity owning this component
	 * @return entity
	 */
	public Entity owner(){
		return mEntity;
	}

	/**
	 * Requires multiple components.
	 * @param components
	 * 		list of components formatted as "Component1 Component2 ..."
	 * @return an array of components instances ordered as in the parameter. If a component couldn't be found, 
	 * a null case stands at it's place and indexes are preserved.
	 */
	public Component[] require(String components){
		return mEntity.require(components);
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
		return mEntity.requireOne(component);
	}
	
	/**
	 * Called when component is activated with game.addActiveComponent
	 * @param mLastStepTime time of last iteration in milliseconds
	 */
	public void step(long mLastStepTime){
		
	}
	
	/**
	 * Gets the game context in which this component was created.
	 * @return game instance.
	 */
	public Game game(){
		return mGame;
	}
}
