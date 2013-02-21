package com.breakingsoft.extremedigger;


public class Cargo extends Upgradable{
	
	//Cargo upgrades
	public static final int[] CAPACITY = {15, 30, 50};
	
	private int mContentSize;
	
	private int[] mCargo;
	
	public Cargo(){
		super(2, new int[]{1000, 2000, 5000}, CAPACITY);
		
		mCargo = new int[MaterialBank.NB_MATERIALS];
	}
	
	
	/**
	 * Tries to add a mineral to the cargo.
	 * @param mineral
	 * @return true if addition was successful, false otherwise
	 */
	public boolean put(int mineral){
		int id = MaterialBank.materialToId(mineral);
		if(mContentSize < CAPACITY[level()] && id != -1){
			mCargo[id] ++;
			mContentSize ++;
			return true;
		}
		
		return false;
	}
	
	/**
	 * Returns the number of minerals of the specified type in the cargo
	 * @param type
	 * @return number of minerals of that type
	 */
	public int getMineralCount(int type){
		
		int id = MaterialBank.materialToId(type);
		
		if(id > 0 && id < MaterialBank.NB_MATERIALS)
			return mCargo[id];
		
		return 0;
	}
	
	/**
	 * Removes every minerals of the specified type from the cargo
	 * @param type
	 */
	public void clearMineral(int type){
		mContentSize -= mCargo[type];
		mCargo[type] = 0;
	}

	/**
	 * Clears every minerals. Cargo is empty after this call.
	 */
	public void clear(){
		mContentSize = 0;
		for(int i = 0 ; i < mCargo.length ; i++){
			mCargo[i] = 0;
		}
	}
	
	/**
	 * Returns the load of the cargo in percent
	 */
	public int getLoadPercent(){
		return 100*mContentSize/CAPACITY[level()];
	}
	
	/**
	 * Returns the price of all contents
	 */
	public int getTotalPrice(){
		int price = 0;
		int material;
		for(int i = 1 ; i < MaterialBank.NB_MATERIALS ; i++){
			material = MaterialBank.idToMaterial(i);
			price += MaterialBank.getMaterialPrice(material)*getMineralCount(material);
		}
		
		return price;
	}
}
