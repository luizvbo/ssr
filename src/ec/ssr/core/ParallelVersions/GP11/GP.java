/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ssr.core.ParallelVersions.GP11;

import ec.EvolutionState;
import ec.gp.GPIndividual;
import ec.ssr.core.Dataset;
import ec.ssr.core.ParallelVersions.SSR;
import ec.ssr.core.Utils;
import ec.ssr.functions.Function;
import ec.ssr.problems.Regression;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author luiz
 */
public class GP extends SSR{

    protected int numFolds;
    protected int stripSize;
    
    protected Dataset[] trainingFolds;
    protected int currentFold;
    
    public GP(Dataset trainingSet,
              Dataset validationSet,
              Dataset testSet, 
              String outputPath, 
              String outputPrefix, 
              int numIterations, 
              int numExecutions,
              long seed,
              double hitLevel, 
              String parameterFilePath,
              ArrayList inputParameters,
              int stripSize,
              int numFolds) throws NullPointerException, 
                                                  FileNotFoundException, 
                                                  IOException, Exception {
        super(trainingSet, validationSet, testSet, outputPath, outputPrefix, 
              numIterations, numExecutions, seed, hitLevel, parameterFilePath, 
              inputParameters);
        this.numFolds = numFolds;
        this.stripSize = stripSize;
    }
    
    @Override
    public void runAlgorithm() {
        // Sample the fold data
//        trainingFolds = Utils.getFoldSampling(numFolds, trainingSet, mainState.random[0]);
        trainingFolds = Utils.getFoldSampling(numFolds, trainingSet, null);
        mainState.startFresh();

        ((Regression)mainState.evaluator.p_problem).setHitLevel(hitLevel);
        ((Regression)mainState.evaluator.p_problem).setOutput(Utils.getDatasetOutputs(trainingSet));

        evolve();   

        GPIndividual bestSoFar = Utils.getBestIndividual(mainState);
        Function bestFunction =  (Function)bestSoFar.trees[0].child;

        solution = bestFunction;

        stats.updateOnIteration(solution, null);

        mainState.output.close();
    }
    
    protected void evolve(){
        long initTime = System.currentTimeMillis();
        int result = EvolutionState.R_NOTDONE;
        
        // Generations
        while(result == EvolutionState.R_NOTDONE ){
            if(stripSize < 0){
                // Use the whole training data (no random sample technique)gi
                ((Regression)mainState.evaluator.p_problem).setDataset(trainingSet);
            }
            else{
                if(mainState.generation % stripSize == 0 && !isLastGeneration())
                    ((Regression)mainState.evaluator.p_problem).setDataset(getCurrentFold());
                else if(isLastGeneration()){
                    ((Regression)mainState.evaluator.p_problem).setDataset(trainingSet);
                }
            }
            result = mainState.evolve();
            // Store the best fitness of generation
            stats.updateBestOfGeneration(mainState);
        }    
        
        System.out.println("Tempo: " + ((System.currentTimeMillis() - initTime)/1000.0) + " seg.");
    }
    
    private boolean isLastGeneration() {
        return mainState.generation == mainState.numGenerations-1;
    }
    
    private Dataset getCurrentFold() {
        Dataset fold = trainingFolds[currentFold++];
        if(currentFold == numFolds) currentFold = 0;
        return fold;
    }
}
