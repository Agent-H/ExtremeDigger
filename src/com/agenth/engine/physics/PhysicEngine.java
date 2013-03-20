package com.agenth.engine.physics;

import com.agenth.engine.core.Game;
import com.agenth.engine.core.GameModule;
import com.agenth.engine.util.LinkedList;
import com.agenth.engine.util.VectF;

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
			 * on va simplement tester tous les �l�ments entre eux. La premi�re boucle parcours chaque �l�ment, et la seconde
			 * fait les essais entre l'�l�ment courant et tout ceux qui n'ont pas encore �t� test�s avec lui.
			 * Aussi, il n'y a aucun moyen de pr�venir l'effet tunel, en bref, ce moteur physique pue la moule.
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
				 * Arriv� ici, on ne demandera plus son avis � la premi�re entit� de la liste.
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
