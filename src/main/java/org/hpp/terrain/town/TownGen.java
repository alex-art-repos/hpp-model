/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.terrain.town;

import org.hpp.terrain.TerrainModel;
import org.hpp.terrain.TerrainPoint;
import org.hpp.terrain.river.RiverModel;

/**
 *
 * @author Gautama
 */
public class TownGen {

    public TownGen() {
        super();
    }

    public TownModel genTown(TerrainModel terrain, RiverModel river) {
        TownModel model = new TownModel();
        
        model.setCenter( new TerrainPoint( 30 , terrain.getMapHeight() / 2) );
        
        return model;
    }
}
