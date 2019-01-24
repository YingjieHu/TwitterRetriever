package edu.buffalo.gsda;

import java.io.IOException;

import edu.princeton.cs.algs4.Out;
import twitter4j.FilterQuery;
import twitter4j.GeoLocation;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterStreamRetriever 
{
	static void retrieveTweets(Out outputFile, String keywordString, double[][] extent) throws TwitterException, IOException
	{
		StatusListener listener = new StatusListener()
	    {
	        public void onStatus(Status status) 
	        {
	        	String tweet = status.getText().replaceAll("\n", " ");

        		long id = status.getId();
        		String user = status.getUser().getScreenName();
	        	String time = status.getCreatedAt().toString();
	        	//String datetimeString = (datetime.getMonth()+1)+"-"+(datetime.getDate())+"-"+datetime.getYear()+" "+datetime.getHours()+":"+datetime.getMinutes()+":"+datetime.getSeconds();
	        	GeoLocation loc = status.getGeoLocation();
	        	//Place place = status.getPlace();
	        	int favoriteCount = status.getFavoriteCount();
	        	int retweetCount = status.getRetweetCount();
	        	
	        	boolean keywordContained = false;
	        	String[] keywords= keywordString.split("\\|");
	        	String lowercaseTweet = tweet.toLowerCase();
	        	for(String keyword: keywords)
	        	{
	        		if(lowercaseTweet.contains(keyword))
	        		{
	        			keywordContained = true;
	        			break;
	        		}
	        	}
	        	
	        	
	        
	        	if(loc != null && keywordContained)
	        	{
	        		Double lat = loc.getLatitude();
	    	        Double lon = loc.getLongitude();
	        		String record = "\""+id+"\",\""+user+"\",\""+time+"\",\""+favoriteCount+"\",\""+retweetCount+"\",\""+tweet+"\",\""+lat+"\",\""+lon+"\"";
	        		outputFile.println(record);
		            System.out.println(record);
	        	}
	        }
	        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
	        
	        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
	        
	        public void onException(Exception ex) {ex.printStackTrace(); }
		
			public void onScrubGeo(long arg0, long arg1) {}
		
			public void onStallWarning(StallWarning arg0) {}
			
	    };
	    
	    String consumerKey = "";
	    String consumerSecret = "";
	    String accessToken = "";
	    String accessSecret = "";
	    
	    
	    ConfigurationBuilder builder = new ConfigurationBuilder();
	    builder.setOAuthConsumerKey(consumerKey);
	    builder.setOAuthConsumerSecret(consumerSecret);

	    Configuration configuration = builder.build();
	    
	    TwitterStream twitterStream = new TwitterStreamFactory(configuration).getInstance();
	  //  twitterStream.setOAuthConsumer(, );
	    twitterStream.setOAuthAccessToken(new AccessToken(accessToken, accessSecret));
	   
	   // StdOut.println(consumerKey+","+consumerSecret+","+accessToken+","+accessSecret);
	   // 36.302659, -114.037625
	    FilterQuery filter = new FilterQuery();
	    //String keyword[]= {"wildfire","fire"};
	    String[] keyword= keywordString.split("\\|");//{"HurricanHarvey"};
	 //   double [][]location ={{-90.338256,35.021414},{-81.593139,36.624751}};
	    double [][]location = extent;

	    filter.track(keyword);
	    filter.locations(location);
	    filter.language("en");


	    twitterStream.addListener(listener);
	    // sample() method internally creates a thread which manipulates TwitterStream and calls these adequate listener methods continuously.
	    twitterStream.filter(filter);
	  //  twitterStream.sample();
	}

}
