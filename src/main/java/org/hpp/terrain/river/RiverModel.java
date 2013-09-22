/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.terrain.river;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.hpp.terrain.TerrainPoint;

/**
 *
 * @author Gautama
 */
@XmlRootElement
public class RiverModel {
    public static final String DEF_FILE_NAME = "river-model.xml";
    
    private List<RiverEdge> channelEdges = new ArrayList<>();

    private Map<TerrainPoint, Integer> terrainPointHeights = new HashMap<>();
    
    public RiverModel() {
        super();
    }

    public void clearEdges() {
        channelEdges.clear();
    }
    
    public void addEdge(RiverEdge edge) {
        if ( channelEdges.isEmpty() ) {
            channelEdges.add( edge );
        } else {
            channelEdges.get( channelEdges.size() - 1 ).setStop(edge.getStart());
            channelEdges.add( edge );
        }
    }
    
    public RiverEdge addEdge(TerrainPoint point, int width) {
        RiverEdge edge = new RiverEdge(point, null, width);
        
        if ( channelEdges.isEmpty() ) {
            channelEdges.add( edge );
        } else {
            channelEdges.get( channelEdges.size() - 1 ).setStop(point);
            channelEdges.add( edge );
        }
        
        return edge;
    }
    
    public void removeEdge(RiverEdge edge) {
        channelEdges.remove(edge);
    }
    
    public List<RiverEdge> edges() {
        return channelEdges;
    }
    
    public Integer getHeight(TerrainPoint point) {
        return terrainPointHeights.get(point);
    }
    
    public Integer getHeight(int x, int z) {
        return terrainPointHeights.get(new TerrainPoint(x, z));
    }
    
    public void setHeight(int x, int z, int height) {
        terrainPointHeights.put(new TerrainPoint(x, z), height);
    }
    
    public void setHeight(TerrainPoint thePoint, int height) {
        terrainPointHeights.put(thePoint, height);
    }
    
    public List<TerrainPoint> getAllPoints() {
        return new ArrayList<>(terrainPointHeights.keySet());
    }

    public List<RiverEdge> getChannelEdges() {
        return channelEdges;
    }

    public void setChannelEdges(List<RiverEdge> channelEdges) {
        this.channelEdges = channelEdges;
    }

    public Map<TerrainPoint, Integer> getTerrainPointHeights() {
        return terrainPointHeights;
    }

    public void setTerrainPointHeights(Map<TerrainPoint, Integer> terrainPointHeights) {
        this.terrainPointHeights = terrainPointHeights;
    }
    
    @XmlTransient
    public double getRiverLength() {
        double length = 0;
        
        for (RiverEdge edge : channelEdges) {
            if ( edge.getStart() != null && edge.getStop() != null ) {
                length += TerrainPoint.distance(edge.getStart(), edge.getStop());
            }
        }
        
        return length;
    }
    
    public double getRiverRangeLength(RiverRange range) {
        int length = 0;
        boolean startCalc = false;
        
        RiverRange.Pair firstPair = range.firstPair() ,
                        lastPair = range.lastPair();
        
        TerrainPoint point1 = firstPair.getEdge().getStop(), 
                     point2 = lastPair.getEdge().getStart();
        
        length += TerrainPoint.distance(range.firstPoint(), point1);
        length += TerrainPoint.distance(range.lastPoint(), point2);
        
        for (RiverEdge edge : channelEdges) {
            if ( point1.equals( edge.getStart() ) ) {
                startCalc = true;
            }
            
            if ( point2.equals( edge.getStop() ) ) {
                startCalc = false;
                break;
            }
            
            if ( startCalc && edge.getStart() != null && edge.getStop() != null ) {
                length += TerrainPoint.distance(edge.getStart(), edge.getStop());
            }
        }
        
        return length;
    }
    
    public List<RiverEdge> intersectedEdges(TerrainPoint center, int radius) {
        List<RiverEdge> edges = new ArrayList<>();
        
        for (RiverEdge edge : channelEdges) {
            if ( edge.circleIntersection(center, radius) != null ) {
                edges.add(edge);
            }
        }
        
        return edges;
    }
    
    public List<TerrainPoint> circleIntersection(TerrainPoint center, int radius) {
        List<TerrainPoint> points = new ArrayList<>();
        
        for (RiverEdge edge : channelEdges) {
            if ( edge.getStop() == null ) {
                continue;
            }
            
            List<TerrainPoint> curPoints = edge.circleIntersection(center, radius);
            if ( !curPoints.isEmpty() ) {
                points.addAll( curPoints );
            }
        }
        
        return points;
    }
    
    public RiverRange circleIntersectionRange(TerrainPoint center, int radius) {
        RiverRange range = new RiverRange();
        
        for (RiverEdge edge : channelEdges) {
            if ( edge.getStop() == null ) {
                continue;
            }
            
            List<TerrainPoint> curPoints = edge.circleIntersection(center, radius);
            
            if ( !curPoints.isEmpty() ) {
                range.addPair( new RiverRange.Pair(edge, curPoints) );
            }
        }
        
        return range;
    }
    
    public void saveToFile(String fileName) throws Exception {
        JAXBContext ctx = JAXBContext.newInstance(RiverModel.class, 
                TerrainPoint.class, RiverEdge.class);
        
        Marshaller marshaller = ctx.createMarshaller();
        
        marshaller.setProperty( Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true);
        
        marshaller.marshal(this, new File(fileName == null ? DEF_FILE_NAME : fileName));
    }
    
    public static RiverModel loadFromFile(String fileName) throws Exception {
        JAXBContext ctx = JAXBContext.newInstance(RiverModel.class, 
                TerrainPoint.class, RiverEdge.class);
        
        Unmarshaller unmarshaller = ctx.createUnmarshaller();
        
        RiverModel model = (RiverModel)unmarshaller.unmarshal(new File(fileName == null ? DEF_FILE_NAME : fileName));
        
        return model;
    }
}
