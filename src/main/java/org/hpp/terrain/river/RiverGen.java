/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.terrain.river;

import org.hpp.terrain.TerrainModel;
import org.hpp.terrain.TerrainPoint;
import org.hpp.utils.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gautama
 */
public class RiverGen {
    public static final Logger log = LoggerFactory.getLogger(RiverGen.class);

    private int minEdgeLen = 0;
    private int maxEdgeLen = 0;
    
    private int minAngle = 0;
    private int maxAngle = 0;
    
    private int minWidth = 0;
    private int maxWidth = 0;
    
    private int maxErrorThreshold = 3;
    
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

    public int getMaxErrorThreshold() {
        return maxErrorThreshold;
    }

    public void setMaxErrorThreshold(int maxErrorThreshold) {
        this.maxErrorThreshold = maxErrorThreshold;
    }

    public RiverModel genRiver(TerrainPoint startPos, TerrainModel terrainMap) {
        int angle = 0;
        boolean leftTurn = false;
        
        int edgeLen = 0, edgeWidth = 0;
        
        int errors = 0;
        
        TerrainPoint lastTerrainPoint = startPos, newTerrainPoint = null;
        RiverModel river = new RiverModel();
        
        edgeWidth = RandomUtil.randomInRange(minWidth, maxWidth);
        river.addEdge(startPos, edgeWidth);
        
        do {
            angle = RandomUtil.randomInRange(minAngle, maxAngle);
            leftTurn = RandomUtil.randomBool();

            edgeLen = RandomUtil.randomInRange(minEdgeLen, maxEdgeLen);
            edgeWidth = RandomUtil.randomInRange(minWidth, maxWidth);

            double deltaX = edgeLen * Math.sin(Math.toRadians(angle));
            double deltaY = edgeLen * Math.cos(Math.toRadians(angle));
            
            if ( leftTurn ) {
                newTerrainPoint = new TerrainPoint(new Double(lastTerrainPoint.getX() + deltaX).intValue(), 
                        new Double(lastTerrainPoint.getY() + deltaY).intValue());
            } else {
                newTerrainPoint = new TerrainPoint(new Double(lastTerrainPoint.getX() - deltaX).intValue(), 
                        new Double(lastTerrainPoint.getY() + deltaY).intValue());
            }
            
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
                
                if ( newTerrainPoint.getX() >= terrainMap.getMapWidth() ) {
                    newTerrainPoint.setX( terrainMap.getMapWidth() - 1 );
                }
                
                if ( newTerrainPoint.getX() <= 0 ) {
                    newTerrainPoint.setX( 0 );
                }
                
                if ( newTerrainPoint.getY() >= terrainMap.getMapHeight() ) {
                    newTerrainPoint.setY( terrainMap.getMapHeight() - 1 );
                }
                
                if ( newTerrainPoint.getY() <= 0 ) {
                    newTerrainPoint.setY( 0 );
                }
                
                river.addEdge(newTerrainPoint, edgeWidth);
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
    
    public RiverModel genNaturalRiver(TerrainPoint startPos, TerrainModel terrain) {
        int edgeWidth = 0;
        
        int errors = 0;
        
        int potentialPoints = 10;
        
        TerrainPoint lastTerrainPoint = startPos, newTerrainPoint = null;
        RiverModel river = new RiverModel();
        
        edgeWidth = RandomUtil.randomInRange(minWidth, maxWidth);
        
        int curX = lastTerrainPoint.getX(), chosenX = curX;
        int curY = lastTerrainPoint.getY();

        for (int i = curX - potentialPoints*2; i < curX + potentialPoints*2 ; i++) {
            if ( !terrain.contains(i, curY) ) {
                continue;
            }

            if ( terrain.getTerrainHeight(i, curY) < terrain.getTerrainHeight(chosenX, curY) ) {
                chosenX = i;
            }
        }
        lastTerrainPoint = new TerrainPoint(chosenX, curY);
        river.addEdge(lastTerrainPoint, edgeWidth);
        
        do {
            edgeWidth = RandomUtil.randomInRange(minWidth, maxWidth);
            
            curX = lastTerrainPoint.getX();
            chosenX = curX;
            curY = lastTerrainPoint.getY();
            
            for (int i = curX - (potentialPoints/2); i < curX + (potentialPoints/2) ; i++) {
                if ( !terrain.contains(i, curY + 1) ) {
                    continue;
                }
                
                if ( terrain.getTerrainHeight(i, curY + 1) < terrain.getTerrainHeight(chosenX, curY + 1) ) {
                    chosenX = i;
                }
            }

            newTerrainPoint = new TerrainPoint(chosenX, curY + 1);
            
            if ( newTerrainPoint.equals(lastTerrainPoint) ) {
                errors ++;
                if ( errors > maxErrorThreshold ) {
                    break;
                }
                continue;
            }
            
            lastTerrainPoint = newTerrainPoint;

            if ( !terrain.contains(newTerrainPoint) ) {
                
                if ( newTerrainPoint.getX() >= terrain.getMapWidth() ) {
                    newTerrainPoint.setX( terrain.getMapWidth() - 1 );
                }
                
                if ( newTerrainPoint.getX() <= 0 ) {
                    newTerrainPoint.setX( 0 );
                }
                
                if ( newTerrainPoint.getY() >= terrain.getMapHeight() ) {
                    newTerrainPoint.setY( terrain.getMapHeight() - 1 );
                }
                
                if ( newTerrainPoint.getY() <= 0 ) {
                    newTerrainPoint.setY( 0 );
                }
                
                river.addEdge(newTerrainPoint, edgeWidth);
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
}
