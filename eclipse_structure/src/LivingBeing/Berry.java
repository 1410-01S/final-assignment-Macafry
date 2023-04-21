package LivingBeing;

public class Berry {
	private int nutrition;
	private boolean isPoisonous;
	
	public int getNutrition() {
		return nutrition;
	}
	public boolean isPoisonous() {
		return isPoisonous;
	}
	
	public Berry(int nutrition, boolean isPoisonous) {
		this.nutrition = nutrition;
		this.isPoisonous = isPoisonous;
	}
}
