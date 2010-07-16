package es.uvigo.darwin.prottest.util.comparator;

import java.util.Comparator;

import es.uvigo.darwin.prottest.model.Model;

// TODO: Auto-generated Javadoc
/**
 * The Class LKComparator.
 */
public class LKComparator implements Comparator<Model> {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Model arg0, Model arg1) {
		
		int value = 0;
		if (arg0.getLk() > arg1.getLk())
			value = 1;
		else if (arg0.getLk() < arg1.getLk())
			value = -1;
		return value;
		
	}

}
