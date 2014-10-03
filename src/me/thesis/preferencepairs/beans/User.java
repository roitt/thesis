package me.thesis.preferencepairs.beans;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
	private String type;
	private String user_id;
	private String name;
	private int review_count;
	private float average_stars;
	private Votes votes;
	private String[] friends;
	private Integer[] elite;
	private String yelping_since;
	private int fans;
	private ArrayList<BusinessAndReview> reviewedBusinesses = new ArrayList<BusinessAndReview>();

	public ArrayList<BusinessAndReview> getReviewedBusinesses() {
		return reviewedBusinesses;
	}

	public void setReviewedBusinesses(
			ArrayList<BusinessAndReview> reviewedBusinesses) {
		this.reviewedBusinesses = reviewedBusinesses;
	}

	public class Votes {
		int funny;
		int useful;
		int cool;

		public Votes() {

		}

		public Votes(int funny, int useful, int cool) {
			this.funny = funny;
			this.useful = useful;
			this.cool = cool;
		}

		public int getFunny() {
			return funny;
		}

		public void setFunny(int funny) {
			this.funny = funny;
		}

		public int getUseful() {
			return useful;
		}

		public void setUseful(int useful) {
			this.useful = useful;
		}

		public int getCool() {
			return cool;
		}

		public void setCool(int cool) {
			this.cool = cool;
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getReview_count() {
		return review_count;
	}

	public void setReview_count(int review_count) {
		this.review_count = review_count;
	}

	public float getAverage_stars() {
		return average_stars;
	}

	public void setAverage_stars(float average_stars) {
		this.average_stars = average_stars;
	}

	public Votes getVotes() {
		return votes;
	}

	public void setVotes(Votes votes) {
		this.votes = votes;
	}

	public String[] getFriends() {
		return friends;
	}

	public void setFriends(String[] friends) {
		this.friends = friends;
	}

	public Integer[] getElite() {
		return elite;
	}

	public void setElite(Integer[] elite) {
		this.elite = elite;
	}

	public String getYelping_since() {
		return yelping_since;
	}

	public void setYelping_since(String yelping_since) {
		this.yelping_since = yelping_since;
	}

	public int getFans() {
		return fans;
	}

	public void setFans(int fans) {
		this.fans = fans;
	}

}