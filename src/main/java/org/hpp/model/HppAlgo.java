/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpp.model;

/**
 *
 * @author Gautama
 */
public class HppAlgo {

    private HppModel model = null;
    
    public HppAlgo(HppModel theModel) {
        super();
        model = theModel;
    }

    public HppModel getModel() {
        return model;
    }

    public void setModel(HppModel model) {
        this.model = model;
    }
    
}
