/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.ui.component;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import org.hpp.terrain.TerrainPoint;

/**
 *
 * @author root
 */
public class ImageDrawObj implements IDrawObject {
    private BufferedImage image = null;
    private TerrainPoint pos = null;
    
    private boolean isShow = true;
    
    public ImageDrawObj() {
        super();
    }

    @Override
    public void paint(Graphics g, ImageObserver imgObs) {
        if ( image != null && pos != null) {
            g.drawImage( image, pos.getX() - image.getHeight()/2, pos.getY() - image.getHeight()/2, imgObs);
        }
    }

    @Override
    public boolean isShow() {
        return isShow;
    }

    public void setIsShow(boolean isShow) {
        this.isShow = isShow;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public TerrainPoint getPos() {
        return pos;
    }

    public void setPos(TerrainPoint pos) {
        this.pos = pos;
    }

    @Override
    public String toString() {
        return "ImageDrawObj{" + "image=" + image + ", pos=" + pos + '}';
    }

}
