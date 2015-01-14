package com.agenth.extremedigger.saving;

import java.io.Serializable;

import com.agenth.engine.core.World;

/**
 * A simple structure that stores game informations in plain format so that it can read from/written to files.
 */
public class GameSavingInfos1 implements Serializable {
	
	public static final long serialVersionUID = 1;
	
	public int money;
	public int fuel;
	public int fuelLevel;
	public int cargoLevel;
	
	public float diggerX, diggerY;
	
	/**
	 * 0: lead,
	 * 1: copper,
	 * 2: alu,
	 * 3: gold,
	 * 4: ruby,
	 * 5: saphir,
	 * 6: uranium,
	 * 7: amethyst
	 */
	public int[] materialCount = new int[8];
	
	public int[][] world = new int[World.MAP_WIDTH][World.MAP_HEIGHT];
}
