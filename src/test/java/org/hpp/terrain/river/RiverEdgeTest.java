/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.terrain.river;

import java.util.List;
import org.hpp.terrain.TerrainPoint;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Gautama
 */
public class RiverEdgeTest {
    
    public RiverEdgeTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testEquationOfLine() {
        int x1 = 1, x2 = 3, y1 = 1, y2 = 5;
        
        double k = (y1-y2)/(double)(x1-x2),
               b = y1 - k * x1;
        
        System.out.println("Expected K = " + k);
        System.out.println("Expected B = " + b);
        
        RiverEdge edge = new RiverEdge(new TerrainPoint(x1, y1), new TerrainPoint(x2, y2), 10);
        
        System.out.println("Actual line = " + edge.getLine());
        
        assertEquals(k, edge.getLine().getK(), 0.0001);
        assertEquals(b, edge.getLine().getB(), 0.0001);
    }
    
    @Test
    public void testEquationOfLine2() {
        int x1 = 1, x2 = 4, y1 = 1, y2 = 8;
        
        double k = (y1-y2)/(double)(x1-x2),
               b = y1 - k * x1;
        
        System.out.println("Expected K = " + k);
        System.out.println("Expected B = " + b);
        
        RiverEdge edge = new RiverEdge(new TerrainPoint(x1, y1), new TerrainPoint(x2, y2), 10);
        
        System.out.println("Actual line = " + edge.getLine());
        
        assertEquals(k, edge.getLine().getK(), 0.0001);
        assertEquals(b, edge.getLine().getB(), 0.0001);
    }
    
    @Test
    public void testContains_Bounds() {
        int x1 = 1, x2 = 4, y1 = 1, y2 = 8;
        
        RiverEdge edge = new RiverEdge(new TerrainPoint(x1, y1), new TerrainPoint(x2, y2), 10);

        assertTrue( edge.contains(x1, y1) );
        assertTrue( edge.contains(x2, y2) );
        
        assertTrue( edge.contains(x2, y1) );
        assertTrue( edge.contains(x1, y2) );
    }
    
    @Test
    public void testContains_InternalPoints() {
        int x1 = 1, x2 = 4, y1 = 1, y2 = 8;
        
        RiverEdge edge = new RiverEdge(new TerrainPoint(x1, y1), new TerrainPoint(x2, y2), 10);

        assertTrue( edge.contains(x1 + 1, y1) );
        assertTrue( edge.contains(x2, y2-1) );
        
        assertTrue( edge.contains(x2-1, y1) );
        assertTrue( edge.contains(x1+1, y2) );
    }
    
    @Test
    public void testContains_InternalPoints2() {
        int x2 = 1, x1 = 4, y1 = 1, y2 = 8;
        
        RiverEdge edge = new RiverEdge(new TerrainPoint(x1, y1), new TerrainPoint(x2, y2), 10);

        assertTrue( edge.contains(x1 - 1, y1) );
        assertTrue( edge.contains(x2, y2-1) );
        
        assertTrue( edge.contains(x2+1, y1) );
        assertTrue( edge.contains(x1-1, y2) );
    }
    
    @Test
    public void testCircleIntersection() {
        int x1 = 3, x2 = 6, y1 = 2, y2 = 4;

        RiverEdge edge = new RiverEdge(new TerrainPoint(x1, y1), new TerrainPoint(x2, y2), 10);

        System.out.println("Actual line = " + edge.getLine());
        
        List<TerrainPoint> points = edge.circleIntersection( new TerrainPoint(2, 2) , 1);
        
        for (TerrainPoint point : points) {
            System.out.println("Intersection point: " + point);
        }
        
        assertEquals(new TerrainPoint(3, 2), points.get(0));
    }
    
    @Test
    public void testCircleIntersection_Double() {
        int x1 = 0, x2 = 6, y1 = 0, y2 = 4;

        RiverEdge edge = new RiverEdge(new TerrainPoint(x1, y1), new TerrainPoint(x2, y2), 10);

        System.out.println("Actual line = " + edge.getLine());
        
        List<TerrainPoint> points = edge.circleIntersection( new TerrainPoint(2, 2) , 1);
        
        for (TerrainPoint point : points) {
            System.out.println("Intersection point: " + point);
        }
        
        assertEquals(new TerrainPoint(2, 1), points.get(0));
        assertEquals(new TerrainPoint(3, 2), points.get(1));
    }
    
    @Test
    public void testCircleIntersection_Special() {
        int x1 = 0, x2 = 0, y1 = 0, y2 = 0;

        RiverEdge edge = new RiverEdge(new TerrainPoint(x1, y1), new TerrainPoint(x2, y2), 10);

        System.out.println("Actual line = " + edge.getLine());
        
        List<TerrainPoint> points = edge.circleIntersection( new TerrainPoint(2, 2) , 1);
        
        assertTrue( points.isEmpty() );
    }
    
    @Test
    public void testCircleIntersection_SpecialOnePoint() {
        int x1 = 3, x2 = 3, y1 = 0, y2 = 2;

        RiverEdge edge = new RiverEdge(new TerrainPoint(x1, y1), new TerrainPoint(x2, y2), 10);

        System.out.println("Actual line = " + edge.getLine());
        
        List<TerrainPoint> points = edge.circleIntersection( new TerrainPoint(2, 2) , 1);
        
        for (TerrainPoint point : points) {
            System.out.println("Intersection point: " + point);
        }
        
        assertEquals(new TerrainPoint(3, 2), points.get(0));
    }
    
    @Test
    public void testCircleIntersection_SpecialTwoPoint() {
        int x1 = 2, x2 = 2, y1 = 0, y2 = 2;

        RiverEdge edge = new RiverEdge(new TerrainPoint(x1, y1), new TerrainPoint(x2, y2), 10);

        System.out.println("Actual line = " + edge.getLine());

        List<TerrainPoint> points = edge.circleIntersection( new TerrainPoint(2, 2) , 1);

        for (TerrainPoint point : points) {
            System.out.println("Intersection point: " + point);
        }

        assertEquals(new TerrainPoint(2, 1), points.get(0));
    }
    
    @Test
    public void testCircleIntersection_SpecialTwoPoint2() {
        int x1 = 2, x2 = 2, 
            y1 = 0, y2 = 4;

        RiverEdge edge = new RiverEdge(new TerrainPoint(x1, y1), new TerrainPoint(x2, y2), 10);

        System.out.println("Actual line = " + edge.getLine());

        List<TerrainPoint> points = edge.circleIntersection( new TerrainPoint(2, 2) , 1);

        assertNotNull(points);
        
        for (TerrainPoint point : points) {
            System.out.println("Intersection point: " + point);
        }

        assertFalse( points.isEmpty() );
        assertEquals(new TerrainPoint(2, 1), points.get(0));
        assertEquals(new TerrainPoint(2, 3), points.get(1));
    }
}