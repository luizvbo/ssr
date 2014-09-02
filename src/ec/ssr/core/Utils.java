/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ssr.core;

import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.ssr.functions.Function;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 *
 * @author luiz
 */
public class Utils {
     public static double[] sumDoubleArray(double[] a, double[] b){
        double[] c = new double[Math.max(a.length, b.length)];
        int maxIteration = Math.min(a.length, b.length);
        for(int i = 0; i < maxIteration; i++){
            c[i] = a[i] + b[i];
        }
        return c;
    }
     
    public static double[] doubleListToArray(ArrayList<Double> a){
        double[] output = new double[a.size()];
        for(int i = 0; i < output.length; i++)
            output[i] = a.get(i);
        return output;
    }
    
    public static String printDouble(double number, int precision){
        String s_precision = "";
        for(int i = 0; i < precision; i++) s_precision += "#";
        DecimalFormat formatter = new DecimalFormat("#." + s_precision, new DecimalFormatSymbols(Locale.ENGLISH));
        if(Double.isNaN(number)){
            return "NaN";
        }
        if(Double.isInfinite(number)){
            return formatter.format(Double.MAX_VALUE);
        }
        return formatter.format(number);
    }
    
    public static double getMedian(double[] array) {
        double[] auxArray = Arrays.copyOf(array, array.length);
        Arrays.sort(auxArray);
        // Even number
        if(auxArray.length % 2 == 0){
            int secondElement = auxArray.length / 2;
            return (auxArray[secondElement-1]+auxArray[secondElement])/2;
        }
        else{
            int element = (auxArray.length-1)/2;
            return auxArray[element];
        }
    }
    
    public static double getMean(double[] data){
        double sum = 0;
        for(int i = 0; i < data.length; i++){
            sum += data[i];
        }
        return sum / data.length; 
    }
    
    public static double getSD(double[] data, double mean){
        double sum = 0;
        for(int i = 0; i < data.length; i++){
            sum += (data[i]-mean) * (data[i]-mean);
        }
        return Math.sqrt(sum/(data.length-1));
    }
    
    /**
     * Returns the max and min value four inputs
     * @param a Input
     * @param b Input
     * @param c Input
     * @param d Input
     * @return The max and min values as upper and lower bounds
     */
    public static Bounds getBounds(double a, double b, double c, double d){
        Bounds bounds = new Bounds();
        Bounds auxBounds = new Bounds();
        if(a > b){
            bounds.lowerBound = b;
            bounds.upperBound = a;
        }
        else{   
            bounds.lowerBound = a;
            bounds.upperBound = b;
        }
        if(c > d){
            auxBounds.lowerBound = d;
            auxBounds.upperBound = c;
        }
        else{
            auxBounds.lowerBound = c;
            auxBounds.upperBound = d;
        }
        if(auxBounds.upperBound > bounds.upperBound)
            bounds.upperBound = auxBounds.upperBound;
        if(auxBounds.lowerBound < bounds.lowerBound)
            bounds.lowerBound = auxBounds.lowerBound;
        return bounds;
    }
    
    /**
     * Calculate the MSE, given a solution object and a datase
     * @param solution Solution objetc
     * @param dataset Dataset used to calculate the error
     * @return Total error
     */
    public static final double getRMSE(Function solution, Dataset dataset) {
        double totalError = 0;
        for(Instance instance : dataset.data){
            double evaluated = solution.eval(instance.input);
            double error = evaluated - instance.output;
            totalError += error * error;
        }
        return Math.sqrt(totalError/dataset.data.size());
    }
    
    /**
     * Generates a array with the expected outputs of a dataset, respecting the order
     * @param dataset Input dataset
     * @return Double array with the outputs with the same order of the dataset
     */
    public static final double[] getDatasetOutputs(Dataset dataset) {
        double[] output = new double[dataset.size()];
        int i = 0;
        for(Instance instance : dataset.data){
            output[i++] = instance.output;
        }
        return output;
    }
    
    /**
     * Find the best individual from the last population of an EvolutionState
     * @param state EvolutionState
     * @return The best GPIndividual (casted)
     */
    public static final GPIndividual getBestIndividual(EvolutionState state){
        Individual best_i = state.population.subpops[0].individuals[0];
        for(int i = 1; i < state.population.subpops[0].individuals.length; i++){
            if (state.population.subpops[0].individuals[i].fitness.betterThan(best_i.fitness))
                best_i = state.population.subpops[0].individuals[i];
        }
        return (GPIndividual)best_i;
    }
}
