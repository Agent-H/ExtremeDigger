package com.agenth.extremedigger;

import java.io.IOException;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.agenth.extremedigger.util.SystemUiHider;

public class GameActivity extends FragmentActivity  {

	private DiggerGame myGame; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(null);
		setContentView(R.layout.activity_game);
		
		myGame = new DiggerGame(this);
		
		if (savedInstanceState != null && savedInstanceState.containsKey("savedGame")) {
			try {
				myGame.load(savedInstanceState.getString("savedGame"));
			} catch (Exception e) {
				// whatever
				e.printStackTrace();
			}
		} else {
			myGame.resume();
		}
		
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
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		try {
			myGame.saveAs("tmp");
			outState.putString("savedGame", "tmp");
		} catch (IOException e) {
			Context ctx = getApplicationContext();
			Toast toast = Toast.makeText(ctx, "Can't save game. You may lose your progression.", Toast.LENGTH_LONG);
			toast.show();
			
			Log.e("save error", e.getStackTrace().toString());
		}
		
		
		
		
	}

}
