/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ssr.handlers.statistics;

import ec.EvolutionState;
import ec.Individual;
import ec.gp.koza.KozaFitness;
import ec.ssr.core.Dataset;
import ec.ssr.core.Instance;
import ec.ssr.core.SSR1.Solution;
import ec.ssr.core.Utils;
import ec.ssr.functions.Function;
import java.util.ArrayList;

/**
 *
 * @author luiz
 */
public class ExecutionStatistics {
    public static final int TRAIN = 0;
    public static final int TEST = 1;
    public static final int PRECISION = 5;
    
    private final double trainError[][];
    private final double testError[][];
    
    private final Dataset trainingSet;
    private final Dataset testSet;
        
    ArrayList<Double> generationBestFitness;
    
    private double iterativeTrainingError[];
    private double iterativeTestError[];
    private int solutionSize;
    private double pontualError[][];
    private double bestOfGenError[][];
    private double outputVectors[][];
    
    private int currentIteration;
    
    public ExecutionStatistics(Dataset trainingSet, Dataset testSet, int numIterations) {  
        trainError = new double[trainingSet.size()][];
        testError = new double[testSet.size()][];
        iterativeTrainingError = new double[numIterations];
        outputVectors = new double[numIterations][];
        iterativeTestError = new double[numIterations];
        pontualError = new double[numIterations][];
        bestOfGenError = new double[numIterations][];
        generationBestFitness = new ArrayList<Double>();
        this.trainingSet = trainingSet;
        this.testSet = testSet;
        currentIteration = 0;
    }
    
    public void updateIterativeErrors(Solution solution){
        iterativeTrainingError[currentIteration] = getRMSE(solution, trainingSet);
        iterativeTestError[currentIteration] = getRMSE(solution, testSet);
    }
    
    public void updateBestOfGeneration(final EvolutionState state){
        generationBestFitness.add(getGenerationBestFitness(state));
    }
    
    public void finishIteration(){
        bestOfGenError[currentIteration] = Utils.doubleListToArray(generationBestFitness);
        generationBestFitness.clear();
        currentIteration++;
    }
    
    public void setSolutionSize(int solutionSize){
        this.solutionSize = solutionSize;
    }

    public int getSolutionSize() {
        return solutionSize;
    }
    
    public void updatePontualError(Function f, double[] output){
        // numIterations x numInstances
        pontualError[currentIteration] = getIndividualError(f, trainingSet, output);
    }
    
    /**
     * Calculate the MSE, given a solution object and a datase
     * @param solution Solution objetc
     * @param dataset Dataset used to calculate the error
     * @return Total error
     */
    protected final double getRMSE(Solution solution, Dataset dataset) {
        double totalError = 0;
        for(Instance instance : dataset.data){
            double evaluated = solution.eval(instance.input);
            double error = evaluated - instance.output;
            totalError += error * error;
        }
        return Math.sqrt(totalError/dataset.data.size());
    }
    
    /**
     * Returns the RMSE of the best individual of the current generation
     * @param state Evolution state from ECJ
     * @return The best fitness from this generation
     */
    protected final double getGenerationBestFitness(final EvolutionState state){
        Individual ind = state.population.subpops[0].individuals[0];
        for(int y=1;y<state.population.subpops[0].individuals.length;y++){
            if (state.population.subpops[0].individuals[y].fitness.betterThan(ind.fitness))
                ind = state.population.subpops[0].individuals[y];
        }
        return ((KozaFitness)ind.fitness).standardizedFitness();
    }
    
    /**
     * Calculates the difference between the output of a funtion and the desired output
     * for a given dataset (absolute error).
     * @param solution Input solution 
     * @param dataset Input data 
     * @param output Desired output
     * @return Absolute error for each instance from the dataset
     */
    protected double[] getIndividualError(Function solution, Dataset dataset, double[] output) {
        double individualError[] = new double[dataset.size()];
        for(int i = 0; i < dataset.size(); i++){
            Instance instance = dataset.data.get(i);
            double evaluated = solution.eval(instance.input);
            individualError[i] = Math.abs(evaluated - output[i]);
        }
        return individualError;
    }
    
    /**
     * Tests a solution with a training and a testSolution set, storing the results
     * @param solution Solution being teste
     */
    public void testSolution(Solution solution) {
        int index = 0;
        for(Instance instance : trainingSet.data){
            double output = solution.eval(instance.input);
            trainError[index] = formatInstanceError(instance.input, instance.output, output);
            index++;
        }    
        index = 0;
        for(Instance instance : testSet.data){
            double output = solution.eval(instance.input);
            testError[index] = formatInstanceError(instance.input, instance.output, output);
            index++;
        }
    }
    
    /**
     * Formats an output as a array with inputs, desired output and evaluated output
     * @param inputs Inputs
     * @param target Desired output
     * @param output Evaluated output
     * @return An array with this data
     */
    public double[] formatInstanceError(double inputs[], double target, double output){
        double testResult[] = new double[inputs.length+2];
        for(int i = 0; i < inputs.length; i++){
            testResult[i] = inputs[i];
        }
        testResult[inputs.length] = target;
        testResult[inputs.length+1] = output;
        return testResult;
    }

    public double[][] getErrorPerInstance(int type) {
        if(type == TEST)
            return testError;
        else
            return trainError;
    }

    public double[] getErrorPerIterarion(int type) {
        if(type == TEST)
            return iterativeTestError;
        else
            return iterativeTrainingError;
    }

    public double[][] getBestOfGenerationError() {
        return bestOfGenError;
    }

    public double[][] getPontualError() {
        return pontualError;
    }

    public void updateOutputVectors(double[] output) {
        outputVectors[currentIteration] = output;
    }

    public double[][] getOutputVectors() {
        return outputVectors;
    }
}
