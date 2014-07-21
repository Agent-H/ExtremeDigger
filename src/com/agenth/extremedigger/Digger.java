package com.agenth.extremedigger;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.agenth.engine.components.Animation;
import com.agenth.engine.components.Animation.AnimationListener;
import com.agenth.engine.components.Toast;
import com.agenth.engine.core.Component;
import com.agenth.engine.core.Entity;
import com.agenth.engine.core.Event;
import com.agenth.engine.core.EventListener;
import com.agenth.engine.core.Game;
import com.agenth.engine.core.World;
import com.agenth.engine.graphics.Graphic;
import com.agenth.engine.graphics.Sprite;
import com.agenth.engine.graphics.ViewTarget;
import com.agenth.engine.physics.CollisionEventData;
import com.agenth.engine.physics.Physic;
import com.agenth.engine.physics.WorldPhysic;
import com.agenth.engine.util.VectF;

public class Digger extends Component implements AnimationListener, JoystickView.JoystickMovedListener, EventListener{
	
	/** Cannot digg if digger has an absolute speed bigger than this */
	public static final int MAX_SPEED_FOR_DIGGING = 4;
	/** Joystick movements less than this won't be considered */
	public static final float JOYSTICK_MIN_THRESHOLD = 0.15f;
	/** Distance between digger and block to start digging */
	public static final float DISTANCE_DIGGING_THRESHOLD = 3;
	/** Total time to digg one block */
	public static final int DIGGING_TIME = 1000;
	/** Delay before starting digging */
	public static final long DIGGING_START_TIME = 20;
	/** Factor for fuel consumption during digging */
	public static final int DIGGING_FUEL_COEFF = 10;
	/** Factor for fuel consumption during normal operation */
	public static final int NOT_DIGGING_FUEL_COEFF = 1;
	public static final float ENGINE_X_FORCE = 2;
	public static final float ENGINE_Y_FORCE = 3;
	
	/** factor to get physic body size from tile size ( <1 ) */
	public static final float SIZE_FACTOR_PHYSIC = 0.90f;
	/** factor to get sprite size from tile size ( >1 )*/
	public static final float SIZE_FACTOR_SPRITE = 77f/60;
	/** offset at which to place the digger once finished digging a tile relative to the tile's origin */
	public static final int AFTER_DIGG_OFFSET = (int)(World.TILE_SIZE*(1-SIZE_FACTOR_PHYSIC)/2);
	/** Amount of time the digger must stay in the air before being considered as "flying" */
	public static final int FLYING_TIMER = 100;
	
	public static final int DIRECTION_LEFT = 0, 
							DIRECTION_RIGHT = 1, 
							DIRECTION_UP = 2, 
							DIRECTION_DOWN = 3,
							DIRECTION_UP_NOJET = 4,
							DIRECTION_NONE = 5;
	
	//True if digger is currently digging (disables physic)
	private boolean mIsDigging;
	//True if current digging can be aborted
	private boolean diggingAbortable;
	//Measures time during which user is trying to digg before starting actual digging
	private long diggingTimer = 0;
	
	// True when the sprite is in contact with the ground
	private boolean mIsFlying;
	
	// Adds a little delay before setting isTouchingGround to false
	private int mFlyingTimer;
	
	private int diggingX, diggingY;
	private int direction, joystickDirection = DIRECTION_NONE;
	
	private Physic mPhysic;
	
	private Animation mAnimation;
	
	private DiggerWorld mWorld;
	
	private VectF mForce = new VectF();
	private float mStickX = 0, mStickY = 0;
	
	private Drawable[] mDrawables = new Drawable[6];
	
	private Sprite mSprite;
	
	private GameState mGS;

	public Digger(Game game, Entity entity){
		super(game, entity);
		
		mGS = (GameState)game().getGameState();
	}
	
	public Digger init(Activity act, DiggerWorld world) {
		mWorld = world;
		
		mAnimation = ((Animation) requireOne("Animation"))
			.setListener(this);
		
		mPhysic = ((Physic)requireOne("Physic"))
			.setSpeed(0,0)
			.setSize(World.TILE_SIZE*SIZE_FACTOR_PHYSIC, World.TILE_SIZE*SIZE_FACTOR_PHYSIC)
			.setBounceFactor(0.1f)
			.setDamping(new VectF(0.06f, 0.0017f))
			.addForce(mForce);
		
		Resources res = act.getResources();
		mDrawables[DIRECTION_UP] = res.getDrawable(R.drawable.foreuse_hd);
		mDrawables[DIRECTION_RIGHT] = res.getDrawable(R.drawable.foreuse_d);
		mDrawables[DIRECTION_LEFT] = res.getDrawable(R.drawable.foreuse_g);
		mDrawables[DIRECTION_DOWN] = res.getDrawable(R.drawable.foreuse_bd);
		mDrawables[DIRECTION_UP_NOJET] = res.getDrawable(R.drawable.foreuse_hd_nojet);
		mDrawables[DIRECTION_NONE] = mDrawables[DIRECTION_RIGHT];
				
		mSprite = ((Sprite)requireOne("Sprite"))
			.setDrawable(mDrawables[DIRECTION_RIGHT]);
		mSprite.setLayer(Graphic.LAYER_FOREGROUND-1);
		
		//Definition de la taille du sprite en respectant les proportions
		mSprite.setSize((int)(World.TILE_SIZE*SIZE_FACTOR_SPRITE), (int)(World.TILE_SIZE*SIZE_FACTOR_SPRITE));
		
		
		((ViewTarget)requireOne("ViewTarget")).enable();
		((JoystickView) act.findViewById(R.id.joystick)).setOnJostickMovedListener(this);
		
		game().addActiveComponent(this);
		
		owner().bind("collision", this);

		return init();
	}
	
	/**
	 * R�initialise la foreuse
	 */
	public Digger init(){
		
		
		mAnimation.stop();
		mPhysic.setPos(500, -100);
		mPhysic.setSpeed(0,0);
		
		return this;
	}

	@Override
	public void step(int time){
		if(!game().isPaused()){
			
			setAppropriateSprite();
			
			if(!mIsDigging){
				stepNotDigging(time);
			} else {
				stepDigging(time);
			}
			
		}
	}

	private void stepDigging(int time) {
		mGS.decreaseFuel(time * DIGGING_FUEL_COEFF);
		if(joystickDirection != direction && diggingAbortable){
			abortDigging();
		}
	}

	private void stepNotDigging(int time) {
		mForce.set(
				mStickX*ENGINE_X_FORCE, 
				mStickY*ENGINE_Y_FORCE
			);
		
		mGS.decreaseFuel(time * NOT_DIGGING_FUEL_COEFF);
		
		if (!mIsFlying) {
			int px = (int)mPhysic.get2D().centerX()/World.TILE_SIZE;
			int py = (int)mPhysic.get2D().centerY()/World.TILE_SIZE;
			switch(joystickDirection){
				case DIRECTION_LEFT:
					tryDigg(px-1, py, time, DIRECTION_LEFT);
					break;
				case DIRECTION_RIGHT:
					tryDigg(px+1, py, time, DIRECTION_RIGHT);
					break;
				case DIRECTION_DOWN:
					tryDigg(px, py+1, time, DIRECTION_DOWN);
					break;
			}
		}
		// Re-checks whether is digging since we may have just started digging
		if (!mIsDigging && !mIsFlying && (mFlyingTimer -= time) < 0) {
			mIsFlying = true;
		}
	}

	private void setAppropriateSprite() {
		if (!mIsFlying) {
			mSprite.setDrawable(mDrawables[direction]);
		} else if (mForce.y < 0){
			mSprite.setDrawable(mDrawables[DIRECTION_UP]);
		} else {
			mSprite.setDrawable(mDrawables[DIRECTION_UP_NOJET]);
		}
	}
	
	public void abortDigging(){
		//Restore le type de brique avant creusage
		int tile = mWorld.get(diggingX, diggingY);
		int type = tile & MaterialBank.MATERIAL_MASK;
		int result = type | WorldPhysic.COLLISION_MASK;
		mWorld.set(diggingX, diggingY, result);
		
		switch(direction){
		case DIRECTION_LEFT:
			mPhysic.setPos(World.TILE_SIZE*(diggingX+1), World.TILE_SIZE * diggingY);
			mWorld.computeVideType(diggingX+1, diggingY);
			break;
		case DIRECTION_RIGHT:
			mPhysic.get2D().setRight(World.TILE_SIZE*diggingX);
			mWorld.computeVideType(diggingX-1, diggingY);
			break;
		case DIRECTION_DOWN:
			mPhysic.get2D().setBottom(World.TILE_SIZE*diggingY);
			mWorld.computeVideType(diggingX, diggingY-1);
			break;
		}
		
		mPhysic.setEnabled(true);
		mIsDigging = false;
		
		mAnimation.stop();
	}
	
	private void tryDigg(int x, int y, int time, int direction){
		
		boolean directionCheck = 
				(direction == DIRECTION_LEFT) ? (mPhysic.get2D().left() % World.TILE_SIZE) < DISTANCE_DIGGING_THRESHOLD
			:	(direction == DIRECTION_RIGHT) ? World.TILE_SIZE - (mPhysic.get2D().right() % World.TILE_SIZE) < DISTANCE_DIGGING_THRESHOLD
			: 	(direction == DIRECTION_DOWN) ? World.TILE_SIZE - (mPhysic.get2D().bottom() % World.TILE_SIZE) < DISTANCE_DIGGING_THRESHOLD
			: false;
		
		if(directionCheck && !mWorld.isVide(x,  y) && mPhysic.getPrevSpeed().length() < MAX_SPEED_FOR_DIGGING){
			if(diggingTimer > DIGGING_START_TIME){
				setDirection(direction);
				startDigging(x, y);
			} else {
				diggingTimer += time;
			}
		} else {
			diggingTimer = 0;
		}
	}

	private void startDigging(int x, int y){
		if(World.isValid(x, y)){
			diggingX = x;
			diggingY = y;
			
			mIsDigging = true;
			diggingAbortable = true;
			diggingTimer = 0;
			mPhysic.setEnabled(false);
			mAnimation.move(x*World.TILE_SIZE+AFTER_DIGG_OFFSET, y*World.TILE_SIZE+AFTER_DIGG_OFFSET, DIGGING_TIME);
		}
	}
	
	public void setDirection(int dir){
		if(dir != DIRECTION_NONE){
			direction = dir;
		}
	}

	@Override
	public void onAnimationEnd() {
		if(mIsDigging){
			
			int mineral = (mWorld.get(diggingX, diggingY) & MaterialBank.MATERIAL_MASK);
			
			if(mineral != 0 && mineral != MaterialBank.MATERIAL_TERRE){
				if(mGS.getCargo().put(mineral)){
					((Toast)new Entity(game())
					.requireOne("Toast"))
					.setText("+1 "+MaterialBank.getMaterialName(mineral))
					.show(diggingX*World.TILE_SIZE+20, (diggingY+0.5f)*World.TILE_SIZE+40);
				} else { // Cannot add material, cargo must be full
					((Toast)new Entity(game())
					.requireOne("Toast"))
					.setText("Cargo full !")
					.show(diggingX*World.TILE_SIZE+20, (diggingY+0.5f)*World.TILE_SIZE+40);
				}
			}
			
			//Remise en place de la foreuse
			mWorld.set(diggingX, diggingY, MaterialBank.TYPE_VIDE);
			mPhysic.setEnabled(true);
			mIsDigging = false;
		}
	}

	@Override
	public void onAnimationStep(int time) {
		if(time > 3*DIGGING_TIME/4){
			mWorld.digMaterial(diggingX, diggingY, direction, 3);
		} else if(time > DIGGING_TIME/2) {
			mWorld.digMaterial(diggingX, diggingY, direction, 2);
		} else if(time > DIGGING_TIME/4){
			diggingAbortable = false;
			mWorld.digMaterial(diggingX, diggingY, direction, 1);
			mWorld.computeVideType(
			(direction == DIRECTION_LEFT) ? diggingX+1 : (direction == DIRECTION_RIGHT) ? diggingX-1 : diggingX, 
			(direction == DIRECTION_UP) ? diggingY+1 : (direction == DIRECTION_DOWN) ? diggingY-1 : diggingY);
		}
	}
	
	@Override
	public void OnMoved(float pan, float tilt) {
		
		if(Math.abs(pan) > JOYSTICK_MIN_THRESHOLD){
			mStickX = pan;
		} else 
			mStickX = 0;
		
		if(-tilt > JOYSTICK_MIN_THRESHOLD) {
			mStickY = tilt;
		} else
			mStickY = 0;
		
		if(Math.abs(pan) > Math.abs(tilt) && Math.abs(pan) > JOYSTICK_MIN_THRESHOLD){
			joystickDirection = (pan > 0) ? DIRECTION_RIGHT : DIRECTION_LEFT;
		}
		else if(Math.abs(tilt) > Math.abs(pan) && Math.abs(tilt) > JOYSTICK_MIN_THRESHOLD) {
			joystickDirection =  (tilt < 0) ? DIRECTION_NONE : DIRECTION_DOWN;
		} else {
			joystickDirection = DIRECTION_NONE;
		}
		
		if(!mIsDigging){
			setDirection(joystickDirection);
		}
	}

	@Override
	public void OnReleased() {
		
	}

	@Override
	public void OnReturnedToCenter() {
		mForce.set(0,0);
	}

	@Override
	public void onEvent(Event evt) {
		/* Only event bound right now are collision events */
		CollisionEventData evtData = (CollisionEventData)evt.data;
		
		if (evtData.side == Physic.Side.BOTTOM) {
			mIsFlying = false;
			mFlyingTimer = FLYING_TIMER;
		}
	}
}
