/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.ui.component;

import java.awt.Graphics;
import java.awt.image.ImageObserver;
import org.hpp.terrain.town.TownModel;

/**
 *
 * @author root
 */
public class TownDrawObj extends ImageDrawObj {

    private int radius = 0;
    
    public TownDrawObj() {
        super();
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    @Override
    public void paint(Graphics g, ImageObserver imgObs) {
        super.paint(g, imgObs);
        
        if ( this.getPos() == null ) {
            return;
        }
        
        double boxSide = 2 * radius;// model.kmToPx( model.getDmax() );
        
        g.drawOval(
                this.getPos().getX() - new Double(boxSide/2).intValue(), 
                this.getPos().getY() - new Double(boxSide/2).intValue(), 
                new Double(boxSide).intValue(), 
                new Double(boxSide).intValue()
            );
    }
    
}
