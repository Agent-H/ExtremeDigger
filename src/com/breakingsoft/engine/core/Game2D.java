package com.breakingsoft.engine.core;

import android.support.v4.app.FragmentActivity;
import android.view.SurfaceView;

import com.breakingsoft.engine.components.Animation;
import com.breakingsoft.engine.components.Toast;
import com.breakingsoft.engine.components.TwoDimention;
import com.breakingsoft.engine.graphics.ColorComponent;
import com.breakingsoft.engine.graphics.GraphicEngine;
import com.breakingsoft.engine.graphics.Sprite;
import com.breakingsoft.engine.graphics.Text;
import com.breakingsoft.engine.graphics.ViewTarget;
import com.breakingsoft.engine.graphics.WorldGraphic;
import com.breakingsoft.engine.physics.Physic;
import com.breakingsoft.engine.physics.PhysicEngine;
import com.breakingsoft.engine.physics.WorldPhysic;

/**
 * Basic implementation of a 2D game. It includes the standard physic and graphic engines.
 * It also registers basic 2D components so do not forget to call super.registerComponents() if you shadow this function.
 * @author Hadrien
 *
 */
public class Game2D extends Game{
	
	/**
	 * Creates a 2D game with physics and graphics using the activity layout.
	 * @param act
	 */
	public Game2D(FragmentActivity act, SurfaceView view, int width){
		super(act);
		addModule(new PhysicEngine(this));
		addModule(new GraphicEngine(this, view, width));
	}
	
	/**
	 * Registers 2D components.
	 */
	@Override
	protected void registerComponents(){
		super.registerComponents();
		registerComponent("2D", TwoDimention.class);
		registerComponent("Physic", Physic.class);
		registerComponent("WorldPhysic", WorldPhysic.class);
		registerComponent("Color", ColorComponent.class);
		registerComponent("Sprite", Sprite.class);
		registerComponent("ViewTarget", ViewTarget.class);
		registerComponent("WorldGraphic", WorldGraphic.class);
		registerComponent("Animation", Animation.class);
		registerComponent("Text", Text.class);
		registerComponent("Toast", Toast.class);
	}

	@Override
	public GameState getGameState() {
		return null;
	}

}
