package com.breakingsoft.engine.graphics;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.breakingsoft.engine.components.TwoDimention;
import com.breakingsoft.engine.core.Game;
import com.breakingsoft.engine.core.GameModule;
import com.breakingsoft.engine.util.LinkedList;
/**
 * Moteur graphique.
 *
 */
public class GraphicEngine extends GameModule implements Callback{
	
	public static final String MODULE_NAME = "Graphics";
	
	private Rect mViewport;
	
	private SurfaceHolder mHolder;
	
	private int mScreenHeight, mScreenWidth;
	
	private float mDimFactor;
	
	//Layers of graphic objects
	SparseArray<LinkedList<Graphic>> mLayers;
	
	WorldGraphic mWorld;
	
	/**
	 * Entity to center in view port
	 */
	TwoDimention mTarget;
	
	public GraphicEngine(Game game, SurfaceView view){
		this(game, view, 800);
	}
	
	public GraphicEngine(Game game, SurfaceView view, int width)
	{
		super(game, MODULE_NAME);
		
		mViewport = new Rect();
		setViewportWidth(width);
		
		mHolder = view.getHolder();
	    mHolder.addCallback(this);
		
		
		mLayers = new SparseArray<LinkedList<Graphic>>();
	}
	
	public void setViewportWidth(int width){
		
		mViewport.right = mViewport.left + width;
		computeViewportHeight();
		
		computeDimFactor();
	}
	
	public void computeViewportHeight(){
		if(mScreenWidth != 0)
			mViewport.bottom = mViewport.top + mScreenHeight*mViewport.width()/mScreenWidth;
	}


	private void doDraw(Canvas c) {
		
		if(mWorld != null){
			mWorld.draw(c, mViewport);
		}
		
		//Iterates over different layers
		int key = 0, layersSize = mLayers.size();
		for(int i = 0 ; i < layersSize ; i++) {
			
		   key = mLayers.keyAt(i);
		 
		   LinkedList<Graphic>.Entry entry = mLayers.get(key).first();

			while(entry != null){
				
				Graphic comp = entry.data();

				comp.draw(c, mViewport);
				
				entry = entry.next();
			}
			
		}
		
		
	}
	
	/**
	 * Converts distances from game scale to graphic scale
	 * @param dim
	 * @return
	 */
	public int mapDimention(int dim){
		return (int) (dim*mDimFactor);
	}
	
	public void mapArea(Rect area){
		area.left = mapDimention(area.left);
		area.right = mapDimention(area.right);
		area.top = mapDimention(area.top);
		area.bottom = mapDimention(area.bottom);
	}
	
	private void computeDimFactor(){
		mDimFactor = (float)mScreenWidth / mViewport.width();
	}

	@Override
	public void step(long time) {
		
		//Refreshing viewport
		if(mTarget != null){
			mViewport.offsetTo((int)mTarget.centerX()-mViewport.width()/2, (int)mTarget.centerY() - mViewport.height()/2);
			mWorld.adjustViewport(mViewport);
		}
		
		if(mHolder.getSurface().isValid()){
			Canvas canvas = mHolder.lockCanvas();
			if(canvas != null){
				doDraw(canvas);
				mHolder.unlockCanvasAndPost(canvas);
			}
		}
	}

	public void addComponent(Graphic c) {
		int layer = c.getLayer();
		
		LinkedList<Graphic> list = mLayers.get(layer);
		if(list == null){
			list = new LinkedList<Graphic>();
			mLayers.append(layer, list);
		}
		
		c.setLinkedListEntry(list.insert(c));
	}
	
	public void setTarget(TwoDimention target){
		mTarget = target;
	}
	
	void setWorld(WorldGraphic world){
		mWorld = world;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		mScreenWidth = width;
		mScreenHeight = height;
		
		computeViewportHeight();
		computeDimFactor();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}
}
