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
public class NRangeFunction implements Function{
    protected Function function;
    protected double min;
    protected double max;

    public NRangeFunction(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public void setFunction(Function function) {
        this.function = function;
    }
    
    @Override
    public double eval(double[] val) {
        return (function.eval(val)*(max-min))+min;
    }

    @Override
    public String print() {
        return "(" + min + "+" + function.print() + "*" + (max-min) + ")";
    }
    
    public int getNumNodes(){
        return ((GPNode)function).numNodes(GPNode.NODESEARCH_ALL);
    }
}
