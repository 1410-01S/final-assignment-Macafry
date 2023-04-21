package Simulation;

import java.util.ArrayList;
import java.util.Random;

import LivingBeing.*;

// using the singleton pattern because I feel like LivingBeing instances shouldn't have access to the environment
// but the class living being needs a reference to this class to make sure entities don't go out of bounds.
public class Environment {
	public final float minX, minY, maxX, maxY;
	private float bushSpawnRate;
	private ArrayList<Entity> entities;
	private Random chaos;

	public static Environment singleton;

	private Environment(float minX, float minY, float maxX, float maxY,
			float bushSpawnRate, int randSeed,
			int initialBunnies, int initialFoxes, int initialBushes) {
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
		this.bushSpawnRate = bushSpawnRate;

		this.entities = new ArrayList<Entity>();
		this.chaos = new Random(randSeed);

		Bunny rootBunny = StaticRootInstances.rootBunny();
		for (int i = 0; i < initialBunnies; i++) {
			this.entities.add(rootBunny.randomInstance(this.chaos, this));
		}

		Fox rootFox = StaticRootInstances.rootFox();
		for (int i = 0; i < initialFoxes; i++) {
			this.entities.add(rootFox.randomInstance(this.chaos, this));
		}

		Bush rootBush = StaticRootInstances.rootBush();
		for (int i = 0; i < initialBushes; i++) {
			this.entities.add(rootBush.randomInstance(this.chaos, this));
		}

	}

	public static Environment getSingleton() {
		if (singleton == null)
			throw new NullPointerException("Singleton hasn't been initialized");

		return singleton;
	}

	public static void setSingleton(float squareRadius, float bushSpawnRate, int randSeed,
			int initialBunnies, int initialFoxes, int initialBushes) {

		singleton = new Environment(-squareRadius, -squareRadius, squareRadius, squareRadius,
				bushSpawnRate, randSeed, initialBunnies, initialFoxes, initialBushes);
		singleton.updateAll();
	}

	/* Tick components */
	private void spawnBushes() {
		int bushesSpawned = (int) (this.bushSpawnRate * (1 + 0.4f * this.chaos.nextFloat() - 0.2f));

		Bush rootBush = StaticRootInstances.rootBush();
		for (int i = 0; i < bushesSpawned; i++) {
			this.entities.add(rootBush.randomInstance(this.chaos, this));
		}
	}

	private void huntGather() {
		ArrayList<Bunny> bunnies = EntityListFunctions.getBunnies(this.entities);
		for (Bunny bun : bunnies) {
			this.gather(bun);
		}

		ArrayList<Fox> foxes = EntityListFunctions.getFoxes(this.entities);
		for (Fox fox : foxes) {
			this.hunt(fox);
		}

	}

	private void hunt(Fox fox) {
		for (int huntAttempts = 0; huntAttempts < 3; huntAttempts++) {
			// get nearby bunnies
			ArrayList<Bunny> possiblePreys = EntityListFunctions.getBunnies(
					EntityListFunctions.getNearbyEntities(fox, this.entities, Fox.huntPerAgg * 100));

			if (possiblePreys.size() == 0) {
				fox.wander();
				continue;
			}
			// choose prey
			int randomPreyIndex = this.chaos.nextInt(possiblePreys.size());
			Bunny prey = possiblePreys.get(randomPreyIndex);

			// hunt
			Bunny food = fox.hunt(prey);

			// eat & kill bunny
			if (food != null) {
				fox.eat(food);
				int foodIndex = EntityListFunctions.getIndex(food, this.entities);
				this.entities.remove(foodIndex);
			}
		}
	}

	private void gather(Bunny bun) {
		for (int huntAttempts = 0; huntAttempts < 5; huntAttempts++) {
			// get nearby bunnies
			ArrayList<Bush> possibleTarget = EntityListFunctions.getBushes(
					EntityListFunctions.getNearbyEntities(bun, this.entities, Bunny.fleePerFear * 100));

			if (possibleTarget.size() == 0) {
				bun.wander();
				continue;
			}
			// choose target
			int randomTargetIndex = this.chaos.nextInt(possibleTarget.size());
			Bush target = possibleTarget.get(randomTargetIndex);

			// gather berries
			Berry[] food = bun.gather(target);

			// eat berries
			if (food != null) {
				for (Berry b : food) {
					bun.eat(b);
				}
			}
		}
	}

	private void mate() {
		ArrayList<Bunny> bunnies = EntityListFunctions.getBunnies(this.entities);

		for (Bunny bun : bunnies) {
			ArrayList<Bunny> nearbyBunnies = EntityListFunctions.getBunnies(
					EntityListFunctions.getNearbyEntities(bun, this.entities, Bunny.fleePerFear * 100));

			for (Bunny possibleMate : nearbyBunnies) {
				Bunny[] offspring = bun.tryReproduce(possibleMate);

				if (offspring == null)
					continue;

				// else
				for (Bunny child : offspring) {
					this.entities.add(child);
				}

				break;
				// end else
			}
		}

		ArrayList<Fox> foxes = EntityListFunctions.getFoxes(this.entities);
		for (Fox fox : foxes) {
			ArrayList<Fox> nearbyFoxes = EntityListFunctions.getFoxes(
					EntityListFunctions.getNearbyEntities(fox, this.entities, Fox.huntPerAgg * 100));

			for (Fox possibleMate : nearbyFoxes) {
				Fox[] offspring = fox.tryReproduce(possibleMate);

				if (offspring == null)
					continue;

				// else
				for (Fox child : offspring) {
					this.entities.add(child);
				}

				break;
				// end else
			}
		}
	}

	private void updateAll() {
		for (Entity e : this.entities) {
			e.update();
		}
	}

	private void removeDead() {
		// iterating in reverse to avoid deletion induced skips
		for (int i = this.entities.size() - 1; i >= 0; i--) {
			Entity e = this.entities.get(i);
			if (e.shouldRemove()) {
				this.entities.remove(i);
			}
		}
	}

	/* Moving the simulation forward */
	public void tick() {
		this.spawnBushes();
		this.mate();
		this.huntGather();
		this.updateAll();
		this.removeDead();
	}

	public void tick(int ticks) {
		for (int i = 0; i < ticks; i++) {
			this.tick();
		}
	}

	public void status() {
		ArrayList<Bunny> bunnies = EntityListFunctions.getBunnies(this.entities);
		ArrayList<Fox> foxes = EntityListFunctions.getFoxes(this.entities);
		ArrayList<Bush> bushes = EntityListFunctions.getBushes(this.entities);

		System.out.println("Bushes: " + bushes.size());
		System.out.println("Bunnies: " + bunnies.size());
		System.out.println("Foxes: " + foxes.size());
	}
}
