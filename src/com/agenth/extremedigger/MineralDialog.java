package com.agenth.extremedigger;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MineralDialog extends PausingDialog{
	
	private GameState mGS;
	
	private TextView mTotal;
	
	
	public void setGameState(GameState gs){
		mGS = gs;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		
		Window w = dialog.getWindow();
		w.setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View contents = inflater.inflate(R.layout.mineral_dialog, null);
		dialog.setContentView(contents);
		
		final ListView list = (ListView)contents.findViewById(R.id.cargoList);
	    
	    final CargoAdapter adapter = new CargoAdapter(getActivity(), mGS.getCargo(), true);
		list.setAdapter(adapter);
		
		mTotal = (TextView)contents.findViewById(R.id.totalPrice);
		
		final MineralDialog that = this;
		
		((Button)contents.findViewById(R.id.done_button)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				that.dismiss();
			}
		});
		
		((Button)contents.findViewById(R.id.sellAll)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mGS.addMoney(mGS.getCargo().getTotalPrice());
				mGS.getCargo().clear();
				
				adapter.notifyDataSetChanged();
				list.invalidateViews();
				that.refresh();
			}
			
		});
		
		refresh();
	    
	    return dialog;
	}	
	
	
	private void refresh() {
		mTotal.setText(mGS.getCargo().getTotalPrice()+"$");
	}
}