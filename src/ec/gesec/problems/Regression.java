/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.gesec.problems;

import ec.gesec.core.Dataset;
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

public class Regression extends GPProblem implements SimpleProblemForm{
    private static final long serialVersionUID = 1;
    
    public double[] currentValue;
    public Dataset dataset;
    public double[] output;
    protected double hitLevel;

    // don't bother cloning the inputs and outputs; they're read-only :-)
    // don't bother cloning the currentValue; it's transitory

    @Override
    public void setup(final EvolutionState state, final Parameter base){
        // very important, remember this
        super.setup(state,base);

        // verify our input is the right class (or subclasses from it)
        if (!(input instanceof RegressionData))
            state.output.fatal("GPData class must subclass from " + RegressionData.class, base.push(P_DATA), null);
    }

    /**
     * Sets the outputs used to calculate the fitness
     * @param output Array of outputs
     */
    public void setOutput(double[] output){
        this.output = output;
    }
    
    /**
     * Sets the dataset used during training
     * @param training Dataset used during training
     */
    public void setDataset(Dataset dataset){
        this.dataset = dataset;
    }
    
    /**
     * Minimum error to consider a hit.
     * @param hitLevel Minimum error
     */
    public void setHitLevel(double hitLevel){
        this.hitLevel = hitLevel;
    }

    @Override
    public void evaluate(final EvolutionState state, final Individual ind, final int subpopulation, final int threadnum){
        if (!ind.evaluated){  // don't bother reevaluating
            RegressionData regressionData = (RegressionData)(this.input);
            
            int hits = 0;
            // Total squared error
            double TSE = 0.0;
            double error;
            double squaredError;
            for (int i = 0; i < dataset.size(); i++){
                currentValue = dataset.get(i).input;
                ((GPIndividual)ind).trees[0].child.eval(
                    state, threadnum, regressionData, stack, ((GPIndividual)ind),this);

                // It's possible to get NaN because cos(infinity) and
                // sin(infinity) are undefined (hence cos(exp(3000)) zings ya!)
                // So since NaN is NOT =,<,>,etc. any other number, including
                // NaN, we're CAREFULLY wording our cutoff to include NaN.
                // Interesting that this has never been reported before to
                // my knowledge.

                
                final double PROBABLY_ZERO = 4.0e-320;
                final double BIG_NUMBER = 1.0e300;  // the same as lilgp uses
//                final double PROBABLY_ZERO = 1.11E-15;
//                final double BIG_NUMBER = 1.0e15;  // the same as lilgp uses

                error = Math.abs(output[i] - regressionData.x);
                
                if (! (error < BIG_NUMBER ) ){   // *NOT* (input.x >= BIG_NUMBER)
                    error = BIG_NUMBER;
                    squaredError = error;
                }
                // very slight math errors can creep in when evaluating
                // two equivalent by differently-ordered functions, like
                // x * (x*x*x + x*x)  vs. x*x*x*x + x*x
                else if (error < PROBABLY_ZERO){  // slightly off
                    error = 0.0;
                    squaredError = 0.0;
                }
                else{
                    squaredError = error * error;
//                    squaredError = error;
                }
                    
                if (error <= hitLevel) hits++;  // whatever!

//                TSE += error;
                TSE += error;
            }
                
            // the fitness better be KozaFitness!
            KozaFitness f = ((KozaFitness)ind.fitness);            
            
//            f.setDoubleFitness(TSE);
            f.setStandardizedFitness(state, TSE);
            f.hits = hits;
            ind.evaluated = true;            
        }
    }

    /**
     * During this step, double fitness are normalized to [0,1] and stored as a float
     */
//    @Override
//    public void finishEvaluating(EvolutionState state, int threadnum) {
////        double smallestFitness = Double.POSITIVE_INFINITY;
//        double biggestFitness = Double.NEGATIVE_INFINITY;
//        for(Individual ind : state.population.subpops[0].individuals){
//            ind.evaluated = false;
////            if(((DoubleKozaFitness)ind.fitness).getDoubleFitness() < smallestFitness){
////                smallestFitness = ((DoubleKozaFitness)ind.fitness).getDoubleFitness();
////            }
//            if(((DoubleKozaFitness)ind.fitness).getDoubleFitness() > biggestFitness){
//                biggestFitness = ((DoubleKozaFitness)ind.fitness).getDoubleFitness();
//            }
//        }  
//        
////        double middleTerm = (biggestFitness + smallestFitness)/2;
//        
//        biggestFitness+=1;
//        
//        // Normalize the error and calculate the finess of each individual
//        for(Individual ind : state.population.subpops[0].individuals){
//            double d_fitness = ((DoubleKozaFitness)ind.fitness).getDoubleFitness();
//            
//            float normalizedFitness = (float)(d_fitness/biggestFitness);
//            
//            float _f = normalizedFitness;
//            if(_f < 0.0f || _f == Float.POSITIVE_INFINITY || Float.isNaN(_f)){
//                int x = 0;
//            }
//                
////            float normalizedFitness = (float)(d_fitness/biggestFitness);
//            ((DoubleKozaFitness)ind.fitness).setStandardizedFitness(state, normalizedFitness);
//            
//        }      
//    }
}
