package ec.ssr.handlers;


import ec.ssr.core.Dataset;
import ec.ssr.core.ParallelVersions.SSR;
import ec.ssr.core.Utils;
import static ec.ssr.handlers.StatisticsHandler.PRECISION;
import ec.ssr.handlers.statistics.ExecutionStatistics;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * FileHandler.java
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class FileHandler {   
    /**
     * Reads a dataset file.
     * @param inputFile Input file
     * @return A dataset object
     * @throws SSRException Error while reading input file
     * @throws Exception Error while reading the file
     */
    public static Dataset readInputDataFile(File inputFile) throws SSRException, Exception{
        BufferedReader br = null;
        String currentLine;
        Dataset data = new Dataset();
        try {
            br = new BufferedReader(new FileReader(inputFile));
            // Reads each line from the file
            while ((currentLine = br.readLine()) != null) {
                // Checks for comment or empty line
                if(!currentLine.matches("^\\s*#.*") && currentLine.matches(".*\\S+.*")){
                    currentLine = currentLine.replaceAll("\\s*", "");
                    if(!currentLine.equals("")){
                        String[] s_inputOutput = currentLine.split(",");
                        double[] in = new double[s_inputOutput.length-1];
                        for(int i = 0; i < s_inputOutput.length-1; i++){
                            in[i] = Double.parseDouble(s_inputOutput[i]);
                        }
                        double out =  Double.parseDouble(s_inputOutput[s_inputOutput.length-1]);
                        data.add(in, out);
                    }
                }
                
            }
        } 
        catch (NumberFormatException e){
            throw new SSRException("Some tokens in the input file " + inputFile.getName() + " were not numbers.");
        }
        catch(ArrayIndexOutOfBoundsException e){
            throw new SSRException("Not enough data points in file.");
        }
        catch(FileNotFoundException e){
            throw new SSRException("Input file not found: " + e.getLocalizedMessage());
        }
        catch(IOException e){
            throw new SSRException("IO Error reading input file.");
        }
        finally {
            try {
                if (br != null)br.close();
            }
            catch (IOException ex) {}
        }
        return data;
    }
    
    public static Dataset readInputDataFile(String filePath) throws SSRException, Exception{
        File inputFile = new File(filePath);
        return readInputDataFile(inputFile);
    }
    
    /**
     * Reads an option sinalized by a flag;
     * @param flag The String indicating the flag.
     * @param options Array de strings de entrada
     * @return The option string related to the flag or an empty string
     * @throws Exception If there is nothing assigned to the flag.
     */
    public static String readOption(String flag, String options[]) throws Exception{
        String newString;
        // Busca a posicao do flag
        int i = getOptPosition(flag, options);

        // Se nao retorna erro, trata a flag
        if (i > -1) {
            // Se não houver nada na posicao seguinte, retorna Exception
            if (i + 1 == options.length) {
                throw new Exception("Nenhum valor atribuído à opção -" + flag + ".");
            }
            // Limpa as strings ja lidas
            options[i] = "";
            newString = options[i + 1] + "";
            options[i + 1] = "";
            return newString;
        }
        return "";
    }
    
    /**
     * Checks if the given array contains the flag "-String". If the flag 
     * is found, it is replaced with the empty string.
     * @param flag The String indicating the flag.
     * @param options The array of strings containing all the options.
     * @return True if the flag was found
     * @exception Exception If an illegal option was found
    */
    public static boolean getFlag(String flag, String[] options) throws Exception {
        int pos = getOptPosition(flag, options);

        if (pos > -1)
            options[pos] = "";

        return (pos > -1);
    }
    
    /**
     * Gets the index of an option or flag indicated by a flag "-String" from 
     * the given array of strings.
     * @param flag 	The String indicating the option.
     * @param options 	The array of strings containing all the options.
     * @return 		The position if found, or -1 otherwise
     */
    public static int getOptPosition(String flag, String options[]){
        // If there is no arguments, returns -1
        if (options == null){
            return -1;
        }
        // For each option, verifies the flag position
        for (int i = 0; i < options.length; i++) {
            if ((options[i].length() > 0) && (options[i].charAt(0) == '-')) {
                // Verifies if it isn't a negative number
                try {
                    Double.valueOf(options[i]);
                } catch (NumberFormatException e) {
                    // Verifies if it's really a flag
                    if (options[i].equals("-" + flag))
                        return i;
                }
            }
        }
        // If it finds nothing, returns -1
        return -1;
    }
        
//    public static void writeResults(String outputPath,
//                     String outputPrefix, 
//                     StatisticsHandler stats,
//                     double hitLevel) throws Exception{
//        File outputDir = getOutputDir(outputPath);
//        outputDir = new File(outputDir.getAbsolutePath()+ File.separator + outputPrefix);
//        outputDir.mkdirs();
//        // Object to write results on file
//        BufferedWriter bw;
//        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ File.separator + "trFinalPoinError.csv"));
//        bw.write(stats.getFinalPointError(StatisticsHandler.TRAIN, hitLevel));
//        bw.close();
//        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ File.separator + "tsFinalPoinError.csv"));
//        bw.write(stats.getFinalPointError(StatisticsHandler.TEST, hitLevel));
//        bw.close();
//        
//        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ File.separator + "trRMSEperIteration.csv"));
//        bw.write(stats.getRMSEperIteration(StatisticsHandler.TRAIN));
//        bw.close();
//        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ File.separator + "tsRMSEperIteration.csv"));
//        bw.write(stats.getRMSEperIteration(StatisticsHandler.TEST));
//        bw.close();
//        
//        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ File.separator + "trErrorBestOfGen.csv"));
//        bw.write(stats.getErrorBestOfGeneration());
//        bw.close();
//        
//        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ File.separator + "trErrorPerGeneration.csv"));
//        bw.write(stats.getErrorBestOfGeneration());
//        bw.close();
//        
//        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ File.separator + "trErrorPointPerIt.csv"));
//        bw.write(stats.getErrorPointPerIteration());
//        bw.close();
//        
////        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ File.separator + outputPrefix + "testError.csv"));
////        stats.writeTestErrorToFile(bw, hitLevel);
////        bw.close();
////        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ File.separator + outputPrefix + "trainingError.csv"));
////        stats.writeTrainingErrorToFile(bw, hitLevel);
////        bw.close();
//    }
    
    public static void writeSolution(String outputPath,
                                     String outputPrefix,
                                     String stringSolution) throws Exception{
        File outputDir = getOutputDir(outputPath);
        outputDir = new File(outputDir.getAbsolutePath()+ File.separator + outputPrefix);
        outputDir.mkdirs();
        
        // Object to write results on file
        BufferedWriter bw;
        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ File.separator + "solution.txt"));
        bw.write(stringSolution);
        bw.close();
    }

    /**
     * Selects the path to save output data.
     * @param outputPath Path to a directory to write the output data
     * @return File object pointing to the output directory
     */
    private static File getOutputDir(String outputPath){
        File outputDir;
        if(!outputPath.equals("")){
            outputDir = new File(outputPath);
        }
        else{
            outputDir = new File(System.getProperty("user.dir"));
        }  
        return outputDir;
    }

//    public static void writeOutputDist(String outputPath, String outputPrefix, ArrayList<double[]>[] rawOutputHistory, ArrayList<double[]>[] normOutputHistory) throws Exception{
//        File outputDir = getOutputDir(outputPath);
//        outputDir = new File(outputDir.getAbsolutePath()+ File.separator + outputPrefix);
//        outputDir.mkdirs();
//        
//        BufferedWriter bw;
//        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ File.separator + "rawOutput.txt"));
//        for(ArrayList<double[]> execOutput : rawOutputHistory){
//            for(double[] output : execOutput){
//                for(int i = 0; i < output.length; i++){
//                    bw.write(output[i] + ",");
//                }
//                bw.write("\n");
//            }
//        }
//        bw.close();
//        
//        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ File.separator + "normOutput.txt"));
//        for(ArrayList<double[]> execOutput : normOutputHistory){
//            for(double[] output : execOutput){
//                for(int i = 0; i < output.length; i++){
//                    bw.write(output[i] + ",");
//                }
//                bw.write("\n");
//            }
//        }
//        bw.close();
//    }

//    public static void writeTrainingIds(String outputPath, 
//                                      String outputPrefix, 
//                                      String stringIds) throws Exception{
//        File outputDir = getOutputDir(outputPath);
//        outputDir = new File(outputDir.getAbsolutePath()+ File.separator + outputPrefix);
//        outputDir.mkdirs();
//        
//        // Object to write results on file
//        BufferedWriter bw;
//        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ File.separator + "trainingIds.txt"));
//        bw.write(stringIds);
//        bw.close();
//    }

    public static void writeResults(SSR[] threadsSSR, 
                                   String outputPath, 
                                   String outputPrefix, 
                                   double hitLevel) throws Exception{
        File outputDir = getOutputDir(outputPath);
        outputDir = new File(outputDir.getAbsolutePath()+ File.separator + outputPrefix);
        outputDir.mkdirs();
        // Object to write results on file
        BufferedWriter bw;
//        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ File.separator + "trFinalPoinError.csv"));
//        bw.write(getFinalPointError(threadsSSR, ExecutionStatistics.TRAIN, hitLevel));
//        bw.close();
//        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ File.separator + "tsFinalPoinError.csv"));
//        bw.write(getFinalPointError(threadsSSR, ExecutionStatistics.TEST, hitLevel));
//        bw.close();
        
        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ File.separator + "trFitness.csv"));
        bw.write(getRMSEperIteration(threadsSSR, ExecutionStatistics.TRAIN));
        bw.close();
        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ File.separator + "tsFitness.csv"));
        bw.write(getRMSEperIteration(threadsSSR, ExecutionStatistics.TEST));
        bw.close();
        
//        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ File.separator + "trErrorBestOfGen.csv"));
//        bw.write(stats.getErrorBestOfGeneration());
//        bw.close();
        
//        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ File.separator + "trErrorPerGeneration.csv"));
//        bw.write(getErrorBestOfGeneration(threadsSSR));
//        bw.close();
        
        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ File.separator + "executionTime.csv"));
        bw.write(getExectuionTime(threadsSSR));
        bw.close();
        
        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ File.separator + "outputs.csv"));
        bw.write(getSolutionOutputs(threadsSSR));
        bw.close();
//        
//        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ File.separator + "trErrorPointPerIt.csv"));
//        bw.write(getErrorPointPerIteration(threadsSSR));
//        bw.close();
        
//        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ File.separator + "outputVectors.csv"));
//        bw.write(getOutputVectors(threadsSSR));
//        bw.close();
        
//        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ File.separator + "numZeroDiv.csv"));
//        bw.write(getNumZeroDivIteration(threadsSSR));
//        bw.close();
        
        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ File.separator + "numNodes.csv"));
        bw.write(getNumNodes(threadsSSR));
        bw.close();     
        
        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ File.separator + "seeds.txt"));
        bw.write(getSeeds(threadsSSR));
        bw.close();     
    }
    
    private static String getSolutionOutputs(SSR[] threadsSSR) {
        StringBuilder outputStr = new StringBuilder();
        double outputs[][] = threadsSSR[0].getStatistics().getOutputsPerInstance(ExecutionStatistics.TRAIN);
        String delimiter = "";
        for(double instance[] : outputs){
            outputStr.append(delimiter+Utils.printDouble(instance[instance.length-2], PRECISION));
            delimiter = ",";
        }
        outputs = threadsSSR[0].getStatistics().getOutputsPerInstance(ExecutionStatistics.TEST);
        for(double instance[] : outputs){
            outputStr.append(delimiter+Utils.printDouble(instance[instance.length-2], PRECISION));
        }
        outputStr.append("\n");
        
        for(SSR exec : threadsSSR){
            outputs = exec.getStatistics().getOutputsPerInstance(ExecutionStatistics.TRAIN);
            delimiter = "";
            for(double instance[] : outputs){
                outputStr.append(delimiter+Utils.printDouble(instance[instance.length-1], PRECISION));
                delimiter = ",";
            }
            outputs = exec.getStatistics().getOutputsPerInstance(ExecutionStatistics.TEST);
            for(double instance[] : outputs){
                outputStr.append(delimiter+Utils.printDouble(instance[instance.length-1], PRECISION));
            }
            outputStr.append("\n");
        }
        return outputStr.toString();
    }

    public static void writeSolution(SSR[] threadsSSR, 
                                    String outputPath, 
                                    String outputPrefix) throws Exception{
        File outputDir = getOutputDir(outputPath);
        outputDir = new File(outputDir.getAbsolutePath()+ File.separator + outputPrefix);
        outputDir.mkdirs();
        
        // Object to write results on file
        BufferedWriter bw;
        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ File.separator + "solution.txt"));
        bw.write(getSolutionsAsString(threadsSSR));
        bw.close();
    }
    
    

    private static String getFinalPointError(SSR[] threadsSSR, int type, double hitLevel) {
        StringBuilder outputStr = new StringBuilder();
        double[] totalError = new double[threadsSSR.length];
        double[] rmse = new double[threadsSSR.length];
        double[] hits = new double[threadsSSR.length];
               
        for(int exec = 0; exec < threadsSSR.length; exec ++){            
            outputStr.append("Execution " + (exec+1) + "\n");
            outputStr.append("input,output,evaluated,error\n");
            double[][] data = threadsSSR[exec].getStatistics().getOutputsPerInstance(type);
            for(int j = 0; j < data.length; j++){
                int inputSize = data[j].length;
                double error = Math.abs(data[j][inputSize-1]-data[j][inputSize-2]);
                totalError[exec] += error;
                rmse[exec] += error * error;
                StringBuilder inputAux = new StringBuilder();
                for(int k = 0; k < inputSize - 2; k++){
                    inputAux.append(Utils.printDouble(data[j][k], PRECISION)).append(",");
                }
                outputStr.append(inputAux.toString() + 
                        Utils.printDouble(data[j][inputSize-2], PRECISION) + "," +
                        Utils.printDouble(data[j][inputSize-1], PRECISION) + "," + 
                        Utils.printDouble(error, PRECISION));
                outputStr.append("\n");
                                
                if(error <= hitLevel)
                    hits[exec]++;
            }
            outputStr.append("Error:," + Utils.printDouble(totalError[exec], PRECISION) +
                    ",Hits:," + Utils.printDouble(hits[exec], PRECISION) + "\n\n");
        }
        
        double mae[] = new double[threadsSSR.length];
        for(int exec = 0; exec < threadsSSR.length; exec++){
            double[][] data = threadsSSR[exec].getStatistics().getOutputsPerInstance(type);
            rmse[exec] /= data.length;
            rmse[exec] = Math.sqrt(rmse[exec]);
            mae[exec] = totalError[exec] / data.length;
        }
        double mTotalError = Utils.getMean(totalError);
        double sdTotalError = Utils.getSD(totalError, mTotalError);
        double mMAE = Utils.getMean(mae);
        double sdMAE = Utils.getSD(mae, mMAE);
        double mRMSE = Utils.getMean(rmse);
        double sdRMSE = Utils.getSD(rmse, mRMSE);
        double mHits = Utils.getMean(hits);
        double sdHits = Utils.getSD(hits, mHits);
        String out = "MAE," + Utils.printDouble(mMAE, PRECISION) + "," + 
                Utils.printDouble(sdMAE, PRECISION) + "\nRMSE," + 
                Utils.printDouble(mRMSE, PRECISION) + "," +
                Utils.printDouble(sdRMSE, PRECISION) + "\nTotal Error," + 
                Utils.printDouble(mTotalError, PRECISION) + "," + 
                Utils.printDouble(sdTotalError, PRECISION) + "\nHits," +
                Utils.printDouble(mHits, PRECISION) + ","+ 
                Utils.printDouble(sdHits, PRECISION);
        outputStr.append(out);
        System.out.println(out);
        return outputStr.toString();
    }

    private static String getRMSEperIteration(SSR[] threadsSSR, int type) {
        StringBuilder outputStr = new StringBuilder();
        outputStr.append("RMSE during iterations (execution x iteration)\n");
        for (SSR execution : threadsSSR) {
            String separator = "";
            double[] errorPerIteration = execution.getStatistics().getErrorPerIterarion(type);
            for (int j = 0; j < errorPerIteration.length; j++) {
                outputStr.append(separator + Utils.printDouble(errorPerIteration[j], PRECISION));
                separator = ",";
            }
            outputStr.append("\n");
        }
        return outputStr.toString();
    }
    
    private static String getNumZeroDivIteration(SSR[] threadsSSR) {
        StringBuilder outputStr = new StringBuilder();
        outputStr.append("Number of zero divisions during iterations (training only) (execution x iteration)\n");
        for (SSR execution : threadsSSR) {
            String separator = "";
            int[] numZeroDivPerIteration = execution.getStatistics().getNumZeroDiv();
            for (int j = 0; j < numZeroDivPerIteration.length; j++) {
                outputStr.append(separator + numZeroDivPerIteration[j]);
                separator = ",";
            }
            outputStr.append("\n");
        }
        return outputStr.toString();
    }

    private static String getErrorBestOfGeneration(SSR[] threadsSSR) {
        StringBuilder outputStr = new StringBuilder();
        outputStr.append("RMSE of the best individual of each generation (iteration x generation). Relative to the iteration output vector.\n");
        for (SSR execution : threadsSSR) {
            double[][] bestOfGenError = execution.getStatistics().getBestOfGenerationError();
            for (double[] byIteration : bestOfGenError) {
                if(byIteration != null){
                    String separator = "";
                    for (int k = 0; k < byIteration.length; k++) {
                        outputStr.append(separator + Utils.printDouble(byIteration[k], PRECISION));
                        separator = ",";
                    }
                }
                outputStr.append("\n");
            }
            outputStr.append("\n\n");
        }
        return outputStr.toString();
    }

    private static String getErrorPointPerIteration(SSR[] threadsSSR) {
        StringBuilder outputStr = new StringBuilder();
        outputStr.append("Abolute error (each block is an execution) for each point per iteration (iteration x point). Relative to the iteration output vector.\n");
        for(SSR execution : threadsSSR){
            double pontualError[][] = execution.getStatistics().getPontualError();
            for(int i = 0; i < pontualError.length; i++){
                if(pontualError[i] != null){
                    String separator = "";
                    for(int j = 0; j < pontualError[i].length; j++){
                        outputStr.append(separator + Utils.printDouble(pontualError[i][j], PRECISION));
                        separator = ",";
                    }
                    outputStr.append("\n");
                }
            }
            outputStr.append("\n");
        }
//        
//        for(int i = 0; i < sumOverExecutions.length; i++){
//            String separator = "";
//            for(int j = 0; j < sumOverExecutions[i].length; j++){
//                outputStr.append(separator + Utils.printDouble(sumOverExecutions[i][j]/threadsSSR.length, PRECISION));
//                separator = ",";
//            }
//            outputStr.append("\n");
//        }
        return outputStr.toString();
    }

    private static String getSolutionsAsString(SSR[] threadsSSR) {
        StringBuilder s_solution = new StringBuilder();
        int execCounter = 1;
        for(SSR execution : threadsSSR){
//            s_solution.append("Execution ").append(execCounter++).append("\n").append(execution.getSolution().print()).append("\n\n");
            s_solution.append("Execution ").append(execCounter++).append("\n").append(execution.getStatistics().getIterativeSolution()).append("\n\n");
        }
        return s_solution.toString();
    }

    private static String getOutputVectors(SSR[] threadsSSR) {
        StringBuilder outputStr = new StringBuilder();
        outputStr.append("Output vector (each block is an execution) for each iteration (iteration x point). Relative to the iteration.\n");
        for(SSR execution : threadsSSR){
            double outputVectors[][] = execution.getStatistics().getOutputVectors();
            if(outputVectors != null){
                for(int i = 0; i < outputVectors.length; i++){
                    String separator = "";
                    if(outputVectors[i] != null){
                        for(int j = 0; j < outputVectors[i].length; j++){
                            outputStr.append(separator + Utils.printDouble(outputVectors[i][j], PRECISION));
                            separator = ",";
                        }
                        outputStr.append("\n");
                    }
                }
                outputStr.append("\n");
            }
        }
        return outputStr.toString();
    }

    private static String getNumNodes(SSR[] threadsSSR) {
        StringBuilder outputStr = new StringBuilder("Number of nodes (per execution) \n");
        for(SSR algorithm : threadsSSR){
            outputStr.append(algorithm.getStatistics().getSolutionSizeAsString());
            outputStr.append("\n");
        }
        return outputStr.toString();
    }

    private static String getSeeds(SSR[] threadsSSR) {
        StringBuilder outputStr = new StringBuilder();
        for(SSR algorithm : threadsSSR){
            outputStr.append(algorithm.getSeed());
            outputStr.append("\n");
        }
        return outputStr.toString();
    }

    private static String getExectuionTime(SSR[] threadsSSR) {
        StringBuilder outputStr = new StringBuilder("Execution Time (per execution) \n");
        for(SSR algorithm : threadsSSR){
            outputStr.append(algorithm.getStatistics().getExecutionTime());
            outputStr.append("\n");
        }
        return outputStr.toString();
    }
}
