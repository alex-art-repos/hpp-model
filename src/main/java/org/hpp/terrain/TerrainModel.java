/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.terrain;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import org.hpp.terrain.libnoise.util.NoiseMap;

/**
 *
 * @author Gautama
 */
@XmlRootElement
public class TerrainModel {
    public static final String DEF_FILE_NAME = "terrain-model.xml";
    public static final int RIVER_MARKER_HEIGHT = -50;
    
    private NoiseMap heightMap = null;
    
    private int pixelScale = 5; // m
    private int mapScale = 10000; // standard scale 1:10000
    private int monitorScale = 4; // px/mm

    private int heightScale = 100; // 
    
    public TerrainModel() {
        super();
    }

    public TerrainModel(NoiseMap theHeightMap) {
        super();
        heightMap = theHeightMap;
    }
    
    public TerrainModel(NoiseMap theHeightMap, int thePixelScale, int theHeightScale) {
        super();
        heightMap = theHeightMap;
        pixelScale = thePixelScale;
        heightScale = theHeightScale;
        
        this.checkState();
    }

    public NoiseMap getInternalMap() {
        return heightMap;
    }

    public void setInternalMap(NoiseMap heightMap) {
        this.heightMap = heightMap;
    }

    /**
     * Meters(scaled) in pixel - m/px. 
     * 
     * @return
     */
    public int getPixelScale() {
        return pixelScale;
    }

    public void setPixelScale(int pixelScale) {
        this.pixelScale = pixelScale;
    }

    public int getHeightScale() {
        return heightScale;
    }

    public void setHeightScale(int heightScale) {
        this.heightScale = heightScale;
    }

    public int getMapScale() {
        return mapScale;
    }

    public void setMapScale(int mapScale) {
        this.mapScale = mapScale;
    }

    public int getMonitorScale() {
        return monitorScale;
    }

    public void setMonitorScale(int monitorScale) {
        this.monitorScale = monitorScale;
    }
    
    public int getTerrainHeight(int x, int z) {
        this.checkState();
        
        double value = heightMap.getValue(x, z);
        
        value *= heightScale;
        
        return new Double(value).intValue();
    }
    
    public void setTerrainHeight(int x, int z, int y) {
        this.checkState();
        
        heightMap.setValue(x, z, y/(double)heightScale);
    }
    
    public int getTerrainHeight(TerrainPoint point) {
        this.checkState();
        
        double value = heightMap.getValue(point.getX(), point.getY());
        
        value *= heightScale;
        
        return new Double(value).intValue();
    }
    
    public void setTerrainHeight(TerrainPoint TerrainPoint, int y) {
        this.checkState();
        
        heightMap.setValue(TerrainPoint.getX(), TerrainPoint.getY(), y/(double)heightScale);
    }
    
    public int getMapWidth() {
        if ( heightMap == null ) {
            return -1;
        }
        
        return heightMap.getWidth();
    }
    
    public int getMapHeight() {
        if ( heightMap == null ) {
            return -1;
        }
        
        return heightMap.getHeight();
    }
    
    public int getMinHeight() {
        this.checkState();
        
        int minHeight = Integer.MAX_VALUE;
        for (int x = 0; x < heightMap.getWidth(); x++) {
            for (int y = 0; y < heightMap.getHeight(); y++) {
                int curHeight = this.getTerrainHeight(x, y);
                
                if ( minHeight > curHeight ) {
                    minHeight = curHeight;
                }
            }
        }
        
        return minHeight;
    }
    
    public int getMaxHeight() {
        this.checkState();
        
        int maxHeight = Integer.MIN_VALUE;
        for (int x = 0; x < heightMap.getWidth(); x++) {
            for (int y = 0; y < heightMap.getHeight(); y++) {
                int curHeight = this.getTerrainHeight(x, y);
                
                if ( maxHeight < curHeight ) {
                    maxHeight = curHeight;
                }
            }
        }
        
        return maxHeight;
    }
    
    public boolean contains(TerrainPoint point) {
        if ( ( point.getX() >= this.getMapWidth() || point.getX() < 0 ) 
            || ( point.getY() >= this.getMapHeight() || point.getY() < 0 ) ) {
            return false;
        }
        return true;
    }

    public boolean contains(int x, int z) {
        if ( ( x >= this.getMapWidth() || x < 0 ) 
            || ( z >= this.getMapHeight() || z < 0 ) ) {
            return false;
        }
        return true;
    }
    
    private void checkState() {
        if ( heightMap == null ) {
            throw new IllegalStateException("No internal map.");
        }
        
        if ( pixelScale <= 0 ) {
            throw new IllegalStateException("Bad pixel scale(" + pixelScale + ").");
        }
        
        if ( heightScale <= 0 ) {
            throw new IllegalStateException("Bad height scale(" + heightScale + ").");
        }
    }
    
    public void saveToFile(String fileName) throws Exception {
        JAXBContext ctx = JAXBContext.newInstance(TerrainModel.class, NoiseMap.class, 
                TerrainPoint.class);
        
        Marshaller marshaller = ctx.createMarshaller();
        
        marshaller.setProperty( Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true);
        
        marshaller.marshal(this, new File(fileName == null ? DEF_FILE_NAME : fileName));
    }
    
    public static TerrainModel loadFromFile(String fileName) throws Exception {
        JAXBContext ctx = JAXBContext.newInstance(TerrainModel.class, NoiseMap.class, 
                TerrainPoint.class);
        
        Unmarshaller unmarshaller = ctx.createUnmarshaller();
        
        TerrainModel model = (TerrainModel)unmarshaller.unmarshal(new File(fileName == null ? DEF_FILE_NAME : fileName));
        
        return model;
    }
}
