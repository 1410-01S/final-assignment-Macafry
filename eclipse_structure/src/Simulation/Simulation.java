package Simulation;

public class Simulation {

	public static void main(String[] args) {
		Environment.setSingleton(50, 5, 5, 500, 30, 100);
		Environment env = Environment.getSingleton();
		env.status();
		int ticks = 100;

		for (int i = 0; i < ticks; i++) {
			System.out.println();
			env.tick();
			env.status();
		}

	}

}
