/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gesec.core.SSR1;

import ec.gesec.core.Dataset;
import ec.gesec.core.Instance;
import ec.gesec.functions.Function;
import ec.gesec.handlers.StatisticsHandler;
import ec.gp.GPNode;

/**
 * Solution.java
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class Solution implements Function{
    protected Function t1;
    protected Function t2;
    private double tr;
    
    public Solution(Function t1, double tr) {
        this.tr = tr;
        this.t1 = t1;
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
            return tr + "," + t1.print() + "\n" + t2.print();
        return tr + "," + t1.print();
        
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

    public int getNumberNodes() {
        int total = 0;
        if(t2 != null){
            if(t2 instanceof Solution){
                total += ((Solution)t2).getNumberNodes();
            }
            else{
                total += ((GPNode)t2).numNodes(GPNode.NODESEARCH_ALL);
                // Add 5, equivalent to +(1-r)*
                total += 5;
            }
        }
        total += ((GPNode)t1).numNodes(GPNode.NODESEARCH_ALL);
        // Add 2, equivalent to *r
        total += 2;
        return total;
    }
}
