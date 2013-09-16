/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.terrain.river;

import java.awt.Polygon;
import org.hpp.terrain.TerrainMap;
import org.hpp.terrain.TerrainPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gautama
 */
public class RiverFormer {
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    private int deltaHeight = 20;
    
    public RiverFormer() {
        super();
    }

    public int getDeltaHeight() {
        return deltaHeight;
    }

    public void setDeltaHeight(int deltaHeight) {
        this.deltaHeight = deltaHeight;
    }

    public void buildRiver(RiverMap riverModel, TerrainMap terrain) {
        RiverEdge lastEdge = null;
        
        int lastHeight, curHeight, newHeight;
        
        for (RiverEdge edge : riverModel.edges()) {
            TerrainPoint point = edge.getStart();
            
            if ( lastEdge == null ) {
                lastEdge = edge;
                continue;
            }
            
            TerrainPoint lastTerrainPoint = lastEdge.getStart();
            
            curHeight = terrain.getTerrainHeight(point);
            
            log.debug("Points: " + lastTerrainPoint + ", " + point);
            lastHeight = riverModel.getHeight(lastTerrainPoint) == null ? Integer.MAX_VALUE : riverModel.getHeight(lastTerrainPoint);
            
            newHeight = Math.min(curHeight - deltaHeight, lastHeight - deltaHeight/2 );
            
            int halfLastWidth = lastEdge.getWidth()/2,
                halfWidth = edge.getWidth()/2;
            
            Polygon polygon = new Polygon();
            
            polygon.addPoint(lastTerrainPoint.getX() - halfLastWidth, lastTerrainPoint.getY());
            polygon.addPoint(lastTerrainPoint.getX() + halfLastWidth, lastTerrainPoint.getY());
            polygon.addPoint(point.getX() + halfWidth, point.getY());
            polygon.addPoint(point.getX() - halfWidth, point.getY());

            riverModel.setHeight(point, newHeight);
            
            int startX = Math.min(lastTerrainPoint.getX() - halfLastWidth, point.getX() - halfWidth),
                stopX = Math.max(lastTerrainPoint.getX() + halfLastWidth, point.getX() + halfWidth);
            
            for (int x = startX; x <= stopX; x++) {
                for (int y = lastTerrainPoint.getY(); y <= point.getY(); y++) {
                    if ( polygon.contains(x, y) ) {
                        log.debug(String.format("River height[%d, %d]: %d, %d", x, y, newHeight, lastHeight));
                        riverModel.setHeight(x, y, newHeight);
                        terrain.setTerrainHeight(x, y, -50);
                    }
                }
            }
            lastEdge = edge;
        }
        
    }
    
    public void traceRiver(RiverMap riverModel, TerrainMap terrain) {
        for (RiverEdge edge : riverModel.edges()) {
            TerrainPoint point = edge.getStart();
            terrain.setTerrainHeight(edge.getStart(), -50);
            
            int halfWidth = edge.getWidth()/2 + 1;
            
            terrain.setTerrainHeight(point.getX() - halfWidth, point.getY(), -50);
            terrain.setTerrainHeight(point.getX() + halfWidth, point.getY(), -50);
        }
        
    }
}
