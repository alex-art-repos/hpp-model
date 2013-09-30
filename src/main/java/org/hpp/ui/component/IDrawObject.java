/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.ui.component;

import java.awt.Graphics;
import java.awt.image.ImageObserver;

/**
 *
 * @author root
 */
public interface IDrawObject {
    boolean isShow();
    
    void paint(Graphics g, ImageObserver imgObs);
}
