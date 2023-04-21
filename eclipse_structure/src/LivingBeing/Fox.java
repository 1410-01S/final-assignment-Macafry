package LivingBeing;

import java.util.Random;

import Coordinate2D.Point2D;
import Coordinate2D.Vector2D;
import Other.Clamp;
import Simulation.Environment;

public class Fox extends Animal<Fox> {
	public final static float huntPerAgg = 0.05f;
	public final static float wanderPerAgg = 0.35f;
	public final static int minReproduceAge = 4;
	public final static int maxReproduceAge = 30;

	private int aggresiveness;

	public Fox(Point2D home, int aggresiveness) {
		super(home);
		this.aggresiveness = aggresiveness;
	}

	public Fox(Random personality, Point2D home, int aggresiveness) {
		super(personality, home);
		this.aggresiveness = aggresiveness;
	}

	public int getAggresiveness() {
		return this.aggresiveness;
	}

	@Override
	public Fox[] tryReproduce(Fox partner) {

		if (!this.isCompatible(partner))
			return null;
		// TODO Auto-generated method stub

		int children = this.personality.nextInt(3) + 2;
		Fox[] pups = new Fox[children];

		// determining offspring parameters;
		for (int i = 0; i < pups.length; i++) {

			int childSeed = this.personality.nextInt();
			Random childPersonality = new Random(childSeed);

			int rawAggresive = (this.aggresiveness + partner.getAggresiveness()) / 2 +
					childPersonality.nextInt(7) - 3;

			int childAggr = Clamp.clamp(rawAggresive, 0, 100);

			Point2D childHome = Point2D.midpoint(this.location, partner.location);

			pups[i] = new Fox(childPersonality, childHome, childAggr);

		}

		// set reproduction cool down for both parents
		this.setRepCoolDown(2 * children);
		partner.setRepCoolDown(2 * children);

		this.decrementHunger(10 * children);
		partner.decrementHunger(10 * children);

		return pups;
	}

	@Override
	protected Point2D getWanderLocation() {
		// Foxes simply move in a random direction
		float randomAngle = 2 * (float) Math.PI * personality.nextFloat();
		// squaring it yields a uniform distribution over a circle
		// cubing it makes it more likely to be near the edges of a circle
		float rand4magnitude = (float) Math.pow(personality.nextFloat(), 3);
		float magnitude = this.aggresiveness * wanderPerAgg * rand4magnitude;

		Vector2D displacement = Vector2D.unitVector(randomAngle).scale(magnitude);
		return this.location.displace(displacement);
	}

	public Bunny hunt(Bunny prey) {
		prey.flee(this.location);
		double distanceToPrey = this.location.distance(prey.location);
		float coverableDistance = this.aggresiveness * huntPerAgg;

		this.health -= (int) (distanceToPrey / 2);

		if (distanceToPrey < coverableDistance) {
			this.move(prey.location);
			return prey;
		}

		return null;
	}

	public void eat(Bunny prey) {
		this.hunger += prey.getHealth() / 2;
	}

	@Override
	public void update() {
		super.update();
		if (this.age == minReproduceAge)
			this.inReproduceAge = true;
		if (this.age == maxReproduceAge + 1)
			this.inReproduceAge = false;

		if (this.age > maxReproduceAge && this.aggresiveness > 20) {
			this.aggresiveness--;
		}
	}

	@Override
	public Fox randomInstance(Random personality, Environment env) {
		Random _personality = new Random(personality.nextInt());
		int _aggr = personality.nextInt(81) + 10;

		float _x_loc = (env.maxX - env.minX) * personality.nextFloat() - env.minX;
		float _y_loc = (env.maxY - env.minY) * personality.nextFloat() - env.minY;
		Point2D _loc = new Point2D(_x_loc, _y_loc);

		return new Fox(_personality, _loc, _aggr);
	}

}
