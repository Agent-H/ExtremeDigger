package com.agenth.engine.graphics;

import com.agenth.engine.components.TwoDimention;
import com.agenth.engine.core.Component;
import com.agenth.engine.core.Entity;
import com.agenth.engine.core.Event;
import com.agenth.engine.core.EventListener;
import com.agenth.engine.core.Game;

/**
 * This component allows the entity to be followed by the viewport.
 * Requires 2D.
 *
 * When removed form game, the entity will no longer be followed.
 */
public class ViewTarget extends Component implements EventListener{

	GraphicEngine mGraphics;
	
	TwoDimention m2D;
	
	public ViewTarget(Game game, Entity entity) {
		super(game, entity);

		entity.bind("removed", this);
		
		mGraphics = (GraphicEngine) game.getModule(GraphicEngine.MODULE_NAME);
		
		m2D = (TwoDimention) requireOne("2D");
	}
	
	@Override
	public void onEvent(Event evt) {
		if(evt.type.equals("removed")){
			mGraphics.setTarget(m2D);
		}
	}
	
	/**
	 * Tells view to follow this entity
	 */
	public void enable(){
		mGraphics.setTarget(m2D);
	}
	
	/**
	 * Stopps following this entity
	 */
	public void disable(){
		mGraphics.setTarget(m2D);
	}
	
}
