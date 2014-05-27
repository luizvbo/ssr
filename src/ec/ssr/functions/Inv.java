/*
  Copyright 2012 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.ssr.functions;
import ec.*;
import ec.app.regression.*;
import ec.gp.*;
import ec.util.*;

/* 
 * Inv.java
 * 
 * Created: Mon Apr 23 17:15:35 EDT 2012
 * By: Sean Luke


 <p>This function appears in the Keijzer function set, and is 1/x.  Note that
 the division is <b>not protected</i>, so 1/0.0 is infinity.
 <p>M. Keijzer. Improving Symbolic Regression with Interval Arithmetic and Linear Scaling. In <i>Proc. EuroGP.</i> 2003.
*/

/**
 * @author Sean Luke
 * @version 1.0 
 */

public class Inv extends GPNode implements Function{
    public String toString() { 
        return "1/"; 
    }

    public int expectedChildren() { 
        return 1; 
    }

    public void eval(final EvolutionState state,
                     final int thread,
                     final GPData input,
                     final ADFStack stack,
                     final GPIndividual individual,
                     final Problem problem) {
        RegressionData rd = ((RegressionData)(input));

        children[0].eval(state,thread,input,stack,individual,problem);
        // Using protected division
        if (rd.x == 0.0){ 
            rd.x = 1.0;
        }
        else{
            rd.x = 1.0 / rd.x;
        }
    }

    @Override
    public double eval(double[] val) {
        // evaluate children[0] to determine if the demoniator is 0
        double denominator = ((Function)children[0]).eval(val);        
        if(denominator == 0.0){
            // the answer is 1.0 since the denominator was 0.0
            return 1.0;
        }
        else{
            return 1.0/denominator;
        }
    }

    @Override
    public String print() {
        return "(1/" + ((Function)children[0]).print() + ")";
    }
    
    @Override
    public int getNumNodes() {
        return numNodes(GPNode.NODESEARCH_ALL);
    }
}



