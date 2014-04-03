/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gesec.handlers;

import ec.gesec.core.Dataset;
import ec.gesec.core.Instance;
import ec.util.MersenneTwisterFast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * HoldoutHandler.java
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class HoldoutHandler implements DataProducer{
    /** Random number generator. */
    private MersenneTwisterFast rnd;
    
    /** Defines if absolute quatities are used */
    private boolean useAbsoluteQuantity;
    
    /** Input dataset. */
    private Dataset dataset;
    
    /** Percentage of data used for test. */
    private double testPercentage;
        
    /**
     * Constructor declaration.
     * @param dataset Input dataset
     * @param rnd Random number generator used to shuffle data. If it is null, data is not shuffled.
     * @param testPercentage Percentage used for test
     */
//    public HoldoutHandler(Dataset dataset, MersenneTwisterFast rnd, double testPercentage) {
//        this.rnd = rnd;
//        this.dataset = dataset;
//        this.testPercentage = testPercentage;
//    }

    /**
     * Constructor declaration.
     * @param testPercentage Percentage used for test
     */
    public HoldoutHandler(double testPercentage) {
        this.testPercentage = testPercentage;
        if(this.testPercentage > 1){
            useAbsoluteQuantity = true;
        }
        else{
            useAbsoluteQuantity = false;
        }
    }
    
    /**
     * Gets a training/test set based on holdout validation. A percentage of data
     * is used for test and remaining for training.
     * @return Array with training and test data in first and second positions, respectvely
     */
    @Override
    public Dataset[] getTrainintTestData(){
        Dataset[] data = new Dataset[2];
        ArrayList<Instance> dataCopy = new ArrayList<Instance>(dataset.data);
        data[0] = new Dataset();
        data[1] = new Dataset();
        int testSize = 0;
        if(!useAbsoluteQuantity){
            testSize = (int)Math.round(testPercentage * dataCopy.size());
        }
        else{
            testSize = (int)Math.round(testPercentage);
        }
        int trainingSize = dataCopy.size() - testSize;
        if(rnd == null){
            Iterator<Instance> it = dataCopy.iterator();
            for(int i = 0; i < trainingSize; i++){
                data[0].add(it.next());
                it.remove();
            }
            while(it.hasNext()){
                data[1].add(it.next());
                it.remove();
            }
        }
        else{
            for(int i = 0; i < testSize; i++){
                data[1].add(dataCopy.remove(rnd.nextInt(dataCopy.size())));
            }
            while(!dataCopy.isEmpty()){
                data[0].add(dataCopy.remove(rnd.nextInt(dataCopy.size())));
            }
        }
        return data;
    }

    @Override
    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    @Override
    public void setRandomGenerator(MersenneTwisterFast rnd) {
        this.rnd = rnd;
    }

    @Override
    public boolean isValid() {
        if(testPercentage >= 0)
            return true;
        return false;
    }
}
