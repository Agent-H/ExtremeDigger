package com.agenth.engine.components;

import android.graphics.Color;

import com.agenth.engine.core.Component;
import com.agenth.engine.core.Entity;
import com.agenth.engine.core.Game;
import com.agenth.engine.graphics.ColorComponent;
import com.agenth.engine.util.LinkedList;
import com.agenth.engine.util.VectF;

public class Animation extends Component{
	
	//Animable components
	private TwoDimention m2D;
	private ColorComponent mColor;
	
	private Game mGame;
	
	private LinkedList<Component>.Entry mEntry;
	
	//Animable values
	private VectF beginVect = new VectF();
	private VectF endVect = new VectF();
	private int beginColor;
	private int endColor;
	
	private long totalDuration;
	private long currentDuration;
	
	private boolean moveEn = false, colorEn = false;
	
	private AnimationListener mListener;
	
	public Animation(Game game, Entity entity) {
		super(game, entity);
		
		mGame = game;
		m2D = (TwoDimention)requireOne("2D");
	}
	
	public Animation setListener(AnimationListener listener){
		mListener = listener;
		
		return this;
	}
	
	/**
	 * Animate the entity to (x,y) position in duration milliseconds
	 * @param x
	 * @param y
	 * @param duration
	 */
	public Animation move(int x, int y, int duration){
		if(mEntry == null)
			mEntry = mGame.addActiveComponent(this);
		
		if(beginVect == null)
			beginVect = new VectF();
		if(endVect == null)
			endVect = new VectF();
		
		beginVect.set(m2D.x(), m2D.y());
		endVect.set(x, y);
		
		moveEn = true;
		
		startAnimation(duration);
		
		return this;
	}
	
	public Animation setColor(int color, int duration){
		if(mEntry == null)
			mEntry = mGame.addActiveComponent(this);
		
		if(mColor == null){
			mColor = (ColorComponent) requireOne("Color");
		}
		
		beginColor = mColor.get();
		endColor = color;
		
		colorEn = true;
		
		startAnimation(duration);
		
		return this;
	}
	
	private void startAnimation(long duration){
		totalDuration = duration*1000000;
		currentDuration = 0;
	}
	/**
	 * Aborts current animation, calls onAnimationStop();
	 * @return this
	 */
	public Animation stop() {
		if(mEntry != null){
			mGame.removeActiveComponent(mEntry);
			mEntry = null;
			
			if(mListener != null){
				mListener.onAnimationEnd();
			}
			
			moveEn = false;
			colorEn = false;
		}
		return this;
	}
	
	@Override
	public void step(long time){
		if(!mGame.isPaused()){
			
			currentDuration += time;
			mListener.onAnimationStep((int)(currentDuration/1000000), (int)(totalDuration/1000000));
			
			if(moveEn)
				animateMove();
			if(colorEn)
				animateColor();
			
			if(currentDuration >= totalDuration && mListener != null){
				mListener.onAnimationEnd();
				if(mEntry != null){
					mGame.removeActiveComponent(mEntry);
					mEntry = null;
				}
			}
		}
	}
	
	private void animateMove(){
		if(currentDuration < totalDuration){
			m2D.moveTo(beginVect.add(endVect.sub(beginVect).mult((float)currentDuration/totalDuration)));
		} else {
			m2D.moveTo(endVect);
		}
	}
	
	private void animateColor(){
		if(currentDuration < totalDuration){
			float ratio = (float)currentDuration/totalDuration;
			mColor.set(Color.rgb((int)(Color.red(beginColor)+(Color.red(endColor)-Color.red(beginColor))*ratio),
					(int)(Color.green(beginColor)+(Color.green(endColor)-Color.green(beginColor))*ratio),
					(int)(Color.blue(beginColor)+(Color.blue(endColor)-Color.blue(beginColor))*ratio)));
		} else {
			m2D.moveTo(endVect);
		}
	}
	
	public interface AnimationListener{
		void onAnimationEnd();
		void onAnimationStep(int time, int total);
	}
}
