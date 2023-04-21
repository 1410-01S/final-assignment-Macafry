package Other;

import Coordinate2D.Point2D;

public class ClampPoint {
	public static Point2D clamp(Point2D p, float minX, float minY, float maxX, float maxY) {
		float newX = Clamp.clamp(p.x, minX, maxX);
		float newY = Clamp.clamp(p.y, minY, maxY);
		
		return new Point2D(newX, newY);
	}
}
