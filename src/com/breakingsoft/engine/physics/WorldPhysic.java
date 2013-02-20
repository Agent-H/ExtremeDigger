package com.breakingsoft.engine.physics;

import android.graphics.RectF;

import com.breakingsoft.engine.components.TwoDimention;
import com.breakingsoft.engine.core.Component;
import com.breakingsoft.engine.core.Entity;
import com.breakingsoft.engine.core.Event;
import com.breakingsoft.engine.core.EventListener;
import com.breakingsoft.engine.core.Game;
import com.breakingsoft.engine.core.World;

public class WorldPhysic extends Component implements EventListener{

	private PhysicEngine mPhysics;
	
	private int mWidth, mHeight;
	
	private Physic physic;
	
	private World mWorld;
	
	public static final int COLLISION_MASK = 0x80000000;
	
	/**
	 * True if entities are blocked by top bottom left or right limits.
	 */
	private boolean 	collideTop = true, 
						collideBottom = true, 
						collideLeft = true, 
						collideRight = true;
	
	public WorldPhysic(Game game, Entity entity) {
		super(game, entity);

		mPhysics = (PhysicEngine) game.getModule(PhysicEngine.MODULE_NAME);
		
		entity.bind("inserted", this);
		
		mWorld = (World) owner();
		mWidth = mWorld.getWidth();
		mHeight = mWorld.getHeight();
		
		//This base physic will be used for every collision with the world
		physic = (Physic)new Entity(game).require("Physic")[0];
		physic.setWeight(0);
		physic.setSize(World.TILE_SIZE, World.TILE_SIZE);
	}
	
	public WorldPhysic setCollisionTop(boolean b){ collideTop = b; return this; }
	public WorldPhysic setCollisionLeft(boolean b){ collideLeft = b; return this; }
	public WorldPhysic setCollisionRight(boolean b){ collideRight = b; return this; }
	public WorldPhysic setCollisionBottom(boolean b){ collideBottom = b; return this; }
	
	public void checkAndSolveCollision(Physic obj){
		RectF nextRect = obj.getNextRect();
		TwoDimention obj2D = obj.get2D();
		
		if(nextRect.top < 0 && collideTop){
			obj.get2D().moveTo(obj2D.x(), Physic.COLLISION_MARGIN);
			obj.doCollision(Physic.Side.TOP, null);
			obj.computeCollisionSpeed(Physic.Orientation.VERTICAL, physic);
		}
		else if(nextRect.bottom > mHeight && collideBottom){
			obj.get2D().moveTo(obj2D.x(), mHeight - obj2D.h()-Physic.COLLISION_MARGIN);
			obj.doCollision(Physic.Side.BOTTOM, null);
			obj.computeCollisionSpeed(Physic.Orientation.VERTICAL, physic);
		}
		if(nextRect.left < 0 && collideLeft){
			obj.get2D().moveTo(+Physic.COLLISION_MARGIN, obj2D.y());
			obj.doCollision(Physic.Side.LEFT, null);
			obj.computeCollisionSpeed(Physic.Orientation.HORIZONTAL, physic);
		}
		else if(nextRect.right > mWidth && collideRight){
			obj.get2D().moveTo(mWidth - obj2D.w()-Physic.COLLISION_MARGIN, obj2D.y());
			obj.doCollision(Physic.Side.RIGHT, null);
			obj.computeCollisionSpeed(Physic.Orientation.HORIZONTAL, physic);
		}
		
		int beginY = Math.max((int) nextRect.top/ World.TILE_SIZE, 0);
		int beginX = Math.max((int) nextRect.left/ World.TILE_SIZE, 0);
		//TODO : vérifier l'utilité du +1
		int endY = Math.min((int) Math.ceil(nextRect.bottom /World.TILE_SIZE)+1, World.MAP_HEIGHT-1);
		int endX = Math.min((int) Math.ceil(nextRect.right / World.TILE_SIZE)+1, World.MAP_WIDTH-1);
		
		//Ce mécanisme tordu permet d'itérer dans le bon sens pour éviter les saccades lorsqu'un objet glisse sur le sol
		int iInc = 1, iInit = beginX, iEnd = endX;
		int jInc = 1, jInit = beginY, jEnd = endY;
		
		if(obj.getSpeed().x < 0){
			iInc = -1;
			iInit = endX;
			iEnd = beginX;
		}
		if(obj.getSpeed().y < 0){
			jInc = -1;
			jInit = endY;
			jEnd = beginY;
		}
		
		for(int i = iInit ; (i <= iEnd && iInc > 0) || (i >= iEnd && iInc < 0) ; i += iInc){
			for(int j = jInit ; (j <= jEnd && jInc > 0) || (j >= jEnd && jInc < 0) ; j += jInc){
				if((mWorld.get(i, j) & COLLISION_MASK) != 0){
					
					physic.setPos(i*World.TILE_SIZE, j*World.TILE_SIZE);
					obj.checkCollisionAndSolve(physic);
				}
			}
		}
	}

	@Override
	public void onEvent(Event evt) {
		if(evt.type.equals("inserted"))
			mPhysics.setWorld(this);
	}
}
