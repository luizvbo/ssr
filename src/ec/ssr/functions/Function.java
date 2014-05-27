/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ssr.functions;

/**
 *
 * @author luiz
 */
public interface Function {
    public double eval(double[] val);
    
    public String print();
    
    public int getNumNodes();
}
