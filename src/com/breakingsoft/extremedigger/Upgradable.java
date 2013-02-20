package com.breakingsoft.extremedigger;

public class Upgradable {

	private int mLevel;
	private int mMaxLevel;
	private int[] mPrices;
	
	public Upgradable(int maxLevel, int[] prices){
		mMaxLevel = maxLevel;
		mPrices = prices;
	}
	
	public int level(){
		return mLevel;
	}
	
	public boolean upgrade(){
		return upgrade(mLevel+1);
	}
	
	public boolean upgrade(int level){
		if(level < mLevel || level > mMaxLevel)
			return false;
		
		mLevel = level;
		return true;
	}
	
	public int getMaxLevel(){
		return mMaxLevel;
	}
	
	public int getUpgradePrice(){
		if(mLevel < mMaxLevel)
			return mPrices[mLevel];
		
		return 0;
	}
	
	public boolean upgradable(){
		return mLevel < mMaxLevel;
	}
	
}
