package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;

@org.parceler.Parcel
public class Tweet{

    //list out the attributes
    public String body;
    public long uid; //database ID for the tweet
    public String createdAt;
    public User user;

    public Tweet(){

    }

    //deserialize the JSON
    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException{
        Tweet tweet = new Tweet();

        //extract the values from JSON
        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt=jsonObject.getString("created_at");
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        return tweet;
    }
}
