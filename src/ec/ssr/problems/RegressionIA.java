/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ssr.problems;

import ec.EvolutionState;
import ec.Individual;
import ec.app.regression.RegressionData;
import ec.gp.GPIndividual;
import ec.gp.koza.KozaFitness;
import ec.ssr.core.Bounds;
import ec.ssr.functions.FunctionIA;


/**
 *
 * @author luiz
 */
public class RegressionIA extends Regression{
    public int nOverflow;
    public int nEval;
    
    @Override
    public void setOutput(double[] output){
        this.output = output;
        
        nOverflow = 0;
        nEval = 0;
    }

    @Override
    public Object clone() {
        Object o = super.clone();
        ((RegressionIA)o).nEval = nEval;
        ((RegressionIA)o).nOverflow = nOverflow;
        return o;
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
            boolean overflow = false;
            // Test individual boundsnroOverflow
            Bounds bounds = ((FunctionIA)(((GPIndividual)ind).trees[0].child)).getBounds(dataset.getBounds());
            // If the individual bounds is as expected, continue to evaluate. Otherwise, treats as an overflow
            if(bounds.isInsideBounds()){
                for (int i = 0; i < dataset.size() && !overflow; i++){
                    currentValue = dataset.get(i).input;
                    ((GPIndividual)ind).trees[0].child.eval(
                        state, threadnum, regressionData, stack, ((GPIndividual)ind), this);

                    // It's possible to get NaN because cos(infinity) and
                    // sin(infinity) are undefined (hence cos(exp(3000)) zings ya!)
                    // So since NaN is NOT =,<,>,etc. any other number, including
                    // NaN, we're CAREFULLY wording our cutoff to include NaN.
                    // Interesting that this has never been reported before to
                    // my knowledge.
                    final double PROBABLY_ZERO = 1.4e-45;
                    final double BIG_NUMBER = 3.4e+38;

                    error = Math.abs(output[i] - regressionData.x);
                    squaredError = error * error;

                    if (squaredError < PROBABLY_ZERO){  // slightly off
                        squaredError = 0.0;
                    }
                    if(Double.isInfinite(squaredError) || Double.isNaN(squaredError) || squaredError >= BIG_NUMBER){
                        overflow = true;
                        hits = 0;
                    }
                    else{
                        if (error <= hitLevel) hits++;  // whatever!
                        TSE += squaredError;
                    }
                }
            }
            else{
                overflow = true;
                
                nOverflow++;
              
            }
            
            nEval++;
            
            // the fitness better be KozaFitness!
            KozaFitness f = ((KozaFitness)ind.fitness);
            if(overflow)
                f.setStandardizedFitness(state, Float.MAX_VALUE);
            else
                f.setStandardizedFitness(state, (float)Math.sqrt(TSE/dataset.size()));
            f.hits = hits;
            ind.evaluated = true;            
        }
    }    
}
