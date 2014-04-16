/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.gesec.core.SSR2;

import ec.gesec.functions.Function;

/**
 *
 * @author luiz
 */
public class NLBoundFunction implements Function{
    protected Function function;
    protected double lBound;
    
    @Override
    public double eval(double[] val) {
        return function.eval(val)+lBound;
    }

    @Override
    public String print() {
        return "(" + function.print() + "+" + lBound + ")";
    }
    
}
