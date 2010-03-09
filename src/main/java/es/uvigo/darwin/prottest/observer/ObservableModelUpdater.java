package es.uvigo.darwin.prottest.observer;

import java.util.ArrayList;
import java.util.Collection;

import es.uvigo.darwin.prottest.global.options.ApplicationOptions;
import es.uvigo.darwin.prottest.model.Model;

public abstract class ObservableModelUpdater {

	private Collection<ModelUpdaterObserver> modelUpdaterObservers;
	
	public ObservableModelUpdater() {
		modelUpdaterObservers = new ArrayList<ModelUpdaterObserver>();
	}
	
	public void addObserver(ModelUpdaterObserver o) {
		modelUpdaterObservers.add(o);
	}
	
	public void removeObserver(ModelUpdaterObserver o) {
		modelUpdaterObservers.remove(o);
	}
	
	public void notifyObservers() {
		for (ModelUpdaterObserver o : modelUpdaterObservers)
			o.update(this, null, null);
	}
	
	public void notifyObservers(Model model, ApplicationOptions options) {
		for (ModelUpdaterObserver o : modelUpdaterObservers)
			o.update(this, model, options);
	}
}
