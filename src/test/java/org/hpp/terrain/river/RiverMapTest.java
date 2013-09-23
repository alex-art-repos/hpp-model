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
 * @author root
 */
public class RiverMapTest {
    
    public RiverMapTest() {
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

    /**
     * Test of clearEdges method, of class RiverMap.
     */
    @Test
    public void testClearEdges() {
        RiverModel instance = new RiverModel();
        instance.clearEdges();
        
        assertNotNull(instance.edges());
        assertTrue(instance.edges().isEmpty());
    }

    /**
     * Test of addEdge method, of class RiverMap.
     */
    @Test
    public void testAddEdge() {
        TerrainPoint point = new TerrainPoint(100, 25);
        int width = 150;
        RiverModel instance = new RiverModel();
        instance.addEdge(point, width);
        
        assertNotNull( instance.edges() );
        assertFalse( instance.edges().isEmpty() );
        
        assertEquals(point, instance.edges().get(0).getStart());
    }

    @Test
    public void testAddEdge_Edge() {
        TerrainPoint point = new TerrainPoint(100, 25);
        int width = 150;
        RiverModel instance = new RiverModel();
        RiverEdge edge = new RiverEdge(point, null, width);
        instance.addEdge( edge );
        
        assertNotNull( instance.edges() );
        assertFalse( instance.edges().isEmpty() );
        
        assertEquals(point, instance.edges().get(0).getStart());
    }
    
    /**
     * Test of removeEdge method, of class RiverMap.
     */
    @Test
    public void testRemoveEdge() {
        TerrainPoint point = new TerrainPoint(100, 25);
        int width = 150;
        
        RiverModel instance = new RiverModel();
        RiverEdge edge = instance.addEdge(point, width);
        
        instance.removeEdge(edge);
        assertNotNull( instance.edges() );
        assertTrue( instance.edges().isEmpty() );
    }

    /**
     * Test of edges method, of class RiverMap.
     */
    @Test
    public void testEdges() {
        RiverModel instance = new RiverModel();
        
        assertNotNull(instance.edges());
        assertTrue(instance.edges().isEmpty());
    }

    /**
     * Test of getHeight method, of class RiverMap.
     */
    @Test
    public void testGetHeight_TerrainPoint() {
        TerrainPoint point = new TerrainPoint(100, 51);
        RiverModel instance = new RiverModel();
        Integer expResult = 1200200;
        instance.setHeight(point, expResult);
        Integer result = instance.getHeight(point);
        
        assertEquals(expResult, result);
    }

    /**
     * Test of getHeight method, of class RiverMap.
     */
    @Test
    public void testGetHeight_int_int() {
        int x = 150;
        int z = 200;
        RiverModel instance = new RiverModel();
        Integer result = instance.getHeight(x, z);
        assertNull(result);
    }

    @Test
    public void testGetHeightWithSet_int_int() {
        int x = 150;
        int z = 200;
        int height = 12500;
        RiverModel instance = new RiverModel();
        instance.setHeight(x, z, height);
        Integer result = instance.getHeight(x, z);
        
        assertEquals(new Integer(height), result);
    }
    
    /**
     * Test of setHeight method, of class RiverMap.
     */
    @Test
    public void testSetHeight_3args() {
        int x = 150;
        int z = 330;
        int height = 1000;
        RiverModel instance = new RiverModel();
        instance.setHeight(x, z, height);
        Integer newHeight = instance.getHeight(x, z);
        
        assertEquals(new Integer( height ), newHeight);
    }

    /**
     * Test of setHeight method, of class RiverMap.
     */
    @Test
    public void testSetHeight_TerrainPoint_int() {
        TerrainPoint thePoint = new TerrainPoint(1500, 25400);
        int height = 15040;
        RiverModel instance = new RiverModel();
        
        instance.setHeight(thePoint, height);
        
        Integer newHeight = instance.getHeight(thePoint);
        
        assertEquals(new Integer(height), newHeight);
    }
    
    @Test
    public void testPointIdentity() {
        int x = 1500;
        int y = 25400;
        int height = 15040;
        
        TerrainPoint thePoint = new TerrainPoint(x, y);
        
        RiverModel instance = new RiverModel();
        
        instance.setHeight(x, y, height);
        
        Integer newHeight = instance.getHeight(thePoint);
        
        assertEquals(new Integer(height), newHeight);
    }
    
    @Test
    public void testPointIdentity2() {
        int x = 1500;
        int y = 25400;
        int height = 15040;
        
        TerrainPoint thePoint = new TerrainPoint(x, y);
        
        RiverModel instance = new RiverModel();
        
        instance.setHeight(thePoint, height);
        
        Integer newHeight = instance.getHeight(x, y);
        
        assertEquals(new Integer(height), newHeight);
    }
}