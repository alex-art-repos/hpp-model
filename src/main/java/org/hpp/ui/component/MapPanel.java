/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.ui.component;

import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 *
 * @author Gautama
 */
public class MapPanel extends JPanel {
    protected BufferedImage backgroundImage = null;

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
    }

    public BufferedImage getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(BufferedImage backgroundImage) {
        this.backgroundImage = backgroundImage;
    }
    
}
