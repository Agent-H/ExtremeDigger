package com.agenth.extremedigger;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;

import com.agenth.engine.physics.WorldPhysic;
import com.agenth.extremedigger.R;

/**
 * Donne des informations sur les matériaux.
 * Format d'un bloc codé sur un int :
 * 
 * [ COLLISION (1), BREAKABLE (1), LIBRE (6), DENSITY (8), MATERIAL (8), MODIFIER (8) ]
 */
public class MaterialBank {
	public static final int BREAKABLE_MASK = 0x20000000;
	
	public static final int DENSITY_MASK = 0x00ff0000;
	public static final int MATERIAL_MASK = 0x0000ff00;
	public static final int DRAWABLE_MASK = 0x0000ffff;
	
	public static final int MODIFIERS_MASK = 0x000000ff;
	
	public static final int MATERIAL_VIDE = 0x00000000,
							MATERIAL_TERRE = 0x00000100,
							MATERIAL_LEAD = 0x00000200,
							MATERIAL_COPPER = 0x00000300,
							MATERIAL_ALU = 0x00000400,
							MATERIAL_GOLD = 0x00000500,
							MATERIAL_RUBY = 0x00000600,
							MATERIAL_SAPHIR = 0x00000700,
							MATERIAL_URANIUM = 0x00000800,
							MATERIAL_AMETHYST = 0x00000900,
							MATERIAL_MIN = 0x00000100,
							MATERIAL_MAX = 0x00000900,
							NB_MATERIALS = 9;
	
	public static final int 
			TYPE_TERRE = MATERIAL_TERRE | WorldPhysic.COLLISION_MASK,
			TYPE_COPPER = MATERIAL_COPPER | WorldPhysic.COLLISION_MASK,
			TYPE_LEAD = MATERIAL_LEAD | WorldPhysic.COLLISION_MASK,
			TYPE_ALU = MATERIAL_ALU | WorldPhysic.COLLISION_MASK,
			TYPE_GOLD = MATERIAL_GOLD | WorldPhysic.COLLISION_MASK,
			TYPE_RUBY = MATERIAL_RUBY | WorldPhysic.COLLISION_MASK,
			TYPE_SAPHIR = MATERIAL_SAPHIR | WorldPhysic.COLLISION_MASK,
			TYPE_URANIUM = MATERIAL_URANIUM | WorldPhysic.COLLISION_MASK,
			TYPE_AMETHYST = MATERIAL_AMETHYST | WorldPhysic.COLLISION_MASK,
			TYPE_TERRE1L = MATERIAL_TERRE + 0x01,
			TYPE_TERRE2L = MATERIAL_TERRE + 0x02,
			TYPE_TERRE3L = MATERIAL_TERRE + 0x03,
			TYPE_TERRE1R = MATERIAL_TERRE + 0x11,
			TYPE_TERRE2R = MATERIAL_TERRE + 0x12,
			TYPE_TERRE3R = MATERIAL_TERRE + 0x13,
			TYPE_TERRE1B = MATERIAL_TERRE + 0x31,
			TYPE_TERRE2B = MATERIAL_TERRE + 0x32,
			TYPE_TERRE3B = MATERIAL_TERRE + 0x33,
			TYPE_LEAD1L = MATERIAL_LEAD + 0x01,
			TYPE_LEAD2L = MATERIAL_LEAD + 0x02,
			TYPE_LEAD3L = MATERIAL_LEAD + 0x03,
			TYPE_LEAD1R = MATERIAL_LEAD + 0x11,
			TYPE_LEAD2R = MATERIAL_LEAD + 0x12,
			TYPE_LEAD3R = MATERIAL_LEAD + 0x13,
			TYPE_LEAD1B = MATERIAL_LEAD + 0x31,
			TYPE_LEAD2B = MATERIAL_LEAD + 0x32,
			TYPE_LEAD3B = MATERIAL_LEAD + 0x33,
			TYPE_ALU1L = MATERIAL_ALU + 0x01,
			TYPE_ALU2L = MATERIAL_ALU + 0x02,
			TYPE_ALU3L = MATERIAL_ALU + 0x03,
			TYPE_ALU1R = MATERIAL_ALU + 0x11,
			TYPE_ALU2R = MATERIAL_ALU + 0x12,
			TYPE_ALU3R = MATERIAL_ALU + 0x13,
			TYPE_ALU1B = MATERIAL_ALU + 0x31,
			TYPE_ALU2B = MATERIAL_ALU + 0x32,
			TYPE_ALU3B = MATERIAL_ALU + 0x33,
			TYPE_COPPER1L = MATERIAL_COPPER + 0x01,
			TYPE_COPPER2L = MATERIAL_COPPER + 0x02,
			TYPE_COPPER3L = MATERIAL_COPPER + 0x03,
			TYPE_COPPER1R = MATERIAL_COPPER + 0x11,
			TYPE_COPPER2R = MATERIAL_COPPER + 0x12,
			TYPE_COPPER3R = MATERIAL_COPPER + 0x13,
			TYPE_COPPER1B = MATERIAL_COPPER + 0x31,
			TYPE_COPPER2B = MATERIAL_COPPER + 0x32,
			TYPE_COPPER3B = MATERIAL_COPPER + 0x33,
			TYPE_GOLD1L = MATERIAL_GOLD + 0x01,
			TYPE_GOLD2L = MATERIAL_GOLD + 0x02,
			TYPE_GOLD3L = MATERIAL_GOLD + 0x03,
			TYPE_GOLD1R = MATERIAL_GOLD + 0x11,
			TYPE_GOLD2R = MATERIAL_GOLD + 0x12,
			TYPE_GOLD3R = MATERIAL_GOLD + 0x13,
			TYPE_GOLD1B = MATERIAL_GOLD + 0x31,
			TYPE_GOLD2B = MATERIAL_GOLD + 0x32,
			TYPE_GOLD3B = MATERIAL_GOLD + 0x33,
			TYPE_VIDE = 0x00000000,
			TYPE_VIDE1TOP = 0x00000001,
			TYPE_VIDE1LEFT = 0x00000002,
			TYPE_VIDE1BOTTOM = 0x00000003,
			TYPE_VIDE1RIGHT = 0x00000004,
			TYPE_VIDE2TOPLEFT = 0x00000005,
			TYPE_VIDE2TOPRIGHT = 0x00000006,
			TYPE_VIDE2BOTTOMLEFT = 0x00000007,
			TYPE_VIDE2BOTTOMRIGHT = 0x00000008,
			TYPE_VIDE2HORIZONTAL = 0x00000009,
			TYPE_VIDE2VERTICAL = 0x0000000A,
			TYPE_VIDE3TOP = 0x0000000B,
			TYPE_VIDE3LEFT = 0X0000000C,
			TYPE_VIDE3BOTTOM = 0x0000000D,
			TYPE_VIDE3RIGHT = 0x0000000E,
			TYPE_VIDE4 = 0x0000000F;

	private static final String[] MATERIAL_NAMES = {
		"dirt", 
		"lead",
		"copper", 
		"aluminium", 
		"gold", 
		"ruby", 
		"saphir", 
		"uranium", 
		"amethyst"
	};
	
	private static final int[] MATERIAL_PRICES = {
		0, 20, 50, 100, 500, 1000, 5000, 10000, 60000
	};
	
	private static final int[] MATERIAL_DRAWABLE_RES = {
		R.drawable.terre,
		R.drawable.lead,
		R.drawable.copper,
		R.drawable.alu,
		R.drawable.gold,
		R.drawable.ruby,
		R.drawable.saphir,
		R.drawable.uranium,
		R.drawable.amethyst
	};
	
	private static SparseArray<Drawable> mDrawables = new SparseArray<Drawable>(74);
	
	private static boolean initialized = false;
	
	private MaterialBank(){
		
	}
	
	public static void init(Context ctx){
		
		if(initialized)
			return;
		
		initialized = true;
		
		Resources res = ctx.getResources();
		
		mDrawables.append(TYPE_TERRE & DRAWABLE_MASK, res.getDrawable(R.drawable.terre));
		mDrawables.append(TYPE_VIDE & DRAWABLE_MASK, res.getDrawable(R.drawable.trou));
		mDrawables.append(TYPE_VIDE1RIGHT & DRAWABLE_MASK, res.getDrawable(R.drawable.trou1));
		mDrawables.append(TYPE_VIDE1BOTTOM  & DRAWABLE_MASK, new BitmapDrawable(res, rotateBitmap(90,BitmapFactory.decodeResource(res, R.drawable.trou1))));
		mDrawables.append(TYPE_VIDE1LEFT  & DRAWABLE_MASK, new BitmapDrawable(res, rotateBitmap(180,BitmapFactory.decodeResource(res, R.drawable.trou1))));
		mDrawables.append(TYPE_VIDE1TOP  & DRAWABLE_MASK, new BitmapDrawable(res, rotateBitmap(270,BitmapFactory.decodeResource(res, R.drawable.trou1))));
		mDrawables.append(TYPE_VIDE2HORIZONTAL  & DRAWABLE_MASK, res.getDrawable(R.drawable.trou2o));
		mDrawables.append(TYPE_VIDE2VERTICAL  & DRAWABLE_MASK, new BitmapDrawable(res, rotateBitmap(90,BitmapFactory.decodeResource(res, R.drawable.trou2o))));
		mDrawables.append(TYPE_VIDE2TOPRIGHT  & DRAWABLE_MASK, res.getDrawable(R.drawable.trou2c));
		mDrawables.append(TYPE_VIDE2BOTTOMRIGHT  & DRAWABLE_MASK, new BitmapDrawable(res, rotateBitmap(90,BitmapFactory.decodeResource(res, R.drawable.trou2c))));
		mDrawables.append(TYPE_VIDE2BOTTOMLEFT  & DRAWABLE_MASK, new BitmapDrawable(res, rotateBitmap(180,BitmapFactory.decodeResource(res, R.drawable.trou2c))));
		mDrawables.append(TYPE_VIDE2TOPLEFT  & DRAWABLE_MASK, new BitmapDrawable(res, rotateBitmap(270,BitmapFactory.decodeResource(res, R.drawable.trou2c))));
		mDrawables.append(TYPE_VIDE3BOTTOM  & DRAWABLE_MASK, res.getDrawable(R.drawable.trou3));
		mDrawables.append(TYPE_VIDE3LEFT  & DRAWABLE_MASK, new BitmapDrawable(res, rotateBitmap(90,BitmapFactory.decodeResource(res, R.drawable.trou3))));
		mDrawables.append(TYPE_VIDE3TOP  & DRAWABLE_MASK, new BitmapDrawable(res, rotateBitmap(180,BitmapFactory.decodeResource(res, R.drawable.trou3))));
		mDrawables.append(TYPE_VIDE3RIGHT  & DRAWABLE_MASK, new BitmapDrawable(res, rotateBitmap(270,BitmapFactory.decodeResource(res, R.drawable.trou3))));
		mDrawables.append(TYPE_TERRE1B  & DRAWABLE_MASK, res.getDrawable(R.drawable.terre1));
		mDrawables.append(TYPE_TERRE1L  & DRAWABLE_MASK, new BitmapDrawable(res, rotateBitmap(90,BitmapFactory.decodeResource(res, R.drawable.terre1))));
		mDrawables.append(TYPE_TERRE1R  & DRAWABLE_MASK, new BitmapDrawable(res, rotateBitmap(270,BitmapFactory.decodeResource(res, R.drawable.terre1))));
		mDrawables.append(TYPE_TERRE2B  & DRAWABLE_MASK, res.getDrawable(R.drawable.terre2));
		mDrawables.append(TYPE_TERRE2L  & DRAWABLE_MASK, new BitmapDrawable(res, rotateBitmap(90,BitmapFactory.decodeResource(res, R.drawable.terre2))));
		mDrawables.append(TYPE_TERRE2R  & DRAWABLE_MASK, new BitmapDrawable(res, rotateBitmap(270,BitmapFactory.decodeResource(res, R.drawable.terre2))));
		mDrawables.append(TYPE_TERRE3B  & DRAWABLE_MASK, res.getDrawable(R.drawable.terre3));
		mDrawables.append(TYPE_TERRE3L  & DRAWABLE_MASK, new BitmapDrawable(res, rotateBitmap(90,BitmapFactory.decodeResource(res, R.drawable.terre3))));
		mDrawables.append(TYPE_TERRE3R  & DRAWABLE_MASK, new BitmapDrawable(res, rotateBitmap(270,BitmapFactory.decodeResource(res, R.drawable.terre3))));
		mDrawables.append(TYPE_COPPER  & DRAWABLE_MASK, res.getDrawable(R.drawable.copper));
		mDrawables.append(TYPE_COPPER1B  & DRAWABLE_MASK, res.getDrawable(R.drawable.copper1b));
		mDrawables.append(TYPE_COPPER2B  & DRAWABLE_MASK, res.getDrawable(R.drawable.copper2b));
		mDrawables.append(TYPE_COPPER3B  & DRAWABLE_MASK, res.getDrawable(R.drawable.copper3b));
		mDrawables.append(TYPE_COPPER1R  & DRAWABLE_MASK, res.getDrawable(R.drawable.copper1r));
		mDrawables.append(TYPE_COPPER2R  & DRAWABLE_MASK, res.getDrawable(R.drawable.copper2r));
		mDrawables.append(TYPE_COPPER3R  & DRAWABLE_MASK, res.getDrawable(R.drawable.copper3r));
		mDrawables.append(TYPE_COPPER1L  & DRAWABLE_MASK, res.getDrawable(R.drawable.copper1l));
		mDrawables.append(TYPE_COPPER2L  & DRAWABLE_MASK, res.getDrawable(R.drawable.copper2l));
		mDrawables.append(TYPE_COPPER3L  & DRAWABLE_MASK, res.getDrawable(R.drawable.copper3l));
		mDrawables.append(TYPE_ALU  & DRAWABLE_MASK, res.getDrawable(R.drawable.alu));
		mDrawables.append(TYPE_ALU1B  & DRAWABLE_MASK, res.getDrawable(R.drawable.alu1b));
		mDrawables.append(TYPE_ALU2B  & DRAWABLE_MASK, res.getDrawable(R.drawable.alu2b));
		mDrawables.append(TYPE_ALU3B  & DRAWABLE_MASK, res.getDrawable(R.drawable.alu3b));
		mDrawables.append(TYPE_ALU1R  & DRAWABLE_MASK, res.getDrawable(R.drawable.alu1r));
		mDrawables.append(TYPE_ALU2R  & DRAWABLE_MASK, res.getDrawable(R.drawable.alu2r));
		mDrawables.append(TYPE_ALU3R  & DRAWABLE_MASK, res.getDrawable(R.drawable.alu3r));
		mDrawables.append(TYPE_ALU1L  & DRAWABLE_MASK, res.getDrawable(R.drawable.alu1l));
		mDrawables.append(TYPE_ALU2L  & DRAWABLE_MASK, res.getDrawable(R.drawable.alu2l));
		mDrawables.append(TYPE_ALU3L  & DRAWABLE_MASK, res.getDrawable(R.drawable.alu3l));
		mDrawables.append(TYPE_LEAD  & DRAWABLE_MASK, res.getDrawable(R.drawable.lead));
		mDrawables.append(TYPE_LEAD1B  & DRAWABLE_MASK, res.getDrawable(R.drawable.lead1b));
		mDrawables.append(TYPE_LEAD2B  & DRAWABLE_MASK, res.getDrawable(R.drawable.lead2b));
		mDrawables.append(TYPE_LEAD3B  & DRAWABLE_MASK, res.getDrawable(R.drawable.lead3b));
		mDrawables.append(TYPE_LEAD1R  & DRAWABLE_MASK, res.getDrawable(R.drawable.lead1r));
		mDrawables.append(TYPE_LEAD2R  & DRAWABLE_MASK, res.getDrawable(R.drawable.lead2r));
		mDrawables.append(TYPE_LEAD3R  & DRAWABLE_MASK, res.getDrawable(R.drawable.lead3r));
		mDrawables.append(TYPE_LEAD1L  & DRAWABLE_MASK, res.getDrawable(R.drawable.lead1l));
		mDrawables.append(TYPE_LEAD2L  & DRAWABLE_MASK, res.getDrawable(R.drawable.lead2l));
		mDrawables.append(TYPE_LEAD3L  & DRAWABLE_MASK, res.getDrawable(R.drawable.lead3l));
		mDrawables.append(TYPE_GOLD  & DRAWABLE_MASK, res.getDrawable(R.drawable.gold));
		mDrawables.append(TYPE_GOLD1B  & DRAWABLE_MASK, res.getDrawable(R.drawable.gold1b));
		mDrawables.append(TYPE_GOLD2B  & DRAWABLE_MASK, res.getDrawable(R.drawable.gold2b));
		mDrawables.append(TYPE_GOLD3B  & DRAWABLE_MASK, res.getDrawable(R.drawable.gold3b));
		mDrawables.append(TYPE_GOLD1R  & DRAWABLE_MASK, res.getDrawable(R.drawable.gold1r));
		mDrawables.append(TYPE_GOLD2R  & DRAWABLE_MASK, res.getDrawable(R.drawable.gold2r));
		mDrawables.append(TYPE_GOLD3R  & DRAWABLE_MASK, res.getDrawable(R.drawable.gold3r));
		mDrawables.append(TYPE_GOLD1L  & DRAWABLE_MASK, res.getDrawable(R.drawable.gold1l));
		mDrawables.append(TYPE_GOLD2L  & DRAWABLE_MASK, res.getDrawable(R.drawable.gold2l));
		mDrawables.append(TYPE_GOLD3L  & DRAWABLE_MASK, res.getDrawable(R.drawable.gold3l));
		
		mDrawables.append(MATERIAL_TERRE, res.getDrawable(R.drawable.terre));
		mDrawables.append(MATERIAL_LEAD, res.getDrawable(R.drawable.lead));
		mDrawables.append(MATERIAL_ALU, res.getDrawable(R.drawable.alu));
		mDrawables.append(MATERIAL_COPPER, res.getDrawable(R.drawable.copper));
		mDrawables.append(MATERIAL_GOLD, res.getDrawable(R.drawable.gold));
		mDrawables.append(MATERIAL_RUBY, res.getDrawable(R.drawable.ruby));
		mDrawables.append(MATERIAL_SAPHIR, res.getDrawable(R.drawable.saphir));
		mDrawables.append(MATERIAL_URANIUM, res.getDrawable(R.drawable.uranium));
		mDrawables.append(MATERIAL_AMETHYST, res.getDrawable(R.drawable.amethyst));
	}
	
	/**
	 * Converts a material data to an id in range 0 - 10. Useful if you want to store some 
	 * infos related to materials in an array. Note that "vide" is not considered as a material
	 * @param material
	 * @return The materials cleaned ID or -1 if operation fails
	 */
	public static int materialToId(int material){

		if( (material & ~MATERIAL_MASK) == 0 && material >= MATERIAL_MIN && material <= MATERIAL_MAX){
			return (material >> 8) -1;
		}
		
		return -1;
	}
	
	/**
	 * Does the exact opposite of materialToId : returns the material data with the specified id
	 * @param id
	 * @return Material data or -1 if operation fails
	 */
	public static int idToMaterial(int id){
		if(id >= 0 && id < NB_MATERIALS){
			return (id + 1) << 8;
		}
		
		return -1;
	}
	
	/**
	 * Returns the material name corresponding to the specified material data.
	 * Note that trying this with an empty bloc will return an empty string.
	 * @param material
	 * @return The material name or an empty string if there was an error
	 */
	public static String getMaterialName(int material){
		int id = materialToId(material);
		if(id != -1)
			return MATERIAL_NAMES[id];
		
		return "";
	}

	public static int getDrawableResId(int material){
		int id = materialToId(material);
		if(id != -1)
			return MATERIAL_DRAWABLE_RES[id];
		
		return R.drawable.terre;
	}
	
	public static Drawable getDrawable(int bloc){
		return mDrawables.get(bloc & DRAWABLE_MASK);
	}
	
	/**
	 * Returns price of the material
	 * @param material
	 */
	public static int getMaterialPrice(int material){
		int id = materialToId(material);
		
		if(id != -1)
			return MATERIAL_PRICES[id];
		
		return 0;
	}
	
	public static int getDensity(int bloc){
		return (bloc & DENSITY_MASK) >> 16;
	}
	
	public static int makeBloc(int material, int density){
		return material | (density << 16);
	}
	
	private static Bitmap rotateBitmap(int angle, Bitmap source){
		Bitmap result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas tempCanvas = new Canvas(result); 
		tempCanvas.rotate(angle, source.getWidth()/2, source.getHeight()/2);
		tempCanvas.drawBitmap(source, 0, 0, null);
		
		return result;
	}
}
