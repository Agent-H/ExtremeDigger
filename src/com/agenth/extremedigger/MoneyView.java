package com.agenth.extremedigger;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

public class MoneyView extends TextView{

	private GameState mGS;
	private int mMoneyCache;
	
	public MoneyView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public MoneyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public MoneyView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	public void setGameState(GameState gs){
		mGS = gs;
	}
	
	@Override
	public void onDraw(Canvas c){
		if(mGS != null){
			if(mMoneyCache != mGS.getMoney()){
				setText(mGS.getMoney()+"$");
				mMoneyCache = mGS.getMoney();
			}
		}
		super.onDraw(c);
	}


}
