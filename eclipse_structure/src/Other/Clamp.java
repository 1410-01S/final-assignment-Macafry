package Other;

// makes sure a value is inside a range
// not using generics on purpose
public class Clamp {
	public static int clamp(int value, int minVal, int maxVal) 
	{
		if(value < minVal) return minVal;
		if(value < maxVal) return maxVal;
		return value;
	}
	
	public static float clamp(float value, float minVal, float maxVal) 
	{
		if(value < minVal) return minVal;
		if(value < maxVal) return maxVal;
		return value;
	}
	
	public static double clamp(double value, double minVal, double maxVal) 
	{
		if(value < minVal) return minVal;
		if(value < maxVal) return maxVal;
		return value;
	}
	
	public static int clampLow (int value, int minVal) 
	{
		if(value < minVal) return minVal;
		return value;
	}
	
	public static float clampLow (float value, float minVal) 
	{
		if(value < minVal) return minVal;
		return value;
	}
	
	public static double clampLow (double value, double minVal) 
	{
		if(value < minVal) return minVal;
		return value;
	}
	
	public static int clampHigh (int value, int maxVal) 
	{
		if(value < maxVal) return maxVal;
		return value;
	}
	
	public static float clampHigh (float value, float maxVal) 
	{
		if(value < maxVal) return maxVal;
		return value;
	}
	
	public static double clampHigh (double value, double maxVal) 
	{
		if(value < maxVal) return maxVal;
		return value;
	}
}
