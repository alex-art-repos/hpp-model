package org.hpp.core;

/*
 * Copyright (C) 2004 Jason Bevins (original code)
 * Copyright © 2010 Thomas J. Hodge (java port of original code)
 * 
 * This program uses the libnoiseforjava to generate a texture map.
 * 
 * libnoiseforjava is a Java port of the C++ library libnoise, which may be found at 
 * http://libnoise.sourceforge.net/.  libnoise was developed by Jason Bevins, who may be 
 * contacted at jlbezigvins@gmzigail.com (for great email, take off every 'zig').
 * Porting to Java was done by Thomas Hodge, who may be contacted at
 * libnoisezagforjava@gzagmail.com (remove every 'zag').
 * 
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * This program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
import org.hpp.terrain.libnoise.util.RendererImage;
import org.hpp.terrain.libnoise.util.ColorCafe;
import org.hpp.terrain.libnoise.util.NoiseMapBuilderPlane;
import org.hpp.terrain.libnoise.util.NoiseMap;
import org.hpp.terrain.libnoise.util.ImageCafe;
import org.hpp.terrain.libnoise.module.Perlin;
import org.hpp.terrain.libnoise.exception.ExceptionInvalidParam;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class TerrainHeightMapExample {
    // generates an example Terrain Height Map, as shown at
    // http://libnoise.sourceforge.net/tutorials/tutorial3.html

    public static void main(String[] args) throws ExceptionInvalidParam {
        // create Perlin noise module object
        Perlin perlin1 = new Perlin();
        
        perlin1.setSeed( new Long(System.currentTimeMillis()).intValue() );

        // create Noisemap object
        NoiseMap heightMap = new NoiseMap(256, 256);

        // create Builder object
        NoiseMapBuilderPlane heightMapBuilder = new NoiseMapBuilderPlane();
        heightMapBuilder.setSourceModule(perlin1);
        heightMapBuilder.setDestNoiseMap(heightMap);
        heightMapBuilder.setDestSize(256, 256);
        heightMapBuilder.setBounds(2.0, 6.0, 1.0, 5.0);
        heightMapBuilder.build();

        // create renderer object
        RendererImage renderer = new RendererImage();

        // terrain gradient
        renderer.clearGradient();
//      renderer.addGradientPoint (-1.0000, new ColorCafe (  0,   0, 128, 255)); // deeps
//      renderer.addGradientPoint (-0.2500, new ColorCafe (  0,   0, 255, 255)); // shallow
//      renderer.addGradientPoint ( 0.0000, new ColorCafe (  0, 128, 255, 255)); // shore
//      renderer.addGradientPoint ( 0.0625, new ColorCafe (240, 240,  64, 255)); // sand
//      renderer.addGradientPoint ( 0.1250, new ColorCafe ( 32, 160,   0, 255)); // grass
//      renderer.addGradientPoint ( 0.3750, new ColorCafe (224, 224,   0, 255)); // dirt
//      renderer.addGradientPoint ( 0.7500, new ColorCafe (128, 128, 128, 255)); // rock
//      renderer.addGradientPoint ( 1.0000, new ColorCafe (255, 255, 255, 255)); // snow

        renderer.buildGrayscaleGradient();


        // Set up the texture renderer and pass the noise map to it.
        ImageCafe destTexture = new ImageCafe(heightMap.getWidth(), heightMap.getHeight());
        renderer.setSourceNoiseMap(heightMap);
        renderer.setDestImage(destTexture);
        renderer.enableLight(true);
        renderer.setLightContrast(3.0); // Triple the contrast
        renderer.setLightBrightness(2.0); // Double the brightness


        // Render the texture.
        renderer.render();

//        BufferedImage im = buffBuilder(destTexture.getHeight(), destTexture.getWidth(), destTexture);
//
//        try {
//            ImageIO.write(im, "png", new File("terrain_test.png"));
//        } catch (IOException e1) {
//            System.out.println("Could not write the image file.");
//        }


    }//end of main

    public BufferedImage getTerrainImage(boolean bGrayscale) throws ExceptionInvalidParam {
        // create Perlin noise module object
        Perlin perlin1 = new Perlin();
        
        perlin1.setSeed( new Long(System.currentTimeMillis()).intValue() );

        // create Noisemap object
        NoiseMap heightMap = new NoiseMap(256, 256);

        // create Builder object
        NoiseMapBuilderPlane heightMapBuilder = new NoiseMapBuilderPlane();
        heightMapBuilder.setSourceModule(perlin1);
        heightMapBuilder.setDestNoiseMap(heightMap);
        heightMapBuilder.setDestSize(256, 256);
        heightMapBuilder.setBounds(2.0, 6.0, 1.0, 5.0);
        heightMapBuilder.build();

        // create renderer object
        RendererImage renderer = new RendererImage();

        // terrain gradient
        renderer.clearGradient();
        if (  bGrayscale ) {
            renderer.buildGrayscaleGradient();
        } else {
            renderer.buildTerrainGradient();
        }

        // Set up the texture renderer and pass the noise map to it.
        ImageCafe destTexture = new ImageCafe(heightMap.getWidth(), heightMap.getHeight());
        renderer.setSourceNoiseMap(heightMap);
        renderer.setDestImage(destTexture);
        renderer.enableLight(true);
        renderer.setLightContrast(3.0); // Triple the contrast
        renderer.setLightBrightness(2.0); // Double the brightness


        // Render the texture.
        renderer.render();

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