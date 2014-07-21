package com.agenth.extremedigger;

import com.agenth.extremedigger.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class FuelDialog extends PausingDialog {
	

	/**
	 * Amount of charge per dollar
	 */
	public static final int C_PER_DOLLAR = 5*FuelTank.FUEL_COEFF;
	
	private GameState mGS;
	private TextView mMoneyCount;
	private FuelView mFuelView;
	private TextView mFuelCount;
	
	public void setGameState(GameState gs){
		mGS = gs;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreateDialog(savedInstanceState);
		
   	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    final View contents = inflater.inflate(R.layout.fuel_dialog, null);
		
		mMoneyCount = (TextView)contents.findViewById(R.id.moneyLabel);
		mFuelCount = (TextView)contents.findViewById(R.id.fuelDialogValue);
		mFuelView = (FuelView)contents.findViewById(R.id.fuelView);
		mFuelView.setFuelTank(mGS.getFuelTank());
		
		((Button)contents.findViewById(R.id.b5bocks)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				fill(5);
			}
		});
		((Button)contents.findViewById(R.id.b10bocks)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				fill(10);
			}
		});
		((Button)contents.findViewById(R.id.b50bocks)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				fill(50);
			}
		});
		((Button)contents.findViewById(R.id.b100bocks)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				fill(100);
			}
		});
		((Button)contents.findViewById(R.id.fillAll)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				fill(0);
			}
		});
		
	    builder.setView(contents)
	    // Add action buttons
       .setPositiveButton("Fermer", null);
	    
	    updateViews();
	    
	    return builder.create();
	}
	
	private void fill(int val){
		
		FuelTank tank = mGS.getFuelTank();
		int maxFuel = tank.getMaxFuel();
		int fuel = tank.getFuel();
		
		if(val == 0){
			val = (maxFuel - fuel);
			if(val/C_PER_DOLLAR > mGS.getMoney()){
				val = mGS.getMoney()*C_PER_DOLLAR;
			}
		} else if(val * C_PER_DOLLAR > maxFuel - fuel){
			val = maxFuel - fuel;
		} else {
			val *= C_PER_DOLLAR;
		}
		
		mGS.subMoney(val/C_PER_DOLLAR);
		mGS.fillTank(val);
		
		updateViews();
	}
	
	private void updateViews(){
		mMoneyCount.setText(mGS.getMoney()+"$");
		mFuelCount.setText((mGS.getFuelTank().getFuel()/FuelTank.FUEL_COEFF)+"/"+(mGS.getFuelTank().getValue())+"C");
		mFuelView.invalidate();
	}
}
