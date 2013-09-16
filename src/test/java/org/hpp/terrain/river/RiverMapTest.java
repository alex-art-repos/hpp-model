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
        RiverMap instance = new RiverMap();
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
        RiverMap instance = new RiverMap();
        instance.addEdge(point, width);
        
        assertNotNull( instance.edges() );
        assertFalse( instance.edges().isEmpty() );
        
        assertEquals(point, instance.edges().get(0).getStart());
    }

    @Test
    public void testAddEdge_Edge() {
        TerrainPoint point = new TerrainPoint(100, 25);
        int width = 150;
        RiverMap instance = new RiverMap();
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
        
        RiverMap instance = new RiverMap();
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
        RiverMap instance = new RiverMap();
        
        assertNotNull(instance.edges());
        assertTrue(instance.edges().isEmpty());
    }

    /**
     * Test of getHeight method, of class RiverMap.
     */
    @Test
    public void testGetHeight_TerrainPoint() {
        TerrainPoint point = new TerrainPoint(100, 51);
        RiverMap instance = new RiverMap();
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
        RiverMap instance = new RiverMap();
        Integer result = instance.getHeight(x, z);
        assertNull(result);
    }

    @Test
    public void testGetHeightWithSet_int_int() {
        int x = 150;
        int z = 200;
        int height = 12500;
        RiverMap instance = new RiverMap();
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
        RiverMap instance = new RiverMap();
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
        RiverMap instance = new RiverMap();
        
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
        
        RiverMap instance = new RiverMap();
        
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
        
        RiverMap instance = new RiverMap();
        
        instance.setHeight(thePoint, height);
        
        Integer newHeight = instance.getHeight(x, y);
        
        assertEquals(new Integer(height), newHeight);
    }
}