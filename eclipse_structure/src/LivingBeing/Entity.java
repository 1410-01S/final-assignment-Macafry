package LivingBeing;

import Coordinate2D.Point2D;

// this class exists for the sole reason of the compiler not crying about 
// an arraylist of unparameterized LivingBeings
// aka ArrayList<LivingBeing> when it should be ArrayList<LivingBeing<[something]>>
public abstract class Entity {
	public Point2D location;
	protected String id;

	public String getID() {
		return this.id;
	}

	public boolean equals(Entity e) {
		return this.id.equals(e.getID());
	}

	public abstract void update();

	public abstract boolean shouldRemove();
}