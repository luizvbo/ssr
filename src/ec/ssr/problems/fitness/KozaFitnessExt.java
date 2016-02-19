/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ssr.problems.fitness;

import ec.EvolutionState;
import ec.gp.koza.KozaFitness;

/**
 *
 * @author luiz
 */
public class KozaFitnessExt extends KozaFitness implements FitnessInterface{
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
