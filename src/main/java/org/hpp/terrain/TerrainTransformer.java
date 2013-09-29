/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.terrain;

import org.hpp.terrain.libnoise.util.NoiseMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gautama
 */
public class TerrainTransformer {
    private static final Logger log = LoggerFactory.getLogger(TerrainTransformer.class);

    public TerrainTransformer() {
        super();
    }
    
    public void invertDeeps(TerrainModel terrain) {
        NoiseMap heightMap = terrain.getInternalMap();
                
        for(int x = 0; x < heightMap.getWidth(); x++) {
            for(int y = 0; y < heightMap.getHeight(); y++) {
                if ( heightMap.getValue(x, y) < 0 ) {
                    heightMap.setValue(x, y, Math.abs(heightMap.getValue(x, y)));
                }
            }
        }
    }
    
    public void rotateTerrain(TerrainModel terrain, int minHeight, int maxHeight) {
        double sinA = Math.abs(maxHeight - minHeight) / (double)(terrain.getPixelScale() * terrain.getMapHeight());
        
        log.debug("Rotate sinA: " + sinA);
        
        for(int x = 0; x < terrain.getMapWidth(); x ++) {
            for(int z = 0; z < terrain.getMapHeight(); z ++) {
                terrain.setTerrainHeight(x, z, (int)Math.ceil( terrain.getTerrainHeight(x, z) - z * sinA ) );
            }
        }
    }
    
    public void normolizeHeight(TerrainModel terrain) {
        int minHeight = terrain.getMinHeight();
        
        if ( minHeight >= 0 ) {
            log.debug("Terrain is normal (" + minHeight + ").");
            return;
        }
        
        int moveHeight = Math.abs(minHeight);
        
        for(int x = 0; x < terrain.getMapWidth(); x ++) {
            for(int z = 0; z < terrain.getMapHeight(); z ++) {
                terrain.setTerrainHeight(x, z, (int)Math.ceil( terrain.getTerrainHeight(x, z) + moveHeight ) );
            }
        }
        
        log.debug("Terrain moved on " + moveHeight + " m.");
    }
}
