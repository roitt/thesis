package me.thesis.preferencepairs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import me.thesis.preferencepairs.beans.Business;
import me.thesis.preferencepairs.beans.BusinessAndReview;
import me.thesis.preferencepairs.beans.Review;
import me.thesis.preferencepairs.beans.User;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Executor {
	private static ObjectMapper mapper = new ObjectMapper();
	private static HashMap<String, Business> businesses = new HashMap<String, Business>();
	private static ArrayList<Review> reviews = new ArrayList<Review>();
	private static HashMap<String, User> users = new HashMap<String, User>();

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		File businessFile = new File(
				"/Users/bearcatmobile/Desktop/Thesis/Data Sets/yelp-dataset/yelp_academic_dataset_business.json");
		File userFile = new File(
				"/Users/bearcatmobile/Desktop/Thesis/Data Sets/yelp-dataset/yelp_academic_dataset_user.json");
		File reviewFile = new File(
				"/Users/bearcatmobile/Desktop/Thesis/Data Sets/yelp-dataset/yelp_academic_dataset_review.json");

		parseBusinessJSON(businessFile);
		parseReviewJSON(reviewFile);
		parseUserJSON(userFile);

		System.out.println("Done gathering data.");
		// printSize(users, businesses, reviews);

		// For each user get all businesses he/she reviewed
		getReviewedBusinessesForEveryUser();
		printUserReviewAndBusiness();
	}

	public static void parseBusinessJSON(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));

		String line = null;
		while ((line = br.readLine()) != null) {
			Business business = mapper.readValue(line, Business.class);
			businesses.put(business.getBusiness_id(), business);
		}
		br.close();
	}

	public static void parseReviewJSON(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));

		String line = null;
		while ((line = br.readLine()) != null) {
			Review review = mapper.readValue(line, Review.class);
			reviews.add(review);
		}
		br.close();
	}

	public static void parseUserJSON(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));

		String line = null;
		while ((line = br.readLine()) != null) {
			User user = mapper.readValue(line, User.class);
			users.put(user.getUser_id(), user);
		}
		br.close();
	}

	public static void getReviewedBusinessesForEveryUser() {
		for (Review r : reviews) {
			String userId = r.getUser_id();
			String businessId = r.getBusiness_id();
			if (users.containsKey(userId)) {
				User user = users.get(userId);
				BusinessAndReview br = new BusinessAndReview();
				br.setReview(r);
				br.setBusiness(businesses.get(businessId));
				user.getReviewedBusinesses().add(br);
			}
		}
	}

	public static void printUserReviewAndBusiness() {
		Iterator<Entry<String, User>> it = users.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, User> pairs = (Map.Entry<String, User>) it.next();
			ArrayList<BusinessAndReview> br = new ArrayList<BusinessAndReview>();
			br = pairs.getValue().getReviewedBusinesses();
			System.out.print(pairs.getValue().getName() + "---->" + "[");
			for (BusinessAndReview item : br) {
				System.out.print(item.getBusiness().getName() + " "
						+ item.getReview().getStars() + ",");
			}
			System.out.println("]");
			it.remove(); // avoids a ConcurrentModificationException
		}
	}

	@SuppressWarnings("rawtypes")
	public static void printSize(HashMap users, HashMap businesses,
			ArrayList reviews) {
		System.out.println("Users: " + users.size());
		System.out.println("Businesses: " + businesses.size());
		System.out.println("Reviews: " + reviews.size());
	}
}
