package com.breakingsoft.extremedigger;

import com.breakingsoft.engine.core.Game;
import com.breakingsoft.engine.core.GameModule;

public class Gameplay extends GameModule{

	private GameState gs;
	
	private MoneyView mMoneyView;
	private FuelView mFuelView;
	private DiggerWorld mWorld;
	
	
	public Gameplay(Game game, DiggerWorld world) {
		super(game, "Gameplay");
		
		mWorld = world;
		gs = new GameState();
	}

	@Override
	public void step(long time) {
		
		if(!game().isPaused()){
			gs.decreaseFuel(time);
			
			if(gs.fuelEmpty()){
				gameOver();
			}
		}
		if(gs.moneyChanged){
			mMoneyView.postInvalidate();
			gs.moneyChanged = false;
		}
		if(gs.fuelChanged){
			mFuelView.postInvalidate();
			gs.fuelChanged = false;
		}
		
	}
	
	public void setFuelView(FuelView view){
		mFuelView = view;
		view.setFuelTank(gs.getFuelTank());
	}
	
	public void setMoneyView(MoneyView view){
		mMoneyView = view;
		view.setGameState(gs);
	}
	
	public GameState getGameState(){
		return gs;
	}

	/**
	 * Methode appellée quand le joueur perd pour une raison quelconque
	 */
	public void gameOver(){
		((Digger)game().getEntity("digger").requireOne("Digger")).init();
		
		mWorld.generate();
		gs.reset();
	}
}
