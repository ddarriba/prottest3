package es.uvigo.darwin.prottest.observer;

import es.uvigo.darwin.prottest.global.options.ApplicationOptions;
import es.uvigo.darwin.prottest.model.Model;

/**
 * The observer interface for model update.
 * 
 * @author Diego Darriba López
 * 
 * @since 3.0
 */
public interface ModelUpdaterObserver {

    /**
     * Updates the observers.
     * 
     * @param o the observable object
     * @param model the updated model
     * @param options the application options
     */
	public void update(ObservableModelUpdater o, Model model, ApplicationOptions options);
}
