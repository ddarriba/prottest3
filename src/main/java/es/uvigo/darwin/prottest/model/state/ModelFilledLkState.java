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
package es.uvigo.darwin.prottest.model.state;

import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;

/**
 * The Class ModelFilledLkState defines the behavior of the model when the
 * likelihood is already calculated.
 */
public class ModelFilledLkState extends ModelLkState {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8247641688056141331L;

	/** The likelihood logarithm value. */
	private double lk;
	
	/** The calculated alpha value. */
	private double alpha;
	
	/** The calculated proportion of invariant sites. */
	private double inv;
	
	/**
	 * Instantiates a new model filled lk state.
	 * 
	 * @param lk the lk
	 */
	public ModelFilledLkState(double lk) {
		this.lk = lk;
	}
	
	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.model.state.ModelLkState#getLk()
	 */
	@Override
	public double getLk() {
		return lk;
	}

	/**
         * Throws an internal exception.
         * 
	 * @see es.uvigo.darwin.prottest.model.state.ModelLkState#setLk(double)
	 */
	@Override
	public ModelLkState setLk(double lk) {
		throw new ProtTestInternalException("Trying to modify lnl value");
	}

	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.model.state.ModelLkState#getAlpha()
	 */
	@Override
	public double getAlpha() {
		return alpha;
	}

	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.model.state.ModelLkState#setAlpha(double)
	 */
	@Override
	public ModelLkState setAlpha(double alpha) {
		this.alpha = alpha;
		return this;
	}

	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.model.state.ModelLkState#getInv()
	 */
	@Override
	public double getInv() {
		return inv;
	}

	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.model.state.ModelLkState#setInv(double)
	 */
	@Override
	public ModelLkState setInv(double inv) {
		this.inv = inv;
		return this;
	}
}
