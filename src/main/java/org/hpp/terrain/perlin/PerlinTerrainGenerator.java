/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.terrain.perlin;

import org.hpp.terrain.TerrainModel;
import org.hpp.terrain.libnoise.module.Perlin;
import org.hpp.terrain.libnoise.util.NoiseMap;
import org.hpp.terrain.libnoise.util.NoiseMapBuilderPlane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gautama
 */
public class PerlinTerrainGenerator {
    private static final Logger log = LoggerFactory.getLogger(PerlinTerrainGenerator.class);
    
    protected Perlin perlin = new Perlin();

    public PerlinTerrainGenerator() {
        super();
    }
    
    public void setSeed(Integer seed) {
        if ( seed == null ) {
            seed = new Long(System.currentTimeMillis()).intValue();
        }
        perlin.setSeed( seed );
    }

    public TerrainModel getHeightMap(int width, int height, 
            double minX, double maxX, double minZ, double maxZ) {

        NoiseMap heightMap = null;
        try {
            heightMap = new NoiseMap(width, height);
            
            // create Builder object
            NoiseMapBuilderPlane heightMapBuilder = new NoiseMapBuilderPlane();
            heightMapBuilder.setSourceModule(perlin);
            heightMapBuilder.setDestNoiseMap(heightMap);
            heightMapBuilder.setDestSize(width, height);
            heightMapBuilder.setBounds(minX, maxX, minZ, maxZ);
            
            heightMapBuilder.build();
        } catch (Exception exc) {
            heightMap = null;
            log.error("Can`t generate map: " + exc.toString());
        }
        
        return new TerrainModel(heightMap);
    }
    
}
