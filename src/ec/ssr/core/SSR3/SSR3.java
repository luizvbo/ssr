/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ssr.core.SSR3;

import ec.EvolutionState;
import ec.gp.GPIndividual;
import ec.gp.koza.KozaFitness;
import ec.simple.SimpleStatistics;
import ec.ssr.core.Dataset;
import ec.ssr.core.SSR2.NormalizedFunction;
import ec.ssr.core.SSR2.NormalizationParameters;
import ec.ssr.core.Utils;
import ec.ssr.functions.Function;
import ec.ssr.handlers.FileHandler;
import ec.ssr.problems.Regression;
import java.util.ArrayList;
import java.util.Arrays;




/**
 * Version with Student's t-statistic normalization and internal crossover
 * New outputs are calculated over the non-normalized outputs.   
 * @author luiz
 */
public class SSR3 extends ec.ssr.core.SSR2.SSR2{
    public SSR3(String[] args){
        super(args);
    }

    @Override
    public void execute(){
        try {            
            final int totalNumberGPExecs = numExecutions*maxIterations;
            StringBuilder s_solution = new StringBuilder();
            ArrayList<Double> bestFitnessList = new ArrayList<Double>();
            // Matrix to store best fitness during iterations
            double[][][] bestFitnessMatrix = new double[numExecutions][maxIterations][];
            // Stores the distribution of outputs (norm. and raw)
            ArrayList<double[]> rawOutputHistory[] = new ArrayList[numExecutions];
            ArrayList<double[]> normOutputHistory[] = new ArrayList[numExecutions];
            
            
            // Run the algorithm for a defined number of repetitions
            for(int execution = 0; execution < numExecutions; execution++){
                Dataset[] data = dataProducer.getTrainintTestData();
                // Solution reset
                solution = null;
                currentSolution = null;
                // Array that stores the error of each iteration
                double[] iterativeTrainingErrors = new double[maxIterations];
                double[] iterativeTestErrors = new double[maxIterations];
                double [][] pontualError = new double[maxIterations][data[0].size()];
                int currentIteration = 0;
                boolean canStop = false;
                // The original expected output
                double output[] = getFirstRunOutput(data[0]);
                // Initializing the history lists
                rawOutputHistory[execution] = new ArrayList<double[]>();
                normOutputHistory[execution] = new ArrayList<double[]>();
                
                while(!canStop){
                    System.out.println("\n======= Execution " + (execution*maxIterations + currentIteration+1)  + " of " + totalNumberGPExecs + " =======");
                    mainState.startFresh();
                    double normalizedOutput[] = Arrays.copyOf(output, output.length);
                    NormalizationParameters normParameters = normalizeData(normalizedOutput);
                    
                    rawOutputHistory[execution].add(output);
                    normOutputHistory[execution].add(normalizedOutput);

                    // Load new inputs on the proble Object
                    ((Regression)mainState.evaluator.p_problem).setDataset(data[0]);
                    ((Regression)mainState.evaluator.p_problem).setHitLevel(hitLevel);
                    ((Regression)mainState.evaluator.p_problem).setOutput(normalizedOutput);
                    int result = EvolutionState.R_NOTDONE;
                    
                    bestFitnessList.clear();
                    
                    // Generations
                    while(result == EvolutionState.R_NOTDONE ){
                        result = mainState.evolve();
                        // Store the best fitness of generation
                        double currentBestFitness = getGenerationBestFitness(mainState);                     
                        bestFitnessList.add(currentBestFitness);                        
                    }                                                        
                    GPIndividual bestSoFar = (GPIndividual)((SimpleStatistics)mainState.statistics).getBestSoFar()[0];
                    KozaFitness fitness = (KozaFitness)bestSoFar.fitness;
                    Function f = new NormalizedFunction((Function)bestSoFar.trees[0].child, normParameters);
                    
                    if(currentIteration == maxIterations - 1 || fitness.hits == data[0].size()){
                        addLastFunctionToSolution(f);
                        canStop = true;
                    }
                    else{
                        double tr = mainState.random[0].nextDouble();
                        addFunctionToSolution(f, tr);
//                        output = getNewOutput(data[0], output, tr);
                        output = getNewOutput(data[0], output, tr);
                    }
                    
                    pontualError[currentIteration] = Utils.sumDoubleArray(
                                                        getIndividualError(f, data[0], output), 
                                                        pontualError[currentIteration]);
                    
                    iterativeTrainingErrors[currentIteration] = getRMSE(solution, data[0]);
                    iterativeTestErrors[currentIteration] = getRMSE(solution, data[1]);
                    
                    bestFitnessMatrix[execution][currentIteration] = Utils.doubleListToArray(bestFitnessList);
                    
                    System.out.println(iterativeTrainingErrors[currentIteration]);
                    
                    currentIteration++;
                    mainState.output.close();
                }
                stats.updateIterativeErrors(iterativeTrainingErrors, iterativeTestErrors);
                stats.updateSolutionSize(solution.getNumNodes());
                if(execution == numExecutions-1){
                    
                    stats.updatePontualError(pontualError);
                }
                // Test
                solution.test(data[0], data[1], stats);
                stats.finishExecution();
                s_solution.append("Iteration ").append(execution+1).append("\n").append(solution.print()).append("\n\n");
            }
            stats.updateBestOfGenErrors(bestFitnessMatrix);
            // Write statistics on a file
            FileHandler.writeResults(outputPath, outputPrefix, stats, hitLevel);
            FileHandler.writeSolution(outputPath, outputPrefix, s_solution.toString());
            
            FileHandler.writeOutputDist(outputPath, outputPrefix, rawOutputHistory, normOutputHistory);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void main(String args[]){
        SSR3 ps = new SSR3(args);
        ps.execute();
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
        
        if(Double.isInfinite(std)){
            int x= 0;
        }
        
        for(int i = 0; i < output.length; i++){
            output[i]=(output[i]-mean)/std;
        }
        return new NormalizationParameters(mean, std);
    }
}
