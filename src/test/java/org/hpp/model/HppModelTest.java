/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.model;

import java.io.File;
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
public class HppModelTest {
    
    public HppModelTest() {
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
    public void testSerialization() throws Exception {
        HppModel model = new HppModel();
        
        model.setCap_user(15000);
        
        String testFile = "test-model.xml";
        
        model.saveToFile(testFile, false);
        
        HppModel loadedModel = HppModel.loadFromFile(testFile, false);
        
        assertEquals(model.getCap_user(), loadedModel.getCap_user(), 0.00001);
        
        new File(testFile).delete();
    }
}