/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.ssr.problems.fitness;

import ec.EvolutionState;
import ec.gp.koza.KozaFitness;

public class GPSOFitness extends KozaFitness implements FitnessInterface{
    @Override
    public float getError() {
        return standardizedFitness();
    }

    @Override
    public void setError(EvolutionState state, float error) {
        setStandardizedFitness(state, standardizedFitness);
    }

    /**
     * Override isPerfectFitness from the interface
     * @param numHits It's used to check if we reach the minimum number of hits
     * @return True if the ideal fitness or the number of hits are reached 
     */
    @Override
    public boolean isPerfectFitness(int numHits) {
        if(super.isIdealFitness() || this.hits >= numHits)
            return true;
        return false;
    } 
}
