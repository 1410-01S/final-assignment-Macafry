package Coordinate2D;

public class Point2D {
	public float x;
	public float y;
	

	public Point2D(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public double distance(Point2D other) {
		float dx = other.x - this.x;
		float dy = other.y - this.y;
		return Math.sqrt(dx*dx + dy*dy);
	}
	
	public static Point2D interpolate(Point2D p1, Point2D p2, int weight1, int weight2) {
		float totalWeight = weight1 + weight2;
		float t = weight1/totalWeight;
		
		float newX = p1.x * t + p2.x * (1-t);
		float newY = p1.y * t + p2.y * (1-t);
		
		return new Point2D(newX, newY);
	}
	
	public static Point2D midpoint(Point2D p1, Point2D p2) {
		return interpolate(p1, p2, 1, 1);
	}
	
	public Point2D displace(Vector2D displacement) {
		float newX = this.x + displacement.dx;
		float newY = this.y + displacement.dy;
		return new Point2D(newX, newY);
	}

	@Override
	public String toString() {
		return "Point [x=" + x + ", y=" + y + "]";
	}
}
