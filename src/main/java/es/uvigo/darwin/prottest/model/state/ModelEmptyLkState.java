package es.uvigo.darwin.prottest.model.state;

import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;

/**
 * The Class ModelEmptyLkState defines the behavior of the model while the
 * likelihood is not yet calculated.
 */
public class ModelEmptyLkState extends ModelLkState {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6972504372160405275L;

    /**
     * Throws an internal exception.
     * 
     * @see es.uvigo.darwin.prottest.model.state.ModelLkState#getLk()
     */
    @Override
    public double getLk() {
        throw new ProtTestInternalException("Lk not initialized");
    }

    /**
     * 
     * Sets the log Likelihood and changes the state to {@link ModelFilledLkState}.
     * 
     * @see es.uvigo.darwin.prottest.model.state.ModelLkState#setLk(double)
     * 
     * @return the new state
     */
    @Override
    public ModelLkState setLk(double lk) {
        return new ModelFilledLkState(lk);
    }

    /* (non-Javadoc)
     * @see es.uvigo.darwin.prottest.model.state.ModelLkState#getAlpha()
     */
    @Override
    public double getAlpha() {
        throw new ProtTestInternalException("This model is still unoptimized");
    }

    /* (non-Javadoc)
     * @see es.uvigo.darwin.prottest.model.state.ModelLkState#getInv()
     */
    @Override
    public double getInv() {
        throw new ProtTestInternalException("This model is still unoptimized");
    }

    /**
     * Throws an internal exception.
     * 
     * @see es.uvigo.darwin.prottest.model.state.ModelLkState#setAlpha(double)
     */
    @Override
    public ModelLkState setAlpha(double alpha) {
        throw new ProtTestInternalException("This model is still unoptimized");
    }

    /**
     * Throws an internal exception.
     * 
     * @see es.uvigo.darwin.prottest.model.state.ModelLkState#setInv(double)
     */
    @Override
    public ModelLkState setInv(double inv) {
        throw new ProtTestInternalException("This model is still unoptimized");
    }
}
