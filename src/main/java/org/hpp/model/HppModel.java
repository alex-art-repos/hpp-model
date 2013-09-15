/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.model;

import org.hpp.terrain.TerrainMap;

/**
 *
 * @author Gautama
 */
public class HppModel {
    private TerrainMap heightMap = null;

    private int scale = 5; // TerrainPoint from each other 5m
    
    private double minP = 0; // Watt
    private double maxP = 0; // Watt
    
    // avg power consume (by hour)
    
    private double maxD = 0; // distance in km
    
    private double maxProjectCost = 0; // mln rub, Cost >= 25*Pmin
    
    // avg year consume m^3/s
    
    private double minRate = 0.5; // m^3/s
    private double maxRate = 20; // m^3/s
    
    private double stokV = 0; // V_stok (=0,1 default), %
    
    private int maxCountRD = 10;
    private int maxCountRF = 10;
    private int maxCountRR = 10;
    
    private int rangeDER = 100; // m
    
    private int maxLenTub = 5; // km
    
    private double efficHP = 0.5;
    
    //wD,wL,wS,wk,wc,wTc (=1default)
    private double wD = 1;
    private double wL = 1;
    private double wS = 1;
    private double wk = 1;
    private double wc = 1;
    private double wTc = 1;
    
    public HppModel() {
        super();
    }

    public TerrainMap getTerrainMap() {
        return heightMap;
    }

    public void setTerrainMap(TerrainMap heightMap) {
        this.heightMap = heightMap;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public double getMinP() {
        return minP;
    }

    public void setMinP(double minP) {
        this.minP = minP;
    }

    public double getMaxP() {
        return maxP;
    }

    public void setMaxP(double maxP) {
        this.maxP = maxP;
    }

    public double getMaxD() {
        return maxD;
    }

    public void setMaxD(double maxD) {
        this.maxD = maxD;
    }

    public double getMaxProjectCost() {
        return maxProjectCost;
    }

    public void setMaxProjectCost(double maxProjectCost) {
        this.maxProjectCost = maxProjectCost;
    }

    public double getMinRate() {
        return minRate;
    }

    public void setMinRate(double minRate) {
        this.minRate = minRate;
    }

    public double getMaxRate() {
        return maxRate;
    }

    public void setMaxRate(double maxRate) {
        this.maxRate = maxRate;
    }

    public double getStokV() {
        return stokV;
    }

    public void setStokV(double stokV) {
        this.stokV = stokV;
    }

    public int getMaxCountRD() {
        return maxCountRD;
    }

    public void setMaxCountRD(int maxCountRD) {
        this.maxCountRD = maxCountRD;
    }

    public int getMaxCountRF() {
        return maxCountRF;
    }

    public void setMaxCountRF(int maxCountRF) {
        this.maxCountRF = maxCountRF;
    }

    public int getMaxCountRR() {
        return maxCountRR;
    }

    public void setMaxCountRR(int maxCountRR) {
        this.maxCountRR = maxCountRR;
    }

    public int getRangeDER() {
        return rangeDER;
    }

    public void setRangeDER(int rangeDER) {
        this.rangeDER = rangeDER;
    }

    public int getMaxLenTub() {
        return maxLenTub;
    }

    public void setMaxLenTub(int maxLenTub) {
        this.maxLenTub = maxLenTub;
    }

    public double getEfficHP() {
        return efficHP;
    }

    public void setEfficHP(double efficHP) {
        this.efficHP = efficHP;
    }

    public double getwD() {
        return wD;
    }

    public void setwD(double wD) {
        this.wD = wD;
    }

    public double getwL() {
        return wL;
    }

    public void setwL(double wL) {
        this.wL = wL;
    }

    public double getwS() {
        return wS;
    }

    public void setwS(double wS) {
        this.wS = wS;
    }

    public double getWk() {
        return wk;
    }

    public void setWk(double wk) {
        this.wk = wk;
    }

    public double getWc() {
        return wc;
    }

    public void setWc(double wc) {
        this.wc = wc;
    }

    public double getwTc() {
        return wTc;
    }

    public void setwTc(double wTc) {
        this.wTc = wTc;
    }

}
