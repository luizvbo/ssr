/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ssr.core;

/**
 *
 * @author luiz
 */
public class Bounds {
    public double upperBound;
    public double lowerBound;
    
    public Bounds() {}

    public Bounds(double lowerBound, double upperBound) {
        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
    }
    
    public static Bounds createStartBounds(){
        Bounds bounds = new Bounds();
        bounds.upperBound = Double.NEGATIVE_INFINITY;
        bounds.lowerBound = Double.POSITIVE_INFINITY;
        return bounds;
    }
    
    public static Bounds createNaNBounds(){
        Bounds bounds = new Bounds();
        bounds.upperBound = Double.NaN;
        bounds.lowerBound = Double.NaN;
        return bounds;
    }

    public boolean isInsideBounds() {
        if(!(Math.abs(upperBound) < Double.MAX_VALUE)) return false;
        if(!(Math.abs(lowerBound) < Double.MAX_VALUE)) return false;
        return true;
    }
}
