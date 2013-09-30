/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.terrain.river;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.hpp.terrain.TerrainLine;
import org.hpp.terrain.TerrainPoint;

/**
 *
 * @author Gautama
 */
public class RiverEdge {
    private TerrainPoint start = null;
    private TerrainPoint stop = null;
    
    private int width = 0;

    private TerrainLine line = null;

    private RiverEdge prevEdge = null;
    private RiverEdge nextEdge = null;
    
    public RiverEdge() {
        super();
    }

    public RiverEdge(TerrainPoint theStart, TerrainPoint theStop, int theWidth) {
        super();
        start = theStart;
        stop = theStop;
        width = theWidth;
        
        this.calcEquationOfLine();
    }
    
    public TerrainPoint getStart() {
        return start;
    }

    public void setStart(TerrainPoint start) {
        this.start = start;
        this.calcEquationOfLine();
    }

    public TerrainPoint getStop() {
        return stop;
    }

    public void setStop(TerrainPoint stop) {
        this.stop = stop;
        this.calcEquationOfLine();
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public RiverEdge getPrevEdge() {
        return prevEdge;
    }

    public void setPrevEdge(RiverEdge prevEdge) {
        this.prevEdge = prevEdge;
    }

    public RiverEdge getNextEdge() {
        return nextEdge;
    }

    public void setNextEdge(RiverEdge nextEdge) {
        this.nextEdge = nextEdge;
    }
    
    public double length() {
        if ( start == null || stop == null ) {
            return 0;
        }
        
        return TerrainPoint.distance( start, stop);
    }

    /**
     * Finds points of intersection with circle.
     * 
     * @param center
     * @param radius
     * @return points sorted by Y ascendance
     */
    public List<TerrainPoint> circleIntersection(TerrainPoint center, int radius) {
        List<TerrainPoint> points = null;
        
        if ( line.isIsSpecial() ) {
            points = this.specialCase(center, radius);
        } else {
            points = this.commonCase(center, radius);
        }
        
        if ( points != null && !points.isEmpty() ) {
            TerrainPoint[] forSort = points.toArray(new TerrainPoint[points.size()]);
            
            Arrays.sort(forSort, new Comparator<TerrainPoint>() {
                @Override
                public int compare(TerrainPoint o1, TerrainPoint o2) {
                    return o1.getY() - o2.getY();
                }
            });
            
            points = Arrays.asList(forSort);
        }
        
        return points;
    }
    
    protected List<TerrainPoint> specialCase(TerrainPoint center, int radius) {
        int minX = center.getX() - radius, 
            maxX = center.getX() + radius;
        
        int lineX = line.getLineX();
        
        if ( lineX < minX || lineX > maxX ) {
            return Collections.EMPTY_LIST;
        }
        
        double dSqrt = Math.sqrt(radius * radius - (lineX - center.getX()) * (lineX - center.getX()));
        
        int sqrt = new Double(Math.rint(dSqrt)).intValue();
        
        TerrainPoint point1 = null, point2 = null;
        
        if ( sqrt == 0 ) {
            point1 = new TerrainPoint(lineX, center.getY());
        } else {
            point1 = new TerrainPoint(lineX, center.getY() + sqrt);
            point2 = new TerrainPoint(lineX, center.getY() - sqrt);
        }
        
        List<TerrainPoint> points = new ArrayList<>(2);

        if ( this.contains(point1) ) {
            points.add( point1 );
        } 
        
        if ( point2 != null && this.contains(point2) ) {
            points.add( point2 );
        }
        
        return points;
    }
    
    /**
     * Equation of line is correct : k != Infinite, b != Infinite
     * @param center
     * @param radius
     * @return
     */
    protected List<TerrainPoint> commonCase(TerrainPoint center, int radius) {
        double k = line.getK(),
               b = line.getB();
        
        double A = k * k + 1, 
               B = 2 * k * (b - center.getY()) - 2 * center.getX(), 
               C = center.getX() * center.getX() + 
                (b - center.getY()) * (b - center.getY()) - radius * radius;
        double D = B * B - 4 * A * C; // B^2 - 4AC
        
        if ( D < 0 ) {
            // no intersection
            return Collections.EMPTY_LIST;
        } else if ( D == 0 ) {
            double x = -1 * B / (2 * A);
            double y = k * x + b;
            
            TerrainPoint point1 = TerrainPoint.nearestPoint(x, y);
                    
            if ( point1 != null && this.contains(point1) ) {
                return Arrays.asList( point1 );
            }
        } else /* D > 0 */ {
            double dX1 = ( -1 * B + Math.sqrt(D) ) / (2 * A),
                   dX2 = ( -1 * B - Math.sqrt(D) ) / (2 * A), 
                   dY1 = k * dX1 + b,
                   dY2 = k * dX2 + b;
            
            TerrainPoint point1 = TerrainPoint.nearestPoint(dX1, dY1),
                         point2 = TerrainPoint.nearestPoint(dX2, dY2);
            
            List<TerrainPoint> points = new ArrayList<>(2);
            
            if ( point1 != null && this.contains(point1) ) {
                points.add( point1 );
            } 
            
            if ( point2 != null && this.contains(point2) ) {
                points.add( point2 );
            }
            
            return points;
        }
        
        return Collections.EMPTY_LIST;
    }
    
    protected boolean contains(int x, int y) {
        if ( start == null || stop == null ) {
            throw new NullPointerException("One of point is null.");
        }

        int leftLimitX = Math.min(start.getX(), stop.getX()),
            rightLimitX = Math.max(start.getX(), stop.getX());
        
        if ( x < leftLimitX || x > rightLimitX ) {
            return false;
        }
        
        if ( y < start.getY() || y > stop.getY() ) {
            return false;
        }
        
        return true;
    }
    
    protected boolean contains(TerrainPoint point) {
        return this.contains(point.getX(), point.getY());
    }
    
    private void calcEquationOfLine() {
        if ( start == null || stop == null ) {
            return;
        }
    
        line = TerrainLine.createByPoints(start, stop);
    }

    public TerrainLine getLine() {
        return line;
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.start);
        hash = 29 * hash + Objects.hashCode(this.stop);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RiverEdge other = (RiverEdge) obj;
        
        if (!Objects.equals(this.start, other.start)) {
            return false;
        }
        if (!Objects.equals(this.stop, other.stop)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "RiverEdge{" + "\n\tstart=" + start + ",\n\tstop=" + stop + ",\n\twidth=" + width + ",\n\tline=" + line + '}';
    }

}
