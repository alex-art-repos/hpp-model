/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.model;

import java.util.List;
import org.hpp.terrain.TerrainPoint;
import org.hpp.terrain.river.RiverEdge;
import org.hpp.terrain.river.RiverModel;
import org.hpp.terrain.river.RiverRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gautama
 */
public class HppAlgo {
    public static enum Status {
        NOT_STARTED, OK, FAIL;
    }
    
    public static final Logger log = LoggerFactory.getLogger( HppAlgo.class );

    private HppModel model = null;
    
    private HppAlgo.Status status = Status.NOT_STARTED;
    
    private TerrainPoint Dam_begin = null;
    private TerrainPoint Dam_end = null;
    
    private double LengthRB = 0;
    private double Fall_min = 0;
    private double Fall_max = 0;
    
    private double Range_dam = 0;
    
    private double Range_fall = 0;
    
    private double NP = 0;
    
    private RiverRange intersection = null;
    
    public HppAlgo(HppModel theModel) {
        super();
        model = theModel;
    }

    public HppModel getModel() {
        return model;
    }

    public void setModel(HppModel model) {
        this.model = model;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TerrainPoint getDam_begin() {
        return Dam_begin;
    }

    public void setDam_begin(TerrainPoint Dam_begin) {
        this.Dam_begin = Dam_begin;
    }

    public TerrainPoint getDam_end() {
        return Dam_end;
    }

    public void setDam_end(TerrainPoint Dam_end) {
        this.Dam_end = Dam_end;
    }

    public double getLengthRB() {
        return LengthRB;
    }

    public void setLengthRB(double LengthRB) {
        this.LengthRB = LengthRB;
    }

    public double getFall_min() {
        return Fall_min;
    }

    public void setFall_min(double Fall_min) {
        this.Fall_min = Fall_min;
    }

    public double getFall_max() {
        return Fall_max;
    }

    public void setFall_max(double Fall_max) {
        this.Fall_max = Fall_max;
    }

    public double getRange_dam() {
        return Range_dam;
    }

    public void setRange_dam(double Range_dam) {
        this.Range_dam = Range_dam;
    }

    public double getRange_fall() {
        return Range_fall;
    }

    public void setRange_fall(double Range_fall) {
        this.Range_fall = Range_fall;
    }

    public double getNP() {
        return NP;
    }

    public void setNP(double NP) {
        this.NP = NP;
    }
    
    public void block1() throws Exception {
        if ( !this.checkIntersection() ) {
            status = Status.FAIL;
            throw new Exception("No intersection.");
        }
        
        Dam_begin = intersection.firstPoint();
        Dam_end = intersection.lastPoint();
        
        log.debug(String.format("Dam_begin: %s, Dam_end: %s", Dam_begin, Dam_end));
        
        LengthRB = model.toKm( model.getRiverModel().getRiverRangeLength(intersection) );
        
        Fall_min = 1000 * ( model.getPmin()/(model.getRate() * HppModel.G * model.getEfficHP() ) );
        
        Fall_max = 1000 * ( model.getPmax()/( model.getRate() * HppModel.G * model.getEfficHP() ) );
        
        Range_dam = LengthRB / model.getMaxCountRD();
        
        Range_fall = (Fall_max - Fall_min)/ model.getMaxCountRF();
        
        NP = model.getCap_user() * 24 * 365;
        
        status = Status.OK;
    }
    
    public boolean checkIntersection() {
        double Dmax = model.getDmax(); // km
        
        Dmax *= 1000; // m
        Dmax /= model.getTerrainModel().getPixelScale();
        
        intersection = model.getRiverModel().circleIntersectionRange(
                model.getTownModel().getCenter(), 
                new Double(Dmax).intValue()
            );
        
        if ( intersection.isEmpty() ) {
            log.debug("No intersection with " + model.getTownModel() + ", radius = " + Dmax);
            return false;
        }
        
        return true;
    }
}
