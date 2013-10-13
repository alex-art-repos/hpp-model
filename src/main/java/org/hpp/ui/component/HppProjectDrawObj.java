/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.ui.component;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import org.hpp.model.HppProject;
import org.hpp.terrain.TerrainPoint;
import org.hpp.terrain.river.DamModel;

/**
 *
 * @author Gautama
 */
public class HppProjectDrawObj implements IDrawObject {

    private boolean isShow = true;
    
    private HppProject project = null;
    
    private boolean isSpecial = false;
    
    private BufferedImage image = null;
    
    private double radius = 10;
    
    public HppProjectDrawObj() {
        super();
    }

    public HppProjectDrawObj(HppProject proj) {
        super();
        project = proj;
    }
    
    public HppProjectDrawObj(HppProject proj, boolean isVisible) {
        super();
        project = proj;
        isShow = isVisible;
    }
    
    @Override
    public boolean isShow() {
        return isShow;
    }

    @Override
    public void paint(Graphics g, ImageObserver imgObs) {
        if ( project == null ) {
            return ;
        }
        
        TerrainPoint point = null;
        DamModel dam = project.getDam();
        
        if ( dam != null ) {
            point = dam.getRiverIntersectPoint();
        } else {
            if ( project.getPair() != null ) {
                point = project.getPair().firstPoint();
            }
        }
        
        if ( point == null ) {
            return;
        }
        
        int sideLen = new Double(radius*2).intValue();
        
        if ( isSpecial ) {
            if ( image == null ) {
                g.fillRect(point.getX() - sideLen/2, 
                           point.getY() - sideLen/2, 
                           sideLen, sideLen);
            } else {
                g.drawImage( image, 
                             point.getX() - image.getHeight()/2, 
                             point.getY() - image.getHeight()/2, imgObs);
            }
        } else {
            g.fillOval(point.getX() - sideLen/2, 
                       point.getY() - sideLen/2, 
                       sideLen, sideLen);
        }
    }

    public void setIsShow(boolean isShow) {
        this.isShow = isShow;
    }

    public HppProject getProject() {
        return project;
    }

    public void setProject(HppProject project) {
        this.project = project;
    }

    public boolean isSpecial() {
        return isSpecial;
    }

    public void setIsSpecial(boolean isSpecial) {
        this.isSpecial = isSpecial;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
    
}
