package es.uvigo.darwin.prottest.observer;

import es.uvigo.darwin.prottest.global.options.ApplicationOptions;
import es.uvigo.darwin.prottest.model.Model;

public interface ModelUpdaterObserver {

	public void update(ObservableModelUpdater o, Model model, ApplicationOptions options);
}
