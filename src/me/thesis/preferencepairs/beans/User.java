package me.thesis.preferencepairs.beans;

import java.util.ArrayList;
import java.util.HashMap;

public class User {
	private String type;
	private String userId;
	private String name;
	private int reviewCount;
	private float averageStars;
	private HashMap<String, Integer> votes;
	private ArrayList<String> friends;
	private ArrayList<Integer> elite;
	private String yelpingSince;
	private int fans;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getReviewCount() {
		return reviewCount;
	}

	public void setReviewCount(int reviewCount) {
		this.reviewCount = reviewCount;
	}

	public float getAverageStars() {
		return averageStars;
	}

	public void setAverageStars(float averageStars) {
		this.averageStars = averageStars;
	}

	public HashMap<String, Integer> getVotes() {
		return votes;
	}

	public void setVotes(HashMap<String, Integer> votes) {
		this.votes = votes;
	}

	public ArrayList<String> getFriends() {
		return friends;
	}

	public void setFriends(ArrayList<String> friends) {
		this.friends = friends;
	}

	public ArrayList<Integer> getElite() {
		return elite;
	}

	public void setElite(ArrayList<Integer> elite) {
		this.elite = elite;
	}

	public String getYelpingSince() {
		return yelpingSince;
	}

	public void setYelpingSince(String yelpingSince) {
		this.yelpingSince = yelpingSince;
	}

	public int getFans() {
		return fans;
	}

	public void setFans(int fans) {
		this.fans = fans;
	}
}