package es.uvigo.darwin.prottest.util.checkpoint.status;

import java.util.Collection;

import es.uvigo.darwin.prottest.global.options.ApplicationOptions;
import es.uvigo.darwin.prottest.global.options.SerializableApplicationOptions;
import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;

public class ProtTestStatus extends ApplicationStatus {

	private static final long serialVersionUID = 1L;
	
	private SerializableApplicationOptions applicationOptions;
	private Model[] models;
	
	public Model[] getModels() {
		return models;
	}
	
	public SerializableApplicationOptions getApplicationOptions() {
		return applicationOptions;
	}
	
	public ProtTestStatus(Model[] models, ApplicationOptions applicationOptions) {
		if (applicationOptions == null)
			throw new ProtTestInternalException("Wrong status format");
		if (models != null 
				&& models.length > 0)
				checkIntegrity(models, applicationOptions);
			
		this.models = models;
		this.applicationOptions = new SerializableApplicationOptions(applicationOptions);
	}

	private boolean checkIntegrity(Model[] models, ApplicationOptions options) {
		Collection<String> matrices = options.getMatrices(); 
		Collection<Integer> distributions = options.getDistributions();
		boolean plusF = options.isPlusF();
		for (Model model : models) {
			if (!(matrices.contains(model.getMatrix())
					&& distributions.contains(model.getDistribution()))
					&& (!model.isPlusF() || plusF)
					&& (model.getAlignment().toString().equals(options.getAlignment().toString())))
				return false;
			if (model.getAlignment() != options.getAlignment()) {
				// sets up the same alignment instance, once equality have been tested
				model.setAlignment(options.getAlignment());
			}
		}
		return true;
	}

	@Override
	public boolean isCompatible(ApplicationStatus other) {
		if (other instanceof ProtTestStatus) {
			ProtTestStatus otherStatus = (ProtTestStatus)other;
			if (otherStatus.getApplicationOptions().equals(getApplicationOptions()))
				return true;
		}
		return false;
	}
}
