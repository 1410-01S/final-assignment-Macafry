package Coordinate2D;


// having a vector class simplifies the movement logic a lot.
public class Vector2D {
	public float dx;
	public float dy;
	
	public Vector2D(float dx, float dy) {
		this.dx = dx;
		this.dy = dy;
	}
	
	public Vector2D(Point2D start, Point2D finish) {
		float dx = finish.x - start.x;
		float dy = finish.y - start.y;
		
		this.dx = dx;
		this.dy = dy;
	}
	
	public double magnitude() {
		return Math.sqrt(this.dx * this.dx + this.dy * this.dy);
	}
	
	public Vector2D scale(float scaleFactor) {
		return new Vector2D(this.dx * scaleFactor, this.dy * scaleFactor);
	}
	
	public Vector2D unitVector() {
		return this.scale((float)(1/this.magnitude()));
	}
	
	public static Vector2D unitVector(float angle) {
		return new Vector2D((float)Math.cos(angle), (float)Math.sin(angle));
	}
	
	public Vector2D oppositeDirection() {
		return this.scale(-1);
	}
	
	public Vector2D add(Vector2D vec) {
		float newDx =  this.dx + vec.dx;
		float newDy =  this.dy + vec.dy;
		return new Vector2D(newDx, newDy);
	}
	
	
		
}
