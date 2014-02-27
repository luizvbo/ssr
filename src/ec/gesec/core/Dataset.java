/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gesec.core;

import java.util.ArrayList;

/**
 * Dataset.java
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class Dataset {
    /** ID conouter. Used to generate exclusive IDs. */
    private int IDCounter;
    
    /** Array of instances. */
    public ArrayList<Instance> data;

    /**
     * Constructor declaration
     */
    public Dataset() {
        data = new ArrayList<Instance>();
        IDCounter = 0;
    }

    /**
     * Constructor declaration
     * @param dataset Dataset copied to the new one.
     */
    public Dataset(Dataset dataset) {
        IDCounter = dataset.IDCounter;
        data = new ArrayList<Instance>(dataset.data);
    }

    /**
     * Returns the instance at the specified position in this dataset.
     * @param index Index of the element to return
     * @return The element at the specified position in this dataset
     */
    public Instance get(int index){
        return data.get(index);
    }
    
    /**
     * Creates a new instance and add it to the dataset.
     * @param input Instance input
     * @param output Instance output
     */
    public void add(double[] input, Double output) {
        Instance newInstance = new Instance(input, output, IDCounter++);
        data.add(newInstance);
    }
    
    /**
     * Adds an existing instance to the dataset.
     * @param instance Instance to be added
     */
    public void add(Instance instance){
        data.add(instance);
    }
    
    /**
     * Adds a set of instances, in a dataset, to this dataset.
     * @param dataset Set of instances
     */
    public void add(Dataset dataset){
        data.addAll(dataset.data);
    }
    
    /**
     * Returns the size of the dataset.
     * @return The size of the datase.
     */
    public int size(){
        return data.size();
    }
    
    /**
     * Soft clones this object, copying the references to data instances to a 
     * new ArrayList. 
     * @return A Dataset pointing to the instances of this one.
     */
    public Dataset softClone() {
        Dataset newDataset = new Dataset();
        newDataset.data.addAll(data);
        newDataset.IDCounter = this.IDCounter;
        return newDataset;
    }

    /**
     * Checks if there is instances in the dataset.
     * @return True if there is none.
     */
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public String toString() {
        return size() + ""; //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
