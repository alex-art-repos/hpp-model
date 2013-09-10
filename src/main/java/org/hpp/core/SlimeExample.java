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
import org.hpp.terrain.libnoise.module.RidgedMulti;
import org.hpp.terrain.libnoise.module.Turbulence;
import org.hpp.terrain.libnoise.module.Select;
import org.hpp.terrain.libnoise.module.Billow;
import org.hpp.terrain.libnoise.module.ScaleBias;
import org.hpp.terrain.libnoise.exception.ExceptionInvalidParam;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.hpp.terrain.libnoise.NoiseGen.NoiseQuality;



public class SlimeExample
{
   // generates an example Slime texture, as shown at
   // http://libnoise.sourceforge.net/examples/textures/index.html
   
   static final int TEXTURE_HEIGHT = 256;
   
   public static void main(String[] args) throws ExceptionInvalidParam 
   {
      
      // Large slime bubble texture.
      Billow largeSlime = new Billow();
      largeSlime.setSeed (0);
      largeSlime.setFrequency (4.0);
      largeSlime.setLacunarity (2.12109375);
      largeSlime.setOctaveCount (1);
      largeSlime.setNoiseQuality (NoiseQuality.QUALITY_BEST);

      // Base of the small slime bubble texture.  This texture will eventually
      // appear inside cracks in the large slime bubble texture.
      Billow smallSlimeBase = new Billow();
      smallSlimeBase.setSeed (1);
      smallSlimeBase.setFrequency (24.0);
      smallSlimeBase.setLacunarity (2.14453125);
      smallSlimeBase.setOctaveCount (1);
      smallSlimeBase.setNoiseQuality (NoiseQuality.QUALITY_BEST);

      // Scale and lower the small slime bubble values.
      ScaleBias smallSlime = new ScaleBias(smallSlimeBase);
      smallSlime.setScale (0.5);
      smallSlime.setBias (-0.5);

      // Create a map that specifies where the large and small slime bubble
      // textures will appear in the final texture map.
      RidgedMulti slimeMap = new RidgedMulti();
      slimeMap.setSeed (0);
      slimeMap.setFrequency (2.0);
      slimeMap.setLacunarity (2.20703125);
      slimeMap.setOctaveCount (3);
      slimeMap.setNoiseQuality (NoiseQuality.QUALITY_STD);

      // Choose between the large or small slime bubble textures depending on the
      // corresponding value from the slime map.  Choose the small slime bubble
      // texture if the slime map value is within a narrow range of values,
      // otherwise choose the large slime bubble texture.  The edge falloff is
      // non-zero so that there is a smooth transition between the two textures
      Select slimeChooser = new Select (largeSlime, smallSlime, slimeMap);
      slimeChooser.setBounds (-0.375, 0.375);
      slimeChooser.setEdgeFalloff (0.5);

      // Finally, perturb the slime texture to add realism.
      Turbulence finalSlime = new Turbulence(slimeChooser);
      finalSlime.setSeed (2);
      finalSlime.setFrequency (8.0);
      finalSlime.setPower (1.0 / 32.0);
      finalSlime.setRoughness (2);
      
     
      // Given the slime noise module, create a non-seamless texture map
      // create Noisemap object
      NoiseMap textureMap = new NoiseMap(256, 256);
      textureMap = CreatePlanarTexture (finalSlime, false, TEXTURE_HEIGHT);

      // create renderer object
      RendererImage renderer = new RendererImage();

      // Create a green slime palette.  A dirt brown color is used for very low
      // values while green is used for the rest of the values.
      renderer.clearGradient (); 
      renderer.addGradientPoint (-1.0000, new ColorCafe (160,  64,  42, 255));
      renderer.addGradientPoint (0.0000, new ColorCafe ( 64, 192,  64, 255));
      renderer.addGradientPoint (1.0000, new ColorCafe (128, 255, 128, 255));
      
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
         ImageIO.write(im, "png", new File("slime_test.png"));
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
