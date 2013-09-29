/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.terrain.river;

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
public class RiverModelTest {
    
    public RiverModelTest() {
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
    public void testAddEdge() {
        RiverModel river = new RiverModel();
        TerrainPoint point1 = new TerrainPoint(10, 10), 
                     point2 = new TerrainPoint(15, 20);
        
        river.addEdge(point1, 10);
        river.setLastPoint(point2);
        
        assertNotNull(river.getChannelEdges());
        assertTrue(river.getChannelEdges().size() == 1);
        
        RiverEdge edge = river.getChannelEdges().get(0);
        
        assertEquals(point1, edge.getStart());
        assertEquals(point2, edge.getStop());
    }
    
    @Test
    public void testFindPointByDistance_Common() {
        RiverModel river = new RiverModel();
        TerrainPoint point1 = new TerrainPoint(10, 10), 
                     point2 = new TerrainPoint(15, 20);
        
        river.addEdge(point1, 10);
        river.setLastPoint(point2);
        
        System.out.println("Edge len = " + TerrainPoint.distance(point1, point2)); // 11.18
        
        double distance = 4;
        
        RiverEdge edge = river.getChannelEdges().get(0);
        RiverRange.Pair foundPair = river.findPointByDistance(edge, edge.getStart(), distance, 0.0001);
        
        assertNotNull(foundPair);
        assertNotNull(foundPair.getPoints());
        assertTrue(foundPair.getPoints().size() == 1);
        
        TerrainPoint foundPoint = foundPair.getPoints().get(0);
        
        System.out.println("Point on edge = " + foundPoint);
        System.out.println(String.format("Distance: expected = %f, real = %f", distance, TerrainPoint.distance(point1, foundPoint)));
        
        assertNotNull(foundPoint);
    }
    
    @Test
    public void testFindPointByDistance_Special() {
        RiverModel river = new RiverModel();
        TerrainPoint point1 = new TerrainPoint(10, 10), 
                     point2 = new TerrainPoint(10, 20);
        
        river.addEdge(point1, 10);
        river.setLastPoint(point2);
        
        System.out.println("Edge len = " + TerrainPoint.distance(point1, point2)); // 11.18
        
        double distance = 4;
        
        RiverEdge edge = river.getChannelEdges().get(0);
        RiverRange.Pair foundPair = river.findPointByDistance(edge, edge.getStart(), distance, 0.0001);
        
        assertNotNull(foundPair);
        assertNotNull(foundPair.getPoints());
        assertTrue(foundPair.getPoints().size() == 1);
        
        TerrainPoint foundPoint = foundPair.getPoints().get(0);
        
        System.out.println("Point on edge = " + foundPoint);
        System.out.println(String.format("Distance: expected = %f, real = %f", distance, TerrainPoint.distance(point1, foundPoint)));
        
        assertNotNull(foundPoint);
    }
    
    @Test
    public void testNaturalIndex_max() {
        RiverModel river = new RiverModel();
        TerrainPoint point1 = new TerrainPoint(10, 10), 
                     point2 = new TerrainPoint(15, 20),
                     point3 = new TerrainPoint(5, 30);

        int width1 = 10,
            width2 = 10;
        
        river.addEdge(point1, width1);
        river.addEdge(point2, width2);
        river.setLastPoint(point3);
        
        for (int i = 0; i < width1; i++) {
            river.setHeight(point1.getX() + i, point1.getY(), 120);
        }
        
        for (int i = 0; i < width2; i++) {
            river.setHeight(point2.getX() + i, point2.getY(), 105);
        }
        
        System.out.println("Avg width: " + river.averageWidth() ) ;
        
        double actualIndex = river.naturalIndex();
        System.out.println(String.format("Naturality: %.2f ", actualIndex )) ;
        
        assertEquals(1, actualIndex, 0.0001);
    }
    
    @Test
    public void testNaturalIndex_min() {
        RiverModel river = new RiverModel();
        TerrainPoint point1 = new TerrainPoint(10, 10), 
                     point2 = new TerrainPoint(15, 20),
                     point3 = new TerrainPoint(5, 30);

        int width1 = 10,
            width2 = 20;
        
        river.addEdge(point1, width1);
        river.addEdge(point2, width2);
        river.setLastPoint(point3);
        
        for (int i = 0; i < width1; i++) {
            river.setHeight(point1.getX() + i, point1.getY(), 100);
        }
        
        for (int i = 0; i < width2; i++) {
            river.setHeight(point2.getX() + i, point2.getY(), 105);
        }
        
        System.out.println("Avg width: " + river.averageWidth() ) ;
        double actualIndex = river.naturalIndex();
        System.out.println(String.format("Naturality: %.2f ", actualIndex )) ;
        
        assertEquals(0, actualIndex, 0.0001);
    }
    
    @Test
    public void testNaturalIndex_half() {
        RiverModel river = new RiverModel();
        TerrainPoint point1 = new TerrainPoint(10, 10), 
                     point2 = new TerrainPoint(15, 20),
                     point3 = new TerrainPoint(5, 30);

        int width1 = 10,
            width2 = 10;
        
        river.addEdge(point1, width1);
        river.addEdge(point2, width2);
        river.setLastPoint(point3);
        
        for (int i = 0; i < width1/2; i++) {
            river.setHeight(point1.getX() + i, point1.getY(), 120);
        }
        
        for (int i = 0; i < width1/2; i++) {
            river.setHeight(point1.getX() - i, point1.getY() + 5, 100);
        }
        
        for (int i = 0; i < width2; i++) {
            river.setHeight(point2.getX() + i, point2.getY(), 105);
        }
        
        System.out.println("Avg width: " + river.averageWidth() ) ;
        double actualIndex = river.naturalIndex();
        System.out.println(String.format("Naturality: %.2f ", actualIndex )) ;
        
        assertEquals(0.5, actualIndex, 0.0001);
    }
}