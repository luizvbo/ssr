/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ssr.core.SSR2;

import ec.ssr.functions.Function;

/**
 * Solution.java
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class Solution extends ec.ssr.core.SSR1.Solution{
    
    public Solution(Function t1, double tr) {
        super(t1, tr);
    }

    public int getNumberNodes() {
        int total = 0;
        if(t2 != null){
            if(t2 instanceof Solution){
                total += ((Solution)t2).getNumberNodes();
            }
            else{
                total += ((NormFunction)t2).getNumNodes();
                // Add 5, equivalent to +(1-r)*
                total += 5;
            }
        }
        total += ((NormFunction)t1).getNumNodes();
        // Add 2, equivalent to *r
        total += 2;
        return total;
    }
}
