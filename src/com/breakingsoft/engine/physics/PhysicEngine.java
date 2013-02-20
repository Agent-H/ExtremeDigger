package com.breakingsoft.engine.physics;

import com.breakingsoft.engine.core.Game;
import com.breakingsoft.engine.core.GameModule;
import com.breakingsoft.engine.util.LinkedList;
import com.breakingsoft.engine.util.VectF;

public class PhysicEngine extends GameModule
{
	public static final String MODULE_NAME = "Physic";
	
	private LinkedList<Physic> mComponents;
	
	private WorldPhysic mWorld;
	
	private VectF mGravity = new VectF();
	
	public PhysicEngine(Game game) {
		super(game, MODULE_NAME);
		
		mComponents = new LinkedList<Physic>();
	}

	@Override
	public void step(long time) {
		if(!game().isPaused()){
	
			/*
			 * Il faut tester toutes les collisions possibles. On n'utilise aucun algorithme de partition de l'espace,
			 * on va simplement tester tous les éléments entre eux. La première boucle parcours chaque élément, et la seconde
			 * fait les essais entre l'élément courant et tout ceux qui n'ont pas encore été testés avec lui.
			 * Aussi, il n'y a aucun moyen de prévenir l'effet tunel, en bref, ce moteur physique pue la moule.
			 */
			LinkedList<Physic>.Entry entry1 = mComponents.first();
			while(entry1 != null){
				Physic phy1 = entry1.data();
				
				if(mWorld != null){
					mWorld.checkAndSolveCollision(phy1);
				}
				
				LinkedList<Physic>.Entry entry2 = entry1.next();
				while(entry2 != null){		
					Physic phy2 = entry2.data();
					
					phy1.checkCollisionAndSolve(phy2);
					entry2 = entry2.next();
				}
				entry1 = entry1.next();
				
				/*
				 * Arrivé ici, on ne demandera plus son avis à la première entité de la liste.
				 * On en profite pour la faire bouger. 
				 */
				
				phy1.step();
			}
		}
	}
	
	public void addComponent(Physic c){
		c.setLinkedListEntry(mComponents.insert(c));

		//Apply gravity
		c.addForce(mGravity);
	}
	
	public void setWorld(WorldPhysic world){
		mWorld = world;
	}
	
	public void setGravity(VectF gravity){
		mGravity.x = gravity.x;
		mGravity.y = gravity.y;
	}
}
