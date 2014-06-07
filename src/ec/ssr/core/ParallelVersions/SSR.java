/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ssr.core.ParallelVersions;

import ec.EvolutionState;
import ec.Evolve;
import ec.Individual;
import ec.gp.koza.KozaFitness;
import ec.ssr.core.Dataset;
import ec.ssr.core.Instance;
import ec.ssr.functions.Function;
import ec.ssr.handlers.statistics.ExecutionStatistics;
import ec.util.Output;
import ec.util.ParameterDatabase;
import java.io.File;

/**
 *
 * @author luiz
 */
public abstract class SSR extends Thread{
    protected Function solution;
    
    protected Dataset trainingSet;
    protected Dataset testSet;
    protected String outputPath;
    protected String outputPrefix;
    protected int maxIterations;
    protected double hitLevel;
    protected int numExecutions;
    
    protected ExecutionStatistics stats;
    protected EvolutionState mainState;

    public SSR(Dataset trainingSet, 
               Dataset testSet, 
               String outputPath, 
               String outputPrefix, 
               int numIterations, 
               int numExecutions,
               double hitLevel, 
               String parameterFilePath) throws Exception{
        this.trainingSet = trainingSet;
        this.testSet = testSet;
        this.outputPath = outputPath;
        this.outputPrefix = outputPrefix;
        this.maxIterations = numIterations;
        this.hitLevel = hitLevel;
        this.numExecutions = numExecutions;
        
        Output mainOutput = Evolve.buildOutput();
        mainOutput.getLog(0).appendOnRestart = true;
        
        File parameterFile = new File(parameterFilePath);
        ParameterDatabase dbase = new ParameterDatabase(parameterFile);
        mainState = Evolve.initialize(dbase, 0, mainOutput);
        
        stats = new ExecutionStatistics(trainingSet, testSet, numIterations);
    }
    
    @Override
    public void run() {
        runAlgorithm();
        stats.testSolution(solution);
    }
    
    public abstract void runAlgorithm();

    
    
    protected final double[] getFirstRunOutput(Dataset dataset) {
        double[] output = new double[dataset.size()];
        int i = 0;
        for(Instance instance : dataset.data){
            output[i++] = instance.output;
        }
        return output;
    }
    
    protected final double[] getIndividualError(Function solution, Dataset dataset) {
        return getIndividualError(solution, dataset, null);
    }
    
    /**
     * Calculates the difference between the output of a funtion and the desired output
     * for a given dataset (absolute error).
     * @param solution
     * @param dataset
     * @param output
     * @return 
     */
    protected double[] getIndividualError(Function solution, Dataset dataset, double[] output) {
        double individualError[] = new double[dataset.size()];
        for(int i = 0; i < dataset.size(); i++){
            Instance instance = dataset.data.get(i);
            double evaluated = solution.eval(instance.input);
            if(output == null){
                individualError[i] = Math.abs(evaluated - instance.output);
            }
            else{
                individualError[i] = Math.abs(evaluated - output[i]);
            }
        }
        return individualError;
    }
    
    protected final double getGenerationBestFitness(final EvolutionState state){
        Individual ind = state.population.subpops[0].individuals[0];
        for(int y=1;y<state.population.subpops[0].individuals.length;y++){
            if (state.population.subpops[0].individuals[y].fitness.betterThan(ind.fitness))
                ind = state.population.subpops[0].individuals[y];
        }
        return ((KozaFitness)ind.fitness).standardizedFitness();
    }

    public ExecutionStatistics getStatistics() {
        return stats;
    }

    public Function getSolution() {
        return solution;
    }
}
