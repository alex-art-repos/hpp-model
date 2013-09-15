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
import org.hpp.terrain.libnoise.module.Cylinders;
import org.hpp.terrain.libnoise.module.ModuleBase;
import org.hpp.terrain.libnoise.module.ScalePoint;
import org.hpp.terrain.libnoise.module.Turbulence;
import org.hpp.terrain.libnoise.module.Add;
import org.hpp.terrain.libnoise.module.ScaleBias;
import org.hpp.terrain.libnoise.module.TranslatePoint;
import org.hpp.terrain.libnoise.module.RotatePoint;
import org.hpp.terrain.libnoise.module.Perlin;
import org.hpp.terrain.libnoise.exception.ExceptionInvalidParam;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.hpp.terrain.libnoise.NoiseGen.NoiseQuality;



public class WoodExample
{
   // generates an example Wood grain texture, as shown at
   // http://libnoise.sourceforge.net/examples/textures/index.html
   
   static final int TEXTURE_HEIGHT = 256;
   
   public static void main(String[] args) throws ExceptionInvalidParam 
   {
      
      // Base wood texture.  The base texture uses concentric cylinders aligned
      // on the z axis, like a log.
      Cylinders baseWood = new Cylinders();
      baseWood.setFrequency (16.0);

      // Perlin noise to use for the wood grain.
      Perlin woodGrainNoise = new Perlin();
      woodGrainNoise.setSeed (0);
      woodGrainNoise.setFrequency (48.0);
      woodGrainNoise.setPersistence (0.5);
      woodGrainNoise.setLacunarity (2.20703125);
      woodGrainNoise.setOctaveCount (3);
      woodGrainNoise.setNoiseQuality (NoiseQuality.QUALITY_STD);

      // Stretch the Perlin noise in the same direction as the center of the
      // log.  This produces a nice wood-grain texture.
      ScalePoint scaledBaseWoodGrain = new ScalePoint(woodGrainNoise);
      scaledBaseWoodGrain.setYScale (0.25);

      // Scale the wood-grain values so that they may be added to the base wood
      // texture.
      ScaleBias woodGrain = new ScaleBias(scaledBaseWoodGrain);
      woodGrain.setScale (0.25);
      woodGrain.setBias (0.125);

      // Add the wood grain texture to the base wood texture.
      Add combinedWood = new Add(baseWood, woodGrain);

      // Slightly perturb the wood texture for more realism.
      Turbulence perturbedWood = new Turbulence(combinedWood);
      perturbedWood.setSeed (1);
      perturbedWood.setFrequency (4.0);
      perturbedWood.setPower (1.0 / 256.0);
      perturbedWood.setRoughness (4);

      // Cut the wood texture a small distance from the center of the "log".
      TranslatePoint translatedWood = new TranslatePoint(perturbedWood);
      translatedWood.setZTranslation (1.48);

      // Cut the wood texture on an angle to produce a more interesting wood
      // texture.
      RotatePoint rotatedWood = new RotatePoint(translatedWood);
      rotatedWood.setAngles (84.0, 0.0, 0.0);

      // Finally, perturb the wood texture to produce the final texture.
      Turbulence finalWood = new Turbulence(rotatedWood);
      finalWood.setSeed (2);
      finalWood.setFrequency (2.0);
      finalWood.setPower (1.0 / 64.0);
      finalWood.setRoughness (4);
      
     
      // Given the wood noise module, create a non-seamless texture map
      // create Noisemap object
      NoiseMap textureMap = new NoiseMap(256, 256);
      textureMap = CreatePlanarTexture (finalWood, false, TEXTURE_HEIGHT);

      // create renderer object
      RendererImage renderer = new RendererImage();

      // Create a dark-stained wood palette
      renderer.clearGradient ();
      renderer.addGradientTerrainPoint (-1.00, new ColorCafe (189, 94, 4, 255));
      renderer.addGradientTerrainPoint ( 0.50, new ColorCafe (144, 48, 6, 255));
      renderer.addGradientTerrainPoint ( 1.00, new ColorCafe ( 60, 10, 8, 255));

      
      // Set up the texture renderer and pass the noise map to it.
      ImageCafe destTexture = new ImageCafe(textureMap.getWidth(), textureMap.getHeight());
      renderer.setSourceNoiseMap (textureMap);
      renderer.setDestImage (destTexture);

      // Render the texture.
      renderer.render ();

      BufferedImage im = buffBuilder(destTexture.getHeight(), destTexture.getWidth(), destTexture);

      try
      {
         ImageIO.write(im, "png", new File("wood_test.png"));
      }
      catch (IOException e1)
      {
         System.out.println("Could not write the image file.");
      }


   }//end of main

   static NoiseMap CreatePlanarTexture (ModuleBase noiseModule, boolean seamless,
         int height) throws ExceptionInvalidParam
   {
      // Map the output values from the noise module onto a plane.  This will
      // create a two-dimensional noise map which can be rendered as a flat
      // texture map.  
      NoiseMapBuilderPlane plane = new NoiseMapBuilderPlane(height,height);
      NoiseMap noiseMap = new NoiseMap(height, height);
      plane.setBounds (-1.0, 1.0, -1.0, 1.0);
      plane.setDestSize (height, height);
      plane.setSourceModule (noiseModule);
      plane.setDestNoiseMap (noiseMap);
      plane.enableSeamless (seamless);
      plane.build ();
 
      return noiseMap;
   }

   static BufferedImage buffBuilder(int height, int width, ImageCafe imageCafe)
   {

      BufferedImage im = new BufferedImage(height, width, BufferedImage.TYPE_INT_ARGB);

      for (int i = 0; i < height; i++)
      {
         for (int j = 0; j < width; j++)
         {
            int c = getRGBA(imageCafe.getValue(i, j));
            im.setRGB(i, j, c);
         }
      }
      return im;
   }

   public static final int getRGBA(ColorCafe colorCafe)
   {
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