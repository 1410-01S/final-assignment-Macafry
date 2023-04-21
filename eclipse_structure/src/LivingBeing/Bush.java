package LivingBeing;

import java.util.ArrayList;
import java.util.Random;

import Coordinate2D.Point2D;
import Simulation.Environment;

public class Bush extends LivingBeing<Bush> {
	private float poisonRate;
	private int berryGrowthRate;
	private ArrayList<Berry> berries;

	public Bush(float poisonRate, int berryGrowthRate, Point2D location) {
		super(location);
		this.poisonRate = poisonRate;
		this.berryGrowthRate = berryGrowthRate;
		this.berries = new ArrayList<Berry>();

	}

	public Bush(Random personality, Point2D location, float poisonRate, int berryGrowthRate) {
		super(personality, location);
		this.poisonRate = poisonRate;
		this.berryGrowthRate = berryGrowthRate;
		this.berries = new ArrayList<Berry>();

	}

	public int berryCount() {
		return berries.size();
	}

	private void growBerry() {
		// determining values for new berry
		boolean isPoisonous = this.personality.nextFloat() > poisonRate;
		int nutrition = this.personality.nextInt(3) + 1;

		// adding berry to bush
		this.berries.add(new Berry(nutrition, isPoisonous));
	}

	public Berry[] looseBerries(int amount) {

		if (this.berries.size() == 0)
			return null;

		ArrayList<Berry> lostBerries = new ArrayList<Berry>();

		for (int i = 0; i < amount && this.berries.size() > 0; i++) {
			// transfer the lost berry from the bush into the pile of lost berries
			lostBerries.add(this.berries.get(0));
			this.berries.remove(0);
		}

		Berry[] ret = new Berry[lostBerries.size()];
		lostBerries.toArray(ret);

		return ret;
	}

	@Override
	public void update() {
		super.update();
		int berriesGrown = this.berryGrowthRate + this.personality.nextInt(3) - 1;
		for (int i = 0; i < berriesGrown; i++) {
			this.growBerry();
		}
	}

	public Bush randomInstance(Random personality, Environment env) {
		Random _personality = new Random(personality.nextInt());
		int _growthRate = personality.nextInt(5) + 3;
		float _poisonRate = 0.4f * personality.nextFloat() + 0.3f;

		float _x_loc = (env.maxX - env.minX) * personality.nextFloat() - env.minX;
		float _y_loc = (env.maxY - env.minY) * personality.nextFloat() - env.minY;
		Point2D _loc = new Point2D(_x_loc, _y_loc);

		return new Bush(_personality, _loc, _poisonRate, _growthRate);
	}

}
