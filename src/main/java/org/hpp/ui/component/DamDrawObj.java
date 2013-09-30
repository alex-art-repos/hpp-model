/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.ui.component;

import java.awt.Graphics;
import java.awt.image.ImageObserver;
import org.hpp.model.HppAlgo;
import org.hpp.terrain.TerrainPoint;
import org.hpp.terrain.river.DamModel;

/**
 *
 * @author root
 */
public class DamDrawObj implements IDrawObject {
    private HppAlgo algorithm = null;

    private boolean isShow = true;
    
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
    
    @Override
    public void paint(Graphics g, ImageObserver imgObs) {
        if ( algorithm == null ) {
            return;
        }
        
        DamModel dam = algorithm.getDamModel();

        if ( dam == null ) {
            return;
        }
        
        g.drawLine(dam.getLeftPoint().getX(), dam.getLeftPoint().getY(), 
                dam.getRightPoint().getX(), dam.getRightPoint().getY());
        
        dam = algorithm.getUpperDam();
        
        if ( dam != null ) {
            g.drawLine(dam.getLeftPoint().getX(), dam.getLeftPoint().getY(), 
                    dam.getRightPoint().getX(), dam.getRightPoint().getY());
        }
        
        if ( algorithm.getTrapezeHeight() != null ) {
            TerrainPoint point1 = algorithm.getTrapezeHeight().get(0),
                         point2 = algorithm.getTrapezeHeight().get(1);
            
            g.drawLine(point1.getX(), point1.getY(), 
                    point2.getX(), point2.getY());
        }
    }
    
}
