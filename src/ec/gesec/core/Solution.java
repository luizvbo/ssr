/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gesec.core;

import ec.gesec.functions.Function;
import ec.gesec.handlers.StatisticsHandler;

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
        return "(" + tr + "*" + t1.print() + "+(1-" + tr + ")*" + t2.print() + ")";
    }

    @Override
    public double eval(double[] val) {
        if(tr == 1){
            return t1.eval(val);
        }
        return tr*t1.eval(val) + (1-tr)*t2.eval(val);
//        return tr*t1.eval(val) + (1-tr)*t2.eval(val);
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
}
