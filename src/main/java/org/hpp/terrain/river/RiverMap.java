/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.terrain.river;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hpp.terrain.TerrainPoint;

/**
 *
 * @author Gautama
 */
public class RiverMap {
    
    private List<RiverEdge> channelEdges = new ArrayList<>();

    private Map<TerrainPoint, Integer> TerrainPointHeights = new HashMap<>();
    
    public RiverMap() {
        super();
    }

    public void clearEdges() {
        channelEdges.clear();
    }
    
    public void addEdge(RiverEdge edge) {
        if ( channelEdges.isEmpty() ) {
            channelEdges.add( edge );
        } else {
            channelEdges.get( channelEdges.size() - 1 ).setStop(edge.getStart());
            channelEdges.add( edge );
        }
    }
    
    public RiverEdge addEdge(TerrainPoint point, int width) {
        RiverEdge edge = new RiverEdge(point, null, width);
        
        if ( channelEdges.isEmpty() ) {
            channelEdges.add( edge );
        } else {
            channelEdges.get( channelEdges.size() - 1 ).setStop(point);
            channelEdges.add( edge );
        }
        
        return edge;
    }
    
    public void removeEdge(RiverEdge edge) {
        channelEdges.remove(edge);
    }
    
    public List<RiverEdge> edges() {
        return channelEdges;
    }
    
    public Integer getHeight(TerrainPoint point) {
        return TerrainPointHeights.get(point);
    }
    
    public Integer getHeight(int x, int z) {
        return TerrainPointHeights.get(new TerrainPoint(x, z));
    }
    
    public void setHeight(int x, int z, int height) {
        TerrainPointHeights.put(new TerrainPoint(x, z), height);
    }
    
    public void setHeight(TerrainPoint thePoint, int height) {
        TerrainPointHeights.put(thePoint, height);
    }
}
