/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ssr.core.SSR2;

import ec.gp.GPNode;
import ec.ssr.functions.Function;

/**
 *
 * @author luiz
 */
public class NormalizedFunction implements Function{
    protected Function function;
    protected NormalizationParameters parameters; 

    public NormalizedFunction(double mean, double std) {
        parameters = new NormalizationParameters(mean, std);
    }

    public NormalizedFunction(Function function, NormalizationParameters parameters) {
        this.function = function;
        this.parameters = parameters;
    }

    public void setFunction(Function function) {
        this.function = function;
    }
    
    public double unormalizedEval(double[] val) {
        return function.eval(val);
    }
    
    @Override
    public double eval(double[] val) {
        return (function.eval(val)*(parameters.std))+parameters.mean;
    }

    @Override
    public String print() {
        return "(" + parameters.std + "*(" + function.print() + ")+" + parameters.mean + ")";
    }
    
    @Override
    public int getNumNodes(){
        return function.getNumNodes();
    }
}
