package me.thesis.preferencepairs.beans;

public class PreferencePair {
	private BusinessAndReview morePreferred;
	private BusinessAndReview lessPreferred;

	public BusinessAndReview getMorePreferred() {
		return morePreferred;
	}

	public void setMorePreferred(BusinessAndReview morePreferred) {
		this.morePreferred = morePreferred;
	}

	public BusinessAndReview getLessPreferred() {
		return lessPreferred;
	}

	public void setLessPreferred(BusinessAndReview lessPreferred) {
		this.lessPreferred = lessPreferred;
	}
}
