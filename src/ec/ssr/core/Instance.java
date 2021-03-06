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
    private static int IDCounter = 0;
    
    /** Instance input (one or more). */
    public double[] input;
    
    /** Instance output (only one). */
    public double output;
    
    /** Normalized output. */
//    public double normOutput;
    
    /** Instance identifier. */
    public int id;

    /**
     * Constructor declaration
     * @param input Instance input 
     * @param output Instance output
     */
//    public Instance(double[] input, double output, int IDCounter) {
    public Instance(double[] input, double output) {
        this.input = input;
        this.output = output;
//        normOutput = output;
        id = IDCounter++;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        for(int i = 0; i < input.length; i++){
            out.append(input[i]).append(",");
        }
        return out.toString() + output;
    }

//    public void resetNormalizedOutput() {
//        normOutput = output;
//    }
}
