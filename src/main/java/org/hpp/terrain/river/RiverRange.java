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
    /**
     * River edge with points on it
     */
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
        
        public TerrainPoint firstPoint() {
            return this.isEmpty() ? null : points.get(0);
        }

        @Override
        public String toString() {
            StringBuilder strPoints = new StringBuilder();
            
            if ( points != null ) {
                for (TerrainPoint point : points) {
                    strPoints.append( "\n\t" )
                             .append(point)
                             ;
                }
            }
            
            return "Pair{" + "edge=" + edge + ", points=" + strPoints + '}';
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
    
    /**
     * First pair(edge with points) in range.
     * 
     * @return
     */
    public Pair firstPair() {
        if ( pairs.isEmpty() ) {
            return null;
        }
        
        return pairs.get(0);
    }
    
    /**
     * Last pair(edge with points) in range/
     * @return
     */
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
    
    public double length() {
        if ( pairs.isEmpty() ) {
            return 0;
        }
        
        double length = 0;
        for (Pair pair : pairs) {
            length += pair.getEdge().length();
        }
        
        return length;
    }

    @Override
    public String toString() {
        StringBuilder strPairs = new StringBuilder();
        
        if ( pairs != null ) {
            for (Pair pair : pairs) {
                strPairs.append( "\n\t " )
                        .append( pair )
                        ;
            }
        }
        
        return "RiverRange{" + "pairs=" + strPairs + '}';
    }
}
