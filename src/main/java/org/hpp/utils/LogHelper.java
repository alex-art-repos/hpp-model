/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *
 * @author Gautama
 */
public final class LogHelper {

    private static final LogHelper instance = new LogHelper();
    
    private LogHelper() {
        super();
    }
    
    public static LogHelper self() {
        return instance;
    }
    
    public String printException(String msg, Exception exc) {
        StringWriter strWriter = new StringWriter();
        
        strWriter.write(msg);
        
        PrintWriter printWriter = new PrintWriter(strWriter);
        
        exc.printStackTrace(printWriter);
        
        return strWriter.toString();
    }
}
