package com.breakingsoft.extremedigger;

import com.breakingsoft.engine.core.Game;
import com.breakingsoft.engine.core.GameModule;

public class Gameplay extends GameModule{
	
	private MoneyView mMoneyView;
	private FuelView mFuelView;
	private DiggerWorld mWorld;
	private GameState gs;
	
	
	public Gameplay(Game game, DiggerWorld world) {
		super(game, "Gameplay");
		
		mWorld = world;
		
		gs = (GameState) game().getGameState();
	}

	@Override
	public void step(long time) {
		
		if(!game().isPaused()){
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

	/**
	 * Methode appell�e quand le joueur perd pour une raison quelconque
	 */
	public void gameOver(){
		((Digger)game().getEntity("digger").requireOne("Digger")).init();
		
		mWorld.generate();
		gs.reset();
	}
}
