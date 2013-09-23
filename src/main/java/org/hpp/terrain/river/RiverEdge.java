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
import org.hpp.terrain.TerrainPoint;

/**
 *
 * @author Gautama
 */
public class RiverEdge {
    private TerrainPoint start = null;
    private TerrainPoint stop = null;
    
    private int width = 0;
    
    // line y = kx + b
    private boolean isSpecial = false; // x = val
    private int lineX = 0;
    private double k = 0;
    private double b = 0;

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

    public double getK() {
        return k;
    }

    public void setK(double k) {
        this.k = k;
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }

    public boolean isIsSpecial() {
        return isSpecial;
    }

    public void setIsSpecial(boolean isSpecial) {
        this.isSpecial = isSpecial;
    }

    public int getLineX() {
        return lineX;
    }

    public void setLineX(int lineX) {
        this.lineX = lineX;
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
        
        if ( isSpecial ) {
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
            
            points.clear();
            points = Arrays.asList(forSort);
        }
        
        return points;
    }
    
    protected List<TerrainPoint> specialCase(TerrainPoint center, int radius) {
        int minX = center.getX() - radius, 
            maxX = center.getX() + radius;
        
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
            
            TerrainPoint point1 = this.nearestPoint(x, y);
                    
            if ( point1 != null && this.contains(point1) ) {
                return Arrays.asList( point1 );
            }
        } else /* D > 0 */ {
            double dX1 = ( -1 * B + Math.sqrt(D) ) / (2 * A),
                   dX2 = ( -1 * B - Math.sqrt(D) ) / (2 * A), 
                   dY1 = k * dX1 + b,
                   dY2 = k * dX2 + b;
            
            TerrainPoint point1 = this.nearestPoint(dX1, dY1),
                         point2 = this.nearestPoint(dX2, dY2);
            
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
    
    protected TerrainPoint nearestPoint(double x, double y) {
        int x1 = new Double(Math.floor(x)).intValue(),
            x2 = new Double(Math.ceil(x)).intValue(),
            y1 = new Double(Math.floor(y)).intValue(),
            y2 = new Double(Math.ceil(y)).intValue();
        
        double dist[] = new double[4];
        
        dist[0] = this.distance(x1, y1, x, y);
        dist[1] = this.distance(x2, y1, x, y);
        dist[2] = this.distance(x1, y2, x, y);
        dist[3] = this.distance(x2, y2, x, y);
        
        int min = 0;
        
        for (int i = 0; i < dist.length; i++) {
            if ( dist[min] > dist[i] ) {
                min = i;
            }
        }

        switch(min) {
            case 0:
                return new TerrainPoint(x1, y1);
            case 1:
                return new TerrainPoint(x2, y1);
            case 2:
                return new TerrainPoint(x1, y2);
            case 3:
                return new TerrainPoint(x2, y2);
        }
        
        return null;
    }
    
    protected double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt( (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2) );
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
        
        if ( start.getX() - stop.getX() == 0 ) {
            isSpecial = true;
            lineX = start.getX();
        } else {
            k = (start.getY() - stop.getY()) / (double)( start.getX() - stop.getX() );
            b = start.getY() - k * start.getX();
        }
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
        return "RiverEdge{" + "start=" + start + ", stop=" + stop + ", width=" + width + ", k=" + k + ", b=" + b + '}';
    }

}
