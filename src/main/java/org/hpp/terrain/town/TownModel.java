/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.terrain.town;

import org.hpp.terrain.TerrainPoint;

/**
 *
 * @author Gautama
 */
public class TownModel {
    private TerrainPoint center = null;

    public TownModel() {
        super();
    }

    public TerrainPoint getCenter() {
        return center;
    }

    public void setCenter(TerrainPoint center) {
        this.center = center;
    }

    @Override
    public String toString() {
        return "TownModel{" + "center=" + center + '}';
    }
}
