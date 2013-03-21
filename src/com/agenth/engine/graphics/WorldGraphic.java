package com.agenth.engine.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.agenth.engine.core.Component;
import com.agenth.engine.core.Entity;
import com.agenth.engine.core.Event;
import com.agenth.engine.core.EventListener;
import com.agenth.engine.core.Game;
import com.agenth.engine.core.World;
import com.agenth.extremedigger.MaterialBank;

public class WorldGraphic extends Component implements EventListener{
	
	GraphicEngine mGraphics;
	
	//Holds graphical width and height, may differ from World.mWidth/mHeight
	int mWidth, mHeight;
	
	private int topBound,
				leftBound,
				rightBound,
				bottomBound;
	
	private int mBgColor = Color.BLACK;
	
	private int mTopColor, mBottomColor;
	private boolean hasVerticalGradient;
	
	World mWorld;

	public WorldGraphic(Game game, Entity entity) {
		super(game, entity);

		mGraphics = (GraphicEngine) game.getModule("Graphics");
		
		entity.bind("inserted removed", this);
		
		mWorld = (World)owner();
		
		mWidth = mWorld.getWidth();
		mHeight = mWorld.getHeight();
		
		topBound = leftBound = 0;
		bottomBound = mHeight;
		rightBound = mWidth;
	}
	
	public WorldGraphic setBackgroundColor(int color){
		mBgColor = color;
		return this;
	}
	
	public WorldGraphic setBackgroundVerticalGradient(int topColor, int bottomColor){
		mTopColor = topColor;
		mBottomColor = bottomColor;
		hasVerticalGradient = true;
		return this;
	}
	
	public void draw(Canvas c, Rect area){
		
		if(hasVerticalGradient){
			if(area.centerY() <= 0){
				c.drawColor(mTopColor);
			} else {
				float h = World.MAP_HEIGHT*World.TILE_SIZE;
				c.drawColor(Color.rgb(
						(int)(-Color.red(mTopColor)/h*area.centerY() + Color.red(mTopColor) + Color.red(mBottomColor)/h*area.centerY()),
						(int)(-Color.green(mTopColor)/h*area.centerY() + Color.green(mTopColor) + Color.green(mBottomColor)/h*area.centerY()), 
						(int)(-Color.blue(mTopColor)/h*area.centerY() + Color.blue(mTopColor) + Color.blue(mBottomColor)/h*area.centerY())
				));
			}
		} else {
			c.drawColor(mBgColor);
		}
		
		int oX = area.left% World.TILE_SIZE;
		int oY = area.top% World.TILE_SIZE;
		
		int beginY = (int) area.top/ World.TILE_SIZE;
		int beginX = (int) area.left/ World.TILE_SIZE;
		int endY = Math.min((int) area.bottom/World.TILE_SIZE+1, World.MAP_HEIGHT);
		int endX = Math.min((int) area.right / World.TILE_SIZE+1, World.MAP_WIDTH);
		
		for(int i = Math.max(beginX, 0) ; i < endX ; i++){
			for(int j = Math.max(beginY, 0) ; j < endY ; j++){
				Drawable sprite = MaterialBank.getDrawable(mWorld.get(i, j));
				if(sprite != null){
					synchronized(sprite){
						sprite.setBounds(mGraphics.mapDimention((i-beginX)* World.TILE_SIZE-oX), 
								mGraphics.mapDimention((j-beginY)* World.TILE_SIZE-oY),
								mGraphics.mapDimention((i+1-beginX)* World.TILE_SIZE-oX),
								mGraphics.mapDimention((j+1-beginY)* World.TILE_SIZE-oY));
						sprite.draw(c);
					}
				}
			}
		}
	}

	@Override
	public void onEvent(Event evt) {
		if(evt.type.equals("inserted")){
			mGraphics.setWorld(this);
		} else if(evt.type.equals("removed")){
			mGraphics.setWorld(null);
		}
	}
	
	public void adjustViewport(Rect viewport){
		if(viewport.left < leftBound)
			viewport.offsetTo(leftBound, viewport.top);
		else if(viewport.right > rightBound)
			viewport.offsetTo(rightBound - viewport.width(), viewport.top);
		
		if(viewport.top < topBound)
			viewport.offsetTo(viewport.left, topBound);
		else if(viewport.bottom > bottomBound)
			viewport.offsetTo(viewport.left, bottomBound - viewport.height());
	}
	
	public WorldGraphic setTopBound(int bound){ topBound = bound; return this; }
	public WorldGraphic setRightBound(int bound){ rightBound = bound; return this; }
	public WorldGraphic setBottomBound(int bound){ bottomBound = bound; return this; }
	public WorldGraphic setLeftBound(int bound){ leftBound = bound; return this; }
	
}
