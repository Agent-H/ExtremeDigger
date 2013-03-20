package com.agenth.engine.components;

import com.agenth.engine.core.Component;
import com.agenth.engine.core.Entity;
import com.agenth.engine.core.Game;
import com.agenth.engine.util.VectF;

/**
 * This component holds basic 2D infos about the entity :
 * x, y coords
 * width and height. This typically corresponds to entity's bounding box
 * 
 * Always use these when referring to the entity's position
 * @author Hadrien
 *
 */
public class TwoDimention extends Component {
	
	private float mX, mY, mW, mH;
	
	public TwoDimention(Game game, Entity entity) {
		super(game, entity);
		
		mW = 100;
		mH = 100;
	}
	
	/**
	 * Getter to access left coordinate of entity
	 * @return x coordinate
	 */
	public float x(){ return mX; }
	
	/**
	 * Getter to access top coordinate of entity
	 * @return y coordinate
	 */
	public float y(){ return mY; }
	
	/**
	 * Getter to access width of entity
	 * @return width
	 */
	public float w(){ return mW;	}
	
	/**
	 * Getter to access height of entity
	 * @return height
	 */
	public float h(){ return mH; }
	
	/**
	 * Getter to access center x coordinate of entity
	 * @return x coordinate of entity's center
	 */
	public float centerX(){ return mX + mW/2; }
	
	/**
	 * Getter to access center y coordinate of entity
	 * @return y coordinate of entity's ccnter
	 */
	public float centerY(){ return mY + mH/2; }
	
	/**
	 * Getter to access left coordinate of entity. This is equivalent to calling x()
	 * @return left coordinate
	 */
	public float left(){ return mX; }
	
	/**
	 * Getter to access right coordinate of entity
	 * @return right coordinate
	 */
	public float right(){ return mX+mW; }
	
	/**
	 * Getter to access top coordinate of entity. This is equivalent to calling y()
	 * @return top coordinate
	 */
	public float top(){ return mY; }
	
	/**
	 * Getter to access bottom coordinate of entity
	 * @return bottom coordinate
	 */
	public float bottom(){ return mY+mH; }
	
	/**
	 * Sets x coordinate
	 */
	public TwoDimention setX(float x){ mX = x; return this;}
	
	/**
	 * Sets y coordinate
	 */
	public TwoDimention setY(float y){ mY = y; return this;}
	
	/**
	 * Translates the 2D so that right is to that position
	 */
	public TwoDimention setRight(float pos){	mX = pos - mW;	return this;}
	
	/**
	 * Translates the 2D so that left is to that position
	 */
	public TwoDimention setLeft(float pos){ mX = pos; return this; }
	
	/**
	 * Translates the 2D so that top is to that position
	 */
	public TwoDimention setTop(float pos){ mY = pos; return this; }
	
	/**
	 * Translates the 2D so that bottom is to that position
	 */
	public TwoDimention setBottom(float pos){ mY = pos - mH; return this; }
	
	/**
	 * Sets entity's width
	 */
	public void setWidth(float width){
		mW = width;
	}
	
	/**
	 * Moves entity by offset
	 */
	public TwoDimention move(VectF offset){
		move(offset.x, offset.y);
		return this;
	}
	/**
	 * Moves entity by x and y
	 */
	public TwoDimention move(float x, float y){
		mX += x;
		mY += y;
		return this;
	}
	
	/**
	 * Moves top left corner of entity to pos
	 */
	public TwoDimention moveTo(VectF pos){
		moveTo(pos.x, pos.y);
		return this;
	}
	
	/**
	 * Moves top left corner of entity to coords (x,y)
	 */
	public TwoDimention moveTo(float x, float y){
		mX = x;
		mY = y;
		return this;
	}
	
	/**
	 * Resizes the entity to width and height
	 * @param width
	 * @param height
	 * @return this
	 */
	public TwoDimention resize(float width, float height){
		mW = width;
		mH = height;
		return this;
	}
	
	/**
	 * Returns true if this Entity intersects t
	 * @param t entity to test against
	 */
	public boolean intersects(TwoDimention t){
		return this.mX < t.mX+t.mH && t.mX < this.mX+this.mH
                && this.mY < t.mY+t.mH && t.mY < this.mY+this.mH;
	}
	
	/**
	 * Returns true if this entity contains t
	 * @param t entity to be checked
	 * @return
	 */
	public boolean contains(TwoDimention t){
		return t.mX >= this.mX && t.mX+t.mW < this.mX+this.mW && t.mY >= this.mY && t.mY+t.mH < this.mY+this.mH;
	}

}
