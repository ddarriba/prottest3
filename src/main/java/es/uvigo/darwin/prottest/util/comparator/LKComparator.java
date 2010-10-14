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

import java.util.Comparator;

import es.uvigo.darwin.prottest.model.Model;

// TODO: Auto-generated Javadoc
/**
 * The Class LKComparator.
 */
public class LKComparator implements Comparator<Model> {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Model arg0, Model arg1) {
		
		int value = 0;
		if (arg0.getLk() > arg1.getLk())
			value = 1;
		else if (arg0.getLk() < arg1.getLk())
			value = -1;
		return value;
		
	}

}
