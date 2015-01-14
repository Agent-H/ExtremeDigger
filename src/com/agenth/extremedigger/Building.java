package com.agenth.extremedigger;

import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;

import com.agenth.engine.components.TwoDimention;
import com.agenth.engine.core.Component;
import com.agenth.engine.core.Entity;
import com.agenth.engine.core.Event;
import com.agenth.engine.core.EventListener;
import com.agenth.engine.core.Game;
import com.agenth.engine.graphics.Graphic;
import com.agenth.engine.graphics.Sprite;
import com.agenth.engine.physics.CollisionEventData;
import com.agenth.engine.physics.Physic;

/**
 * A building allows the user to open a dialog in which he can do lots of exciting stuff.
 * A building is placed on the surface of the world, is represented by a sprite and opens 
 * the popup when the digger stopps in front of it.
 * @author Hadrien
 *
 */
public class Building extends Component implements EventListener {

	/** Time the player must stop in front of the store before the dialog opens */
	public static final int DIALOG_OPEN_DURATION = 500;
	
	/** initial value for disableDialogTimer */
	public static final int DIALOG_DISABLE_TIME = 500;
	
	/** When timer is 0, dialog can be shown. Timer counts down when the player is not in front of the store **/
	private int disableDialogTimer;
	
	/** True when digger is in front of the store */
	private boolean isDigger;
	
	/** True when digger is stopped */
	private boolean isStopped;
	
	/** Indicates duration of digger's stop */
	int mTimeStopped;
	
	
	private PausingDialog mDialog;
	private FragmentManager mManager;
	private Sprite mSprite;
	private TwoDimention m2D;
	
	public Building(Game game, Entity owner) {
		super(game, owner);

		Physic physic = (Physic)requireOne("Physic");
		physic.setEnabled(false);
		mSprite = (Sprite) requireOne("Sprite");
		mSprite.setLayer(Graphic.LAYER_BACKGROUND);
		m2D = (TwoDimention) requireOne("2D");
		
		owner.bind("collision", this);
		game.addActiveComponent(this);
	}

	public void setDialogFragment(PausingDialog dialog, FragmentManager manager){
		mDialog = dialog;
		mManager = manager;
	}
	
	public void setDrawable(Drawable drawable){
		mSprite.setDrawable(drawable);
	}
	
	public void setRect(int x, int y, int w, int h){
		m2D.moveTo(x, y);
		m2D.resize(w,  h);
	}

	
	//Les deux m�hodes qui suivent garde une trace du d�placement du digger devant le building
	@Override
	public void onEvent(Event evt) {
		if(evt.type.equals("collision")){
			CollisionEventData data = (CollisionEventData) evt.data;
			
			if(data.other.owner().id() == "digger"){
				isDigger = true;
				if(data.other.getSpeed().length() < 5)
					isStopped = true;
			}
		}
	}

	@Override
	public void step(int time){
		if(!game().isPaused()){
			if(isDigger) {
				if(isStopped && disableDialogTimer <= 0){
					mTimeStopped += time;
					if (mTimeStopped > DIALOG_OPEN_DURATION) {
						mDialog.show(mManager, "Building");
						disableDialogTimer = DIALOG_DISABLE_TIME;
					}
				} else {
					mTimeStopped = 0;
				}
			} else if(disableDialogTimer > 0) {
				disableDialogTimer -= time;				
			}
			
			isStopped = false;
			isDigger = false;
		}
	}
}
