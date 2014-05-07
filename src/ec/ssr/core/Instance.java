/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ssr.core;

/**
 * Instance.java
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class Instance {
    /** Instance input (one or more). */
    public double[] input;
    
    /** Instance output (only one). */
    public double output;
    
    /** Instance identifier. */
    public int id;

    /**
     * Constructor declaration
     * @param input Instance input 
     * @param output Instance output
     * @param IDCounter Instance identifier
     */
    public Instance(double[] input, double output, int IDCounter) {
        this.input = input;
        this.output = output;
        id = IDCounter;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        for(int i = 0; i < input.length; i++){
            out.append(input[i]).append(",");
        }
        return out.toString() + output;
    }
}
