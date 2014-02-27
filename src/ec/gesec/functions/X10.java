/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.gesec.functions;

import ec.*;
import ec.app.regression.RegressionData;
import ec.gesec.problems.Regression;
import ec.gp.*;

/* 
 * X.java
 * 
 * Created: Wed Nov  3 18:26:37 1999
 * By: Sean Luke
 */

/**
 * @author Sean Luke
 * @version 1.0 
 */

public class X10 extends GPNode implements Function{
    public String toString() {
        return "x10"; 
    }

    public int expectedChildren() {
        return 0; 
    }

    public void eval(final EvolutionState state,
                     final int thread,
                     final GPData input,
                     final ADFStack stack,
                     final GPIndividual individual,
                     final Problem problem){
            RegressionData rd = ((RegressionData)(input));
            rd.x = ((Regression)problem).currentValue[9];    
    }
    
    @Override
    public double eval(double[] x){
        return x[9];
    }

    @Override
    public String print() {
        return "x10";
    }
}



