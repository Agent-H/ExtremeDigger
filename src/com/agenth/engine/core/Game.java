package com.agenth.engine.core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import android.content.Context;
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
	public static final int STEP_INTERVAL = 30;
	
	/**
	 * Prefix prepened to saved files to avoid collisions
	 */
	public static final String FILE_PREFIX = "SAVE_";
	
	private GameThread mThread;
	
	private ArrayList<GameModule> mModules = new ArrayList<GameModule>();
	
	private LinkedList<Entity> mEntities = new LinkedList<Entity>();
	
	private LinkedList<Component> mActiveComponents = new LinkedList<Component>();
	
	private HashMap<String, Class<Component>> mComponents = new HashMap<String, Class<Component>>();
	
	private boolean mRunning = false;
	
	private boolean mPaused = true;
	
	private FragmentActivity mAct;
	
	/** Used to persist game state to phone's storage. */
	private String mName;
	
	
	/**
	 * Default constructor. Calls registerComponents().<br />
	 * note : it is recomended that you call super.registerComponents() within 
	 * registerComponents() if you are implementing this function since the default 
	 * Game implementation may add components too.
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
			Log.v("com.agenth.components", "Starting game");
			mRunning = true;
			
			postEvent("started", this);
			
			if(mThread == null)
				mThread = new GameThread();
			mThread.start();
		}
		else{
			Log.v("com.agenth.components", "Starting game : game is already running.");
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
			Log.v("com.agenth.components", "Game pause requested...");
			
			//Pauses game
			pause();
			
			//Asks the thread to stop
			mRunning = false;
			
			//And waits for it to stop
			boolean keepWaiting = true;
			while(keepWaiting) {
				try {
					// Stopps the thread
					mThread.join();
					// Need to throw away the thread since it is one-time use.
					mThread = null;
					keepWaiting = false;
				} catch (InterruptedException e) {
					// Try again
					keepWaiting = true;
				}
			}
			
			postEvent("stopped", this);
		}
		else
			Log.v("com.agenth.components", "Pausing game : game already stopped.");
		
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
	
	
	/* Persistence */
	
	/**
	 * Save this game as name
	 * @param name name to save this game as
	 * @throws FileNotFoundException 
	 * @see {@link save}
	 */
	public final void saveAs(String name) throws IOException {
		
		FileOutputStream os = mAct.openFileOutput(FILE_PREFIX+name, Context.MODE_PRIVATE);
		
		GameDescriptor desc = this._save();
		
		int version = desc.getDataVersion();
		os.write(version >> 8);
		os.write(version);
		
		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(desc.getData());
		oos.close();
	}
	
	/**
	 * Persists the current game state to storage. The current game name is used if any, otherwise a datetime string is used instead.
	 * If a game with that name already exists, it is overwritten.
	 * 
	 * @return the actual name used for saving.
	 * @throws IOException 
	 */
	public final String save() throws IOException {
		String filename = mName;
		
		if (filename == null) {
			filename = (new Date()).toString();
		}
		
		saveAs(filename);
		
		return filename;
	}
	
	/**
	 * Restores the game from memory. Implement _load to actually do this.
	 * @param name
	 * @throws FileNotFoundException 
	 */
	public final void load(String name) throws IOException, ClassNotFoundException {
		FileInputStream is = mAct.openFileInput(FILE_PREFIX+name);
		
		int version = (is.read() << 8) | is.read();
		
		ObjectInputStream ois = new ObjectInputStream(is);
		Serializable ser = (Serializable) ois.readObject();
		ois.close();
		
		this._load(new GameDescriptor(name, ser, version));
	}
	
	/**
	 * Returns a game descriptor to be saved. The game descriptor must not contain a "name" property as it is already specified by
	 * the name of the game (see saveAs(String name))
	 */
	protected abstract GameDescriptor _save();
	
	/**
	 * Implement this method to restore the game state from a game descriptor.
	 */
	protected abstract void _load(GameDescriptor desc);
	
	
	/**
	 * Gets the list of saved games
	 * @return an array of GameDescriptors
	 */
	public static final GameDescriptor[] getGameList() {
		// TODO implement this method
		return null;
	}
	
	public final String getName() {
		return mName;
	}
	
	
	/**
	 * Internal thread in which game loop runs
	 */
	private class GameThread extends Thread
	{
		
		private int mLastStepTime;
		private long mPrevTime;
		
		@Override
	    public void run() {
			Log.v("com.agenth.components", "Game thread started.");
			
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
			
			Log.v("com.agenth.components", "Game thread aborted.");
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
			mLastStepTime = (int)(System.nanoTime() - mPrevTime)/1000000;
		}
	}
}
