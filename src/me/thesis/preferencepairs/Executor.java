package me.thesis.preferencepairs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import me.thesis.preferencepairs.alchemy.AlchemyAPI;
import me.thesis.preferencepairs.beans.Business;
import me.thesis.preferencepairs.beans.BusinessAndReview;
import me.thesis.preferencepairs.beans.PreferencePair;
import me.thesis.preferencepairs.beans.Review;
import me.thesis.preferencepairs.beans.User;

import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Executor {
	private static ObjectMapper mapper = new ObjectMapper();
	private static HashMap<String, Business> businesses = new HashMap<String, Business>();
	private static ArrayList<Review> reviews = new ArrayList<Review>();
	private static HashMap<String, User> users = new HashMap<String, User>();
	static AlchemyAPI alchemyObj;
	static ArrayList<String> plusFriendsIds = new ArrayList<String>();

	// private static HashMap<String, ArrayList<PreferencePair>> gpairs = new
	// HashMap<String, ArrayList<PreferencePair>>();

	public static void main(String[] args) throws FileNotFoundException,
			IOException, XPathExpressionException, SAXException,
			ParserConfigurationException {

		// alchemyObj = AlchemyAPI
		// .GetInstanceFromFile("../../alchemyapi_java/testdir/api_key.txt");
		//
		// File businessFile = new File(
		// "/Users/bearcatmobile/Desktop/Thesis/Data Sets/yelp-dataset/yelp_academic_dataset_business.json");
		File userFile = new File(
				"/Users/bearcatmobile/Desktop/Thesis/Data Sets/yelp-dataset/yelp_academic_dataset_user.json");
		// File reviewFile = new File(
		// "/Users/bearcatmobile/Desktop/Thesis/Data Sets/yelp-dataset/yelp_academic_dataset_review.json");
		//
		// System.out.println("Parsing data ...");
		//
		// parseBusinessJSON(businessFile);
		// parseReviewJSON(reviewFile);
		parseUserJSON(userFile);
		//
		// System.out.println("Done gathering data.");
		// // printSize(users, businesses, reviews);
		//
		// System.out.println("Getting reviewed businesses for every user ...");
		//
		// // For each user get all businesses he/she reviewed
		// getReviewedBusinessesForEveryUser();
		// // printUserReviewAndBusiness();
		// // reviews.clear();
		// // businesses.clear();
		//
		// System.out.println("Done");
		//
		// // System.out.println("Creating preference pairs ...");
		// // For each user create pairs of businesses he/she reviewed
		// // createPairs();
		// // System.out.println("Done.");
		// // Print preference pairs
		// // printPreferencePairs();
		// // createJSONFromUserObjects();
		//
		// System.out.println("Creating preference pairs for each user ...");
		// createJSONPreferencePairs();
		// System.out.println("Done.");

		// Create s CSV file for a preference pair JSON
		String currentUserId = "61HlVi4obZXwOJzCvZuqzw";
		plusFriendsIds.add(currentUserId);
		addFriendsToList(currentUserId);

		// Now we have Ids of friends to take-care of.
		ArrayList<PreferencePair> plusFriendsPPList = parseUserAndFriendsPPJSON(
				plusFriendsIds, currentUserId);
		System.out.println("Cumulative Preference Pairs gathered.");

		// Run Zermelo on this list
		runZermelo(plusFriendsPPList, plusFriendsIds);

		// createCSVfromJSON(preferencePairFile);
	}

	public static void runZermelo(ArrayList<PreferencePair> ppl,
			ArrayList<String> pfl) {
		System.out.println("Circle size: " + pfl.size());
		int cnr = getCumulativeReviewedBusinessCount(ppl);
		System.out.println("Cumulative number of businesses reviewed: " + cnr);

		// Start the algorithm here
	}

	public static int sumTon(ArrayList<String> pfl) {
		int sum = 0;
		for (String id : pfl) {
			int iSum = 0;
			int currentProbe = users.get(id).getReview_count();
			for (int i = 1; i <= currentProbe - 1; i++)
				iSum += i;
			System.out.println(id + ":" + currentProbe + ":" + iSum);
			sum += iSum;
		}
		return sum;
	}

	public static int getCumulativeReviewedBusinessCount(
			ArrayList<PreferencePair> ppl) {
		HashSet<String> uniqueBusinessesReviewed = new HashSet<String>();
		for (PreferencePair pp : ppl) {
			uniqueBusinessesReviewed.add(pp.getLesspreferredbi());
			uniqueBusinessesReviewed.add(pp.getMorepreferredbi());
		}
		return uniqueBusinessesReviewed.size();
	}

	public static ArrayList<PreferencePair> parseUserAndFriendsPPJSON(
			ArrayList<String> pfis, String ci) throws IOException {
		ArrayList<PreferencePair> ppl = new ArrayList<PreferencePair>();
		for (String pfi : pfis) {
			File preferencePairFile = new File(
					"/Users/bearcatmobile/Desktop/Preference_Pairs/" + pfi
							+ ".json");

			BufferedReader br = new BufferedReader(new FileReader(
					preferencePairFile));

			String line = null;
			while ((line = br.readLine()) != null) {
				PreferencePair pp = mapper
						.readValue(line, PreferencePair.class);
				ppl.add(pp);
			}
			br.close();
		}
		return ppl;
	}

	public static void addFriendsToList(String cUserId) {
		User cUser = users.get(cUserId);
		String[] friends = cUser.getFriends();
		for (String f : friends)
			plusFriendsIds.add(f);
	}

	public static double getLogLikelihood() {
		return 0;
	}

	public static void createCSVfromJSON(File file) throws JsonParseException,
			JsonMappingException, IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		FileWriter fw = new FileWriter(
				"/Users/bearcatmobile/Desktop/CSV/61HlVi4obZXwOJzCvZuqzwQ.csv");
		fw.append("Source;Target;Label");
		fw.append("\n");
		String line = null;
		while ((line = br.readLine()) != null) {
			PreferencePair pp = mapper.readValue(line, PreferencePair.class);
			fw.append(pp.getLesspreferredbi().toString() + ";");
			fw.append(pp.getMorepreferredbi().toString() + ";");
			fw.append(((User) users.get(pp.getUserid())).getName());
			fw.append("\n");
		}
		br.close();
		fw.flush();
		fw.close();
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
	public static void createJSONPreferencePairs()
			throws XPathExpressionException, SAXException,
			ParserConfigurationException {
		Iterator it = users.entrySet().iterator();
		ArrayList<BusinessAndReview> br;
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			User user = (User) pairs.getValue();
			try {
				PrintWriter writer = new PrintWriter(
						"/Users/bearcatmobile/Desktop/Preference_Pairs/"
								+ user.getUser_id() + ".json", "UTF-8");
				// Get reviewed businesses
				br = user.getReviewedBusinesses();

				if (br.size() > 0) {
					if (br.size() == 1) {
						PreferencePair pp = new PreferencePair();
						pp.setUserid(user.getUser_id());
						pp.setMorepreferredbi(br.get(0).getBusiness()
								.getBusiness_id());
						writer.print(pp.toJSONObjectStringOne());
					} else {
						for (int i = 0; i < br.size() - 1; i++) {
							for (int j = i + 1; j < br.size(); j++) {
								PreferencePair pp = new PreferencePair();
								pp.setUserid(user.getUser_id());
								if (br.get(i).getReview().getStars() > br
										.get(j).getReview().getStars()) {
									pp.setMorepreferredbi(br.get(i)
											.getBusiness().getBusiness_id());
									pp.setLesspreferredbi(br.get(j)
											.getBusiness().getBusiness_id());
								} else if (br.get(i).getReview().getStars() < br
										.get(j).getReview().getStars()) {
									pp.setMorepreferredbi(br.get(j)
											.getBusiness().getBusiness_id());
									pp.setLesspreferredbi(br.get(i)
											.getBusiness().getBusiness_id());
								} else { // Equals case
									int random = (int) Math.round((Math
											.random() * 1));
									if (random == 0) {
										pp.setMorepreferredbi(br.get(i)
												.getBusiness().getBusiness_id());
										pp.setLesspreferredbi(br.get(j)
												.getBusiness().getBusiness_id());
									} else {
										pp.setMorepreferredbi(br.get(j)
												.getBusiness().getBusiness_id());
										pp.setLesspreferredbi(br.get(i)
												.getBusiness().getBusiness_id());
									}

									// Code for sentiment analysis
									// String iReview = br.get(i).getReview()
									// .getText().toString().trim();
									// String jReview = br.get(j).getReview()
									// .getText().toString().trim();
									//
									// double iScore =
									// getSentimentScoreForString(iReview);
									// double jScore =
									// getSentimentScoreForString(jReview);
									//
									// if (iScore >= jScore) {
									// pp.setMorepreferredbi(br.get(i)
									// .getBusiness().getBusiness_id());
									// pp.setLesspreferredbi(br.get(j)
									// .getBusiness().getBusiness_id());
									// } else {
									// pp.setMorepreferredbi(br.get(j)
									// .getBusiness().getBusiness_id());
									// pp.setLesspreferredbi(br.get(i)
									// .getBusiness().getBusiness_id());
									// }
								}
								writer.print(pp.toJSONObjectStringTwo());
								writer.print("\n");
							}
						}
					}
				}
				writer.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	// @SuppressWarnings("rawtypes")
	// public static void createPairs() {
	// ArrayList<BusinessAndReview> br;
	// // ArrayList<PreferencePair> preferencePairs;
	// Iterator it = users.entrySet().iterator();
	// while (it.hasNext()) {
	// Map.Entry pairs = (Map.Entry) it.next();
	// User user = (User) pairs.getValue();
	// br = user.getReviewedBusinesses();
	// // preferencePairs = new ArrayList<PreferencePair>();
	// System.out.println("---------------------------" + user.getName()
	// + "---------------------------");
	// if (br.size() > 0) {
	// if (br.size() == 1) {
	// PreferencePair pp = new PreferencePair();
	// pp.setMorePreferred(br.get(0));
	// // preferencePairs.add(pp);
	// System.out.println(pp.getMorePreferred());
	// } else {
	// for (int i = 0; i < br.size() - 1; i++) {
	// for (int j = i + 1; j < br.size(); j++) {
	// PreferencePair pp = new PreferencePair();
	// pp.setMorePreferred(br.get(i));
	// pp.setLessPreferred(br.get(j));
	// // preferencePairs.add(pp);
	// System.out.println(pp.getMorePreferred()
	// .getBusiness().getName()
	// + "----->"
	// + pp.getLessPreferred().getBusiness()
	// .getName());
	// }
	// }
	// }
	// }
	// // gpairs.put(user.getUser_id(), preferencePairs);
	// }
	// }

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

	// public static void printPreferencePairs() {
	// Iterator<Entry<String, ArrayList<PreferencePair>>> it = gpairs
	// .entrySet().iterator();
	// while (it.hasNext()) {
	// Map.Entry<String, ArrayList<PreferencePair>> pair = (Map.Entry<String,
	// ArrayList<PreferencePair>>) it
	// .next();
	// ArrayList<PreferencePair> ppList = pair.getValue();
	// System.out.println("---------------------------"
	// + users.get(pair.getKey()).getName()
	// + "--------------------------");
	// for (PreferencePair item : ppList) {
	// System.out.println(item.getMorePreferred().getBusiness()
	// .getName()
	// + "------------>"
	// + item.getLessPreferred().getBusiness().getName());
	// }
	// System.out
	// .println("-----------------------------------------------------------");
	// }
	// }

	@SuppressWarnings("rawtypes")
	public static void printSize(HashMap users, HashMap businesses,
			ArrayList reviews) {
		System.out.println("Users: " + users.size());
		System.out.println("Businesses: " + businesses.size());
		System.out.println("Reviews: " + reviews.size());
	}

	// utility method
	// private static String getStringFromDocument(Document doc) {
	// try {
	// DOMSource domSource = new DOMSource(doc);
	// StringWriter writer = new StringWriter();
	// StreamResult result = new StreamResult(writer);
	//
	// TransformerFactory tf = TransformerFactory.newInstance();
	// Transformer transformer = tf.newTransformer();
	// transformer.transform(domSource, result);
	//
	// return writer.toString();
	// } catch (TransformerException ex) {
	// ex.printStackTrace();
	// return null;
	// }
	// }

	// private static double getSentimentScoreForString(String sString)
	// throws XPathExpressionException, IOException, SAXException,
	// ParserConfigurationException {
	// // Extract sentiment for a text string.
	// Document doc = alchemyObj.TextGetTextSentiment(sString);
	// String xml = getStringFromDocument(doc);
	//
	// XPathFactory xpathFactory = XPathFactory.newInstance();
	// XPath xpath = xpathFactory.newXPath();
	//
	// InputSource source = new InputSource(new StringReader(xml));
	// String status = xpath.evaluate("/results/status", source);
	// double score = 0;
	// if (status.equals("OK")) {
	// InputSource sourceScore = new InputSource(new StringReader(xml));
	// String sScore = xpath.evaluate("/results/docSentiment/score",
	// sourceScore);
	// if (sScore != null && sScore != "")
	// score = Double.valueOf(sScore);
	// }
	// return score;
	// }
}
