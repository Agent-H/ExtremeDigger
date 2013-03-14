package com.breakingsoft.extremedigger;


public class GameState extends com.breakingsoft.engine.core.GameState{

	public static final int START_MONEY = 100;
	
	private int mMoney;
	private FuelTank mFuel;
	private Cargo mCargo;
	
	public boolean moneyChanged;
	public boolean fuelChanged;
	
	public GameState(){
		mFuel = new FuelTank();
		mCargo = new Cargo();
		
		reset();
	}
	
	public Cargo getCargo(){
		return mCargo;
	}
	
	public void addMoney(int money){
		mMoney += money;
		moneyChanged = true;
	}
	
	public void subMoney(int money){
		mMoney -= money;
		moneyChanged = true;
	}
	
	public int getMoney(){
		return mMoney;
	}
	
	public FuelTank getFuelTank(){
		return mFuel;
	}
	
	public void decreaseFuel(int quty){
		mFuel.decreaseFuel(quty);
		fuelChanged = true;
	}
	
	public boolean fuelEmpty(){
		return mFuel.getFuel() <= 0;
	}
	
	public void reset(){
		mFuel.upgrade(0);
		mMoney = START_MONEY;
	}
	
	public void fillTank(int val){
		mFuel.refill(val);
		fuelChanged = true;
	}
	
}
