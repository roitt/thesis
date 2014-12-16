package me.thesis.preferencepairs.beans;

public class PreferencePair {
	private String userid;
	private String morepreferredbi;
	private String lesspreferredbi;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getMorepreferredbi() {
		return morepreferredbi;
	}

	public void setMorepreferredbi(String morepreferredbi) {
		this.morepreferredbi = morepreferredbi;
	}

	public String getLesspreferredbi() {
		return lesspreferredbi;
	}

	public void setLesspreferredbi(String lesspreferredbi) {
		this.lesspreferredbi = lesspreferredbi;
	}

	public String toJSONObjectStringOne() {
		return new String("{" + "\"userid\" : \"" + userid + "\", "
				+ "\"morepreferredbi\" : \"" + morepreferredbi + "\"}");
	}

	public String toJSONObjectStringTwo() {
		return new String("{" + "\"userid\" : \"" + userid + "\", "
				+ "\"morepreferredbi\" : \"" + morepreferredbi + "\", "
				+ "\"lesspreferredbi\" : \"" + lesspreferredbi + "\"}");
	}
}
