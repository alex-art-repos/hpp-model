/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.terrain.river;

import java.util.ArrayList;
import java.util.List;
import org.hpp.terrain.TerrainPoint;

/**
 *
 * @author Gautama
 */
public class RiverRange {
    public static class Pair {
        private List<TerrainPoint> points = new ArrayList<>();
        private RiverEdge edge = null;

        public Pair() {
            super();
        }
        
        public Pair(RiverEdge edge, List<TerrainPoint> points) {
            super();
            this.edge = edge;
            this.points.addAll( points );
        }

        public Pair(RiverEdge edge, TerrainPoint point) {
            super();
            this.edge = edge;
            this.points.add(point);
        }
        
        public RiverEdge getEdge() {
            return edge;
        }

        public void setEdge(RiverEdge edge) {
            this.edge = edge;
        }

        public List<TerrainPoint> getPoints() {
            return points;
        }

        public void setPoints(List<TerrainPoint> points) {
            this.points = points;
        }
        
        public boolean isEmpty() {
            return edge == null || points == null || points.isEmpty();
        }

        @Override
        public String toString() {
            return "Pair{" + "points=" + points + ", edge=" + edge + '}';
        }
    }

    private List<Pair> pairs = new ArrayList<>();
    
    public RiverRange() {
        super();
        
    }

    public List<Pair> getPairs() {
        return pairs;
    }

    public void setPairs(List<Pair> pairs) {
        this.pairs = pairs;
    }

    public void addPair(Pair pair) {
        pairs.add(pair);
    }
    
    public void removePair(Pair pair) {
        pairs.remove(pair);
    }
    
    public void clear() {
        pairs.clear();
    }
    
    public boolean isEmpty() {
        return pairs.isEmpty();
    }
    
    public Pair firstPair() {
        if ( pairs.isEmpty() ) {
            return null;
        }
        
        return pairs.get(0);
    }
    
    public Pair lastPair() {
        if ( pairs.isEmpty() ) {
            return null;
        }
        
        return pairs.get( pairs.size() - 1 );
    }
    
    public TerrainPoint firstPoint() {
        Pair firstPair = this.firstPair();
        
        if ( firstPair == null ) {
            return null;
        }
        
        if ( firstPair.isEmpty() ) {
            return null;
        }
        
        return firstPair.getPoints().get(0);
    }
    
    public TerrainPoint lastPoint() {
        Pair lastPair = this.lastPair();
        
        if ( lastPair == null ) {
            return null;
        }
        
        if ( lastPair.isEmpty() ) {
            return null;
        }
        
        return lastPair.getPoints().get( lastPair.getPoints().size() - 1 );
    }
}
