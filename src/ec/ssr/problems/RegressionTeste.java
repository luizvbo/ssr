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

public class RegressionTeste extends Regression{
    @Override
    public void setOutput(double[] output){
        this.output = output;
    }   

    @Override
    public void evaluate(final EvolutionState state, final Individual ind, final int subpopulation, final int threadnum){
        if (!ind.evaluated){  // don't bother reevaluating
            RegressionData input = (RegressionData)(this.input);

            int hits = 0;
            double sum = 0.0;
            double result;
            for (int i=0; i < dataset.size(); i++)
                {
                currentValue = dataset.get(i).input;
                ((GPIndividual)ind).trees[0].child.eval(
                    state,threadnum,input,stack,((GPIndividual)ind),this);

                // It's possible to get NaN because cos(infinity) and
                // sin(infinity) are undefined (hence cos(exp(3000)) zings ya!)
                // So since NaN is NOT =,<,>,etc. any other number, including
                // NaN, we're CAREFULLY wording our cutoff to include NaN.
                // Interesting that this has never been reported before to
                // my knowledge.

                final double HIT_LEVEL = 0.01;
                final double PROBABLY_ZERO = 1.11E-15;
                final double BIG_NUMBER = 1.0e15;  // the same as lilgp uses

                result = Math.abs(output[i] - input.x);

                if (! (result < BIG_NUMBER ) )   // *NOT* (input.x >= BIG_NUMBER)
                    result = BIG_NUMBER;

                // very slight math errors can creep in when evaluating
                // two equivalent by differently-ordered functions, like
                // x * (x*x*x + x*x)  vs. x*x*x*x + x*x

                else if (result<PROBABLY_ZERO)  // slightly off
                    result = 0.0;
                    
                if (result <= HIT_LEVEL) hits++;  // whatever!

                sum += result;              
                }
                
            // the fitness better be KozaFitness!
            KozaFitness f = ((KozaFitness)ind.fitness);
            f.setStandardizedFitness(state,(float)sum);
            f.hits = hits;
            ind.evaluated = true;
        }
    }
}
