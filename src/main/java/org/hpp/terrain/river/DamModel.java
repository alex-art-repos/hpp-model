/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.terrain.river;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import org.hpp.terrain.TerrainLine;
import org.hpp.terrain.TerrainModel;
import org.hpp.terrain.TerrainPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gautama
 */
public class DamModel {
    public static final Logger log = LoggerFactory.getLogger( DamModel.class );
    
    private TerrainLine line = null;
    
    private TerrainPoint leftPoint = null;
    private TerrainPoint rightPoint = null;

    private int height = 0;
    
    private TerrainPoint riverIntersectPoint = null;
    private RiverEdge riverIntersecrEdge = null;
    
    public DamModel() {
        super();
    }

    public DamModel(TerrainLine line, TerrainPoint left, TerrainPoint right) {
        super();
        this.line = line;
        this.leftPoint = left;
        this.rightPoint = right;
    }
    
    public TerrainLine getLine() {
        return line;
    }

    public void setLine(TerrainLine line) {
        this.line = line;
    }

    public TerrainPoint getLeftPoint() {
        return leftPoint;
    }

    public void setLeftPoint(TerrainPoint leftPoint) {
        this.leftPoint = leftPoint;
    }

    public TerrainPoint getRightPoint() {
        return rightPoint;
    }

    public void setRightPoint(TerrainPoint rightPoint) {
        this.rightPoint = rightPoint;
    }

    public double getWidth() {
        return TerrainPoint.distance(leftPoint, rightPoint);
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public TerrainPoint getRiverIntersectPoint() {
        return riverIntersectPoint;
    }

    public void setRiverIntersectPoint(TerrainPoint riverIntersectionPoint) {
        this.riverIntersectPoint = riverIntersectionPoint;
    }

    public RiverEdge getRiverIntersectEdge() {
        return riverIntersecrEdge;
    }

    public void setRiverIntersectEdge(RiverEdge riverIntersectionEdge) {
        this.riverIntersecrEdge = riverIntersectionEdge;
    }

    /**
     *  Finds dam with minimal river width (bubble algorithm).
     * 
     * @param terrain
     * @param edge
     * @param point
     * @param damHeight
     * @return
     * @throws Exception
     */
    public static DamModel findMinimalDam(TerrainModel terrain, RiverEdge edge, TerrainPoint point, int damHeight) throws Exception {
        TerrainLine edgeLine = edge.getLine(),
                    damLine = edgeLine.normalLineByPoint(point);
        
        // log.debug("Dam line: " + damLine + ", point = " + point);
        
        List<TerrainPoint> riverSection = DamModel.findShortestSection(terrain, point, edge.getWidth());
        
        TerrainPoint leftBankPoint = null, rightBankPoint = null;
        
        if ( riverSection.isEmpty() ) {
            log.debug("fineDam: default algorithm.");
            leftBankPoint = DamModel.findLeftBank(terrain, damLine, point);
            rightBankPoint = DamModel.findRightBank(terrain, damLine, point);
        } else {
            log.debug("fineDam: used shortest section.");
            leftBankPoint = riverSection.get(0); 
            rightBankPoint = riverSection.get(1);
        }
        
        // log.debug("Dam banks: " + leftBankPoint + ", " + rightBankPoint);
        
        if ( leftBankPoint == null || rightBankPoint == null ) {
            throw new Exception("Can`t find river banks.");
        }
        
        TerrainPoint leftDamPoint = DamModel.findLeftDamPoint(terrain, damLine, leftBankPoint, damHeight), 
                     rightDamPoint = DamModel.findRightDamPoint(terrain, damLine, rightBankPoint, damHeight);
        
        if ( leftDamPoint == null || rightDamPoint == null ) {
            throw new Exception("Can`t find dam points.");
        }
        
        DamModel curDamModel = new DamModel(damLine, leftDamPoint, rightDamPoint);
        curDamModel.setHeight(damHeight);
        curDamModel.setRiverIntersectEdge(edge);
        curDamModel.setRiverIntersectPoint( point );
        
        return curDamModel;
    }
    
    private static List<TerrainPoint> findShortestSection(TerrainModel terrain, final TerrainPoint point, int radius) {
        List<TerrainPoint> scanedPoints = new ArrayList<>(4 * radius * radius + 1);
        int count = 0;
        for (int x = point.getX() - radius; x < point.getX() + radius; x++) {
            for (int y = point.getY() - radius; y < point.getY() + radius; y++) {
                TerrainPoint curPoint = new TerrainPoint(x, y);
                int curHeight = terrain.getTerrainHeight(curPoint);
                
                if ( curHeight != TerrainModel.RIVER_MARKER_HEIGHT ) {
                    count++;
                    scanedPoints.add( curPoint );
                }
            }
        }
        
        log.debug("findShortestSection: " + scanedPoints.size() + ", count = " + count);
        
        TerrainPoint[] sortedMas = scanedPoints.toArray( new TerrainPoint[scanedPoints.size()] );
        TerrainPoint shortest1 = null, shortest2 = null;
        double distance = Double.MAX_VALUE, curDist = 0;
        
        for (int i = 0; i < sortedMas.length; i++) {
            for (int j = 0; j < sortedMas.length; j++) {
                if ( i == j ) {
                    continue;
                }
                
                if ( point.isBetween(sortedMas[i], sortedMas[j]) ) {
                    curDist = TerrainPoint.distance(sortedMas[i], sortedMas[j]);
                    if ( distance > curDist ) {
                        distance = curDist;
                        shortest1 = sortedMas[i];
                        shortest2 = sortedMas[j];
                    }
                }
            }
        }
        
        if ( shortest1 != null && shortest2 != null ) {
            return Arrays.asList( shortest1, shortest2 );
        } else {
            return Collections.EMPTY_LIST;
        }
    }
    
    public static DamModel findNormalizeDam(TerrainModel terrain, RiverEdge edge, TerrainPoint point, int damHeight) throws Exception {
        TerrainLine edgeLine = edge.getLine(),
                    damLine = edgeLine.normalLineByPoint(point);
        
        // log.debug("Dam line: " + damLine + ", point = " + point);
        
        TerrainPoint leftBankPoint = DamModel.findLeftBank(terrain, damLine, point), 
                     rightBankPoint = DamModel.findRightBank(terrain, damLine, point);
        
        // log.debug("Dam banks: " + leftBankPoint + ", " + rightBankPoint);
        
        if ( leftBankPoint == null || rightBankPoint == null ) {
            throw new Exception("Can`t find river banks.");
        }
        
        TerrainPoint leftDamPoint = DamModel.findLeftDamPoint(terrain, damLine, leftBankPoint, damHeight), 
                     rightDamPoint = DamModel.findRightDamPoint(terrain, damLine, rightBankPoint, damHeight);
        
        if ( leftDamPoint == null || rightDamPoint == null ) {
            throw new Exception("Can`t find dam points.");
        }
        
        DamModel curDamModel = new DamModel(damLine, leftDamPoint, rightDamPoint);
        curDamModel.setHeight(damHeight);
        curDamModel.setRiverIntersectEdge(edge);
        curDamModel.setRiverIntersectPoint( point );
        
        return curDamModel;
    }
    
    public static TerrainPoint findLeftBank(TerrainModel terrain, TerrainLine damLine, TerrainPoint point) {
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
    
    public static TerrainPoint findRightBank(TerrainModel terrain, TerrainLine damLine, TerrainPoint point) {
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
    
    private static TerrainPoint findLeftDamPoint(TerrainModel terrain, TerrainLine damLine, TerrainPoint point, int damHeight) {
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
    
    private static TerrainPoint findRightDamPoint(TerrainModel terrain, TerrainLine damLine, TerrainPoint point, int damHeight) {
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
    
    @Override
    public String toString() {
        return "DamModel{" + "line=" + line + ", leftPoint=" + leftPoint + ", rightPoint=" + rightPoint + ", height=" + height + ", riverIntersectionPoint=" + riverIntersectPoint + ", riverIntersectionEdge=" + riverIntersecrEdge + '}';
    }

}
