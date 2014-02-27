/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gesec.core;

import ec.EvolutionState;
import ec.Evolve;
import ec.gp.GPIndividual;
import ec.gp.koza.KozaFitness;
import ec.simple.SimpleStatistics;
import ec.util.Output;
import ec.util.ParameterDatabase;
import ec.gesec.functions.Function;
import ec.gesec.handlers.CrossvalidationHandler;
import ec.gesec.handlers.FileHandler;
import ec.gesec.handlers.HoldoutHandler;
import ec.gesec.handlers.StatisticsHandler;
import ec.gesec.handlers.DataProducer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import ec.gesec.problems.Regression;




/**
 *
 * @author luiz
 */
public class GeSeC{          
    private Solution currentSolution;
    private Solution solution;
    
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
    private DataProducer dataProducer;
    
    
      
    public GeSeC(String[] args){
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
        catch (IOException ex) {
            System.out.print("Error loading parameter file.");
        }
        catch (NullPointerException ex){
            System.out.println("Parameter file not found.");
        }
        catch(NumberFormatException e){
            System.out.println("Incorrect number format. Try -h to see help.");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    protected void initInputs() throws IOException, NullPointerException, Exception{
        inputValidation();
        
        Output mainOutput = Evolve.buildOutput();
        mainOutput.getLog(0).appendOnRestart=true;
        
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
                + "\t\t Percentage used for test in holdout validation.\n"
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
            throw new Exception("Test percentage must be in [0,1) for holdout validation.");
        }
        if(dataProducer instanceof CrossvalidationHandler && !dataProducer.isValid()){
            throw new Exception("Number of folds must be greater than 1 for cross validation.");
        }
    }
    
    
    public void execute(){
        try {
            final int totalNumberGPExecs = numExecutions*maxIterations;
            // Run the algorithm for a defined number of repetitions
            for(int execution = 1; execution <= numExecutions; execution++){
                Dataset[] data = dataProducer.getTrainintTestData();
                int currentIteration = 0;
                boolean canStop = false;
                double output[] = getFirstRunOutput(data[0]);
                while(!canStop){
                    System.out.println("\n======= Execution " + ((execution-1)*maxIterations + currentIteration)  + " of " + totalNumberGPExecs + " =======");
                    mainState.startFresh();
                    // Load new inputs on the proble Object
                    ((Regression)mainState.evaluator.p_problem).setDataset(data[0]);
                    ((Regression)mainState.evaluator.p_problem).setHitLevel(hitLevel);
                    ((Regression)mainState.evaluator.p_problem).setOutput(output);
                    int result = EvolutionState.R_NOTDONE;
                    // Generations
                    while( result == EvolutionState.R_NOTDONE ){
                        result = mainState.evolve();
                    }                                                        
                    GPIndividual bestSoFar = (GPIndividual)((SimpleStatistics)mainState.statistics).getBestSoFar()[0];
                    KozaFitness fitness = (KozaFitness)bestSoFar.fitness;
                    
                    if(currentIteration == maxIterations - 1 || fitness.hits == data[0].size()){
                        addLastFunctionToSolution(bestSoFar);
                        canStop = true;
                    }
                    else{
                        double tr = mainState.random[0].nextDouble(true,true);
                        addFunctionToSolution(bestSoFar, tr);
                        output = getNewOutput(data[0], output, tr);
                    }
                    stats.updateOnNewFunction(fitness.hits, mainState.generation+1);
                    currentIteration ++;
                }
                // Test
                solution.test(data[0], data[1], stats);
                stats.finishExecution();
//                FileHandler.writeInducedFunction(outputPath, outputPrefix, solution.toString());
            }
            // Write statistics on a file
            FileHandler.writeResults(outputPath, outputPrefix, stats, hitLevel);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void main(String args[]){
        GeSeC ps = new GeSeC(args);
        ps.execute();
    }

    private void addFunctionToSolution(GPIndividual generatedFunction, double tr) {
        if(solution == null){
            solution = new Solution((Function)generatedFunction.trees[0].child, tr);
            currentSolution = solution;
        }
        else{
            currentSolution.setT2(new Solution((Function)generatedFunction.trees[0].child, tr));
            currentSolution = (Solution)currentSolution.getT2();
        }
    }
    
    private void addLastFunctionToSolution(GPIndividual generatedFunction){
        if(solution == null){
            solution = new Solution((Function)generatedFunction.trees[0].child, 1);
        }
        else{
            currentSolution.setT2((Function)generatedFunction.trees[0].child);
        }
    }

    private double[] getNewOutput(Dataset dataset, double[] oldOutput, double tr){
        double[] newOutput = new double[dataset.size()];
        for(int i = 0; i < dataset.size(); i++){
            Instance instance = dataset.get(i);
            double output = currentSolution.getT1().eval(instance.input);
            newOutput[i] = (oldOutput[i] - tr*output)/(1-tr);
        }
        return newOutput;
    }
    
    private double[] getFirstRunOutput(Dataset dataset) {
        double[] output = new double[dataset.size()];
        int i = 0;
        for(Instance instance : dataset.data){
            output[i++] = instance.output;
        }
        return output;
    }
}
