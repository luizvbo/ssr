/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ssr.handlers;

import ec.ssr.core.Utils;
import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

/**
 *
 * @author luiz
 */
public class StatisticsHandler {    
    public static final int TRAIN = 0;
    public static final int TEST = 1;
    public static final int PRECISION = 5;
    
    private final double train[][][];
    private final double test[][][];
        
//    private int numGenerations[];
    private int currentExecution;
    private final double iterativeTrainingError[][];
    private final double iterativeTestError[][];
    private final int solutionSize[];
    private double pontualError[][];
    private double bestOfGenError[][][];

    public StatisticsHandler(int numExecutions) {  
        train = new double[numExecutions][][];
        test = new double[numExecutions][][];
        iterativeTrainingError = new double[numExecutions][];
        iterativeTestError = new double[numExecutions][];
        solutionSize = new int[numExecutions];
       
        // First dimension of size two (fisrt for ecjCovered and second for treeCovered)
//        numGenerations = new int[numExecutions];
        currentExecution = 0;
    }
    
    public void updateIterativeErrors(double[] trainingError, double[] testError){
//        this.numGenerations[currentExecution] += numGenerations;
        iterativeTrainingError[currentExecution] = trainingError;        
        iterativeTestError[currentExecution] = testError;
    }
    
    public void updateBestOfGenErrors(double[][][] errors){
        bestOfGenError = errors;        
    }
    
    public void updateSolutionSize(int solutionSize){
        this.solutionSize[currentExecution] = solutionSize;
    }
    
    public void updatePontualError(double[][] pontualError) {
        this.pontualError = pontualError;
        for (double[] errorPerExec : this.pontualError) {
            for (int j = 0; j < errorPerExec.length; j++) {
                errorPerExec[j] /= train.length;
            }
        }
    }
    
    public void finishExecution(){
        System.out.println("\n ====== Execution: " + (currentExecution+1) + " ======");
        currentExecution ++;
    }
        
    public void writeTestErrorToFile(BufferedWriter bw, double hitLevel) throws IOException{
        if(test[0].length > 0)
            writeErrorToFile(bw, hitLevel, test, false);
    }
    
    public void writeTrainingErrorToFile(BufferedWriter bw, double hitLevel) throws IOException{
        writeErrorToFile(bw, hitLevel, train, true);
    }
    
    public void writeErrorToFile(BufferedWriter bw, double hitLevel, double[][][] data, boolean isTraining) throws IOException{
        // Transpose the matrix to calculate the mean and sd
//        double[][] transposeError = new double[data[0].length+1][data.length];
//        double[] totalError = new double[data.length];
//        double[] mse = new double[data.length];
//        double[] hits = new double[data.length];
//        for(int exec = 0; exec < data.length; exec ++){            
//            bw.write("Execution " + (exec+1) + "\n");
//            bw.write("input,output,evaluated,error\n");
//            for(int j = 0; j < data[exec].length; j++){
//                int inputSize = data[exec][j].length;
//                double error = Math.abs(data[exec][j][inputSize-1]-data[exec][j][inputSize-2]);
//                totalError[exec] += error;
//                mse[exec] += error * error;
//                StringBuilder inputAux = new StringBuilder();
//                for(int k = 0; k < inputSize - 2; k++){
//                    inputAux.append(data[exec][j][k]).append(",");
//                }
//                bw.write(inputAux.toString() + data[exec][j][inputSize-2] + "," + data[exec][j][inputSize-1] + "," + error);
//                bw.write("\n");
//                transposeError[j][exec]= error;
//                                
//                if(error <= hitLevel)
//                    hits[exec]++;
//            }
//            bw.write("Error:," + totalError[exec] + ",Hits:," + hits[exec] + "\n\n");
//        }
//        bw.write("Point, error mean, DP\n");
//        for(int i = 0; i < transposeError.length; i++){
//            double mean = getMean(transposeError[i]);
//            double sd = getSD(transposeError[i], mean);
//            bw.write((i+1) + "," + mean + "," + sd + "\n");
//        }
        DecimalFormat formatter = new DecimalFormat("#.###", new DecimalFormatSymbols(Locale.ENGLISH));
        // Write this data only for training
        if(isTraining){
//            bw.write("\nMSE during iterations (execution x iteration)\n");
//            for (double[] byExecution : iterativeTrainingError) {
//                for (int j = 0; j < byExecution.length; j++) {
//                    bw.write(formatter.format(byExecution[j]) + ",");
//                }
//                bw.write("\n");
//            }
//            bw.write("\nBest of Generation Error (iteration x generation) \n");
//            for (double[][] byExecution : bestOfGenError) {
//                for (double[] byIteration : byExecution) {
//                    for (int k = 0; k < byIteration.length; k++) {
////                    bw.write(formatter.format(bestOfGenError[i][j]/train.length) + ",");
//                        bw.write(formatter.format(byIteration[k]) + ",");
//                    }
//                    bw.write("\n");
//                }
//                bw.write("\n\n");
//            }
//            bw.write("\nPontual Error per Iteration (iteration x point) \n");
//            for(int i = 0; i < pontualError.length; i++){
//                for(int j = 0; j < pontualError[i].length; j++){
//                    bw.write(formatter.format(pontualError[i][j]/train.length) + ",");
//                }
//                bw.write("\n");
//            }
//            bw.write("\nNumber of nodes (per execution) \n");
//            for(int i = 0; i < solutionSize.length; i++){
//                bw.write(solutionSize[i] + ",");
//            }
//            bw.write("\n");
        }
        else{
//            bw.write("\nMSE during iterations (execution x iteration)\n");
//            for(int i = 0; i < iterativeTestError.length; i++){
//                for(int j = 0; j < iterativeTestError[i].length; j++){
//                    bw.write(formatter.format(iterativeTestError[i][j]) + ",");
//                }
//                bw.write("\n");
//            }
        }
//        double rmse[] = new double[data.length];
//        double mae[] = new double[data.length];
//        for(int i = 0; i < data.length; i++){
//            mse[i] /= data[i].length;
//            rmse[i] = Math.sqrt(mse[i]);
//            mae[i] = totalError[i] / data[i].length;
//        }
////        double rmseMedian = getMedian(rmse);
////        double iqr = getIQR(rmse, rmseMedian);
//        double mTotalError = getMean(totalError);
//        double sdTotalError = getSD(totalError, mTotalError);
//        double mMae = getMean(mae);
//        
////        if(Double.isNaN(mMae) || Double.isInfinite(mMae)){
////            int x=1;
////        }
//        
//        double sdMae = getSD(mae, mMae);
//        double mMse = getMean(mse);
//        double sdMse = getSD(mse, mMse);
//        double mHits = getMean(hits);
//        double sdHits = getSD(hits, mHits);
//        String out = "MAE," + formatter.format(mMae) + "," + formatter.format(sdMae) + "\n"
//                //+ "RMSE (median)," + formatter.format(rmseMedian) + ",IQR," + formatter.format(iqr) + "\n" 
//                + "MSE," + formatter.format(mMse) + "," + formatter.format(sdMse) + "\n" 
//                + "Total Error," + formatter.format(mTotalError) + "," + formatter.format(sdTotalError) + "\n"
//                + "Hits," + formatter.format(mHits) + ","+ formatter.format(sdHits);
//        bw.write(out);
//        System.out.println(out);
    }
    
    public double[] getMeanFromArrayList(ArrayList<double[]> data){
        if(data != null && !data.isEmpty()){
            double[] sum = new double[data.get(0).length];
            for(double[] d : data){
                for(int i = 0; i < sum.length; i++){
                    sum[i] += d[i];
                }
            }
            for(int i = 0; i < sum.length; i++){
                sum[i] /= data.size();
            }
            return sum;
        }
        return null;
    }

    private double[] getSDFromArrayList(ArrayList<double[]> data, double[] mean) {
        if(data != null && !data.isEmpty()){
            double[] sum = new double[data.get(0).length];
            for(double[] d : data){
                for(int i = 0; i < sum.length; i++){
                    sum[i] += Math.pow(d[i]-mean[i], 2);
                }
            }
            for(int i = 0; i < sum.length; i++){
                sum[i] = Math.sqrt(sum[i]/(data.size()-1));
            }
            return sum;
        }
        return null;
    }
    
    public void newTrainingExecution(int trainingSize){
        train[currentExecution] = new double[trainingSize][];
    }
    
    public void newTestExecution(int testSize){
        test[currentExecution] = new double[testSize][];
    }
    
    public void addTrainingData(double[] data, double output, double evaluated, int index){
        train[currentExecution][index] = new double[data.length+2];
        for(int i = 0; i < data.length; i++){
            train[currentExecution][index][i] = data[i];
        }
        train[currentExecution][index][data.length] = output;
        train[currentExecution][index][data.length+1] = evaluated;
    }
    
    public void addTestData(double[] data, double output, double evaluated, int index){
        test[currentExecution][index] = new double[data.length+2];
        for(int i = 0; i < data.length; i++){
            test[currentExecution][index][i] = data[i];
        }
        test[currentExecution][index][data.length] = output;
        test[currentExecution][index][data.length+1] = evaluated;
    }


    private double[] getRMSE(double[][] transposeError) {
        double[] rmse = new double[transposeError[0].length];
        for(int i = 0; i < transposeError.length; i++){
            for(int j = 0; j < rmse.length; j++){
                rmse[j] += transposeError[i][j]*transposeError[i][j];
            }
        }
        for(int i = 0; i < rmse.length; i++){
            rmse[i] = Math.sqrt(rmse[i]/transposeError.length);
        }
        return rmse;
    }

    private double getIQR(double[] values, double median) {
        if (values.length < 3)
            return 0;
        
        double[] lowerHalf = getValuesLessThan(values, median, true);
        double[] upperHalf = getValuesGreaterThan(values, median, true);

        return Utils.getMedian(upperHalf) - Utils.getMedian(lowerHalf);
    }
    
    public static double[] getValuesGreaterThan(double[] values, double limit, boolean orEqualTo) {
        ArrayList<Double> modValues = new ArrayList<Double>();

        for (double value : values)
            if (value > limit || (value == limit && orEqualTo))
                modValues.add(value);
        
        double[] ret = new double[modValues.size()];
        for(int i = 0; i < modValues.size(); i++){
            ret[i] = (double)modValues.get(i);
        }        
        return ret;
    }

    public static double[] getValuesLessThan(double[] values, double limit, boolean orEqualTo) {
        ArrayList<Double> modValues = new ArrayList<Double>();

        for (double value : values)
            if (value < limit || (value == limit && orEqualTo))
                modValues.add(value);

        double[] ret = new double[modValues.size()];
        for(int i = 0; i < modValues.size(); i++){
            ret[i] = (double)modValues.get(i);
        }        
        return ret;
    }

    public String getFinalPointError(int type, double hitLevel) {
        double[][][] data;
        if(type == TRAIN){
            data = train;
        }
        else{
            data = test;
        }
        StringBuilder outputStr = new StringBuilder();
        // Transpose the matrix to calculate the mean and sd
//        double[][] transposeError = new double[data[0].length+1][data.length];
        double[] totalError = new double[data.length];
        double[] rmse = new double[data.length];
        double[] hits = new double[data.length];
               
        for(int exec = 0; exec < data.length; exec ++){            
            outputStr.append("Execution " + (exec+1) + "\n");
            outputStr.append("input,output,evaluated,error\n");
            for(int j = 0; j < data[exec].length; j++){
                int inputSize = data[exec][j].length;
                double error = Math.abs(data[exec][j][inputSize-1]-data[exec][j][inputSize-2]);
                totalError[exec] += error;
                rmse[exec] += error * error;
                StringBuilder inputAux = new StringBuilder();
                for(int k = 0; k < inputSize - 2; k++){
                    inputAux.append(Utils.printDouble(data[exec][j][k], PRECISION)).append(",");
                }
                outputStr.append(inputAux.toString() + 
                        Utils.printDouble(data[exec][j][inputSize-2], PRECISION) + "," +
                        Utils.printDouble(data[exec][j][inputSize-1], PRECISION) + "," + 
                        Utils.printDouble(error, PRECISION));
                outputStr.append("\n");
//                transposeError[j][exec]= error;
                                
                if(error <= hitLevel)
                    hits[exec]++;
            }
            outputStr.append("Error:," + Utils.printDouble(totalError[exec], PRECISION) +
                    ",Hits:," + Utils.printDouble(hits[exec], PRECISION) + "\n\n");
        }
        outputStr.append("Number of nodes (per execution) \n");
        for(int i = 0; i < solutionSize.length; i++){
            outputStr.append(solutionSize[i] + ",");
        }
        outputStr.append("\n");
        
        double mae[] = new double[data.length];
        for(int i = 0; i < data.length; i++){
            rmse[i] /= data[i].length;
            rmse[i] = Math.sqrt(rmse[i]);
            mae[i] = totalError[i] / data[i].length;
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

    public String getRMSEperIteration(int type) {
        double[][] errorPerIteration;
        if(type == TRAIN){
            errorPerIteration = iterativeTrainingError;
        }
        else{
            errorPerIteration = iterativeTestError;
        }
        StringBuilder outputStr = new StringBuilder();
        outputStr.append("RMSE during iterations (execution x iteration)\n");
        for (double[] byExecution : errorPerIteration) {
            String separator = "";
            for (int j = 0; j < byExecution.length; j++) {
                outputStr.append(separator + Utils.printDouble(byExecution[j], PRECISION));
                separator = ",";
            }
            outputStr.append("\n");
        }
        return outputStr.toString();
    }

    public String getErrorBestOfGeneration() {
        StringBuilder outputStr = new StringBuilder();
        outputStr.append("Sum of squared error (not the mean) of the best individual of each generation (iteration x generation). Relative to the iteration output vector.\n");
        for (double[][] byExecution : bestOfGenError) {
            for (double[] byIteration : byExecution) {
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

    public String getErrorPointPerIteration() {
        StringBuilder outputStr = new StringBuilder();
        outputStr.append("Abolute error (average of executions) for each point per iteration (iteration x point). Relative to the iteration output vector.\n");
        for(int i = 0; i < pontualError.length; i++){
            String separator = "";
            for(int j = 0; j < pontualError[i].length; j++){
                outputStr.append(separator + Utils.printDouble(pontualError[i][j]/train.length, PRECISION));
                separator = ",";
            }
            outputStr.append("\n");
        }
        return outputStr.toString();
    }
}
