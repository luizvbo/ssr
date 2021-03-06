/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ssr.handlers.statistics;

import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.simple.SimpleStatistics;
import ec.ssr.core.Dataset;
import ec.ssr.core.Instance;
import ec.ssr.core.Utils;
import ec.ssr.functions.Div;
import ec.ssr.functions.Function;
import ec.ssr.problems.fitness.FitnessInterface;
import java.util.ArrayList;

/**
 *
 * @author luiz
 */
public class ExecutionStatistics {
    public static final int TRAIN = 0;
    public static final int TEST = 1;
    public static final int PRECISION = 5;
    
    private final double trainOutput[][];
    private final double testOutput[][];
    
    private final Dataset trainingSet;
    private final Dataset testSet;
        
    ArrayList<Double> generationBestFitness;
    
    private double trainRMSEperIteration[];
    private double testRMSEperIteration[];
    private int numZeroDiv[];
    private int solutionSize[];
    private double pontualError[][];
    private double bestOfGenError[][];
    private double outputVectors[][];
    private StringBuilder iterativeSolutions;
    
    private int currentIteration;
    private long executionTime;
    
    public ExecutionStatistics(Dataset trainingSet, Dataset testSet, int numIterations) {  
        trainOutput = new double[trainingSet.size()][];
        testOutput = new double[testSet.size()][];
        trainRMSEperIteration = new double[numIterations];
        outputVectors = new double[numIterations][];
        testRMSEperIteration = new double[numIterations];
        solutionSize = new int[numIterations];
        numZeroDiv = new int[numIterations];
        pontualError = new double[numIterations][];
        bestOfGenError = new double[numIterations][];
        generationBestFitness = new ArrayList<Double>();
        iterativeSolutions = new StringBuilder();
        this.trainingSet = trainingSet;
        this.testSet = testSet;
        currentIteration = 0;
    }
    
    private void updateIterationRMSE(Function solution){
        trainRMSEperIteration[currentIteration] = Utils.getRMSE(solution, trainingSet);
        Div.numZeroDiv = 0;
        testRMSEperIteration[currentIteration] = Utils.getRMSE(solution, testSet);
        numZeroDiv[currentIteration] = Div.numZeroDiv;
    }
    
    public void updateBestOfGeneration(final EvolutionState state){
        generationBestFitness.add(getGenerationBestFitness(state));
        
        // Só para testes
        GPIndividual gp = (GPIndividual)((SimpleStatistics)state.statistics).getBestSoFar()[0];
    }
    
    private void finishIteration(){
        bestOfGenError[currentIteration] = Utils.doubleListToArray(generationBestFitness);
        generationBestFitness.clear();
        currentIteration++;
    }
        
    private void updateSolutionSize(Function solution) {
        this.solutionSize[currentIteration] = solution.getNumNodes();
    }

    public String getSolutionSizeAsString() {
        String sep = "";
        StringBuilder output = new StringBuilder();
        for(int i = 0; i < solutionSize.length; i++){
            output.append(sep + solutionSize[i]);
            sep = ",";
        }
        return output.toString();
    }
    
    private void updatePontualError(Function f, double[] output){
        // numIterations x numInstances
        pontualError[currentIteration] = getIndividualError(f, trainingSet, output);
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
        return ((FitnessInterface)ind.fitness).getError();
    }
    
    /**
     * Calculates the difference between the output of a function and the desired output
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
     * Tests a solution with a training and a testSolution set, storing the results.
     * The results are stored in a matrix where each line corresponds to an instance
     * and each instance is composed by the inputs, the target output and solution
     * output.
     * @param solution Solution being teste
     */
    public void testSolution(Function solution) {
        int index = 0;
        for(Instance instance : trainingSet.data){
            double output = solution.eval(instance.input);
            trainOutput[index] = formatInstanceError(instance.input, instance.output, output);
            index++;
        }    
        index = 0;
        for(Instance instance : testSet.data){
            double output = solution.eval(instance.input);
            testOutput[index] = formatInstanceError(instance.input, instance.output, output);
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

    public double[][] getOutputsPerInstance(int type) {
        if(type == TEST)
            return testOutput;
        else
            return trainOutput;
    }

    public double[] getErrorPerIterarion(int type) {
        if(type == TEST)
            return testRMSEperIteration;
        else
            return trainRMSEperIteration;
    }

    public int[] getNumZeroDiv() {
        return numZeroDiv;
    }

    public double[][] getBestOfGenerationError() {
        return bestOfGenError;
    }

    public double[][] getPontualError() {
        return pontualError;
    }

    private void updateOutputVectors(double[] output) {
        outputVectors[currentIteration] = output;
    }

    public double[][] getOutputVectors() {
        return outputVectors;
    }

    public void updateOnIteration(Function solution) {
        updateIterationSolution(solution);
        updateIterationRMSE(solution);
        updateSolutionSize(solution);
        finishIteration();
    }
    
    public void updateOnIteration(Function solution, double[] output) {
        updateIterationSolution(solution);
        updateIterationRMSE(solution);
        updateSolutionSize(solution);
        updateOutputVectors(output);
        finishIteration();
    }

    private void updateIterationSolution(Function solution) {
        iterativeSolutions.append(solution.print() + "\n");
    }

    public String getIterativeSolution() {
        return iterativeSolutions.toString();
    }

    public ArrayList<Double> getGenerationBestFitness() {
        return generationBestFitness;
    }

    public void updateTime(long executionTime) {
        this.executionTime = executionTime;
    }
    
    public long getExecutionTime() {
        return executionTime;
    }
}
