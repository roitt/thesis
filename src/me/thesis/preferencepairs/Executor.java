package me.thesis.preferencepairs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import me.thesis.preferencepairs.beans.Business;
import me.thesis.preferencepairs.beans.Review;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Executor {
	private static ObjectMapper mapper = new ObjectMapper();
	private static ArrayList<Business> businesses = new ArrayList<Business>();
	private static ArrayList<Review> reviews = new ArrayList<Review>();

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		File businessFile = new File(
				"/Users/bearcatmobile/Desktop/Thesis/Data Sets/yelp-dataset/yelp_academic_dataset_business.json");
		File userFile = new File(
				"/Users/bearcatmobile/Desktop/Thesis/Data Sets/yelp-dataset/yelp_academic_dataset_user.json");
		File reviewFile = new File(
				"/Users/bearcatmobile/Desktop/Thesis/Data Sets/yelp-dataset/yelp_academic_dataset_review.json");

		// parseBusinessJSON(businessFile);
		parseReviewJSON(reviewFile);
		printSize(reviews);
	}

	public static void parseBusinessJSON(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));

		String line = null;
		while ((line = br.readLine()) != null) {
			Business business = mapper.readValue(line, Business.class);
			businesses.add(business);
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

	@SuppressWarnings("rawtypes")
	public static void printSize(ArrayList list) {
		System.out.println(list.size());
	}
}
