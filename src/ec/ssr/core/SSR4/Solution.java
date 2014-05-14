/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ssr.core.SSR4;

import ec.ssr.core.SSR2.NormalizationParameters;
import ec.ssr.functions.Function;

/**
 *
 * @author luiz
 */
public class Solution extends ec.ssr.core.SSR1.Solution{
    protected NormalizationParameters parameters;
    
    private Solution(Function t1, double tr) {
        super(t1, tr);
    }
    
    public static Solution createSolution(Function t1, double tr, NormalizationParameters parameters){
        Solution newSolution = new Solution(t1, tr);
        newSolution.parameters = parameters;
        return newSolution;
    }
    
    public static Solution createSolution(Function t1, NormalizationParameters parameters){
        return createSolution(t1, 1, parameters);
    }
    
    @Override
    public double eval(double[] val) {
        if(t2 == null){
            return unNormalize(t1.eval(val));
        }
        return tr*unNormalize(t1.eval(val)) + (1-tr)*unNormalize(t2.eval(val));
    }
    
    private double unNormalize(double output){
        return (output * parameters.getStd()) + parameters.getMean();
    }
    
    private String unNormalized2String(Function f){
        return parameters.getStd() + "*(" + f.print() +")+" + parameters.getMean();
    }
    
    @Override
    public String print() {
        if(t2 != null)
            return tr + "*("  + unNormalized2String(t1) + ")+\n" + "(1-" + tr+ ")*(" + unNormalized2String(t2) + ")";
        return tr + "*(" + unNormalized2String(t1) + ")";
        
//        return "(" + tr + "*" + t1.print() + "+(1-" + tr + ")*" + t2.print() + ")";
    }
    
//    public int getNumberNodes() {
//        int total = 0;
//        if(t2 != null){
//            if(t2 instanceof Solution){
//                total += ((Solution)t2).getNumberNodes();
//            }
//            else{
//                total += ((NormalizedFunction)t2).getNumNodes();
//                // Add 5, equivalent to +(1-r)*
//                total += 5;
//            }
//        }
//        total += ((NormalizedFunction)t1).getNumNodes();
//        // Add 2, equivalent to *r
//        total += 2;
//        return total;
//    }
}
