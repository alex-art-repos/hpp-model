/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.terrain.perlin;

import java.awt.Color;
import java.awt.image.BufferedImage;
import org.hpp.terrain.libnoise.module.Perlin;
import org.hpp.terrain.libnoise.util.ColorCafe;
import org.hpp.terrain.libnoise.util.ImageCafe;
import org.hpp.terrain.libnoise.util.NoiseMap;
import org.hpp.terrain.libnoise.util.NoiseMapBuilderPlane;
import org.hpp.terrain.libnoise.util.RendererImage;
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

    public NoiseMap getHeightMap(int width, int height, 
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
        
        return heightMap;
    }
    
    public void invertDeeps(NoiseMap heightMap) {
        for(int x = 0; x < heightMap.getWidth(); x++) {
            for(int y = 0; y < heightMap.getHeight(); y++) {
                if ( heightMap.getValue(x, y) < 0 ) {
                    heightMap.setValue(x, y, Math.abs(heightMap.getValue(x, y)));
                }
            }
        }
    }
    
    public BufferedImage getTerrainImage(NoiseMap heightMap) {
        ImageCafe destTexture = null;
        
        try {
            // create renderer object
            RendererImage renderer = new RendererImage();

            // terrain gradient
            renderer.clearGradient();
            renderer.addGradientTerrainPoint(-1.00, new ColorCafe(0, 0, 128, 255));
            renderer.addGradientTerrainPoint(-0.20, new ColorCafe(32, 64, 128, 255));
            renderer.addGradientTerrainPoint(-0.04, new ColorCafe(64, 96, 192, 255));
            renderer.addGradientTerrainPoint(-0.02, new ColorCafe(192, 192, 128, 255));
            renderer.addGradientTerrainPoint(0.00, new ColorCafe(0, 192, 0, 255));
            renderer.addGradientTerrainPoint(0.25, new ColorCafe(192, 192, 0, 255));
            renderer.addGradientTerrainPoint(0.50, new ColorCafe(160, 96, 64, 255));
            renderer.addGradientTerrainPoint(0.75, new ColorCafe(128, 255, 255, 255));
            renderer.addGradientTerrainPoint(1.00, new ColorCafe(255, 255, 255, 255));

            // Set up the texture renderer and pass the noise map to it.
            destTexture = new ImageCafe(heightMap.getWidth(), heightMap.getHeight());
            renderer.setSourceNoiseMap(heightMap);
            renderer.setDestImage(destTexture);
            renderer.enableLight(true);
            renderer.setLightContrast(3.0); // Triple the contrast
            renderer.setLightBrightness(2.0); // Double the brightness
            
            // Render the texture.
            renderer.render();
        } catch (Exception exc) {
            destTexture = null;
            log.error("Can`t render map: " + exc.toString());
        }
        
        if ( destTexture == null ) {
            return null;
        }
        
        return this.buffBuilder(destTexture.getHeight(), destTexture.getWidth(), destTexture);
    }
    
    protected BufferedImage buffBuilder(int height, int width, ImageCafe imageCafe) {

        BufferedImage im = new BufferedImage(height, width, BufferedImage.TYPE_INT_ARGB);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int c = getRGBA(imageCafe.getValue(i, j));
                im.setRGB(i, j, c);
            }
        }
        return im;
    }

    protected int getRGBA(ColorCafe colorCafe) {
        int red, blue, green, alpha;
        red = colorCafe.getRed();
        blue = colorCafe.getBlue();
        green = colorCafe.getGreen();
        alpha = colorCafe.getAlpha();
        Color color = new Color(red, green, blue, alpha);
        int rgbnumber = color.getRGB();
        return rgbnumber;
    }
}
