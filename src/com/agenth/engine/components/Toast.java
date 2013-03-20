package com.agenth.engine.components;

import com.agenth.engine.components.Animation.AnimationListener;
import com.agenth.engine.core.Component;
import com.agenth.engine.core.Entity;
import com.agenth.engine.core.Game;
import com.agenth.engine.graphics.Graphic;
import com.agenth.engine.graphics.Text;
import com.agenth.engine.util.LinkedList;

public class Toast extends Text implements AnimationListener{

	public static final int DEFAULT_SHOW_TIME = 1000;
	
	private TwoDimention m2D;
	private Animation mAnim;
	
	LinkedList<Component>.Entry mEntry;
	
	public Toast(Game game, Entity entity) {
		super(game, entity);

		m2D = (TwoDimention) requireOne("2D");
		
		mAnim = ((Animation) requireOne("Animation")).setListener(this);
		
		setLayer(Graphic.LAYER_FOREGROUND);
		setTextSize(20);
		setColor(0xffffff22);
	}
	
	
	public Toast show(int time){
		owner().insertToGame();
		
		mAnim
			.move((int)m2D.x(), (int)m2D.y()-20, time)
			.setColor(0x00ff2244, time);
		
		return this;
	}
	
	public Toast show(float x, float y, int time){
		m2D.moveTo(x, y);
		show(time);
		
		return this;
	}
	
	public Toast show(float x, float y){
		show(x, y, DEFAULT_SHOW_TIME);
		return this;
	}
	
	public Toast show(){
		show(DEFAULT_SHOW_TIME);
		return this;
	}
	
	@Override
	public Toast setText(String text){
		super.setText(text);
		return this;
	}


	@Override
	public void onAnimationEnd() {
		owner().removeFromGame();
	}


	@Override
	public void onAnimationStep(int time) {
		// TODO Auto-generated method stub
		
	}

}
