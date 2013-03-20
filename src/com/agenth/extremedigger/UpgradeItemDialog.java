package com.agenth.extremedigger;

import com.breakingsoft.extremedigger.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class UpgradeItemDialog extends DialogFragment{
	
	private Upgradable mItem;
	private GameState mGS;
	private int mLayout;
	
	public void setItem(Upgradable item, GameState gs){
		mItem = item;
		mGS = gs;
	}
	
	public void setLayout(int layout){
		mLayout = layout;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		super.onCreateDialog(savedInstanceState);
		
		AlertDialog.Builder myBuilder=  new AlertDialog.Builder(getActivity());
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View contents = inflater.inflate(mLayout, null);
		
		final TextView levelView  = (TextView)contents.findViewById(R.id.levelView);
		final TextView upgradePrice = (TextView)contents.findViewById(R.id.priceView);
		final TextView capacity = (TextView)contents.findViewById(R.id.capacityView);
		final TextView upgradeCapacity = (TextView)contents.findViewById(R.id.upgradeCapacityView);
		
		levelView.setText(""+(mItem.level()+1));
		upgradePrice.setText(""+mItem.getUpgradePrice()+"$");
		capacity.setText(""+(mItem.getValue()));
		upgradeCapacity.setText(""+(mItem.getUpgradeValue()));
		
		Button upgradeButton = (Button)contents.findViewById(R.id.upgradeButton);
		
		
		final Toast notEnoughMoneyToast = Toast.makeText(getActivity(), "Not enough money", Toast.LENGTH_SHORT);
		
		
		upgradeButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				int price = mItem.getUpgradePrice();
				
				if(mGS.getMoney() > price){
					mItem.upgrade();
					mGS.subMoney(price);
					
					levelView.setText(""+(mItem.level()+1));
					upgradePrice.setText(""+mItem.getUpgradePrice()+"$");
					capacity.setText(""+(mItem.getValue()));
					upgradeCapacity.setText(""+(mItem.getUpgradeValue()));
					
				} else {
					notEnoughMoneyToast.show();
				}
			}
			
		});
		
		myBuilder.setView(contents);
		
		return myBuilder.create();
	}

}
