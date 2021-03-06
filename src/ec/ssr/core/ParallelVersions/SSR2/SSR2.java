/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ssr.core.ParallelVersions.SSR2;

import ec.ssr.core.ParallelVersions.SSR1.SSR1;
import ec.EvolutionState;
import ec.gp.GPIndividual;
import ec.gp.koza.KozaFitness;
import ec.simple.SimpleStatistics;
import ec.ssr.core.Dataset;
import ec.ssr.core.Utils;
import ec.ssr.functions.Function;
import ec.ssr.problems.Regression;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Version with range normalization ([0,1]) and internal crossover
 * @author luiz
 */
public class SSR2 extends SSR1{
    public SSR2(Dataset trainingSet,
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
            
            // Load new inputs on the proble Object
            ((Regression)mainState.evaluator.p_problem).setDataset(trainingSet);
            ((Regression)mainState.evaluator.p_problem).setHitLevel(hitLevel);
            ((Regression)mainState.evaluator.p_problem).setOutput(output);
            int result = EvolutionState.R_NOTDONE;
            // Generations
            while(result == EvolutionState.R_NOTDONE ){
                result = mainState.evolve();
                // Store the best fitness of generation
                stats.updateBestOfGeneration(mainState);
            }                                                        
            GPIndividual bestSoFar = (GPIndividual)((SimpleStatistics)mainState.statistics).getBestSoFar()[0];
            KozaFitness fitness = (KozaFitness)bestSoFar.fitness;
            NormalizedFunction bestFunction =  new NormalizedFunction((Function)bestSoFar.trees[0].child, normParameters);

            if(currentIteration == maxIterations - 1 || fitness.hits == trainingSet.size() || fitness.isIdealFitness()){
                addLastFunctionToSolution(bestFunction);
                canStop = true;
            }
            else{
                double tr = mainState.random[0].nextDouble();
                addFunctionToSolution(bestFunction, tr);
                output = getNewOutput(trainingSet, output, tr);
            }

            stats.updateOnIteration(solution);
            
//            stats.updatePontualError(bestFunction, output);
//            stats.updateIterativeErrors(solution);
//
//            stats.finishIteration();
            currentIteration++;
            mainState.output.close();
        }
    }
    
    protected NormalizationParameters normalizeData(double[] output) {
        double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
        for(int i = 0; i < output.length; i++){
            if(output[i] < min){
                min = output[i];
            }
            if(output[i] > max){
                max = output[i];
            }
        }       
        double range = max-min;
        for(int i = 0; i < output.length; i++){
            output[i]=(output[i]-min)/range;
        }
        return new NormalizationParameters(min, range);
    }
}
