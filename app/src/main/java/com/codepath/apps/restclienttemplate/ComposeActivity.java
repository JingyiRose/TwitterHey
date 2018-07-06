package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    private TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client =TwitterApp.getRestClient(this);

        textCount();

    }

    public void textCount(){
        final EditText etPost = (EditText) findViewById(R.id.etPost);

        etPost.addTextChangedListener(new TextWatcher() {

            TextView tvCount = (TextView) findViewById(R.id.tvCount);

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String currentText = s.toString();
                int charLeft = 140-currentText.length();
                tvCount.setText("Characters left: " + charLeft);

            }

        });

    }

    public void onSubmit(View v) {
        EditText etPost = (EditText) findViewById(R.id.etPost);

        client.sendTweet(etPost.getText().toString(), new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Tweet tweet = null;
                try {
                    tweet = Tweet.fromJSON(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("TwitterClient", response.toString());
                // Prepare data intent
                Intent data = new Intent();
                // Pass relevant data back as a result
                data.putExtra("Post", Parcels.wrap(tweet));
                // Activity finished ok, return the data
                setResult(RESULT_OK, data); // set result code and bundle data for response

                finish(); // closes the activity, pass data to parent

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

        });




    }


}