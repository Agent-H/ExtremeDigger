package com.breakingsoft.extremedigger;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.breakingsoft.engine.core.Game;
import com.breakingsoft.engine.core.GameModule;

public class Gameplay extends GameModule{
	
	private MoneyView mMoneyView;
	private FuelView mFuelView;
	private DiggerWorld mWorld;
	private GameState gs;
	private FragmentActivity act;
	PausingDialog mGameOverDialog;
	
	
	public Gameplay(Game game, DiggerWorld world) {
		super(game, "Gameplay");
		
		mWorld = world;
		
		act = game.getActivity();
		gs = (GameState) game().getGameState();
		
		mGameOverDialog = new PausingDialog(){
			@Override
			public Dialog onCreateDialog(Bundle savedInstanceState) {
				super.onCreateDialog(savedInstanceState);
				
		   	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		   	    
		   	    View view = act.getLayoutInflater().inflate(R.layout.game_over_dialog, null);
		   	 
		   	    return builder.setView(view)
				.setPositiveButton("Restart", new OnClickListener(){

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						((Digger)game().getEntity("digger").requireOne("Digger")).init();
						
						mWorld.generate();
						gs.reset();
					}
					
				}).create();
		   	    
			}
		};
		mGameOverDialog.setGame(game);
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
	 * Methode appellée quand le joueur perd pour une raison quelconque
	 */
	public void gameOver(){

		
				
		mGameOverDialog.show(act.getSupportFragmentManager(), "Game over");
		
	}
}
