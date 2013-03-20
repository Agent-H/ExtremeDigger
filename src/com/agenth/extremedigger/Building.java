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

	/** Le dialog s'ouvre si le digger est stopp� devant le building durant DIALOG_OPEN_DURATION */
	public static final int DIALOG_OPEN_DURATION = 500000000;
	
	/** 0 : dialog has not been shown yet **/
	private int hasDialog;
	
	/** True when digger is in front of the store */
	private boolean isDigger;
	
	/** True when digger is stopped */
	private boolean isStopped;
	
	/** Indicates duration of digger's stop */
	int timeStopped;
	
	
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
	public void step(long time){
		if(!game().isPaused()){
			if(isDigger){
				if(isStopped && hasDialog == 0){
					timeStopped += time;
					mDialog.show(mManager, "Building");
					hasDialog = 2;
				} else {
					timeStopped = 0;
				}
			} else if(hasDialog > 0) {
				hasDialog--;				
			}
			
			isStopped = false;
			isDigger = false;
		}
	}
}
