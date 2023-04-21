package LivingBeing;

import java.util.Random;
import java.util.UUID;

import Coordinate2D.Point2D;
import Other.ClampPoint;
import Simulation.Environment;

public abstract class LivingBeing<TLivingBeing extends LivingBeing<?>> extends Entity {
	protected int age;
	protected int health;
	protected Random personality;

	protected LivingBeing(Point2D location) {
		this.age = 0;
		this.health = 100;
		this.location = location;
		// using the Universally Unique Identifier class to create id's
		this.id = UUID.randomUUID().toString();
		this.personality = new Random();
	}

	protected LivingBeing(Random personality, Point2D location) {
		this.age = 0;
		this.health = 100;
		this.location = location;
		// using the Universally Unique Identifier class to create id's
		this.id = UUID.randomUUID().toString();
		this.personality = personality;
	}

	// ideally static, but couldn't make it work
	public abstract TLivingBeing randomInstance(Random personality, Environment env);

	public int getAge() {
		return this.age;
	}

	public int getHealth() {
		return this.health;
	}

	public boolean shouldDie() {
		return health < 0;
	};

	@Override
	public boolean shouldRemove() {
		return this.shouldDie();
	};

	@Override
	public void update() {
		this.age++;
		if (age > 20) {
			int ageOver20 = age - 20;
			health -= ageOver20;
		}

		Environment env = Environment.getSingleton();
		this.location = ClampPoint.clamp(location, env.minX, env.minY, env.maxX, env.maxY);
	};
}
