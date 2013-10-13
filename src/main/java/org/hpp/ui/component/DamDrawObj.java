/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.ui.component;

import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.image.ImageObserver;
import java.util.List;
import org.hpp.model.HppAlgo;
import org.hpp.terrain.TerrainPoint;
import org.hpp.terrain.river.DamModel;
import org.hpp.terrain.river.FloodInfo;

/**
 *
 * @author root
 */
public class DamDrawObj implements IDrawObject {
    private HppAlgo algorithm = null;

    private boolean isShow = true;
    
    private boolean isShowFlood = false;
    
    public DamDrawObj() {
        super();
    }

    public DamDrawObj(HppAlgo algorithm) {
        super();
        this.algorithm = algorithm;
    }

    @Override
    public boolean isShow() {
        return isShow;
    }

    public void setIsShow(boolean isShow) {
        this.isShow = isShow;
    }

    public HppAlgo getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(HppAlgo algorithm) {
        this.algorithm = algorithm;
    }

    public boolean isShowFlood() {
        return isShowFlood;
    }

    public void setIsShowFlood(boolean isShowFlood) {
        this.isShowFlood = isShowFlood;
    }
    
    @Override
    public void paint(Graphics g, ImageObserver imgObs) {
        if ( algorithm == null ) {
            return;
        }
        
        FloodInfo flood = algorithm.getBestProjectFlood();
        
        if ( flood == null ) {
            return;
        }
        
        DamModel dam = flood.getDam();

        if ( dam == null ) {
            return;
        }
        
        g.drawLine(dam.getLeftPoint().getX(), dam.getLeftPoint().getY(), 
                dam.getRightPoint().getX(), dam.getRightPoint().getY());
        
        dam = flood.getUpperDam();
        
        if ( dam != null ) {
            g.drawLine(dam.getLeftPoint().getX(), dam.getLeftPoint().getY(), 
                    dam.getRightPoint().getX(), dam.getRightPoint().getY());
        }
        
        if ( flood.getTrapezeHeight() != null ) {
            TerrainPoint point1 = flood.getTrapezeHeight().get(0),
                         point2 = flood.getTrapezeHeight().get(1);
            
            g.drawLine(point1.getX(), point1.getY(), 
                    point2.getX(), point2.getY());
        }
        
        if ( isShowFlood ) {
            List<TerrainPoint> points = flood.getFloodAreaPoints();

            if ( points == null || points.isEmpty() ) {
                return;
            }

            Polygon polygon = new Polygon();

            for (TerrainPoint point : points) {
                polygon.addPoint(point.getX(), point.getY());
            }

            g.fillPolygon( polygon );
        }
    }
    
}
