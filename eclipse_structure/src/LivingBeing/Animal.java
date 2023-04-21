package LivingBeing;

import java.util.Random;

import Coordinate2D.Point2D;
import Simulation.Environment;

// using generics to eliminate the chance of a fox mating with a rabbit and getting a sheep as an offspring
public abstract class Animal<TAnimal extends Animal<?>> extends LivingBeing<TAnimal> {
	protected int hunger, reproduceCoolDown;
	protected Point2D home;
	protected boolean inReproduceAge;
	public final char gender;

	protected Animal(Point2D home) {
		super(home);
		this.home = home;
		this.hunger = 80;
		this.reproduceCoolDown = 0;
		this.gender = this.personality.nextInt(2) == 1 ? 'M' : 'F';
	}

	protected Animal(Random personality, Point2D home) {
		super(personality, home);
		this.home = home;
		this.hunger = 80;
		this.reproduceCoolDown = 0;
		this.gender = this.personality.nextInt(2) == 1 ? 'M' : 'F';
	}

	public void setRepCoolDown(int time) {
		this.reproduceCoolDown = time;
	}

	public void decrementHunger(int hunger) {
		this.hunger -= hunger;
	}

	public boolean canReproduce() {
		return this.inReproduceAge && this.reproduceCoolDown == 0;
	}

	public boolean isCompatible(TAnimal partner) {

		return this.canReproduce() && partner.canReproduce() && this.gender != partner.gender;
	}

	public abstract TAnimal[] tryReproduce(TAnimal partner);

	protected abstract Point2D getWanderLocation();

	// Fake 'override' so that the compiler doesn't cry
	// ideally static
	public abstract TAnimal randomInstance(Random personality, Environment env);

	protected void move(Point2D targerLocation) {
		this.location = targerLocation;
	}

	public void wander() {
		this.move(this.getWanderLocation());
	}

	// partial logic for all animals
	@Override
	public void update() {
		super.update();
		this.hunger -= personality.nextInt(6) + 1;

		if (this.reproduceCoolDown > 0)
			this.reproduceCoolDown--;

		if (this.hunger < 50) {
			this.health -= 20;
		}

		this.wander();
	}

}
