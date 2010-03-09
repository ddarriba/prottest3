package es.uvigo.darwin.prottest.util.comparator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import es.uvigo.darwin.prottest.global.AminoAcidApplicationGlobals;
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
				List<String> matrices = Arrays.asList(AminoAcidApplicationGlobals.ALL_MATRICES);
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
