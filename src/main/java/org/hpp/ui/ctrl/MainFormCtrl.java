/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.ui.ctrl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.hpp.domain.MapGeneratorConfig;
import org.hpp.model.HppAlgo;
import org.hpp.model.HppModel;
import org.hpp.terrain.TerrainModel;
import org.hpp.terrain.TerrainPoint;
import org.hpp.terrain.TerrainRenderer;
import org.hpp.terrain.TerrainTransformer;
import org.hpp.terrain.perlin.PerlinTerrainGenerator;
import org.hpp.terrain.river.DamModel;
import org.hpp.terrain.river.RiverFormer;
import org.hpp.terrain.river.RiverGen;
import org.hpp.terrain.river.RiverModel;
import org.hpp.terrain.town.TownGen;
import org.hpp.terrain.town.TownModel;
import org.hpp.ui.MainForm;
import org.hpp.ui.component.DamDrawObj;
import org.hpp.ui.component.ImageDrawObj;
import org.hpp.ui.component.MapPanel;
import org.hpp.ui.component.TownDrawObj;
import org.hpp.utils.LogHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gautama
 */
public class MainFormCtrl extends BaseController<MainForm> {
    public static final Logger log = LoggerFactory.getLogger(MainFormCtrl.class);
    
    protected BufferedImage mapImage = null;
    protected TownDrawObj townDrawObj = null;
    
    protected HppModel model = null;
    protected HppAlgo algorithm = null;

    protected DamDrawObj damDrawObj = null;
    
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
        
        try {
            model.setRate_midleday( HppModel.loadRateMiddleDayFromFile(null) );
        } catch (Exception exc) {
            log.error("Can`t load middle day rate: " + exc.toString());
            JOptionPane.showMessageDialog(this.getForm(), "Can`t load middle day rate.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
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
        if ( model == null ) {
            JOptionPane.showMessageDialog(this.getForm(), "No model found.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        log.debug("Generating map for " + mapGenCfg);
        
        PerlinTerrainGenerator genMap = new PerlinTerrainGenerator();
        
        genMap.setSeed(null);
        
        TerrainModel terrain = genMap.getHeightMap(
                mapGenCfg.getMapWidth(), 
                mapGenCfg.getMapHeight(), 
                mapGenCfg.getMinGenX(), 
                mapGenCfg.getMaxGenX(), 
                mapGenCfg.getMinGenZ(), 
                mapGenCfg.getMaxGenZ()
            );
        
        int pixelScale = mapGenCfg.getPixelScale();
        
        terrain.setPixelScale( pixelScale );
        terrain.setHeightScale( mapGenCfg.getHeightScale() );

        terrain.setMapScale( mapGenCfg.getMapScale() );
        terrain.setMonitorScale( mapGenCfg.getMonitorScale() );

        TerrainTransformer terrainTransformer = new TerrainTransformer();

        terrainTransformer.invertDeeps( terrain );
        terrainTransformer.rotateTerrain( terrain, 10, 500 );
        terrainTransformer.normolizeHeight(terrain);
        
        model.setTerrainModel( terrain );

        log.debug(String.format("Terrain map was generated[%d, %d]", terrain.getMinHeight(), terrain.getMaxHeight()));
        
        RiverGen riverGen = new RiverGen();
        
        riverGen.setMinAngle( mapGenCfg.getMinRiverAngle() );
        riverGen.setMaxAngle( mapGenCfg.getMaxRiverAngle() );
        
        riverGen.setMinEdgeLen( mapGenCfg.getMinRiverEdge() );
        riverGen.setMaxEdgeLen( mapGenCfg.getMaxRiverEdge() );
        
        riverGen.setMinWidth( mapGenCfg.getMinRiverWidth() );
        riverGen.setMaxWidth( mapGenCfg.getMaxRiverWidth() );
        
        TerrainPoint startPos = new TerrainPoint(terrain.getMapWidth()/2, 0);
        
        RiverModel river = null;

        if ( mapGenCfg.isIsNaturalGen() ) {
            river = riverGen.genNaturalRiver(startPos, terrain);
        } else {
            river = riverGen.genRiver(startPos, terrain);
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
        
        riverFormer.setIsBuildBanks( mapGenCfg.isIsBuildBanks() );
        riverFormer.setIsForcedBanks( mapGenCfg.isIsForcedBanks() );
        riverFormer.setRiverDepthMode( mapGenCfg.getRiverDepthMode() );
        
        riverFormer.buildRiver(river, terrain);
        // riverFormer.traceRiver(river, terrainMap);
        
        river.normolizeHeights();
        
        log.debug(String.format("River was formed. Naturality = %.2f %%", river.naturalIndex() * 100));
        
        TownGen townGen = new TownGen();
        
        TownModel townModel = townGen.genTown(terrain, river);
        
        log.debug("Town was generated.");
        
        if ( model != null ) {
            model.setTownModel(townModel);
        }
        
        mapImage = new TerrainRenderer().getTerrainImage( terrain );
        
        if ( townDrawObj == null ) {
            townDrawObj = new TownDrawObj();
            townDrawObj.setImage( this.loadTownImage() );
            townDrawObj.setIsShow(false);
            
            this.getForm().mapPanel.addDrawObj(townDrawObj);
        }
        
        this.refreshScaleInfo();
        
        return true;
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
            
        }
        
        form.mapPanel.setBackgroundImage( this.getMapImage() );
        
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
            mapImage = new TerrainRenderer().getTerrainImage( model.getTerrainModel() );
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
                         method.getReturnType() == long.class ||
                         method.getReturnType() == TerrainPoint.class
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
                         method.getReturnType() == long.class ||
                         method.getReturnType() == TerrainPoint.class
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
    
    public void refreshScaleInfo() {
        if ( model == null || model.getTerrainModel() == null) {
            return ;
        }
        
        TerrainModel terrain = model.getTerrainModel();
        
        this.getForm().scaleLab.setText(
                String.format("1:%d (%d px/mm) %d m/px", 
                    terrain.getMapScale(), 
                    terrain.getMonitorScale(),
                    terrain.getPixelScale()
                    )
            );
    }
    
    public void drawPoint(TerrainPoint point) {
        Graphics g = this.getForm().mapPanel.getGraphics();
        
        g.fillOval(point.getX(), point.getY(), 20, 20);
    }
    
    public void drawTownModel() {
        if ( model == null ) {
            return;
        }
        
        double radius = model.kmToPx( model.getDmax() );
        
        if ( townDrawObj != null ) {
            townDrawObj.setPos( model.getTownModel().getCenter() );
            townDrawObj.setRadius( new Double(radius).intValue() );
        }
        
        townDrawObj.setIsShow( !townDrawObj.isShow() );
        
        this.getForm().mapPanel.repaint();
    }
    
    public void drawDamModel() {
        if ( algorithm == null ) {
            return;
        }
        
        if ( damDrawObj == null ) {
            damDrawObj = new DamDrawObj(algorithm);
            damDrawObj.setIsShow(false);
            this.getForm().mapPanel.addDrawObj(damDrawObj);
        } else {
            damDrawObj.setAlgorithm(algorithm);
        }
        
        damDrawObj.setIsShow( !damDrawObj.isShow() );
        
        this.getForm().mapPanel.repaint();
    }
    
    public void drawFloodModel() {
        Graphics g = this.getForm().mapPanel.getGraphics();

        List<TerrainPoint> points = algorithm.getFloodArea();
        
        if ( points == null || points.isEmpty() ) {
            return;
        }
        
        Polygon polygon = new Polygon();
        
        for (TerrainPoint point : points) {
            polygon.addPoint(point.getX(), point.getY());
        }
                
        g.fillPolygon( polygon );
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
        } finally {
            this.refreshAlgoStatus();
        }
        
        this.refreshAlgoValFromModel(null);
    }
    
    public void block2Algorithm() {
        if ( algorithm == null ) {
            JOptionPane.showMessageDialog(this.getForm(), "No algorithm.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            algorithm.block2();
        } catch (Exception exc) {
            log.warn(LogHelper.self().printException("Algorithm.block2.", exc));
            JOptionPane.showMessageDialog(this.getForm(), "Block2: " + exc.toString(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        } finally {
            this.refreshAlgoStatus();
        }
        
        this.refreshAlgoValFromModel(null);
    }
    
    public void block3Algorithm() {
        if ( algorithm == null ) {
            JOptionPane.showMessageDialog(this.getForm(), "No algorithm.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            algorithm.block3();
        } catch (Exception exc) {
            log.warn(LogHelper.self().printException("Algorithm.block3.", exc));
            JOptionPane.showMessageDialog(this.getForm(), "Block3: " + exc.toString(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        } finally {
            this.refreshAlgoStatus();
        }
        
        this.refreshAlgoValFromModel(null);
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
