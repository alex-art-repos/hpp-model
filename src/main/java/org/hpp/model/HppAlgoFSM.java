/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.hpp.model;

/**
 *
 * @author Gautama
 */
public class HppAlgoFSM {
    public static enum AlgoState {
        NOT_STARTED, 
        ENTER_BLOCK1, BLOCK1_OK, BLOCK1_FAIL, 
        ENTER_BLOCK2, BLOCK2_OK, BLOCK2_FAIL, 
        ENTER_BLOCK3, BLOCK3_FAIL,
        COMPLETED 
    }
    
    private AlgoState state = AlgoState.NOT_STARTED;

    private String msg = "";
    
    public HppAlgoFSM() {
        super();
    }
 
    public boolean enterBlock1() {
        boolean entered = state == AlgoState.NOT_STARTED 
                || state == AlgoState.BLOCK1_FAIL 
                || state == AlgoState.COMPLETED;
        
        if ( entered ) {
            state = AlgoState.ENTER_BLOCK1;
        }
        
        return entered;
    }
    
    public boolean enterBlock2() {
        boolean entered = state == AlgoState.BLOCK1_OK
                || state == AlgoState.BLOCK2_FAIL;
        
        if ( entered ) {
            state = AlgoState.ENTER_BLOCK2;
        }
        
        return entered;
    }
    
    public boolean enterBlock3() {
        boolean entered = state == AlgoState.BLOCK2_OK
                || state == AlgoState.BLOCK3_FAIL;
        
        if ( entered ) {
            state = AlgoState.ENTER_BLOCK3;
        }
        
        return entered;
    }
    
    public boolean isOk() {
        return state != AlgoState.BLOCK1_FAIL
                && state != AlgoState.BLOCK2_FAIL
                && state != AlgoState.BLOCK3_FAIL;
    }
    
    public String getStateString() {
        switch(state) {
            case BLOCK1_OK:
            case BLOCK1_FAIL:
                return "BLOCK1";
                
            case BLOCK2_OK:
            case BLOCK2_FAIL:
                return "BLOCK2";
                
            case BLOCK3_FAIL:
                return "BLOCK3";
                
            case NOT_STARTED:
                return "NOT_STARTED";
                
            case COMPLETED:
                return "COMPLETED";
        }
        
        return "UNKNOWN";
    }
    
    public void fail(String msg) {
        this.msg = msg;
        switch(state) {
            case ENTER_BLOCK1:
                state = AlgoState.BLOCK1_FAIL;
                break;
            case ENTER_BLOCK2:
                state = AlgoState.BLOCK2_FAIL;
                break;
            case ENTER_BLOCK3:
                state = AlgoState.BLOCK3_FAIL;
                break;
        }
    }
    
    public void ok(String msg) {
        this.msg = msg;
        switch(state) {
            case ENTER_BLOCK1:
                state = AlgoState.BLOCK1_OK;
                break;
            case ENTER_BLOCK2:
                state = AlgoState.BLOCK2_OK;
                break;
            case ENTER_BLOCK3:
                state = AlgoState.COMPLETED;
                break;
        }
    }
    
    public void reset() {
        state = AlgoState.NOT_STARTED;
        this.msg = null;
    }
    
    public AlgoState getState() {
        return state;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "HppAlgoFSM{" + "state=" + state + '}';
    }
}
