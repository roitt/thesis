package me.thesis.preferencepairs.beans;

public class PreferencePair {
	private String userId;
	private String morePreferredBI;
	private String lessPreferredBI;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getMorePreferredBI() {
		return morePreferredBI;
	}

	public void setMorePreferredBI(String morePreferredBI) {
		this.morePreferredBI = morePreferredBI;
	}

	public String getLessPreferredBI() {
		return lessPreferredBI;
	}
	
	public void setLessPreferredBI(String lessPreferredBI) {
		this.lessPreferredBI = lessPreferredBI;
	}

	public String toJSONObjectStringOne() {
		return new String("{" + "\"userid\" : \"" + userId + "\", " + "\"morepreferredbi\" : \""
				+ morePreferredBI + "\"}");
	}

	public String toJSONObjectStringTwo() {
		return new String("{" + "\"userid\" : \"" + userId + "\", " + "\"morepreferredbi\" : \""
				+ morePreferredBI + "\", " + "\"lesspreferredbi\" : \"" + lessPreferredBI
				+ "\"}");
	}
}
