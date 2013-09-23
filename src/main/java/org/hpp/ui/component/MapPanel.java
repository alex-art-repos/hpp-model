/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.ui.component;

import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import org.hpp.terrain.TerrainPoint;

/**
 *
 * @author Gautama
 */
public class MapPanel extends JPanel {
    protected BufferedImage backgroundImage = null;
    protected BufferedImage townImage = null;
    protected TerrainPoint townImagePos = null;

    public MapPanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
    }

    public MapPanel(LayoutManager layout) {
        super(layout);
    }

    public MapPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
    }

    public MapPanel() {
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g); //To change body of generated methods, choose Tools | Templates.
        if ( backgroundImage != null ) {
            g.drawImage( backgroundImage, 0, 0, this);
        }
        
        if ( townImage != null ) {
            g.drawImage( townImage, townImagePos.getX(), townImagePos.getY() - townImage.getHeight()/2, this);
        }
    }

    public BufferedImage getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(BufferedImage backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public BufferedImage getTownImage() {
        return townImage;
    }

    public void setTownImage(BufferedImage townImage) {
        this.townImage = townImage;
    }

    public TerrainPoint getTownImagePos() {
        return townImagePos;
    }

    public void setTownImagePos(TerrainPoint townImagePos) {
        this.townImagePos = townImagePos;
    }
    
}
