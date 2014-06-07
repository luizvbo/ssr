/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ssr.core.ParallelVersions.SSR1;

import ec.ssr.core.Dataset;
import ec.ssr.core.Instance;
import ec.ssr.functions.Function;
import ec.ssr.handlers.StatisticsHandler;

/**
 * Solution.java
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class Solution implements Function{
    protected Function t1;
    protected Function t2;
    protected double tr;
    
    public Solution(Function t1, double tr) {
        this.tr = tr;
        this.t1 = t1;
    }
    
    public Solution(Function t){
        this.t1 = t;
    }

    public Function getT2() {
        return t2;
    }

    public Function getT1() {
        return t1;
    }
    
    public void setT2(Function t2) {
        this.t2 = t2;
    }
    
    @Override
    public String print() {
        if(t2 != null)
            return tr + "*(" + t1.print() + ")+\n(1-" + tr + ")*(" + t2.print() + ")";
        return t1.print();
//        return tr + "," + t1.print();
        
//        return "(" + tr + "*" + t1.print() + "+(1-" + tr + ")*" + t2.print() + ")";
    }

    @Override
    public double eval(double[] val) {
        if(t2 == null){
            return t1.eval(val);
        }
        return tr*t1.eval(val) + (1-tr)*t2.eval(val);
    }

    public void test(Dataset training, Dataset test, StatisticsHandler stats) {
        int index = 0;
        stats.newTrainingExecution(training.size());
        for(Instance instance : training.data){
            double output = eval(instance.input);
            stats.addTrainingData(instance.input, instance.output, output, index);
            index++;
        }    
        index = 0;
        stats.newTestExecution(test.size());
        for(Instance instance : test.data){
            double output = eval(instance.input);
            stats.addTestData(instance.input, instance.output, output, index);
            index++;
        }
    }

    @Override
    public int getNumNodes() {
        int total = 0;
        if(t2 != null){
            // Add 5, equivalent to f1 <*> <r> <+> <(1-r)> <*> f2
            total += (t2.getNumNodes() + 5);
        }
            
//            if(t2 instanceof GPNode){
//                total += ((GPNode)t2).numNodes(GPNode.NODESEARCH_ALL);
//                // Add 2, equivalent to 1-r (as a constant) and *
//                total += 2;
//            }
//            else{
//                total += ((Branch)t2).getNumNodes();
//            }
            
//            if(t2 instanceof Solution){
//                total += ((Solution)t2).getNumNodes();
//            }
//            else{
//                total += ((GPNode)t2).numNodes(GPNode.NODESEARCH_ALL);
//                // Add 5, equivalent to +(1-r)*
//                total += 5;
//            }
//        if(t1 instanceof  GPNode){
//            total += ((GPNode)t1).numNodes(GPNode.NODESEARCH_ALL);
//            // Add 2, equivalent to *r
//            total += 2;
//        }
//        else{
//            total += ((Branch)t1).getNumNodes();
//        }
        total += (t1.getNumNodes());
        return total;
    }
}
