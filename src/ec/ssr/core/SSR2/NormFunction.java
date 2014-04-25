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
public class NormFunction implements Function{
    protected Function function;
    protected double mean;
    protected double std;

    public NormFunction(double mean, double std) {
        this.mean = mean;
        this.std = std;
    }

    public void setFunction(Function function) {
        this.function = function;
    }
    
    @Override
    public double eval(double[] val) {
        return (function.eval(val)*(std))+mean;
    }

    @Override
    public String print() {
        return "(" + mean + "+" + function.print() + "*" + std + ")";
    }
    
    public int getNumNodes(){
        return ((GPNode)function).numNodes(GPNode.NODESEARCH_ALL);
    }
}
