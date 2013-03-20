package com.agenth.engine.graphics;

import com.agenth.engine.core.Component;
import com.agenth.engine.core.Entity;
import com.agenth.engine.core.Game;

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
