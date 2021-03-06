/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ssr.core.ParallelVersions.SSR8;

import ec.ssr.core.ParallelVersions.SSR4.*;
import ec.gp.GPIndividual;
import ec.simple.SimpleStatistics;
import ec.ssr.core.Dataset;
import ec.ssr.core.Instance;
import ec.ssr.core.ParallelVersions.SSR1.SolutionSSR1;
import ec.ssr.core.ParallelVersions.SSR2.NormalizationParameters;
import ec.ssr.functions.Function;
import ec.ssr.problems.Regression;
import ec.ssr.problems.fitness.FitnessInterface;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Version with range normalization (student-t) and internal crossover
 * - New outputs are calculated over the normalized output.
 * - Utilizes only 50% of the training data at each new Iteration.
 * @author luiz
 */
public class SSR8 extends SSR4{
    public SSR8(Dataset trainingSet,
                Dataset validationSet,
                Dataset testSet, 
                String outputPath, 
                String outputPrefix, 
                int numIterations, 
                int numExecutions,
                long seed,
                double hitLevel, 
                String parameterFilePath,
                ArrayList inputParameters) throws NullPointerException, 
                                                  FileNotFoundException, 
                                                  IOException, Exception {
        super(trainingSet, validationSet, testSet, outputPath, outputPrefix, 
              numIterations, numExecutions, seed, hitLevel, parameterFilePath, 
              inputParameters);
    }
 
    @Override
    public void runAlgorithm() {
        boolean canStop = false;
        resetNormalizedOutputs();
        
        int currentIteration = 0;
        while(!canStop){
            System.out.println("\nIteration: " + (currentIteration+1));
            mainState.startFresh();
            
            NormalizationParameters normParameters = normalizeData();
            Dataset sampledData = getResampledData();
            // The original expected output
            double normalizedOutput[] = getDatasetNormOutputs(sampledData);
            
            // Load new inputs on the proble Object
            ((Regression)mainState.evaluator.p_problem).setDataset(sampledData);
            ((Regression)mainState.evaluator.p_problem).setHitLevel(hitLevel);
            ((Regression)mainState.evaluator.p_problem).setOutput(normalizedOutput);
            
            evolve();
            
//            int result = EvolutionState.R_NOTDONE;
//            // Generations
//            while(result == EvolutionState.R_NOTDONE ){
//                result = mainState.evolve();
//                // Store the best fitness of generation
//                stats.updateBestOfGeneration(mainState);
//            }     
            
            GPIndividual bestSoFar = (GPIndividual)((SimpleStatistics)mainState.statistics).getBestSoFar()[0];
            FitnessInterface fitness = (FitnessInterface)bestSoFar.fitness;
            Function bestFunction =  (Function)bestSoFar.trees[0].child;
            
            if(currentIteration == maxIterations - 1 || fitness.isPerfectFitness(sampledData.size())){
                addLastFunctionToSolution(bestFunction, normParameters);
                canStop = true;
            }
            else{
                double tr = mainState.random[0].nextDouble(); // it ensures (1-r) != 0
                addFunctionToSolution(bestFunction, tr, normParameters);
                
                setNewOutput(sampledData, tr);
            }
                        
            stats.updateOnIteration(solution, normalizedOutput);
//            stats.updatePontualError(bestFunction, lastOutput);
//            stats.updateIterativeErrors(solution);
//            stats.updateOutputVectors(lastOutput);
//            stats.updateSolutionSize(solution);

//            stats.finishIteration();
            currentIteration++;
            mainState.output.close();
        }
    }
    
    /**
     * Generates dataset composed by 50% of the training dataset, sampled uniformly
     * @return The new sampled dataset
     */
    protected Dataset getResampledData(){
        int n = (int)Math.round(0*trainingSet.size());
//        int n = (int)Math.round(.5*trainingSet.size());
        Dataset dataCopy = trainingSet.softClone();
        for(int i = 0; i < n; i++){
            dataCopy.data.remove(mainState.random[0].nextInt(dataCopy.size()));
        }
        return dataCopy;
    }
    
    protected NormalizationParameters normalizeData() {
        // Calculates the mean
        double mean = 0;
        for(int i = 0; i < trainingSet.size(); i++){
//            mean += trainingSet.get(i).normOutput;
        }       
        mean = mean/trainingSet.size();
        // Calculates the std. dev.
        double std = 0;
        for(int i = 0; i < trainingSet.size(); i++){
//            double aux = trainingSet.get(i).normOutput - mean;
//            std += aux * aux;
        }
        std /= trainingSet.size()-1;
        std = Math.sqrt(std);
        
        for(int i = 0; i < trainingSet.size(); i++){
//            double aux =  (trainingSet.get(i).normOutput-mean)/std;
//            trainingSet.get(i).normOutput = aux;
        }
        return new NormalizationParameters(mean, std);
    }

    private void resetNormalizedOutputs() {
        for(int i = 0; i < trainingSet.size(); i++){
//            trainingSet.get(i).resetNormalizedOutput();
        } 
    }
    
    protected void setNewOutput(Dataset dataset, double tr){
        for(int i = 0; i < dataset.size(); i++){
            Instance instance = dataset.get(i);
            double output = ((SolutionSSR1)currentFunction).getT1().eval(instance.input);
//            instance.normOutput = (instance.normOutput - tr*output)/(1-tr);
        }
    }
    
    /**
     * Generates a array with the expected normalized outputs of a dataset, respecting the order
     * @param dataset Input dataset
     * @return Double array with the outputs with the same order of the dataset
     */
    protected final double[] getDatasetNormOutputs(Dataset dataset) {
        double[] output = new double[dataset.size()];
        int i = 0;
        for(Instance instance : dataset.data){
//            output[i++] = instance.normOutput;
        }
        return output;
    }
}
