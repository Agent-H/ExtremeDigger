package com.agenth.extremedigger;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

import com.agenth.extremedigger.util.SystemUiHider;
import com.agenth.extremedigger.R;

public class GameActivity extends FragmentActivity  {

	private DiggerGame myGame;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		myGame = new DiggerGame(this);
		myGame.resume();
		
		SystemUiHider mSystemUiHider = SystemUiHider.getInstance(this, findViewById(R.id.fullscreen_content),
				SystemUiHider.FLAG_FULLSCREEN);
		mSystemUiHider.setup();
		mSystemUiHider.hide();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_game, menu);
		return true;
	}
	
	@Override
	public void onPause(){
		super.onPause();
		myGame.stop();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		myGame.start();
	}

}
