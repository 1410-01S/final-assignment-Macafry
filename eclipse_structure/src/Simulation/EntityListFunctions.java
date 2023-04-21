package Simulation;

import java.util.ArrayList;

import LivingBeing.*;

public class EntityListFunctions {
	public static ArrayList<Entity> getNearbyEntities(Entity target, ArrayList<Entity> entities, double maxDistance){
		ArrayList<Entity> nearbyEntities = new ArrayList<Entity>();
		
		for(Entity e : entities) {
			if(target.location.distance(e.location) <= maxDistance && !target.equals(e)) {
				nearbyEntities.add(e);
			}
		}
		
		return nearbyEntities;
	}
	
	// Generics did not want to cooperate for this
	public static ArrayList<Bunny> getBunnies(ArrayList<Entity> entities){
		ArrayList<Bunny> bunnies= new ArrayList<Bunny>();
		
		for(Entity e : entities) {
			if(e instanceof Bunny) {
				bunnies.add((Bunny)e);
			}
		}
		
		return bunnies;
	}
	
	public static ArrayList<Fox> getFoxes(ArrayList<Entity> entities){
		ArrayList<Fox> foxes = new ArrayList<Fox>();
		
		for(Entity e : entities) {
			if(e instanceof Fox) {
				foxes.add((Fox)e);
			}
		}
		
		return foxes;
	}
	
	public static ArrayList<Bush> getBushes(ArrayList<Entity> entities){
		ArrayList<Bush> bushes = new ArrayList<Bush>();
		
		for(Entity e : entities) {
			if(e instanceof Bush) {
				bushes.add((Bush)e);
			}
		}
		
		return bushes;
	}
	
	public static int getIndex(Entity target, ArrayList<Entity> entities) {
		
		for(int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			if(target.equals(e)) {
				return i;
			}
		}
		
		return -1;
	}
}
