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
import org.hpp.terrain.libnoise.module.RidgedMulti;
import org.hpp.terrain.libnoise.module.Turbulence;
import org.hpp.terrain.libnoise.module.Add;
import org.hpp.terrain.libnoise.module.ScaleBias;
import org.hpp.terrain.libnoise.module.RotatePoint;
import org.hpp.terrain.libnoise.exception.ExceptionInvalidParam;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.hpp.terrain.libnoise.NoiseGen.NoiseQuality;



public class JadeExample
{
   // generates an example Jade texture, as shown at
   // http://libnoise.sourceforge.net/examples/textures/index.html
   
   static final int TEXTURE_HEIGHT = 256;
   
   public static void main(String[] args) throws ExceptionInvalidParam 
   {
      
      // Primary jade texture.  The ridges from the ridged-multifractal module
      // produces the veins.
      RidgedMulti primaryJade = new RidgedMulti();
      primaryJade.setSeed (0);
      primaryJade.setFrequency (2.0);
      primaryJade.setLacunarity (2.20703125);
      primaryJade.setOctaveCount (6);
      //primaryJade.setNoiseQuality (NoiseQuality.QUALITY_STD);

      // Base of the secondary jade texture.  The base texture uses concentric
      // cylinders aligned on the z axis, which will eventually be perturbed.
      Cylinders baseSecondaryJade = new Cylinders();
      baseSecondaryJade.setFrequency (2.0);

      // Rotate the base secondary jade texture so that the cylinders are not
      // aligned with any axis.  This produces more variation in the secondary
      // jade texture since the texture is parallel to the y-axis.
      RotatePoint rotatedBaseSecondaryJade = new RotatePoint (baseSecondaryJade);
      rotatedBaseSecondaryJade.setAngles (90.0, 25.0, 5.0);

      // Slightly perturb the secondary jade texture for more realism.
      Turbulence perturbedBaseSecondaryJade = new Turbulence(rotatedBaseSecondaryJade);
      perturbedBaseSecondaryJade.setSeed (1);
      perturbedBaseSecondaryJade.setFrequency (4.0);
      perturbedBaseSecondaryJade.setPower (1.0 / 4.0);
      perturbedBaseSecondaryJade.setRoughness (4);

      // Scale the secondary jade texture so it contributes a small part to the
      // final jade texture.
      ScaleBias secondaryJade = new ScaleBias(perturbedBaseSecondaryJade);
      secondaryJade.setScale (0.25);
      secondaryJade.setBias (0.0);

      // Add the two jade textures together.  These two textures were produced
      // using different combinations of coherent noise, so the final texture will
      // have a lot of variation.
      Add combinedJade = new Add(primaryJade, secondaryJade);

      // Finally, perturb the combined jade textures to produce the final jade
      // texture.  A low roughness produces nice veins.
      Turbulence finalJade = new Turbulence(combinedJade);
      finalJade.setSeed (2);
      finalJade.setFrequency (4.0);
      finalJade.setPower (1.0 / 16.0);
      finalJade.setRoughness (2);
      
     
      // Given the jade noise module, create a non-seamless texture map
      // create Noisemap object
      NoiseMap textureMap = new NoiseMap(256, 256);
      textureMap = CreatePlanarTexture (finalJade, false, TEXTURE_HEIGHT);

      // create renderer object
      RendererImage renderer = new RendererImage();

      // jade gradient
      renderer.clearGradient ();
      renderer.addGradientPoint (-1.000, new ColorCafe ( 24, 146, 102, 255));
      renderer.addGradientPoint ( 0.000, new ColorCafe ( 78, 154, 115, 255));
      renderer.addGradientPoint ( 0.250, new ColorCafe (128, 204, 165, 255));
      renderer.addGradientPoint ( 0.375, new ColorCafe ( 78, 154, 115, 255));
      renderer.addGradientPoint ( 1.000, new ColorCafe ( 29, 135, 102, 255));

      
      // Set up the texture renderer and pass the noise map to it.
      ImageCafe destTexture = new ImageCafe(textureMap.getWidth(), textureMap.getHeight());
      renderer.setSourceNoiseMap (textureMap);
      renderer.setDestImage (destTexture);

      // Render the texture.
      renderer.render ();

      BufferedImage im = buffBuilder(destTexture.getHeight(), destTexture.getWidth(), destTexture);

      try
      {
         ImageIO.write(im, "png", new File("jade_test.png"));
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