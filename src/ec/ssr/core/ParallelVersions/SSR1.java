/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ssr.core.ParallelVersions;

import ec.EvolutionState;
import ec.gp.GPIndividual;
import ec.gp.koza.KozaFitness;
import ec.simple.SimpleStatistics;
import ec.ssr.core.Dataset;
import ec.ssr.core.SSR;
import ec.ssr.core.SSR1.Solution;
import ec.ssr.functions.Function;
import ec.ssr.problems.Regression;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author luiz
 */
public class SSR1 extends SSR{

    public SSR1(Dataset trainingSet, 
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
        int currentIteration = 0;
        while(!canStop){
            mainState.startFresh();
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
            Function bestFunction = (Function)bestSoFar.trees[0].child;

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
    
    protected void addFunctionToSolution(Function generatedFunction, double tr) {
        if(solution == null){
            solution = new Solution(generatedFunction, tr);
            currentSolution = solution;
        }
        else{
            currentSolution.setT2(new Solution(generatedFunction, tr));
            currentSolution = (Solution)currentSolution.getT2();
        }
    }
    
    protected void addLastFunctionToSolution(Function generatedFunction){
        if(solution == null){
            solution = new Solution(generatedFunction, 1);
        }
        else{
            currentSolution.setT2(generatedFunction);
        }
    }
}
