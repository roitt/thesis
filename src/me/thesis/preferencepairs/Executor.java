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
import me.thesis.preferencepairs.beans.PreferencePair;
import me.thesis.preferencepairs.beans.Review;
import me.thesis.preferencepairs.beans.User;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
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

		System.out.println("Parsing data ...");

		parseBusinessJSON(businessFile);
		parseReviewJSON(reviewFile);
		parseUserJSON(userFile);

		System.out.println("Done gathering data.");
		// printSize(users, businesses, reviews);

		System.out.println("Getting reviewed businesses for every user ...");

		// For each user get all businesses he/she reviewed
		getReviewedBusinessesForEveryUser();
		// printUserReviewAndBusiness();

		System.out.println("Writing User data to JSON ...");
		// For each user create pairs of businesses he/she reviewed
		// createPairs();

		// Print preference pairs
		// printPreferencePairs();

		// createJSONFromUserObjects();

		System.out.println("Done.");
	}

	@SuppressWarnings("rawtypes")
	public static void createJSONFromUserObjects() {
		User[] array = new User[users.size()];
		int i = 0;
		Iterator it = users.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			User user = (User) pairs.getValue();
			array[i++] = user;
		}

		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(
					new File("/Users/bearcatmobile/Desktop/user.json"), array);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	@SuppressWarnings("rawtypes")
	public static void createPairs() {
		ArrayList<BusinessAndReview> br;
		ArrayList<PreferencePair> preferencePairs;
		Iterator it = users.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			User user = (User) pairs.getValue();
			br = user.getReviewedBusinesses();
			preferencePairs = new ArrayList<PreferencePair>();
			if (br.size() > 0) {
				if (br.size() == 1) {
					PreferencePair pp = new PreferencePair();
					pp.setMorePreferred(br.get(0));
					preferencePairs.add(pp);
				} else {
					for (int i = 0; i < br.size() - 1; i++) {
						for (int j = i + 1; j < br.size(); j++) {
							PreferencePair pp = new PreferencePair();
							pp.setMorePreferred(br.get(i));
							pp.setLessPreferred(br.get(j));
							preferencePairs.add(pp);
						}
					}
				}
			}
			user.setPreferencePairs(preferencePairs);
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
		}
	}

	public static void printPreferencePairs() {
		Iterator<Entry<String, User>> it = users.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, User> user = (Map.Entry<String, User>) it.next();
			ArrayList<PreferencePair> ppList = new ArrayList<PreferencePair>();
			ppList = user.getValue().getPreferencePairs();
			System.out.println("---------------------------"
					+ user.getValue().getName() + "--------------------------");
			for (PreferencePair item : ppList) {
				System.out.println(item.getMorePreferred().getBusiness()
						.getName()
						+ "------------>"
						+ item.getLessPreferred().getBusiness().getName());
			}
			System.out
					.println("-----------------------------------------------------------");
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
