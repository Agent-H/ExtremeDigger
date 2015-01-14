package com.agenth.extremedigger;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.agenth.engine.core.Game;

public abstract class PausingDialog extends DialogFragment{
	
	private Game mGame;
	
	
	public PausingDialog setGame(Game game){
		mGame = game;
		return this;
	}
	
	@Override
	public void onDismiss(DialogInterface dialog){
		super.onDismiss(dialog);
		if(mGame != null)
			mGame.resume();
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		mGame.pause();
		return dialog;
	}
	
	
}
