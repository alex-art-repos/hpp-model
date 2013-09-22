/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.domain;

/**
 *
 * @author Gautama
 */
public class MapGeneratorConfig {
    private int minRiverAngle = 0;
    private int maxRiverAngle = 80;

    private int minRiverEdge = 20;
    private int maxRiverEdge = 60;
    
    private int riverHeightDelta = 10;
    
    private int minRiverWidth = 10;
    private int maxRiverWidth = 30;
    
    private int minBankWidth = 5;
    private int maxBankWidth = 15;
    
    private int bankHeightDelta = 5;
    
    private boolean isNaturalGen = true;
    
    private int heightStability = 65;
    
    // Map params 
    private int mapWidth = 400;
    private int mapHeight = 400;

    private int mapScale = 10000; // 1:10000
    private int monitorScale = 4; // ~ 3.81
    
    // Perlin generator params
    private int minGenX = 1;
    private int maxGenX = 4;
    
    private int minGenZ = 1;
    private int maxGenZ = 2;
    
    public MapGeneratorConfig() {
        super();
    }

    public int getMinRiverAngle() {
        return minRiverAngle;
    }

    public void setMinRiverAngle(int minRiverAngle) {
        this.minRiverAngle = minRiverAngle;
    }

    public int getMaxRiverAngle() {
        return maxRiverAngle;
    }

    public void setMaxRiverAngle(int maxRiverAngle) {
        this.maxRiverAngle = maxRiverAngle;
    }

    public int getMinRiverEdge() {
        return minRiverEdge;
    }

    public void setMinRiverEdge(int minRiverEdge) {
        this.minRiverEdge = minRiverEdge;
    }

    public int getMaxRiverEdge() {
        return maxRiverEdge;
    }

    public void setMaxRiverEdge(int maxRiverEdge) {
        this.maxRiverEdge = maxRiverEdge;
    }

    public int getRiverHeightDelta() {
        return riverHeightDelta;
    }

    public void setRiverHeightDelta(int riverHeightDelta) {
        this.riverHeightDelta = riverHeightDelta;
    }

    public int getMinRiverWidth() {
        return minRiverWidth;
    }

    public void setMinRiverWidth(int minRiverWidth) {
        this.minRiverWidth = minRiverWidth;
    }

    public int getMaxRiverWidth() {
        return maxRiverWidth;
    }

    public void setMaxRiverWidth(int maxRiverWidth) {
        this.maxRiverWidth = maxRiverWidth;
    }

    public int getMinBankWidth() {
        return minBankWidth;
    }

    public void setMinBankWidth(int minBankWidth) {
        this.minBankWidth = minBankWidth;
    }

    public int getMaxBankWidth() {
        return maxBankWidth;
    }

    public void setMaxBankWidth(int maxBankWidth) {
        this.maxBankWidth = maxBankWidth;
    }

    public int getBankHeightDelta() {
        return bankHeightDelta;
    }

    public void setBankHeightDelta(int bankHeightDelta) {
        this.bankHeightDelta = bankHeightDelta;
    }

    public boolean isIsNaturalGen() {
        return isNaturalGen;
    }

    public void setIsNaturalGen(boolean isNaturalGen) {
        this.isNaturalGen = isNaturalGen;
    }

    public int getHeightStability() {
        return heightStability;
    }

    public void setHeightStability(int heightStability) {
        this.heightStability = heightStability;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public void setMapWidth(int mapWidth) {
        this.mapWidth = mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public void setMapHeight(int mapHeight) {
        this.mapHeight = mapHeight;
    }

    public int getMapScale() {
        return mapScale;
    }

    public void setMapScale(int mapScale) {
        this.mapScale = mapScale;
    }

    public int getMonitorScale() {
        return monitorScale;
    }

    public void setMonitorScale(int monitorScale) {
        this.monitorScale = monitorScale;
    }

    public int getMinGenX() {
        return minGenX;
    }

    public void setMinGenX(int minGenX) {
        this.minGenX = minGenX;
    }

    public int getMaxGenX() {
        return maxGenX;
    }

    public void setMaxGenX(int maxGenX) {
        this.maxGenX = maxGenX;
    }

    public int getMinGenZ() {
        return minGenZ;
    }

    public void setMinGenZ(int minGenZ) {
        this.minGenZ = minGenZ;
    }

    public int getMaxGenZ() {
        return maxGenZ;
    }

    public void setMaxGenZ(int maxGenZ) {
        this.maxGenZ = maxGenZ;
    }
    
    public int getPixelScale() {
        return (int)Math.ceil( this.getMapScale() / 1000f / this.getMonitorScale() );
    }

    @Override
    public String toString() {
        return "MapGeneratorConfig{" + "pixelScale =" + this.getPixelScale() + ", minRiverAngle=" + minRiverAngle + ", maxRiverAngle=" + maxRiverAngle + ", minRiverEdge=" + minRiverEdge + ", maxRiverEdge=" + maxRiverEdge + ", riverHeightDelta=" + riverHeightDelta + ", minRiverWidth=" + minRiverWidth + ", maxRiverWidth=" + maxRiverWidth + ", minBankWidth=" + minBankWidth + ", maxBankWidth=" + maxBankWidth + ", bankHeightDelta=" + bankHeightDelta + ", isNaturalGen=" + isNaturalGen + ", heightStability=" + heightStability + ", mapWidth=" + mapWidth + ", mapHeight=" + mapHeight + ", mapScale=" + mapScale + ", monitorScale=" + monitorScale + ", minGenX=" + minGenX + ", maxGenX=" + maxGenX + ", minGenZ=" + minGenZ + ", maxGenZ=" + maxGenZ + '}';
    }
    
}
