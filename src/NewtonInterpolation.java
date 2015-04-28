import java.awt.Point;
import java.util.Iterator;

import Jama.Matrix;

public class NewtonInterpolation implements DrawerInterface {

	private Point[] basePoints;
	private Matrix coefficients;
	double lenX = 0.1;
	
	public NewtonInterpolation(Point[] basePoints) {
		this.basePoints = basePoints;
		coefficients = new Matrix(basePoints.length, 1);
		doInterpolation();
	}
	/***
	 * Purpose: Bestimmen der Koeffizienten des Interpolationspolynoms
	 */
	private void doInterpolation() {
		// Set initial coefficients
		for (int i=0; i<coefficients.getRowDimension(); i++){
			coefficients.set(i, 0, basePoints[i].getY());
			System.out.printf("C%d = %f\n",i, coefficients.get(i, 0) );
		}
		// Replace initial coefficients with new computation
		for (int k=1; k<coefficients.getRowDimension(); k++){
			for (int i = coefficients.getRowDimension()-1; i >= k; i--){
				// Get needed values
				double cI   = coefficients.get(i, 0);
				double cIm1 = coefficients.get(i-1,0);
				double xI   = basePoints[i].getX();
				double xIm1 = basePoints[i-1].getX();
				
				// Compute new coefficient at place i
				double quot = (cI - cIm1) / (xI - xIm1);
				coefficients.set(i, 0, quot);
			}
			System.out.println("-------------------------\nRECOMPUTED COEEFICIENTS\n-------------------------");
			for (int i=0; i<coefficients.getRowDimension(); i++){
				System.out.printf("C%d = %f\n",i, coefficients.get(i, 0));
			}
		}
	}
	
	/***
	 * Purpose: Bestimmen des Wertes des Interpolationspolynoms an der Stelle x
	 ***/
	private double p(double x) {
		double p = coefficients.get(coefficients.getRowDimension()-1, 0) / 2;
		int j = basePoints.length-1;
		for (int i = coefficients.getRowDimension()-2; i>=0; i--) {
			double cK = coefficients.get(i, 0);
			p = cK + (x - basePoints[--j].getX()) * p;
		}
		return p;
	}
	
	/***
	 * Zeichnen der Interpolierten Funktion
	 ***/
	@Override
	public void drawOnto(GraphAreaInterface gA) {
		GraphAreaInterface.color[][] canvas = gA.getPixelArray();
		// Draw Function Points
		// Set initial x|y
		int lineStartX = (int) (basePoints[0].getX() / lenX);
		int lineStartY = (int) Math.round(p(lineStartX*lenX));
		
		// Iterate over all x from first to last basepoint
		for (int x = lineStartX + 1; x <= basePoints[basePoints.length - 1].getX()/lenX; x++) {
			// Compute y value to current x
			int y = (int) Math.round(p(x*lenX));
			
			// Draw line from last x|y to new x|y 
			Bresenham.drawLine(canvas, lineStartX, lineStartY, x, y, GraphAreaInterface.color.GRAY);
			
			// Update last x|y
			lineStartX = x;
			lineStartY = y;
		}
		// Draw Base Points
		for (Point p : basePoints) {
			int x = (int) (p.getX()/lenX);
			int y = (int) p.getY();
			if (0 <= x && x < canvas[0].length && 0 <= y && y < canvas.length) {
				canvas[x][y] = GraphAreaInterface.color.RED;
			}
		}
	}
}
