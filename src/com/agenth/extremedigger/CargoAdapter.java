package com.agenth.extremedigger;

import com.agenth.extremedigger.R;

import android.app.Activity;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CargoAdapter extends BaseAdapter{
	
	private SparseIntArray mMaterialCounts;
	private LayoutInflater mInflater;
	private boolean mDisplayPrice;
	private Cargo mCargo;
	
	public CargoAdapter(Activity act, Cargo cargo, boolean displayPrice){
		
		mInflater = act.getLayoutInflater();
		
		mDisplayPrice = displayPrice;
		
		mMaterialCounts = new SparseIntArray();
		
		mCargo = cargo;
		
		init();
		
	}
	
	@Override
	public void notifyDataSetChanged(){
		init();
		super.notifyDataSetChanged();
	}
	
	private void init(){
		
		mMaterialCounts.clear();
		
		int count;
		for(int i = 1 ; i < MaterialBank.NB_MATERIALS ; i++){
			int material = MaterialBank.idToMaterial(i);
			
			count = mCargo.getMineralCount(material);
			if(count > 0){
				mMaterialCounts.append(material, count);
			}
		}
	}
	/**
	 * R�cup�rer un item de la liste en fonction de sa position
	 * @param position - Position de l'item � r�cup�rer
	 * @return l'item r�cup�r�
	*/
	@Override
	public Object getItem(int position) {
		return mMaterialCounts.keyAt(position);
	}
	 
	/**
	 * R�cup�rer l'identifiant d'un item de la liste en fonction de sa position (plut�t utilis� dans le cas d'une
	 * base de donn�es, mais on va l'utiliser aussi)
	 * @param position - Position de l'item � r�cup�rer
	 * @return l'identifiant de l'item
	 * 
	*/
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		
		if(convertView == null){
			convertView  = mInflater.inflate(R.layout.cargo_item, null);
			
			holder = new ViewHolder();
			
			holder.name = (TextView) convertView.findViewById(R.id.cargoItemName);
		    holder.count = (TextView) convertView.findViewById(R.id.cargoItemCount);
		    holder.image = (ImageView) convertView.findViewById(R.id.cargoItemImage);
		    
		    convertView.setTag(holder);
			
		} else {
			
			holder = (ViewHolder) convertView.getTag();
		}
		
		int material = (Integer) getItem(position);
		
		holder.name.setText(MaterialBank.getMaterialName(material));
		holder.image.setImageResource(MaterialBank.getDrawableResId(material));
		
		if(mDisplayPrice){
			holder.count.setText(""+mMaterialCounts.get(material)+" x "+MaterialBank.getMaterialPrice(material)+"$");
		} else {
			holder.count.setText(""+mMaterialCounts.get(material));
		}
		return convertView;
	}

	@Override
	public int getCount() {
		return mMaterialCounts.size();
	}
	
	private class ViewHolder{
		public TextView name;
		public TextView count;
		public ImageView image;
	}

}
