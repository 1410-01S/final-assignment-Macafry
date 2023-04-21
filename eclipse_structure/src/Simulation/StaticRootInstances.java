package Simulation;

import java.util.Random;

import LivingBeing.*;

public class StaticRootInstances {
	
	public static Bunny rootBunny() {
		return new Bunny(new Random(0), null, 0 ,0);
	}
	
	public static Fox rootFox() {
		return new Fox(new Random(0), null, 0);
	}
	
	public static Bush rootBush() {
		return new Bush(new Random(0), null, 0, 0);
	}
}
