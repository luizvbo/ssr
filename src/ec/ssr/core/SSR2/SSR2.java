/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ssr.core.SSR2;

import ec.EvolutionState;
import ec.gp.GPIndividual;
import ec.gp.koza.KozaFitness;
import ec.simple.SimpleStatistics;
import ec.ssr.core.Dataset;
import ec.ssr.core.SSR2.Solution;
import ec.ssr.core.Utils;
import ec.ssr.functions.Function;
import ec.ssr.handlers.FileHandler;
import ec.ssr.problems.Regression;
import java.util.ArrayList;
import java.util.Arrays;




/**
 * Version with range normalization ([0,1]) and internal crossover
 * @author luiz
 */
public class SSR2 extends ec.ssr.core.SSR1.SSR1{      
    
    
    public SSR2(String[] args){
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
                while(!canStop){
                    System.out.println("\n======= Execution " + (execution*maxIterations + currentIteration+1)  + " of " + totalNumberGPExecs + " =======");
                    mainState.startFresh();
                    double normalizedOutput[] = Arrays.copyOf(output, output.length);
//                    double lBound = setLowerBound(normalizedOutput);
                    NormalizationParameters normParameters = normalizeData(normalizedOutput);
//                    NormalizedFunction f = normalizeData(normalizedOutput);

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
                        
                        if(currentBestFitness == 0){
                            int x = 1;
                        }
                        
                        bestFitnessList.add(currentBestFitness);                        
                    }                                                        
                    GPIndividual bestSoFar = (GPIndividual)((SimpleStatistics)mainState.statistics).getBestSoFar()[0];
                    KozaFitness fitness = (KozaFitness)bestSoFar.fitness;
                    NormalizedFunction f = new NormalizedFunction((Function)bestSoFar.trees[0].child, normParameters);
                    
                    if(currentIteration == maxIterations - 1 || fitness.hits == data[0].size()){
                        addLastFunctionToSolution(f);
                        canStop = true;
                    }
                    else{
//                        double tr = possibleR1[mainState.random[0].nextInt(possibleR1.length)];
                        double tr = mainState.random[0].nextDouble();
                        addFunctionToSolution(f, tr);
                        output = getNewOutput(data[0], output, tr);
                    }
                    
                    pontualError[currentIteration] = Utils.sumDoubleArray(
                                                        getIndividualError(f, data[0], output), 
                                                        pontualError[currentIteration]);
                    
                    iterativeTrainingErrors[currentIteration] = getRMSE(solution, data[0]);
                    iterativeTestErrors[currentIteration] = getRMSE(solution, data[1]);
                    
//                    if(execution == numExecutions-1){
                        bestFitnessMatrix[execution][currentIteration] = Utils.doubleListToArray(bestFitnessList);
//                    }
//                    double[] currentBestFitness = Utils.doubleListToArray(bestFitnessList);
//                    if(bestFitness[currentIteration] == null){
//                        bestFitness[currentIteration] = new double[currentBestFitness.length];
//                    }
//                    bestFitness[currentIteration] = Utils.sumDoubleArray(bestFitness[currentIteration], currentBestFitness);
                    
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void main(String args[]){
        SSR2 ps = new SSR2(args);
        ps.execute();
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
    
    protected void addLastFunctionToSolution(Function lastFunction){
        if(solution == null){
            solution = new Solution(lastFunction, 1);
        }
        else{
            currentSolution.setT2(lastFunction);
        }
    }

//    protected double[] getNewOutput(Dataset dataset, double[] oldOutput, double tr){
//        double[] newOutput = new double[dataset.size()];
//        for(int i = 0; i < dataset.size(); i++){
//            Instance instance = dataset.get(i);
//            double output = currentSolution.getT1().eval(instance.input);
//            newOutput[i] = (oldOutput[i] - tr*output)/(1-tr);
//        }
//        return newOutput;
//    }
    
//    protected double[] getFirstRunOutput(Dataset dataset) {
//        double[] output = new double[dataset.size()];
//        int i = 0;
//        for(Instance instance : dataset.data){
//            output[i++] = instance.output;
//        }
//        return output;
//    }

    /**
     * Calculate the total absolute error, given a solution object and a datase
     * @param solution Solution objetc
     * @param dataset Dataset used to calculate the error
     * @return Total error
     */
//    private double getRMSE(Solution solution, Dataset dataset) {
//        double totalError = 0;
//        for(Instance instance : dataset.data){
//            double evaluated = solution.eval(instance.input);
//            double error  = evaluated - instance.output;
//            totalError += error * error;
//        }
//        return totalError;
//    }
    
//    private double[] getIndividualError(Function solution, Dataset dataset) {
//        return getIndividualError(solution, dataset, null);
//    }
    
//    private double[] getIndividualError(Function solution, Dataset dataset, double[] output) {
//        double individualError[] = new double[dataset.size()];
//        for(int i = 0; i < dataset.size(); i++){
//            Instance instance = dataset.data.get(i);
//            double evaluated = solution.eval(instance.input);
//            if(output == null){
//                individualError[i] = Math.abs(evaluated - instance.output);
//            }
//            else{
//                individualError[i] = Math.abs(evaluated - output[i]);
//            }
//        }
//        return individualError;
//    }
    
//    private double getGenerationBestFitness(final EvolutionState state){
//        Individual ind = state.population.subpops[0].individuals[0];
//        for(int y=1;y<state.population.subpops[0].individuals.length;y++){
//            if (state.population.subpops[0].individuals[y].fitness.betterThan(ind.fitness))
//                ind = state.population.subpops[0].individuals[y];
//        }
//        return ((KozaFitness)ind.fitness).standardizedFitness();
//    }

    private double setLowerBound(double[] output) {
        double lBound = Double.MAX_VALUE;
        for(int i = 0; i < output.length; i++){
            if(output[i] < lBound){
                lBound = output[i];
            }
        }
        for(int i = 0; i < output.length; i++){
            output[i]-=lBound;
        }
        return lBound;
    }

    protected NormalizationParameters normalizeData(double[] output) {
        double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
        for(int i = 0; i < output.length; i++){
            if(output[i] < min){
                min = output[i];
            }
            if(output[i] > max){
                max = output[i];
            }
        }       
        double range = max-min;
        for(int i = 0; i < output.length; i++){
            output[i]=(output[i]-min)/range;
        }
        return new NormalizationParameters(min, range);
    }
}
