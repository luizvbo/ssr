/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ssr.core.ParallelVersions;

import ec.EvolutionState;
import ec.Evolve;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.koza.KozaFitness;
import ec.simple.SimpleStatistics;
import ec.ssr.core.Dataset;
import ec.ssr.core.Instance;
import ec.ssr.core.Utils;
import ec.ssr.functions.Function;
import ec.ssr.handlers.statistics.ExecutionStatistics;
import ec.ssr.problems.RegressionResampling;
import ec.util.Output;
import ec.util.Parameter;
import ec.util.ParameterDatabase;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author luiz
 */
public abstract class SSR extends Thread{
    protected Function solution;
    
    protected Dataset trainingSet;
    protected Dataset validationSet;
    protected Dataset testSet;
    protected String outputPath;
    protected String outputPrefix;
    protected int maxIterations;
    protected double hitLevel;
    protected int numExecutions;
    protected long seed;
    
    protected ExecutionStatistics stats;
    protected EvolutionState mainState;

    public SSR(Dataset trainingSet, 
               Dataset validationSet,
               Dataset testSet, 
               String outputPath, 
               String outputPrefix, 
               int numIterations, 
               int numExecutions,
               long seed,
               double hitLevel, 
               String parameterFilePath,
               ArrayList inputParameters) throws Exception{
        this.trainingSet = trainingSet;
        this.validationSet = validationSet;
        this.testSet = testSet;
        this.outputPath = outputPath;
        this.outputPrefix = outputPrefix;
        this.maxIterations = numIterations;
        this.hitLevel = hitLevel;
        this.numExecutions = numExecutions;
        this.seed = seed;
        
        Output mainOutput = Evolve.buildOutput();
        mainOutput.getLog(0).appendOnRestart = true;
        
        File parameterFile = new File(parameterFilePath);
        String[] args = getParameterArgsFromArray(inputParameters);
        ParameterDatabase dbase = new ParameterDatabase(parameterFile, args);
        dbase.set(new Parameter("seed.0"), (int)this.seed + "");
        mainState = Evolve.initialize(dbase, 0, mainOutput);
                
        stats = new ExecutionStatistics(trainingSet, testSet, numIterations);
    }
    
    private String[] getParameterArgsFromArray(ArrayList<String> inputParameters){
        if(inputParameters == null) return new String[0];
        String[] args = new String[inputParameters.size() * 2];
        int i = 0;
        for(String parameter : inputParameters){
            args[i++] = "-p";
            args[i++] = parameter;
        }
        return args;
    }
    
    @Override
    public void run() {
        runAlgorithm();
        stats.testSolution(solution);
    }
    
    public abstract void runAlgorithm();
    
    
    
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

    public long getSeed() {
        return seed;
    }
    
    protected void evolve(){
        long initTime = System.currentTimeMillis();
        int result = EvolutionState.R_NOTDONE;
        
//        double alpha = 51;
//        int stripSize = 5;
//        double bestValError = Double.MAX_VALUE;
        
        // Generations
        while(result == EvolutionState.R_NOTDONE ){            
            result = mainState.evolve();
//            if(result == EvolutionState.R_SUCCESS) result = EvolutionState.R_NOTDONE;
            // Store the best fitness of generation
            stats.updateBestOfGeneration(mainState);
            
            // Early stopping test
//            if(validationSet != null && mainState.generation % stripSize == 0){
//                Function bestOfGeneration = (Function)((GPIndividual)((SimpleStatistics)mainState.statistics).getBestSoFar()[0]).trees[0].child;
//                double currentValError = Utils.getRMSE(bestOfGeneration, validationSet);
//                if(currentValError < bestValError) bestValError = currentValError;
//                double gl = (currentValError/bestValError)-1;
//                ArrayList<Double> bestFitness = stats.getGenerationBestFitness();
//                double e_min = Double.MAX_VALUE;
//                double e_sum = 0;
//                for(int i = mainState.generation - stripSize; i < mainState.generation; i++){
//                    double e = bestFitness.get(i);
//                    e_sum += e;
//                    if(e < e_min) e_min = e;
//                }
//                double pk = 10*((e_sum/(stripSize*e_min))-1);
//                if(gl/pk > alpha) result = EvolutionState.R_FAILURE;
//            }
        }    

        System.out.println("Tempo: " + ((System.currentTimeMillis() - initTime)/1000.0) + " seg.");
    }
}
