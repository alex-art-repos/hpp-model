/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.terrain.river;

import org.hpp.terrain.TerrainLine;
import org.hpp.terrain.TerrainModel;
import org.hpp.terrain.TerrainPoint;

/**
 *
 * @author Gautama
 */
public class DamModel {
    private TerrainLine line = null;
    
    private TerrainPoint leftPoint = null;
    private TerrainPoint rightPoint = null;

    private int height = 0;
    
    private TerrainPoint riverIntersectPoint = null;
    private RiverEdge riverIntersecrEdge = null;
    
    public DamModel() {
        super();
    }

    public DamModel(TerrainLine line, TerrainPoint left, TerrainPoint right) {
        super();
        this.line = line;
        this.leftPoint = left;
        this.rightPoint = right;
    }
    
    public TerrainLine getLine() {
        return line;
    }

    public void setLine(TerrainLine line) {
        this.line = line;
    }

    public TerrainPoint getLeftPoint() {
        return leftPoint;
    }

    public void setLeftPoint(TerrainPoint leftPoint) {
        this.leftPoint = leftPoint;
    }

    public TerrainPoint getRightPoint() {
        return rightPoint;
    }

    public void setRightPoint(TerrainPoint rightPoint) {
        this.rightPoint = rightPoint;
    }

    public double getWidth() {
        return TerrainPoint.distance(leftPoint, rightPoint);
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public TerrainPoint getRiverIntersectPoint() {
        return riverIntersectPoint;
    }

    public void setRiverIntersectPoint(TerrainPoint riverIntersectionPoint) {
        this.riverIntersectPoint = riverIntersectionPoint;
    }

    public RiverEdge getRiverIntersectEdge() {
        return riverIntersecrEdge;
    }

    public void setRiverIntersectEdge(RiverEdge riverIntersectionEdge) {
        this.riverIntersecrEdge = riverIntersectionEdge;
    }

    @Override
    public String toString() {
        return "DamModel{" + "line=" + line + ", leftPoint=" + leftPoint + ", rightPoint=" + rightPoint + ", height=" + height + ", riverIntersectionPoint=" + riverIntersectPoint + ", riverIntersectionEdge=" + riverIntersecrEdge + '}';
    }

}
