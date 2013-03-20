package com.agenth.engine.core;


public class World extends Entity{
	
	public int mWidth;
	public int mHeight;	
	
	public static final int MAP_WIDTH = 48;
	public static final int MAP_HEIGHT = 256;
	
	public static final int TILE_SIZE = 120;
	
	private int[][] map = new int[MAP_WIDTH][MAP_HEIGHT];
	
	public World(Game game){
		super(game);
		
		mWidth = TILE_SIZE * MAP_WIDTH;
		mHeight = TILE_SIZE * MAP_HEIGHT;
		
		require("WorldPhysic WorldGraphic");
	}
	
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
}
