package edu.buffalo.gsda;

import edu.princeton.cs.algs4.Out;

public class TwitterMain {

	public static void main(String[] args) 
	{
		try 
		{
			// Overall parameters
			String filename = "winter_storm_gia";
			double[][] extent = { { -94.972608, 36.362766 }, { -70.078066, 43.085633 } };  // lower left and upper right coordinates
			String queryString = "Winter Storm Gia|snow|blizzard";

			// Approach 1: using the stream API
			Out outputFile = new Out(filename + "_stream.csv");
			outputFile.println("\"id\",\"user\",\"time\",\"favoriteCount\",\"retweetCount\",\"tweet\",\"lat\",\"lon\"");
			TwitterStreamRetriever.retrieveTweets(outputFile, queryString, extent);

			// Approach 2: using the search API
			Out outputFile2 = new Out(filename + "_search.csv");
			outputFile2.println("\"id\",\"user\",\"time\",\"favoriteCount\",\"retweetCount\",\"tweet\",\"lat\",\"lon\"");
			String[] queryStringArray = queryString.split("\\|");
			String queryString2 = "";
			for (String thisQuery : queryStringArray)
				queryString2 = queryString2 + "(" + thisQuery + ") OR ";
			queryString2 = queryString2.substring(0, queryString2.length() - 4);

			while (true) 
			{
				TwitterSearch.retrieveTweets(outputFile2, queryString2, extent);
				Thread.sleep(960000);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
