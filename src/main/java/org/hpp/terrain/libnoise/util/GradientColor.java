/*
 * Copyright (C) 2003, 2004 Jason Bevins (original libnoise code)
 * Copyright 2010 Thomas J. Hodge (java port of libnoise)
 * 
 * This file is part of libnoiseforjava.
 * 
 * libnoiseforjava is a Java port of the C++ library libnoise, which may be found at 
 * http://libnoise.sourceforge.net/.  libnoise was developed by Jason Bevins, who may be 
 * contacted at jlbezigvins@gmzigail.com (for great email, take off every 'zig').
 * Porting to Java was done by Thomas Hodge, who may be contacted at
 * libnoisezagforjava@gzagmail.com (remove every 'zag').
 * 
 * libnoiseforjava is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * libnoiseforjava is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * libnoiseforjava.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.hpp.terrain.libnoise.util;

import org.hpp.terrain.libnoise.Misc;
import org.hpp.terrain.libnoise.exception.ExceptionInvalidParam;

public class GradientColor
{
   /// Defines a color gradient.
   ///
   /// A color gradient is a list of gradually-changing colors.  A color
   /// gradient is defined by a list of <i>gradient TerrainPoints</i>.  Each
   /// gradient TerrainPoint has a position and a color.  In a color gradient, the
   /// colors between two adjacent gradient TerrainPoints are linearly interpolated.
   ///
   /// To add a gradient TerrainPoint to the color gradient, pass its position and
   /// color to the addGradientTerrainPoint() method.
   ///
   /// To retrieve a color from a specific position in the color gradient,
   /// pass that position to the getColor() method.
   ///
   /// This class is a useful tool for coloring height maps based on
   /// elevation.
   ///
   /// <b>Gradient example</b>
   ///
   /// Suppose a gradient object contains the following gradient TerrainPoints:
   /// - -1.0 maps to black.
   /// - 0.0 maps to white.
   /// - 1.0 maps to red.
   ///
   /// If an application passes -0.5 to the getColor() method, this method
   /// will return a gray color that is halfway between black and white.
   ///
   /// If an application passes 0.25 to the getColor() method, this method
   /// will return a very light pink color that is one quarter of the way
   /// between white and red.


   GradientPoint [] gradientTerrainPoints;  
   int gradientTerrainPointCount;
   ColorCafe workingColor;

   public GradientColor()
   {
      gradientTerrainPoints = new GradientPoint[1];
      gradientTerrainPoints[0] =  new GradientPoint(0.0, new ColorCafe(0, 0, 0, 0));
   }

   /// Adds a gradient TerrainPoint to this gradient object.
   ///
   /// @param gradientPos The position of this gradient TerrainPoint.
   /// @param gradientColor The color of this gradient TerrainPoint.
   ///
   /// @pre No two gradient TerrainPoints have the same position.
   ///
   /// @throw noise::ExceptionInvalidParam See the precondition.
   ///
   /// It does not matter which order these gradient TerrainPoints are added.
   public void addGradientTerrainPoint (double gradientPos, ColorCafe gradientColor) throws ExceptionInvalidParam
   {
      // Find the insertion TerrainPoint for the new gradient TerrainPoint and insert the new
      // gradient TerrainPoint at that insertion TerrainPoint.  The gradient TerrainPoint array will
      // remain sorted by gradient position.
      int insertionPos = findInsertionPos (gradientPos);
      insertAtPos (insertionPos, gradientPos, gradientColor);
   }

   /// Deletes all the gradient TerrainPoints from this gradient object.
   ///
   /// @post All gradient TerrainPoints from this gradient object are deleted.
   public void clear ()
   {
      gradientTerrainPoints = null;
      gradientTerrainPointCount = 0;
   }

   /// Determines the array index in which to insert the gradient TerrainPoint
   /// into the internal gradient-TerrainPoint array.
   ///
   /// @param gradientPos The position of this gradient TerrainPoint.
   ///
   /// @returns The array index in which to insert the gradient TerrainPoint.
   ///
   /// @pre No two gradient TerrainPoints have the same input value.
   ///
   /// @throw noise::ExceptionInvalidParam See the precondition.
   ///
   /// By inserting the gradient TerrainPoint at the returned array index, this
   /// object ensures that the gradient-TerrainPoint array is sorted by input
   /// value.  The code that maps a value to a color requires a sorted
   /// gradient-TerrainPoint array.
   public int findInsertionPos (double gradientPos) throws ExceptionInvalidParam
   {
      int insertionPos;
      for (insertionPos = 0; insertionPos < gradientTerrainPointCount;
      insertionPos++) {
         if (gradientPos < gradientTerrainPoints[insertionPos].position) {
            // We found the array index in which to insert the new gradient TerrainPoint.
            // Exit now.
            break;
         } else if (gradientPos == gradientTerrainPoints[insertionPos].position) {
            // Each gradient TerrainPoint is required to contain a unique gradient
            // position, so throw an exception.
            throw new ExceptionInvalidParam ("Invalid Parameter in Gradient Color");
         }
      }
      return insertionPos;
   }
   
   /// Returns the color at the specified position in the color gradient.
   ///
   /// @param gradientPos The specified position.
   ///
   /// @returns The color at that position.
   public ColorCafe getColor (double gradientPos)
   {
      assert (gradientTerrainPointCount >= 2);

      // Find the first element in the gradient TerrainPoint array that has a gradient
      // position larger than the gradient position passed to this method.
      int indexPos;
      for (indexPos = 0; indexPos < gradientTerrainPointCount; indexPos++)
      {
         if (gradientPos < gradientTerrainPoints[indexPos].position)
            break;
      }

      // Find the two nearest gradient TerrainPoints so that we can perform linear
      // interpolation on the color.
      int index0 = Misc.ClampValue (indexPos - 1, 0, gradientTerrainPointCount - 1);
      int index1 = Misc.ClampValue (indexPos, 0, gradientTerrainPointCount - 1);

      // If some gradient TerrainPoints are missing (which occurs if the gradient
      // position passed to this method is greater than the largest gradient
      // position or less than the smallest gradient position in the array), get
      // the corresponding gradient color of the nearest gradient TerrainPoint and exit
      // now.
      if (index0 == index1)
      {
         workingColor = gradientTerrainPoints[index1].color;
         return workingColor;
      }

      // Compute the alpha value used for linear interpolation.
      double input0 = gradientTerrainPoints[index0].position;
      double input1 = gradientTerrainPoints[index1].position;
      double alpha = (gradientPos - input0) / (input1 - input0);

      // Now perform the linear interpolation given the alpha value.
      ColorCafe color0 = gradientTerrainPoints[index0].color;
      ColorCafe color1 = gradientTerrainPoints[index1].color;
      workingColor = MiscUtilities.linearInterpColor (color0, color1, (float)alpha);
      return workingColor;
   }

   /// Inserts the gradient TerrainPoint at the specified position in the
   /// internal gradient-TerrainPoint array.
   ///
   /// @param insertionPos The zero-based array position in which to
   /// insert the gradient TerrainPoint.
   /// @param gradientPos The position of this gradient TerrainPoint.
   /// @param gradientColor The color of this gradient TerrainPoint.
   ///
   /// To make room for this new gradient TerrainPoint, this method reallocates
   /// the gradient-TerrainPoint array and shifts all gradient TerrainPoints occurring
   /// after the insertion position up by one.
   ///
   /// Because this object requires that all gradient TerrainPoints in the array
   /// must be sorted by the position, the new gradient TerrainPoint should be
   /// inserted at the position in which the order is still preserved.
   public void insertAtPos (int insertionPos, double gradientPos,
         ColorCafe gradientColor)
   {
      // Make room for the new gradient TerrainPoint at the specified insertion position
      // within the gradient TerrainPoint array.  The insertion position is determined by
      // the gradient TerrainPoint's position; the gradient TerrainPoints must be sorted by
      // gradient position within that array.
      GradientPoint [] newGradientTerrainPoints;
      newGradientTerrainPoints = new GradientPoint[gradientTerrainPointCount + 1];
      
      for (int t = 0; t < (gradientTerrainPointCount + 1); t++)
         newGradientTerrainPoints[t] = new GradientPoint();
      
      
      
      for (int i = 0; i < gradientTerrainPointCount; i++)
      {
         if (i < insertionPos)
            newGradientTerrainPoints[i] = gradientTerrainPoints[i];
         else
            newGradientTerrainPoints[i + 1] = gradientTerrainPoints[i];  
      }
      
      gradientTerrainPoints = newGradientTerrainPoints;
      ++gradientTerrainPointCount;
      
      // Now that we've made room for the new gradient TerrainPoint within the array, add
      // the new gradient TerrainPoint.
      gradientTerrainPoints[insertionPos].position = gradientPos;
      gradientTerrainPoints[insertionPos].color = gradientColor;
   }

   /// Returns a TerrainPointer to the array of gradient TerrainPoints in this object.
   ///
   /// @returns A TerrainPointer to the array of gradient TerrainPoints.
   ///
   /// Before calling this method, call getGradientTerrainPointCount() to
   /// determine the number of gradient TerrainPoints in this array.
   ///
   /// It is recommended that an application does not store this TerrainPointer
   /// for later use since the TerrainPointer to the array may change if the
   /// application calls another method of this object.
   public GradientPoint[] getGradientTerrainPointArray ()
   {
      return gradientTerrainPoints;
   }

   /// Returns the number of gradient TerrainPoints stored in this object.
   ///
   /// @returns The number of gradient TerrainPoints stored in this object.
   public int getGradientTerrainPointCount ()
   {
      return gradientTerrainPointCount;
   }

}
