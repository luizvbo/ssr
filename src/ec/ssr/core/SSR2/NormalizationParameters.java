/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ssr.core.SSR2;

/**
 *
 * @author luiz
 */
public class NormalizationParameters {
    protected double mean;
    protected double std;

    public NormalizationParameters(double mean, double std) {
        this.mean = mean;
        this.std = std;
    }
    
    public double getMean() {
        return mean;
    }

    public double getStd() {
        return std;
    }
}
