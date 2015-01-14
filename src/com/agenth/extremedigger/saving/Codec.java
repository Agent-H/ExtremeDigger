package com.agenth.extremedigger.saving;

import java.io.Serializable;

import com.agenth.engine.components.TwoDimention;
import com.agenth.engine.core.World;
import com.agenth.extremedigger.Digger;
import com.agenth.extremedigger.GameState;
import com.agenth.extremedigger.MaterialBank;

/**
 * Adapter used to write to and read from GameSavingInfos structures. This way, GameSavingInfos don't need to be modified
 * very often to avoid breaking serialization compatibility
 */
public class Codec {
	
	public static final int CURRENT_VERSION = 1;
	
	
	private GameSavingInfos1 mData;
	
	
	/**
	 * Empty constructor. Use this to create new savings
	 */
	public Codec()  { 
		mData = new GameSavingInfos1();
	}
	
	/**
	 * Constructor to read data from a savingInfo version 1
	 */
	public Codec(GameSavingInfos1 infos) {
		mData = infos;
	}
	
	
	public void saveWorld(World world) {
		for (int i = 0 ; i < World.MAP_WIDTH ; i++) {
			for (int j = 0 ; j < World.MAP_HEIGHT ; j++) {
				mData.world[i][j] = world.get(i, j);
			}
		}
	}
	
	public void restoreWorld(World world) {
		
		for (int i = 0 ; i < World.MAP_WIDTH ; i++) {
			for (int j = 0 ; j < World.MAP_HEIGHT ; j++) {
				world.set(i, j, mData.world[i][j]);
			}
		}
	}
	
	public void saveDigger(Digger digger) {
		TwoDimention twoDim = (TwoDimention)(digger.requireOne("2D"));
		
		mData.diggerX = twoDim.x();
		mData.diggerY = twoDim.y();
	}
	
	public void restoreDigger(Digger digger) {
		TwoDimention twoDim = (TwoDimention)(digger.requireOne("2D"));
		
		twoDim.moveTo(mData.diggerX, mData.diggerY);
	}
	
	public void saveGameState(GameState gs) {
		mData.money = gs.getMoney();
		mData.fuel = gs.getFuelTank().getFuel();
		mData.fuelLevel = gs.getFuelTank().level();
		mData.cargoLevel = gs.getCargo().level();
		
		int id;
		for (int i = 0 ; i < MaterialBank.NB_MATERIALS - 1 ; ++i) {
			id = MaterialBank.idToMaterial(i);
			mData.materialCount[i] = gs.getCargo().getMineralCount(id);
		}
	}
	
	public void restoreGameState(GameState gs) {
		gs.setMoney(mData.money);
		gs.getFuelTank().upgrade(mData.fuelLevel);
		gs.getFuelTank().setFuel(mData.fuel);
		gs.getCargo().upgrade(mData.cargoLevel);
		
		int id;
		for (int i = 0 ; i < MaterialBank.NB_MATERIALS - 1 ; ++i) {
			id = MaterialBank.idToMaterial(i);
			gs.getCargo().setMineralCount(id, mData.materialCount[i]);
		}
	}
	
	
	public Serializable encode() {
		return mData;
	}
	
	public static Codec decode(Serializable ser, int version) {
		switch(version) {
			case 1:
				return new Codec((GameSavingInfos1) ser);
			default:
				throw new IllegalArgumentException("Invalid version number");
		}
	}
}
