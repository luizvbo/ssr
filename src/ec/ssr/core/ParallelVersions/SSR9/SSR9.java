/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ssr.core.ParallelVersions.SSR9;

import ec.ssr.core.ParallelVersions.SSR4.*;
import ec.EvolutionState;
import ec.gp.GPIndividual;
import ec.ssr.core.Dataset;
import ec.ssr.core.ParallelVersions.SSR2.NormalizationParameters;
import ec.ssr.core.Utils;
import ec.ssr.functions.Function;
import ec.ssr.problems.Regression;
import ec.ssr.problems.RegressionRST;
import ec.ssr.problems.fitness.FitnessInterface;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Version with range normalization (student-t) and internal crossover
 * The training dataset is redefined at each generation by random sampling technique
 * @author luiz
 */
public class SSR9 extends SSR4{
    public SSR9(Dataset trainingSet,
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
        // The original expected output
        double output[] = Utils.getDatasetOutputs(trainingSet);
        int currentIteration = 0;
        while(!canStop){
            System.out.println("\nIteration: " + (currentIteration+1));
            mainState.startFresh();
            
            double normalizedOutput[] = Arrays.copyOf(output, output.length);
            NormalizationParameters normParameters = normalizeData(normalizedOutput);
            
            // Load new inputs on the problem Object
            ((Regression)mainState.evaluator.p_problem).setDataset(trainingSet);
            ((Regression)mainState.evaluator.p_problem).setHitLevel(hitLevel);
            ((Regression)mainState.evaluator.p_problem).setOutput(normalizedOutput);
            
            evolve();   
            
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
                output = getNewOutput(trainingSet, normalizedOutput, tr);
            }
                        
            stats.updateOnIteration(solution, normalizedOutput);
            
            currentIteration++;
            mainState.output.close();
        }
    }
    
    protected void evolve(){
        long initTime = System.currentTimeMillis();
        int result = EvolutionState.R_NOTDONE;
        
        boolean canResample = true;
        
        // Generations
        while(result == EvolutionState.R_NOTDONE ){
            canResample = mainState.generation > 0 && mainState.generation < mainState.numGenerations - 1 ? true : false;
            ((RegressionRST)(mainState.evaluator.p_problem)).resample(mainState, canResample);
            
            result = mainState.evolve();
//            if(result == EvolutionState.R_SUCCESS) result = EvolutionState.R_NOTDONE;
            // Store the best fitness of generation
            stats.updateBestOfGeneration(mainState);
        }    

        System.out.println("Tempo: " + ((System.currentTimeMillis() - initTime)/1000.0) + " seg.");
    }
}
