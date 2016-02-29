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
     * Sets a dataset path
     * @param trainingPath Path for the (training) dataset used
     * @param testPath Path for the (test) dataset used
     * @throws java.lang.Exception Exception caused when reading dataset file(s)
     */
    public void setDataset(String trainingPath, String testPath) throws Exception;
    
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
