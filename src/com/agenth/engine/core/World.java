package com.agenth.engine.core;

import android.os.Bundle;
import android.util.Log;


public abstract class World extends Entity{
	
	private int mWidth;
	private int mHeight;	
	
	public static final int MAP_WIDTH = 30;
	public static final int MAP_HEIGHT = 300;
	
	public static final int TILE_SIZE = 120;
	
	private int[][] map = new int[MAP_WIDTH][MAP_HEIGHT];
	
	public World(Game game, Bundle savedInstanceState){
		super(game);
		
		mWidth = TILE_SIZE * MAP_WIDTH;
		mHeight = TILE_SIZE * MAP_HEIGHT;
		
		require("WorldPhysic WorldGraphic");
		
		
		if (savedInstanceState != null) {
			Log.v("com.agenth.extremedigger", "restore map");
			for (int i = 0 ; i < MAP_WIDTH ; ++i) {
				for (int j = 0 ; j < MAP_HEIGHT ; ++j) {
					map[i][j] = savedInstanceState.getInt("world.map"+i+"x"+j);
				}
			}
			Log.v("com.agenth.extremedigger", "map restored");
		} else {
			generate();
		}
	}
	
	public abstract void generate();
	
	public int getWidth(){
		return mWidth;
	}
	
	public int getHeight(){
		return mHeight;
	}
	
	public int get(int i, int j){
		return map[i][j];
	}
	
	public void set(int i, int j, int tile){
		map[i][j] = tile;
	}
	
	public static boolean isValid(int x, int y){
		return x >= 0 && x < MAP_WIDTH && y >= 0 && y < MAP_HEIGHT;
	}
	
	public void saveState(Bundle outState) {
		for (int i = 0 ; i < MAP_WIDTH ; ++i) {
			for (int j = 0 ; j < MAP_HEIGHT ; ++j) {
				outState.putInt("world.map"+i+"x"+j, map[i][j]);
			}
		}
	}
}
