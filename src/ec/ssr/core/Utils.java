/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ssr.core;

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
}
