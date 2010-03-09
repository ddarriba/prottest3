package es.uvigo.darwin.prottest.util.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import pal.alignment.Alignment;
import pal.tree.Tree;
import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.util.comparator.ModelWeightComparator;

/**
 * A complex implementation of model collection, which is able to
 * distribute whole set of substitution models amongst a certain
 * number of groups, generally amongst the number of processors.
 * 
 * This class is a composition of model collections, that will have
 * disjunction and completeness properties.
 */
public class ParallelModelCollection extends ModelCollection {

	/** The serialVersionUID. */
	private static final long serialVersionUID = 20090903L;
	
	/** The list of model collections. */
	private List<ModelCollection> modelCollections;
	
	/** The number of groups. */
	private int groups;
	
	/** Defines if the current distribution is valid. */
	private boolean validDistribution = false;
	
	/** The heuristic algorithm to compare models. */
	private ModelWeightComparator comparator;
	
	/**
	 * Instantiates a new parallel model collection.
	 * 
	 * @param allModels the whole set of models
	 * @param groups the number of groups, typically the size of the MPJ communicator
	 */
	public ParallelModelCollection(Model[] allModels, int groups, ModelWeightComparator comparator) {
		ArrayList<Model> models = new ArrayList<Model>(Arrays.asList(allModels));
		this.comparator = comparator;
		Collections.sort(models, comparator);
		initialize(models, groups);
	}
	
	/**
	 * Instantiates a new parallel model collection.
	 * 
	 * @param allModels the whole set of models.
	 * @param groups the number of groups, typically the size of the MPJ communicator
	 */
	public ParallelModelCollection(ModelCollection allModels, int groups, ModelWeightComparator comparator) {
		List<Model> models = new ArrayList<Model>(allModels);
		this.comparator = comparator;
		Collections.sort(models, comparator);
		initialize(models, groups);
	}
	
	/**
	 * Initialize.
	 * 
	 * @param allModels the whole set of models
	 * @param groups the number of groups, typically the size of the MPJ communicator
	 */
	private void initialize(List<Model> allModels, int groups) {
		this.allModels = allModels;
		this.groups = groups;
		modelCollections = new ArrayList<ModelCollection>();
		distributeModels(this.allModels, this.groups);		
	}
	
	/**
	 * Distribute models amongst groups. This method grants completeness
	 * and disjunction of single collections.
	 * 
	 * @param allModels the whole set of models.
	 * @param groups the number of groups to distribute models into
	 */
	private void distributeModels(List<Model> allModels, int groups) {
		
		modelCollections.clear();
		
		// initialize
		for (int i = 0; i < groups; i++)
			modelCollections.add(new SingleModelCollection());
		
		int maxWeight = 1;
		int currentGroup = 0;
		boolean modelAdded = false;
		
		for (Model model : allModels) {
			modelAdded = false;
			ModelCollection minModelGroup = modelCollections.get(0);
			for (currentGroup = 0; currentGroup < groups; currentGroup++) {
				ModelCollection currentModelGroup = modelCollections.get(currentGroup);
				if (currentModelGroup.getWeight(comparator) + comparator.getWeight(model) <= maxWeight)
				{
					modelAdded = currentModelGroup.addModel(model);
					maxWeight = currentModelGroup.getWeight(comparator);
					break;
				} else if (currentModelGroup.getWeight(comparator) < minModelGroup.getWeight(comparator))
					minModelGroup = currentModelGroup;
			}
			if (!modelAdded) {
				minModelGroup.addModel(model);
				maxWeight = minModelGroup.getWeight(comparator);
			}
		}
		
		validDistribution = true;
	}
	
	/**
	 * Gets a single model collection. This method will return null
	 * if the group parameter is out of rank.
	 * 
	 * @param group the group
	 * 
	 * @return the model iterator
	 */
	public ModelCollection getModelCollection(int group) {
		
		if (!validDistribution)
			distributeModels(this.allModels, this.groups);
		
		if (group < 0 || group > groups)
			return null;
		
		return modelCollections.get(group);
	}

	/* (non-Javadoc)
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	public void add(int index, Model element) {
		validDistribution = false;
		super.add(index, element);
	}
	
	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.util.collection.ModelCollection#add(es.uvigo.darwin.prottest.model.Model)
	 */
	@Override
	public boolean add(Model e) {
		validDistribution = false;
		return super.add(e);
	}
	
	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.util.collection.ModelCollection#addModel(java.lang.String, int, java.util.Properties)
	 */
	@Override
	public boolean addModel(String matrix, int distribution, Properties modelProperties,
			Alignment alignment, Tree tree, int ncat) {
		validDistribution = false;
		return super.addModel(matrix, distribution, modelProperties,
				alignment, tree, ncat);
	}
	
	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.util.collection.ModelCollection#addModel(es.uvigo.darwin.prottest.model.Model)
	 */
	@Override
	public boolean addModel(Model model) {
		validDistribution = false;
		return super.addModel(model);
	}
	
	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.util.collection.ModelCollection#addModelCartesian(java.util.Collection, java.util.Collection, java.util.Properties)
	 */
	@Override
	public boolean addModelCartesian(Collection<String> matrices, Collection<Integer> distributions, 
			Properties modelProperties, Alignment alignment, Tree tree, int ncat) {
		validDistribution = false;
		return super.addModelCartesian(matrices, distributions, modelProperties,
				alignment, tree, ncat);
	}
	
	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.util.collection.ModelCollection#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends Model> c) {
		validDistribution = false;
		return super.addAll(c);
	}
	
	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.util.collection.ModelCollection#addAll(int, java.util.Collection)
	 */
	@Override
	public boolean addAll(int index, Collection<? extends Model> c) {
		validDistribution = false;
		return super.addAll(index, c);
	}
	
	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.util.collection.ModelCollection#clear()
	 */
	@Override
	public void clear() {
		validDistribution = false;
		super.clear();
	}
	
	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.util.collection.ModelCollection#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object o) {
		validDistribution = false;
		return super.remove(o);
	}
	
	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.util.collection.ModelCollection#remove(int)
	 */
	@Override
	public Model remove(int index) {
		validDistribution = false;
		return super.remove(index);
	}

	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.util.collection.ModelCollection#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		validDistribution = false;
		return super.removeAll(c);
	}
	
	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.util.collection.ModelCollection#set(int, es.uvigo.darwin.prottest.model.Model)
	 */
	@Override
	public Model set(int index, Model element) {
		validDistribution = false;
		return super.set(index, element);
	}
}
