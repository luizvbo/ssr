/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ssr.core.ParallelVersions.SSR3;

import ec.ssr.core.ParallelVersions.SSR2.SSR2;
import ec.EvolutionState;
import ec.gp.GPIndividual;
import ec.gp.koza.KozaFitness;
import ec.simple.SimpleStatistics;
import ec.ssr.core.Dataset;
import ec.ssr.core.SSR2.NormalizationParameters;
import ec.ssr.core.SSR2.NormalizedFunction;
import ec.ssr.functions.Function;
import ec.ssr.problems.Regression;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author luiz
 */
public class SSR3 extends SSR2{
    public SSR3(Dataset trainingSet, Dataset testSet, String outputPath, String outputPrefix, int numIterations, int numExecutions, double hitLevel, String parameterFilePath) throws NullPointerException, FileNotFoundException, IOException, Exception {
        super(trainingSet, testSet, outputPath, outputPrefix, numIterations, numExecutions, hitLevel, parameterFilePath);
    }
    
    @Override
    public void runAlgorithm() {
        boolean canStop = false;
        // The original expected output
        double output[] = getFirstRunOutput(trainingSet);
        int currentIteration = 0;
        while(!canStop){
            System.out.println("\nIteration: " + (currentIteration+1));
            mainState.startFresh();
            double normalizedOutput[] = Arrays.copyOf(output, output.length);
            NormalizationParameters normParameters = normalizeData(normalizedOutput);
            // Load new inputs on the proble Object
            ((Regression)mainState.evaluator.p_problem).setDataset(trainingSet);
            ((Regression)mainState.evaluator.p_problem).setHitLevel(hitLevel);
            ((Regression)mainState.evaluator.p_problem).setOutput(normalizedOutput);
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

            if(currentIteration == maxIterations - 1 || fitness.hits == trainingSet.size()){
                addLastFunctionToSolution(bestFunction);
                canStop = true;
            }
            else{
                double tr = mainState.random[0].nextDouble();
                addFunctionToSolution(bestFunction, tr);
                output = getNewOutput(trainingSet, output, tr);
            }

            stats.updatePontualError(bestFunction, output);
            stats.updateIterativeErrors(solution);

            stats.finishIteration();
            currentIteration++;
            mainState.output.close();
        }
    }
    
    @Override
    protected NormalizationParameters normalizeData(double[] output) {
        // Calculates the mean
        double mean = 0;
        for(int i = 0; i < output.length; i++){
            mean += output[i];
        }       
        mean = mean/output.length;
        // Calculates the std. dev.
        double std = 0;
        for(int i = 0; i < output.length; i++){
            double aux = output[i] - mean;
            std += aux * aux;
        }
        std /= output.length-1;
        std = Math.sqrt(std);
        
        for(int i = 0; i < output.length; i++){
            output[i]=(output[i]-mean)/std;
        }
        return new NormalizationParameters(mean, std);
    }
}
