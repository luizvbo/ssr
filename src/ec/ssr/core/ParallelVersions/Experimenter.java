/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ssr.core.ParallelVersions;

import ec.ssr.core.ParallelVersions.SSR6.SSR6;
import ec.ssr.core.ParallelVersions.SSR4.SSR4;
import ec.ssr.core.ParallelVersions.SSR3.SSR3;
import ec.ssr.core.ParallelVersions.SSR2.SSR2;
import ec.ssr.core.ParallelVersions.SSR1.SSR1;
import ec.EvolutionState;
import ec.ssr.core.Dataset;
import ec.ssr.core.Instance;
import ec.ssr.core.ParallelVersions.SSR7.SSR7;
import ec.ssr.core.ParallelVersions.SSR1.SolutionSSR1;
import ec.ssr.core.ParallelVersions.SSR8.SSR8;
import ec.ssr.handlers.CrossvalidationHandler;
import ec.ssr.handlers.DataProducer;
import ec.ssr.handlers.FileHandler;
import ec.ssr.handlers.SSRException;
import ec.ssr.handlers.HoldoutHandler;
import ec.ssr.handlers.StatisticsHandler;
import ec.util.MersenneTwisterFast;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author luiz
 */
public class Experimenter {
    protected SolutionSSR1 currentSolution;
    protected SolutionSSR1 solution;
    
    protected int version = 1;
    protected String parameterFilePath;
    protected String inputDataFilePath; 
    protected String seedPath = ""; 
    protected String outputPath = "";
    protected String outputPrefix = "";
    protected int numIterations = 10;
    protected double hitLevel = 0.0;
    protected double validationPercentage = 0.0;
    protected int[] seed;
    protected int numExecutions = 1;
    protected int numThreads = 1;
    protected boolean shuffle = true;
    protected ArrayList<String> inputParameters;
    
    protected StatisticsHandler stats;
    protected EvolutionState mainState;
    protected DataProducer dataProducer;
    
    public Experimenter(String[] args){
        initialize(args);
//        stats = new StatisticsHandler(numExecutions);
    }
    
    public static void main(String args[]){
        long startTime = System.currentTimeMillis();
        Experimenter experiment = new Experimenter(args);
        experiment.execute();
        System.out.println("Elapsed Time: " + ((System.currentTimeMillis() - startTime)/1000) + " seconds");
    }
    
    private void execute(){
        try {
            SSR[] threadsSSR = new SSR[numExecutions];
            int currentSeed;
            ExecutorService executor = Executors.newFixedThreadPool(numThreads);
            // Run the algorithm for a defined number of repetitions
            for(int execution = 0; execution < numExecutions; execution++){
                // If we have a list of seeds, we use it
                if(seed != null) currentSeed = seed[execution];
                // Else, we use a random seed
                else currentSeed = (int)System.currentTimeMillis();
                Dataset[] data = partitionatedData();
                threadsSSR[execution] = getNewSSR(data[0], data[1], data[2], currentSeed);
                executor.execute(threadsSSR[execution]);    
            }
            executor.shutdown();
            executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
            FileHandler.writeResults(threadsSSR, outputPath, outputPrefix, hitLevel);
            FileHandler.writeSolution(threadsSSR, outputPath, outputPrefix);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public SSR getNewSSR(Dataset trainingSet, Dataset validationSet, 
                         Dataset testSet, long seed) throws Exception{
        SSR algorithm;
        switch(version){
            case 1:
                algorithm = new SSR1(trainingSet, validationSet, testSet, outputPath, outputPrefix, numIterations, 
                                     numExecutions, seed, hitLevel, parameterFilePath, inputParameters);
                break;
            case 2:
                algorithm = new SSR2(trainingSet, validationSet, testSet, outputPath, outputPrefix, numIterations, 
                                     numExecutions, seed, hitLevel, parameterFilePath, inputParameters);
                break;
            case 3:
                algorithm = new SSR3(trainingSet, validationSet, testSet, outputPath, outputPrefix, numIterations, 
                                     numExecutions, seed, hitLevel, parameterFilePath, inputParameters);
                break;
            case 4:
                algorithm = new SSR4(trainingSet, validationSet, testSet, outputPath, outputPrefix, numIterations, 
                                     numExecutions, seed, hitLevel, parameterFilePath, inputParameters);
                break;
            case 6:
                algorithm = new SSR6(trainingSet, validationSet, testSet, outputPath, outputPrefix, numIterations, 
                                     numExecutions, seed, hitLevel, parameterFilePath, inputParameters);
                break;
            case 7:
                algorithm = new SSR7(trainingSet, validationSet, testSet, outputPath, outputPrefix, numIterations, 
                                     numExecutions, seed, hitLevel, parameterFilePath, inputParameters);
                break;
            case 8:
                algorithm = new SSR8(trainingSet, validationSet, testSet, outputPath, outputPrefix, numIterations, 
                                     numExecutions, seed, hitLevel, parameterFilePath, inputParameters);
                break;
            default:
                algorithm = new SSR4(trainingSet, validationSet, testSet, outputPath, outputPrefix, numIterations, 
                                     numExecutions, seed, hitLevel, parameterFilePath, inputParameters);
                break;
        }
        return algorithm;
    }
    
    
    /**
     * Reads the options from command line
     * @param args Command line parameters
     * @throws FileNotFoundException Error while loadin parameter file
     * @throws NumberFormatException Erro while parsing numerical parameters 
     * @throws Exception Other exceptions
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
            String inputString = FileHandler.readOption("s", args);
            if(!inputString.equals("")){
                seedPath = inputString;
            }
            inputString = FileHandler.readOption("p", args);
            if(!inputString.equals("")){
                parameterFilePath = inputString;
            }
            inputString = FileHandler.readOption("P", args);
            while(!inputString.equals("")){
                if(inputParameters == null)
                    inputParameters = new ArrayList<String>();
                inputParameters.add(inputString);
                inputString = FileHandler.readOption("P", args);
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
                numIterations = Integer.parseInt(inputString);
            }
            inputString = FileHandler.readOption("n", args);
            if(!inputString.equals("")){
                numExecutions = Integer.parseInt(inputString);
            }
            inputString = FileHandler.readOption("t", args);
            if(!inputString.equals("")){
                numThreads = Integer.parseInt(inputString);
            }
            inputString = FileHandler.readOption("v", args);
            if(!inputString.equals("")){
                version = Integer.parseInt(inputString);
            }
            inputString = FileHandler.readOption("e", args);
            if(!inputString.equals("")){
                hitLevel = Double.parseDouble(inputString);
            }
            inputString = FileHandler.readOption("V", args);
            if(!inputString.equals("")){
                validationPercentage = Double.parseDouble(inputString);
            }
            inputString = FileHandler.readOption("k", args);
            if(!inputString.equals("")){
                dataProducer = new CrossvalidationHandler(Integer.parseInt(inputString));
            }
            inputString = FileHandler.readOption("b", args);
            if(!inputString.equals("")){
                dataProducer= new HoldoutHandler(Double.parseDouble(inputString));
            }
            if(FileHandler.getFlag("C", args)){
                dataProducer= new CrossvalidationHandler();
            }
            if(FileHandler.getFlag("B", args)){
                dataProducer= new HoldoutHandler();
            }
            // Uses unshuffled data
            if(FileHandler.getFlag("u", args)){
                shuffle = false;
            }
    }
    
    /**
     * Reads inputs, initialize the TrainingData object and load the parameter file
     * @param args Input parameters from command line
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
        catch(SSRException e){
            e.printStackTrace();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * Validate and instantiate data objects
     * @throws IOException 
     * @throws NullPointerException
     * @throws SSRException
     * @throws Exception 
     */
    protected void initInputs() throws IOException, NullPointerException, SSRException, Exception{
        inputValidation();
        
//        Output mainOutput = Evolve.buildOutput();
//        mainOutput.getLog(0).appendOnRestart=true;
//        
//        File parameterFile = new File(parameterFilePath);
//        ParameterDatabase dbase = new ParameterDatabase(parameterFile);
//        mainState = Evolve.initialize(dbase, 0, mainOutput);
        
        
        dataProducer.setDataset(inputDataFilePath);
        if(shuffle)
            dataProducer.setRandomGenerator(new MersenneTwisterFast(System.currentTimeMillis()));
    }
    
    /**
     * Shows the list of command line options
     */
    protected void showHelp() {
        System.out.println("You must set the mandatory parameters. Available options (* indicates a mandatory option):\n"
                + "  -v <int>\n"
                + "     * SSR version.\n"
                + "  -p <file path>\n"
                + "     * Path to the parameter file.\n"
                + "  -i <file path>\n"
                + "     * Path to the training file.\n"
                + "  -o <file path>\n"
                + "     Path to the output files.\n"
                + "  -s <file path>\n"
                + "     Path to a file with a list of seeds to use during iterations.\n"
                + "  -a <prefix string>\n"
                + "     Identifier prefix for files.\n"
                + "  -t <int>\n"
                + "     Number of threads (for parallel execution)."
                + "  -g <int>\n"
                + "     Maximum number of iterations (default = 10).\n"
                + "  -n <int>\n"
                + "     Number of repetitions of the algorithm (default = 1).\n"
                + "  -k <int>\n"
                + "     Number of folds (cross-validation).\n"
                + "  -b <double>\n"
                + "     If the value is in [0,1], defines percentage used for test in holdout validation.\n"
                + "     If the value is bigger than 1, defines absolute quatity used for test in holdout validation.\n"
                + "  -e <int>\n"
                + "     Minimun error to compute a hit (default = 0.01).\n"
                + "  -C\n"
                + "     Uses splited data from files for cross-validation. Use a path to the files in the form\n"
                + "     /pathToFile/repeatedName#repeatedName, where # indicates where the fold index is placed\n"
                + "     (a number from 0 to k-1), e.g. /home/iris-#.dat, with 3 folds in the path will look for\n"
                + "     iris-0.dat, iris-1.dat and iris-2.dat\n"
                + "  -B\n"
                + "     Uses sampled data from files for holdout. Use a path to the files in the form\n"
                + "     /pathToFile/repeatedName#repeatedName, where # is composed by the pattern "
                + "     (train|test)-i with i=0,1,...,n-1, where n is the number of experiment files,\n"
                + "     e.g. /home/iris-#.dat, with 4 files (2x(train+test)) in the path will look for\n"
                + "     iris-train-0.dat, iris-test-0.dat, iris-train-1.dat and iris-test-0.dat\n"
                + "  -u\n"
                + "    Uses unshuffled data.\n");
    }

    /**
     * Checks for input errors
     * @throws Exception Errors while setting DataProducers
     */
    private void inputValidation() throws Exception{
        // Check whether the input files was set
        if(inputDataFilePath == null || parameterFilePath == null || dataProducer == null){
            showHelp();
            System.exit(0);
        }
        // ==== Check for validation method =====
//        if(dataProducer instanceof HoldoutHandler && !dataProducer.isValid()){
//            throw new Exception("Test percentage/quantity must be bigger than 0 for holdout validation.");
//        }
//        if(dataProducer instanceof CrossvalidationHandler && !dataProducer.isValid()){
//            throw new Exception("Number of folds must be greater than 1 for cross validation.");
//        }
    }
    
    /**
     * Copies the outputs from a datase as an double array
     * @param dataset Input dataset
     * @return An array of doubles containing the outputs
     */
    protected final double[] getFirstRunOutput(Dataset dataset) {
        double[] output = new double[dataset.size()];
        int i = 0;
        for(Instance instance : dataset.data){
            output[i++] = instance.output;
        }
        return output;
    }

    /**
     * Partitionate the dataset in training, validation and test sets (in this order)
     * @return An array with the training, validation and test sets (in this order)
     */
    private Dataset[] partitionatedData() {
        Dataset data[] = new Dataset[3];
        Dataset testTraining[] = dataProducer.getTrainintTestData();
        data[2] = testTraining[1];
        if(validationPercentage <= 0 || validationPercentage >= 1){
            data[0] = testTraining[0];
            return data;
        }
        int trainingSize = (int)Math.round((1-validationPercentage) * testTraining[0].size());
        ArrayList<Instance> dataCopy = new ArrayList<Instance>(testTraining[0].data);
        Iterator<Instance> it = dataCopy.iterator();
        data[0] = new Dataset();
        data[1] = new Dataset();
        for(int i = 0; i < trainingSize; i++){
            data[0].add(it.next());
            it.remove();
        }
        while(it.hasNext()){
            data[1].add(it.next());
            it.remove();
        }
        return data;
    }
}
