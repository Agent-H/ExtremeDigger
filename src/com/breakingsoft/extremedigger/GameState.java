package com.breakingsoft.extremedigger;


public class GameState {

	public static final int START_MONEY = 100;
	
	private int mMoney;
	private FuelTank mFuel;
	
	public boolean moneyChanged;
	public boolean fuelChanged;
	
	public GameState(){
		mFuel = new FuelTank();
		
		reset();
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
	
	public void decreaseFuel(long time){
		mFuel.decreaseFuel((int) (time/100000));
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
