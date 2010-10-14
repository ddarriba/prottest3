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

import java.io.Serializable;
import java.util.Calendar;

public abstract class ApplicationStatus implements Serializable {

	private static final long serialVersionUID = 1L;

	private Calendar creationDate;
	
	public Calendar getCreationDate() {
		return creationDate;
	}
	
	public ApplicationStatus() {
		creationDate = Calendar.getInstance();
	}
	
	/**
	 * Compare the current application status with another one
	 * in order to check if both application status are
	 * consistent.
	 * 
	 * @param other the other aplication status
	 * 
	 * @return true, if both are consistents
	 */
	public abstract boolean isCompatible(ApplicationStatus other);
}
