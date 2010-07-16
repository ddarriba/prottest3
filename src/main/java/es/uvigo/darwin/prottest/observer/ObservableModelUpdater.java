package es.uvigo.darwin.prottest.observer;

import java.util.ArrayList;
import java.util.Collection;

import es.uvigo.darwin.prottest.global.options.ApplicationOptions;
import es.uvigo.darwin.prottest.model.Model;

/**
 * The observable class for model update.
 * 
 * @author Diego Darriba López
 * 
 * @since 3.0
 */
public abstract class ObservableModelUpdater {

    /** Collection of observers */
    private Collection<ModelUpdaterObserver> modelUpdaterObservers;

    /**
     * Constructor for observable objects.
     */
    public ObservableModelUpdater() {
        modelUpdaterObservers = new ArrayList<ModelUpdaterObserver>();
    }

    /**
     * Adds a new observer.
     * 
     * @param o the observer
     */
    public void addObserver(ModelUpdaterObserver o) {
        modelUpdaterObservers.add(o);
    }

    /**
     * Removes an observer.
     * 
     * @param o the observer
     */
    public void removeObserver(ModelUpdaterObserver o) {
        modelUpdaterObservers.remove(o);
    }

    /**
     * Notifies all observers.
     */
    public void notifyObservers() {
        for (ModelUpdaterObserver o : modelUpdaterObservers) {
            o.update(this, null, null);
        }
    }

    /**
     * Notifies all observers with a model an options
     * 
     * @param model the substitution models
     * @param options the application options
     */
    public void notifyObservers(Model model, ApplicationOptions options) {
        for (ModelUpdaterObserver o : modelUpdaterObservers) {
            o.update(this, model, options);
        }
    }
}
