package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimeLineActivity extends AppCompatActivity {

    private TwitterClient client;
    TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    RecyclerView rvTweets;
    MenuItem miCompose;
    Tweet tweetReply;

    private SwipeRefreshLayout swipeContainer;

    // Instance of the progress action-view
    MenuItem miActionProgressItem;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line);

        client = TwitterApp.getRestClient(this);

        //find the RecyclerView
        rvTweets = (RecyclerView) findViewById(R.id.rvTweet);
        //init the arrayList (data source)
        tweets = new ArrayList<>();
        //construct the adapter from this datasource
        tweetAdapter = new TweetAdapter(this, tweets,new TweetAdapter.ClickListener() {
            @Override public void onPositionClicked(int position) {
                // callback performed on click
                tweetReply=tweets.get(position);
                Toast.makeText(getApplicationContext(), "get Tweet"+tweetReply.user.name, Toast.LENGTH_SHORT).show();
            }

        });

        // RecyclerView setup (layout manager, use adapter)
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        //set the adapter
        rvTweets.setAdapter(tweetAdapter);

        miCompose =findViewById(R.id.miCompose);

        populateTimeLine();

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);

            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        // Extract the action-view from the menu item
        ProgressBar v =  (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);

        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }

    public void showProgressBar() {
        // Show progress item
        miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisible(false);
    }

    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.
        showProgressBar();
        client.getHomeTimeLine(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Remember to CLEAR OUT old items before appending in the new ones
                tweetAdapter.clear();
                populateTimeLine();
                // ...the data has come back, add new items to your adapter...
                tweetAdapter.addAll(tweets);
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("DEBUG", "Fetch timeline error: " + errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

        });
        hideProgressBar();
    }



    // REQUEST_CODE can be any value we like, used to determine the result type later
    private final int REQUEST_CODE = 20;


    private void populateTimeLine() {

        client.getHomeTimeLine(new JsonHttpResponseHandler(){



            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
               Log.d("TwitterClient", response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
     //           Log.d("TwitterClient", response.toString());
                //iterate through the JSON array
                //for each entry, deserialize the JSSON object

                for(int i=0; i<response.length();i++){
                    //convert each object to a Tweet model
                    //add that Tweet model to our data source
                    //notify the adapter that we have added an itehm
                    try {
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));

                        tweets.add(tweet);
                        tweetAdapter.notifyItemInserted(tweets.size() - 1);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TwitterClient", responseString);
                throwable.printStackTrace();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent i = new Intent(TimeLineActivity.this, ComposeActivity.class);
        i.putExtra("mode", 2); // pass arbitrary data to launched activity
        startActivityForResult(i, REQUEST_CODE);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract object from result extras
            // Make sure the key here matches the one specified in the result passed from ActivityTwo.java
            Tweet tweet = (Tweet) Parcels.unwrap(data.getParcelableExtra("Post"));
            tweets.add(0, tweet);
            tweetAdapter.notifyItemInserted(0);
            rvTweets.scrollToPosition(0);


        }
    }

    private final int REQUEST_CODE_2 = 20;
    public void replyPost() {


        Intent i = new Intent(TimeLineActivity.this, ReplyActivity.class);
        i.putExtra("mode", 3); // pass arbitrary data to launched activity
        startActivityForResult(i, REQUEST_CODE_2);
    }


}