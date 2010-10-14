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
					&& (model.checkAlignment(options.getAlignment())))
				return false;
			if (!model.checkAlignment(options.getAlignment())) {
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
