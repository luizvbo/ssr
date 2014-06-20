/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ssr.core.SSR1;

import ec.ssr.core.ParallelVersions.SSR1.Solution;
import ec.EvolutionState;
import ec.Evolve;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.koza.KozaFitness;
import ec.simple.SimpleStatistics;
import ec.ssr.core.Dataset;
import ec.ssr.core.Instance;
import ec.ssr.core.Utils;
import ec.util.Output;
import ec.util.ParameterDatabase;
import ec.ssr.functions.Function;
import ec.ssr.handlers.CrossvalidationHandler;
import ec.ssr.handlers.FileHandler;
import ec.ssr.handlers.HoldoutHandler;
import ec.ssr.handlers.StatisticsHandler;
import ec.ssr.handlers.DataProducer;
import ec.ssr.handlers.GesecException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import ec.ssr.problems.Regression;
import java.util.ArrayList;




/**
 * Version with no normalization and internal crossover
 * @author luiz
 */
public class SSR1{          
    protected Solution currentSolution;
    protected Solution solution;
    
    protected String parameterFilePath;
    protected String inputDataFilePath; 
    protected String outputPath = "";
    protected String outputPrefix = "";
    protected int maxIterations = 10;
    protected double hitLevel = 0.01;
    protected int numExecutions = 1;
    protected boolean shuffle = true;
    
    protected StatisticsHandler stats;
    protected EvolutionState mainState;
    protected DataProducer dataProducer;
    
    
    public SSR1(String[] args){
        initialize(args);
        stats = new StatisticsHandler(numExecutions);
    }
    
    /**
     * Reads the options form command line
     * @param args Command line parameters
     */
    protected void readInputs(String args[]) throws NumberFormatException, FileNotFoundException, Exception{
        
            // Reads the options from input
            if(FileHandler.getFlag("h", args)){
                showHelp();
                System.exit(0);
            }
            // Checks if there is a file path flag
            if(FileHandler.getOptPosition("i", args) == -1){
                throw new FileNotFoundException("There is no input file path. Select a valid one.");
            }
            String inputString = FileHandler.readOption("p", args);
            if(!inputString.equals("")){
                parameterFilePath = inputString;
            }
            inputString = FileHandler.readOption("i", args);
            if(!inputString.equals("")){
                inputDataFilePath = inputString;
            }
            inputString = FileHandler.readOption("o", args);
            if(!inputString.equals("")){
                outputPath = inputString;
            }
            inputString = FileHandler.readOption("a", args);
            if(!inputString.equals("")){
                outputPrefix = inputString;
            }
            inputString = FileHandler.readOption("g", args);
            if(!inputString.equals("")){
                maxIterations = Integer.parseInt(inputString);
            }
            inputString = FileHandler.readOption("n", args);
            if(!inputString.equals("")){
                numExecutions = Integer.parseInt(inputString);
            }
            inputString = FileHandler.readOption("e", args);
            if(!inputString.equals("")){
                hitLevel = Double.parseDouble(inputString);
            }
            inputString = FileHandler.readOption("k", args);
            if(!inputString.equals("")){
                dataProducer = new CrossvalidationHandler(Integer.parseInt(inputString));
            }
            inputString = FileHandler.readOption("b", args);
            if(!inputString.equals("")){
                dataProducer= new HoldoutHandler(Double.parseDouble(inputString));
            }
            // Uses unshuffled data
            if(FileHandler.getFlag("u", args)){
                shuffle = false;
            }
    }
    
    /**
     * Initialize the TrainingData object and load the parameter file
     */
    protected final void initialize(String[] args){
        try{
            readInputs(args);
            initInputs();
        }
        catch (IOException e) {
            System.out.print("Error loading parameter file.");
            e.printStackTrace();
        }
        catch (NullPointerException e){
            System.out.println("Parameter file not found.");
            e.printStackTrace();
        }
        catch(NumberFormatException e){
            System.out.println("Incorrect number format. Try -h to see help.");
            e.printStackTrace();
        }
        catch(GesecException e){
            e.printStackTrace();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    protected void initInputs() throws IOException, NullPointerException, GesecException, Exception{
        inputValidation();
        
        Output mainOutput = Evolve.buildOutput();
        mainOutput.getLog(0).appendOnRestart = true;
        
        File parameterFile = new File(parameterFilePath);
        ParameterDatabase dbase = new ParameterDatabase(parameterFile);
        mainState = Evolve.initialize(dbase, 0, mainOutput);
        
        Dataset data = FileHandler.readInputDataFile(inputDataFilePath);
        dataProducer.setDataset(data);
        if(shuffle)
            dataProducer.setRandomGenerator(mainState.random[0]);
    }
    
    
    
    protected void showHelp() {
        System.out.println("You must set the mandatory parameters. Available options (* indicates a mandatory option):\n"
                + "\t-p <file path>\n"
                + "\t\t * Path to the parameter file.\n"
                + "\t-i <file path>\n"
                + "\t\t * Path to the training file.\n"
                + "\t-o <file path>\n"
                + "\t\t Path to the output files.\n"
                + "\t-a <prefix string>\n"
                + "\t\t Identifier prefix for files.\n"
                + "\t-g <int>\n"
                + "\t\t Maximum number of iterations (default = 10).\n"
                + "\t-n <int>\n"
                + "\t\t Number of repetitions of the algorithm (default = 1).\n"
                + "\t-k <int>\n"
                + "\t\t Number of folds (cross validation).\n"
                + "\t-b <double>\n"
                + "\t\t If the value is in [0,1], defines percentage used for test in holdout validation.\n"
                + "\t\t If the value is bigger than 1, defines absolute quatity used for test in holdout validation.\n"
                + "\t-e <int>\n"
                + "\t\t Minimun error to compute a hit (default = 0.01).\n"
                + "\t-u\n"
                + "\t\tUses unshuffled data.\n");
    }

    private void inputValidation() throws Exception{
        // Check whether the input files was set
        if(inputDataFilePath == null || parameterFilePath == null || dataProducer == null){
            showHelp();
            System.exit(0);
        }
        // ==== Check for validation method =====
        if(dataProducer instanceof HoldoutHandler && !dataProducer.isValid()){
            throw new Exception("Test percentage/quantity must be bigger than 0 for holdout validation.");
        }
        if(dataProducer instanceof CrossvalidationHandler && !dataProducer.isValid()){
            throw new Exception("Number of folds must be greater than 1 for cross validation.");
        }
    }
    
    
    public void execute(){
        try {
//            final double[] possibleR1 = {0.1, 0.2, 0.3, 0.4, 0.5};
            
            final int totalNumberGPExecs = numExecutions*maxIterations;
            StringBuilder s_solution = new StringBuilder();
            ArrayList<Double> bestFitnessList = new ArrayList<Double>();
            // Matrix to store best fitness during iterations
            double[][][] bestFitness = new double[numExecutions][maxIterations][];
            
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
                    // Load new inputs on the proble Object
                    ((Regression)mainState.evaluator.p_problem).setDataset(data[0]);
                    ((Regression)mainState.evaluator.p_problem).setHitLevel(hitLevel);
                    ((Regression)mainState.evaluator.p_problem).setOutput(output);
                    int result = EvolutionState.R_NOTDONE;
                    
//                    if(execution == numExecutions-1)
                        bestFitnessList.clear();
                    
                    // Generations
                    while(result == EvolutionState.R_NOTDONE ){
                        result = mainState.evolve();
                        // Store the best fitness of generation
//                        if(execution == numExecutions-1)
                            bestFitnessList.add(getGenerationBestFitness(mainState));
                    }                                                        
                    GPIndividual bestSoFar = (GPIndividual)((SimpleStatistics)mainState.statistics).getBestSoFar()[0];
                    KozaFitness fitness = (KozaFitness)bestSoFar.fitness;
                    
                    if(currentIteration == maxIterations - 1 || fitness.hits == data[0].size()){
                        addLastFunctionToSolution(bestSoFar);
                        canStop = true;
                    }
                    else{
//                        double tr = possibleR1[mainState.random[0].nextInt(possibleR1.length)];
                        double tr = mainState.random[0].nextDouble();
                        addFunctionToSolution(bestSoFar, tr);
                        output = getNewOutput(data[0], output, tr);
                    }
                    
                    pontualError[currentIteration] = Utils.sumDoubleArray(
                                                        getIndividualError((Function)bestSoFar.trees[0].child, data[0], output), 
                                                        pontualError[currentIteration]);
                    
                    iterativeTrainingErrors[currentIteration] = getRMSE(solution, data[0]);
                    iterativeTestErrors[currentIteration] = getRMSE(solution, data[1]);
                    
//                    if(execution == numExecutions-1){
                        bestFitness[execution][currentIteration] = Utils.doubleListToArray(bestFitnessList);
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
            stats.updateBestOfGenErrors(bestFitness);
            // Write statistics on a file
            FileHandler.writeResults(outputPath, outputPrefix, stats, hitLevel);
            FileHandler.writeSolution(outputPath, outputPrefix, s_solution.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void main(String args[]){
        SSR1 ps = new SSR1(args);
        ps.execute();
    }

    protected void addFunctionToSolution(GPIndividual generatedFunction, double tr) {
        if(solution == null){
            solution = new Solution((Function)generatedFunction.trees[0].child, tr);
            currentSolution = solution;
        }
        else{
            currentSolution.setT2(new Solution((Function)generatedFunction.trees[0].child, tr));
            currentSolution = (Solution)currentSolution.getT2();
        }
    }
    
    protected void addLastFunctionToSolution(GPIndividual generatedFunction){
        if(solution == null){
            solution = new Solution((Function)generatedFunction.trees[0].child, 1);
        }
        else{
            currentSolution.setT2((Function)generatedFunction.trees[0].child);
        }
    }

    protected double[] getNewOutput(Dataset dataset, double[] oldOutput, double tr){
        double[] newOutput = new double[dataset.size()];
        for(int i = 0; i < dataset.size(); i++){
            Instance instance = dataset.get(i);
            double output = currentSolution.getT1().eval(instance.input);
            newOutput[i] = (oldOutput[i] - tr*output)/(1-tr);
        }
        return newOutput;
    }
    
    protected final double[] getFirstRunOutput(Dataset dataset) {
        double[] output = new double[dataset.size()];
        int i = 0;
        for(Instance instance : dataset.data){
            output[i++] = instance.output;
        }
        return output;
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
}
