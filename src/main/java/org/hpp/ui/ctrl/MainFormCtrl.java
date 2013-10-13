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
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.hpp.domain.MapGeneratorConfig;
import org.hpp.model.HppAlgo;
import org.hpp.model.HppModel;
import org.hpp.model.HppProject;
import org.hpp.terrain.TerrainModel;
import org.hpp.terrain.TerrainPoint;
import org.hpp.terrain.TerrainRenderer;
import org.hpp.terrain.TerrainTransformer;
import org.hpp.terrain.perlin.PerlinTerrainGenerator;
import org.hpp.terrain.river.DamModel;
import org.hpp.terrain.river.RiverFormer;
import org.hpp.terrain.river.RiverGen;
import org.hpp.terrain.river.RiverModel;
import org.hpp.terrain.river.TubeInfo;
import org.hpp.terrain.town.TownGen;
import org.hpp.terrain.town.TownModel;
import org.hpp.ui.MainForm;
import org.hpp.ui.component.DamDrawObj;
import org.hpp.ui.component.HppProjectDrawObj;
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
    private List<HppProjectDrawObj> projectDraw = new ArrayList<>();
    
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
    
    public void saveMapToFile() {
        String fileName = null;
        
        JFileChooser chooser = new JFileChooser( new File(System.getProperty("user.dir")) );
        
        chooser.setDialogTitle("Choose image file ...");
        
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Images (*.png)", "png");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showSaveDialog(this.getForm());
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            fileName = chooser.getSelectedFile().getAbsolutePath();
            if ( !fileName.endsWith(".png") ) {
                fileName += ".png";
            }
        }

        log.debug("Save map to " + fileName);
        
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
    
    public void saveMapScreenshotToFile() {
        String fileName = null;
        
        JFileChooser chooser = new JFileChooser( new File(System.getProperty("user.dir")) );
        
        chooser.setDialogTitle("Choose image file ...");
        
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Images (*.png)", "png");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showSaveDialog(this.getForm());
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            fileName = chooser.getSelectedFile().getAbsolutePath();
            if ( !fileName.endsWith(".png") ) {
                fileName += ".png";
            }
        }

        log.debug("Save map screenshot to " + fileName);
        
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
        
        double riverNaturalIndex = river.naturalIndex() * 100;
        log.debug(String.format("River was formed. Naturality = %.2f %%", riverNaturalIndex));
        
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
        } else {
            if ( model.getTownModel() != null ) {
                townDrawObj.setPos( model.getTownModel().getCenter() );
            }
        }
        
        this.refreshScaleInfo();
        
        JOptionPane.showMessageDialog(this.getForm(), 
                String.format("River naturality = %.2f %%", riverNaturalIndex),
                "Info", 
                JOptionPane.INFORMATION_MESSAGE);
        
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

    public BufferedImage loadDamImage() {
        try {
            return ImageIO.read( this.getClass().getResourceAsStream("dam-icon.png") );
        } catch (Exception exc) {
            log.error("Can`t load dam image: " + exc.toString());
            return null;
        }
    }
    
    public BufferedImage loadTubeImage() {
        try {
            return ImageIO.read( this.getClass().getResourceAsStream("tube.png") );
        } catch (Exception exc) {
            log.error("Can`t load tube image: " + exc.toString());
            return null;
        }
    }
    
    public BufferedImage getMapImage() {
        return mapImage;
    }

    public void exitApp() {
        System.exit(0);
    }
    
    public void loadInputValues(boolean isFull) {
        String hppModelPath = null, 
               terrainModelPath = null,
               riverModelPath = null,
               rateModelPath = null;
        
        JFileChooser chooser = new JFileChooser( new File(System.getProperty("user.dir")) );
        
        chooser.setDialogTitle("Choose hpp model file ...");
        
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "HPP model (*.hpp)", "hpp");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(this.getForm());
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            hppModelPath = chooser.getSelectedFile().getAbsolutePath();
        }

        log.debug("Load hpp model from " + hppModelPath);
        
        if ( isFull ) {
            filter = new FileNameExtensionFilter("Terrain model (*.ter)", "ter");
            chooser.setFileFilter(filter);
            chooser.setDialogTitle("Choose terrain model file ...");
            returnVal = chooser.showOpenDialog(this.getForm());
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                terrainModelPath = chooser.getSelectedFile().getAbsolutePath();
            }

            log.debug("Load terrain model from " + terrainModelPath);

            filter = new FileNameExtensionFilter("River model (*.riv)", "riv");
            chooser.setFileFilter(filter);
            chooser.setDialogTitle("Choose river model file ...");
            returnVal = chooser.showOpenDialog(this.getForm());
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                riverModelPath = chooser.getSelectedFile().getAbsolutePath();
            }

            log.debug("Load river model from " + riverModelPath);

            filter = new FileNameExtensionFilter("Property file (*.props)", "props");
            chooser.setFileFilter(filter);
            chooser.setDialogTitle("Choose rate model file ...");
            returnVal = chooser.showOpenDialog(this.getForm());
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                rateModelPath = chooser.getSelectedFile().getAbsolutePath();
            }

            log.debug("Load rate model from " + rateModelPath);
        }
        try {
            model = HppModel.loadFromFile(
                    new String[] { hppModelPath, terrainModelPath, riverModelPath, rateModelPath  }, 
                    isFull);
        } catch (Exception exc) {
            log.error("Can`t load HPP model: " + exc.toString());
            JOptionPane.showMessageDialog(this.getForm(), "Can`t load model.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if ( model.getTerrainModel() != null ) {
            mapImage = new TerrainRenderer().getTerrainImage( model.getTerrainModel() );
            if ( mapImage != null ) {
                this.paintMap();
            }
        }
        
        this.refreshScaleInfo();
        
        JOptionPane.showMessageDialog(this.getForm(), "Model successfully loaded.", 
                "Info", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void saveInputValues(boolean isFull) {
        if ( model == null ) {
            JOptionPane.showMessageDialog(this.getForm(), "No model.", "Warn", 
                    JOptionPane.WARNING_MESSAGE);
            return ;
        }
        
        String hppModelPath = null, 
               terrainModelPath = null,
               riverModelPath = null;
        
        JFileChooser chooser = new JFileChooser( new File(System.getProperty("user.dir")) );
        
        chooser.setDialogTitle("Choose hpp model file ...");
        
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "HPP model (*.hpp)", "hpp");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showSaveDialog(this.getForm());
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            hppModelPath = chooser.getSelectedFile().getAbsolutePath();
            if ( !hppModelPath.endsWith(".hpp") ) {
                hppModelPath += ".hpp";
            }
        }

        log.debug("Save hpp model from " + hppModelPath);
        
        if ( isFull ) {
            filter = new FileNameExtensionFilter("Terrain model (*.ter)", "ter");
            chooser.setFileFilter(filter);
            chooser.setDialogTitle("Choose terrain model file ...");
            returnVal = chooser.showSaveDialog(this.getForm());
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                terrainModelPath = chooser.getSelectedFile().getAbsolutePath();
                if ( !terrainModelPath.endsWith(".ter") ) {
                    terrainModelPath += ".ter";
                }
            }

            log.debug("Save terrain model from " + terrainModelPath);

            filter = new FileNameExtensionFilter("River model (*.riv)", "riv");
            chooser.setFileFilter(filter);
            chooser.setDialogTitle("Choose river model file ...");
            returnVal = chooser.showSaveDialog(this.getForm());
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                riverModelPath = chooser.getSelectedFile().getAbsolutePath();
                if ( !riverModelPath.endsWith(".riv") ) {
                    riverModelPath += ".riv";
                }
            }

            log.debug("Save river model from " + riverModelPath);
        }
        
        try {
            model.saveToFile(new String[]{ hppModelPath, terrainModelPath, riverModelPath }, isFull);
        } catch (Exception exc) {
            log.error("Can`t save HPP model: " + exc.toString());
            JOptionPane.showMessageDialog(this.getForm(), "Can`t save model to " + 
                    HppModel.DEF_FILE_NAME, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JOptionPane.showMessageDialog(this.getForm(), "Model successfully saved.", 
                "Info", JOptionPane.INFORMATION_MESSAGE);
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
                
                if ( HppModel.UNITS.get(rowName) != null ) {
                    rowName += ", " + HppModel.UNITS.get(rowName);
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
                
                if ( HppAlgo.UNITS.get(rowName) != null ) {
                    rowName += ", " + HppAlgo.UNITS.get(rowName);
                }
                
                tableModel.setValueAt(rowName, rowInd, 0);
                tableModel.setValueAt(rowValue, rowInd, 1);
                
                rowInd++;
            }
        }
        
        this.getForm().algorithmValueTab.setModel(tableModel);
    }
    
    public void refreshProjectInfo(HppProject project, JTable table) {
        if ( project == null ) {
            return;
        }
        
        Method methods[] = HppProject.class.getDeclaredMethods();
        
        int count = 1; // one for type
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
                    rowValue = method.invoke(project, (Object[]) null);
                } catch (Exception exc) {
                    log.error("Can`t read value: (" + rowName + ") - " + exc.toString());
                }
                
                if ( HppProject.UNITS.get(rowName) != null ) {
                    rowName += ", " + HppProject.UNITS.get(rowName);
                }
                
                tableModel.setValueAt(rowName, rowInd, 0);
                tableModel.setValueAt(rowValue, rowInd, 1);
                
                rowInd++;
            }
        }
        
        tableModel.setValueAt("Type", rowInd, 0);
        tableModel.setValueAt( project.getPlantType(), rowInd, 1);
        
        table.setModel(tableModel);
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
    
    public void showCurrentProject(Point point) {
        if ( projectDraw.isEmpty() ) {
            return;
        }

        for (HppProjectDrawObj draw : projectDraw) {
            if ( draw.getProject().getPlantType() == HppAlgo.HydroPlant.DAM ) {
                DamModel dam = draw.getProject().getDam();
                if ( dam == null ) {
                    continue;
                }

                double dist = TerrainPoint.distance( dam.getRiverIntersectPoint(), TerrainPoint.fromPoint(point));

                if ( dist <= draw.getRadius() ) {
                    this.refreshProjectInfo(draw.getProject(), this.getForm().curProjectTab);
                }
            } else {
                TubeInfo tube = draw.getProject().getTubeInfo();
                
                if ( tube == null ) {
                    continue;
                }
                
                double dist = TerrainPoint.distance( tube.getStart(), TerrainPoint.fromPoint(point));

                if ( dist <= draw.getRadius() ) {
                    this.refreshProjectInfo(draw.getProject(), this.getForm().curProjectTab);
                }
            }
        }
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
        
        if ( townDrawObj == null ) {
            townDrawObj = new TownDrawObj();
            townDrawObj.setImage( this.loadTownImage() );
            townDrawObj.setIsShow(false);
            
            this.getForm().mapPanel.addDrawObj(townDrawObj);
        }
        
        if ( townDrawObj != null ) {
            townDrawObj.setPos( model.getTownModel().getCenter() );
            townDrawObj.setRadius( new Double(radius).intValue() );
        }
        
        townDrawObj.setIsShow( this.getForm().showTownBtn.isSelected() );
        
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
        
        damDrawObj.setIsShow( this.getForm().showDamBtn.isSelected() );
        
        this.getForm().mapPanel.repaint();
    }
    
    public void drawFloodModel() {
        if ( damDrawObj == null ) {
            return;
        }
        
        damDrawObj.setIsShowFlood( this.getForm().showFloodBtn.isSelected() );
        
        this.getForm().mapPanel.repaint();
    }
    
    public void drawProjects() {
        if ( algorithm == null 
                || algorithm.getProjects() == null
                || algorithm.getProjects().isEmpty() ) {
            return ;
        }

        List<HppProject> projects = algorithm.getProjects();

        if ( !projectDraw.isEmpty() ) {
            for (HppProjectDrawObj draw : projectDraw) {
                this.getForm().mapPanel.removeDrawObj( draw );
            }
        }
        
        projectDraw.clear();
        
        HppProjectDrawObj newDraw;
        for (HppProject project : projects) {
            if ( project.equals( algorithm.getBestProject() ) ) {
                newDraw = new HppProjectDrawObj(project, false);
                if ( project.getPlantType() == HppAlgo.HydroPlant.DAM ) {
                    newDraw.setImage( this.loadDamImage() );
                } else {
                    newDraw.setImage( this.loadTubeImage() );
                }
                newDraw.setIsSpecial(true);
            } else {
                newDraw = new HppProjectDrawObj(project, false);
            }
            
            projectDraw.add(newDraw);
            this.getForm().mapPanel.addDrawObj(newDraw);
        }
    }
    
    public void showProjects() {
        if ( projectDraw.isEmpty() ) {
            return;
        }
        
        for (HppProjectDrawObj draw : projectDraw) {
            draw.setIsShow( this.getForm().showProjectsBtn.isSelected() );
        }
        
        if ( algorithm != null && algorithm.getBestProject() != null ) {
            this.refreshProjectInfo(algorithm.getBestProject(), this.getForm().bestProjectTab);
        }
        
        this.getForm().mapPanel.repaint();
    }
    
    public void showProjectInfo() {
        if ( algorithm == null ) {
            return;
        }
        
        List<HppProject> projects = new ArrayList<>();
        HppProject proj;
        DamModel dam;
        for (int i = 0; i < 10; i++) {
            proj = new HppProject();
            proj.setCc( i );
            
            if ( i %2 == 0 ) {
                proj.setPlantType( HppAlgo.HydroPlant.DERIVATE );
            } else {
                proj.setPlantType( HppAlgo.HydroPlant.DAM );
            }
            
            dam = new DamModel();
            dam.setRiverIntersectPoint( new TerrainPoint(100, 100 + i*20) );
            proj.setDam( dam );
            projects.add(proj);
        }
        algorithm.setProjects(projects);
        
        proj = new HppProject();
        proj.setCc( 11 );
        dam = new DamModel();
        dam.setRiverIntersectPoint( new TerrainPoint(100, 100 + 11*20) );
        proj.setPlantType(HppAlgo.HydroPlant.DERIVATE);
        proj.setDam( dam );
        algorithm.setBestProject( proj );
        
        this.refreshProjectInfo(new HppProject(), this.getForm().curProjectTab);
        this.refreshProjectInfo(new HppProject(), this.getForm().bestProjectTab);
        
        this.drawProjects();
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
            this.refreshAlgoValFromModel(null);
        }
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
            this.refreshAlgoValFromModel(null);
        }
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
            this.refreshAlgoValFromModel(null);
        }
        
        JOptionPane.showMessageDialog(this.getForm(), 
                String.format("Calculated %d projects.", algorithm.getProjects().size()), 
                "Info", JOptionPane.INFORMATION_MESSAGE);
        
        this.drawProjects();
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
