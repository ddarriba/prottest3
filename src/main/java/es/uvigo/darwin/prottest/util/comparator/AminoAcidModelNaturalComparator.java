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

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static es.uvigo.darwin.prottest.global.AminoAcidApplicationGlobals.*;
import es.uvigo.darwin.prottest.model.AminoAcidModel;
import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;

public class AminoAcidModelNaturalComparator implements Comparator<Model> {

	@Override
	public int compare(Model model0, Model model1) {
		
		if (model0 instanceof AminoAcidModel && model1 instanceof AminoAcidModel) {
			AminoAcidModel arg0 = (AminoAcidModel) model0;
			AminoAcidModel arg1 = (AminoAcidModel) model1;
			int value = 0;
			if (!arg0.getMatrix().equals(arg1.getMatrix())) {
				List<String> matrices = Arrays.asList(ALL_MATRICES);
				int pos0 = matrices.indexOf(arg0.getMatrix());
				int pos1 = matrices.indexOf(arg1.getMatrix());
				if ( pos0 < pos1 )
					value = -1;
				else
					value = 1;
			} else if (arg0.getDistribution() != arg1.getDistribution()) {
				if (arg0.getDistribution() < arg1.getDistribution())
					value = -1;
				else if (arg1.getDistribution() < arg0.getDistribution())
					value = 1;
			} else if (arg0.isPlusF() != arg1.isPlusF()) {
				if (arg0.isPlusF())
					value = 1;
				else
					value = -1;
			} else
				value = 0;
			return value;
		} else
			throw new ProtTestInternalException("Wrong use of Amino-acid Model Comparator");
	}
	
}
