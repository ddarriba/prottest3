/*
Copyright (C) 2009  Diego Darriba

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package es.uvigo.darwin.prottest.util.collection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;

import pal.alignment.Alignment;
import pal.tree.Tree;
import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.util.comparator.ModelWeightComparator;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;
import es.uvigo.darwin.prottest.util.factory.ProtTestFactory;

/**
 * An common interface for substitution models, that includes
 * some specific operations to work with model collections.  
 * 
 * @author Diego Darriba
 * @version 3.0
 */
public abstract class ModelCollection implements List<Model>, Serializable {

    protected Alignment alignment;
    /** The serialVersionUID. */
    private static final long serialVersionUID = 515892620727410572L;
    /** The set of all models inside the collection. */
    protected List<Model> allModels;

    public Alignment getAlignment() {
        return alignment;
    }

    /**
     * Instantiates a new model collection.
     */
    public ModelCollection(Alignment alignment) {
        this.alignment = alignment;
        allModels = new ArrayList<Model>();
    }

    /**
     * Instantiates a new model collection with the array of models.
     */
    public ModelCollection(Model[] models, Alignment alignment) {
        this.alignment = alignment;
        for (Model model : models) {
            checkAlignment(model);
        }
        allModels = new ArrayList<Model>();
        allModels.addAll(Arrays.asList(models));
    }

    /**
     * Instantiates a new model collection with another model collection.
     */
    public ModelCollection(ModelCollection mc) {
        this.alignment = mc.getAlignment();
        allModels = new ArrayList<Model>();
        allModels.addAll(mc);
    }

    /**
     * Creates a new model and adds it to the collection.
     *
     * @param matrix the substitution matrix name
     * @param distribution the distribution of the model
     * @param modelProperties the model specific properties
     *
     * @return true, if successfully added the model to the iterator
     */
    public boolean addModel(String matrix, int distribution, Properties modelProperties,
            Alignment alignment, Tree tree, int ncat) {
        Model model = ProtTestFactory.getInstance().createModel(matrix, distribution, modelProperties,
                alignment, tree, ncat);

        boolean toAdd = !allModels.contains(model);
        if (toAdd) {
            checkAlignment(model);
            toAdd &= allModels.add(model);
        }

        return toAdd;
    }

    /**
     * Adds a substitution model to the collection.
     *
     * @param model the substitution model to add
     *
     * @return true, if successfully added the model to the iterator
     */
    public boolean addModel(Model model) {
        boolean toAdd = !allModels.contains(model);
        if (toAdd) {
            checkAlignment(model);
            toAdd &= allModels.add(model);
        }

        return toAdd;
    }

    /**
     * Adds the models result of combine each matrix with each distribution.
     * There will be MxD new models in the iterator, where
     * M is the number of matrices
     * D is the number of distributions
     *
     * @param matrices collection of all the substitution matrices
     * @param distributions collection of all the distributions
     * @param modelProperties the model properties
     *
     * @return true, if successfully added all models
     */
    public boolean addModelCartesian(Collection<String> matrices, Collection<Integer> distributions,
            Properties modelProperties, Alignment alignment, Tree tree, int ncat) {
        boolean allDone = true;
        boolean plusF = Boolean.parseBoolean(
                modelProperties.getProperty(
                Model.PROP_PLUS_F, "false"));
        Properties modelPropertiesFalse = new Properties();
        Properties modelPropertiesTrue = new Properties();
        modelPropertiesFalse.setProperty(Model.PROP_PLUS_F, "false");
        modelPropertiesTrue.setProperty(Model.PROP_PLUS_F, "true");
        for (String matrix : matrices) {
            for (Integer distribution : distributions) {
                allDone &= addModel(matrix, distribution, modelPropertiesFalse,
                        alignment, tree, ncat);
                if (plusF) {
                    allDone &= addModel(matrix, distribution, modelPropertiesTrue,
                            alignment, tree, ncat);
                }
            }
        }
        return allDone;
    }

    /**
     * Gets the weight of the collection. This attribute is only used in
     * order to heuristically sort models.
     *
     * @return the whole weight of the collection
     */
    public int getWeight(ModelWeightComparator mwc) {
        int weight = 0;
        for (Model model : allModels) {
            weight += mwc.getWeight(model);
        }
        return weight;
    }

    /* (non-Javadoc)
     * @see java.util.List#size()
     */
    public int size() {
        return allModels.size();
    }

//	public abstract void printModelsSorted(char sortBy, PrintWriter out) 
//	throws ProtTestInternalException;

    /* (non-Javadoc)
     * @see java.util.List#add(java.lang.Object)
     */
    public boolean add(Model e) {
        return allModels.add(e);
    }

    /* (non-Javadoc)
     * @see java.util.List#add(int, java.lang.Object)
     */
    public void add(int index, Model element) {
        allModels.add(index, element);
    }

    /* (non-Javadoc)
     * @see java.util.List#addAll(java.util.Collection)
     */
    public boolean addAll(Collection<? extends Model> c) {
        return allModels.addAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.List#addAll(int, java.util.Collection)
     */
    public boolean addAll(int index, Collection<? extends Model> c) {
        return allModels.addAll(index, c);
    }

    /* (non-Javadoc)
     * @see java.util.List#clear()
     */
    public void clear() {
        allModels.clear();
    }

    /* (non-Javadoc)
     * @see java.util.List#contains(java.lang.Object)
     */
    public boolean contains(Object o) {
        return allModels.contains(o);
    }

    /* (non-Javadoc)
     * @see java.util.List#containsAll(java.util.Collection)
     */
    public boolean containsAll(Collection<?> c) {
        return allModels.containsAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.List#get(int)
     */
    public Model get(int index) {
        return allModels.get(index);
    }

    /* (non-Javadoc)
     * @see java.util.List#indexOf(java.lang.Object)
     */
    public int indexOf(Object o) {
        return allModels.indexOf(o);
    }

    /* (non-Javadoc)
     * @see java.util.List#isEmpty()
     */
    public boolean isEmpty() {
        return allModels.isEmpty();
    }

    /* (non-Javadoc)
     * @see java.util.List#iterator()
     */
    public Iterator<Model> iterator() {
        return allModels.iterator();
    }

    /* (non-Javadoc)
     * @see java.util.List#lastIndexOf(java.lang.Object)
     */
    public int lastIndexOf(Object o) {
        return allModels.lastIndexOf(o);
    }

    /* (non-Javadoc)
     * @see java.util.List#listIterator()
     */
    public ListIterator<Model> listIterator() {
        return allModels.listIterator();
    }

    /* (non-Javadoc)
     * @see java.util.List#listIterator(int)
     */
    public ListIterator<Model> listIterator(int index) {
        return allModels.listIterator(index);
    }

    /* (non-Javadoc)
     * @see java.util.List#remove(java.lang.Object)
     */
    public boolean remove(Object o) {
        return allModels.remove(o);
    }

    /* (non-Javadoc)
     * @see java.util.List#remove(int)
     */
    public Model remove(int index) {
        return allModels.remove(index);
    }

    /* (non-Javadoc)
     * @see java.util.List#removeAll(java.util.Collection)
     */
    public boolean removeAll(Collection<?> c) {
        return allModels.removeAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.List#retainAll(java.util.Collection)
     */
    public boolean retainAll(Collection<?> c) {
        return allModels.retainAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.List#set(int, java.lang.Object)
     */
    public Model set(int index, Model element) {
        return allModels.set(index, element);
    }

    /* (non-Javadoc)
     * @see java.util.List#subList(int, int)
     */
    public List<Model> subList(int fromIndex, int toIndex) {
        return allModels.subList(fromIndex, toIndex);
    }

    /* (non-Javadoc)
     * @see java.util.List#toArray()
     */
    public Object[] toArray() {
        return allModels.toArray();
    }

    /* (non-Javadoc)
     * @see java.util.List#toArray(T[])
     */
    public <T> T[] toArray(T[] a) {
        return allModels.toArray(a);
    }

    private void checkAlignment(Model model) {
        if (this.alignment == null) {
            throw new ProtTestInternalException("Internal error: Alignment is not initialized");
        } else if (!model.checkAlignment(this.alignment)) {
            throw new ProtTestInternalException("Different alignments among model collection");
        }
    }

    @Override
    public ModelCollection clone() {
        return new SingleModelCollection(this);
    }
}
