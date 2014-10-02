package me.thesis.preferencepairs.beans;

import java.util.HashMap;

public class Review {
	private String type;
	private String businessId;
	private String userId;
	private float stars;
	private String text;
	private String data;
	private HashMap<String, Integer> votes;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public float getStars() {
		return stars;
	}

	public void setStars(float stars) {
		this.stars = stars;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public HashMap<String, Integer> getVotes() {
		return votes;
	}

	public void setVotes(HashMap<String, Integer> votes) {
		this.votes = votes;
	}
}
