package com.agenth.engine.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.agenth.engine.core.Entity;
import com.agenth.engine.core.Game;

public class Text extends Graphic{

	private String mText;
	
	private ColorComponent mColor;
	private Paint mPaint;
	
	public Text(Game game, Entity entity) {
		super(game, entity);
		
		mPaint = new Paint();
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(Color.WHITE);
		
		mColor = (ColorComponent) requireOne("Color");
	}
	
	public Text setText(String text){
		mText = text;
		return this;
	}
	
	public Text setTextSize(float size){
		mPaint.setTextSize(size);
		return this;
	}
	
	public Text setColor(int color){
		mColor.set(color);
		return this;
	}

	@Override
	public void doDraw(Canvas c, Rect area) {
		mPaint.setColor(mColor.get());
		c.drawText(mText, area.left, area.top, mPaint);
	}
	
}
