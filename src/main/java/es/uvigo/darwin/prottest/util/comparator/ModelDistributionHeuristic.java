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
package es.uvigo.darwin.prottest.util.comparator;

import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;

// TODO: Auto-generated Javadoc
/**
 * The Class ModelDistributionHeuristic.
 */
public class ModelDistributionHeuristic extends ModelWeightComparator {

	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.util.comparator.ModelWeightComparator#getWeight(es.uvigo.darwin.prottest.model.Model)
	 */
	public int getWeight(Model model) {
		
		int weight;
		switch (model.getDistribution()) {
		case 0:
			weight = 1;
			break;
		case 1:
			weight = 2;
			break;
		case 2:
			weight = 6;
			break;
		case 3:
			weight = 30;
			break;
		default:
			throw new ProtTestInternalException("Invalid model distribution " + model.getDistribution());
		}
		
		return weight;
	}
	
}
