package com.agenth.extremedigger;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class CargoDialog extends PausingDialog{
	private Cargo mCargo;
	
	public void setCargo(Cargo cargo){
		mCargo = cargo;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		
		Window w = dialog.getWindow();
		w.setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View contents = inflater.inflate(R.layout.cargo_dialog, null);
		dialog.setContentView(contents);
		
		ListView list = (ListView)contents.findViewById(R.id.cargoList);
	    
	    CargoAdapter adapter = new CargoAdapter(getActivity(), mCargo, false);
		list.setAdapter(adapter);
		
		
		TextView load = (TextView)contents.findViewById(R.id.cargoLoadPercent);
		load.setText(mCargo.getLoadPercent()+" %");
		
		final DialogFragment that = this;
		((Button)contents.findViewById(R.id.done_button)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				that.dismiss();
			}
		});
		
	    
	    return dialog;
	}
}
