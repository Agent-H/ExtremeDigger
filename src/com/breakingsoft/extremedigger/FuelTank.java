package com.breakingsoft.extremedigger;

public class FuelTank extends Upgradable{

	public static final int FUEL_LEVELS[] = {100, 200, 400, 700, 1200};
	public static final int FUEL_PRICES[] = {750, 1500, 5000, 10000};
	public static final int FUEL_COEFF = 100;

	private int mFuel = FUEL_LEVELS[0]*FUEL_COEFF;
	
	public FuelTank(){
		super(4, FUEL_PRICES, FUEL_LEVELS);
	}
	
	public void refill(int val){
		
		mFuel = Math.min(mFuel + val, FUEL_LEVELS[level()]*FUEL_COEFF);
	}
	
	public void decreaseFuel(int val){
		mFuel -= val;
	}
	
	public int getFuel(){
		return mFuel;
	}
	
	public int getMaxFuel(){
		return FUEL_LEVELS[level()]*FUEL_COEFF;
	}
	
	public boolean upgrade(int level){
		boolean ret = super.upgrade(level);
		
		mFuel = FUEL_LEVELS[level()]*FUEL_COEFF;
		
		return ret;
	}
}
