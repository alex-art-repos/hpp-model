/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.hpp.terrain.river.DamModel;
import org.hpp.terrain.river.FloodInfo;
import org.hpp.terrain.river.RiverRange;
import org.hpp.terrain.river.TubeInfo;

/**
 *
 * @author Gautama
 */
public class HppProject {

    public static final Map<String, String> UNITS;

    static {
        Map<String, String> map = new HashMap<>();

        map.put("Cc", "rub");
        map.put("Cc_tot", "rub");
        map.put("Cem", "rub");
        map.put("Cem_tot", "rub");
        map.put("Tc", "rub");
        map.put("Tc_tot", "rub");
        map.put("P_cur", "watt");
        map.put("Fall_cur", "m");
        map.put("FloodS", "m^2");
        map.put("TubLen", "km");

        UNITS = Collections.unmodifiableMap(map);
    }
    private double[] cp;
    private double[] co;
    private HppAlgo.HydroPlant plantType;
    private double Cc;
    private double Cc_tot; // rub
    private double Cem; // rub on kWt
    private double Cem_tot; // rub
    private double Tc;
    private double Tc_tot;
    private double P_cur;
    private double Fall_cur;
    private RiverRange.Pair pair;
    private FloodInfo flood;
    private DamModel dam;
    private TubeInfo tubeInfo;
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

    public HppAlgo.HydroPlant getPlantType() {
        return plantType;
    }

    public void setPlantType(HppAlgo.HydroPlant plantType) {
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
        return flood == null ? -1 : flood.getFloodArea();
    }

    public FloodInfo getFlood() {
        return flood;
    }

    public void setFlood(FloodInfo flood) {
        this.flood = flood;
    }

    public DamModel getDam() {
        return dam;
    }

    public void setDam(DamModel dam) {
        this.dam = dam;
    }

    public double getTubLen() {
        return tubeInfo == null ? -1 : tubeInfo.getTubeLen();
    }

    public TubeInfo getTubeInfo() {
        return tubeInfo;
    }

    public void setTubeInfo(TubeInfo tubeInfo) {
        this.tubeInfo = tubeInfo;
    }

    public double getRank() {
        return rank;
    }

    public void setRank(double rank) {
        this.rank = rank;
    }

    @Override
    public String toString() {
        return "HppProject{" + "cp=" + cp + ", co=" + co + ", plantType=" + plantType + ", Cc=" + Cc + ", Cc_tot=" + Cc_tot + ", Cem=" + Cem + ", Cem_tot=" + Cem_tot + ", Tc=" + Tc + ", Tc_tot=" + Tc_tot + ", P_cur=" + P_cur + ", Fall_cur=" + Fall_cur + ", pair=" + pair + ", flood=" + flood + ", dam=" + dam + ", tubeInfo=" + tubeInfo + ", rank=" + rank + '}';
    }

}
