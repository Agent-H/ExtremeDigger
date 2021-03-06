package com.agenth.extremedigger;

import com.agenth.extremedigger.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableRow;
import android.widget.TextView;

public class UpgradesDialog extends PausingDialog{

	private GameState mGS;
	private LayoutInflater mInflater;
	private TextView mBatteryPrice;
	private TextView mBatteryLevel;
	private TextView mCargoPrice;
	private TextView mCargoLevel;
	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreateDialog(savedInstanceState);
		
		 AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		 
		 mInflater = getActivity().getLayoutInflater();
		 
		 View contents = mInflater.inflate(R.layout.upgrade_dialog, null);
		 
		 final UpgradeItemDialog batteryDialog = new UpgradeItemDialog();
		 batteryDialog.setItem(mGS.getFuelTank(), mGS);
		 batteryDialog.setLayout(R.layout.upgrade_battery_dialog);
		 
		 final UpgradeItemDialog cargoDialog = new UpgradeItemDialog();
		 cargoDialog.setItem(mGS.getCargo(), mGS);
		 cargoDialog.setLayout(R.layout.upgrade_cargo_dialog);
		 
		 TableRow batteryRow = (TableRow) contents.findViewById(R.id.upgradeBatteryRow);
		 batteryRow.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				batteryDialog.show(getFragmentManager(), "upgrade");
			}
			 
		 });
		 
		 TableRow cargoRow = (TableRow) contents.findViewById(R.id.upgradeCargoRow);
		 cargoRow.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				cargoDialog.show(getFragmentManager(), "upgrade");
			}
			 
		 });
		 
		 mBatteryPrice = (TextView)contents.findViewById(R.id.batteryPrice);
		 mBatteryLevel = (TextView)contents.findViewById(R.id.batteryLevel);
		 mCargoPrice = (TextView)contents.findViewById(R.id.cargoPrice);
		 mCargoLevel = (TextView)contents.findViewById(R.id.cargoLevel);
		 
		 refresh();
		 
		 builder.setView(contents)
		 .setPositiveButton("Ok", null);
		 
		 return builder.create();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		refresh();
	}
	
	public void setGameState(GameState gs){
		mGS = gs;
	}
	
	private void refresh(){
		mBatteryLevel.setText(""+(mGS.getFuelTank().level()+1));
		mBatteryPrice.setText(""+mGS.getFuelTank().getUpgradePrice()+"$");
		mCargoLevel.setText(""+(mGS.getCargo().level()+1));
		mCargoPrice.setText(""+mGS.getCargo().getUpgradePrice()+"$");
	}
	
	/*private void showCargoDialog(){
		AlertDialog.Builder myBuilder=  new AlertDialog.Builder(getActivity());
		
		View contents = mInflater.inflate(R.layout.upgrade_cargo_dialog, null);
		
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
	}*/
}
