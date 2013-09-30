/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.terrain;

/**
 *
 * @author Gautama
 */
public class TerrainLine {
    // line y = kx + b
    private boolean isSpecial = false; // x = val
    private int lineX = 0;
    private double k = 0;
    private double b = 0;

    public TerrainLine() {
        super();
    }

    public TerrainLine(double k, double b) {
        super();
        this.k = k;
        this.b = b;
    }
    
    public TerrainLine(int x) {
        super();
        lineX = x;
        isSpecial = true;
    }
    
    public boolean isIsSpecial() {
        return isSpecial;
    }

    public int getLineX() {
        return lineX;
    }

    public void setLineX(int lineX) {
        this.lineX = lineX;
    }

    public double getK() {
        return k;
    }

    public void setK(double k) {
        this.k = k;
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }
    
    public double getValue(double x) {
        if ( isSpecial ) {
            return Double.MAX_VALUE;
        } else {
            return k * x + b;
        }
    }
    
    public TerrainPoint nextRightPoint(TerrainPoint point) {
        if ( isSpecial ) {
            return new TerrainPoint(lineX, point.getY() + 1);
        } else {
            int newX = point.getX() + 1;
            return TerrainPoint.nearestPoint(newX, k * newX + b);
        }
    }
    
    public TerrainPoint nextLeftPoint(TerrainPoint point) {
        if ( isSpecial ) {
            return new TerrainPoint(lineX, point.getY() - 1);
        } else {
            int newX = point.getX() - 1;
            return TerrainPoint.nearestPoint(newX, k * newX + b);
        }
    }
    
    /**
     * Is current point upper than line(inclusive) (for positive coord plane).
     * 
     * @param point
     * @return
     */
    public boolean isUpper(TerrainPoint point) {
        if ( isSpecial ) {
            return point.getX() <= lineX;
        } else {
            return point.getY() <= k * point.getX() + b;
        }
    }
    
    /**
     * Normal line to current.
     * (y = kx +b, y = mx + p ==> k*m = -1)
     * @param point point on the normal line
     * @return normal line
     */
    public TerrainLine normalLineByPoint(TerrainPoint point) {
        if ( isSpecial ) {
            return new TerrainLine(0, point.getY());
        } else {
            if ( k == 0  ) { // almost 0
                return new TerrainLine(point.getX());
            } else {
                double newK = (-1) / k,
                       newB = point.getY() - newK * point.getX();

                return new TerrainLine(newK, newB);
            }
        }
    }
    
    /**
     * Parallel line to current.
     * (y = kx +b, y = mx + p ==> k = m )
     * @param point point on the parallel line
     * @return normal line
     */
    public TerrainLine parallelLineByPoint(TerrainPoint point) {
        if ( isSpecial ) {
            return new TerrainLine(point.getX());
        } else {
            double newK = k,
                   newB = point.getY() - newK * point.getX();

            return new TerrainLine(newK, newB);
        }
    }
    
    public TerrainPoint intersection(TerrainLine line) {
        if ( isSpecial ) {
            if ( line.isIsSpecial() ) {
                // parallel or congruent 
                return null;
            } else {
                return TerrainPoint.nearestPoint(lineX, line.getValue(lineX));
            }
        } else {
            if ( line.isIsSpecial() ) {
                return TerrainPoint.nearestPoint(lineX, this.getValue(lineX));
            } else {
                if ( Math.abs(k - line.getK()) < 0.001 ) {
                    // almost parallel
                    return null;
                } else {
                    double newX = (line.getB() - b) / (k - line.getK());

                    return TerrainPoint.nearestPoint(newX, this.getValue(newX));
                }
            }
        }
    }

    public static TerrainLine createByPoints(TerrainPoint point1, TerrainPoint point2) {
        if ( point1 == null || point2 == null ) {
            return null;
        }
        
        if ( point1.getX() - point2.getX() == 0 ) {
            return new TerrainLine(point1.getX());
        } else {
            double k = (point1.getY() - point2.getY()) / (double)( point1.getX() - point2.getX() ),
                   b = point1.getY() - k * point1.getX();
            
            return new TerrainLine(k, b);
        }
    }

    @Override
    public String toString() {
        return "TerrainLine{" + "isSpecial=" + isSpecial + ", lineX=" + lineX + ", k=" + k + ", b=" + b + '}';
    }
    
}
