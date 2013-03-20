package com.agenth.engine.physics;

import java.util.ArrayList;
import java.util.Iterator;

import android.graphics.RectF;

import com.agenth.engine.components.TwoDimention;
import com.agenth.engine.core.Component;
import com.agenth.engine.core.Entity;
import com.agenth.engine.core.Event;
import com.agenth.engine.core.EventListener;
import com.agenth.engine.core.Game;
import com.agenth.engine.util.LinkedList;
import com.agenth.engine.util.VectF;

public class Physic extends Component implements EventListener{

	public enum Side { NONE, LEFT, RIGHT, BOTTOM, TOP };
	public enum Orientation  { HORIZONTAL, VERTICAL };

	public static final float COLLISION_MARGIN = 0.0f;
	public static final float DECIMAL_ROUNDING = 0.001f;
	public static final float MIN_SPEED = 0.05f;
	
	private float mWeight = 100;
	
	private float mDamping = 0.1f;
	private VectF mDampingVect;
	
	private PhysicEngine mPhysics;
	
	private float mBounceFactor = 0.5f;
	private LinkedList<Physic>.Entry mEntry;
	
	private VectF mSpeed = new VectF();
	private VectF mPrevSpeed = new VectF();
	private float mMaxSpeed = 30;
	
	private TwoDimention my2D;
	
	private ArrayList<VectF> mForces = new ArrayList<VectF>();
	
	private RectF mNextRect, mOverlapRect;
	
	private CollisionEventData mEventData;
	
	private boolean mEnabled = true;
	
	public Physic(Game game, Entity entity) {
		super(game, entity);

		mPhysics = (PhysicEngine) game.getModule(PhysicEngine.MODULE_NAME);
		my2D = (TwoDimention)require("2D")[0];
		
		entity.bind("inserted removed", this);
		
		mDampingVect = new VectF();
		//addForce(mDampingVect);
		
		mNextRect = new RectF(my2D.left(), my2D.top(), my2D.right(), my2D.bottom());
		mOverlapRect = new RectF();
		mEventData = new CollisionEventData();
	}
	
	public TwoDimention get2D(){ return my2D; }
	
	public float getWeight(){ return mWeight; }
	public Physic setWeight(float weight){ mWeight = (weight < 0) ? 0 : weight; return this;}
	
	public Physic setMaxSpeed(float speed){ mMaxSpeed = speed; return this; }
	public float getMaxSpeed(){ return mMaxSpeed; }

	public Physic setBounceFactor(float factor){ mBounceFactor = factor; return this;}
	public float getBounceFactor(){ return mBounceFactor; }
	
	public Physic addForce(VectF force){
		mForces.add(force);
		
		return this;
	}
	
	public Physic setEnabled(boolean enabled){	mEnabled = enabled; return this;	}
	public boolean getEnabled(){ return mEnabled; }
	
	public Physic removeForce(VectF force){
		mForces.remove(force);
		
		return this;
	}
	
	public PhysicEngine getEngine(){
		return mPhysics;
	}
	
	public Physic setSpeed(VectF speed){ 
		float length = speed.length();
		if(length > mMaxSpeed){
			
			//If it is already maxSpeed, avoid altering x/y components
			if(!isMaxSpeed()){
				mSpeed.set(
						speed.x*mMaxSpeed/length,
						speed.y*mMaxSpeed/length
					);
			}
		}
		else if(length < DECIMAL_ROUNDING){
			speed.x = speed.y = 0;
		} else{
			mSpeed.x = speed.x;
			mSpeed.y = speed.y;
		}
		
		return this;
	}
	
	public Physic setSpeed(float x, float y){
		setSpeed(new VectF(x, y));
		return this;
	}
	
	public boolean isMaxSpeed(){
		return Math.round(mSpeed.length()) == Math.round(mMaxSpeed);
	}
	
	public VectF getSpeed(){
		return mSpeed;
	}
	
	public Physic setDamping(float damping){
		mDamping = damping;
		return this;
	}
	
	public RectF getNextRect(){
		mNextRect.set(my2D.left(), my2D.top(), my2D.right(), my2D.bottom());
		mNextRect.offset(mSpeed.x,  mSpeed.y);
		return mNextRect;
	}
	
	//Pas public, seul le moteur physique bouge une entit�
	void step(){
		if(mEnabled){
			my2D.move(mSpeed);
			
			if(mWeight != 0){
				updateSpeed();
			}
			
			mDampingVect.set(mSpeed.x*mDamping+Math.signum(mSpeed.x)*0.5f, mSpeed.y*mDamping+Math.signum(mSpeed.y)*0.5f);
			
			setSpeed(
				(Math.abs(mSpeed.x) > Math.abs(mDampingVect.x)) ? mSpeed.x - mDampingVect.x : 0,
				(Math.abs(mSpeed.y) > Math.abs(mDampingVect.y)) ? mSpeed.y - mDampingVect.y : 0
			);
		}
	}
	
	private void updateSpeed(){
		mPrevSpeed.x = mSpeed.x;
		mPrevSpeed.y = mSpeed.y;
		
		Iterator<VectF> it = mForces.iterator();
		while (it.hasNext()) {
			VectF v = it.next();
			
			setSpeed(mSpeed.add(v));
		}
	}
	
	public void setLinkedListEntry(LinkedList<Physic>.Entry entry){
		mEntry = entry;
	}

	public void doCollision(Side direction, Physic other){
		mEventData.other = other;
		mEventData.side = direction;
		owner().postEvent("collision", mEventData);
	}
	
	public void computeCollisionSpeed(Orientation axis, Physic other){
		//Speed on collision has to be computed with previous speed to consider inertia
		float v1 = (axis == Orientation.HORIZONTAL) ? mSpeed.x : mSpeed.y;
		float v2 = (axis == Orientation.HORIZONTAL) ? other.mSpeed.x : other.mSpeed.y;
		
		float m1 = getWeight();
		float m2 = other.getWeight();
		
		float nextV1, nextV2;
		
		if(m1 > 0 && m2 > 0){
			nextV2 = ((m2-m1)/(m1+m2)*v2 + 2*m1/(m1+m2)*v1)*other.mBounceFactor;
			nextV1 = ((m1-m2)/(m1+m2)*v1 + 2*m2/(m1+m2)*v2)*mBounceFactor;
		} else if(m1 > 0 && m2 <= 0) {
			nextV2 = v2;
			nextV1 = (-v1)*mBounceFactor;
		} else if(m2 > 0 && m1 <= 0){
			nextV1 = v1;
			nextV2 = (-v2)*mBounceFactor;
		} else {
			nextV1 = v2;
			nextV2 = v1;
		}
		
		if(axis == Orientation.HORIZONTAL){
			mSpeed.x = nextV1;
			other.mSpeed.x = nextV2;
			mPrevSpeed.x = 0;
		} else {
			mSpeed.y = nextV1;
			other.mSpeed.y = nextV2;
			mPrevSpeed.y = 0;
		}
	}
	
	public void getOverlap(Physic other, RectF overlap){
		if(!overlap.setIntersect(other.getNextRect(), getNextRect())){
			overlap.set(0, 0, 0, 0);
		}
	}
	
	public boolean checkCollisionAndSolve(Physic other){
		getOverlap(other, mOverlapRect);
		
		//If there is a collision
		if(mOverlapRect.width() > 0){
			solveCollision(other, mOverlapRect);
			return true;
		}
		return false;
	}
	
	public void solveCollision(Physic o2, RectF overlap){
		float bord1X = 0, bord2X = 0, bord1Y = 0, bord2Y = 0;
		float hX, hY;
		
		VectF relativeSpeed = mSpeed.sub(o2.mSpeed);
		
		if(relativeSpeed.x < 0){
			bord1X = my2D.left();
			bord2X = o2.my2D.right();
			hX = getCollisionHeight(bord1X, bord1X + mSpeed.x, bord2X, bord2X + o2.mSpeed.x);
		} else if(relativeSpeed.x > 0){
			bord1X = my2D.right();
			bord2X = o2.my2D.left();
			hX = getCollisionHeight(bord1X, bord1X + mSpeed.x, bord2X, bord2X + o2.mSpeed.x);
		} else {
			hX = 0;
		}
		
		if(relativeSpeed.y < 0){
			bord1Y = my2D.top();
			bord2Y = o2.my2D.bottom();
			hY = getCollisionHeight(bord1Y, bord1Y + mSpeed.y, bord2Y, bord2Y + o2.mSpeed.y);
		} else if(relativeSpeed.y > 0){
			bord1Y = my2D.bottom();
			bord2Y = o2.my2D.top();
			hY = getCollisionHeight(bord1Y, bord1Y + mSpeed.y, bord2Y, bord2Y + o2.mSpeed.y);
		} else {
			hY = 0;
		}
		
		float maxH = Math.max(hX, hY);
		
		float newX = bord1X;
		float newY = bord1Y;

		if(!Float.isInfinite(hX) && !Float.isInfinite(hY)){
			maxH = Math.max(hX, hY);
			
			newX = computeCoordWithHeight(bord1X, bord1X + mSpeed.x, maxH);
			newY = computeCoordWithHeight(bord1Y, bord1Y + mSpeed.y, maxH);
		}
		//La collision a lieu sur l'axe x
		if(hX > hY){
			if(relativeSpeed.x < 0){
				
				if(mEnabled && o2.mEnabled){
					if(mSpeed.x != 0)
						my2D.setX(newX+COLLISION_MARGIN);
					if(o2.mSpeed.x != 0)
						o2.my2D.setX(newX-o2.my2D.w()-COLLISION_MARGIN);
				}
				
				doCollision(Side.LEFT, o2);
				o2.doCollision(Side.RIGHT, this);
			}
			else if(relativeSpeed.x > 0){
				if(mEnabled && o2.mEnabled){
					if(mSpeed.x != 0)
						my2D.setX(newX-my2D.w()-COLLISION_MARGIN);
					if(o2.mSpeed.x != 0)
						o2.my2D.setX(newX+COLLISION_MARGIN);
				}
				
				doCollision(Side.RIGHT, o2);
				o2.doCollision(Side.LEFT, this);
			} else {
				doCollision(Side.NONE, o2);
				o2.doCollision(Side.NONE, this);
			}
			if(mEnabled && o2.mEnabled)
				computeCollisionSpeed(Orientation.HORIZONTAL, o2);
		}
		else{
			if(relativeSpeed.y < 0){
				if(mEnabled && o2.mEnabled){
					if(mSpeed.y != 0)
						my2D.setY(newY+COLLISION_MARGIN);
					if(o2.mSpeed.y != 0)
						o2.my2D.setY(newY-o2.my2D.h()-COLLISION_MARGIN);
				}
				
				doCollision(Side.TOP, o2);
				o2.doCollision(Side.BOTTOM, this);
			}
			else if(relativeSpeed.y > 0){
				if(mEnabled && o2.mEnabled){
					if(mSpeed.y != 0)
						my2D.setY(newY-my2D.h()-COLLISION_MARGIN);
					if(o2.mSpeed.y != 0)
						o2.my2D.setY(newY+COLLISION_MARGIN);
				}
				
				doCollision(Side.BOTTOM, o2);
				o2.doCollision(Side.TOP, this);
			} else {
				doCollision(Side.NONE, o2);
				o2.doCollision(Side.NONE, this);
			}
			
			if(mEnabled && o2.mEnabled)
				computeCollisionSpeed(Orientation.VERTICAL, o2);
		}
	}
	
	private static float computeCoordWithHeight(float X1, float X2, float H){
		return H*(X2-X1)+X1;
	}
	private static float getCollisionHeight(float A1, float A2, float B1, float B2){
		return (B1-A1)/(A2-A1-B2+B1);
	}
	
	///Shorthand methods to access 2D properties
	public Physic setPos(float x, float y){
		my2D.moveTo(x,y);
		return this;
	}
	
	public Physic setSize(float w, float h){
		my2D.resize(w, h);
		return this;
	}

	@Override
	public void onEvent(Event evt) {
		if(evt.type.equals("inserted")){
			mPhysics.addComponent(this);
		} else if(evt.type.equals("removed")){
			//Il faut supprimer l'�l�ment du monde physique quand on supprime l'entit� du monde.
			if(mEntry != null)
				mEntry.remove();
		}
	}
}
