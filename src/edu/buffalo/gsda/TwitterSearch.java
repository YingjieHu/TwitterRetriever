package edu.buffalo.gsda;

import java.util.ArrayList;
import java.util.List;

import edu.princeton.cs.algs4.Out;
import edu.princeton.cs.algs4.StdOut;
import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.Query.Unit;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;


public class TwitterSearch 
{
	static void retrieveTweets(Out output, String keywords, double[][] extent)
	{
		String consumerKey = "";
	    String consumerSecret = "";
	    String accessToken = "";
	    String accessSecret = "";
	    
	    ConfigurationBuilder builder = new ConfigurationBuilder();
	    builder.setOAuthConsumerKey(consumerKey);
	    builder.setOAuthConsumerSecret(consumerSecret);
	    builder.setOAuthAccessToken(accessToken);
	    builder.setOAuthAccessTokenSecret(accessSecret);

	    Configuration configuration = builder.build();

	    Twitter twitter = new TwitterFactory(configuration).getInstance();
	    
	    Query query = new Query(keywords);
	    query.setLang("en");
	    
	    // convert geo extent to center and buffer
	    GeoLocation center = new GeoLocation((extent[0][1] + extent[1][1])/2.0, (extent[0][0] + extent[1][0])/2.0 );
	    double bufferDistance =  extent[1][0] - extent[0][0];
	    if(bufferDistance< (extent[1][1] - extent[0][1]))
	    	bufferDistance = extent[1][1] - extent[0][1];
	    bufferDistance = bufferDistance * 111.111;
    	query.setGeoCode(center, bufferDistance, Unit.km);
	    
    	query.setCount(100);
	    long numberOfTweets = 18000;
	    long lastID = Long.MAX_VALUE;
	    
	    // How many times zero tweets are returned 
	    int zeroCount = 0;
	    while(true)
	    {
		    List<Status> tweets = new ArrayList<Status>();
		    long lastCount = 0;
		    while (tweets.size () < numberOfTweets) 
		    {
		      try 
		      {
		        QueryResult result = twitter.search(query);
		        tweets.addAll(result.getTweets());
		        StdOut.println("Gathered " + tweets.size() + " tweets");
		        for (Status t: tweets) 
		          if(t.getId() < lastID) lastID = t.getId();
		      }
		      catch (TwitterException te) 
		      {
		    	  StdOut.println("Couldn't connect: " + te);
		      }; 
		      
		      query.setMaxId(lastID-1);
		      
		      if(tweets.size()==lastCount)
		        	break;
		      
		      lastCount = tweets.size();
		    }
		    
		    
		    // if no tweets retrieved, we count it
		    if(tweets.size() == 0)
		    	zeroCount++;
		    
		    // If no tweets are retrieved for three times, then we terminate this function
		    if(tweets.size() == 0 && zeroCount >=3)
		    	return;

		    
		    
		    for (int i = 0; i < tweets.size(); i++) 
		    {
		      Status t = (Status) tweets.get(i);
		      
		      long id = t.getId();
		      
		      GeoLocation loc = t.getGeoLocation();

		      String user = t.getUser().getScreenName();
		      String tweet = t.getText().replaceAll("\n", " ");
		      String time = t.getCreatedAt().toString();
		      int favoriteCount = t.getFavoriteCount();
		      int retweetCount = t.getRetweetCount();
		      if (loc!=null) {
		        Double lat = t.getGeoLocation().getLatitude();
		        Double lon = t.getGeoLocation().getLongitude();
		        //StdOut.println(i + " USER: " + user + " wrote: " + msg + " located at " + lat + ", " + lon);
		        
		        String record = "\""+id+"\",\""+user+"\",\""+time+"\",\""+favoriteCount+"\",\""+retweetCount+"\",\""+tweet+"\",\""+lat+"\",\""+lon+"\"";
	        	StdOut.println(record);
	        	output.println(record);
		      } 
		     // else 
		    //	  StdOut.println(i + " USER: " + user + " wrote: " + tweet);
		    }
	    	
	    	
	    	// sleep 16 min before next query
	    	try 
	    	{
	    		Thread.sleep(960000);
	    	}
	    	catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}
	    	
	    }
	    
	}

}
