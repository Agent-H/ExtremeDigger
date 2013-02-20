package com.breakingsoft.engine.graphics;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.breakingsoft.engine.components.TwoDimention;
import com.breakingsoft.engine.core.Component;
import com.breakingsoft.engine.core.Entity;
import com.breakingsoft.engine.core.Event;
import com.breakingsoft.engine.core.EventListener;
import com.breakingsoft.engine.core.Game;
import com.breakingsoft.engine.util.LinkedList;

/**
 * 
 */
public abstract class Graphic extends Component implements EventListener{
	
	public static final int LAYER_FOREGROUND = 16;
	public static final int LAYER_BACKGROUND = 0;
	
	private GraphicEngine mGraphics;
	
	private LinkedList<Graphic>.Entry mEntry;
	
	private TwoDimention m2D;
	
	private boolean mShow = true;
	
	private int mLayer = LAYER_BACKGROUND;
	
	private Rect mArea;
	
	private int mHeight, mWidth;
	
	public Graphic(Game game, Entity entity) {
		super(game, entity);
		
		m2D = (TwoDimention) require("2D")[0];
		
		mGraphics = (GraphicEngine) game.getModule(GraphicEngine.MODULE_NAME);
		entity.bind("inserted removed", this);
		
		mArea = new Rect();
	}
	
	public int getLayer(){ return mLayer; }
	
	public Graphic setLayer(int layer){ 
		if(layer >= LAYER_BACKGROUND && layer <= LAYER_FOREGROUND)
			mLayer = layer;
		return this;
	}

	public final void draw(Canvas c, Rect viewport){
		
		if(mShow){
			if(mHeight != 0 && mWidth != 0){
				mArea.offsetTo((int)m2D.left()+((int)m2D.w()-mWidth)/2, (int)m2D.top() + ((int)m2D.h()-mHeight)/2);
				mArea.bottom = mArea.top + mHeight;
				mArea.right = mArea.left + mWidth;
			} else {
				mArea.set((int)m2D.left(), (int)m2D.top(), (int)m2D.right(), (int)m2D.bottom());
			}
			
			if(Rect.intersects(viewport, mArea)){
				
				mArea.offset(-viewport.left, -viewport.top);
				mGraphics.mapArea(mArea);
				doDraw(c, mArea);
			}
		}
	}
	
	public abstract void doDraw(Canvas c, Rect area);
	
	public void setLinkedListEntry(LinkedList<Graphic>.Entry entry){
		mEntry = entry;
	}
	
	/**
	 * Overrides default size. By default, this is 2D's width and height
	 * @param width
	 * @param height
	 */
	public void setSize(int width, int height){
		mWidth = width;
		mHeight = height;
	}

	@Override
	public void onEvent(Event evt) {
		if(evt.type.equals("inserted"))
			mGraphics.addComponent(this);
		else if(evt.type.equals("removed")){
			//Il faut supprimer l'élément du monde physique quand on supprime l'entité du monde.
			if(mEntry != null)
				mEntry.remove();
		}
	}
	
	public Graphic show(){
		mShow = true;
		return this;
	}
	
	public Graphic hide(){
		mShow = false;
		return this;
	}

}
