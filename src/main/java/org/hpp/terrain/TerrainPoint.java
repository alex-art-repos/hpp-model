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
