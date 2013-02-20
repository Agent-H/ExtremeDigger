package com.breakingsoft.extremedigger;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.breakingsoft.engine.components.Animation;
import com.breakingsoft.engine.components.Animation.AnimationListener;
import com.breakingsoft.engine.components.Toast;
import com.breakingsoft.engine.core.Component;
import com.breakingsoft.engine.core.Entity;
import com.breakingsoft.engine.core.Game;
import com.breakingsoft.engine.core.World;
import com.breakingsoft.engine.graphics.Graphic;
import com.breakingsoft.engine.graphics.Sprite;
import com.breakingsoft.engine.graphics.ViewTarget;
import com.breakingsoft.engine.physics.Physic;
import com.breakingsoft.engine.physics.WorldPhysic;
import com.breakingsoft.engine.util.VectF;

public class Digger extends Component implements AnimationListener, JoystickMovedListener{
	
	public static final int MAX_SPEED_FOR_DIGGING = 4;
	public static final int JOYSTICK_MIN_THRESHOLD = 40;
	public static final float DISTANCE_DIGGING_THRESHOLD = 1;
	public static final int DIGGING_TIME = 1000;
	public static final long DIGGING_START_TIME = 20000000;
	
	public static final int DIRECTION_LEFT = 0, 
							DIRECTION_RIGHT = 1, 
							DIRECTION_UP = 2, 
							DIRECTION_DOWN = 3,
							DIRECTION_NONE = 4;
	
	//True if digger is currently digging (disables physic)
	private boolean isDigging;
	//True if current digging can be aborted
	private boolean diggingAbortable;
	//Measures time during which user is trying to digg before starting actual digging
	private long diggingTimer = 0;
	
	private int diggingX, diggingY;
	private int direction, joystickDirection = DIRECTION_NONE;
	
	private Physic mPhysic;
	
	private Animation mAnimation;
	
	private DiggerWorld mWorld;
	
	private VectF force = new VectF();
	
	private Drawable[] mDrawables = new Drawable[4];
	
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
			.setSize(World.TILE_SIZE-5, World.TILE_SIZE-5)
			.setBounceFactor(0.3f)
			.setDamping(0.2f)
			.addForce(force);
		
		Resources res = act.getResources();
		mDrawables[DIRECTION_UP] = res.getDrawable(R.drawable.foreuse_hd);
		mDrawables[DIRECTION_RIGHT] = res.getDrawable(R.drawable.foreuse_d);
		mDrawables[DIRECTION_LEFT] = res.getDrawable(R.drawable.foreuse_g);
		mDrawables[DIRECTION_DOWN] = res.getDrawable(R.drawable.foreuse_bd);
				
		mSprite = ((Sprite)requireOne("Sprite"))
			.setDrawable(mDrawables[DIRECTION_RIGHT]);
		mSprite.setLayer(Graphic.LAYER_FOREGROUND-1);
		
		//Definition de la taille du sprite en respectant les proportions
		mSprite.setSize((int)(World.TILE_SIZE*((float)77/60)), (int)(World.TILE_SIZE*((float)77/60)));
		
		
		((ViewTarget)requireOne("ViewTarget")).enable();
		((JoystickView) act.findViewById(R.id.joystick)).setOnJostickMovedListener(this);
		
		game().addActiveComponent(this);

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
	public void step(long time){
		if(!game().isPaused()){
			if(!isDigging){
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
			} else {
				if(joystickDirection != direction && diggingAbortable){
					abortDigging();
				}
			}
			
		}
	}
	
	private void abortDigging(){
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
		isDigging = false;
		
		mAnimation.stop();
	}
	
	private void tryDigg(int x, int y, long time, int direction){
		
		boolean directionCheck = 
				(direction == DIRECTION_LEFT) ? (mPhysic.get2D().left() % World.TILE_SIZE) < DISTANCE_DIGGING_THRESHOLD
			:	(direction == DIRECTION_RIGHT) ? World.TILE_SIZE - (mPhysic.get2D().right() % World.TILE_SIZE) < DISTANCE_DIGGING_THRESHOLD
			: 	(direction == DIRECTION_DOWN) ? World.TILE_SIZE - (mPhysic.get2D().bottom() % World.TILE_SIZE) < DISTANCE_DIGGING_THRESHOLD
			: false;
		
		if(directionCheck && !mWorld.isVide(x,  y) && mPhysic.getSpeed().length() < MAX_SPEED_FOR_DIGGING){
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
			
			isDigging = true;
			diggingAbortable = true;
			diggingTimer = 0;
			mPhysic.setEnabled(false);
			mAnimation.move(x*World.TILE_SIZE, y*World.TILE_SIZE, DIGGING_TIME);
		}
	}
	
	public void setDirection(int dir){
		direction = dir;
		if(dir != DIRECTION_NONE){
			mSprite.setDrawable(mDrawables[direction]);
		}
	}

	@Override
	public void onAnimationEnd() {
		if(isDigging){
			
			int mineral = (mWorld.get(diggingX, diggingY) & MaterialBank.MATERIAL_MASK);
			
			if(mineral != 0 && mineral != MaterialBank.MATERIAL_TERRE){
				if(mGS.getCargo().put(mineral)){
					((Toast)new Entity(game())
					.requireOne("Toast"))
					.setText("+1 "+MaterialBank.getMaterialName(mineral))
					.show(diggingX*World.TILE_SIZE+20, (diggingY+0.5f)*World.TILE_SIZE+40);
				} else { //Echec de l'ajout du mineral. Le cargo doit �tre plein
					((Toast)new Entity(game())
					.requireOne("Toast"))
					.setText("Cargo full !")
					.show(diggingX*World.TILE_SIZE+20, (diggingY+0.5f)*World.TILE_SIZE+40);
				}
			}
			
			//Remise en place de la foreuse
			mWorld.set(diggingX, diggingY, MaterialBank.TYPE_VIDE);
			mPhysic.setEnabled(true);
			isDigging = false;
		}
	}

	@Override
	public void onAnimationStep(int time) {
		if(time > 3*DIGGING_TIME/4){
			mWorld.setDirect(diggingX, diggingY, (mWorld.get(diggingX, diggingY) & MaterialBank.MATERIAL_MASK)+ 0x10*direction+3);
		} else if(time > DIGGING_TIME/2) {
			mWorld.setDirect(diggingX, diggingY, (mWorld.get(diggingX, diggingY) & MaterialBank.MATERIAL_MASK) + 0x10*direction+2);
		} else if(time > DIGGING_TIME/4){
			diggingAbortable = false;
			mWorld.setDirect(diggingX, diggingY, (mWorld.get(diggingX, diggingY) & MaterialBank.MATERIAL_MASK) + 0x10*direction + 1);
			mWorld.computeVideType(
			(direction == DIRECTION_LEFT) ? diggingX+1 : (direction == DIRECTION_RIGHT) ? diggingX-1 : diggingX, 
			(direction == DIRECTION_UP) ? diggingY+1 : (direction == DIRECTION_DOWN) ? diggingY-1 : diggingY);
		}
	}
	
	@Override
	public void OnMoved(int pan, int tilt) {
		float fX = 0, fY = 0;
		
		if(Math.abs(pan) > JOYSTICK_MIN_THRESHOLD){
			fX = (float)pan / 40;
		}
		if(tilt > JOYSTICK_MIN_THRESHOLD) {
			fY = -tilt/10;
		}
		
		force.set(fX, fY);
		
		if(Math.abs(pan) > Math.abs(tilt) && Math.abs(pan) > JOYSTICK_MIN_THRESHOLD){
			joystickDirection = (pan > 0) ? DIRECTION_RIGHT : DIRECTION_LEFT;
		}
		else if(Math.abs(tilt) > Math.abs(pan) && Math.abs(tilt) > JOYSTICK_MIN_THRESHOLD) {
			joystickDirection =  (tilt > 0) ? DIRECTION_UP : DIRECTION_DOWN;
		} else {
			joystickDirection = DIRECTION_NONE;
		}
		
		if(!isDigging){
			setDirection(joystickDirection);
		}
	}

	@Override
	public void OnReleased() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnReturnedToCenter() {
		force.set(0,0);
	}
}
