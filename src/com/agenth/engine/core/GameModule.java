package com.agenth.engine.core;

/**
 * A game module implements a function of the game that needs to be refreshed on evry game loop iteration.
 * When possible, use an event-based system for your gameplay implementation. Create a module only if necessary. 
 * Default modules should be sufficient.
 * 
 * Adding new modules degrades performances but keep in mind that if you need to refresh something very often,
 * the module approach should be faster than the event one.
 *
 */
public abstract class GameModule extends EventEmitter{
	
	private String mName;
	
	private Game mGame;
	
	/**
	 * Creates a game module within game with the name name.
	 * Note that it doesn't adds the module to the game allowing you to stack modules the way you want.
	 * Use Game.addModule() for that purpose.
	 * 
	 * 
	 * @param game
	 * 		Game context in which the module was created
	 * @param name
	 * 		Name of this module. This name should be used when looking for this module in Game.getModule()
	 */
	public GameModule(Game game, String name)
	{
		mGame = game;
		mName = name;
	}
	
	/**
	 * Method called on every game loop iteration.
	 * Note that game loop is active when game is paused therefore you should
	 * check for game state with game().isPaused(); in order to keep consistent.
	 * 
	 * One steps normally lasts Game.STEP_INTERVAL milliseconds but it cannot be guaranteed 
	 * so the time parameter gives the exact time last step lasted.
	 * @param time
	 * 		Duration of the last step in milliseconds.
	 */
	public abstract void step(long time);
	
	/**
	 * Gets module name as given in the constructor
	 * @return module name
	 */
	public String getName(){
		return mName;
	}
	
	/**
	 * Gets the game context in which this module was created
	 * @return a Game instance
	 */
	public Game game(){
		return mGame;
	}
}
