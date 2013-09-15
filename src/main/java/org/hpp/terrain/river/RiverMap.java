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
    
    public void addEdge(TerrainPoint TerrainPoint, int width) {
        if ( channelEdges.isEmpty() ) {
            channelEdges.add( new RiverEdge(TerrainPoint, null, width) );
        } else {
            channelEdges.get( channelEdges.size() - 1 ).setStop(TerrainPoint);
            channelEdges.add( new RiverEdge(TerrainPoint, null, width) );
        }
    }
    
    public void removeEdge(RiverEdge edge) {
        channelEdges.remove(edge);
    }
    
    public List<RiverEdge> edges() {
        return channelEdges;
    }
    
    public Integer getHeight(TerrainPoint TerrainPoint) {
        return TerrainPointHeights.get(TerrainPoint);
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
