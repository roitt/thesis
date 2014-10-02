package me.thesis.preferencepairs.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Review {
	private String type;
	private String business_id;
	private String user_id;
	private float stars;
	private String text;
	private String date;
	private Votes votes;

	public Review() {

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

	public String getBusiness_id() {
		return business_id;
	}

	public void setBusiness_id(String business_id) {
		this.business_id = business_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Votes getVotes() {
		return votes;
	}

	public void setVotes(Votes votes) {
		this.votes = votes;
	}

}
