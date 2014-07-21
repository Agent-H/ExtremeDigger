package com.agenth.extremedigger;

import com.agenth.extremedigger.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class FuelView extends View {

	private Bitmap background;
	private Bitmap filling;
	private Bitmap fillingOk;
	private Bitmap fillingLow;
	
	private Rect destRect;
	private Rect fillingRect;
	
	
	private int fillingTop;
	private int fillingBottom;
	
	private FuelTank mTank;
	
	public FuelView(Context context) {
		super(context);

		init(context);
	}
	
	public FuelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init(context);
	}
	
	public FuelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		init(context);
	}
	
	public void setFuelTank(FuelTank tank){
		mTank = tank;
	}
	
	private void init(Context context){
		background = BitmapFactory.decodeResource(context.getResources(), R.drawable.battery);
		fillingOk = BitmapFactory.decodeResource(context.getResources(), R.drawable.b_green_bar);
		fillingLow = BitmapFactory.decodeResource(context.getResources(), R.drawable.b_red_bar);
		
		filling = fillingOk;
		
		destRect = new Rect();
		fillingRect = new Rect();
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		
		destRect.right = right - left;
		destRect.bottom = bottom - top;
		
		fillingRect.left = destRect.right*1/10;
		fillingRect.right = destRect.right*9/10;
		fillingTop = destRect.bottom*1/8;
		fillingBottom = fillingRect.bottom = destRect.bottom*19/20;
	}

	@Override
	public void onDraw(Canvas c){
		
		int maxFuel = 1;
		int fuel = 1;
		
		if(mTank != null){
			maxFuel = mTank.getMaxFuel();
			fuel = mTank.getFuel();
		}
		
		fillingRect.top = fillingTop + (fillingBottom - fillingTop)*(maxFuel - fuel)/maxFuel;
		if( (float)fuel/maxFuel < 0.20f)
			filling = fillingLow;
		else
			filling = fillingOk;
		
		c.drawBitmap(background, null, destRect, null);
		c.drawBitmap(filling, null, fillingRect, null);
	}
}
