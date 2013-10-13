/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.terrain.river;

import java.util.List;
import org.hpp.terrain.TerrainPoint;

/**
 *
 * @author Gautama
 */
public class FloodInfo {
    private double floodArea = 0;
    
    private DamModel dam = null;
    private DamModel upperDam = null;
    
    private List<TerrainPoint> trapezeHeight = null;
    private List<TerrainPoint> floodAreaPoints = null;

    public FloodInfo() {
        super();
    }

    public double getFloodArea() {
        return floodArea;
    }

    public void setFloodArea(double floodArea) {
        this.floodArea = floodArea;
    }

    public DamModel getDam() {
        return dam;
    }

    public void setDam(DamModel dam) {
        this.dam = dam;
    }

    public DamModel getUpperDam() {
        return upperDam;
    }

    public void setUpperDam(DamModel upperDam) {
        this.upperDam = upperDam;
    }

    public List<TerrainPoint> getTrapezeHeight() {
        return trapezeHeight;
    }

    public void setTrapezeHeight(List<TerrainPoint> trapezeHeight) {
        this.trapezeHeight = trapezeHeight;
    }

    public List<TerrainPoint> getFloodAreaPoints() {
        return floodAreaPoints;
    }

    public void setFloodAreaPoints(List<TerrainPoint> floodAreaPoints) {
        this.floodAreaPoints = floodAreaPoints;
    }

    @Override
    public String toString() {
        return "FloodInfo{" + "floodArea=" + floodArea + ", dam=" + dam + ", upperDam=" + upperDam + ", trapezeHeight=" + trapezeHeight + ", floodAreaPoints=" + floodAreaPoints + '}';
    }
    
}
