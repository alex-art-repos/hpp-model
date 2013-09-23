/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.utils;

import java.util.Random;

/**
 *
 * @author Gautama
 */
public final class RandomUtil {

    private static Random rnd = new Random(System.nanoTime());
    
    private RandomUtil() {
        throw new IllegalAccessError("Can`t creat instance.");
    }
    
    public static int randomInRange(int min, int max) {
        if ( (max - min) == 0 ) {
            return min;
        }
        
        return min + rnd.nextInt(max - min);
    }
    
    public static int randomGaussInRange(int min, int max) {
        if ( (max - min) == 0 ) {
            return min;
        }
        
        return min + new Double((max-min) * Math.abs(rnd.nextGaussian())).intValue();
    }
    
    public static boolean randomBool() {
        return rnd.nextBoolean();
    }
    
    public static double randomDouble() {
        return rnd.nextDouble();
    }
}
