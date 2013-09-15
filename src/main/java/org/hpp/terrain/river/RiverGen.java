/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.terrain.river;

import java.util.Random;
import org.hpp.terrain.TerrainMap;
import org.hpp.terrain.TerrainPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gautama
 */
public class RiverGen {
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    private int minEdgeLen = 0;
    private int maxEdgeLen = 0;
    
    private int minAngle = 0;
    private int maxAngle = 0;
    
    private int minWidth = 0;
    private int maxWidth = 0;
    
    private int minHeightDelta = 0;
    private int maxHeightDelta = 0;
    
    private int maxErrorThreshold = 3;
    
    private Random rnd = new Random(System.nanoTime());
    
    public RiverGen() {
        super();
    }

    public int getMinEdgeLen() {
        return minEdgeLen;
    }

    public void setMinEdgeLen(int minEdgeLen) {
        this.minEdgeLen = minEdgeLen;
    }

    public int getMaxEdgeLen() {
        return maxEdgeLen;
    }

    public void setMaxEdgeLen(int maxEdgeLen) {
        this.maxEdgeLen = maxEdgeLen;
    }

    public int getMinAngle() {
        return minAngle;
    }

    public void setMinAngle(int minAngle) {
        this.minAngle = minAngle;
    }

    public int getMaxAngle() {
        return maxAngle;
    }

    public void setMaxAngle(int maxAngle) {
        this.maxAngle = maxAngle;
    }

    public int getMinWidth() {
        return minWidth;
    }

    public void setMinWidth(int minWidth) {
        this.minWidth = minWidth;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public int getMinHeightDelta() {
        return minHeightDelta;
    }

    public void setMinHeightDelta(int minHeightDelta) {
        this.minHeightDelta = minHeightDelta;
    }

    public int getMaxHeightDelta() {
        return maxHeightDelta;
    }

    public void setMaxHeightDelta(int maxHeightDelta) {
        this.maxHeightDelta = maxHeightDelta;
    }

    public int getMaxErrorThreshold() {
        return maxErrorThreshold;
    }

    public void setMaxErrorThreshold(int maxErrorThreshold) {
        this.maxErrorThreshold = maxErrorThreshold;
    }

    public RiverMap genRiver(TerrainPoint startPos, TerrainMap terrainMap) {
        int angle = 0;
        boolean leftTurn = false;
        
        int edgeLen = 0, edgeWidth = 0;
        
        int errors = 0;
        
        TerrainPoint lastTerrainPoint = startPos, newTerrainPoint = null;
        RiverMap river = new RiverMap();
        
        edgeWidth = this.randomInRange(minWidth, maxWidth);
        river.addEdge(startPos, edgeWidth);
        
        log.debug("CurPos =" + startPos + ", w=" + edgeWidth);
        
        do {
            angle = this.randomInRange(minAngle, maxAngle);
            leftTurn = this.randomBool();

            edgeLen = this.randomInRange(minEdgeLen, maxEdgeLen);
            edgeWidth = this.randomInRange(minWidth, maxWidth);

            double deltaX = edgeLen * Math.sin(Math.toRadians(angle));
            double deltaY = edgeLen * Math.cos(Math.toRadians(angle));
            
            if ( leftTurn ) {
                newTerrainPoint = new TerrainPoint(new Double(lastTerrainPoint.getX() + deltaX).intValue(), 
                        new Double(lastTerrainPoint.getY() + deltaY).intValue());
            } else {
                newTerrainPoint = new TerrainPoint(new Double(lastTerrainPoint.getX() - deltaX).intValue(), 
                        new Double(lastTerrainPoint.getY() + deltaY).intValue());
            }
            
            log.debug("CurPos =" + newTerrainPoint + ", w=" + edgeWidth);
            if ( newTerrainPoint.equals(lastTerrainPoint) ) {
                errors ++;
                if ( errors > maxErrorThreshold ) {
                    break;
                }
                continue;
            }
            
            lastTerrainPoint = newTerrainPoint;

            if ( ( newTerrainPoint.getX() >= terrainMap.getMapWidth() || newTerrainPoint.getX() <= 0 ) 
                || ( newTerrainPoint.getY() >= terrainMap.getMapHeight() || newTerrainPoint.getY() <= 0 ) ) {
                newTerrainPoint = null;
            } else {
                river.addEdge(newTerrainPoint, edgeWidth);
            }
        } while(newTerrainPoint != null);
        
        if ( errors > maxErrorThreshold ) {
            return null;
        }
        
        return river;
    }
    
    protected int randomInRange(int min, int max) {
        return min + rnd.nextInt(max - min);
    }
    
    protected int randomGaussInRange(int min, int max) {
        return min + new Double((max-min) * Math.abs(rnd.nextGaussian())).intValue();
    }
    
    protected boolean randomBool() {
        return rnd.nextBoolean();
    }
    
}
