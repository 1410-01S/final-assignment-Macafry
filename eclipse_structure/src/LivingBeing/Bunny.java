package LivingBeing;

import java.util.Random;

import Coordinate2D.Point2D;
import Coordinate2D.Vector2D;
import Other.Clamp;
import Simulation.Environment;

public class Bunny extends Animal<Bunny> {
	public final static float fleePerFear = 0.07f;
	public final static int minReproduceAge = 3;
	public final static int maxReproduceAge = 20;

	private int fertility, fear;

	public Bunny(Point2D home, int fertility, int fear) {
		super(home);
		this.fertility = fertility;
		this.fear = fear;
	}

	public Bunny(Random personality, Point2D home, int fertility, int fear) {
		super(personality, home);
		this.fertility = fertility;
		this.fear = fear;
	}

	public int getFertility() {
		return this.fertility;
	}

	public int getFear() {
		return this.fear;
	}

	@Override
	public Bunny[] tryReproduce(Bunny partner) {
		// if they can't reproduce, then no offsprings are produced
		if (!this.isCompatible(partner))
			return null;

		int children = (this.fertility + partner.getFertility());
		Bunny[] bunnies = new Bunny[children];

		// determining offspring parameters;
		for (int i = 0; i < bunnies.length; i++) {

			int childSeed = this.personality.nextInt();
			Random childPersonality = new Random(childSeed);

			int rawChildFert = (this.fertility + partner.getFertility()) / 2 +
					childPersonality.nextInt(4) - 1;

			int rawChildFear = (this.fertility + partner.getFertility()) / 2 +
					childPersonality.nextInt(7) - 3;

			int childFert = Clamp.clampLow(rawChildFert, 0);
			int childFear = Clamp.clamp(rawChildFear, 0, 100);

			Point2D childHome = Point2D.midpoint(this.location, partner.location);

			bunnies[i] = new Bunny(childPersonality, childHome, childFert, childFear);

		}

		// set reproduction cool down for both parents
		this.setRepCoolDown(2 + children / 3);
		partner.setRepCoolDown(2 + children / 3);

		this.decrementHunger(children);
		partner.decrementHunger(children);

		return bunnies;
	}

	@Override
	public Point2D getWanderLocation() {
		// bunnies are atracted to their homes
		Vector2D towardsHome = new Vector2D(this.location, this.home);

		// move in a random direction
		float randomAngle = 2 * (float) Math.PI * personality.nextFloat();
		Vector2D randomDir = Vector2D.unitVector(randomAngle);

		// get the final move direction
		Vector2D direction = towardsHome.unitVector()
				.scale(1.5f)
				.add(randomDir)
				.unitVector();

		// random value between -0.5 and 1.3
		double rnd = 1.8 * this.personality.nextDouble() - 0.5;

		double unitsMoved = towardsHome.magnitude() * (1 + rnd);

		// get the final destination
		return this.location.displace(direction.scale((float) unitsMoved));
	}

	public void flee(Point2D dangerLocation) {
		Vector2D badDirection = new Vector2D(this.location, dangerLocation);

		double unitsMoved = fleePerFear * fear / badDirection.magnitude();
		Vector2D getAway = badDirection.oppositeDirection()
				.unitVector()
				.scale((float) unitsMoved);

		Point2D safePoint_probably = this.location.displace(getAway);

		this.move(safePoint_probably);
	}

	public Berry[] gather(Bush foodSource) {
		int idealBerries = (100 - this.hunger) / 2 + 10;

		return foodSource.looseBerries(idealBerries);
	}

	public void eat(Berry b) {
		if (b.isPoisonous()) {
			this.health -= 3;
		} else {
			this.hunger += b.getNutrition();
		}
	}

	@Override
	public void update() {
		super.update();
		if (this.age == minReproduceAge)
			this.inReproduceAge = true;
		if (this.age == maxReproduceAge + 1)
			this.inReproduceAge = false;
	}

	@Override
	public Bunny randomInstance(Random personality, Environment env) {
		Random _personality = new Random(personality.nextInt());
		int _fert = personality.nextInt(5) + 3;
		int _fear = personality.nextInt(81) + 10;

		float _x_loc = (env.maxX - env.minX) * personality.nextFloat() - env.minX;
		float _y_loc = (env.maxY - env.minY) * personality.nextFloat() - env.minY;
		Point2D _loc = new Point2D(_x_loc, _y_loc);

		return new Bunny(_personality, _loc, _fert, _fear);
	}

}
