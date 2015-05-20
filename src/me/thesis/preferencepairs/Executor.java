package me.thesis.preferencepairs;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import me.thesis.preferencepairs.alchemy.AlchemyAPI;
import me.thesis.preferencepairs.beans.Business;
import me.thesis.preferencepairs.beans.BusinessAndReview;
import me.thesis.preferencepairs.beans.BusinessPreference;
import me.thesis.preferencepairs.beans.PreferencePair;
import me.thesis.preferencepairs.beans.Review;
import me.thesis.preferencepairs.beans.User;

import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Executor {
	private static final String BUSINESS_DATASET_PATH = "/Users/rbhoompally/Desktop/Thesis/Data Sets/yelp_phoenix_dataset/yelp_academic_dataset_business.json";
	private static final String USER_DATASET_PATH = "/Users/rbhoompally/Desktop/Thesis/Data Sets/yelp_phoenix_dataset/yelp_academic_dataset_user.json";
	private static final String REVIEW_DATASET_PATH = "/Users/rbhoompally/Desktop/Thesis/Data Sets/yelp_phoenix_dataset/yelp_academic_dataset_review.json";

	private static final String CATEGORY_RESTAURANT = "Restaurants";

	private static ObjectMapper mapper = new ObjectMapper();
	private static HashMap<String, Business> businesses = new HashMap<String, Business>();
	private static ArrayList<Review> reviews = new ArrayList<Review>();
	private static HashMap<String, User> users = new HashMap<String, User>();
	static AlchemyAPI alchemyObj;
	static ArrayList<String> plusFriendsIds = new ArrayList<String>();
	static HashSet<String> uniqueBusinessesReviewed = new HashSet<String>();
	static ArrayList<PreferencePair> plusFriendsPPList = new ArrayList<PreferencePair>();

	private static List<String> allUserIds = new ArrayList<String>();
	private static List<String> userIdRandomSample = new ArrayList<String>();

	// private static HashMap<String, ArrayList<PreferencePair>> gpairs = new
	// HashMap<String, ArrayList<PreferencePair>>();

	public static void main(String[] args) throws FileNotFoundException,
			IOException, XPathExpressionException, SAXException,
			ParserConfigurationException {

		/*
		 * Clear out directory of all existing files, so new CSV files can get
		 * in
		 */
		File file = new File("/Users/rbhoompally/Desktop/CSV/");
		FileUtils.cleanDirectory(file);

		// alchemyObj = AlchemyAPI
		// .GetInstanceFromFile("../../alchemyapi_java/testdir/api_key.txt");
		//

		/* Parse Yelp data files */
		parseYelpDataFiles();
		
		/* Pick a random sample of 10 users from all our users */
		while (userIdRandomSample.size() < 2) {
			int totalUsers = users.size();
			int randomPick = (int) (Math.random() * totalUsers);
			String candidate = allUserIds.get(randomPick);
			
			if (isEligibleCandidate(candidate)) {
				userIdRandomSample.add(candidate);
			}
		}

		/*
		 * The data provided by Yelp as such is not very organized. So we do a
		 * single run on their entire data-set and create our own user data
		 * files. And the completion of this method, we will have a large number
		 * of user files, each file representing a single user, and the name of
		 * the file is his user ID. The fields in each JSON object will include
		 * userId, lessPreferredBusinnessId, and morePreferredBusinnessId
		 */
		// createUserDataFiles();
		
		/* Here we have a random sample of 10 users and perform our algorithm on all of these */
		for (String userId : userIdRandomSample) {
			runAlgorithm(userId);
		}
	}
	
	private static boolean isEligibleCandidate(String candidate) {
		if (getFriendCount(candidate) > 1)
			return true;
		return false;
	}
	
	private static void runAlgorithm(String userIdSample) throws IOException {
		System.out.println("------------------------" + userIdSample + "------------------------");
		System.out.println("Name: " + users.get(userIdSample));
		
		// Clear previous runs data
		plusFriendsIds = new ArrayList<String>();
		plusFriendsPPList = new ArrayList<PreferencePair>();
		uniqueBusinessesReviewed = new HashSet<String>();
		
		/*
		 * We pick a user, and gather all his friends. Later we can run it over
		 * multiple users.
		 */
		createFriendList(userIdSample);

		/*
		 * Now for the user and his group of friends gather all possible
		 * preference pairs
		 */
		createAllPreferencePairs(userIdSample);

		/* Run zermelo's algorithm of our preference pair list */
		float[] finalGamma = runZermelo(plusFriendsPPList, plusFriendsIds);

		/*
		 * Convert the float matrix indicating ranking of items in our node
		 * graph into a list of preference pairs. And sort them highest rank
		 * first.
		 */
		ArrayList<BusinessPreference> preferredBusinesses = makePreferredBusinesses(
				uniqueBusinessesReviewed, finalGamma);
		Collections.sort(preferredBusinesses);

		/* Print the output with business names */
		printClean(preferredBusinesses);
		printUserVisitedRestaurants(userIdSample);
	}
	
	private static void printUserVisitedRestaurants(String userId) {
		System.out.println("------------Businesses visited by the user---------------");
		User user = users.get(userId);
		if(user == null) {
			System.out.println("No businesses visited.");
			return;
		}
		int count = 1;
		for (BusinessAndReview b: user.getReviewedBusinesses()) {
			System.out.println(count++ + ". " + b.getBusiness().getName());
		}
	}

	private static void parseYelpDataFiles() throws IOException {
		File businessFile = new File(BUSINESS_DATASET_PATH);
		File userFile = new File(USER_DATASET_PATH);
		File reviewFile = new File(REVIEW_DATASET_PATH);

		System.out.println("Parsing data ...");

		/* Restaurants only */
		parseBusinessJSON(businessFile);
		parseReviewJSON(reviewFile);
		parseUserJSON(userFile);

		System.out.println("Done gathering data. Restaurants only.");
		printSize(users, businesses, reviews);
	}

	private static void createAllPreferencePairs(String currentUserId)
			throws IOException {
		// Now we have Ids of friends to take-care of.
		plusFriendsPPList = parseUserAndFriendsPPJSON(plusFriendsIds,
				currentUserId);
		System.out.println("Cumulative Preference Pairs gathered.");
	}

	private static void createFriendList(String currentUserId) {
		// plusFriendsIds.add(currentUserId);
		addFriendsToList(currentUserId);
	}

	private static void createUserDataFiles() throws FileNotFoundException,
			IOException, XPathExpressionException, SAXException,
			ParserConfigurationException {

		System.out.println("Getting reviewed businesses for every user ...");

		// For each user get all businesses he/she reviewed
		getReviewedBusinessesForEveryUser();
		// printUserReviewAndBusiness();
		// reviews.clear();
		// businesses.clear();

		System.out.println("Done");

		// System.out.println("Creating preference pairs ...");
		// For each user create pairs of businesses he/she reviewed
		// createPairs();
		// System.out.println("Done.");
		// Print preference pairs
		// printPreferencePairs();
		// createJSONFromUserObjects();

		System.out.println("Creating preference pairs for each user ...");
		createJSONPreferencePairs();
		System.out.println("Done.");
	}

	public static void printClean(ArrayList<BusinessPreference> bps) {
		int count = 1;
		for (BusinessPreference bp : bps) {
			System.out.println(count++ + ". "
					+ businesses.get(bp.getBusinessId()).getName() + " ("
					+ bp.getBusinessId() + ")");
			if (count == 11)
				break;
		}
	}

	public static ArrayList<BusinessPreference> makePreferredBusinesses(
			HashSet<String> businesses, float[] gamma) {
		int k = gamma.length;
		ArrayList<BusinessPreference> bp = new ArrayList<BusinessPreference>();
		Object[] bs = businesses.toArray();
		for (int i = 0; i < k; i++) {
			BusinessPreference _bp = new BusinessPreference();
			_bp.setGamma(gamma[i]);
			_bp.setBusinessId((String) bs[i]);
			bp.add(_bp);
		}
		return bp;
	}

	public static float[] runZermelo(ArrayList<PreferencePair> ppl,
			ArrayList<String> pfl) {
		System.out.println("Circle size: " + pfl.size());
		uniqueBusinessesReviewed = getCumulativeReviewedBusiness(ppl);
		int cnr = uniqueBusinessesReviewed.size();
		System.out.println("Cumulative number of businesses reviewed: " + cnr);

		// Start the algorithm here
		float gammaValue = (float) 1 / (float) cnr;
		System.out.println("Gamma: " + gammaValue);
		float[] gammaArray = new float[cnr];
		populateArrayWithGamma(gammaArray, gammaValue);
		int[][] winMatrix = populateWinMatrix(uniqueBusinessesReviewed, ppl);
		
		/* Check if our win matrix is strongly connected */
//		StronglyConnectedComponents strong = new StronglyConnectedComponents(winMatrix.length - 1);
//        strong.strongConnectedComponent(winMatrix);
//        
//        System.out.println("The Strong Connected Components are");
//        for (int i = 1; i < strong.getLeaderNodes().length; i++)
//        {
//            System.out.println( "Node " + i+ "belongs to SCC" 
//                + strong.getFinishingTimeMap().get(strong.getLeaderNodes()[i]));
//        }
        
		// printWinMatrix(winMatrix);
		double likelihood = getLogLikelihood(winMatrix, gammaArray);
		System.out.println(likelihood);

		// Run until converges
		double epsilon = 0.01;
		while (true) {
			gammaArray = EZUpdate(winMatrix, gammaArray);
			double newlikelihood = getLogLikelihood(winMatrix, gammaArray);
			// System.out.println(likelihood);
			if (Math.abs(newlikelihood - likelihood) < epsilon)
				break;
			likelihood = newlikelihood;
		}

		// Here we have a gamma array that rates high priority restaurants based
		// on its value
		return gammaArray;
	}

	public static float[] EZUpdate(int[][] winMatrix, float[] oldGammaArray) {
		oldGammaArray = normalizeGamma(oldGammaArray);
		int k = oldGammaArray.length;
		double epsilon = 0.0000001;
		float[] newGammaArray = new float[k];
		for (int i = 0; i < k; i++) {
			float sumnbi = 0;
			for (int j = 0; j < k; j++) {
				if (i != j) {
					double iG = epsilon;
					double jG = epsilon;
					if (oldGammaArray[i] != 0.0)
						iG = oldGammaArray[i];
					if (oldGammaArray[j] != 0.0)
						jG = oldGammaArray[j];
					sumnbi += (float) (winMatrix[i][j] + winMatrix[j][i])
							/ (iG + jG);
				}
			}
			// Populate new gamma array here.
			float sumW = 0;
			for (int z = 0; z < k; z++)
				sumW += winMatrix[i][z];
			newGammaArray[i] = sumW / sumnbi;
		}
		return newGammaArray;
	}

	public static float[] normalizeGamma(float[] gammaArray) {
		float sum = 0;
		for (int i = 0; i < gammaArray.length; i++)
			sum += gammaArray[i];
		for (int i = 0; i < gammaArray.length; i++)
			gammaArray[i] = gammaArray[i] / sum;
		return gammaArray;
	}

	public static void printWinMatrix(int[][] winMatrix) {
		for (int i = 0; i < winMatrix.length; i++) {
			for (int j = 0; j < winMatrix[0].length; j++) {
				System.out.print(winMatrix[i][j] + " ");
			}
			System.out.println();
		}
	}

	public static double getLogLikelihood(int[][] winMatrix, float[] gammaArray) {
		double likelihood = 0;
		int k = gammaArray.length;
		for (int i = 0; i < k; i++) {
			for (int j = 0; j < k; j++) {
				// Taking off the log, because the data is usually normalized.
				double firstLog = (double) gammaArray[i];
				double secondLog = (double) (gammaArray[i] + gammaArray[j]);
				double difference = firstLog - secondLog;
				likelihood += (double) (winMatrix[i][j]) * difference;
			}
		}
		return likelihood;
	}

	public static int[][] populateWinMatrix(HashSet<String> ubr,
			ArrayList<PreferencePair> ppl) {
		int[][] winMatrix = new int[ubr.size()][ubr.size()];
		HashMap<String, Integer> positionMap = new HashMap<String, Integer>();
		int position = 0;
		for (String bs : ubr) {
			positionMap.put(bs, position++);
		}
		for (PreferencePair pp : ppl) {
			String lp = pp.getLesspreferredbi();
			String mp = pp.getMorepreferredbi();
			int i = positionMap.get(mp);
			int j = positionMap.get(lp);
			winMatrix[i][j]++;
		}
		return winMatrix;
	}

	public static void populateArrayWithGamma(float[] array, float gamma) {
		for (int i = 0; i < array.length; i++)
			array[i] = gamma;
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

	public static HashSet<String> getCumulativeReviewedBusiness(
			ArrayList<PreferencePair> ppl) {
		HashSet<String> uniqueBusinessesReviewed = new HashSet<String>();
		for (PreferencePair pp : ppl) {
			if (pp.getLesspreferredbi() != null) {
				uniqueBusinessesReviewed.add(pp.getLesspreferredbi());
			}
			if (pp.getMorepreferredbi() != null) {
				uniqueBusinessesReviewed.add(pp.getMorepreferredbi());
			}
		}
		return uniqueBusinessesReviewed;
	}

	public static ArrayList<PreferencePair> parseUserAndFriendsPPJSON(
			ArrayList<String> pfis, String ci) throws IOException {
		ArrayList<PreferencePair> ppl = new ArrayList<PreferencePair>();
		for (String pfi : pfis) {
			File preferencePairFile = new File(
					"/Users/rbhoompally/Desktop/Preference_Pairs_Restaurants/"
							+ pfi + ".json");

			BufferedReader br = new BufferedReader(new FileReader(
					preferencePairFile));

			String line = null;
			while ((line = br.readLine()) != null) {
				PreferencePair pp = mapper
						.readValue(line, PreferencePair.class);
				if (pp.getLesspreferredbi() != null
						&& pp.getMorepreferredbi() != null)
					ppl.add(pp);
			}
			br.close();

			createCSVfromJSON(preferencePairFile, ci);
		}
		return ppl;
	}

	public static void addFriendsToList(String cUserId) {
		User cUser = users.get(cUserId);
		String[] friends = cUser.getFriends();
		for (String f : friends)
			plusFriendsIds.add(f);
	}
	
	public static int getFriendCount(String userId) {
		User cUser = users.get(userId);
		String[] friends = cUser.getFriends();
		return friends.length;
	}

	public static double getLogLikelihood() {
		return 0;
	}

	public static void createCSVfromJSON(File file, String userId) throws JsonParseException,
			JsonMappingException, IOException {
		String csvFilePath = "/Users/rbhoompally/Desktop/CSV/"
				+ userId + ".csv";
		BufferedReader br = new BufferedReader(new FileReader(file));
		FileWriter fw = new FileWriter(csvFilePath, true);
		int numOfLines = countLines(csvFilePath);
		if (numOfLines == 0) {
			fw.append("Source;Target;Label");
			fw.append("\n");
		}
		String line = null;
		while ((line = br.readLine()) != null) {
			PreferencePair pp = mapper.readValue(line, PreferencePair.class);
			if (pp.getLesspreferredbi() != null
					&& pp.getMorepreferredbi() != null) {
				fw.append(businesses.get(pp.getLesspreferredbi()).getName()
						+ ";");
				fw.append(businesses.get(pp.getMorepreferredbi()).getName()
						+ ";");
				fw.append(((User) users.get(pp.getUserid())).getName());
				fw.append("\n");
			}
		}
		br.close();
		fw.flush();
		fw.close();
	}

	public static int countLines(String filename) throws IOException {
		InputStream is = new BufferedInputStream(new FileInputStream(filename));
		try {
			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			boolean empty = true;
			while ((readChars = is.read(c)) != -1) {
				empty = false;
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++count;
					}
				}
			}
			return (count == 0 && !empty) ? 1 : count;
		} finally {
			is.close();
		}
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
			mapper.writeValue(new File("/Users/rbhoompally/Desktop/user.json"),
					array);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void parseBusinessJSON(File file) throws IOException {
		/* We are only interested in restaurants at the moment */
		/* Add more tags to widen the scope of look-up categories */
		BufferedReader br = new BufferedReader(new FileReader(file));

		String line = null;
		while ((line = br.readLine()) != null) {
			Business business = mapper.readValue(line, Business.class);
			if (business.getCategories().length > 0) {
				for (String category : business.getCategories()) {
					if (category.equals(CATEGORY_RESTAURANT)) {
						businesses.put(business.getBusiness_id(), business);
					}
				}
			}
		}
		br.close();
	}

	public static void parseReviewJSON(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));

		String line = null;
		while ((line = br.readLine()) != null) {
			Review review = mapper.readValue(line, Review.class);
			if (businesses.containsKey(review.getBusiness_id()))
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
			allUserIds.add(user.getUser_id());
		}
		br.close();
	}

	public static void getReviewedBusinessesForEveryUser() {
		for (Review r : reviews) {
			String userId = r.getUser_id();
			String businessId = r.getBusiness_id();
			if (users.containsKey(userId) && businesses.containsKey(businessId)) {
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
						"/Users/rbhoompally/Desktop/Preference_Pairs_Restaurants/"
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
