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
package es.uvigo.darwin.prottest.global;

import java.util.Arrays;
import java.util.List;

/**
 * The global application settings and parameter names for
 * amino-acid alignment sequences using RAxML.
 */
public class RaxmlAminoAcidApplicationGlobals extends
		AminoAcidApplicationGlobals {

	/** The Amino-Acid substitution matrices supported by RAxML. */
	public static String[] ALL_MATRICES	= { 
		 "JTT",
		 "DCMut",
		 "MtREV",
		 "MtMam",
		 "Dayhoff",
		 "WAG",
		 "RtREV",
		 "CpREV",
		 "Blosum62",
		 "VT",
	};
	
	/* (non-Javadoc)
	 * @see es.uvigo.darwin.prottest.global.ApplicationGlobals#getSupportedMatrices()
	 */
	public List<String> getSupportedMatrices() {
		return Arrays.asList(ALL_MATRICES);
	}
}
