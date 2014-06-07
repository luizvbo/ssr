/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ssr.core.ParallelVersions.SSR6;

import ec.EvolutionState;
import ec.gp.GPIndividual;
import ec.gp.koza.KozaFitness;
import ec.simple.SimpleStatistics;
import ec.ssr.core.Dataset;
import ec.ssr.core.Instance;
import ec.ssr.core.ParallelVersions.SSR;
import ec.ssr.core.SSR1.Solution;
import ec.ssr.functions.Function;
import ec.ssr.problems.Regression;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

/**
 * In this version functions are concatenated externaly
 * @author luiz
 */
public class SSR6 extends SSR{

    public SSR6(Dataset trainingSet, 
               Dataset testSet, 
               String outputPath, 
               String outputPrefix, 
               int numIterations, 
               int numExecutions,
               double hitLevel, 
               String parameterFilePath) throws NullPointerException, FileNotFoundException, IOException, Exception {
        super(trainingSet, testSet, outputPath, outputPrefix, 
              numIterations, numExecutions, hitLevel, parameterFilePath);
    }
    
    @Override
    public void runAlgorithm() {
        boolean canStop = false;
        // The original expected output
        double output[] = getFirstRunOutput(trainingSet);
        double newOutput[] = Arrays.copyOf(output, output.length);
        int currentIteration = 0;
        while(!canStop){
            System.out.println("\nIteration: " + (currentIteration+1));
            mainState.startFresh();
            // Load new inputs on the proble Object
            ((Regression)mainState.evaluator.p_problem).setDataset(trainingSet);
            ((Regression)mainState.evaluator.p_problem).setHitLevel(hitLevel);
            ((Regression)mainState.evaluator.p_problem).setOutput(newOutput);
            int result = EvolutionState.R_NOTDONE;
            double[] lastOutput = output; 
            // Generations
            while(result == EvolutionState.R_NOTDONE ){
                result = mainState.evolve();
                // Store the best fitness of generation
                stats.updateBestOfGeneration(mainState);
            }                                                        
            GPIndividual bestSoFar = (GPIndividual)((SimpleStatistics)mainState.statistics).getBestSoFar()[0];
            KozaFitness fitness = (KozaFitness)bestSoFar.fitness;
            Function bestFunction = (Function)bestSoFar.trees[0].child;

            if(currentIteration == maxIterations - 1 || fitness.hits == trainingSet.size()){
                double tr = mainState.random[0].nextDouble();
                addLastFunctionToSolution(bestFunction, tr);
                canStop = true;
            }
            else{
                double tr = mainState.random[0].nextDouble();
                addFunctionToSolution(bestFunction, tr);
                newOutput = getNewOutput(trainingSet, output, tr);
            }

            stats.updatePontualError(bestFunction, lastOutput);
            stats.updateIterativeErrors(solution);

            stats.finishIteration();
            currentIteration++;
            mainState.output.close();
        }
    }
    
    protected void addFunctionToSolution(Function generatedFunction, double tr) {
        if(solution == null){
            solution = new Solution(generatedFunction, tr);
        }
        else{
            ((Solution)solution).setT2(generatedFunction);
            solution = new Solution(solution, tr);
        }
    }
    
    protected void addLastFunctionToSolution(Function generatedFunction, double tr){
        if(solution == null){
            solution = new Solution(generatedFunction, 1);
        }
        else{
            ((Solution)solution).setT2(generatedFunction);
        }
    }
    
    protected double[] getNewOutput(Dataset dataset, double[] oldOutput, double tr){
        double[] newOutput = new double[dataset.size()];
        for(int i = 0; i < dataset.size(); i++){
            Instance instance = dataset.get(i);
            double output = ((Solution)solution).getT1().eval(instance.input);
            newOutput[i] = (oldOutput[i] - tr*output)/(1-tr);
        }
        return newOutput;
    }
}
