/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ssr.handlers;

import ec.ssr.core.Dataset;
import ec.ssr.core.Instance;
import ec.ssr.core.Utils;
import ec.util.MersenneTwisterFast;
import java.io.File;
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
    
    /** Boolean to indicates if we should sample or read the folds from files **/
    private boolean useFiles = false;

    public CrossvalidationHandler(int numFolds) {
        this.numFolds = numFolds;
        currentFold = 0;
    }
    
    public CrossvalidationHandler(){
        useFiles = true;
        currentFold = 0;
    }
    
    /**
     * Sample partitions for cross validation. Not stratified.
     */
    private void resampleFolds() {
        folds = Utils.getFoldSampling(numFolds, dataset, rnd);
//        folds = new Dataset[numFolds];
//        ArrayList<Instance> dataCopy = new ArrayList<Instance>(dataset.data);
//        int foldIndex = 0;
//        if(rnd == null){
//            Iterator<Instance> it = dataCopy.iterator();
//            while(it.hasNext()){
//                if(folds[foldIndex] == null)
//                    folds[foldIndex] = new Dataset();
//                folds[foldIndex].add(it.next());
//                it.remove();
//                if(foldIndex < numFolds - 1) foldIndex++;
//                else foldIndex = 0;
//            }
//        }
//        else{
//            while(!dataCopy.isEmpty()){
//                if(folds[foldIndex] == null)
//                    folds[foldIndex] = new Dataset();
//                folds[foldIndex].add(dataCopy.remove(rnd.nextInt(dataCopy.size())));
//                if(foldIndex < numFolds - 1) foldIndex++;
//                else foldIndex = 0;
//            }
//        }
    }
    
    /**
     * Get a training/test set based on cross validation. Make one partition as test and remaining as training. 
     * @return Array with training and test data in first and second positions, respectvely
     */
    @Override
    public Dataset[] getTrainintTestData(){
        if(currentFold == 0 && !useFiles){
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
    public void setDataset(String dataPath) throws Exception{
        if(!useFiles){
            Dataset data = FileHandler.readInputDataFile(dataPath);
            this.dataset = data;
        }
        else{
            getFoldsFromFile(dataPath);
        }
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

    /**
     * Get the folds from a list of files named prename#posname, where # is the 
     * index for the fold inside the file. 
     * @param dataPath Full path to the file with the pattern prename#posname.
     * @throws Exception Error while reading the dataset within a file.
     * @throws SSRException Error in the file path/pattern.
     */
    private void getFoldsFromFile(String dataPath) throws Exception, SSRException{
        int lastFileSeparator = dataPath.lastIndexOf(File.separator);
        String filePattern = dataPath.substring(lastFileSeparator + 1);
        String folderName = dataPath.substring(0, lastFileSeparator);
        String[] aux = filePattern.split("#");
        if(aux.length != 2)
            throw new SSRException("The file pattern must have one and only one # symbol as fold index.");
        ArrayList<File> files = new ArrayList<File>();
        int index = 0;
        File newFold = new File(folderName + File.separator + aux[0] + index + aux[1]);
        while(newFold.isFile()){
            files.add(newFold);
            index++;
            newFold = new File(folderName + File.separator + aux[0] + index + aux[1]);
        }
        if(files.isEmpty()) 
            throw new SSRException("No files found for this file pattern/path.");
        numFolds = files.size();
        folds = new Dataset[numFolds];
        for(int i = 0; i < numFolds; i++){
            folds[i] = FileHandler.readInputDataFile(files.get(i));
        }
    }
}
