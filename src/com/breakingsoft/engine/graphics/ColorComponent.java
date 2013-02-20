package com.breakingsoft.engine.graphics;

import com.breakingsoft.engine.core.Component;
import com.breakingsoft.engine.core.Entity;
import com.breakingsoft.engine.core.Game;

public class ColorComponent extends Component{
	
	private int color;
	
	public ColorComponent(Game game, Entity entity) {
		super(game, entity);
	}
	
	public int get(){
		return color;
	}
	
	public ColorComponent set(int c){ 
		color = c; 
		return this; 
	}
}
