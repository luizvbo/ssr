/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ssr.core.ParallelVersions.SSR10;

import ec.EvolutionState;
import ec.gp.GPIndividual;
import ec.ssr.core.Dataset;
import ec.ssr.core.Instance;
import ec.ssr.core.ParallelVersions.SSR1.SolutionSSR1;
import ec.ssr.core.ParallelVersions.SSR2.NormalizationParameters;
import ec.ssr.core.ParallelVersions.SSR8.SSR8;
import ec.ssr.core.Utils;
import ec.ssr.functions.Function;
import ec.ssr.problems.Regression;
import ec.ssr.problems.RegressionRST2;
import ec.ssr.problems.fitness.FitnessInterface;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Similar to SSR4 but with internal crossvalidation: Given the number of folds
 * (k) and a strip size (s) the algorithm uses one of the k folds (sampled from
 * training set) as training/evaluation set at each s generations. At the last 
 * generation, it uses the whole original training set.
 * 
 * @author luiz
 */
public class SSR10 extends SSR8{
    protected Dataset[] trainingFolds;
    protected int currentFold;
    
    protected int numFolds;
    protected int stripSize;
    protected final double samplePercentage = 0.3;
    
    public SSR10(Dataset trainingSet,
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
        boolean canStop = false;
        // Sample the fold data
//        trainingFolds = Utils.getFoldSampling(numFolds, trainingSet, mainState.random[0]);
        trainingFolds = Utils.getFoldSampling(numFolds, trainingSet, null);
        // The original expected output
        HashMap<Integer, Double> outputs = getDatasetOutputs(trainingSet);
        int currentIteration = 0;
        while(!canStop){
            System.out.println("\nIteration: " + (currentIteration+1));
            mainState.startFresh();
            
//            double normalizedOutput[] = Arrays.copyOf(output, output.length);
            NormalizationParameters normParameters = normalizeData(outputs);
            
            // Load new inputs on the problem Object
            
            ((Regression)mainState.evaluator.p_problem).setHitLevel(hitLevel);
//            ((Regression)mainState.evaluator.p_problem).setOutput(normalizedOutput);
            
            evolve(outputs);   
            
            GPIndividual bestSoFar = Utils.getBestIndividual(mainState);
            FitnessInterface fitness = (FitnessInterface)bestSoFar.fitness;
            Function bestFunction =  (Function)bestSoFar.trees[0].child;
            
            if(currentIteration == maxIterations - 1 || fitness.isPerfectFitness(trainingSet.size())){
                addLastFunctionToSolution(bestFunction, normParameters);
                canStop = true;
            }
            else{
                double tr = mainState.random[0].nextDouble(); // it ensures (1-r) != 0
                addFunctionToSolution(bestFunction, tr, normParameters);
                getNewOutput(trainingSet, outputs, tr);
            }
                        
            stats.updateOnIteration(solution, null);
            
            currentIteration++;
            mainState.output.close();
        }
    }
    
    protected void getNewOutput(Dataset dataset, HashMap<Integer, Double> outputMap, double tr){
        for(int i = 0; i < dataset.size(); i++){
            Instance instance = dataset.get(i);
            double output = ((SolutionSSR1)currentFunction).getT1().eval(instance.input);
            double oldOutput = outputMap.get(instance.id);
            double newOutput = (oldOutput - tr*output)/(1-tr);
            outputMap.put(instance.id, newOutput);
        }
    }
    
    protected void evolve(HashMap<Integer, Double> outputMap){
        long initTime = System.currentTimeMillis();
        int result = EvolutionState.R_NOTDONE;
        
        // Generations
        while(result == EvolutionState.R_NOTDONE ){            
            if(mainState.generation % stripSize == 0 && !isLastGeneration())
                ((RegressionRST2)mainState.evaluator.p_problem).setDataset(getCurrentFold(), outputMap);
            else if(isLastGeneration()){
                ((RegressionRST2)mainState.evaluator.p_problem).setDataset(trainingSet, outputMap);
            }
            result = mainState.evolve();
            // Store the best fitness of generation
            stats.updateBestOfGeneration(mainState);
        }    
        
        System.out.println("Tempo: " + ((System.currentTimeMillis() - initTime)/1000.0) + " seg.");
    }

    private Dataset getCurrentFold() {
        Dataset fold = trainingFolds[currentFold++];
        if(currentFold == numFolds) currentFold = 0;
        return fold;
    }
    
//    private Dataset getCurrentFold(){
//        Dataset fold = new Dataset(trainingSet);
//        int size = (int)Math.round(samplePercentage * trainingSet.size());
//        for(int i = trainingSet.size(); i > size; i--){
//            fold.data.remove(mainState.random[0].nextInt(fold.size()));
//        }
//        return fold;
//    }

    private boolean isLastGeneration() {
        return mainState.generation == mainState.numGenerations-1;
    }

    /**
     * Creates a hash mapping each output to the respective instance (by id)
     * @param trainingSet
     * @return 
     */
    private HashMap<Integer, Double> getDatasetOutputs(Dataset trainingSet) {
        HashMap<Integer, Double> outputMap = new HashMap<Integer, Double>();
        Iterator<Instance> it = trainingSet.data.iterator();
        while(it.hasNext()) {
            Instance instance = it.next();
            outputMap.put(instance.id, instance.output);
        }
        return outputMap;
    }
    
    protected NormalizationParameters normalizeData(HashMap<Integer, Double> outputMap) {
        // Calculates the mean
        double mean = 0;
        for(Map.Entry<Integer, Double> output: outputMap.entrySet()) {
            mean += output.getValue();
        }
        mean = mean/outputMap.size();
        // Calculates the std. dev.
        double std = 0;
        for(Map.Entry<Integer, Double> output: outputMap.entrySet()) {
            double aux = output.getValue() - mean;
            std += aux * aux;
        }        
        std /= outputMap.size() - 1;
        std = Math.sqrt(std);
        
        for(Map.Entry<Integer, Double> output: outputMap.entrySet()) {
            double aux = (output.getValue()-mean)/std;
            output.setValue(aux);
        }
        return new NormalizationParameters(mean, std);
    }
}
