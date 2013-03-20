package com.agenth.engine.core;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.agenth.engine.util.LinkedList;

/**
 * This class represents the whole context of a game. It enables coordination between 
 * entities, modules and components and handles the game loop. 
 * 
 * To control the game state, use start(), stop(), resume(), pause().<br />
 * When creating custom components, you will need to use registerComponent() in order to make it available to entities.<br />
 * Modules can be added with addModule().
 * 
 * Otherwise, the game is passed as a parameter when creating a new entity.
 * 
 * The game cycle is typically this one :
 * start
 * resume
 * * game is running *
 * pause
 * * game is still running but on pause. Modules should handle this case in order to freeze 
 * game state though they are still refreshed on every game iteration. *
 * resume
 * * game is running *
 * pause
 * stop
 * 
 * Game cannot be stopped without being paused and cannot be resumed without being started.
 * 
 * When creating a game, one should subclass Game, implement registerComponents() in order to register 
 * custom components on game start and create stuff to make the gameplay.
 *
 */
public abstract class Game extends EventEmitter{
	
	/**
	 * Duration of one step in milliseconds
	 */
	public static final long STEP_INTERVAL = 30;
	
	private GameThread mThread;
	
	private ArrayList<GameModule> mModules = new ArrayList<GameModule>();
	
	private LinkedList<Entity> mEntities = new LinkedList<Entity>();
	
	private LinkedList<Component> mActiveComponents = new LinkedList<Component>();
	
	private HashMap<String, Class<Component>> mComponents = new HashMap<String, Class<Component>>();
	
	private boolean mRunning = false;
	
	private boolean mPaused = true;
	
	private FragmentActivity mAct;
	
	
	/**
	 * Default constructor. Calls registerComponents().<br />
	 * note : it is recomended that you call super.registerComponents() within 
	 * registerComponents() if you are shadowing this function since the default 
	 * Game implementation may add components for it to work well.
	 */
	public Game(FragmentActivity act){
		mAct = act;
		registerComponents();
	}
	
	/**
	 * Adds a module to the game. Modules are refreshed in order of addition so that first added module will refresh first.
	 * There should only be one module of one type also number of modules should be limited.
	 * Usually modules are physic, graphic and AI.
	 * @param module
	 * 		Game module to add.
	 */
	public void addModule(GameModule module){
		mModules.add(module);
	}
	
	/**
	 * Returns a module with it's id if it is used by the game.
	 * @param id
	 * @return
	 */
	public GameModule getModule(String id){
		Iterator<GameModule> it = mModules.iterator();
		while (it.hasNext()) {
			GameModule mod = it.next();
			  
			if(mod.getName().equals(id)){
				return mod;
			}
		}
		
		return null;
	}
	
	/**
	 * Starts the game after it was stopped
	 */
	public void start()
	{
		if(!mRunning){
			Log.v("com.breakingsoft.components", "Starting game");
			mRunning = true;
			
			postEvent("started", this);
			
			if(mThread == null)
				mThread = new GameThread();
			mThread.start();
		}
		else{
			Log.v("com.breakingsoft.components", "Starting game : game is already running.");
		}
	}
	
	/**
	 * Checks if the game is paused
	 * @return true if game is paused
	 */
	public boolean isPaused(){
		return mPaused;
	}
	
	/**
	 * Resumes the game
	 */
	public void resume()
	{
		if(!mRunning)
			start();
		postEvent("resumed", this);
		mPaused = false;
	}
	
	/**
	 * Pauses the game
	 */
	public void pause(){
		postEvent("paused", this);
		mPaused = true;
	}
	
	abstract public GameState getGameState();
	
	/**
	 * Stops the game
	 */
	public void stop()
	{
		if(mRunning){
			Log.v("com.breakingsoft.components", "Game pause requested...");
			
			//Pauses game
			pause();
			
			//Asks the thread to stop
			mRunning = false;
			
			//And waits for it to stop
			boolean keepWaiting = true;
			while(keepWaiting) {
				try {
					// Arr�te le thread
					mThread.join();
					//Il faut jeter le thread car les threads sont � usage unique.
					mThread = null;
					keepWaiting = false;
					// Comme on veut s'assurer qu'il s'arr�te, on fait une boucle infinie
				} catch (InterruptedException e) {
					// Essaie encore 
					keepWaiting = true;
				}
			}
			
			postEvent("stopped", this);
		}
		else
			Log.v("com.breakingsoft.components", "Pausing game : game already stopped.");
		
	}
	
	/**
	 * Redefine this function if you need to register custom components.
	 * it will be called by the constructor
	 */
	protected void registerComponents(){
		
	}
	
	public FragmentActivity getActivity(){
		return mAct;
	}
	
	
	/**
	 * Registers a component to the game so that it can be required with Entity.require().
	 * If a component is already registered with this key, the new one will replace it
	 * @param key
	 * 		Name of the component as used in require().
	 * @param c
	 * 		Class object of the component. Use YourComponentClass.class int this field. 
	 * 		Of course, YoutComponentClass should subclass Component !
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public final void registerComponent(String key, Class c){

		//On ajoute la component quoi qu'il arrive pour autoriser le masquage
		mComponents.put(key, c);
	}
	
	/**
	 * Called by an entity when it needs to create a new component instance following a require() call.
	 * Never call this function yourself, always use require().
	 * @param compID
	 * 		Name of the component as stated in registerComponent()
	 * @param entity
	 * 		Entity creating the component. The created component and this entity will be bonded for the whole entity lifecycle.
	 * @return The created instance of component
	 */
	Component createComponent(String compID, Entity entity){
		Class<Component> comp = mComponents.get(compID);
		
		if(comp == null){
			Log.e(this.getClass().getName(), "Ce component n'existe pas !");
			return null;
		}
		try {
			Constructor<Component> ctr = comp.getConstructor(new Class[]{Game.class, Entity.class});
			return ctr.newInstance(this, entity);
			
		} catch (Exception e) {
			Log.e(this.getClass().getName(), "Impossible d'instancier le component : "+compID);
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Adds an entity to the game. This is use internally. 
	 * Use entity.addToGame() when you want to insert an entity to the game.
	 * @param entity
	 * 		Entity to add.
	 */
	void addEntity(Entity entity){
		if(entity.id() != null){
			entity.setLinkedListEntry(mEntities.insert(entity));
		}
	}
	
	/**
	 * Gets an entity from the game using it's id.
	 * @param id
	 * 		Id of entity to get
	 * @return Last inserted entity with specified id or null if it was not found
	 */
	public Entity getEntity(String id){
		
		LinkedList<Entity>.Entry e = mEntities.first();
		
		while(e != null){
			Entity ent = e.data();
			if(ent.id().equals(id))
				return ent;
			
			e = e.next();
		}
		return null;
	}

	/**
	 * Adds this component to the list of components to refresh on every loop iteration.
	 * This will result in a call to the step() method of the component
	 * @param component component to add
	 * @return
	 */
	public LinkedList<Component>.Entry addActiveComponent(Component component){
		return mActiveComponents.insert(component);
	}
	
	/**
	 * Removes the component from the list of components to refresh
	 */
	public void removeActiveComponent(LinkedList<Component>.Entry entry){
		entry.remove();
	}
	
	
	/**
	 * Internal thread in which game loop runs
	 */
	private class GameThread extends Thread
	{
		
		private long mLastStepTime;
		private long mPrevTime;
		
		@Override
	    public void run() {
			Log.v("com.breakingsoft.components", "Game thread started.");
			
			mLastStepTime = STEP_INTERVAL;
			
			while(mRunning){
				startChrono();
				
				//Iterates over modules and calls step() on each
				Iterator<GameModule> it = mModules.iterator();
				while (it.hasNext()) {
			      GameModule mod = it.next();
			      mod.step(mLastStepTime);
			    }
				
				LinkedList<Component>.Entry component = mActiveComponents.first();
				while(component != null){
					component.data().step(mLastStepTime);
					component = component.next();
				}
				
				
				stopChronoAndSleep();
			}
			
			Log.v("com.breakingsoft.components", "Game thread aborted.");
		}
		
		/**
		 * Saves current time at beginning of step
		 */
		private void startChrono(){
			mPrevTime = System.nanoTime();
		}
		
		/**
		 * Measure step active time and sleeps to match STEP_INTERVAL. Total step time (active + sleep) is saved.
		 */
		private void stopChronoAndSleep(){
			long nextTime = System.nanoTime();
			
			//If step was too short
			if(nextTime - mPrevTime < STEP_INTERVAL*1000000){
				//Tries to sleep for the rest of current step
				try {
					long sleeptime = (STEP_INTERVAL*1000000 - nextTime+mPrevTime)/1000000;
					Thread.sleep(sleeptime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			//After sleeping, measures total step time
			mLastStepTime = System.nanoTime() - mPrevTime;
		}
	}
}
