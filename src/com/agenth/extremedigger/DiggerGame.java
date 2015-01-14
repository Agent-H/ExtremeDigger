package com.agenth.extremedigger;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.agenth.engine.core.Entity;
import com.agenth.engine.core.Game2D;
import com.agenth.engine.core.GameDescriptor;
import com.agenth.engine.core.World;
import com.agenth.engine.physics.PhysicEngine;
import com.agenth.engine.util.VectF;
import com.agenth.extremedigger.saving.Codec;
import com.agenth.extremedigger.R;

public class DiggerGame extends Game2D{

	private TextView mPauseView;
	private GameState mGS;
	private DiggerWorld mWorld;
	private Digger mDigger;
	
	public DiggerGame(final FragmentActivity act) {
		super(act, (SurfaceView) act.findViewById(R.id.fullscreen_content), 680);
		
		act.findViewById(R.id.fullscreen_content).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				resume();
			}
			
		});
		
		MaterialBank.init(act);
		
		mGS = new GameState();
		mWorld = new DiggerWorld(this, act, null);

		
		mWorld.insertToGame();
		
		Gameplay gameplay = new Gameplay(this, mWorld);
		addModule(gameplay);
		
		//Init views
		gameplay.setMoneyView((MoneyView)act.findViewById(R.id.moneyLabel));
		gameplay.setFuelView((FuelView) act.findViewById(R.id.fuelView));

		mDigger = (Digger)new Entity(this, "digger").requireOne("Digger");
		mDigger.init(act, mWorld);
		mDigger.owner().insertToGame();
		
		((PhysicEngine) getModule("Physic")).setGravity(new VectF(0f, 1.3f));
		
		mPauseView = (TextView) act.findViewById(R.id.pauseText);
		
		
		//Dialog showing collected minerals
		final CargoDialog cargoDialog = new CargoDialog();
	
		cargoDialog.setCargo(mGS.getCargo());
		cargoDialog.setGame(this);
		
		//Shows cargo dialog on click on cargo button
		((Button) act.findViewById(R.id.cargoButton)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				cargoDialog.show(act.getSupportFragmentManager(), "Cargo");
			}
		});
		
		
		// --- Génération des batiments
		final MineralDialog mineralDialog = new MineralDialog();
		mineralDialog.setGame(this);
		mineralDialog.setGameState(mGS);
		
		final FuelDialog fuelDialog = new FuelDialog();
		fuelDialog.setGameState(mGS);
		fuelDialog.setGame(this);
		
		UpgradesDialog upgradesDialog = new UpgradesDialog();
		upgradesDialog.setGame(this);
		upgradesDialog.setGameState(mGS);
		
		Building b_mineral = (Building)(new Entity(this)).requireOne("Building");
		b_mineral.setDrawable(act.getResources().getDrawable(R.drawable.building_mineral));
		b_mineral.setRect(1000, World.TILE_SIZE-200, 300, 200);
		b_mineral.setDialogFragment(mineralDialog, act.getSupportFragmentManager());
		b_mineral.owner().insertToGame();
		
		Building b_fuel = (Building)(new Entity(this)).requireOne("Building");
		b_fuel.setDrawable(act.getResources().getDrawable(R.drawable.building_fuel));
		b_fuel.setRect(50, World.TILE_SIZE-200, 300, 200);
		b_fuel.setDialogFragment(fuelDialog, act.getSupportFragmentManager());
		b_fuel.owner().insertToGame();
		
		Building b_upgrades = (Building)(new Entity(this)).requireOne("Building");
		b_upgrades.setDrawable(act.getResources().getDrawable(R.drawable.building_upgrades));
		b_upgrades.setRect(1500, World.TILE_SIZE-200, 300, 200);
		b_upgrades.setDialogFragment(upgradesDialog, act.getSupportFragmentManager());
		b_upgrades.owner().insertToGame();
	}
	
	@Override
	public void registerComponents(){
		super.registerComponents();
		registerComponent("Digger", Digger.class);
		registerComponent("Building", Building.class);
	}
	
	@Override
	public GameState getGameState(){
		return mGS;
	}
	
	@Override
	public void pause(){
		super.pause();
		
		mPauseView.post(new Runnable(){
			@Override
			public void run() {
				mPauseView.setVisibility(View.VISIBLE);
			}
		});
	}
	
	@Override
	public void resume(){
		super.resume();
		
		mPauseView.post(new Runnable(){
			@Override
			public void run() {
				mPauseView.setVisibility(View.GONE);
			}
		});
	}
	
	
	@Override
	protected GameDescriptor _save() {
		
		mDigger.abortDigging();
		
		Codec codec = new Codec();
		
		codec.saveGameState(mGS);
		codec.saveWorld(mWorld);
		codec.saveDigger(mDigger);
		
		GameDescriptor desc = new GameDescriptor(codec.encode(), Codec.CURRENT_VERSION);
		
		return desc;
	}

	@Override
	protected void _load(GameDescriptor desc) {
		Log.v("save", "restoring : "+desc.getDataVersion());
		
		Codec codec = Codec.decode(desc.getData(), desc.getDataVersion());
		
		codec.restoreGameState(mGS);
		codec.restoreWorld(mWorld);
		codec.restoreDigger(mDigger);
	}
	
}
