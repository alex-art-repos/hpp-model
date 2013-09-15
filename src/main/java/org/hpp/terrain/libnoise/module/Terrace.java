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

package org.hpp.terrain.libnoise.module;

import org.hpp.terrain.libnoise.Interp;
import org.hpp.terrain.libnoise.Misc;
import org.hpp.terrain.libnoise.exception.ExceptionInvalidParam;

public class Terrace extends ModuleBase
{

   /// Noise module that maps the output value from a source module onto a
   /// terrace-forming curve.
   ///
   /// This noise module maps the output value from the source module onto a
   /// terrace-forming curve.  The start of this curve has a slope of zero;
   /// its slope then smoothly increases.  This curve also contains
   /// <i>control TerrainPoints</i> which resets the slope to zero at that TerrainPoint,
   /// producing a "terracing" effect.  Refer to the following illustration:
   ///
   /// @image html terrace.png
   ///
   /// To add a control TerrainPoint to this noise module, call the
   /// addControlTerrainPoint() method.
   ///
   /// An application must add a minimum of two control TerrainPoints to the curve.
   /// If this is not done, the getValue() method fails.  The control TerrainPoints
   /// can have any value, although no two control TerrainPoints can have the same
   /// value.  There is no limit to the number of control TerrainPoints that can be
   /// added to the curve.
   ///
   /// This noise module clamps the output value from the source module if
   /// that value is less than the value of the lowest control TerrainPoint or
   /// greater than the value of the highest control TerrainPoint.
   ///
   /// This noise module is often used to generate terrain features such as
   /// your stereotypical desert canyon.
   ///
   /// This noise module requires one source module.


   /// Number of control TerrainPoints stored in this noise module.
   int controlTerrainPointCount;

   /// Determines if the terrace-forming curve between all control TerrainPoints
   /// is inverted.
   boolean invertTerraces;

   /// Array that stores the control TerrainPoints.
   double [] controlTerrainPoints;


   public Terrace (ModuleBase sourceModule) throws ExceptionInvalidParam
   {
      super(1);
      setSourceModule(0, sourceModule);
      controlTerrainPointCount = 0;
      invertTerraces = false;
      controlTerrainPoints = new double [0];

   }

   /// Adds a control TerrainPoint to the terrace-forming curve.
   ///
   /// @param value The value of the control TerrainPoint to add.
   ///
   /// @pre No two control TerrainPoints have the same value.
   ///
   /// @throw ExceptionInvalidParam An invalid parameter was
   /// specified; see the preconditions for more information.
   ///
   /// Two or more control TerrainPoints define the terrace-forming curve.  The
   /// start of this curve has a slope of zero; its slope then smoothly
   /// increases.  At the control TerrainPoints, its slope resets to zero.
   ///
   /// It does not matter which order these TerrainPoints are added.
   public void addControlTerrainPoint (double value) throws ExceptionInvalidParam
   {
      // Find the insertion TerrainPoint for the new control TerrainPoint and insert the new
      // TerrainPoint at that position.  The control TerrainPoint array will remain sorted by
      // value.
      int insertionPos = findInsertionPos (value);
      insertAtPos (insertionPos, value);
   }


   /// Deletes all the control TerrainPoints on the terrace-forming curve.
   ///
   /// @post All control TerrainPoints on the terrace-forming curve are deleted.
   public void clearAllControlTerrainPoints ()
   {
      controlTerrainPoints = null;
      controlTerrainPointCount = 0;
   }

   /// Determines the array index in which to insert the control TerrainPoint
   /// into the internal control TerrainPoint array.
   ///
   /// @param value The value of the control TerrainPoint.
   ///
   /// @returns The array index in which to insert the control TerrainPoint.
   ///
   /// @pre No two control TerrainPoints have the same value.
   ///
   /// @throw ExceptionInvalidParam An invalid parameter was
   /// specified; see the preconditions for more information.
   ///
   /// By inserting the control TerrainPoint at the returned array index, this
   /// class ensures that the control TerrainPoint array is sorted by value.
   /// The code that maps a value onto the curve requires a sorted
   /// control TerrainPoint array.
   public int findInsertionPos (double value) throws ExceptionInvalidParam
   {
      int insertionPos;
      for (insertionPos = 0; insertionPos < controlTerrainPointCount; insertionPos++)
      {
         if (value < controlTerrainPoints[insertionPos])
            // We found the array index in which to insert the new control TerrainPoint.
            // Exit now.
            break;
         else if (value == controlTerrainPoints[insertionPos])
            // Each control TerrainPoint is required to contain a unique value, so throw
            // an exception.
            throw new ExceptionInvalidParam ("Invalid Parameter in Terrace Noise Moduled");        
      }
      return insertionPos;
   }

   public double getValue (double x, double y, double z)
   {
      assert (sourceModules[0] != null);
      assert (controlTerrainPointCount >= 2);

      // Get the output value from the source module.
      double sourceModuleValue = sourceModules[0].getValue (x, y, z);

      // Find the first element in the control TerrainPoint array that has a value
      // larger than the output value from the source module.
      int indexPos;
      for (indexPos = 0; indexPos < controlTerrainPointCount; indexPos++)
      {
         if (sourceModuleValue < controlTerrainPoints[indexPos])
            break;

      }

      // Find the two nearest control TerrainPoints so that we can map their values
      // onto a quadratic curve.
      int index0 = Misc.ClampValue (indexPos - 1, 0, controlTerrainPointCount - 1);
      int index1 = Misc.ClampValue (indexPos, 0, controlTerrainPointCount - 1);

      // If some control TerrainPoints are missing (which occurs if the output value from
      // the source module is greater than the largest value or less than the
      // smallest value of the control TerrainPoint array), get the value of the nearest
      // control TerrainPoint and exit now.
      if (index0 == index1)
         return controlTerrainPoints[index1];

      // Compute the alpha value used for linear interpolation.
      double value0 = controlTerrainPoints[index0];
      double value1 = controlTerrainPoints[index1];
      double alpha = (sourceModuleValue - value0) / (value1 - value0);
      if (invertTerraces)
      {
         alpha = 1.0 - alpha;
         double tempValue = value0;
         value0 = value1;
         value1 = tempValue;
      }

      // Squaring the alpha produces the terrace effect.
      alpha *= alpha;

      // Now perform the linear interpolation given the alpha value.
      return Interp.linearInterp (value0, value1, alpha);
   }

   /// Inserts the control TerrainPoint at the specified position in the
   /// internal control TerrainPoint array.
   ///
   /// @param insertionPos The zero-based array position in which to
   /// insert the control TerrainPoint.
   /// @param value The value of the control TerrainPoint.
   ///
   /// To make room for this new control TerrainPoint, this method reallocates
   /// the control TerrainPoint array and shifts all control TerrainPoints occurring
   /// after the insertion position up by one.
   ///
   /// Because the curve mapping algorithm in this noise module requires
   /// that all control TerrainPoints in the array be sorted by value, the new
   /// control TerrainPoint should be inserted at the position in which the
   /// order is still preserved.
   public void insertAtPos (int insertionPos, double value)
   {
      // Make room for the new control TerrainPoint at the specified position within
      // the control TerrainPoint array.  The position is determined by the value of
      // the control TerrainPoint; the control TerrainPoints must be sorted by value within
      // that array.
      double[] newControlTerrainPoints = new double[controlTerrainPointCount + 1];

      for (int i = 0; i < controlTerrainPointCount; i++)
      {
         if (i < insertionPos)
            newControlTerrainPoints[i] = controlTerrainPoints[i];
         else
            newControlTerrainPoints[i + 1] = controlTerrainPoints[i];  
      }

      controlTerrainPoints = newControlTerrainPoints;
      ++controlTerrainPointCount;

      // Now that we've made room for the new control TerrainPoint within the array,
      // add the new control TerrainPoint.
      controlTerrainPoints[insertionPos] = value;
   }

   /// Creates a number of equally-spaced control TerrainPoints that range from
   /// -1 to +1.
   ///
   /// @param controlTerrainPointCount The number of control TerrainPoints to generate.
   ///
   /// @pre The number of control TerrainPoints must be greater than or equal to
   /// 2.
   ///
   /// @post The previous control TerrainPoints on the terrace-forming curve are
   /// deleted.
   ///
   /// @throw ExceptionInvalidParam An invalid parameter was
   /// specified; see the preconditions for more information.
   ///
   /// Two or more control TerrainPoints define the terrace-forming curve.  The
   /// start of this curve has a slope of zero; its slope then smoothly
   /// increases.  At the control TerrainPoints, its slope resets to zero.
   void makeControlTerrainPoints (int controlTerrainPointCount) throws ExceptionInvalidParam
   {
      if (controlTerrainPointCount < 2)
         throw new ExceptionInvalidParam ("Invalid Parameter in Terrace Noise Module");

      clearAllControlTerrainPoints ();

      double terraceStep = 2.0 / ((double)controlTerrainPointCount - 1.0);
      double curValue = -1.0;
      for (int i = 0; i < (int)controlTerrainPointCount; i++)
      {
         addControlTerrainPoint (curValue);
         curValue += terraceStep;
      }
   }

   /// Returns a TerrainPointer to the array of control TerrainPoints on the
   /// terrace-forming curve.
   ///
   /// @returns A TerrainPointer to the array of control TerrainPoints in this noise
   /// module.
   ///
   /// Two or more control TerrainPoints define the terrace-forming curve.  The
   /// start of this curve has a slope of zero; its slope then smoothly
   /// increases.  At the control TerrainPoints, its slope resets to zero.
   ///
   /// Before calling this method, call getControlTerrainPointCount() to
   /// determine the number of control TerrainPoints in this array.
   ///
   /// It is recommended that an application does not store this TerrainPointer
   /// for later use since the TerrainPointer to the array may change if the
   /// application calls another method of this object.
   public double[] getControlTerrainPointArray ()
   {
      return controlTerrainPoints;
   }

   /// Returns the number of control TerrainPoints on the terrace-forming curve.
   ///
   /// @returns The number of control TerrainPoints on the terrace-forming
   /// curve.
   public int getControlTerrainPointCount ()
   {
      return controlTerrainPointCount;
   }

   /// Enables or disables the inversion of the terrace-forming curve
   /// between the control TerrainPoints.
   ///
   /// @param invert Specifies whether to invert the curve between the
   /// control TerrainPoints.
   public void invertTerraces (boolean invert)
   {
      if (invert)
         invertTerraces = invert;
   }

   /// Determines if the terrace-forming curve between the control
   /// TerrainPoints is inverted.
   ///
   /// @returns
   /// - @a true if the curve between the control TerrainPoints is inverted.
   /// - @a false if the curve between the control TerrainPoints is not
   ///   inverted.
   public boolean isTerracesInverted ()
   {
      return invertTerraces;
   }

}
