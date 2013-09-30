/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.terrain.river;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.hpp.model.HppAlgo;
import org.hpp.terrain.TerrainPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gautama
 */
@XmlRootElement
public class RiverModel {
    public static final Logger log = LoggerFactory.getLogger( RiverModel.class );
    
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
            RiverEdge prev = channelEdges.get( channelEdges.size() - 1 );
            
            prev.setStop(edge.getStart());
            prev.setNextEdge(edge);
            edge.setPrevEdge(prev);
            
            channelEdges.add( edge );
        }
    }
    
    public RiverEdge addEdge(TerrainPoint point, int width) {
        RiverEdge edge = new RiverEdge(point, null, width);
        
        if ( channelEdges.isEmpty() ) {
            channelEdges.add( edge );
        } else {
            RiverEdge prev = channelEdges.get( channelEdges.size() - 1 );
            
            prev.setStop(edge.getStart());
            prev.setNextEdge(edge);
            edge.setPrevEdge(prev);
            
            channelEdges.add( edge );
        }
        
        return edge;
    }
    
    public boolean setLastPoint(TerrainPoint point) {
        if ( channelEdges.isEmpty() ) {
            return false;
        } else {
            channelEdges.get( channelEdges.size() - 1 ).setStop(point);
        }
        
        return true;
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
    
    public List<TerrainPoint> getAllHeightPoints() {
        return new ArrayList<>(terrainPointHeights.keySet());
    }
    
    public boolean contains(TerrainPoint point) {
        return terrainPointHeights.get(point) != null;
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
    
    /**
     * Returns point on river backborn by distance along the river channel.
     * 
     * @param startPoint 
     * @param distance in pixels
     * @param delta delta > |expected - real|
     * @return
     */
    public RiverRange.Pair findPointByDistance(RiverEdge edge, TerrainPoint startPoint, double distance, double delta) {
        double curDist = TerrainPoint.distance(startPoint, edge.getStop());
        
        if ( curDist > distance ) {
            if ( Math.abs( curDist - distance ) < delta  ) {
                return new RiverRange.Pair(edge, edge.getStop());
            } else {
                return new RiverRange.Pair(edge, this.findPointOnEdge(edge, distance));
            }
        } else {
            distance -= curDist; // shift in first gap
            
            boolean searchStarted = false;
            for (RiverEdge curEdge : channelEdges) {
                if ( !searchStarted ) {
                    if ( curEdge.equals( edge ) ) {
                        searchStarted = true;
                        // pass through current edge
                    }
                    continue;
                }
                
                curDist = TerrainPoint.distance(curEdge.getStart(), curEdge.getStop());
                
                if ( curDist > distance ) {
                    if ( Math.abs( curDist - distance ) < delta  ) {
                        return new RiverRange.Pair(edge, edge.getStop());
                    } else {
                        return new RiverRange.Pair(edge, this.findPointOnEdge(edge, distance));
                    }
                }
                
                distance -= curDist; // shift in first gap
            }
            
            if ( !searchStarted ) {
                return null;
            }
            
            // search is reached the river end but distance is too big
        }
            
        return null;
    }
    
    protected TerrainPoint findPointOnEdge(RiverEdge edge, double distance) {
        List<TerrainPoint> points = edge.circleIntersection(edge.getStart(), (int)Math.floor(distance) );
        
        if ( points == null || points.size() != 1 ) {
            throw new AssertionError("Algorithm error: no point on edge found.");
        }
        
        return points.get(0);
    }
    
    public int getMinHeight() {
        List<TerrainPoint> points = this.getAllHeightPoints();
        
        if ( points.isEmpty() ) {
            return Integer.MIN_VALUE;
        }
        
        int minHeight = this.getHeight( points.get(0) ), 
            curHeight;
        
        for (TerrainPoint point : points) {
            curHeight = this.getHeight( point );
            if ( curHeight < minHeight ) {
                minHeight = curHeight;
            }
        }
        
        return minHeight;
    }
    
    public int getMaxHeight() {
        List<TerrainPoint> points = this.getAllHeightPoints();
        
        if ( points.isEmpty() ) {
            return Integer.MAX_VALUE;
        }
        
        int maxHeight = this.getHeight( points.get(0) ), 
            curHeight;
        
        for (TerrainPoint point : points) {
            curHeight = this.getHeight( point );
            if ( curHeight > maxHeight ) {
                maxHeight = curHeight;
            }
        }
        
        return maxHeight;
    }
    
    public void normolizeHeights() {
        List<TerrainPoint> points = this.getAllHeightPoints();
        
        Map<Integer, Integer> heights = new HashMap<>();
        
        for (TerrainPoint point : points) {
            if ( !heights.containsKey(point.getY()) ) {
                heights.put(point.getY(), this.getHeight(point));
            }
        }
        
        for (TerrainPoint point : points) {
            this.setHeight(point, heights.get(point.getY()));
        }
    }
    
    public double averageWidth() {
        long width = 0;
        
        for (RiverEdge edge : channelEdges) {
            width += edge.getWidth();
        }
        
        return width / (double)channelEdges.size();
    }
    
    public double naturalIndex () {
        List<TerrainPoint> points = this.getAllHeightPoints();
        
        int errors = 0;
        
        TerrainPoint sortedPoints[] = points.toArray( new TerrainPoint[points.size()] );
        
        Arrays.sort(sortedPoints, new Comparator<TerrainPoint>() {

            @Override
            public int compare(TerrainPoint o1, TerrainPoint o2) {
                return o1.getY() - o2.getY();
            }
        });
        
        Set<Integer> rows = new HashSet<>();
        
        for (int i = 0; i < sortedPoints.length-1 ; i++ ) {
            rows.add(sortedPoints[i].getY());
            if ( this.getHeight(sortedPoints[i]) < this.getHeight(sortedPoints[i+1]) ) {
                log.debug(String.format("Naturality error : %s - %d, %s - %d", 
                        sortedPoints[i], 
                        this.getHeight(sortedPoints[i]),
                        sortedPoints[i+1],
                        this.getHeight(sortedPoints[i+1])
                        )
                    );
                errors++;
            }
        }
        
        log.debug(String.format("Scan for naturality: rows %d, errors %d", rows.size(), errors));
        
        return 1 - (errors)/(double)(rows.size() - 1);
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
