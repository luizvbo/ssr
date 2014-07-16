/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ssr.core.ParallelVersions.SSR7;

import ec.ssr.core.Dataset;
import ec.ssr.core.Instance;
import ec.ssr.functions.Function;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

/**
 *
 * @author luiz
 */
public class Solution implements Function{
    protected ArrayList<Function> functions;
    protected double[] coefficients;
    protected double[] error;
    
    public Solution(Function f, Dataset training) {
        functions = new ArrayList<Function>();
        // We hava b0+b1*f1
        functions.add(f);
        adjustCoefficients(training);
    }
    
    @Override
    public double eval(double[] val) {
        double result = coefficients[0];
        int index = 1;
        for(Function f : functions){
            result += coefficients[index++]*f.eval(val);
        }
        return result;
    }    
    
    @Override
    public String print() {
        StringBuilder strOutput = new StringBuilder(coefficients[0] + " + ");
        int i = 1;
        for(Function f : functions){
            strOutput.append(coefficients[i++] + "*(" + f.print() +")");
        }
//        return t1.print();
        return strOutput.toString();
    }

    private void adjustCoefficients(Dataset training) {
        OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
        double[] y = new double[training.size()];
        double[][] x = new double[training.size()][functions.size()];
        
        int i = 0;
        for(Instance instance : training.data){
            y[i] = instance.output;
            int j = 0;
            for(Function f : functions){
                x[i][j++] = f.eval(instance.input);
            }
            i++;
        }
        regression.newSampleData(y, x);
        try{
            coefficients = regression.estimateRegressionParameters();            
            error = regression.estimateResiduals();
        }
        catch(SingularMatrixException e){
//            coefficients = new double[functions.size()+1];
//            for(i = 0; i < coefficients.length; i++) coefficients[i] = 1;
            coefficients = Arrays.copyOf(coefficients, coefficients.length+1);
            error = new double[training.size()];
            i = 0;
            for(Instance instance : training.data){
                error[i++] = instance.output - this.eval(instance.input);
            }
        }
    }

    public void addFunction(Function f, Dataset training){
        functions.add(f);
        adjustCoefficients(training);
    }
    
    @Override
    public int getNumNodes() {
        int numNodes = 0;
        for(Function f : functions){
            numNodes += f.getNumNodes();
        }
        return (coefficients.length - 1)*3 + numNodes;
    }

    public double[] getError() {
        return error;
    }
}
