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
import org.hpp.terrain.libnoise.module.ModuleBase;
import org.hpp.terrain.libnoise.module.Turbulence;
import org.hpp.terrain.libnoise.module.Add;
import org.hpp.terrain.libnoise.module.Voronoi;
import org.hpp.terrain.libnoise.module.Billow;
import org.hpp.terrain.libnoise.module.ScaleBias;
import org.hpp.terrain.libnoise.exception.ExceptionInvalidParam;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.hpp.terrain.libnoise.NoiseGen.NoiseQuality;



public class GraniteExample
{
   // generates an example Granite texture, as shown at
   // http://libnoise.sourceforge.net/examples/textures/index.html
   
   static final int TEXTURE_HEIGHT = 256;
   
   public static void main(String[] args) throws ExceptionInvalidParam 
   {
      
      // Primary granite texture.  This generates the "roughness" of the texture
      // when lit by a light source.
      Billow primaryGranite = new Billow();
      primaryGranite.setSeed (0);
      primaryGranite.setFrequency (8.0);
      primaryGranite.setPersistence (0.625);
      primaryGranite.setLacunarity (2.18359375);
      primaryGranite.setOctaveCount (6);
      primaryGranite.setNoiseQuality (NoiseQuality.QUALITY_STD);

      // Use Voronoi polygons to produce the small grains for the granite texture.
      Voronoi baseGrains = new Voronoi();
      baseGrains.setSeed (1);
      baseGrains.setFrequency (16.0);
      baseGrains.enableDistance (true);

      // Scale the small grain values so that they may be added to the base
      // granite texture.  Voronoi polygons normally generate pits, so apply a
      // negative scaling factor to produce bumps instead.
      ScaleBias scaledGrains = new ScaleBias(baseGrains);
      scaledGrains.setScale (-0.5);
      scaledGrains.setBias (0.0);

      // Combine the primary granite texture with the small grain texture.
      Add combinedGranite = new Add(primaryGranite, scaledGrains);

      // Finally, perturb the granite texture to add realism.
      Turbulence finalGranite = new Turbulence(combinedGranite);
      finalGranite.setSeed (2);
      finalGranite.setFrequency (4.0);
      finalGranite.setPower (1.0 / 8.0);
      finalGranite.setRoughness (6);
      
     
      // Given the granite noise module, create a non-seamless texture map
      // create Noisemap object
      NoiseMap textureMap = new NoiseMap(256, 256);
      textureMap = CreatePlanarTexture (finalGranite, false, TEXTURE_HEIGHT);

      // create renderer object
      RendererImage renderer = new RendererImage();

      // Create a gray granite palette.  Black and pink appear at either ends of
      // the palette; those colors provide the charactistic black and pink flecks
      // in granite.
      renderer.clearGradient (); 
      renderer.addGradientPoint (-1.0000, new ColorCafe (  0,   0,   0, 255));
      renderer.addGradientPoint (-0.9375, new ColorCafe (  0,   0,   0, 255));
      renderer.addGradientPoint (-0.8750, new ColorCafe (216, 216, 242, 255));
      renderer.addGradientPoint ( 0.0000, new ColorCafe (191, 191, 191, 255));
      renderer.addGradientPoint ( 0.5000, new ColorCafe (210, 116, 125, 255));
      renderer.addGradientPoint ( 0.7500, new ColorCafe (210, 113,  98, 255));
      renderer.addGradientPoint ( 1.0000, new ColorCafe (255, 176, 192, 255));

      
      // Set up the texture renderer and pass the noise map to it.
      ImageCafe destTexture = new ImageCafe(textureMap.getWidth(), textureMap.getHeight());
      renderer.setSourceNoiseMap (textureMap);
      renderer.setDestImage (destTexture);
      renderer.enableLight (true);
      renderer.setLightAzimuth (135.0);
      renderer.setLightElev (60.0);
      renderer.setLightContrast (2.0);
      renderer.setLightColor (new ColorCafe (255, 255, 255, 0));

      // Render the texture.
      renderer.render ();

      BufferedImage im = buffBuilder(destTexture.getHeight(), destTexture.getWidth(), destTexture);

      try
      {
         ImageIO.write(im, "png", new File("granite_test.png"));
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
