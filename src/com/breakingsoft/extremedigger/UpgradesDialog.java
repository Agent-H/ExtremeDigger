package com.breakingsoft.extremedigger;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class UpgradesDialog extends PausingDialog{

	private GameState mGS;
	private LayoutInflater mInflater;
	private TextView mBatteryPrice;
	private TextView mBatteryLevel;
	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreateDialog(savedInstanceState);
		
		 AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		 
		 mInflater = getActivity().getLayoutInflater();
		 
		 View contents = mInflater.inflate(R.layout.upgrade_dialog, null);
		 
		 TableRow batteryRow = (TableRow) contents.findViewById(R.id.upgradeBatteryRow);
		 
		 batteryRow.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				showBatteryDialog();
			}
			 
		 });
		 
		 mBatteryPrice = (TextView)contents.findViewById(R.id.batteryPrice);
		 mBatteryLevel = (TextView)contents.findViewById(R.id.batteryLevel);
		 
		 refresh();
		 
		 builder.setView(contents)
		 .setPositiveButton("Ok", null);
		 
		 return builder.create();
	}
	
	public void setGameState(GameState gs){
		mGS = gs;
	}
	
	private void showBatteryDialog(){
		AlertDialog.Builder myBuilder=  new AlertDialog.Builder(getActivity());
		
		View contents = mInflater.inflate(R.layout.upgrade_battery_dialog, null);
		
		final TextView levelView  = (TextView)contents.findViewById(R.id.levelView);
		final TextView upgradePrice = (TextView)contents.findViewById(R.id.priceView);
		final TextView capacity = (TextView)contents.findViewById(R.id.capacityView);
		final TextView upgradeCapacity = (TextView)contents.findViewById(R.id.upgradeCapacityView);
		
		levelView.setText(""+(mGS.getFuelTank().level()+1));
		upgradePrice.setText(""+mGS.getFuelTank().getUpgradePrice()+"$");
		capacity.setText(""+(mGS.getFuelTank().getCapacity()/10)+"C");
		upgradeCapacity.setText(""+(mGS.getFuelTank().getUpgradeCapacity()/10)+"C");
		
		Button upgradeButton = (Button)contents.findViewById(R.id.upgradeButton);
		
		
		final Toast notEnoughMoneyToast = Toast.makeText(getActivity(), "Not enough money", Toast.LENGTH_SHORT);
		
		
		upgradeButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				int price = mGS.getFuelTank().getUpgradePrice();
				
				if(mGS.getMoney() > price){
					mGS.getFuelTank().upgrade();
					mGS.subMoney(price);
					
					levelView.setText(""+(mGS.getFuelTank().level()+1));
					upgradePrice.setText(""+mGS.getFuelTank().getUpgradePrice()+"$");
					capacity.setText(""+(mGS.getFuelTank().getCapacity()/10)+"C");
					upgradeCapacity.setText(""+(mGS.getFuelTank().getUpgradeCapacity()/10)+"C");
					
					//Refreshes the main dialog
					refresh();
				} else {
					notEnoughMoneyToast.show();
				}
			}
			
		});
		
		myBuilder.setView(contents);
		
		myBuilder.create().show();
	}
	
	private void refresh(){
		mBatteryLevel.setText(""+(mGS.getFuelTank().level()+1));
		mBatteryPrice.setText(""+mGS.getFuelTank().getUpgradePrice()+"$");
	}
}
