/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.ssr.problems;

import ec.*;
import ec.app.regression.RegressionData;
import ec.gp.*;
import ec.gp.koza.*;
import ec.ssr.core.Dataset;
import ec.ssr.core.Instance;
import java.util.HashMap;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;


/**
 * The same as Regression, but with random sampling technique (RST)
 * @author Luiz Otavio
 * @version 1.0 
 */
public class RegressionRST_corr extends RegressionRST2{ 
    
    @Override
    public void evaluate(final EvolutionState state, final Individual ind, final int subpopulation, final int threadnum){
        RegressionData regressionData = (RegressionData)(this.input);
        // Total squared error

        
        // Output vector (desired)
        double[] t = new double[dataset.size()];
        // Evaluated output
        double[] fx = new double[dataset.size()];
//        
//        double fx_mean = 0;
//        double t_mean = 0;
//        
        for (int i = 0; i < dataset.size(); i++){
            Instance instance = dataset.get(i);
            currentValue = instance.input;
            ((GPIndividual)ind).trees[0].child.eval(
                state, threadnum, regressionData, stack, ((GPIndividual)ind),this);

            t[i] = outputMap.get(instance.id);
//            t_mean += t[i];
            fx[i] = regressionData.x;
//            fx_mean = fx[i];
        }
//        fx_mean /= fx.length;
//        t_mean /= t.length;    
//        
//        double sum_num = 0; // sum [(xi-x.mean)(yi-y.mean)
//        double fx_squared = 0; // sum (xi-x.mean)^2
//        double t_squared = 0; // sum (yi-y.mean)^2
//        
//        for(int i = 0; i < dataset.size(); i++){
//            double fx_temp = fx[i]-fx_mean;
//            double t_temp = t[i]-t_mean;
//            
//            sum_num += fx_temp * t_temp;
//            fx_squared += fx_temp * fx_temp;
//            t_squared += t_temp * t_temp;
//        }

        PearsonsCorrelation pearsonCorr = new PearsonsCorrelation();
        double corr = pearsonCorr.correlation(t, fx);
        if(Double.isNaN(corr) || Double.isInfinite(corr)){
            corr = -1;
        }
        
        // the fitness better be KozaFitness!
        KozaFitness f = ((KozaFitness)ind.fitness);            
//        double corr = sum_num/(Math.sqrt(fx_squared)*Math.sqrt(t_squared));
//        f.setStandardizedFitness(state, (float)(corr+1));
        f.setStandardizedFitness(state, (float)(1-corr));
        f.hits = 0;
        ind.evaluated = true;            
    }
}
