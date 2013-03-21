package com.agenth.extremedigger;

import com.agenth.extremedigger.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MineralDialog extends PausingDialog{
	
	private GameState mGS;
	
	
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
	    final View contents = inflater.inflate(R.layout.mineral_dialog, null);
	    
	    final ListView list = (ListView)contents.findViewById(R.id.cargoList);
	    
	    final CargoAdapter adapter = new CargoAdapter(getActivity(), mGS.getCargo(), true);
		list.setAdapter(adapter);
		
		final TextView total = (TextView)contents.findViewById(R.id.totalPrice);
		total.setText(mGS.getCargo().getTotalPrice()+"$");
	    
		
		((Button)contents.findViewById(R.id.sellAll)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				mGS.addMoney(mGS.getCargo().getTotalPrice());
				mGS.getCargo().clear();
				
				adapter.notifyDataSetChanged();
				list.invalidateViews();
				total.setText(mGS.getCargo().getTotalPrice()+"$");
			}
			
		});
		
	    builder.setView(contents)
	    // Add action buttons
       .setPositiveButton("Fermer", null);
	    
	    return builder.create();
	}	
}