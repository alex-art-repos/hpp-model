/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.terrain.river;

import java.util.Objects;
import org.hpp.terrain.TerrainPoint;

/**
 *
 * @author Gautama
 */
public class RiverEdge {
    private TerrainPoint start = null;
    private TerrainPoint stop = null;
    
    private int width = 0;

    public RiverEdge() {
        super();
    }

    public RiverEdge(TerrainPoint theStart, TerrainPoint theStop, int theWidth) {
        super();
        start = theStart;
        stop = theStop;
        width = theWidth;
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

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.start);
        hash = 29 * hash + Objects.hashCode(this.stop);
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
        final RiverEdge other = (RiverEdge) obj;
        
        if (!Objects.equals(this.start, other.start)) {
            return false;
        }
        if (!Objects.equals(this.stop, other.stop)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "RiverEdge{" + "start=" + start + ", stop=" + stop + ", width=" + width + '}';
    }
}
