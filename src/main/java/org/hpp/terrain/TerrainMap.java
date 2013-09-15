/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.terrain;

import org.hpp.terrain.libnoise.util.NoiseMap;

/**
 *
 * @author Gautama
 */
public class TerrainMap {
    private NoiseMap heightMap = null;
    
    private int pixelScale = 5; // m

    private int heightScale = 100; // 
    
    public TerrainMap() {
        super();
    }

    public TerrainMap(NoiseMap theHeightMap, int thePixelScale, int theHeightScale) {
        super();
        heightMap = theHeightMap;
        pixelScale = thePixelScale;
        heightScale = theHeightScale;
        
        this.checkState();
    }

    public NoiseMap getInternalMap() {
        return heightMap;
    }

    public void setInternalMap(NoiseMap heightMap) {
        this.heightMap = heightMap;
    }

    public int getPixelScale() {
        return pixelScale;
    }

    public void setPixelScale(int pixelScale) {
        this.pixelScale = pixelScale;
    }

    public int getHeightScale() {
        return heightScale;
    }

    public void setHeightScale(int heightScale) {
        this.heightScale = heightScale;
    }
    
    public int getTerrainHeight(int x, int z) {
        this.checkState();
        
        double value = heightMap.getValue(x, z);
        
        value *= heightScale;
        
        return new Double(value).intValue();
    }
    
    public void setTerrainHeight(int x, int z, int y) {
        this.checkState();
        
        heightMap.setValue(x, z, y/(double)heightScale);
    }
    
    public int getTerrainHeight(TerrainPoint TerrainPoint) {
        this.checkState();
        
        double value = heightMap.getValue(TerrainPoint.getX(), TerrainPoint.getY());
        
        value *= heightScale;
        
        return new Double(value).intValue();
    }
    
    public void setTerrainHeight(TerrainPoint TerrainPoint, int y) {
        this.checkState();
        
        heightMap.setValue(TerrainPoint.getX(), TerrainPoint.getY(), y/(double)heightScale);
    }
    
    public int getMapWidth() {
        if ( heightMap == null ) {
            return -1;
        }
        
        return heightMap.getWidth();
    }
    
    public int getMapHeight() {
        if ( heightMap == null ) {
            return -1;
        }
        
        return heightMap.getHeight();
    }
    
    public int getMinHeight() {
        this.checkState();
        
        int minHeight = Integer.MAX_VALUE;
        for (int x = 0; x < heightMap.getWidth(); x++) {
            for (int y = 0; y < heightMap.getHeight(); y++) {
                int curHeight = this.getTerrainHeight(x, y);
                
                if ( minHeight > curHeight ) {
                    minHeight = curHeight;
                }
            }
        }
        
        return minHeight;
    }
    
    public int getMaxHeight() {
        this.checkState();
        
        int maxHeight = Integer.MIN_VALUE;
        for (int x = 0; x < heightMap.getWidth(); x++) {
            for (int y = 0; y < heightMap.getHeight(); y++) {
                int curHeight = this.getTerrainHeight(x, y);
                
                if ( maxHeight < curHeight ) {
                    maxHeight = curHeight;
                }
            }
        }
        
        return maxHeight;
    }
    
    private void checkState() {
        if ( heightMap == null ) {
            throw new IllegalStateException("No internal map.");
        }
        
        if ( pixelScale <= 0 ) {
            throw new IllegalStateException("Bad pixel scale(" + pixelScale + ").");
        }
        
        if ( heightScale <= 0 ) {
            throw new IllegalStateException("Bad height scale(" + heightScale + ").");
        }
    }
}
