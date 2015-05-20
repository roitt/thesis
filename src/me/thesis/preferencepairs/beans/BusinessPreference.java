/** UC NightRide
 *  Copyright @ UC Applab 2014
 *  Written by Rohit & Chris
 *  Jan 13, 2015
 */
package me.thesis.preferencepairs.beans;

/**
 * @author bearcatmobile
 * 
 */
public class BusinessPreference implements Comparable<BusinessPreference> {
	private String businessId;
	private String businessName;
	private float gamma;

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	public float getGamma() {
		return gamma;
	}

	public void setGamma(float gamma) {
		this.gamma = gamma;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	@Override
	public int compareTo(BusinessPreference o) {
		// TODO Auto-generated method stub
		return (this.gamma > o.gamma) ? -1 : (this.gamma < o.gamma) ? 1 : 0;
	}
}
