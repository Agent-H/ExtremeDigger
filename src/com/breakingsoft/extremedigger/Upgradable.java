package com.breakingsoft.extremedigger;

public class Upgradable {

	private int mLevel;
	private int mMaxLevel;
	private int[] mPrices;
	private int[] mValues;
	
	public Upgradable(int maxLevel, int[] prices, int[] values){
		mMaxLevel = maxLevel;
		mPrices = prices;
		mValues = values;
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
	
	public int getValue(){
		return mValues[mLevel];
	}
	
	public int getUpgradeValue(){
		if(mLevel < mMaxLevel)
			return mValues[mLevel+1];
		
		return 0;
	}
	
}
