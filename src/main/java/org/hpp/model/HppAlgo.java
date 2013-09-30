/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.model;

import java.util.ArrayList;
import java.util.List;
import org.hpp.terrain.TerrainLine;
import org.hpp.terrain.TerrainModel;
import org.hpp.terrain.TerrainPoint;
import org.hpp.terrain.river.DamModel;
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
    
    public static enum HydroPlant {
        DAM, DERIVATE;
    }
    
    public static class HppProject {
        private double[] cp;
        private double[] co;
        
        private HydroPlant plantType;
        
        private double Cc;
        
        private double Cc_tot ; // rub
        
        private double Cem; // rub on kWt
        
        private double Cem_tot; // rub
        
        private double Tc;
        
        private double Tc_tot;
        
        private double P_cur;
        private double Fall_cur;
        
        private RiverRange.Pair pair;
        
        private double floodS;
        private DamModel dam;
        
        private double tubLen = 0;
    
        private double rank;

        public HppProject() {
            super();
        }

        public HppProject(double[] cp, double[] co) {
            this.cp = cp;
            this.co = co;
        }

        public double[] getCp() {
            return cp;
        }

        public void setCp(double[] cp) {
            this.cp = cp;
        }

        public double[] getCo() {
            return co;
        }

        public void setCo(double[] co) {
            this.co = co;
        }

        public HydroPlant getPlantType() {
            return plantType;
        }

        public void setPlantType(HydroPlant plantType) {
            this.plantType = plantType;
        }

        public double getCc() {
            return Cc;
        }

        public void setCc(double Cc) {
            this.Cc = Cc;
        }

        public double getCc_tot() {
            return Cc_tot;
        }

        public void setCc_tot(double Cc_tot) {
            this.Cc_tot = Cc_tot;
        }

        public double getCem() {
            return Cem;
        }

        public void setCem(double Cem) {
            this.Cem = Cem;
        }

        public double getCem_tot() {
            return Cem_tot;
        }

        public void setCem_tot(double Cem_tot) {
            this.Cem_tot = Cem_tot;
        }

        public double getTc() {
            return Tc;
        }

        public void setTc(double Tc) {
            this.Tc = Tc;
        }

        public double getTc_tot() {
            return Tc_tot;
        }

        public void setTc_tot(double Tc_tot) {
            this.Tc_tot = Tc_tot;
        }

        public double getP_cur() {
            return P_cur;
        }

        public void setP_cur(double P_cur) {
            this.P_cur = P_cur;
        }

        public double getFall_cur() {
            return Fall_cur;
        }

        public void setFall_cur(double Fall_cur) {
            this.Fall_cur = Fall_cur;
        }

        public RiverRange.Pair getPair() {
            return pair;
        }

        public void setPair(RiverRange.Pair pair) {
            this.pair = pair;
        }

        public double getFloodS() {
            return floodS;
        }

        public void setFloodS(double floodS) {
            this.floodS = floodS;
        }

        public DamModel getDam() {
            return dam;
        }

        public void setDam(DamModel dam) {
            this.dam = dam;
        }

        public double getTubLen() {
            return tubLen;
        }

        public void setTubLen(double tubLen) {
            this.tubLen = tubLen;
        }

        public double getRank() {
            return rank;
        }

        public void setRank(double rank) {
            this.rank = rank;
        }

        @Override
        public String toString() {
            return "HppProject{" + "cp=" + cp + ", co=" + co + ", plantType=" + plantType + ", Cc=" + Cc + ", Cc_tot=" + Cc_tot + ", Cem=" + Cem + ", Cem_tot=" + Cem_tot + ", Tc=" + Tc + ", Tc_tot=" + Tc_tot + ", P_cur=" + P_cur + ", Fall_cur=" + Fall_cur + ", pair=" + pair + ", floodS=" + floodS + ", dam=" + dam + ", tubLen=" + tubLen + ", rank=" + rank + '}';
        }

    }
    
    public static final Logger log = LoggerFactory.getLogger( HppAlgo.class );

    public static final double DISTANCE_DELTA = 0.001;
    
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
    
    private double S_cal = 0;
    
    private RiverRange intersection = null;
    private DamModel damModel = null;
    private List<TerrainPoint> floodAreaPoints = null;
    
    private double GenRate_norm0 = 0;
    private double Range_rate = 0;
    private double Popt_year_ud = 0;
    
    private List<HppProject> projects = null;
    private HppProject bestProject = null;
    
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

    public DamModel getDamModel() {
        return damModel;
    }

    public List<TerrainPoint> getFloodArea() {
        return floodAreaPoints;
    }

    public double getS_cal() {
        return S_cal;
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

    public void block1() throws Exception {
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
                status = Status.FAIL;
                throw new Exception("No intersection.");
            }
        }
        
        log.debug(String.format("Dam_begin: %s, Dam_end: %s", Dam_begin, Dam_end));
        
        LengthRB = model.toKm( model.getRiverModel().getRiverRangeLength(intersection) );
        
        Fall_min = 1000 * ( model.getPmin()/(model.getRate() * HppModel.G * model.getEfficHP() ) );
        
        Fall_max = 1000 * ( model.getPmax()/( model.getRate() * HppModel.G * model.getEfficHP() ) );
        
        Range_dam = LengthRB / model.getMaxCountRD();
        
        Range_fall = (Fall_max - Fall_min)/ model.getMaxCountRF();
        
        NP = model.getCap_user() * 24 * 365;
        
        damModel = this.findDam(intersection.firstPair().getEdge(), Dam_begin, 15);
        
        log.debug("Found dam: " + damModel + ", width = " + damModel.getWidth() );
        
        S_cal = this.floodArea(damModel);
        
        log.debug(String.format("Flood area: %f m^2", S_cal));
        
        status = Status.OK;
    }
    
    public void block2() throws Exception {
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
               Pyear_ud = 0, // MWt/h
               GenRateNorm_opt
                ;
        
        double Pday_udMas[] = new double[rateMas.length],
               GenRateNormMas[] = new double[rateMas.length];
        
        for (int CountRR = 0; CountRR < model.getMaxCountRR() ; CountRR++) {
            for (int day = 0; day < rateMas.length ; day++) {
                Rate_middleday = (model.getRate_dbmiddle() * model.getRate())/ rateMas[day];

                GenRateNorm = GenRate_norm0 - Range_rate * CountRR; // ???

                GenRateNormMas[day] = GenRateNorm;

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

                Pday_udMas[day] = Pday_ud;

                Pyear_ud += Pday_ud;
            }
            Pyear_ud = Pyear_ud / 1000000;
            
            if ( Popt_year_ud < Pyear_ud ) {
                Popt_year_ud = Pyear_ud;
                // GenRateNorm_opt
            }
        }
    }
    
    public void block3() throws Exception {
        this.buildDamProjects();
        this.buildDerivateProjects();
        
        this.findBestProject();
    }

    protected void findBestProject() {
        if ( projects == null || projects.isEmpty() ) {
            bestProject = null;
            return;
        }
        
        double rank = Double.MIN_VALUE;
        
        for (HppProject project : projects) {
            if ( project.getRank() > rank ) {
                rank = project.getRank();
                bestProject = project;
            }
        }
    }
    
    protected void buildDamProjects() throws Exception {
        int Count_rd = 0, Count_rf = 0;
        
        TerrainPoint Dam_cur = Dam_begin;
        double curDist = 0, Hmin, H, Fall_cur, P_cur, S;
        RiverRange.Pair curPair = null;
        
        RiverModel river = model.getRiverModel();

        DamModel dam = null;
        HppProject project = null;
        
        projects = new ArrayList<>();
        
        for (; Count_rd < model.getMaxCountRD(); Count_rd++) {
            curDist += Range_dam * Count_rd;

            curPair = river.findPointByDistance(
                    intersection.firstPair().getEdge(),
                    intersection.firstPoint(),
                    curDist,
                    DISTANCE_DELTA);

            Dam_cur = curPair.firstPoint();

            Hmin = Fall_min;
            
            for (; Count_rf < model.getMaxCountRF(); Count_rf++) {
                H = Hmin + Count_rf * Range_fall;
                
                if ( H > Fall_max ) {
                    break;
                }
                
                Fall_cur = H;
                P_cur = (HppModel.G * H * model.getEfficHP() * model.getRate())/1000;
                
                dam = this.findDam(curPair.getEdge(), Dam_cur, new Double(H).intValue());
                S = this.floodArea(dam);
                
                project = new HppProject();
                
                project.setP_cur(P_cur);
                project.setFall_cur(Fall_cur);
                project.setPair( curPair );
                project.setPlantType( HydroPlant.DAM );
                
                project.setFloodS(S);
                project.setDam(dam);
                
                this.calcCost(project);
                
                if ( project.getTc_tot() > model.getCost() ) {
                    break;
                }
                
                this.calcRank(project);
                
                projects.add(project);
            }
        }
    }
    
    protected void buildDerivateProjects() throws Exception {
        int Count_rd = 0, Count_rf = 0,
            H_dam_cur = 0, H_dem_end_cur = 0;
        
        TerrainPoint Dam_cur = Dam_begin, Dam_end_cur;
        double curDist = 0, curEndDist = 0, Hmin, H, Fall_cur, P_cur, 
               intersectLength = intersection.length(), 
               tubLen = 0;
        RiverRange.Pair curPair = null, curEndPair = null;
        
        RiverModel river = model.getRiverModel();

        HppProject project = null;
        
        projects = new ArrayList<>();

    COUNT_RD:
        for (; Count_rd < model.getMaxCountRD(); Count_rd++) {
            curDist += Range_dam * Count_rd;

            curPair = river.findPointByDistance(
                    intersection.firstPair().getEdge(),
                    intersection.firstPoint(),
                    curDist,
                    DISTANCE_DELTA);

            Dam_cur = curPair.firstPoint();
            Dam_end_cur = Dam_cur;
            
            if ( curDist + model.getRangeDER() > intersectLength ) {
                break;
            }

            for (; Count_rf < model.getMaxCountRF(); Count_rf++) {
                Fall_cur = Fall_min + Count_rf * Range_fall;

                curEndDist = curDist;
                
                while (curEndDist <= intersectLength) {
                    curEndDist += model.getRangeDER();
                    
                    if ( curEndDist > intersectLength ) {
                        break COUNT_RD;
                    }
                    
                    curEndPair = river.findPointByDistance(
                            intersection.firstPair().getEdge(),
                            intersection.firstPoint(),
                            curEndDist, 
                            DISTANCE_DELTA);

                    Dam_end_cur = curEndPair.firstPoint();

                    H_dam_cur = this.findHeight(Dam_cur);
                    H_dem_end_cur = this.findHeight(Dam_end_cur);
                    
                    tubLen = TerrainPoint.distance3D(
                            Dam_cur, H_dam_cur, 
                            Dam_end_cur, H_dem_end_cur);

                    if ( tubLen > model.getMaxLenTub() ) {
                        break COUNT_RD;
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
                
                project.setTubLen( tubLen );
                
                this.calcCost(project);
                
                if ( project.getTc_tot() > model.getCost() ) {
                    break;
                }
                
                this.calcRank(project);
                
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
        
        // TODO not used
        double Coef_p_use = (project.getP_cur() * 24 * 365) / Pcur_year_ud;
        
        double NW = (HppModel.G * project.getFall_cur() * model.getRate() 
                * model.getVstok() * model.getEfficHP() * 3600 * 24 * 365) / 1000000;
        
        double D = TerrainPoint.distance(project.getPair().firstPoint(), model.getTownModel().getCenter());
        
        double k = Pcur_year_ud / NW;
        
        double c = Pcur_year_ud / NP;
        
        double rw = 1 / Math.sqrt(
                        model.getwD() * model.getwD() + 
                        model.getwL() * model.getwL() + 
                        model.getwS() * model.getwS() + 
                        model.getWk() *  model.getWk() + 
                        model.getWc() *  model.getWc() *  model.getwTc() *  model.getwTc()
                    );
        
        double rank = rw * ( (-1) * (model.getwD() * D)/model.getDmax() 
                    - ( model.getwL() * project.getTubLen() ) / model.getMaxLenTub()
                    - ( model.getwS() * project.getFloodS() ) / S_cal
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
    
    // TODO exclude square of river
    protected double floodArea(DamModel dam) throws Exception {
        if ( dam == null ) {
            throw new Exception("No dam model.");
        }

        RiverEdge curEdge = null;
        
        TerrainPoint nextDamPoint = null, curBasePoint = null, workingPoint = null;
        int damHeight = 0;
        
        damHeight = dam.getHeight();
        curEdge = dam.getRiverIntersectEdge();
        workingPoint = dam.getRiverIntersectPoint();
        
        while (curEdge != null && damHeight > 0) {
            curBasePoint = curEdge.getStart();
            
            if ( workingPoint.equals( curBasePoint ) ) {
                workingPoint = curEdge.getStop();
                curEdge = curEdge.getPrevEdge();
                continue;
            }
            
            nextDamPoint = this.nextDamPoint(curBasePoint, workingPoint, 1);
            
            damHeight -= Math.abs( this.findHeight(workingPoint) - this.findHeight(nextDamPoint) );
            
            workingPoint = nextDamPoint;
        }
        
        DamModel upperDam = this.findDam(curEdge, nextDamPoint, 1);
        TerrainLine upperDamLine = upperDam.getLine();
        
        TerrainPoint damIntersect = damModel.getLine().intersection( upperDam.getLine() ),
                     leftPoint = null,
                     rightPoint = null;
        
        if ( damIntersect != null 
                && damIntersect.isBetween( damModel.getLeftPoint(), damModel.getRightPoint()) ) {
            upperDamLine.setK( damModel.getLine().getK() ); // make parallel
        } 
        
        leftPoint = this.findLeftBank(upperDam.getLine(), nextDamPoint);
        rightPoint = this.findRightBank(upperDam.getLine(), nextDamPoint);
        
        floodAreaPoints = new ArrayList<>();
        floodAreaPoints.add( leftPoint );
        floodAreaPoints.add( rightPoint );
        floodAreaPoints.add( dam.getRightPoint() );
        floodAreaPoints.add( dam.getLeftPoint() );
        
        for (TerrainPoint point : floodAreaPoints) {
            log.debug("\tFlood area point:" + point);
        }
        
        double riverWidth = model.getRiverModel().averageWidth();
        if ( curEdge != null )  {
            riverWidth = curEdge.getWidth();
        }
        
        TerrainLine dmLine = dam.getLine();
        
        TerrainLine heightLine = dmLine.normalLineByPoint( nextDamPoint );
        TerrainPoint heightPoint = dmLine.intersection(heightLine);
        
        double height = TerrainPoint.distance(heightPoint, nextDamPoint);
        
        int pxScale = model.getTerrainModel().getPixelScale();
                 
        return (pxScale * pxScale) * ( ( riverWidth + dam.getWidth() )/2 * height);
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
    
    protected DamModel findDam(RiverEdge edge, TerrainPoint point, int damHeight) throws Exception {
        TerrainLine edgeLine = edge.getLine(),
                    damLine = edgeLine.normalLineByPoint(point);
        
        // log.debug("Dam line: " + damLine + ", point = " + point);
        
        TerrainPoint leftBankPoint = this.findLeftBank(damLine, point), 
                     rightBankPoint = this.findRightBank(damLine, point);
        
        // log.debug("Dam banks: " + leftBankPoint + ", " + rightBankPoint);
        
        if ( leftBankPoint == null || rightBankPoint == null ) {
            throw new Exception("Can`t find river banks.");
        }
        
        TerrainPoint leftDamPoint = this.findLeftDamPoint(damLine, leftBankPoint, damHeight), 
                     rightDamPoint = this.findRightDamPoint(damLine, rightBankPoint, damHeight);
        
        if ( leftDamPoint == null || rightDamPoint == null ) {
            throw new Exception("Can`t find dam points.");
        }
        
        DamModel curDamModel = new DamModel(damLine, leftDamPoint, rightDamPoint);
        curDamModel.setHeight(damHeight);
        curDamModel.setRiverIntersectEdge(edge);
        curDamModel.setRiverIntersectPoint( point );
        
        return curDamModel;
    }
    
    protected TerrainPoint findLeftBank(TerrainLine damLine, TerrainPoint point) {
        TerrainModel terrain = model.getTerrainModel();
        
        TerrainPoint curPoint = damLine.nextLeftPoint(point);
        
        if ( !terrain.contains(curPoint) ) {
            return null;
        }
        
        while(terrain.getTerrainHeight(curPoint) == TerrainModel.RIVER_MARKER_HEIGHT) {
            curPoint = damLine.nextLeftPoint(curPoint);
            if ( !terrain.contains(curPoint) ) {
                return null;
            }
        }
        
        return curPoint;
    }
    
    protected TerrainPoint findRightBank(TerrainLine damLine, TerrainPoint point) {
        TerrainModel terrain = model.getTerrainModel();
        
        TerrainPoint curPoint = damLine.nextRightPoint(point);
        
        if ( !terrain.contains(curPoint) ) {
            return null;
        }
        
        while(terrain.getTerrainHeight(curPoint) == TerrainModel.RIVER_MARKER_HEIGHT) {
            curPoint = damLine.nextRightPoint(curPoint);
            if ( !terrain.contains(curPoint) ) {
                return null;
            }
        }
        
        return curPoint;
    }
    
    protected TerrainPoint findLeftDamPoint(TerrainLine damLine, TerrainPoint point, int damHeight) {
        TerrainModel terrain = model.getTerrainModel();
        
        TerrainPoint curPoint = damLine.nextLeftPoint(point);
        
        int baseHeight = terrain.getTerrainHeight(point);
        
        if ( !terrain.contains(curPoint) ) {
            return null;
        }
        
        while(terrain.getTerrainHeight(curPoint) - baseHeight < damHeight) {
            curPoint = damLine.nextLeftPoint(curPoint);
            if ( !terrain.contains(curPoint) ) {
                return null;
            }
        }
        
        return curPoint;
    }
    
    protected TerrainPoint findRightDamPoint(TerrainLine damLine, TerrainPoint point, int damHeight) {
        TerrainModel terrain = model.getTerrainModel();
        
        TerrainPoint curPoint = damLine.nextRightPoint(point);
        
        int baseHeight = terrain.getTerrainHeight(point);
        
        if ( !terrain.contains(curPoint) ) {
            return null;
        }
        
        while(terrain.getTerrainHeight(curPoint) - baseHeight < damHeight) {
            curPoint = damLine.nextRightPoint(curPoint);
            if ( !terrain.contains(curPoint) ) {
                return null;
            }
        }
        
        return curPoint;
    }
}
