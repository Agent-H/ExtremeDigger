package com.breakingsoft.extremedigger;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class CargoDialog extends PausingDialog{
	private Cargo mCargo;
	
	public void setCargo(Cargo cargo){
		mCargo = cargo;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreateDialog(savedInstanceState);
		
   	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    View contents = inflater.inflate(R.layout.cargo_dialog, null);
	    
	    ListView list = (ListView)contents.findViewById(R.id.cargoList);
	    
	    CargoAdapter adapter = new CargoAdapter(getActivity(), mCargo, false);
		list.setAdapter(adapter);
		
		TextView load = (TextView)contents.findViewById(R.id.cargoLoadPercent);
		load.setText(mCargo.getLoadPercent()+" %");
		
	    builder.setView(contents)
	    // Add action buttons
       .setPositiveButton("Ok", null);
	    
	    //builder.setOnDismissListener(this);
	    
	    return builder.create();
	}
}
