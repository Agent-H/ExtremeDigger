package com.agenth.extremedigger;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.SparseArray;

import com.agenth.engine.core.Game;
import com.agenth.engine.core.World;
import com.agenth.engine.graphics.WorldGraphic;
import com.agenth.engine.physics.WorldPhysic;

public class DiggerWorld extends World {
	
	
	public DiggerWorld(Game game, Context ctx, Bundle savedInstanceState) {
		super(game, savedInstanceState);
		
		//	---	Initialisation des paramètres	---	

		
		// Generating sprites map	
		WorldGraphic graphic = (WorldGraphic) requireOne("WorldGraphic");
		graphic.setTopBound(-10000);
		graphic.setBackgroundVerticalGradient(0xff330099, Color.BLACK);
		
		WorldPhysic physic = (WorldPhysic) requireOne("WorldPhysic");
		physic.setCollisionTop(false);
	}
	
	/**
	 * Generates a new map
	 */
	public void generate(){
		for(int i = 0 ; i < MAP_WIDTH ; i++){
			for(int j = 1 ; j < MAP_HEIGHT ; j++){
				if(j == 1)
					super.set(i, j, MaterialBank.makeBloc(MaterialBank.TYPE_TERRE, j*100/MAP_HEIGHT+1));
				else{

					if(makeVide()){
						super.set(i, j, MaterialBank.makeBloc(MaterialBank.TYPE_VIDE, 0));
					} else if (makeLead(j)){
						super.set(i, j, MaterialBank.makeBloc(MaterialBank.TYPE_LEAD, j*100/MAP_HEIGHT+1));
					} else if (makeCopper(j)){
						super.set(i, j, MaterialBank.makeBloc(MaterialBank.TYPE_COPPER, j*100/MAP_HEIGHT+1));
					} else if (makeGold(j)){
						super.set(i, j, MaterialBank.makeBloc(MaterialBank.TYPE_GOLD, j*100/MAP_HEIGHT+1));
					} else if(makeAlu(j)){
						super.set(i, j, MaterialBank.makeBloc(MaterialBank.TYPE_ALU, j*100/MAP_HEIGHT+1));
					}  else {
						super.set(i, j, MaterialBank.makeBloc(MaterialBank.TYPE_TERRE, j*100/MAP_HEIGHT+1));
					}
				}
			}
		}
		
		//Calcul des bons types de vides
		for(int i = 0 ; i < MAP_WIDTH ; i++){
			for(int j = 0 ; j < MAP_HEIGHT ; j++){
				if(isVide(i,j))
					computeVideType(i, j);
			}
		}
	}
	
	private boolean makeVide(){
		return Math.random() < 0.25;
	}
	
	private boolean makeGold(int x){
		return (Math.random() < Math.atan(((x-150)/30.0)*0.020+0.025));
	}
	private boolean makeCopper(int x){
		return Math.random() < Math.sqrt(x/30000.0);
	}
	private boolean makeLead(int x){
		return Math.random() < -0.05/256+0.15;
	}
	private boolean makeAlu(int x){
		return Math.random() < Math.sqrt((x-50f)/51200);
	}
	private boolean makeRuby(int x){
		return Math.random() < Math.sqrt(Math.max(0, (x-110.0)/(MAP_HEIGHT*30)));
	}
	private boolean makeSaphir(int x){
		return Math.random() < Math.sqrt(Math.max(0, (x-90.0)/(MAP_HEIGHT*10)));
	}
	private boolean makeUranium(int x){
		return Math.random() < Math.sqrt(Math.max(0, (x-150.0)/(MAP_HEIGHT*10)));
	}
	private boolean makeAmethyst(int x){
		return Math.random() < Math.sqrt(Math.max(0, (x-200.0)/(MAP_HEIGHT*10)));
	}
	

	@Override
	public void set(int x, int y, int tile){
		if(x >= 0 && x < World.MAP_WIDTH && y >= 0 && y < World.MAP_HEIGHT){
			super.set(x, y, tile);

			if(tile == MaterialBank.TYPE_VIDE)
				computeVideType(x, y);
			
			if(isVide(x-1, y) && x > 0){
				computeVideType(x-1, y);
			}
			if(isVide(x, y-1) && y > 0){
				computeVideType(x, y-1);
			}
			if(isVide(x+1, y) && x < MAP_WIDTH-1){
				computeVideType(x+1, y);
			}
			if(isVide(x, y+1) && y < MAP_HEIGHT-1){
				computeVideType(x, y+1);
			}
		}
	}
	
	public void setDirect(int x, int y, int tile) {
		super.set(x, y, tile);
	}
	
	public void applyModifier(int x, int y, int modifier) {
		setDirect(x, y, (get(x, y) & MaterialBank.MATERIAL_MASK) + (modifier & (~MaterialBank.MATERIAL_MASK)));
	}
	
	/**
	 * Alters the tile's material so that it is dug at some level in some direction
	 * @param x
	 * @param y
	 * @param direction direction in which to dig
	 * @param level how much to dig
	 */
	public void digMaterial(int x, int y, int direction, int level) {
		applyModifier(x, y, 0x10*direction+level);
	}
	
	public boolean isVide(int x, int y){
		
		if(y <= 0 || y >= World.MAP_HEIGHT)	return true;
		if(x < 0 || x >= World.MAP_WIDTH)	return false;
		
		return isVide(get(x, y));
	}
	
	public static boolean isVide(int tile){
		return (tile & WorldPhysic.COLLISION_MASK) == 0;
	}
	
	public void computeVideType(int x, int y){
		// True if adjacent case is empty
		boolean left = isVide(x-1, y);
		boolean right = isVide(x+1, y);
		boolean top = isVide(x, y-1);
		boolean bottom = isVide(x, y+1);
		
		if(left && right && top && bottom)				super.set(x,y, MaterialBank.TYPE_VIDE4);
		else if(left && right && top && !bottom)		super.set(x,y, MaterialBank.TYPE_VIDE3BOTTOM);
		else if(left && right && !top && bottom)		super.set(x,y, MaterialBank.TYPE_VIDE3TOP);
		else if(left && !right && top && bottom)		super.set(x,y, MaterialBank.TYPE_VIDE3RIGHT);
		else if(!left && right && top && bottom)		super.set(x,y, MaterialBank.TYPE_VIDE3LEFT);
		else if(left && right && !top && !bottom)		super.set(x,y, MaterialBank.TYPE_VIDE2HORIZONTAL);
		else if(!left && !right && top && bottom)		super.set(x,y, MaterialBank.TYPE_VIDE2VERTICAL);
		else if(left && !right && top && !bottom)		super.set(x,y, MaterialBank.TYPE_VIDE2TOPLEFT);
		else if(!left && right && top && !bottom)		super.set(x,y, MaterialBank.TYPE_VIDE2TOPRIGHT);
		else if(left && !right && !top && bottom)		super.set(x,y, MaterialBank.TYPE_VIDE2BOTTOMLEFT);
		else if(!left && right && !top && bottom)		super.set(x,y, MaterialBank.TYPE_VIDE2BOTTOMRIGHT);
		else if(!left && !right && !top && bottom)		super.set(x,y, MaterialBank.TYPE_VIDE1BOTTOM);
		else if(!left && !right && top && !bottom)		super.set(x,y, MaterialBank.TYPE_VIDE1TOP);
		else if(!left && right && !top && !bottom)		super.set(x,y, MaterialBank.TYPE_VIDE1RIGHT);
		else if(left && !right && !top && !bottom)		super.set(x,y, MaterialBank.TYPE_VIDE1LEFT);
		else											super.set(x,y, MaterialBank.TYPE_VIDE);
	}

}
