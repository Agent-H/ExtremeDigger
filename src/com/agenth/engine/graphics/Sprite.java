package com.agenth.engine.graphics;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.agenth.engine.core.Entity;
import com.agenth.engine.core.Game;

public class Sprite extends Graphic{

	Drawable mDrawable;
	
	public Sprite(Game game, Entity entity) {
		super(game, entity);

	}

	@Override
	public void doDraw(Canvas c, Rect area) {
		if(mDrawable != null){
			mDrawable.setBounds(area);
			mDrawable.draw(c);
		}		
	}

	public Sprite setDrawable(Drawable drawable){
		mDrawable = drawable;
		return this;
	}
}
