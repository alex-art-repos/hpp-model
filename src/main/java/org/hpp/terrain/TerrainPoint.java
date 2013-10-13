/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.terrain;

import java.awt.Point;

/**
 *
 * @author Gautama
 */
public class TerrainPoint {
    private int x;
    private int y;

    public TerrainPoint() {
        super();
    }
    
    public TerrainPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
    
    public Point asPoint() {
        return new Point(x, y);
    }
    
    public boolean isBetween(TerrainPoint point1, TerrainPoint point2) {
        int minX = Math.min(point1.getX(), point2.getX()),
            maxX = Math.max(point1.getX(), point2.getX()),
            minY = Math.min(point1.getY(), point2.getY()),
            maxY = Math.max(point1.getY(), point2.getY());
        
        return (minX <= x && x <= maxX) && (minY <= y && y <= maxY);
    }
    
    public static double distance(TerrainPoint point1, TerrainPoint point2) {
        double x1 = point1.getX() - point2.getX();
        double y1 = point1.getY() - point2.getY();

        return Math.sqrt(x1 * x1 + y1 * y1);
    }

    public static double distance3D(TerrainPoint point1, int height1, TerrainPoint point2, int height2) {
        double x1 = point1.getX() - point2.getX();
        double y1 = point1.getY() - point2.getY();
        double z1 = height1 - height2;

        return Math.sqrt(x1 * x1 + y1 * y1 + z1 * z1);
    }
    
    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt( (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2) );
    }
    
    public static TerrainPoint nearestPoint(double x, double y) {
        int x1 = new Double(Math.floor(x)).intValue(),
            x2 = new Double(Math.ceil(x)).intValue(),
            y1 = new Double(Math.floor(y)).intValue(),
            y2 = new Double(Math.ceil(y)).intValue();
        
        double dist[] = new double[4];
        
        dist[0] = TerrainPoint.distance(x1, y1, x, y);
        dist[1] = TerrainPoint.distance(x2, y1, x, y);
        dist[2] = TerrainPoint.distance(x1, y2, x, y);
        dist[3] = TerrainPoint.distance(x2, y2, x, y);
        
        int min = 0;
        
        for (int i = 0; i < dist.length; i++) {
            if ( dist[min] > dist[i] ) {
                min = i;
            }
        }

        switch(min) {
            case 0:
                return new TerrainPoint(x1, y1);
            case 1:
                return new TerrainPoint(x2, y1);
            case 2:
                return new TerrainPoint(x1, y2);
            case 3:
                return new TerrainPoint(x2, y2);
        }
        
        return null;
    }
    
    public static TerrainPoint fromPoint(Point point) {
        return new TerrainPoint(point.x, point.y);
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.x;
        hash = 29 * hash + this.y;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TerrainPoint other = (TerrainPoint) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TerrainPoint{" + "x=" + x + ", y=" + y + '}';
    }
    
}
