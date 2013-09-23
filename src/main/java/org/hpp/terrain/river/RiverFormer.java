/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.terrain.river;

import java.awt.Polygon;
import java.util.List;
import org.hpp.terrain.TerrainModel;
import org.hpp.terrain.TerrainPoint;
import org.hpp.utils.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gautama
 */
public class RiverFormer {
    private static final Logger log = LoggerFactory.getLogger(RiverFormer.class);

    private int riverDeltaHeight = 20;
    private int minBankWidth = 5;
    private int maxBankWidth = 15;
    private int bankHeightDelta = 5;
    
    private boolean isBuildBanks = false;
    
    private double heightStability = 0.65;
    
    public RiverFormer() {
        super();
    }

    public int getRiverDeltaHeight() {
        return riverDeltaHeight;
    }

    public void setRiverDeltaHeight(int riverDeltaHeight) {
        this.riverDeltaHeight = riverDeltaHeight;
    }

    public int getMinBankWidth() {
        return minBankWidth;
    }

    public void setMinBankWidth(int minBankWidth) {
        this.minBankWidth = minBankWidth;
    }

    public int getMaxBankWidth() {
        return maxBankWidth;
    }

    public void setMaxBankWidth(int maxBankWidth) {
        this.maxBankWidth = maxBankWidth;
    }

    public int getBankHeightDelta() {
        return bankHeightDelta;
    }

    public void setBankHeightDelta(int bankHeightDelta) {
        this.bankHeightDelta = bankHeightDelta;
    }

    public boolean isIsBuildBanks() {
        return isBuildBanks;
    }

    public void setIsBuildBanks(boolean isBuildBanks) {
        this.isBuildBanks = isBuildBanks;
    }

    public double getHeightStability() {
        return heightStability;
    }

    public void setHeightStability(double heightStability) {
        this.heightStability = heightStability;
    }

    public void buildRiver(RiverModel riverModel, TerrainModel terrain) {
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
            
            lastHeight = riverModel.getHeight(lastTerrainPoint) == null 
                    ? terrain.getTerrainHeight(lastTerrainPoint) : riverModel.getHeight(lastTerrainPoint);
            
            newHeight = Math.min(curHeight - riverDeltaHeight, lastHeight - riverDeltaHeight/2 );
            
            if ( RandomUtil.randomDouble() < heightStability ) {
                newHeight = lastHeight;
            }
            
            int halfLastWidth = lastEdge.getWidth()/2,
                halfWidth = edge.getWidth()/2;
            
            Polygon polygon = new Polygon();
            
            polygon.addPoint(lastTerrainPoint.getX() - halfLastWidth, lastTerrainPoint.getY() - 1);
            polygon.addPoint(lastTerrainPoint.getX() + halfLastWidth, lastTerrainPoint.getY() - 1);
            polygon.addPoint(point.getX() + halfWidth, point.getY() + 1);
            polygon.addPoint(point.getX() - halfWidth, point.getY() + 1);

            riverModel.setHeight(point, newHeight);
            
            int startX = Math.min(lastTerrainPoint.getX() - halfLastWidth, point.getX() - halfWidth),
                stopX = Math.max(lastTerrainPoint.getX() + halfLastWidth, point.getX() + halfWidth);
            
            for (int x = startX; x <= stopX; x++) {
                for (int y = lastTerrainPoint.getY(); y <= point.getY(); y++) {
                    if ( polygon.contains(x, y) ) {
                        riverModel.setHeight(x, y, newHeight);
                        terrain.setTerrainHeight(x, y, TerrainModel.RIVER_MARKER_HEIGHT);
                    }
                }
            }
            lastEdge = edge;
        }
        
        if (isIsBuildBanks()) {
            this.buildBanks(riverModel, terrain);
        }
    }
    
    protected void buildBanks( RiverModel riverModel, TerrainModel terrain) {
        List<TerrainPoint> points = riverModel.getAllPoints();
        
        int bankWidth = RandomUtil.randomInRange(minBankWidth, maxBankWidth);
                
        for (TerrainPoint point : points) {
            int x = point.getX(), 
                y = point.getY(), 
                height = riverModel.getHeight(point);
            
            if ( riverModel.getHeight(x-1, y) == null ) {
                for (int i = 0; i < bankWidth; i++) {
//                    if ( height + (i+1)*bankHeightDelta >=  terrain.getTerrainHeight(x - i, y) ) {
//                        break;
//                    }
                    terrain.setTerrainHeight(x - i, y, height + (i+1)*bankHeightDelta);
                }
            }
            
            if ( riverModel.getHeight(x+1, y) == null ) {
                for (int i = 0; i < bankWidth; i++) {
//                    if ( height + (i+1)*bankHeightDelta >=  terrain.getTerrainHeight(x + i, y) ) {
//                        break;
//                    }
                    
                    terrain.setTerrainHeight(x + i, y, height + (i+1)*bankHeightDelta);
                }
            }
        }
    }
    
    public void traceRiver(RiverModel riverModel, TerrainModel terrain) {
        for (RiverEdge edge : riverModel.edges()) {
            TerrainPoint point = edge.getStart();
            terrain.setTerrainHeight(edge.getStart(), -50);
            
            int halfWidth = edge.getWidth()/2 + 1;
            
            terrain.setTerrainHeight(point.getX() - halfWidth, point.getY(), -50);
            terrain.setTerrainHeight(point.getX() + halfWidth, point.getY(), -50);
        }
        
    }
}
