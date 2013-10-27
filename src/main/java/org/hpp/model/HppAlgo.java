/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hpp.terrain.TerrainLine;
import org.hpp.terrain.TerrainModel;
import org.hpp.terrain.TerrainPoint;
import org.hpp.terrain.river.DamModel;
import org.hpp.terrain.river.FloodInfo;
import org.hpp.terrain.river.RiverEdge;
import org.hpp.terrain.river.RiverModel;
import org.hpp.terrain.river.RiverRange;
import org.hpp.terrain.river.TubeInfo;
import org.hpp.utils.LogHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gautama
 */
public class HppAlgo {
    public static enum HydroPlant {
        DAM, DERIVATE;
    }
    
    public static final Map<String, String> UNITS;
    
    static {
        Map<String, String> map = new HashMap<>();

        map.put("GenRate_norm0", "m^3/s");
        map.put("Popt_year_ud", "MWatt/h");
        map.put("Dam_begin", "point");
        map.put("Dam_end", "point");
        map.put("LengthRB", "km");
        map.put("Fall_min", "m");
        map.put("Fall_max", "m");
        map.put("Range_dam", "px");
        map.put("Range_fall", "m");
        map.put("NP", "MWatt");
        map.put("S_cal", "m^2");
        map.put("Range_rate", "m^3/s");
        
        UNITS = Collections.unmodifiableMap(map);
    }
    
    public static final Logger log = LoggerFactory.getLogger( HppAlgo.class );

    public static final double DISTANCE_DELTA = 0.001;
    
    private HppModel model = null;
    
    private TerrainPoint Dam_begin = null;
    private TerrainPoint Dam_end = null;
    
    private double LengthRB = 0;
    private double Fall_min = 0;
    private double Fall_max = 0;
    
    private double Range_dam = 0;
    
    private double Range_fall = 0;
    
    private double NP = 0;
    
    private FloodInfo calFlood = null; // calibrating flood
    
    private RiverRange intersection = null;
    
    private double GenRate_norm0 = 0;
    private double Range_rate = 0;
    private double Popt_year_ud = 0;
    
    private List<HppProject> projects = new ArrayList<>();
    private HppProject bestProject = null;
    
    private final HppAlgoFSM algoFSM = new HppAlgoFSM();
    
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

    public TerrainPoint getDam_begin() {
        return Dam_begin;
    }

    public TerrainPoint getDam_end() {
        return Dam_end;
    }

    public double getLengthRB() {
        return LengthRB;
    }

    public double getFall_min() {
        return Fall_min;
    }

    public double getFall_max() {
        return Fall_max;
    }

    public double getRange_dam() {
        return Range_dam;
    }

    public double getRange_fall() {
        return Range_fall;
    }

    public double getNP() {
        return NP;
    }

    public RiverRange getIntersection() {
        return intersection;
    }

    public FloodInfo getBestProjectFlood() {
        return bestProject == null ? null : bestProject.getFlood();
    }

    public double getS_cal() {
        return calFlood == null ? 0 : calFlood.getFloodArea();
    }

    public double getGenRate_norm0() {
        return GenRate_norm0;
    }

    public double getRange_rate() {
        return Range_rate;
    }

    public double getPopt_year_ud() {
        return Popt_year_ud;
    }

    public HppProject getBestProject() {
        return bestProject;
    }

    public void setBestProject(HppProject fakeProj) {
        bestProject = fakeProj;
    }
    
    public List<HppProject> getProjects() {
        return projects;
    }

    public void setProjects(List<HppProject> fakeProjs) {
        projects = fakeProjs;
    }

    public boolean isCompleted() {
        return algoFSM.getState() == HppAlgoFSM.AlgoState.COMPLETED;
    }
    
    public boolean isOk() {
        return algoFSM.isOk();
    }
    
    public String getStateString() {
        return algoFSM.getStateString();
    }
    
    public String getStateMsg() {
        return algoFSM.getMsg();
    }
    
    public HppAlgoFSM.AlgoState getState() {
        return algoFSM.getState();
    }
    
    public void block1() {
        if ( !algoFSM.enterBlock1() ) {
            log.warn("Can`t enter BLOCK1: " + algoFSM.getState());
            return;
        }
        
        if ( this.checkIntersection() ) {
            Dam_begin = intersection.firstPoint();
            Dam_end = intersection.lastPoint();
        } else {
            int townRadius = new Double(model.toPx(model.getDmax() * 1000)).intValue();            
            
            List<RiverEdge> edges = model.getRiverModel().edges();
            
            if ( TerrainPoint.distance( model.getTownModel().getCenter(), 
                    edges.get(0).getStart()) <= townRadius ) {
                Dam_begin = edges.get(1).getStart();
                Dam_end = edges.get(edges.size() - 1).getStop();
                log.debug(String.format("Used whole river."));
            } else {
                algoFSM.fail("No intersection.");
                return;
            }
        }
        
        log.debug(String.format("Dam_begin: %s, Dam_end: %s", Dam_begin, Dam_end));
        
        LengthRB = model.toKm( model.getRiverModel().getRiverRangeLength(intersection) );
        
        Fall_min = 1000 * ( model.getPmin()/(model.getRate() * HppModel.G * model.getEfficHP() ) );
        
        Fall_max = 1000 * ( model.getPmax()/( model.getRate() * HppModel.G * model.getEfficHP() ) );
        
        Range_dam = model.toPx( (LengthRB*1000) / model.getMaxCountRD() );
        
        Range_fall = (Fall_max - Fall_min)/ model.getMaxCountRF();
        
        NP = model.getCap_user() * 24 * 365;
        
        // damModel = DamModel.findNormalizeDam(model.getTerrainModel(), intersection.firstPair().getEdge(), Dam_begin, 15);
        DamModel damModel = null;
        
        try {
            damModel = DamModel.findMinimalDam(
                    model.getTerrainModel(), 
                    intersection.firstPair().getEdge(), 
                    Dam_begin, 
                    new Double(Fall_min).intValue());
        } catch (Exception exc) {
            log.warn(LogHelper.self().printException("Can`y find init dam. ", exc));
        }
        
        log.debug("Found dam: " + damModel + ", width = " + (damModel == null ? 0 : damModel.getWidth()) );

        if ( damModel != null ) {
            try {
                calFlood = this.floodArea(damModel);
            } catch (Exception exc) {
                log.warn(LogHelper.self().printException("Can`t find flood area. ", exc));
                algoFSM.fail("Can`t find flood area.");
                return;
            }
        } else {
            calFlood = null;
        }
        
        log.debug(String.format("Flood area: %f m^2", calFlood));

        log.debug("BLOCK1[" + new Date() + "]: " + this);
        
        algoFSM.ok(null);
    }
    
    public void block2() {
        if ( !algoFSM.enterBlock2() ) {
            log.warn("Can`t enter BLOCK2: " + algoFSM.getState());
            return;
        }
        
        GenRate_norm0 = model.getRate() * model.getVstok();
        
        log.debug("GenRate_norm0 = " + GenRate_norm0);
        
        if ( GenRate_norm0 > model.getRate_max() ) {
            GenRate_norm0 = model.getRate_max();
            log.debug("Set GenRate_norm0 to Rate_max = " + GenRate_norm0);
        } else if ( GenRate_norm0 < model.getRate_min() ) {
            model.setRate_min(0);
            log.debug("Set Rate_min to 0.");
        }
        
        Range_rate = (GenRate_norm0 - model.getRate_min()) / model.getMaxCountRR();
        
        double Rate_middleday = 0, rateMas[] = model.getRate_midleday(),
               GenRateNorm = 0, // m^3/s
               GenRate_day, NormRatePart, KPD_day, Fall_ud = 1,
               Phmid_ud, // Wt
               Pday_ud, //Wt/h
               Pyear_ud = 0 // MWt/h
               ;
        
        for (int CountRR = 0; CountRR < model.getMaxCountRR() ; CountRR++) {
            for (int day = 0; day < rateMas.length ; day++) {
                Rate_middleday = (model.getRate_dbmiddle() * model.getRate())/ rateMas[day];

                GenRateNorm = GenRate_norm0 - Range_rate * CountRR; // ???

                double rateMD_Vstok = Rate_middleday * model.getVstok();
                if ( rateMD_Vstok >= GenRateNorm ) {
                    GenRate_day = GenRateNorm;
                } else if ( model.getRate_min() < rateMD_Vstok ) {
                    GenRate_day = rateMD_Vstok;
                } else {
                    GenRate_day = 0;
                }

                NormRatePart = GenRate_day/GenRateNorm;

                KPD_day = -1.5625 * NormRatePart * ( NormRatePart - 1.6 ) * 9;

                Phmid_ud = HppModel.G * Fall_ud * GenRate_day * KPD_day * 3600;

                Pday_ud = Phmid_ud * 24;

                Pyear_ud += Pday_ud;
            }
            Pyear_ud = Pyear_ud / 1000000;
            
            if ( Popt_year_ud < Pyear_ud ) {
                Popt_year_ud = Pyear_ud;
            }
        }
        
        log.debug("BLOCK2[" + new Date() + "]: " + this);
        
        algoFSM.ok(null);
    }
    
    public void block3() {
        if ( !algoFSM.enterBlock3() ) {
            log.warn("Can`t enter BLOCK3: " + algoFSM.getState());
            return;
        }
        
        projects.clear();
        
        try {
            this.buildDamProjects();
        } catch (Exception exc) {
            log.warn(LogHelper.self().printException("Can`y build DAM projects: ", exc));
            algoFSM.fail("Can`t build DAM projects.");
            return;
        }
        
        try {
            this.buildDerivateProjects();
        } catch (Exception exc) {
            log.warn(LogHelper.self().printException("Can`y build DERIVATE projects: ", exc));
            algoFSM.fail("Can`t build DERIVATE projects.");
            return;
        }
        
        this.findBestProject();
        
        log.debug("BLOCK3[" + new Date() + "]: " + this);
        
        log.debug("BLOCK3[" + new Date() + "]: Project count = " + projects.size());
        
        algoFSM.ok(null);
    }

    protected void findBestProject() {
        if ( projects.isEmpty() ) {
            bestProject = null;
            return;
        }
        
        double rank = projects.get(0).getRank();
        
        for (HppProject project : projects) {
            if ( project.getRank() > rank ) {
                rank = project.getRank();
                bestProject = project;
            }
        }
    }
    
    protected void buildDamProjects() throws Exception {
        log.debug("Start building DAM projects.");
        
        int Count_rd = 0, Count_rf = 0;
        
        TerrainPoint Dam_cur = Dam_begin;
        double curDist = 0, Hmin, H, Fall_cur, P_cur;
        RiverRange.Pair curPair = null;
        
        RiverModel river = model.getRiverModel();

        DamModel dam = null;
        HppProject project = null;
        
        for (Count_rd = 0; Count_rd < model.getMaxCountRD(); Count_rd++) {
            curDist = Range_dam * Count_rd;
            log.debug(String.format("count[%d,%d]: dist = %f", Count_rd, Count_rf, curDist));

            curPair = river.findPointByDistance(
                    intersection.firstPair().getEdge(),
                    intersection.firstPoint(),
                    curDist,
                    DISTANCE_DELTA);

            log.debug(String.format("River pair: %s", curPair));
            if ( curPair == null ) {
                log.debug("No point for distance: " + intersection.firstPoint() + ", " + curDist);
                continue;
            }
            
            Dam_cur = curPair.firstPoint();

            Hmin = Fall_min;
            
            log.debug(String.format("Hmin=%f, point=%s", Hmin, Dam_cur));
            
            for (Count_rf = 0; Count_rf < model.getMaxCountRF(); Count_rf++) {
                H = Hmin + Count_rf * Range_fall;
                
                log.debug(String.format("H=%f, count_rf=%d", H, Count_rf));
                
                if ( H > Fall_max ) {
                    break;
                }
                
                Fall_cur = H;
                P_cur = (HppModel.G * H * model.getEfficHP() * model.getRate())/1000;
                
                FloodInfo flood = null;
                try {
                    dam = DamModel.findMinimalDam(model.getTerrainModel(), curPair.getEdge(), Dam_cur, new Double(H).intValue());
                    flood = this.floodArea(dam);
                } catch (Exception exc) {
                    log.debug(LogHelper.self().printException("No dam or flood for " + 
                            Dam_cur + ", H = " + H, exc));
                    continue;
                }

                if ( flood != null ) {
                    if ( calFlood == null ) {
                        calFlood = flood;
                    }
                } else {
                    continue;
                }
                
                log.debug(String.format("Found flood: %s", flood));
                
                project = new HppProject();
                
                project.setP_cur(P_cur);
                project.setFall_cur(Fall_cur);
                project.setPair( curPair );
                project.setPlantType( HydroPlant.DAM );
                
                project.setFlood(flood);
                project.setDam(dam);
                
                this.calcCost(project);
                
                log.debug(String.format("Test project: %s", project));
                
                if ( project.getTc_tot() > model.getCost() * 1000_000 ) { // cost - mln rub
                    break;
                }
                
                this.calcRank(project);
                
                log.debug(String.format("Save project: %s", project));
                
                projects.add(project);
            }
        }
    }
    
    protected void buildDerivateProjects() throws Exception {
        log.debug("Start building DERIVATE projects.");
        
        int Count_rd = 0, Count_rf = 0,
            H_dam_cur = 0, H_dem_end_cur = 0;
        
        RiverModel river = model.getRiverModel();

        TerrainPoint Dam_cur = Dam_begin, Dam_end_cur;
        double curDist = 0, curEndDist = 0, Fall_cur, P_cur, 
               intersectLength = river.getRiverRangeLength(intersection), 
               tubLen_km = 0,
               rangeDer = model.toPx( model.getRangeDER() );
        RiverRange.Pair curPair = null, curEndPair = null;
        
        HppProject project = null;
        
        log.debug(String.format("intersectLength = %f px", intersectLength));
            
    COUNT_RD:
        for (Count_rd = 0; Count_rd < model.getMaxCountRD(); Count_rd++) {
            curDist = Range_dam * Count_rd;

            log.debug(String.format("count[%d,%d]: dist = %f", Count_rd, Count_rf, curDist));
            
            curPair = river.findPointByDistance(
                    intersection.firstPair().getEdge(),
                    intersection.firstPoint(),
                    curDist,
                    DISTANCE_DELTA);

            log.debug(String.format("Tube start pair: %s", curPair));
            
            Dam_cur = curPair.firstPoint();
            Dam_end_cur = Dam_cur;
            
            if ( curDist + rangeDer > intersectLength ) {
                break;
            }

            for (Count_rf = 0; Count_rf < model.getMaxCountRF(); Count_rf++) {
                Fall_cur = Fall_min + Count_rf * Range_fall;

                curEndDist = curDist;
                
                log.debug(String.format("count_rf=%d, Fall_cur=%f,curEndDist= %f", Count_rf, Fall_cur, curEndDist));
                
                while (curEndDist <= intersectLength) {
                    curEndDist += rangeDer;
                    
                    log.debug(String.format("curEndDist = %f", curEndDist));
                    
                    if ( curEndDist > intersectLength ) {
                        continue COUNT_RD;
                    }
                    
                    curEndPair = river.findPointByDistance(
                            intersection.firstPair().getEdge(),
                            intersection.firstPoint(),
                            curEndDist, 
                            DISTANCE_DELTA);

                    log.debug(String.format("End river pair: %s", curEndPair));
                    
                    Dam_end_cur = curEndPair.firstPoint();

                    H_dam_cur = this.findHeight(Dam_cur);
                    H_dem_end_cur = this.findHeight(Dam_end_cur);
                    
                    double tubLen = TerrainPoint.distance3D(
                                Dam_cur, new Double(model.toPx( H_dam_cur )).intValue(), 
                                Dam_end_cur, new Double(model.toPx( H_dem_end_cur )).intValue()
                            );

                    tubLen_km = model.toKm(tubLen);
                    log.debug(String.format("TubLen: %f, %f km", tubLen, tubLen_km));
                    
                    if ( tubLen_km > model.getMaxLenTub() ) { // km
                        continue COUNT_RD;
                    }
                    
                    if ( Math.abs( H_dam_cur - H_dem_end_cur ) >= Fall_cur ) {
                        break;
                    }
                }
                
                if ( Math.abs( H_dam_cur - H_dem_end_cur ) < Fall_cur ) {
                    break;
                }
                
                P_cur = (HppModel.G * Fall_cur * model.getEfficHP() * model.getRate())/1000;
                
                project = new HppProject();
                
                project.setP_cur(P_cur);
                project.setFall_cur(Fall_cur);
                project.setPair( curPair );
                project.setPlantType( HydroPlant.DERIVATE );
                
                project.setTubeInfo( new TubeInfo(Dam_cur, Dam_end_cur, tubLen_km) );
                
                this.calcCost(project);
                
                log.debug(String.format("Test project: %s", project));
                
                if ( project.getTc_tot() > model.getCost() * 1000_000 ) { // mln. rub
                    break;
                }
                
                this.calcRank(project);
                
                log.debug(String.format("Save project: %s", project));
                
                projects.add(project);
            }
        }
    }
    
    protected void calcCost(HppProject project) {
        double cp[] = new double[5], 
               P_cur = project.getP_cur(), 
               Fall_cur = project.getFall_cur();
        
        cp[0] = model.getCp1()[0] * Math.pow(P_cur, model.getCp1()[1]) 
                * Math.pow(Fall_cur, model.getCp1()[2]);
        
        cp[1] = model.getCp2()[0] * Math.pow(P_cur, model.getCp2()[1]) 
                * Math.pow(Fall_cur, model.getCp2()[2]);
        
        cp[2] = model.getCp3()[0] * Math.pow(P_cur, model.getCp3()[1]) 
                * Math.pow(Fall_cur, model.getCp3()[2]);
        
        cp[3] = model.getCp4()[0] * Math.pow(P_cur, model.getCp4()[1]) 
                * Math.pow(Fall_cur, model.getCp4()[2]);
        
        cp[4] = model.getCp5()[0] * Math.pow(P_cur, model.getCp5()[1]) 
                * Math.pow(Fall_cur, model.getCp5()[2]);
        
        
        double co[] = new double[4];
        
        co[0] = model.getCo1()[0] * Math.pow(P_cur, model.getCo1()[1]) 
                * Math.pow(Fall_cur, model.getCo1()[2]);
        
        co[1] = model.getCo2()[0] * Math.pow(P_cur, model.getCo2()[1]) 
                * Math.pow(Fall_cur, model.getCo2()[2]);
        
        co[2] = model.getCo3()[0] * Math.pow(P_cur, model.getCo3()[1]) 
                * Math.pow(Fall_cur, model.getCo3()[2]);
        
        co[3] = model.getCo4()[0] * Math.pow(P_cur, model.getCo4()[1]) 
                * Math.pow(Fall_cur, model.getCo4()[2]);
        
        double Cc = 0;
        
        switch (project.getPlantType()) {
            case DAM:
                // Плотинная схема (руб.): Cс=0,5*(〖Cp〗_1+〖Cp〗_2+〖〖Cp〗_3+Cp〗_5) 
                Cc = 0.5 * (cp[0] + cp[1] + cp[2] + cp[4]);
                break;
            case DERIVATE:
                // Деривационная схема (руб.): Cс=0,5*(〖Cp〗_1+〖Cp〗_2+〖Cp〗_3+〖Cp〗_4)
                Cc = 0.5 * (cp[0] + cp[1] + cp[2] + cp[3]);
                break;
        }
        
        double Cc_tot = Cc * P_cur; // rub
        
        double Cem = 0.5 * (co[0] + co[1] + co[2] + co[3]); // rub on kWt
        
        double Cem_tot = Cem * P_cur; // rub
        
        double Tc = 1.13 * (Cc + Cem);
        
        double Tc_tot = Tc * P_cur;
        
        project.setCp(cp); 
        project.setCo(co); 
        
        project.setCc(Cc);
        project.setCc_tot(Cc_tot);
        project.setCem(Cem);
        project.setCem_tot(Cem_tot);
        project.setTc(Tc);
        project.setTc_tot(Tc_tot);
    }
    
    protected void calcRank(HppProject project) {
        double Pcur_year_ud = Popt_year_ud * project.getFall_cur();
        
        double NW = (HppModel.G * project.getFall_cur() * model.getRate() 
                * model.getVstok() * model.getEfficHP() * 3600 * 24 * 365) / 1000000;
        
        double D = TerrainPoint.distance(project.getPair().firstPoint(), model.getTownModel().getCenter());
        
        double k = Pcur_year_ud / NW;
        
        double c = Pcur_year_ud / NP;
        
        project.setK(k);
        project.setC(c);
        
        double rw = 1 / Math.sqrt(
                        model.getwD() * model.getwD() + 
                        model.getwL() * model.getwL() + 
                        model.getwS() * model.getwS() + 
                        model.getWk() *  model.getWk() + 
                        model.getWc() *  model.getWc() *  model.getwTc() *  model.getwTc()
                    );
        
        double plantTypeKoef = project.getPlantType() == HydroPlant.DAM 
                ? ( model.getwS() * project.getFloodS() ) / calFlood.getFloodArea()
                : ( model.getwL() * model.toKm(project.getTubLen()) ) / model.getMaxLenTub();
        
        double rank = rw * ( (-1) * (model.getwD() * D)/model.getDmax() 
                    - plantTypeKoef
                    + model.getWk() * k + model.getWc() * c 
                    - (model.getwTc() * project.getTc_tot()) / model.getCost()
                    
                );
        
        project.setRank(rank);
    }
    
    public boolean checkIntersection() {
        int torwnRadius = new Double(model.toPx(model.getDmax() * 1000)).intValue();
        
        intersection = model.getRiverModel().circleIntersectionRange(
                model.getTownModel().getCenter(), 
                torwnRadius
            );
        
        if ( intersection.isEmpty() ) {
            log.debug("No intersection with " + model.getTownModel() + ", radius = " + torwnRadius);
            return false;
        } else {
            log.debug("Intersection: " + intersection);
        }
        
        return true;
    }
    
    protected FloodInfo floodArea(DamModel dam) throws Exception {
        if ( dam == null ) {
            throw new Exception("No dam model.");
        }

        FloodInfo flood = new FloodInfo();
        
        flood.setDam(dam);
        
        RiverEdge curEdge = null;
        
        TerrainPoint nextDamPoint = null, curBasePoint = null, workingPoint = null;
        int damHeight = 0, pxScale_2 = model.getTerrainModel().getPixelScale() * model.getTerrainModel().getPixelScale();
        double riverSquare =  0;
        
        damHeight = dam.getHeight();
        curEdge = dam.getRiverIntersectEdge();
        workingPoint = dam.getRiverIntersectPoint();
        
        while (curEdge != null && damHeight > 0) {
            curBasePoint = curEdge.getStart();
            
            if ( workingPoint.equals( curBasePoint ) ) {
                riverSquare += curEdge.getWidth() * curEdge.length();
                curEdge = curEdge.getPrevEdge();
                continue;
            }
            
            nextDamPoint = this.nextDamPoint(curBasePoint, workingPoint, 1);
            
            damHeight -= Math.abs( this.findHeight(workingPoint) - this.findHeight(nextDamPoint) );
            
            workingPoint = nextDamPoint;
        }
        
        if (curEdge == null) {
            log.debug("Start of river was reached (not implemented).");
            return null;
        }
        
        if (nextDamPoint == null) {
            log.debug("Dam point is null (not implemented).");
            return null;
        }
        
        log.debug(String.format("Upper dam point: %s", nextDamPoint));
        
        DamModel upperDam = DamModel.findMinimalDam(model.getTerrainModel(), curEdge, nextDamPoint, 1);
        flood.setUpperDam(upperDam);
        TerrainLine upperDamLine = upperDam.getLine();
        
        log.debug(String.format("Upper dam : %s", upperDam));
        
        TerrainPoint damIntersect = dam.getLine().intersection( upperDam.getLine() ),
                     leftPoint = null,
                     rightPoint = null;
        
        log.debug("Dams intersect: " + damIntersect);
        
        if ( damIntersect != null 
                && damIntersect.isBetween( dam.getLeftPoint(), dam.getRightPoint()) ) {
            upperDamLine = dam.getLine().parallelLineByPoint(nextDamPoint);
            log.debug("Corrected upper dam line: " + upperDamLine);
        } 
        
        leftPoint = DamModel.findLeftBank(model.getTerrainModel(), upperDamLine, nextDamPoint);
        rightPoint = DamModel.findRightBank(model.getTerrainModel(), upperDamLine, nextDamPoint);
        
        List<TerrainPoint> floodAreaPoints = new ArrayList<>();
        
        if ( leftPoint == null || rightPoint == null ) {
            log.debug("Can`t find upper dam bound points.");
        } else {
            floodAreaPoints.add( leftPoint );
            floodAreaPoints.add( rightPoint );
            floodAreaPoints.add( dam.getRightPoint() );
            floodAreaPoints.add( dam.getLeftPoint() );
        }
        
        flood.setFloodAreaPoints(floodAreaPoints);
        
        for (TerrainPoint point : floodAreaPoints) {
            log.debug("\tFlood area point:" + point);
        }
        
        double riverWidth = model.getRiverModel().averageWidth();
        if ( curEdge != null )  {
            riverWidth = curEdge.getWidth();
        }
        
        TerrainLine dmLine = dam.getLine();
        
        TerrainLine heightLine = dmLine.normalLineByPoint( leftPoint );
        log.debug(String.format("Height line: %s", heightLine));
        
        TerrainPoint heightPoint = dmLine.intersection(heightLine);
        
        List<TerrainPoint> trapezeHeight = new ArrayList<>();
        trapezeHeight.add( heightPoint );
        trapezeHeight.add( leftPoint );
        log.debug(String.format("Trapeze height: base = %s, left point = %s", heightPoint, leftPoint));
        
        flood.setTrapezeHeight(trapezeHeight);
        
        double height = TerrainPoint.distance(heightPoint, leftPoint);
        
        log.debug(String.format("Trapeze : a = %f, b = %f, h = %f", riverWidth, dam.getWidth(), height));
        
        double floodSquare = ( ( riverWidth + dam.getWidth() )/2 * height);
        
        if ( riverSquare > 0 ) {
            log.debug("River square : " + riverSquare);
            if (riverSquare < floodSquare) {
                log.debug(String.format("Real square %f, corrected %f", floodSquare, (floodSquare-riverSquare)));
                floodSquare -= riverSquare;
            }
        }
        
        flood.setFloodArea( floodSquare * pxScale_2 );
        return flood;
    }
    
    protected int findHeight(TerrainPoint point) {
        TerrainModel terrain = model.getTerrainModel();
        
        int height = terrain.getTerrainHeight(point);
        
        if ( height == TerrainModel.RIVER_MARKER_HEIGHT ) {
            height = model.getRiverModel().getHeight(point);
        } 
        
        return height;
    }
    
    protected TerrainPoint nextDamPoint(TerrainPoint basePoint, TerrainPoint point, int radius) {
        TerrainPoint candidates[] = new TerrainPoint[] {
            new TerrainPoint(point.getX() - radius, point.getY() - radius),
            new TerrainPoint(point.getX(), point.getY() - radius),
            new TerrainPoint(point.getX() + radius, point.getY() - radius),
            
            new TerrainPoint(point.getX() + radius, point.getY()),
            
            new TerrainPoint(point.getX() + radius, point.getY() + radius),
            new TerrainPoint(point.getX(), point.getY() + radius),
            new TerrainPoint(point.getX() - radius, point.getY() + radius),
            
            new TerrainPoint(point.getX() - radius, point.getY())
        };
        
        TerrainPoint closestPoint = candidates[0];
        
        for (TerrainPoint candPoint : candidates ) {
            if ( TerrainPoint.distance(candPoint, basePoint) < TerrainPoint.distance(closestPoint, basePoint) ) {
                closestPoint = candPoint;
            }
        }
        
        return closestPoint;
    }

    @Override
    public String toString() {
        return "HppAlgo{" + "model=" + model + ", state=" + algoFSM + ", Dam_begin=" + Dam_begin + ", Dam_end=" + Dam_end + ", LengthRB=" + LengthRB + ", Fall_min=" + Fall_min + ", Fall_max=" + Fall_max + ", Range_dam=" + Range_dam + ", Range_fall=" + Range_fall + ", NP=" + NP + ", calFlood=" + calFlood + ", intersection=" + intersection + ", GenRate_norm0=" + GenRate_norm0 + ", Range_rate=" + Range_rate + ", Popt_year_ud=" + Popt_year_ud + ", projects=" + projects + ", bestProject=" + bestProject + '}';
    }
}
