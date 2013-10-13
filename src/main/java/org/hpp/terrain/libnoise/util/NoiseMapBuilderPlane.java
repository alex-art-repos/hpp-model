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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.hpp.terrain.libnoise.Interp;
import org.hpp.terrain.libnoise.exception.ExceptionInvalidParam;
import org.hpp.terrain.libnoise.model.Plane;
import org.hpp.utils.LogHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoiseMapBuilderPlane extends NoiseMapBuilder {
    private static final Logger log = LoggerFactory.getLogger(NoiseMapBuilderPlane.class);

    /**
     * Perlin generation CAN`T be simply parallelized.
     */
    public class GenerateTask implements Runnable {

        private final Plane planeModel;

        private final int threadNum;
        private final int threadCount;

        public GenerateTask(Plane planeModel, int threadNum, int threadCount) {
            this.planeModel = planeModel;
            this.threadNum = threadNum;
            this.threadCount = threadCount;
        }

        @Override
        public void run() {
            double xExtent = upperXBound - lowerXBound;
            double zExtent = upperZBound - lowerZBound;
            double xDelta = xExtent / (double) destWidth;
            double zDelta = zExtent / (double) destHeight;
            double xCur = lowerXBound;
            double zCur = lowerZBound;

            for (int z = 0; z < destHeight; z++) {
//                if ( z % threadCount != threadNum ) {
//                    continue;
//                }
                
                xCur = lowerXBound;
                for (int x = 0; x < destWidth; x++) {
//                    if ( x % threadCount != threadNum ) {
//                        continue;
//                    }
                
                    double finalValue;

                    if (!isSeamlessEnabled) {
                        finalValue = planeModel.getValue(xCur, zCur);
                    } else {
                        double swValue, seValue, nwValue, neValue;
                        swValue = planeModel.getValue(xCur, zCur);
                        seValue = planeModel.getValue(xCur + xExtent, zCur);
                        nwValue = planeModel.getValue(xCur, zCur + zExtent);
                        neValue = planeModel.getValue(xCur + xExtent, zCur + zExtent);
                        double xBlend = 1.0 - ((xCur - lowerXBound) / xExtent);
                        double zBlend = 1.0 - ((zCur - lowerZBound) / zExtent);
                        double z0 = Interp.linearInterp(swValue, seValue, xBlend);
                        double z1 = Interp.linearInterp(nwValue, neValue, xBlend);
                        finalValue = Interp.linearInterp(z0, z1, zBlend);
                    }

                    destNoiseMap.setValue(x, z, finalValue);
                    xCur += xDelta;
                }
                zCur += zDelta;
                setCallback(z);
            }
        }
    }
    /// Builds a planar noise map.
    ///
    /// This class builds a noise map by filling it with coherent-noise values
    /// generated from the surface of a plane.
    ///
    /// This class describes these input values using (x, z) coordinates.
    /// Their y coordinates are always 0.0.
    ///
    /// The application must provide the lower and upper x coordinate bounds
    /// of the noise map, in units, and the lower and upper z coordinate
    /// bounds of the noise map, in units.
    ///
    /// To make a tileable noise map with no seams at the edges, call the
    /// enableSeamless() method.
    /// A flag specifying whether seamless tiling is enabled.
    boolean isSeamlessEnabled;
    /// Lower x boundary of the planar noise map, in units.
    double lowerXBound;
    /// Lower z boundary of the planar noise map, in units.
    double lowerZBound;
    /// Upper x boundary of the planar noise map, in units.
    double upperXBound;
    /// Upper z boundary of the planar noise map, in units.
    double upperZBound;

    public NoiseMapBuilderPlane() throws ExceptionInvalidParam {
        super();
        isSeamlessEnabled = false;
        lowerXBound = 0.0;
        lowerZBound = 0.0;
        upperXBound = 0.0;
        upperZBound = 0.0;
    }

    public NoiseMapBuilderPlane(int height, int width) throws ExceptionInvalidParam {
        super(height, width);
        isSeamlessEnabled = false;
        lowerXBound = 0.0;
        lowerZBound = 0.0;
        upperXBound = 0.0;
        upperZBound = 0.0;
    }

    @Override
    public void build() throws ExceptionInvalidParam {
        if (upperXBound <= lowerXBound
                || upperZBound <= lowerZBound
                || destWidth <= 0
                || destHeight <= 0
                || sourceModule == null
                || destNoiseMap == null) {
            throw new ExceptionInvalidParam("Invalid parameter in NoiseMapBuilderPlane");
        }

        // Resize the destination noise map so that it can store the new output
        // values from the source model.
        destNoiseMap.setSize(destWidth, destHeight);

        // Create the plane model.
        Plane planeModel = new Plane();
        planeModel.setModule(sourceModule);

        double xExtent = upperXBound - lowerXBound;
        double zExtent = upperZBound - lowerZBound;
        double xDelta = xExtent / (double) destWidth;
        double zDelta = zExtent / (double) destHeight;
        double xCur = lowerXBound;
        double zCur = lowerZBound;

        // Fill every TerrainPoint in the noise map with the output values from the model.
        for (int z = 0; z < destHeight; z++) {
            xCur = lowerXBound;
            for (int x = 0; x < destWidth; x++) {
                double finalValue;

                if (!isSeamlessEnabled) {
                    finalValue = planeModel.getValue(xCur, zCur);
                } else {
                    double swValue, seValue, nwValue, neValue;
                    swValue = planeModel.getValue(xCur, zCur);
                    seValue = planeModel.getValue(xCur + xExtent, zCur);
                    nwValue = planeModel.getValue(xCur, zCur + zExtent);
                    neValue = planeModel.getValue(xCur + xExtent, zCur + zExtent);
                    double xBlend = 1.0 - ((xCur - lowerXBound) / xExtent);
                    double zBlend = 1.0 - ((zCur - lowerZBound) / zExtent);
                    double z0 = Interp.linearInterp(swValue, seValue, xBlend);
                    double z1 = Interp.linearInterp(nwValue, neValue, xBlend);
                    finalValue = Interp.linearInterp(z0, z1, zBlend);
                }

                destNoiseMap.setValue(x, z, finalValue);
                xCur += xDelta;
            }
            zCur += zDelta;
            setCallback(z);
        }
    }

    public void build(int threads) throws ExceptionInvalidParam {
        if (upperXBound <= lowerXBound
                || upperZBound <= lowerZBound
                || destWidth <= 0
                || destHeight <= 0
                || sourceModule == null
                || destNoiseMap == null) {
            throw new ExceptionInvalidParam("Invalid parameter in NoiseMapBuilderPlane");
        }

        // Resize the destination noise map so that it can store the new output
        // values from the source model.
        destNoiseMap.setSize(destWidth, destHeight);

        // Create the plane model.
        Plane planeModel = new Plane();
        planeModel.setModule(sourceModule);

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            executor.submit( new GenerateTask(planeModel, i, threads) );
        }
        
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (Exception exc) {
            log.warn(LogHelper.self().printException("Waiting interruption. ", exc));
        }
    }
    
    /// Enables or disables seamless tiling.
    ///
    /// @param enable A flag that enables or disables seamless tiling.
    ///
    /// Enabling seamless tiling builds a noise map with no seams at the
    /// edges.  This allows the noise map to be tileable.
    public void enableSeamless(boolean enable) {
        isSeamlessEnabled = enable;
    }

    /// Returns the lower x boundary of the planar noise map.
    ///
    /// @returns The lower x boundary of the planar noise map, in units.
    public double getLowerXBound() {
        return lowerXBound;
    }

    /// Returns the lower z boundary of the planar noise map.
    ///
    /// @returns The lower z boundary of the noise map, in units.
    public double getLowerZBound() {
        return lowerZBound;
    }

    /// Returns the upper x boundary of the planar noise map.
    ///
    /// @returns The upper x boundary of the noise map, in units.
    public double getUpperXBound() {
        return upperXBound;
    }

    /// Returns the upper z boundary of the planar noise map.
    ///
    /// @returns The upper z boundary of the noise map, in units.
    public double getUpperZBound() {
        return upperZBound;
    }

    /// Determines if seamless tiling is enabled.
    ///
    /// @returns
    /// - @a true if seamless tiling is enabled.
    /// - @a false if seamless tiling is disabled.
    ///
    /// Enabling seamless tiling builds a noise map with no seams at the
    /// edges.  This allows the noise map to be tileable.
    public boolean isSeamlessEnabled() {
        return isSeamlessEnabled;
    }

    /// Sets the boundaries of the planar noise map.
    ///
    /// @param lowerXBound The lower x boundary of the noise map, in
    /// units.
    /// @param upperXBound The upper x boundary of the noise map, in
    /// units.
    /// @param lowerZBound The lower z boundary of the noise map, in
    /// units.
    /// @param upperZBound The upper z boundary of the noise map, in
    /// units.
    ///
    /// @pre The lower x boundary is less than the upper x boundary.
    /// @pre The lower z boundary is less than the upper z boundary.
    ///
    /// @throw ExceptionInvalidParam See the preconditions.
    public void setBounds(double lowerXBound, double upperXBound,
            double lowerZBound, double upperZBound) throws ExceptionInvalidParam {
        if (lowerXBound >= upperXBound || lowerZBound >= upperZBound) {
            throw new ExceptionInvalidParam("Invalid parameter in NoiseMapBuilderPlane");
        }

        this.lowerXBound = lowerXBound;
        this.upperXBound = upperXBound;
        this.lowerZBound = lowerZBound;
        this.upperZBound = upperZBound;
    }

    public void setLowerXBound(double lowerXBound) {
        this.lowerXBound = lowerXBound;
    }

    public void setLowerZBound(double lowerZBound) {
        this.lowerZBound = lowerZBound;
    }

    public void setUpperXBound(double upperXBound) {
        this.upperXBound = upperXBound;
    }

    public void setUpperZBound(double upperZBound) {
        this.upperZBound = upperZBound;
    }
}
