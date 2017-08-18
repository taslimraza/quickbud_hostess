package com.shaddyhollow.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Point {
	public final int column;
	public final int row;
	
	public Point(int row, int column) {
		this.column = column;
		this.row = row;
	}
	
	@Override
	public String toString() {
		return String.format(Locale.US, "(%d,  %d)", column, row);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Point) {
			Point p = (Point)o;
			if (p.column == this.column && p.row == this.row) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Determines if the point is in a straight line between two other points
	 */
	public boolean isInStraightLineBetween(Point p1, Point p2) {
		// Vertical line
		if (p1.column == p2.column && p1.column == this.column) {
			return between(this.row, p1.row, p2.row);
		}
		// Horizontal line
		else if (p1.row == p2.row && p1.row == this.row) {
			return between(this.column, p1.column, p2.column);
		}
		
		else return false;
	}
	
	/**
	 * Returns the points in a straight line between the point and
	 * the given point p.
	 * 
	 * If the points are not in a straight line, an empty list is returned.
	 * 
	 * Both end points are included, i.e. if the point is equal to p, a
	 * list containing only the point is returned
	 */
	public List<Point> pointsInStraightLineTo(Point p) {
		List<Point> result = new ArrayList<Point>();
		// Vertical line
		if (this.column == p.column) {
			if (this.row < p.row) {
				for (int r=this.row; r<=p.row; r++) {
					result.add( new Point( r, this.column ) );
				}
			} else {
				for (int r=this.row; r>=p.row; r--) {
					result.add( new Point( r, this.column ) );
				}
			}
		}
		// Horizontal line
		else if (this.row == p.row) {
			if (this.column < p.column) {
				for (int c=this.column; c<=p.column; c++) {
					result.add( new Point( this.row, c ) );
				}
			} else {
				for (int c=this.column; c>=p.column; c--) {
					result.add( new Point( this.row, c ) );
				}
			}
		}
		
		return result;
	}
	
	public List<Point> allPointsBetween(Point p) {
		List<Point> result = new ArrayList<Point>();

		int minX = Math.min(row, p.row);
		int maxX = Math.max(row, p.row);
		int minY = Math.min(column, p.column);
		int maxY = Math.max(column, p.column);
		
		for(int x=minX;x<=maxX;x++) {
			for(int y=minY;y<=maxY;y++) {
				result.add(new Point(x, y));
			}
		}
		return result;
	}
	/**
	 * Determines if the point is in the same column or row as
	 * the given point p
	 */
	public boolean sharesColumnOrRowWith(Point p) {
		return (this.column==p.column || this.row==p.row);
	}
	
	/**
	 * Calculates distance to another point. Returns 1 for same point
	 * 
	 * Equals ( row difference + column difference + 1 )
	 */
	public int lengthTo(Point p) {
		return java.lang.Math.abs( this.column - p.column ) +
			   java.lang.Math.abs( this.row - p.row ) + 1;
	}
	
	/**
	 * Is i between a and b? (Both included)
	 * @param i
	 * @param a
	 * @param b
	 * @return
	 */
	private boolean between(int i, int a, int b) {
		if (a>b)
			return (i>=b && i<=a);
		else
			return (i>=a && i<=b);
	}
}