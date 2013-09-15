/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.ui.ctrl;

import javax.swing.JFrame;

/**
 *
 * @author Gautama
 */
public class BaseController<T extends JFrame> {
    private T form;

    public T getForm() {
        return form;
    }

    public void setForm(T form) {
        this.form = form;
    }
    
}
