/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.ui.ctrl;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import org.hpp.model.HppModel;
import org.hpp.terrain.TerrainMap;
import org.hpp.terrain.TerrainPoint;
import org.hpp.terrain.libnoise.util.NoiseMap;
import org.hpp.terrain.perlin.PerlinTerrainGenerator;
import org.hpp.terrain.river.RiverFormer;
import org.hpp.terrain.river.RiverGen;
import org.hpp.terrain.river.RiverMap;
import org.hpp.ui.MainForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gautama
 */
public class MainFormCtrl extends BaseController<MainForm> {
    protected Logger log = LoggerFactory.getLogger(this.getClass());
    
    protected BufferedImage mapImage = null;
    
    protected HppModel model = new HppModel();

    public MainFormCtrl(MainForm theForm) {
        super();
        
        if ( theForm == null ) {
            throw new NullPointerException("Form can`t be null.");
        }
        
        this.setForm(theForm);
    }

    public HppModel getModel() {
        return model;
    }
    
    public void newModel() {
        model = new HppModel();
    }
    
    public void saveMapToFile(String fileName) {
        if ( mapImage == null ) {
            return;
        }
        
        try {
            ImageIO.write(mapImage, "png", new File(fileName));
        } catch (IOException exc) {
            log.error("Can`t save the image to file: " + exc.toString());
            JOptionPane.showConfirmDialog(this.getForm(), "Can`t save file.", "Error", JOptionPane.OK_OPTION, JOptionPane.ERROR);
        }
    }
    
    public void generateMap(int width, int height, int pixelScale, int heightScale,
            double minX, double maxX, double minZ, double maxZ) {
        PerlinTerrainGenerator genMap = new PerlinTerrainGenerator();
        
        genMap.setSeed(null);
        
        NoiseMap noiseHeightMap = genMap.getHeightMap(width, height, minX, maxX, minZ, maxZ);

        genMap.invertDeeps(noiseHeightMap);
        
        if ( model == null ) {
            JOptionPane.showConfirmDialog(this.getForm(), "No model found.", "Error", JOptionPane.OK_OPTION, JOptionPane.ERROR);
            return;
        }
        
        TerrainMap terrainMap = new TerrainMap(noiseHeightMap, pixelScale, heightScale);
        model.setTerrainMap( terrainMap );

        log.debug(String.format("Terrain map was generated[%d, %d]", terrainMap.getMinHeight(), terrainMap.getMaxHeight()));
        
        RiverGen riverGen = new RiverGen();
        
        riverGen.setMinAngle(0);
        riverGen.setMaxAngle(80);
        
        riverGen.setMinEdgeLen(20);
        riverGen.setMaxEdgeLen(60);
        
        riverGen.setMinHeightDelta(10);
        riverGen.setMaxHeightDelta(20);
        
        riverGen.setMinWidth(10);
        riverGen.setMaxWidth(30);
        
        TerrainPoint startPos = new TerrainPoint(terrainMap.getMapWidth()/2, 0);
        
        RiverMap river = riverGen.genRiver(startPos, terrainMap);
        
        if ( river == null ) {
            JOptionPane.showConfirmDialog(this.getForm(), "Can`t generate river.", "Error", JOptionPane.OK_OPTION, JOptionPane.ERROR);
            return;
        }
        log.debug("River map was generated.");
        
        RiverFormer riverFormer = new RiverFormer();
        
        riverFormer.buildRiver(river, terrainMap);
        
        log.debug("River was formed.");
        
        mapImage = genMap.getTerrainImage( terrainMap.getInternalMap() );
    }
    
    public void paintMap(Graphics g) {
        MainForm form = this.getForm();
        
        if ( mapImage == null ) {
            log.warn("No current image.");
            return;
        }
        
        if ( g == null ) {
            g = form.mapPanel.getGraphics();
        }
        
        g.drawImage( mapImage, 0, 0, form);
    }
    
    public void paintMap() {
        MainForm form = this.getForm();
        
        if ( model != null && model.getTerrainMap() != null ) {
            Rectangle rect = form.mapPanel.getBounds();

            rect.width = model.getTerrainMap().getMapWidth();
            rect.height = model.getTerrainMap().getMapHeight();
            
            form.mapPanel.setBounds( rect );
        }
        
        form.mapPanel.setBackgroundImage( this.getMapImage() );
        
        form.mapPanel.repaint();
    }

    public BufferedImage getMapImage() {
        return mapImage;
    }

    public void exitApp() {
        System.exit(0);
    }
    
}
