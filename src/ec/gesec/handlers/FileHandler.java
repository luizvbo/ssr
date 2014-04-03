package ec.gesec.handlers;


import ec.gesec.core.Dataset;
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
     * @param filePath Path to the file
     * @return A dataset object
     * @throws Exception Error while reading the file
     */
    public static Dataset readInputDataFile(String filePath) throws Exception{
        BufferedReader br = null;
        String currentLine;
        Dataset data = new Dataset();
        try {
            br = new BufferedReader(new FileReader(filePath));
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
            throw new GesecException("Some tokens in the input file were not numbers.");
        }
        catch(ArrayIndexOutOfBoundsException e){
            throw new GesecException("Not enough data points in file.");
        }
        catch(FileNotFoundException e){
            throw new GesecException("Input file not found.");
        }
        catch(IOException e){
            throw new GesecException("IO Error reading input file.");
        }
        finally {
            try {
                if (br != null)br.close();
            }
            catch (IOException ex) {}
        }
        return data;
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
        
    public static void writeResults(String outputPath,
                     String outputPrefix, 
                     StatisticsHandler stats,
                     double hitLevel) throws Exception{
        File outputDir = getOutputDir(outputPath);
        
        // Object to write results on file
        BufferedWriter bw;
        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ "/" + outputPrefix + "testError.csv"));
        stats.writeTestErrorToFile(bw, hitLevel);
        bw.close();
        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ "/" + outputPrefix + "trainingError.csv"));
        stats.writeTrainingErrorToFile(bw, hitLevel);
        bw.close();
    }
    
    public static void writeSolution(String outputPath,
                                     String outputPrefix,
                                     String stringSolution) throws Exception{
        File outputDir = getOutputDir(outputPath);
        
        // Object to write results on file
        BufferedWriter bw;
        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ "/" + outputPrefix + "solution.txt"));
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
    
}
