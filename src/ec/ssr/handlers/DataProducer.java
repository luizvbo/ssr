/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ssr.handlers;

import ec.ssr.core.Dataset;
import ec.util.MersenneTwisterFast;

/**
 * DataProducer.java
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public interface DataProducer {
    /**
     * Returns an array with trainin and test set, in this order
     * @return Two positions array
     */
    public Dataset[] getTrainintTestData();
    
    /**
     * Sets a dataset
     * @param dataset Dataset used
     */
    public void setDataset(Dataset dataset);
    
    /**
     * Sets the random number generator
     * @param rnd A MersenneTwisterFast objetct, used by ECJ
     */
    public void setRandomGenerator(MersenneTwisterFast rnd);
    
    /**
     * Check if parameter ranges are correct.
     * @return True if everything is ok.
     */
    public boolean isValid();
}
