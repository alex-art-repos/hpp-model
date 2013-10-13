/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.terrain.river;

import org.hpp.terrain.TerrainPoint;

/**
 *
 * @author Gautama
 */
public class TubeInfo {
    private TerrainPoint start;
    private TerrainPoint stop;
    
    private double tubeLen;

    public TubeInfo() {
        super();
    }

    public TubeInfo(TerrainPoint start, TerrainPoint stop, double tubeLen) {
        this.start = start;
        this.stop = stop;
        this.tubeLen = tubeLen;
    }

    public TerrainPoint getStart() {
        return start;
    }

    public void setStart(TerrainPoint start) {
        this.start = start;
    }

    public TerrainPoint getStop() {
        return stop;
    }

    public void setStop(TerrainPoint stop) {
        this.stop = stop;
    }

    public double getTubeLen() {
        return tubeLen;
    }

    public void setTubeLen(double tubeLen) {
        this.tubeLen = tubeLen;
    }

    @Override
    public String toString() {
        return "TubeInfo{" + "start=" + start + ", stop=" + stop + ", tubeLen=" + tubeLen + '}';
    }
}
