/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.ui.ctrl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.hpp.domain.MapGeneratorConfig;
import org.hpp.model.HppAlgo;
import org.hpp.model.HppModel;
import org.hpp.terrain.TerrainModel;
import org.hpp.terrain.TerrainPoint;
import org.hpp.terrain.libnoise.util.NoiseMap;
import org.hpp.terrain.perlin.PerlinTerrainGenerator;
import org.hpp.terrain.river.RiverFormer;
import org.hpp.terrain.river.RiverGen;
import org.hpp.terrain.river.RiverModel;
import org.hpp.terrain.town.TownGen;
import org.hpp.terrain.town.TownModel;
import org.hpp.ui.MainForm;
import org.hpp.ui.component.MapPanel;
import org.hpp.utils.LogHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gautama
 */
public class MainFormCtrl extends BaseController<MainForm> {
    public static final int HEIGHT_SCALE = 100; // 
    
    public static final Logger log = LoggerFactory.getLogger(MainFormCtrl.class);
    
    protected BufferedImage mapImage = null;
    protected BufferedImage townImage = null;
    
    protected HppModel model = null;
    protected HppAlgo algorithm = null;

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
        algorithm = null;
    }
    
    public void saveMapToFile(String fileName) {
        if ( mapImage == null ) {
            return;
        }
        
        try {
            ImageIO.write(mapImage, "png", new File(fileName));
        } catch (IOException exc) {
            log.error("Can`t save the image to file: " + exc.toString());
            JOptionPane.showMessageDialog(this.getForm(), "Can`t save file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        JOptionPane.showMessageDialog(this.getForm(), "Saved to " + fileName, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void saveMapScreenshotToFile(String fileName) {
        MapPanel mapPanel = this.getForm().mapPanel;
        BufferedImage screen = new BufferedImage(mapPanel.getWidth(), mapPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
        mapPanel.paint(screen.getGraphics());

        try {
            ImageIO.write(screen, "png", new File(fileName));
        } catch (IOException exc) {
            log.error("Can`t save the image to file: " + exc.toString());
            JOptionPane.showMessageDialog(this.getForm(), "Can`t save file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        JOptionPane.showMessageDialog(this.getForm(), "Saved to " + fileName, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public boolean generateMap(MapGeneratorConfig mapGenCfg) {
        log.debug("Generating map for " + mapGenCfg);
        
        PerlinTerrainGenerator genMap = new PerlinTerrainGenerator();
        
        genMap.setSeed(null);
        
        NoiseMap noiseHeightMap = genMap.getHeightMap(
                mapGenCfg.getMapWidth(), 
                mapGenCfg.getMapHeight(), 
                mapGenCfg.getMinGenX(), 
                mapGenCfg.getMaxGenX(), 
                mapGenCfg.getMinGenZ(), 
                mapGenCfg.getMaxGenZ()
            );

        genMap.invertDeeps(noiseHeightMap);
        
        if ( model == null ) {
            JOptionPane.showMessageDialog(this.getForm(), "No model found.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        int pixelScale = mapGenCfg.getPixelScale();
        
        TerrainModel terrainMap = new TerrainModel(noiseHeightMap, pixelScale, HEIGHT_SCALE);
        model.setTerrainModel( terrainMap );

        log.debug(String.format("Terrain map was generated[%d, %d]", terrainMap.getMinHeight(), terrainMap.getMaxHeight()));
        
        RiverGen riverGen = new RiverGen();
        
        riverGen.setMinAngle( mapGenCfg.getMinRiverAngle() );
        riverGen.setMaxAngle( mapGenCfg.getMaxRiverAngle() );
        
        riverGen.setMinEdgeLen( mapGenCfg.getMinRiverEdge() );
        riverGen.setMaxEdgeLen( mapGenCfg.getMaxRiverEdge() );
        
        riverGen.setMinWidth( mapGenCfg.getMinRiverWidth() );
        riverGen.setMaxWidth( mapGenCfg.getMaxRiverWidth() );
        
        TerrainPoint startPos = new TerrainPoint(terrainMap.getMapWidth()/2, 0);
        
        RiverModel river = null;

        if ( mapGenCfg.isIsNaturalGen() ) {
            river = riverGen.genNaturalRiver(startPos, terrainMap);
        } else {
            river = riverGen.genRiver(startPos, terrainMap);
        }
        
        if ( river == null ) {
            JOptionPane.showMessageDialog(this.getForm(), "Can`t generate river.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        log.debug("River map was generated.");

        if ( model != null ) {
            model.setRiverModel(river);
        }
        
        RiverFormer riverFormer = new RiverFormer();
        
        riverFormer.setRiverDeltaHeight( mapGenCfg.getRiverHeightDelta() );
        riverFormer.setMinBankWidth( mapGenCfg.getMinBankWidth() );
        riverFormer.setMaxBankWidth( mapGenCfg.getMaxBankWidth() );
        riverFormer.setBankHeightDelta( mapGenCfg.getBankHeightDelta() );
        riverFormer.setHeightStability( mapGenCfg.getHeightStability()/100f );
        
        riverFormer.buildRiver(river, terrainMap);
        // riverFormer.traceRiver(river, terrainMap);
        
        log.debug("River was formed.");
        
        TownGen townGen = new TownGen();
        
        TownModel townModel = townGen.genTown(terrainMap, river);
        
        log.debug("Town was generated.");
        
        if ( model != null ) {
            model.setTownModel(townModel);
        }
        
        mapImage = genMap.getTerrainImage( terrainMap.getInternalMap() );
        
        if ( townImage == null ) {
            townImage = this.loadTownImage();
        }
        
        return true;
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
        
        if ( model != null && model.getTerrainModel() != null ) {
            Rectangle rect = form.mapPanel.getBounds();

            rect.width = model.getTerrainModel().getMapWidth();
            rect.height = model.getTerrainModel().getMapHeight();
            
            form.mapPanel.setBounds( rect );
            form.mapPanel.setSize(rect.width, rect.height);
            form.mapPanel.setPreferredSize( new Dimension(rect.width, rect.height) );
            
            if ( model.getTownModel() != null ) {
                form.mapPanel.setTownImagePos( model.getTownModel().getCenter() );
            }
        }
        
        form.mapPanel.setBackgroundImage( this.getMapImage() );
        form.mapPanel.setTownImage( townImage );
        
        form.mapPanel.repaint();
    }
    
    public BufferedImage loadTownImage() {
        try {
            return ImageIO.read( this.getClass().getResourceAsStream("town.png") );
        } catch (Exception exc) {
            log.error("Can`t load town image: " + exc.toString());
            return null;
        }
    }

    public BufferedImage getMapImage() {
        return mapImage;
    }

    public void exitApp() {
        System.exit(0);
    }
    
    public void loadInputValues() {
        try {
            model = HppModel.loadFromFile(null, true);
        } catch (Exception exc) {
            log.error("Can`t load HPP model: " + exc.toString());
            JOptionPane.showMessageDialog(this.getForm(), "Can`t load model from " + 
                    HppModel.DEF_FILE_NAME, 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if ( model.getTerrainModel() != null ) {
            PerlinTerrainGenerator genMap = new PerlinTerrainGenerator();
            mapImage = genMap.getTerrainImage( model.getTerrainModel().getInternalMap() );
            if ( mapImage != null ) {
                this.paintMap();
            }
        }
        
        JOptionPane.showMessageDialog(this.getForm(), "Model loaded from " + 
                HppModel.DEF_FILE_NAME, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void saveInputValues() {
        if ( model == null ) {
            JOptionPane.showMessageDialog(this.getForm(), "No model.", "Warn", 
                    JOptionPane.WARNING_MESSAGE);
            return ;
        }
        
        try {
            model.saveToFile(null, true);
        } catch (Exception exc) {
            log.error("Can`t save HPP model: " + exc.toString());
            JOptionPane.showMessageDialog(this.getForm(), "Can`t save model to " + 
                    HppModel.DEF_FILE_NAME, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JOptionPane.showMessageDialog(this.getForm(), "Model saved to " + 
                HppModel.DEF_FILE_NAME, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void refreshInputValFromModel(HppModel curModel) {
        if ( curModel == null ) {
            curModel = model;
        }
        
        Method methods[] = HppModel.class.getDeclaredMethods();
        
        int count = 0;
        for (Method method : methods) {
            if ( method.getName().startsWith( "get" ) 
                    && ( method.getReturnType() == double.class ||
                         method.getReturnType() == int.class ||
                         method.getReturnType() == long.class
                        )
                    ) {
                count++;
            }
        }
        
        DefaultTableModel tableModel = new DefaultTableModel(new String[] {"Name", "Value"}, count);
        
        int rowInd = 0;
        for (Method method : methods) {
            if ( method.getName().startsWith( "get" ) 
                    && ( method.getReturnType() == double.class ||
                         method.getReturnType() == int.class ||
                         method.getReturnType() == long.class
                        )
                    ) {
                String rowName = method.getName().substring(3); // cut get
                Object rowValue = null;
                
                try {
                    rowValue = method.invoke(curModel, (Object[]) null);
                } catch (Exception exc) {
                    log.error("Can`t read value: (" + rowName + ") - " + exc.toString());
                }
                
                tableModel.setValueAt(rowName, rowInd, 0);
                tableModel.setValueAt(rowValue, rowInd, 1);
                
                rowInd++;
            }
        }
        
        this.getForm().inputValueTab.setModel(tableModel);
    }
    
    public void refreshInputValToModel(HppModel curModel) {
        if ( curModel == null ) {
            curModel = model;
        }
        
        TableModel tableModel = this.getForm().inputValueTab.getModel();
        
        Method methods[] = HppModel.class.getDeclaredMethods();
        
        int rowInd = 0;
        for (Method method : methods) {
            if ( method.getName().startsWith( "get" ) 
                    && ( method.getReturnType() == double.class ||
                         method.getReturnType() == int.class ||
                         method.getReturnType() == long.class
                        )
                    ) {
                String methodName = "set" + method.getName().substring(3); // cut "get"
                
                try {
                    Method setterMethod = HppModel.class.getMethod(methodName, method.getReturnType());
                    Object value = tableModel.getValueAt(rowInd, 1); // value
                    
                    if ( method.getReturnType() == double.class ) {
                        value = Double.parseDouble(value.toString());
                    } else if ( method.getReturnType() == int.class ) {
                        value = Integer.parseInt(value.toString());
                    } else if ( method.getReturnType() == long.class ) {
                        value = Long.parseLong(value.toString());
                    }
                    
                    setterMethod.invoke(curModel, value);
                } catch (Exception exc) {
                    log.error("Can`t write value: (" + methodName + ") - " + exc.toString());
                }
                
                rowInd++;
            }
        }
    }

    public void refreshAlgoValFromModel(HppAlgo curAlgo) {
        if ( curAlgo == null ) {
            curAlgo = algorithm;
        }
        
        Method methods[] = HppAlgo.class.getDeclaredMethods();
        
        int count = 0;
        for (Method method : methods) {
            if ( method.getName().startsWith( "get" ) 
                    && ( method.getReturnType() == double.class ||
                         method.getReturnType() == int.class ||
                         method.getReturnType() == long.class
                        )
                    ) {
                count++;
            }
        }
        
        DefaultTableModel tableModel = new DefaultTableModel(new String[] {"Name", "Value"}, count);
        
        int rowInd = 0;
        for (Method method : methods) {
            if ( method.getName().startsWith( "get" ) 
                    && ( method.getReturnType() == double.class ||
                         method.getReturnType() == int.class ||
                         method.getReturnType() == long.class
                        )
                    ) {
                String rowName = method.getName().substring(3); // cut get
                Object rowValue = null;
                
                try {
                    rowValue = method.invoke(curAlgo, (Object[]) null);
                } catch (Exception exc) {
                    log.error("Can`t read value: (" + rowName + ") - " + exc.toString());
                }
                
                tableModel.setValueAt(rowName, rowInd, 0);
                tableModel.setValueAt(rowValue, rowInd, 1);
                
                rowInd++;
            }
        }
        
        this.getForm().algorithmValueTab.setModel(tableModel);
    }
    
    public void showCurrentHeight(Point point) {
        if ( model == null || model.getTerrainModel() == null) {
            this.getForm().curHeightLab.setText("[No model]");
            return;
        }
        
        if ( point == null) {
            this.getForm().curHeightLab.setText("[No point]");
            return;
        }

        Integer height = model.getTerrainModel().getTerrainHeight(point.x, point.y);
        
        if ( height == TerrainModel.RIVER_MARKER_HEIGHT ) {
            if ( model.getRiverModel() != null ) {
                height = model.getRiverModel().getHeight(point.x, point.y);
            }
        }
        
        this.getForm().curHeightLab.setText( String.format("(%d, %d) %d", 
                point.x, point.y, height) );
    }
    
    public void drawPoint(TerrainPoint point) {
        Graphics g = this.getForm().mapPanel.getGraphics();
        
        g.fillOval(point.getX(), point.getY(), 20, 20);
    }
    
    public void drawTownModel() {
        Graphics g = this.getForm().mapPanel.getGraphics();
        
        TownModel town = model.getTownModel();
        
        double boxSide = 2 * model.kmToPx( model.getDmax() );
        
        g.drawOval(
                town.getCenter().getX() - new Double(boxSide/2).intValue(), 
                town.getCenter().getY() - new Double(boxSide/2).intValue(), 
                new Double(boxSide).intValue(), 
                new Double(boxSide).intValue()
            );
    }
    
    public void startHppAlgorithm() {
        if ( model == null 
                || model.getTerrainModel() == null
                || model.getRiverModel() == null
                || model.getTownModel() == null) {
            JOptionPane.showMessageDialog(this.getForm(), "No models(hpp, terrain, river, town).", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        algorithm = new HppAlgo(model);
        
        this.refreshAlgoStatus();
        this.refreshAlgoValFromModel(null);
    }
    
    public void block1Algorithm() {
        if ( algorithm == null ) {
            JOptionPane.showMessageDialog(this.getForm(), "No algorithm.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            algorithm.block1();
        } catch (Exception exc) {
            log.warn(LogHelper.self().printException("Algorithm.block1.", exc));
            JOptionPane.showMessageDialog(this.getForm(), "Block1: " + exc.toString(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        this.refreshAlgoStatus();
        this.refreshAlgoValFromModel(null);
        
        this.drawTownModel();
    }
    
    public void refreshAlgoStatus() {
        if ( algorithm == null ) {
            return;
        }
        
        if ( algorithm.getStatus() != HppAlgo.Status.FAIL ) {
            this.getForm().algoStatusField.setBackground( Color.GREEN );
        } else {
            this.getForm().algoStatusField.setBackground( Color.RED );
        }
        
        this.getForm().algoStatusField.setText( algorithm.getStatus().toString() );
    }
    
    public void startup() {
        
    }
}
