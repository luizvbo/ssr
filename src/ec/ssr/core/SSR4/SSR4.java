/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ssr.core.SSR4;

import ec.EvolutionState;
import ec.gp.GPIndividual;
import ec.gp.koza.KozaFitness;
import ec.simple.SimpleStatistics;
import ec.ssr.core.Dataset;
import ec.ssr.core.Instance;
import ec.ssr.core.SSR2.NormalizationParameters;
import ec.ssr.core.SSR2.NormalizedFunction;
import ec.ssr.core.Utils;
import ec.ssr.functions.Function;
import ec.ssr.handlers.FileHandler;
import ec.ssr.problems.Regression;
import java.util.ArrayList;
import java.util.Arrays;




/**
 * Version with range normalization (student-t) and internal crossover
 * New outputs are calculated over the normalized output. 
 * @author luiz
 */
public class SSR4 extends ec.ssr.core.SSR3.SSR3{
    public SSR4(String[] args){
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
            // Stores the ids of the training instances
            StringBuilder trainingIds = new StringBuilder("Each line a different execution\n");
            
            // Run the algorithm for a defined number of repetitions
            for(int execution = 0; execution < numExecutions; execution++){
                Dataset[] data = dataProducer.getTrainintTestData();
                trainingIds.append(data[0].getStringIDs() + "\n");
                
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
                    
                    if(currentIteration == maxIterations - 1 || fitness.hits == data[0].size()){
                        addLastFunctionToSolution((Function)bestSoFar.trees[0].child, normParameters);
                        canStop = true;
                    }
                    else{
                        double tr = mainState.random[0].nextDouble();
                        addFunctionToSolution((Function)bestSoFar.trees[0].child, tr, normParameters);
//                        output = getNewOutput(data[0], output, tr);
                        output = getNewOutput(data[0], normalizedOutput, tr);
                    }
                    
                    pontualError[currentIteration] = Utils.sumDoubleArray(
                                                        getIndividualError((Function)bestSoFar.trees[0].child, data[0], output), 
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
            FileHandler.writeTrainingIds(outputPath, outputPrefix, trainingIds.toString());
            
            FileHandler.writeOutputDist(outputPath, outputPrefix, rawOutputHistory, normOutputHistory);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    protected void addFunctionToSolution(Function generatedFunction, double tr, NormalizationParameters parameters) {
        if(solution == null){
            solution = Solution.createSolution(generatedFunction, tr, parameters);
            currentSolution = solution;
        }
        else{
            // Add a new level of normalization
            currentSolution.setT2(Solution.createSolution(generatedFunction, tr, parameters));
            currentSolution = (Solution)currentSolution.getT2();
        }
    }
    
    protected void addLastFunctionToSolution(Function lastFunction, NormalizationParameters parameters){
        if(solution == null){
            solution = Solution.createSolution(lastFunction, parameters);
        }
        else{
            // Add a new level of normalization
            Function normalizedT2 = new NormalizedFunction(lastFunction, parameters);
            currentSolution.setT2(normalizedT2);
        }
    }
    
    @Override
    protected double[] getNewOutput(Dataset dataset, double[] oldOutput, double tr){
        double[] newOutput = new double[dataset.size()];
        for(int i = 0; i < dataset.size(); i++){
            Instance instance = dataset.get(i);
            Function f = currentSolution.getT1();
            double output = 0;
            if(f instanceof NormalizedFunction)
                output = ((NormalizedFunction)f).unormalizedEval(instance.input);
            else 
                output = f.eval(instance.input);
            newOutput[i] = (oldOutput[i] - tr*output)/(1-tr);
        }   
        return newOutput;
    }
    
    public static void main(String args[]){
        SSR4 ps = new SSR4(args);
        ps.execute();
    }
}
