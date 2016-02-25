package core.campaigns;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import core.data.DataFilter;
import core.data.InvalidUserException;
import core.data.User;
import core.records.Click;
import core.records.Impression;
import core.records.Server;
import gnu.trove.map.hash.TLongIntHashMap;
//import net.openhft.koloboke.collect.map.hash.HashLongIntMap;
//import net.openhft.koloboke.collect.map.hash.HashLongIntMaps;
import util.DateProcessor;

public class Campaign {

	// ==== Constants ====

	// file name references
	private static final String IMPRESSIONS_FILE = "impression_log.csv";
	private static final String SERVERS_FILE = "server_log.csv";
	private static final String CLICKS_FILE = "click_log.csv";

	// ==== Properties ====

	private final File campaignDirectory;

	private final File impressionLog;
	private final File serverLog;
	private final File clickLog;

	private LocalDateTime campaignStartDate;
	private LocalDateTime campaignEndDate;

	public List<Impression> impressionsList;
	private Iterable<Click> clicksList;
	private Iterable<Server> serversList;
	
	public ByteBuffer bb;

	private int numberOfImpressions;
	private int numberOfClicks;
	private int numberOfConversions;
	private int numberOfPagesViewed;
	private int numberOfUniques;

	private double costOfImpressions;
	private double costOfClicks;
	
//	https://www.dropbox.com/s/otrfjuqzzw923yu/2_month_campaign.zip?dl=0

	// ==== Constructor ====

	public Campaign(File campaignDirectory) {
		impressionLog = new File(campaignDirectory, IMPRESSIONS_FILE);
		serverLog = new File(campaignDirectory, SERVERS_FILE);
		clickLog = new File(campaignDirectory, CLICKS_FILE);

		this.campaignDirectory = campaignDirectory;
	}
	
	public final void loadData() throws InvalidCampaignException {
		try {
			System.out.println("--------------------------------------");

			System.gc();

			long startMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			long totalTime = 0;
			
			// usersMap = HashLongIntMaps.newUpdatableMap((int) (5000000 / .75));
			TLongIntHashMap usersMap = new TLongIntHashMap((int) (5000000 / .75));
			
			long time = System.currentTimeMillis();
			processImpressions(usersMap);
			long end = System.currentTimeMillis();
			
			totalTime += end - time;
			
			System.out.println("impression_log:\t" + (end - time) + "ms");
			
			time = System.currentTimeMillis();
			processClicks(usersMap);
			end = System.currentTimeMillis();
			
			totalTime += end - time;
			
			System.out.println("click_log:\t" + (end - time) + "ms");
			
			time = System.currentTimeMillis();
			processServers(usersMap);
			end = System.currentTimeMillis();
			
			totalTime += end - time;
			usersMap = null;
			
			System.out.println("server_log:\t" + (end - time) + "ms");

			System.gc();
			
			long endMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

			System.out.println("--------------------------------------");
			System.out.println("Campaign:\t" + campaignDirectory.getName());
			System.out.println("Load Time:\t" + totalTime + "ms");
			System.out.println("Memory Used:\t" + (endMem - startMem) / (1024 * 1024) + "MB");
			System.out.println("Processed:\t" + getNumberOfRecords() + " records");
			System.out.println("Start Date:\t" + campaignStartDate);
			System.out.println("End Date:\t" + campaignEndDate);
			System.out.println("Impressions:\t" + numberOfImpressions);
			System.out.println("Uniques:\t" + numberOfUniques);
			System.out.println("Cost(I):\t" + costOfImpressions);
			System.out.println("Cost(C):\t" + costOfClicks);
			System.out.println("Cost(T):\t" + (costOfImpressions + costOfClicks));
			System.out.println("Conversions:\t" + numberOfConversions);
			System.out.println("Page Views:\t" + numberOfPagesViewed);
			System.out.println("--------------------------------------");
			
		} catch (InvalidUserException e) {
			throw new InvalidCampaignException("invalid user data in impression_log");
		} catch (IOException e) {
			throw new InvalidCampaignException("something happened when reading files");
		}		
	}

	// ==== Accessors ====

	public final Iterable<Impression> getImpressions() {
		return impressionsList;
	}

	public final Iterable<Click> getClicks() {
		return clicksList;
	}

	public final Iterable<Server> getServers() {
		return serversList;
	}

	public final LocalDateTime getStartDateTime() {
		return campaignStartDate;
	}

	public final LocalDateTime getEndDateTime() {
		return campaignEndDate;
	}

	public final int getNumberOfImpressions() {
		return numberOfImpressions;
	}

	public final int getNumberOfClicks() {
		return numberOfClicks;
	}

	public final int getNumberOfConversions() {
		return numberOfConversions;
	}

	public final int getNumberOfPagesViewed() {
		return numberOfPagesViewed;
	}

	public final int getNumberOfRecords() {
		return numberOfImpressions + 2 * numberOfClicks;
	}

	public final double getTotalCostOfCampaign() {
		return costOfImpressions + costOfClicks;
	}

	public final double getCostOfImpressions() {
		return costOfImpressions;
	}

	public final double getCostOfClicks() {
		return costOfClicks;
	}
	

	// ==== Private Helper Methods ====

	/**
	 * @throws IOException 
	 * 
	 */
	private void processServers(TLongIntHashMap usersMap) throws IOException {
		final ArrayList<Server> serversList = new ArrayList<Server>(numberOfClicks);
		
		BufferedReader br = new BufferedReader(new FileReader(serverLog));
		// Initialise variables
		String line = br.readLine();

		// Reset variables
		numberOfPagesViewed = 0;
		numberOfConversions = 0;

		while ((line = br.readLine()) != null) {
			final String[] data = line.split(",");

			final long dateTime = DateProcessor.stringToEpoch(data[0]);
			final long userID = Long.valueOf(data[1]);
			final int userData = usersMap.get(userID);// clicksList.get(serversList.size()).getUserData();
			final long exitDateTime = DateProcessor.stringToEpoch(data[2]);
			final int pagesViewed = Integer.valueOf(data[3]);
			final boolean conversion = data[4].equals("Yes");

			// update page views
			numberOfPagesViewed += pagesViewed;

			// increment conversions if Yes
			if (conversion)
				numberOfConversions++;

			// add to memory
			serversList.add(new Server(dateTime, userID, userData, exitDateTime, pagesViewed, conversion));
		}
		
		// Close the BufferedReader
		br.close();

		// Trim to size
		serversList.trimToSize();

		// assign reference
		this.serversList = serversList;
	}

	/**
	 * This method processes the click log It checks that User IDs in the clicks
	 * file matches with the User IDs in the impressions Computes the total cost
	 * of clicks in this campaign TODO: - Check that start/end dates are in-form
	 * with Impressions
	 * @throws IOException 
	 */
	private void processClicks(TLongIntHashMap usersMap) throws IOException {
		final ArrayList<Click> clicksList = new ArrayList<Click>(20000);
		
		BufferedReader br = new BufferedReader(new FileReader(clickLog));
		
		// Initialise variables
		String line = br.readLine();

		// Reset counters etc
		costOfClicks = 0;

		while ((line = br.readLine()) != null) {
			final String[] data = line.split(",");

			final long dateTime = DateProcessor.stringToEpoch(data[0]);
			final long userID = Long.valueOf(data[1]);
			final int userData = usersMap.get(userID);
			final double cost = Double.parseDouble(data[2]);

			final Click click = new Click(dateTime, userID, userData, cost);

			// increment these values
			costOfClicks += cost;

			// add to memory
			clicksList.add(click);
		}
		
		// Close the BufferedReader
		br.close();

		// Trim list to save memory
		clicksList.trimToSize();

		// Set these variables
		numberOfClicks = clicksList.size();
		this.clicksList = clicksList;
	}

	/**
	 * This method is called when to process the impressions log file It checks
	 * that the impressions file is valid and counts the number of impressions
	 * It is also responsible for the handling of unique users in the campaign
	 * We compute the cost of impressions here too, as well as start and end
	 * dates
	 * 
	 * TODO: - find a way to record start and end dates - make exceptions more
	 * understandable v.s. stacktrace e.g. FileNotFound, NumberFormatException Chr
	 * (print out line number for easy debug), IOException - consider User, use
	 * of Enum v.s. String.intern()
	 * @throws IOException 
	 * @throws InvalidUserException 
	 */
	private void processImpressions(TLongIntHashMap usersMap) throws IOException, InvalidUserException {
		final ArrayList<Impression> impressionsList = new ArrayList<Impression>();
		final int offset = 8 + 8 + 4 + 8;

		final FileInputStream fis = new FileInputStream(impressionLog);			
		final FileChannel fc = fis.getChannel();
		final MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
		
		// store in ByteBuffer
		final ByteBuffer bn = ByteBuffer.allocate((int) (fc.size() / 62 * offset));	
		
		// close this stream
		fis.close();

		final int nullEntry = usersMap.getNoEntryValue();
		// final int nullEntry = usersMap.defaultValue();
		
		final byte newLine = '\n';
		final byte comma = ',';

		// reset
		costOfImpressions = 0;
		numberOfImpressions = 0;

		long time = System.currentTimeMillis();
		// mbb.get(buffer);
		mbb.load();
		System.out.println("Load time:\t" + (System.currentTimeMillis() - time) + "ms");

		time = System.currentTimeMillis();

		// skip the header -- precomputed
		mbb.position(50);
		// int index = 50;
		
		while (mbb.hasRemaining()) {
			byte temp;

			/*
			 * BEGIN DATE PROCESSING SECTION
			 */

			long dateTime = DateProcessor.toEpochSeconds(mbb);

			/*
			 * END DATE PROCESSING SECTION
			 */

			/*
			 * BEGIN USERID PROCESSING SECTION
			 */

			// we know MIN(id).length = 12
			// skip first multiplication by 0
			long userID = mbb.get() & 0xF;

			userID *= 10;
			userID += mbb.get() & 0xF;

			userID *= 10;
			userID += mbb.get() & 0xF;

			userID *= 10;
			userID += mbb.get() & 0xF;

			userID *= 10;
			userID += mbb.get() & 0xF;

			userID *= 10;
			userID += mbb.get() & 0xF;

			userID *= 10;
			userID += mbb.get() & 0xF;

			userID *= 10;
			userID += mbb.get() & 0xF;

			userID *= 10;
			userID += mbb.get() & 0xF;

			userID *= 10;
			userID += mbb.get() & 0xF;

			userID *= 10;
			userID += mbb.get() & 0xF;

			userID *= 10;
			userID += mbb.get() & 0xF;

			while ((temp = mbb.get()) != comma) {
				userID *= 10;
				userID += temp & 0xF;
			}

			/*
			 * END USERID PROCESSING SECTION
			 */

			/*
			 * BEGIN USERDATA PROCESSING SECTION
			 */

			int userData = usersMap.get(userID);

			if (userData == nullEntry) {
				userData = User.encodeUser(mbb);
				usersMap.put(userID, userData);
				
			} else {
				mbb.position(mbb.position() + 8);
				
				for (int i = 0; i < 3;) {
					if (mbb.get() == comma)
						i++;
				}
			}

			/*
			 * END USERDATA PROCESSING SECTION
			 */

			/*
			 * BEGIN COST PROCESSING SECTION
			 */

			int costTemp = mbb.get() & 0xF;

			while ((temp = mbb.get()) != '.') {
				costTemp *= 10;
				costTemp += temp & 0xF;
			}

			costTemp *= 10;
			costTemp += mbb.get() & 0xF;

			costTemp *= 10;
			costTemp += mbb.get() & 0xF;

			costTemp *= 10;
			costTemp += mbb.get() & 0xF;

			costTemp *= 10;
			costTemp += mbb.get() & 0xF;

			costTemp *= 10;
			costTemp += mbb.get() & 0xF;

			costTemp *= 10;
			costTemp += mbb.get() & 0xF;

			double cost = costTemp * 0.000001;

			/*
			 * END COST PROCESSING SECTION
			 */
			
			if (mbb.get() != newLine)
				throw new IllegalArgumentException("expected newline");
			
//			impressionsList.add(new Impression(dateTime, userID, userData, cost));
			bn.putLong(dateTime);
			bn.putLong(userID);
			bn.putInt(userData);
			bn.putDouble(cost);

			// misc increment
			costOfImpressions += cost;
		}

		System.out.println("Processing:\t" + (System.currentTimeMillis() - time) + "ms");

		// trim the ArrayList to save capacity
		impressionsList.trimToSize();

		// transfer references
		this.impressionsList = impressionsList;
		
		this.bb = ByteBuffer.allocate(numberOfImpressions * 28);
		bb.put(bn);

		// compute size of impressions
		numberOfUniques = usersMap.size();
		numberOfImpressions = impressionsList.size();
		
		// transfer reference
		this.impressionsList = impressionsList;

		// compute dates
		campaignStartDate = DateProcessor.epochSecondsToLocalDateTime(bn.getLong(0));
		campaignEndDate = DateProcessor.epochSecondsToLocalDateTime(bn.getLong((numberOfImpressions - 1) * 28));
	}

	// ==== Object Override ====

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((campaignDirectory == null) ? 0 : campaignDirectory.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Campaign)) {
			return false;
		}
		Campaign other = (Campaign) obj;
		if (campaignDirectory == null) {
			if (other.campaignDirectory != null) {
				return false;
			}
		} else if (!campaignDirectory.equals(other.campaignDirectory)) {
			return false;
		}
		return true;
	}

	@Override
	public final String toString() {
		return campaignDirectory.getName();
	}

}