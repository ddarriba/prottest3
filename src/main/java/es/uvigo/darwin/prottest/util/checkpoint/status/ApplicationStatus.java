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
