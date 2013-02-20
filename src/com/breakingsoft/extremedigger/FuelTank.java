package com.breakingsoft.extremedigger;

public class FuelTank extends Upgradable{

	public static final int FUEL_LEVELS[] = {1000, 2000, 5000, 10000, 20000};

	private int mFuel = FUEL_LEVELS[0];
	
	public FuelTank(){
		super(4, new int[]{750, 1500, 5000, 10000});
	}
	
	public void refill(int val){
		
		mFuel = Math.min(mFuel + val, FUEL_LEVELS[level()]);
	}
	
	public void decreaseFuel(int val){
		mFuel --;
	}
	
	public int getFuel(){
		return mFuel;
	}
	
	public int getCapacity(){
		return FUEL_LEVELS[level()];
	}
	
	public int getUpgradeCapacity(){
		if(upgradable())
			return FUEL_LEVELS[level()+1];
		return 0;
	}
	
	public boolean upgrade(int level){
		boolean ret = super.upgrade(level);
		
		mFuel = getCapacity();
		
		return ret;
	}
}
