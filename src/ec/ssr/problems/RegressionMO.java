/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.ssr.problems;

import ec.ssr.core.Dataset;
import ec.util.*;
import ec.*;
import ec.app.regression.RegressionData;
import ec.gp.*;
import ec.gp.koza.*;
import ec.simple.*;
import ec.ssr.functions.Function;
import ec.ssr.problems.fitness.FitnessInterface;
import ec.ssr.problems.fitness.GPMOFitness;

/* 
 * Regression.java
 * 
 * Created: Mon Nov  1 15:46:19 1999
 * By: Sean Luke
 */

/**
 * Regression implements the Koza (quartic) Symbolic Regression problem.
 *
 * <p>This equation was introduced in J. R. Koza, GP II, 1994.
 *
 <p><b>Parameters</b><br>
 <table>
 <tr><td valign=top><i>base</i>.<tt>data</tt><br>
 <font size=-1>classname, inherits or == ec.app.regression.RegressionData</font></td>
 <td valign=top>(the class for the prototypical GPData object for the Regression problem)</td></tr>
 <tr><td valign=top><i>base</i>.<tt>size</tt><br>
 <font size=-1>int >= 1</font></td>
 <td valign=top>(the size of the training set)</td></tr>
 </table>

 <p><b>Parameter bases</b><br>
 <table>
 <tr><td valign=top><i>base</i>.<tt>data</tt></td>
 <td>species (the GPData object)</td></tr>
 </table>
 *
 * @author Sean Luke
 * @version 1.0 
 */

public class RegressionMO extends Regression{
    @Override
    public void evaluate(final EvolutionState state, final Individual ind, final int subpopulation, final int threadnum){
        if (!ind.evaluated){  // don't bother reevaluating
            RegressionData regressionData = (RegressionData)(this.input);
            
            // Total squared error
            double TSE = 0.0;
            double error;
            double squaredError;
            boolean overflow = false;
            for (int i = 0; i < dataset.size() && !overflow; i++){
                currentValue = dataset.get(i).input;
                ((GPIndividual)ind).trees[0].child.eval(
                    state, threadnum, regressionData, stack, ((GPIndividual)ind),this);

                // It's possible to get NaN because cos(infinity) and
                // sin(infinity) are undefined (hence cos(exp(3000)) zings ya!)
                // So since NaN is NOT =,<,>,etc. any other number, including
                // NaN, we're CAREFULLY wording our cutoff to include NaN.
                // Interesting that this has never been reported before to
                // my knowledge.
                final double PROBABLY_ZERO = 1.4e-45;
                final double BIG_NUMBER = 3.4e+38;

                error = output[i] - regressionData.x;
                squaredError = error * error;
                
                if (squaredError < PROBABLY_ZERO){  // slightly off
                    squaredError = 0.0;
                }
                if(Double.isInfinite(squaredError) || Double.isNaN(squaredError) || squaredError >= BIG_NUMBER){
                    overflow = true;
                }
                else{
                    TSE += squaredError;
                }
            }
                
            // the fitness better be KozaFitness!
            GPMOFitness f = ((GPMOFitness)ind.fitness);            
            if(overflow)
                f.setError(state, Float.MAX_VALUE);
            else
                f.setError(state, (float)Math.sqrt(TSE/dataset.size()));
            
            f.setSize(((GPIndividual)ind).trees[0].child.numNodes(GPNode.NODESEARCH_ALL));
//            f.hits = hits;
            ind.evaluated = true;            
        }
    }
}
