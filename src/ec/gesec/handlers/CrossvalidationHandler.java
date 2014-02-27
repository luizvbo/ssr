/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gesec.handlers;

import ec.gesec.core.Dataset;
import ec.gesec.core.Instance;
import ec.util.MersenneTwisterFast;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * CrossvalidationHandler.java
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class CrossvalidationHandler implements DataProducer{
    /** Curretn fold. Used to return a training/test set.*/
    private int currentFold;
    
    /** Array of folds, used for complete a crossvalidation. */
    private Dataset[] folds;
    
    /** Random number generator. */
    private MersenneTwisterFast rnd;
    
    /** Number of folds. */
    private int numFolds;
    
    /** Input dataset. */
    private Dataset dataset;
    
    /**
     * Constructor declaration.
     * @param dataset Input dataset
     * @param numFolds Number of folds (k)
     * @param rnd Random number generator used to shuffle data.
     */
//    public CrossvalidationHandler(Dataset dataset, int numFolds, MersenneTwisterFast rnd) {
//        this.rnd = rnd;
//        this.dataset = dataset;
//        this.numFolds = numFolds;
//        
//        this.currentFold = 0;
//    }

    public CrossvalidationHandler(int numFolds) {
        this.numFolds = numFolds;
        this.currentFold = 0;
    }
    
    
    /**
     * Sample partitions for cross validation. Not stratified.
     */
    private void resampleFolds() {
        folds = new Dataset[numFolds];
        ArrayList<Instance> dataCopy = new ArrayList<Instance>(dataset.data);
        int foldIndex = 0;
        if(rnd == null){
            Iterator<Instance> it = dataCopy.iterator();
            while(it.hasNext()){
                if(folds[foldIndex] == null)
                    folds[foldIndex] = new Dataset();
                folds[foldIndex].add(it.next());
                it.remove();
                if(foldIndex < numFolds - 1) foldIndex++;
                else foldIndex = 0;
            }
        }
        else{
            while(!dataCopy.isEmpty()){
                if(folds[foldIndex] == null)
                    folds[foldIndex] = new Dataset();
                folds[foldIndex].add(dataCopy.remove(rnd.nextInt(dataCopy.size())));
                if(foldIndex < numFolds - 1) foldIndex++;
                else foldIndex = 0;
            }
        }
    }
    
    /**
     * Get a training/test set based on cross validation. Make one partition as test and remaining as training. 
     * @return Array with training and test data in first and second positions, respectvely
     */
    @Override
    public Dataset[] getTrainintTestData(){
        if(currentFold == 0){
            resampleFolds();
        }
        Dataset[] data = new Dataset[2];
        for(int i = 0; i < numFolds; i++){
            if(i == currentFold)
                data[1] = new Dataset(folds[i]);
            else{
                if(data[0] == null)
                    data[0] = new Dataset(folds[i]);
                else 
                    data[0].add(folds[i]);
            }
            
        }
        if(currentFold < numFolds - 1) currentFold++;
        else currentFold = 0;
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
        if(numFolds > 1)
            return true;
        return false;
    }
}
