/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ssr.core.ParallelVersions.SSR1;

import ec.EvolutionState;
import ec.gp.GPIndividual;
import ec.gp.koza.KozaFitness;
import ec.simple.SimpleStatistics;
import ec.ssr.core.Dataset;
import ec.ssr.core.Instance;
import ec.ssr.core.ParallelVersions.SSR;
import ec.ssr.core.Utils;
import ec.ssr.functions.Function;
import ec.ssr.problems.Regression;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Version with no normalization and internal crossover
 * @author luiz
 */
public class SSR1 extends SSR{
    protected Function currentSolution;
    
    public SSR1(Dataset trainingSet,
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
            
            // Load new inputs on the proble Object
            ((Regression)mainState.evaluator.p_problem).setDataset(trainingSet);
            ((Regression)mainState.evaluator.p_problem).setHitLevel(hitLevel);
            ((Regression)mainState.evaluator.p_problem).setOutput(output);
//            double[] lastOutput = output;
            
            evolve();
            
//            int result = EvolutionState.R_NOTDONE;
//            long initTime = System.currentTimeMillis();
//            
//            // Generations
//            while(result == EvolutionState.R_NOTDONE ){
//                result = mainState.evolve();
//                // Store the best fitness of generation
//                stats.updateBestOfGeneration(mainState);
//            }    
//            
//            System.out.println("Tempo: " + ((System.currentTimeMillis() - initTime)/1000) + " seg.");
            
            GPIndividual bestSoFar = (GPIndividual)((SimpleStatistics)mainState.statistics).getBestSoFar()[0];
            KozaFitness fitness = (KozaFitness)bestSoFar.fitness;
            Function bestFunction = (Function)bestSoFar.trees[0].child;

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
            
//            stats.updatePontualError(bestFunction, lastOutput);
//            stats.updateOutputVectors(lastOutput);
//            stats.updateIterativeErrors(solution);
//            stats.updateSolutionSize(solution);
//
//            stats.finishIteration();
            currentIteration++;
            mainState.output.close();
        }
    }
    
    protected void addFunctionToSolution(Function generatedFunction, double tr) {
        if(solution == null){
            solution = new SolutionSSR1(generatedFunction, tr);
            currentSolution = solution;
        }
        else{
            ((SolutionSSR1)currentSolution).setT2(new SolutionSSR1(generatedFunction, tr));
            currentSolution = ((SolutionSSR1)currentSolution).getT2();
        }
    }
    
    protected void addLastFunctionToSolution(Function generatedFunction){
        if(solution == null){
            solution = new SolutionSSR1(generatedFunction, 1);
        }
        else{
            ((SolutionSSR1)currentSolution).setT2(generatedFunction);
        }
    }
    
    protected double[] getNewOutput(Dataset dataset, double[] oldOutput, double tr){
        double[] newOutput = new double[dataset.size()];
        for(int i = 0; i < dataset.size(); i++){
            Instance instance = dataset.get(i);
            double output = ((SolutionSSR1)currentSolution).getT1().eval(instance.input);
            newOutput[i] = (oldOutput[i] - tr*output)/(1-tr);
        }
        return newOutput;
    }
}
